package com.hallvardlaerum.post.normalpost;

import com.hallvardlaerum.grunndata.kategori.*;
import com.hallvardlaerum.libs.database.EntitetAktig;
import com.hallvardlaerum.libs.eksportimport.CSVImportassistentMal;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.felter.Datokyklop;
import com.hallvardlaerum.libs.felter.TekstKyklop;
import com.hallvardlaerum.post.Post;
import com.hallvardlaerum.post.PostklasseEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import org.springframework.data.domain.Example;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NormalpostFraGamleBlaahvalenCSVImportassistent extends CSVImportassistentMal<Post> {
    private NormalpostService normalpostService;
    private Post normalpost;
    private NormalpostView normalpostView;
    private ArrayList<Ekstrafeltrad> ekstrafeltradArrayList;
    private KategoriService kategoriService;
    private Kategori skalIkkekategoriseresKategori;

    public NormalpostFraGamleBlaahvalenCSVImportassistent() {
        this.normalpostService = Allvitekyklop.hent().getNormalpostService();
        this.normalpostView = Allvitekyklop.hent().getNormalpostView();
        this.kategoriService = Allvitekyklop.hent().getKategoriService();
    }

    @Override
    public void forberedImport() {
        Loggekyklop.hent().initierLoggfil();
        Loggekyklop.hent().huskStatus();
        Loggekyklop.hent().settNivaaINFO();
    }

    @Override
    public void ryddOppEtterImport() {

        skalIkkekategoriseresKategori = kategoriService.hentRepository().findByKategoriType(KategoriType.SKAL_IKKE_KATEGORISERES).getFirst();

        importerInnholdIEkstraFeltArrayList();

        normalpostView.oppdaterSoekeomraade();
        Loggekyklop.hent().lukkLoggfil();
        Loggekyklop.hent().tilbakestillStatus();
    }

    @Override
    public Post konverterFraTekstRadOgLagre(ArrayList<String> feltnavnCSVArrayList, String[] celler) {
        super.lesInnFeltnavnogCeller(feltnavnCSVArrayList, celler);

        normalpost = normalpostService.opprettEntitet();

        normalpost.setDatoLocalDate(Datokyklop.hent().opprettDatoSom_DDpMMpYYYY(hentVerdi("datoLocalDate")));
        normalpost.setTekstFraBankenString(hentVerdi("tekstFraBankenString"));
        normalpost.setInnPaaKontoInteger(parseInt(hentVerdi("innPaaKontoInteger")));
        normalpost.setUtFraKontoInteger(parseInt(hentVerdi("utFraKontoInteger")));
        normalpost.setBeskrivelseString(hentVerdi("beskrivelseString"));
        normalpost.setPostklasseEnum(PostklasseEnum.NORMALPOST);
        normalpost.setNormalposttypeEnum(NormalposttypeEnum.hentFraTittel(hentVerdi("normalposttypeEnum")));
        normalpost.setNormalPoststatusEnum(NormalpoststatusEnum.hentFraTittel(hentVerdi("normalpoststatusEnum")));
        normalpost.setEkstraInfoString(hentVerdier(true,true,
                "EkstraInfo", "Valuta", "Kurs", "Original beloep", "tekstFraAvsenderString"));


        String tittelKategoriString = hentVerdi("Kategori");
        String kostnadspakketittelString = hentVerdi("KostnadspakkeTittel");

        if (kostnadspakketittelString != null && kostnadspakketittelString.length()>1) {
            String foersteTegnString = kostnadspakketittelString.substring(0,1);
            try {
                Integer foersteTegnInteger = Integer.parseInt(foersteTegnString);
                kostnadspakketittelString = TekstKyklop.hent().fjernFoersteDelAvStrengMedDelimiter(kostnadspakketittelString, "\\ ");
            } catch (NumberFormatException e) {
                // Trenger ikke gjøre noe, første tegn er ikke et tall;
            }
        }


        Optional<Kategori> kategoriOptionalEndenode = kategoriService.finnEtterTittelOgUnderTittel(tittelKategoriString,kostnadspakketittelString);
        if (kategoriOptionalEndenode.isPresent()) {
            normalpost.setKategori(kategoriOptionalEndenode.get());
        } else {
            Kategori kategori = kategoriService.opprettEntitet();
            kategori.setKategoriType(KategoriType.DETALJERT);
            kategori.setKategoriRetning(KategoriRetning.UT);
            kategori.setTittel(tittelKategoriString);
            kategori.setUndertittel(kostnadspakketittelString);
            kategoriService.lagre(kategori);
            normalpost.setKategori(kategori);
            Loggekyklop.hent().loggTilFilINFO("La til ny kategori med tittel " + tittelKategoriString + " og undertittel " + kostnadspakketittelString);
        }

        String forelderpostKortnavn = hentVerdi("ForelderpostKortnavn");
        if (!forelderpostKortnavn.isEmpty()) {
            lagreEkstrafeltTilSenere(normalpost, "ForelderpostKortnavn",forelderpostKortnavn, celler);
        }
        normalpostService.lagre(normalpost);

        return normalpost;
    }



    public boolean lagreEkstrafeltTilSenere(EntitetAktig entitet, String feltnavnString, String verdiStreng, String[] celler) {
        Ekstrafeltrad ekstrafeltrad = hentEllerOpprettEkstraFeltrad((Post)entitet);
        if (ekstrafeltrad==null) {
            return false;
        } else {
            ekstrafeltrad.setImportradString(celler);
        }

        if (feltnavnString.equalsIgnoreCase("ForelderpostKortnavn")) {
            ekstrafeltrad.setForelderpostkortnavnString(verdiStreng);
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
                normalpostService.lagre(ekstrafeltrad.getPost());
            }
        }

    }



    private void importerForelderpostFraKortnavn(Ekstrafeltrad ekstrafeltrad) {
        if (ekstrafeltrad.getForelderpostkortnavnString()==null || ekstrafeltrad.getForelderpostkortnavnString().isEmpty()) {
            return;
        }

        if (ekstrafeltrad.getPost()==null) {
            Loggekyklop.hent().loggADVARSEL("Post eller forelderpostkortnavnString er tom, avbryter.");
            return;
        }

        List<Post> forelderposter = normalpostService.hentRepository().findByDatoLocalDateAndTekstFraBankenStringAndNormalposttypeEnum(
                ekstrafeltrad.getPost().getDatoLocalDate(),
                ekstrafeltrad.getPost().getTekstFraBankenString(),
                NormalposttypeEnum.UTELATES
        );

        if (!forelderposter.isEmpty()) {
            ekstrafeltrad.getPost().setForelderPostUUID(forelderposter.getFirst().getUuid().toString());
            normalpostService.lagre(ekstrafeltrad.getPost());
        } else {
            List<Post> forelderposterSkalIkkeKategoriseres = normalpostService.hentRepository().findByDatoLocalDateAndTekstFraBankenStringAndKategori(
                    ekstrafeltrad.getPost().getDatoLocalDate(),
                    ekstrafeltrad.getPost().getTekstFraBankenString(),
                    skalIkkekategoriseresKategori
            );
            if (!forelderposterSkalIkkeKategoriseres.isEmpty()) {
                ekstrafeltrad.getPost().setForelderPostUUID(forelderposterSkalIkkeKategoriseres.getFirst().getUuid().toString());
                normalpostService.lagre(ekstrafeltrad.getPost());
            } else {
                    String strOpplysninger = "Dato = " + ekstrafeltrad.getPost().getDatoLocalDate() + ", " +
                            "TekstFraBankenString = " + ekstrafeltrad.getPost().getTekstFraBankenString() + ", " +
                            "NormalposttypeEnum = UTELATES";
                    if (forelderposterSkalIkkeKategoriseres.size()>1) {
                        Loggekyklop.hent().loggTilFilADVARSEL("Fant mer enn en post med opplysningene " + strOpplysninger + ", kobler ikke til forelderpost");
                    } else if (forelderposterSkalIkkeKategoriseres.isEmpty()) {
                        Loggekyklop.hent().loggTilFilADVARSEL("Fant ingen poster med opplysningene " + strOpplysninger + ", kobler ikke til forelderpost");
                    }
            }
        }
    }




    private Ekstrafeltrad hentEllerOpprettEkstraFeltrad(Post post){
        if (post==null) {
            return null;
        }

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


    private class Ekstrafeltrad {
        Post post;
        String forelderpostkortnavnString;
        String[] importradString;
        LocalDate datoLocalDate;
        String tekstFraBanken;


        public Ekstrafeltrad(Post post) {
            this.post = post;
        }


        public LocalDate getDatoLocalDate() {
            return datoLocalDate;
        }

        public void setDatoLocalDate(LocalDate datoLocalDate) {
            this.datoLocalDate = datoLocalDate;
        }

        public String getTekstFraBanken() {
            return tekstFraBanken;
        }

        public void setTekstFraBanken(String tekstFraBanken) {
            this.tekstFraBanken = tekstFraBanken;
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

        public String[] getImportradString() {
            return importradString;
        }

        public void setImportradString(String[] importradString) {
            this.importradString = importradString;
        }
    }

}

