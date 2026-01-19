package com.hallvardlaerum.post.normalpost;

import com.hallvardlaerum.kategori.Kategori;
import com.hallvardlaerum.kategori.KategoriService;
import com.hallvardlaerum.kategori.KategoriType;
import com.hallvardlaerum.libs.database.EntitetAktig;
import com.hallvardlaerum.libs.eksportimport.CSVImportassistentMal;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.felter.Datokyklop;
import com.hallvardlaerum.periodepost.Periodepost;
import com.hallvardlaerum.periodepost.kostnadspakke.KostnadspakkeService;
import com.hallvardlaerum.post.Post;
import com.hallvardlaerum.post.PostklasseEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;

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
    private KostnadspakkeService kostnadspakkeService;

    public NormalpostFraGamleBlaahvalenCSVImportassistent() {
        this.normalpostService = Allvitekyklop.hent().getNormalpostService();
        this.normalpostView = Allvitekyklop.hent().getNormalpostView();
        this.kategoriService = Allvitekyklop.hent().getKategoriService();
        this.kostnadspakkeService = Allvitekyklop.hent().getKostnadspakkeService();
    }

    @Override
    public void forberedImport() {
        Loggekyklop.bruk().forberedTilImportloggTilFil();
    }

    @Override
    public void ryddOppEtterImport() {

        skalIkkekategoriseresKategori = kategoriService.hentRepository().findByKategoriType(KategoriType.SKAL_IKKE_KATEGORISERES).getFirst();
        importerInnholdIEkstraFeltArrayList();

        normalpostView.oppdaterSoekeomraadeFinnAlleRader();
        Loggekyklop.bruk().avsluttImportloggTilFil();
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

        knyttTilKostnadspakke(hentVerdi("KostnadspakkeTittel"));
        knyttTilKategori(hentVerdi("Kategori"));

        // Lagre navn på forelderpost nå, knytter senere
        String forelderpostKortnavn = hentVerdi("ForelderpostKortnavn");
        if (!forelderpostKortnavn.isEmpty()) {
            lagreEkstrafeltTilSenere(normalpost, "ForelderpostKortnavn",forelderpostKortnavn, celler);
        }
        normalpostService.lagre(normalpost);

        return normalpost;
    }

    private void knyttTilKostnadspakke(String kostnadspakketittelString) {
        if (kostnadspakketittelString==null || kostnadspakketittelString.isEmpty()) {
            return;
        }

        Periodepost kostnadspakke = kostnadspakkeService.finnEtterTittel(kostnadspakketittelString);
        if (kostnadspakke==null) {
            Loggekyklop.bruk().loggADVARSEL("Fant ikke kostnadspakke med tittel " + kostnadspakketittelString);
        } else {
            normalpost.setKostnadsPakke(kostnadspakke);
        }
    }


    private void knyttTilKategori(String kategoriString) {
        Optional<Kategori> kategoriOptional = kategoriService.finnOppsummerendeUnderkategori(kategoriString);
        //Optional<Kategori> kategoriOptional = kategoriService.finnEtterTittelOgUnderTittel(kategoriString,"-");
        if (kategoriOptional.isEmpty()) {
            Loggekyklop.bruk().loggINFO("Fant ikke kategori med hovedtittel " + kategoriString + " og undertittel '-'");
            return;
        }

        Kategori kategori = kategoriOptional.get();
        Kategori kategoriFraKostnadspakke = null;

        if (normalpost.getKostnadsPakke()==null) {
            normalpost.setKategori(kategori);
        } else {
            kategoriFraKostnadspakke = normalpost.getKostnadsPakke().getKategori();
            if (kategoriFraKostnadspakke!=null) {
                //Samme hovedtittel, men normalposten har
                if (kategori.getTittel().equals(kategoriFraKostnadspakke.getTittel()) &&
                        kategori.getErOppsummerendeUnderkategori()){
                    normalpost.setKategori(kategoriFraKostnadspakke);
                }
            }
        }
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

        List<Post> forelderposter = normalpostService.finnEtterDatoOgTekstfrabankenOgNormalposttypeenum(
                ekstrafeltrad.getPost().getDatoLocalDate(),
                ekstrafeltrad.getPost().getTekstFraBankenString(),
                NormalposttypeEnum.UTELATES
        );

        if (!forelderposter.isEmpty()) {
            ekstrafeltrad.getPost().setForelderPostUUID(forelderposter.getFirst().getUuid().toString());
            normalpostService.lagre(ekstrafeltrad.getPost());
        } else {
            List<Post> forelderposterSkalIkkeKategoriseres = normalpostService.finnPostEtterDatoOgTekstfrabankenOgKategoriOgPostklasseEnum(
                    ekstrafeltrad.getPost().getDatoLocalDate(),
                    ekstrafeltrad.getPost().getTekstFraBankenString(),
                    skalIkkekategoriseresKategori,
                    PostklasseEnum.NORMALPOST
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

