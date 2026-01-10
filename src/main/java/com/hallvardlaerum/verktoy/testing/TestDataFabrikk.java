package com.hallvardlaerum.verktoy.testing;

import com.hallvardlaerum.kategori.Kategori;
import com.hallvardlaerum.kategori.KategoriService;
import com.hallvardlaerum.libs.felter.Datokyklop;
import com.hallvardlaerum.periode.Periode;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import com.hallvardlaerum.periode.aarsoversikt.AarsoversiktService;
import com.hallvardlaerum.periode.maanedsoversikt.MaanedsoversiktService;
import com.hallvardlaerum.periodepost.Periodepost;
import com.hallvardlaerum.periodepost.aarsoversiktpost.AarsoversiktpostService;
import com.hallvardlaerum.periodepost.maanedsoversiktpost.MaanedsoversiktpostService;
import com.hallvardlaerum.post.Post;
import com.hallvardlaerum.post.PostklasseEnum;
import com.hallvardlaerum.post.budsjettpost.BudsjettpostService;
import com.hallvardlaerum.post.budsjettpost.BudsjettpoststatusEnum;
import com.hallvardlaerum.post.normalpost.NormalpostService;
import com.hallvardlaerum.post.normalpost.NormalpoststatusEnum;
import com.hallvardlaerum.post.normalpost.NormalposttypeEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TestDataFabrikk {
    private NormalpostService normalpostService;
    private BudsjettpostService budsjettpostService;
    private AarsoversiktService aarsoversiktService;
    private AarsoversiktpostService aarsoversiktpostService;
    private MaanedsoversiktService maanedsoversiktService;
    private MaanedsoversiktpostService maanedsoversiktpostService;
    private KategoriService kategoriService;
    private Integer aarstall;
    private LocalDate fraLocalDate;
    private LocalDate tilLocalDate;


    /**
     * Produser testData for et årstall som kan skilles fra ekte data
     * @param aarstall Årstallet det skal opprettes testdata for
     */
    public void produserData(Integer aarstall){
        this.aarstall = aarstall;
        hentServicer();
        fraLocalDate = LocalDate.of(this.aarstall, 1,1);
        tilLocalDate = LocalDate.of(this.aarstall, 12,31);

        slettTestData();

        produserAarsoversikt();
        produserMaanedsoversikter();
        produserNormalposter();
        produserBudsjettposter();
    }

    /**
     * Normalpostene skal dekke
     * <br/>
     * - Ukategoriserte poster
     * - Vanlige kategorier
     * - Kredittkortregninger med kredittkortposter (hver kredittkortpost  er egentlig et helt kjøp)
     * - Delposter, dvs. splittede utgifter
     * - Uferdige, dvs. under arbeid
     * - Overføringer
     */
    private void produserNormalposter() {
        ArrayList<NormalpostDataKnippe> normalpostKnippe = opprettDataknipper_Normalposter();
        for (NormalpostDataKnippe normalpostDataKnippe :normalpostKnippe) {
            Kategori kategori = null;
            Optional<Kategori> kategoriOptional = kategoriService.finnEtterTittelOgUnderTittel(normalpostDataKnippe.kategoriTittel, normalpostDataKnippe.kategoriUndertittel);
            if (kategoriOptional.isPresent()) {
                kategori = kategoriOptional.get();
            }
            Post normalpost = normalpostService.opprettEntitet();
            normalpost.setDatoLocalDate(Datokyklop.hent().opprettDatoSomYYYY_MM_DD(aarstall + "-" + normalpostDataKnippe.deldatoString));
            normalpost.setKategori(kategori);
            normalpost.setNormalposttypeEnum(normalpostDataKnippe.normalposttypeEnum);
            normalpost.setNormalPoststatusEnum(normalpostDataKnippe.normalpoststatusEnum);
            normalpost.setBeskrivelseString(normalpostDataKnippe.beskrivelse);
            normalpost.setInnPaaKontoInteger(normalpostDataKnippe.innPaaKontoInteger);
            normalpost.setUtFraKontoInteger(normalpostDataKnippe.utFraKontoInteger);
            normalpost.setTekstFraBankenString("Automatisk opprettet testdata");
            normalpostService.lagre(normalpost);

        }
    }

    private ArrayList<NormalpostDataKnippe> opprettDataknipper_Normalposter(){
        ArrayList<NormalpostDataKnippe> knipper = new ArrayList<>();

        //deldatoString, kategoriTittel, kategoriUndertittel, normalposttypeEnum, normalpoststatusEnum, beskrivelse, innPaaKontoInteger, utFraKontoInteger, budsjettpoststatusEnum

        // === Inn på konto ===
        knipper.add(new NormalpostDataKnippe("01-01","Lønn","-",NormalposttypeEnum.NORMAL, NormalpoststatusEnum.FERDIG, "Lønn",50000,null));
        knipper.add(new NormalpostDataKnippe("02-01","Lønn","-",NormalposttypeEnum.NORMAL, NormalpoststatusEnum.FERDIG, "Lønn",50000,null));
        knipper.add(new NormalpostDataKnippe("03-01","Lønn","-",NormalposttypeEnum.NORMAL, NormalpoststatusEnum.FERDIG, "Lønn",50000,null));

        // === Vanlig post ===
        knipper.add(new NormalpostDataKnippe("01-02","Dagligvarer","-",NormalposttypeEnum.NORMAL, NormalpoststatusEnum.FERDIG, "Dagligvarer",null,2000));
        knipper.add(new NormalpostDataKnippe("02-02","Dagligvarer","-",NormalposttypeEnum.NORMAL, NormalpoststatusEnum.FERDIG, "Dagligvarer",null,2000));
        knipper.add(new NormalpostDataKnippe("03-02","Dagligvarer","-",NormalposttypeEnum.NORMAL, NormalpoststatusEnum.FERDIG, "Dagligvarer",null,2000));

        // === Kredittkortregning ===
        knipper.add(new NormalpostDataKnippe("01-03","Kredittkort Hallvard","-",NormalposttypeEnum.UTELATES, NormalpoststatusEnum.FERDIG, "Kredittkort",null,6000));
        knipper.add(new NormalpostDataKnippe("01-03","Media og underholdning","-",NormalposttypeEnum.KREDITTKORTPOST, NormalpoststatusEnum.FERDIG, "Dataspill",null,2000));
        knipper.add(new NormalpostDataKnippe("01-03","Innbo, møbler og utstyr","-",NormalposttypeEnum.KREDITTKORTPOST, NormalpoststatusEnum.FERDIG, "Airfryer",null,2000));
        knipper.add(new NormalpostDataKnippe("01-03","Ferie og reiser","-",NormalposttypeEnum.KREDITTKORTPOST, NormalpoststatusEnum.FERDIG, "Ferietur",null,2000));

        // === Uferdige ===
        knipper.add(new NormalpostDataKnippe("01-04","Fagforening","-",NormalposttypeEnum.NORMAL, NormalpoststatusEnum.UNDER_ARBEID, "Fagforening",null,500));
        knipper.add(new NormalpostDataKnippe("02-04","Fagforening","-",NormalposttypeEnum.NORMAL, NormalpoststatusEnum.UNDER_ARBEID, "Fagforening",null,500));
        knipper.add(new NormalpostDataKnippe("03-04","Fagforening","-",NormalposttypeEnum.NORMAL, NormalpoststatusEnum.UNDER_ARBEID, "Fagforening",null,500));

        // === Overføringer ===
        knipper.add(new NormalpostDataKnippe("01-05","Overføring inn","-",NormalposttypeEnum.NORMAL, NormalpoststatusEnum.FERDIG, "Overføring fra sparekonto",10000,null));
        knipper.add(new NormalpostDataKnippe("02-05","Overføring ut","-",NormalposttypeEnum.NORMAL, NormalpoststatusEnum.FERDIG, "Overføring til ",null,15000));
        knipper.add(new NormalpostDataKnippe("03-05","Til sparing","-",NormalposttypeEnum.NORMAL, NormalpoststatusEnum.FERDIG, "Til sparekonto",null,3000));

        // === Kostnadspakker og detaljert kategorisering ===
        knipper.add(new NormalpostDataKnippe("01-06","Sport, hobby og fritid","Jakt",NormalposttypeEnum.NORMAL, NormalpoststatusEnum.FERDIG, "Ammunisjon",null,1000));
        knipper.add(new NormalpostDataKnippe("02-06","Sport, hobby og fritid","Tinas verksted",NormalposttypeEnum.NORMAL, NormalpoststatusEnum.FERDIG, "Garn",null,500));
        knipper.add(new NormalpostDataKnippe("03-06","Sport, hobby og fritid","Jakt",NormalposttypeEnum.NORMAL, NormalpoststatusEnum.FERDIG, "Ammunisjon",null,1000));

        // === Poster som ikke er kategorisert ===
        knipper.add(new NormalpostDataKnippe("01-07",null,null,NormalposttypeEnum.NORMAL, NormalpoststatusEnum.UBEHANDLET, "Ikke kategorisert, ut",null,1000));
        knipper.add(new NormalpostDataKnippe("02-07",null,null,NormalposttypeEnum.NORMAL, NormalpoststatusEnum.UBEHANDLET, "Ikke kategorisert, ut",null,2000));
        knipper.add(new NormalpostDataKnippe("03-07",null,null,NormalposttypeEnum.NORMAL, NormalpoststatusEnum.UBEHANDLET, "Ikke kategorisert, inn",3000,null));

        // === Delposter ===
        knipper.add(new NormalpostDataKnippe("01-08","[Skal ikke kategoriseres]","-",NormalposttypeEnum.UTELATES, NormalpoststatusEnum.FERDIG, "Dagligvarer med helgekos",null,4000));
        knipper.add(new NormalpostDataKnippe("01-08","Dagligvarer","-",NormalposttypeEnum.DELPOST, NormalpoststatusEnum.FERDIG, "Dagligvarer",null,2000));
        knipper.add(new NormalpostDataKnippe("01-08","Helgekos","-",NormalposttypeEnum.DELPOST, NormalpoststatusEnum.FERDIG, "Øl og dram",null,1000));
        knipper.add(new NormalpostDataKnippe("01-08","Gaver og veldedighet","-",NormalposttypeEnum.DELPOST, NormalpoststatusEnum.FERDIG, "Juleskinke",null,1000));

        return knipper;
    }

    /**
     * Budsjettpostene skal dekke:
     * - Inn på konto
     * - Ut fra konto
     * - Ikke tildelt
     */
    private void produserBudsjettposter() {
        ArrayList<BudsjettpostDataKnippe> knipper = opprettDataknipper_Budsjettposter();
        for (BudsjettpostDataKnippe knippe :knipper) {
            Kategori kategori = null;
            Optional<Kategori> kategoriOptional = kategoriService.finnEtterTittelOgUnderTittel(knippe.kategoriTittel, knippe.kategoriUndertittel);
            if (kategoriOptional.isPresent()) {
                kategori = kategoriOptional.get();
            }
            Post budsjettpost = budsjettpostService.opprettEntitet();
            budsjettpost.setDatoLocalDate(Datokyklop.hent().opprettDatoSomYYYY_MM_DD(aarstall + "-" + knippe.deldatoString));
            budsjettpost.setKategori(kategori);
            budsjettpost.setBeskrivelseString(knippe.beskrivelse);
            budsjettpost.setInnPaaKontoInteger(knippe.innPaaKontoInteger);
            budsjettpost.setUtFraKontoInteger(knippe.utFraKontoInteger);
            budsjettpost.setBudsjettpoststatusEnum(knippe.budsjettpoststatusEnum);
            budsjettpost.setTekstFraBankenString("Automatisk opprettet testdata");
            budsjettpostService.lagre(budsjettpost);

        }
    }

    private ArrayList<BudsjettpostDataKnippe> opprettDataknipper_Budsjettposter(){
        ArrayList<BudsjettpostDataKnippe> knipper = new ArrayList<>();

        // deldatoString, kategoriTittel, kategoriUndertittel, beskrivelse, innPaaKontoInteger, utFraKontoInteger, budsjettpoststatusEnum) {

        // === Inn på konto ===
        knipper.add(new BudsjettpostDataKnippe("01-01","Lønn","-", "Lønn",50000,null,BudsjettpoststatusEnum.TILDELT));
        knipper.add(new BudsjettpostDataKnippe("02-01","Lønn","-", "Lønn",50000,null,BudsjettpoststatusEnum.TILDELT));
        knipper.add(new BudsjettpostDataKnippe("03-01","Lønn","-", "Lønn",50000,null,BudsjettpoststatusEnum.TILDELT));

        // === Ut fra konto ===
        knipper.add(new BudsjettpostDataKnippe("01-02","Dagligvarer","-", "Dagligvarer",null,3000,BudsjettpoststatusEnum.TILDELT));
        knipper.add(new BudsjettpostDataKnippe("02-02","Dagligvarer","-", "Dagligvarer",null,2000,BudsjettpoststatusEnum.TILDELT));
        knipper.add(new BudsjettpostDataKnippe("03-02","Dagligvarer","-", "Dagligvarer",null,6000,BudsjettpoststatusEnum.TILDELT));

        // === Ikke tildelt ===
        knipper.add(new BudsjettpostDataKnippe("01-03","Sport, hobby og fritid","Jakt", "Jakt",null,999,BudsjettpoststatusEnum.FORESLAATT));
        knipper.add(new BudsjettpostDataKnippe("02-03","Sport, hobby og fritid","Jakt", "Jakt",null,999,BudsjettpoststatusEnum.FORESLAATT));
        knipper.add(new BudsjettpostDataKnippe("03-03","Sport, hobby og fritid","Jakt", "Jakt",null,999,BudsjettpoststatusEnum.FORESLAATT));

        return knipper;
    }

    /**
     * Lag tre månedsoversikter: Jan, Feb og Mars
     */
    private void produserMaanedsoversikter() {
        for (int i = 1; i<=3; i++) {
            Periode maanedsoversikt = maanedsoversiktService.opprettEntitet();
            LocalDate datoLocalDate = LocalDate.of(aarstall,i,1);
            maanedsoversikt.setDatoFraLocalDate(datoLocalDate);
            maanedsoversikt.setDatoTilLocalDate(Datokyklop.hent().finnSisteIMaaneden(datoLocalDate));
            maanedsoversikt.setBeskrivelseString("Automatisk opprettet testdata");
            maanedsoversiktService.lagre(maanedsoversikt);
        }
    }

    /**
     * Lag en aarsoversikt med summer som er lette å gjenkjenne, og legg informasjon om dem i beskrivelsen
     */
    private void produserAarsoversikt(){
        Periode aarsoversikt = aarsoversiktService.opprettEntitet();
        aarsoversikt.setDatoFraLocalDate(LocalDate.of(aarstall,1,1));
        aarsoversikt.setDatoTilLocalDate(LocalDate.of(aarstall,12,31));
        aarsoversikt.setBeskrivelseString("Automatisk opprettet testdata");
        aarsoversiktService.lagre(aarsoversikt);
    }

    private void hentServicer(){
        aarsoversiktService = Allvitekyklop.hent().getAarsoversiktService();
        aarsoversiktpostService = Allvitekyklop.hent().getAarsoversiktpostService();
        maanedsoversiktService = Allvitekyklop.hent().getMaanedsoversiktService();
        maanedsoversiktpostService = Allvitekyklop.hent().getMaanedsoversiktpostService();
        kategoriService = Allvitekyklop.hent().getKategoriService();
        normalpostService = Allvitekyklop.hent().getNormalpostService();
        budsjettpostService = Allvitekyklop.hent().getBudsjettpostService();
    }

    /**
     * Slett alle data fra årstallet for test, for eksempel 2050
     */
    public void slettTestData() {
        slettPoster();
        slettPeriodeoversiktposter();
        slettPerioder();
    }

    private void slettPoster() {
        normalpostService.slettAlle(normalpostService.finnPosterFraDatoTilDatoPostklasse(fraLocalDate, tilLocalDate, PostklasseEnum.NORMALPOST));
        normalpostService.slettAlle(normalpostService.finnPosterFraDatoTilDatoPostklasse(fraLocalDate, tilLocalDate, PostklasseEnum.BUDSJETTPOST));
    }

    private void slettPerioder() {
        maanedsoversiktService.slettAlle(maanedsoversiktService.finnEtterPeriodetypeOgFradato(PeriodetypeEnum.MAANEDSOVERSIKT,LocalDate.of(aarstall,1,1)));
        maanedsoversiktService.slettAlle(maanedsoversiktService.finnEtterPeriodetypeOgFradato(PeriodetypeEnum.MAANEDSOVERSIKT,LocalDate.of(aarstall,2,1)));
        maanedsoversiktService.slettAlle(maanedsoversiktService.finnEtterPeriodetypeOgFradato(PeriodetypeEnum.MAANEDSOVERSIKT,LocalDate.of(aarstall,3,1)));

        Periode aarsoversikt = aarsoversiktService.finnAarsoversiktFraAarString(aarstall.toString());
        aarsoversiktService.slett(aarsoversikt);

    }

    private void slettPeriodeoversiktposter() {
        Periode aarsoversikt = aarsoversiktService.finnAarsoversiktFraAarString(aarstall.toString());
        List<Periodepost> periodeposter = aarsoversiktpostService.finnEtterPeriode(aarsoversikt);
        aarsoversiktpostService.slettAlle(periodeposter);

        periodeposter = maanedsoversiktpostService.finnEtterPeriode(aarsoversikt);
        maanedsoversiktpostService.slettAlle(periodeposter);

    }


    private static class BudsjettpostDataKnippe {
        private final String deldatoString;
        private final String kategoriTittel;
        private final String kategoriUndertittel;
        private final String beskrivelse;
        private final Integer innPaaKontoInteger;
        private final Integer utFraKontoInteger;
        private final BudsjettpoststatusEnum budsjettpoststatusEnum;

        public BudsjettpostDataKnippe(String deldatoString, String kategoriTittel, String kategoriUndertittel, String beskrivelse, Integer innPaaKontoInteger, Integer utFraKontoInteger, BudsjettpoststatusEnum budsjettpoststatusEnum) {
            this.deldatoString = deldatoString;
            this.kategoriTittel = kategoriTittel;
            this.kategoriUndertittel = kategoriUndertittel;
            this.beskrivelse = beskrivelse;
            this.innPaaKontoInteger = innPaaKontoInteger;
            this.utFraKontoInteger = utFraKontoInteger;
            this.budsjettpoststatusEnum = budsjettpoststatusEnum;
        }
    }

    private static class NormalpostDataKnippe {
        private final String deldatoString;
        private final String kategoriTittel;
        private final String kategoriUndertittel;
        private final NormalposttypeEnum normalposttypeEnum;
        private final NormalpoststatusEnum normalpoststatusEnum;
        private final String beskrivelse;
        private final Integer innPaaKontoInteger;
        private final Integer utFraKontoInteger;


        public NormalpostDataKnippe(String deldatoString, String kategoriTittel, String kategoriUndertittel, NormalposttypeEnum normalposttypeEnum, NormalpoststatusEnum normalpoststatusEnum, String beskrivelse, Integer innPaaKontoInteger, Integer utFraKontoInteger) {
            this.deldatoString = deldatoString;
            this.kategoriTittel = kategoriTittel;
            this.kategoriUndertittel = kategoriUndertittel;
            this.normalposttypeEnum = normalposttypeEnum;
            this.normalpoststatusEnum = normalpoststatusEnum;
            this.beskrivelse = beskrivelse;
            this.innPaaKontoInteger = innPaaKontoInteger;
            this.utFraKontoInteger = utFraKontoInteger;
        }
    }

}
