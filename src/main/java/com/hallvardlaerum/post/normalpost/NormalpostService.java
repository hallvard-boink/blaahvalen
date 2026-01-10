package com.hallvardlaerum.post.normalpost;


import com.hallvardlaerum.kategori.Kategori;
import com.hallvardlaerum.kategori.KategoriService;
import com.hallvardlaerum.libs.database.EntitetAktig;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.periodepost.Periodepost;
import com.hallvardlaerum.post.Post;
import com.hallvardlaerum.post.PostRepository;
import com.hallvardlaerum.post.PostServiceMal;
import com.hallvardlaerum.post.PostklasseEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import jakarta.persistence.Tuple;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NormalpostService extends PostServiceMal implements InitieringsEgnet {
    private KategoriService kategoriService;
    private ArrayList<Ekstrafeltrad> ekstrafeltradArrayList;
    private Boolean erInitiert = false;
    private PostRepository postRepository;


    //TODO: Hva brukes denne til?
    @Override
    public Post opprettEntitetMedForelder() {
        return opprettEntitet();
        //Skulle det ha vært satt inn en forelder her? Fra hvor???
    }

    @Override
    public Post opprettEntitet() {
        Post normalpost = leggTilUUID(new Post());
        normalpost.setPostklasseEnum(PostklasseEnum.NORMALPOST);
        return normalpost;
    }


    @Override
    public boolean behandleSpesialfeltVedImport(Object entitet, Field field, String nyVerdi, String importradString) {
        if (field==null) {
            return false;
        }

        if (field.getName().equalsIgnoreCase("Kategori")) {
            Optional<Kategori> kategoriOptional = kategoriService.finnEtterTittel(nyVerdi);
            if (kategoriOptional.isPresent()) {
                try {
                    field.set(entitet,kategoriOptional.get());
                    return true;
                } catch (IllegalAccessException e) {
                    Loggekyklop.hent().loggTilFilINFO("Klarte ikke å sette Kategori med entiteten " + kategoriOptional.get().hentBeskrivendeNavn() + ". Importrad: " + importradString);
                    return false;
                }
            } else {
                Loggekyklop.hent().loggTilFilINFO("Klarte ikke å finne Kategori med navn " + nyVerdi + ". Importrad: " + importradString);
                return false;
            }
        }

        return false;

    }


    @Override
    public boolean erInitiert() {
        return erInitiert;
    }

    public NormalpostService() {
    }

    @Override
    public void init() {
        if (!erInitiert) {
            super.initPostServiceMal(PostklasseEnum.NORMALPOST);
            this.kategoriService = Allvitekyklop.hent().getKategoriService();
            this.postRepository = Allvitekyklop.hent().getPostRepository();
            erInitiert=true;
        }
    }

    @Override
    public boolean lagreEkstrafeltTilSenere(EntitetAktig entitet, String feltnavnString, String verdiStreng, String importradString) {
        Ekstrafeltrad ekstrafeltrad = hentEllerOpprettEkstraFeltrad((Post)entitet);
        if (ekstrafeltrad==null) {
            return false;
        } else {
            ekstrafeltrad.setImportradString(importradString);
        }

        if (feltnavnString.equalsIgnoreCase("ForelderpostKortnavn")) {
            ekstrafeltrad.setForelderpostkortnavnString(verdiStreng);
        } else if (feltnavnString.equalsIgnoreCase("Kurs")) {
            ekstrafeltrad.setKursString(verdiStreng);
        } else if (feltnavnString.equalsIgnoreCase("Original beloep")) {
            ekstrafeltrad.setOriginaltBeloepString(verdiStreng);
        } else if (feltnavnString.equalsIgnoreCase("Valuta")) {
            ekstrafeltrad.setValutaString(verdiStreng);
        } else if (feltnavnString.equalsIgnoreCase("EkstraInfo")) {
            ekstrafeltrad.setEkstraInfoString(verdiStreng);
        } else {
            return false;
        }

        return true;
    }

    private Ekstrafeltrad hentEllerOpprettEkstraFeltrad(Post post){
        if (post==null) {
            return null;
        }

        Ekstrafeltrad ekstrafeltrad;
        if (ekstrafeltradArrayList==null) {
            ekstrafeltradArrayList = new ArrayList<>();
            ekstrafeltrad = new Ekstrafeltrad(post);
            ekstrafeltradArrayList.add(ekstrafeltrad);
            return ekstrafeltrad;
        }

        ekstrafeltrad = ekstrafeltradArrayList.getLast();
        if (ekstrafeltrad.getPost().equals(post)) {
            return ekstrafeltrad;
        } else {
            ekstrafeltrad = new Ekstrafeltrad(post);
            ekstrafeltradArrayList.add(ekstrafeltrad);
            return ekstrafeltrad;
        }
    }

    public List<Post> finnPosterIKostnadspakken(Periodepost kostnadspakke) {
        return postRepository.findByKostnadsPakke(kostnadspakke);
    }


    private static class Ekstrafeltrad {
        Post post;
        String forelderpostkortnavnString;
        String kursString;
        String originaltBeloepString;
        String valutaString;
        String ekstraInfoString;
        String importradString;

        public Ekstrafeltrad(Post post) {
            this.post = post;
        }

        public Post getPost() {
            return post;
        }

        public void setForelderpostkortnavnString(String forelderpostkortnavnString) {
            this.forelderpostkortnavnString = forelderpostkortnavnString;
        }

        public void setKursString(String kursString) {
            this.kursString = kursString;
        }

        public void setOriginaltBeloepString(String originaltBeloepString) {
            this.originaltBeloepString = originaltBeloepString;
        }

        public void setValutaString(String valutaString) {
            this.valutaString = valutaString;
        }

        public void setEkstraInfoString(String ekstraInfoString) {
            this.ekstraInfoString = ekstraInfoString;
        }

        public void setImportradString(String importradString) {
            this.importradString = importradString;
        }
    }
}
