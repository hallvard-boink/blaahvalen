package com.hallvardlaerum.regnskap.service;



import com.hallvardlaerum.grunndata.data.Kategori;
import com.hallvardlaerum.grunndata.service.KategoriService;
import com.hallvardlaerum.libs.database.EntitetAktig;
import com.hallvardlaerum.libs.database.EntitetMedForelderAktig;
import com.hallvardlaerum.libs.database.EntitetserviceMal;
import com.hallvardlaerum.libs.database.EntitetserviceMedForelderMal;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeAktig;

import com.hallvardlaerum.regnskap.data.Post;
import com.hallvardlaerum.regnskap.data.PosttypeEnum;
import com.hallvardlaerum.regnskap.ui.PostRedigeringsomraade;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class PostService extends EntitetserviceMedForelderMal<Post,Kategori, PostRepository> {
    private PostRedigeringsomraade postRedigeringsomraade;
    private KategoriService kategoriService;
    private ArrayList<Ekstrafeltrad> ekstrafeltradArrayList;

    public PostService(PostRepository postRepository) {
        super(Post.class, postRepository);
    }

    public void initier(PostRedigeringsomraade postRedigeringsomraade, KategoriService kategoriService) {
        this.postRedigeringsomraade = postRedigeringsomraade;
        this.kategoriService = kategoriService;
        postRedigeringsomraade.initier(kategoriService);
    }


    //TODO: Hva brukes denne til?
    @Override
    public Post opprettEntitetMedForelder() {
        return opprettEntitet();
    }

    @Override
    public Post opprettEntitet() {
        return leggTilUUID(new Post());
    }

    @Override
    public boolean behandleSpesialfeltVedImport(Object entitet, Field field, String nyVerdi) {
        return false;
    }

    @Override
    public boolean importerFeltUtenomEntiteten(EntitetAktig entitet, String feltnavnString, String verdiStreng, String importradString) {
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



    public void importerInnholdIEkstraFeltArrayList(){
        if (ekstrafeltradArrayList==null || ekstrafeltradArrayList.isEmpty()) {
            Loggekyklop.hent().loggADVARSEL("EkstrafeltradArrayList er tom, avbryter import av innholdet");
            return;
        }

        for (Ekstrafeltrad ekstrafeltrad: ekstrafeltradArrayList) {
            if (ekstrafeltrad.getPost()!=null) {
                importerForelderpostFraKortnavn(ekstrafeltrad);
                settSammenEkstraInfo(ekstrafeltrad);
                lagre(ekstrafeltrad.getPost());
            }
        }

    }

    private void settSammenEkstraInfo(Ekstrafeltrad ekstrafeltrad) {
        StringBuilder sb = new StringBuilder();
        if (!ekstrafeltrad.getEkstraInfoString().isEmpty()) {
            sb.append(ekstrafeltrad.getEkstraInfoString()).append("\n");
        }
        if (!ekstrafeltrad.getValutaString().isEmpty()){
            sb.append("Valuta: ").append(ekstrafeltrad.getValutaString()).append("\n");
        }
        if (!ekstrafeltrad.getKursString().isEmpty()) {
            sb.append("Kurs: ").append(ekstrafeltrad.getKursString()).append("\n");
        }
        if(!ekstrafeltrad.getOriginaltBeloepString().isEmpty()) {
            sb.append("Originalt beløp: ").append(ekstrafeltrad.getOriginaltBeloepString()).append("\n");
        }
        ekstrafeltrad.getPost().setEkstraInfoString(sb.toString());
    }

    private void importerForelderpostFraKortnavn(Ekstrafeltrad ekstrafeltrad) {
        if (ekstrafeltrad.getForelderpostkortnavnString()==null || ekstrafeltrad.getForelderpostkortnavnString().isEmpty()) {
            return;
        }

        if (ekstrafeltrad.getPost()==null) {
            Loggekyklop.hent().loggADVARSEL("Post eller forelderpostkortnavnString er tom, avbryter.");
            return;
        }


        List<Post> forelderposter = super.hentRepository().findByDatoLocalDateAndTekstFraBankenStringAndPosttypeEnum(
                ekstrafeltrad.getPost().getDatoLocalDate(),
                ekstrafeltrad.getPost().getTekstFraBankenString(),
                PosttypeEnum.UTELATES
        );
        if (forelderposter.size()==1) {
            ekstrafeltrad.getPost().setForelderPostUUID(forelderposter.getFirst().getUuid().toString());
            lagre(ekstrafeltrad.getPost());
        }


    }


    private Ekstrafeltrad hentEllerOpprettEkstraFeltrad(Post post){
        Ekstrafeltrad ekstrafeltrad = null;
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


    @Override
    public boolean behandleSpesialfeltVedImport(Object entitet, Field field, String nyVerdi, String importradString) {
        if (field==null) {
            return false;
        }

        if (field.getName().equalsIgnoreCase("Kategori")) {
            Kategori kategori = kategoriService.finnEtterTittel(nyVerdi);
            if (kategori!=null) {
                try {
                    field.set(entitet,kategori);
                    return true;
                } catch (IllegalAccessException e) {

                    Loggekyklop.hent().loggTilFilINFO("Klarte ikke å sette Kategori med entiteten " + kategori.hentBeskrivendeNavn() + ". Importrad: " + importradString);
                    return false;
                }
            }
        }

        return false;

    }


    @Override
    public RedigeringsomraadeAktig<Post> hentRedigeringsomraadeAktig() {
        return postRedigeringsomraade;
    }



    public Stream<Post> finnAlleSomStream(PageRequest springPageRequest) {
        return super.hentRepository().findAll(springPageRequest).stream();

    }


    private class Ekstrafeltrad {
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

        public void setPost(Post post) {
            this.post = post;
        }

        public String getForelderpostkortnavnString() {
            return forelderpostkortnavnString;
        }

        public void setForelderpostkortnavnString(String forelderpostkortnavnString) {
            this.forelderpostkortnavnString = forelderpostkortnavnString;
        }

        public String getKursString() {
            return kursString;
        }

        public void setKursString(String kursString) {
            this.kursString = kursString;
        }

        public String getOriginaltBeloepString() {
            return originaltBeloepString;
        }

        public void setOriginaltBeloepString(String originaltBeloepString) {
            this.originaltBeloepString = originaltBeloepString;
        }

        public String getValutaString() {
            return valutaString;
        }

        public void setValutaString(String valutaString) {
            this.valutaString = valutaString;
        }

        public String getEkstraInfoString() {
            return ekstraInfoString;
        }

        public void setEkstraInfoString(String ekstraInfoString) {
            this.ekstraInfoString = ekstraInfoString;
        }

        public String getImportradString() {
            return importradString;
        }

        public void setImportradString(String importradString) {
            this.importradString = importradString;
        }
    }
}
