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
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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



    public void importerInnholdIEkstraFeltArrayList(){
        if (ekstrafeltradArrayList==null || ekstrafeltradArrayList.isEmpty()) {
            Loggekyklop.hent().loggADVARSEL("EkstrafeltradArrayList er tom, avbryter import av innholdet");
            return;
        }

        System.out.println("EkstrafeltradArraylist har " + ekstrafeltradArrayList.size() + " rader.");

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
        if (ekstrafeltrad.getEkstraInfoString()!=null && !ekstrafeltrad.getEkstraInfoString().isEmpty()) {
            sb.append(ekstrafeltrad.getEkstraInfoString()).append("\n");
        }
        if (ekstrafeltrad.getValutaString()!=null && !ekstrafeltrad.getValutaString().isEmpty()){
            sb.append("Valuta: ").append(ekstrafeltrad.getValutaString()).append("\n");
        }
        if (ekstrafeltrad.getKursString()!=null && !ekstrafeltrad.getKursString().isEmpty()) {
            sb.append("Kurs: ").append(ekstrafeltrad.getKursString()).append("\n");
        }
        if(ekstrafeltrad.getOriginaltBeloepString()!=null && !ekstrafeltrad.getOriginaltBeloepString().isEmpty()) {
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


        List<Post> forelderposter = super.findByDatoLocalDateAndTekstFraBankenStringAndNormalposttypeEnum(
                ekstrafeltrad.getPost().getDatoLocalDate(),
                ekstrafeltrad.getPost().getTekstFraBankenString(),
                NormalposttypeEnum.UTELATES
        );

        if (forelderposter.size()==1) {
            ekstrafeltrad.getPost().setForelderPostUUID(forelderposter.getFirst().getUuid().toString());
            lagre(ekstrafeltrad.getPost());
        } else {
            String strOpplysninger = "Dato = " + ekstrafeltrad.getPost().getDatoLocalDate() + ", " +
                    "TekstFraBankenString = " + ekstrafeltrad.getPost().getTekstFraBankenString() + ", " +
                    "NormalposttypeEnum = UTELATES";
            if (forelderposter.size()>1) {
                Loggekyklop.hent().loggADVARSEL("Fant mer enn en post med opplysningene " + strOpplysninger + ", kobler ikke til forelderpost");
            } else if (forelderposter.isEmpty()) {
                Loggekyklop.hent().loggADVARSEL("Fant ingen poster med opplysningene " + strOpplysninger + ", kobler ikke til forelderpost");
            }
        }

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



    public Stream<Post> finnAlleSomStream(PageRequest springPageRequest) {
        return postRepository.findAll(springPageRequest).stream();
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

        public void setImportradString(String importradString) {
            this.importradString = importradString;
        }
    }
}
