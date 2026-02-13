package com.hallvardlaerum.post.normalpost;


import com.hallvardlaerum.kategori.Kategori;
import com.hallvardlaerum.libs.database.EntitetAktig;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.felter.HelTallMester;
import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.periode.Periode;
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

/**
 * Entitetservicer skal ha metoder på norsk, og fungerer som en oversetter mot repository
 */
@Service
public class NormalpostService extends PostServiceMal implements InitieringsEgnet {
    private ArrayList<Ekstrafeltrad> ekstrafeltradArrayList;
    private Boolean erInitiert = false;
    private PostRepository postRepository;


// ===================
// === Finn poster ===
// region Finn poster

    public List<Post> finnEtterDatoOgTekstfrabankenOgNormalposttypeenum(LocalDate datoLocalDate, String tekstFraBankenString, NormalposttypeEnum normalposttypeEnum) {
        return postRepository.findByDatoLocalDateAndTekstFraBankenStringAndNormalposttypeEnum(datoLocalDate, tekstFraBankenString, normalposttypeEnum);
    }

    public List<Post> finnPosterIKostnadspakken(Periodepost kostnadspakke) {
        return postRepository.findByKostnadsPakke(kostnadspakke);
    }

// endregion




// =======================
// === Finn enkeltpost ===
// region Finn enkeltpost

    public Post finnEtterDatoOgTekstfrabankenOgBeskrivelseNormalposttypeenum(LocalDate dato, String tekstFraBankenString, String beskrivelseString) {
        if (dato == null || tekstFraBankenString == null) {
            return null;
        }

        List<Post> poster = postRepository.findByDatoLocalDateAndTekstFraBankenStringAndBeskrivelseString(dato, tekstFraBankenString, beskrivelseString);
        if (poster.isEmpty()) {
            Loggekyklop.bruk().loggADVARSEL("Fant ikke post for dato:" + dato + ", tekstfrabanken:" + tekstFraBankenString + ", beskrivelseString:" + beskrivelseString + ". Avbryter");
            return null;
        } else if (poster.size() > 1) {
            Loggekyklop.bruk().loggADVARSEL("Fant mer enn en post for dato:" + dato + ", tekstfrabanken:" + tekstFraBankenString + ", beskrivelseString:" + beskrivelseString + ". Avbryter");
            return null;
        } else {
            return poster.getFirst();
        }
    }

// endregion



// ==============
// === Summer ===
// region Summer

    public Integer sumInnPeriodeNormalposterUtenkategori(Periode periode) {
        if (periode == null) {
            return null;
        }
        return postRepository.sumInnFraDatoTilDatoNormalposterUtenKategori(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate());
    }

    public Integer sumUtPeriodeNormalposterUtenkategori(Periode periode) {
        if (periode == null) {
            return null;
        }
        return postRepository.sumUtFraDatoTilDatoNormalposterUtenKategori(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate());
    }

    public Integer sumInnEllerUtFradatoTildatoKategoritittel(LocalDate fraDato, LocalDate tilDato, String kategoritittel) {
        Tuple tuple = postRepository.sumNormalPosterFradatoTilDatoKategoritittel(fraDato, tilDato, kategoritittel);
        if (tuple == null) {
            return 0;
        }

        Integer innInteger = HelTallMester.konverterBigdecimalTilInteger(tuple.get(0, BigDecimal.class), true);
        Integer utInteger = HelTallMester.konverterBigdecimalTilInteger(tuple.get(1, BigDecimal.class), true);

        return innInteger + utInteger;
    }

// endregion



// ======================
// === CRUD og import ===
// region CRUD og import


    @Override
    public boolean behandleSpesialfeltVedImport(Object entitet, Field field, String nyVerdi, String importradString) {
        if (field==null) {
            return false;
        }
        if (field.getName().equalsIgnoreCase("kategori")) {
            behandleSpesialfeltVedImport_importerKategorifra_UUID(entitet,field,nyVerdi, importradString);
            return true;

        } else if (field.getName().equalsIgnoreCase("kostnadspakke")) {
            behandleSpesialfeltVedImport_importerPeriodepost(entitet,field,nyVerdi,importradString);
            return true;

        } else {
            return false;
        }
    }

    private void behandleSpesialfeltVedImport_importerKategorifra_UUID(Object entitet, Field field, String nyVerdi, String importradString) {
        Kategori kategori = Allvitekyklop.hent().getKategoriService().finnEtterUUID(nyVerdi);
        if (kategori!=null) {
            try {
                field.set(entitet,kategori);
            } catch (IllegalAccessException e) {
                Loggekyklop.bruk().loggADVARSEL("Fant kategori, men klarte ikke å sette den: " + kategori.hentBeskrivendeNavn() + ". Hele raden:" + importradString);
            }
        } else {
            Loggekyklop.bruk().loggADVARSEL("Fant ikke kategori med UUID " + nyVerdi + ". Hele raden:" + importradString);
        }
    }



    private void behandleSpesialfeltVedImport_importerPeriodepost(Object entitet, Field field, String nyVerdi, String importradString) {
        Periodepost periodepost = Allvitekyklop.hent().getMaanedsoversiktpostService().finnEtterUUID(nyVerdi); //klarer å håndtere årsoversiktposter og
        if (periodepost!=null) {
            try {
                field.set(entitet,periodepost);
            } catch (IllegalAccessException e) {
                Loggekyklop.bruk().loggADVARSEL("Fant periodepost, men klarte ikke å sette den: " + periodepost.hentBeskrivendeNavn() + ". Hele raden:" + importradString);
            }
        } else {
            Loggekyklop.bruk().loggADVARSEL("Fant ikke periodepost med UUID " + nyVerdi + ". Hele raden:" + importradString);
        }
    }


    @Override
    public boolean lagreEkstrafeltTilSenere(EntitetAktig entitet, String feltnavnString, String verdiStreng, String importradString) {
        Ekstrafeltrad ekstrafeltrad = hentEllerOpprettEkstraFeltrad((Post) entitet);
        if (ekstrafeltrad == null) {
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


    private Ekstrafeltrad hentEllerOpprettEkstraFeltrad(Post post) {
        if (post == null) {
            return null;
        }

        Ekstrafeltrad ekstrafeltrad;
        if (ekstrafeltradArrayList == null) {
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
    public Post opprettEntitet() {
        Post normalpost = leggTilUUID(new Post());
        normalpost.setPostklasseEnum(PostklasseEnum.NORMALPOST);
        return normalpost;
    }


    //TODO: Hva brukes denne til?
    @Override
    public Post opprettEntitetMedForelder() {
        return opprettEntitet();
        //Skulle det ha vært satt inn en forelder her? Fra hvor???
    }


// endregion




// =================================
// === Constructor og Initiering ===
// region Constructor og initiering

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
            this.postRepository = Allvitekyklop.hent().getPostRepository();
            erInitiert = true;
        }
    }


// endregion


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
