package com.hallvardlaerum.periode;


import com.hallvardlaerum.kategori.Kategori;
import com.hallvardlaerum.kategori.KategoriRetning;
import com.hallvardlaerum.kategori.KategoriService;
import com.hallvardlaerum.kategori.KategoriType;
import com.hallvardlaerum.libs.database.EntitetserviceMal;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.felter.HelTallMester;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeAktig;
import com.hallvardlaerum.periodepost.Periodepost;
import com.hallvardlaerum.periodepost.PeriodepostServiceMal;
import com.hallvardlaerum.periodepost.periodeoversiktpost.PeriodeoversiktpostService;
import com.hallvardlaerum.post.Post;
import com.hallvardlaerum.post.PostServiceMal;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import jakarta.persistence.Tuple;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PeriodeServiceMal extends EntitetserviceMal<Periode, PeriodeRepository> {
    private PeriodeRepository periodeRepository;
    private RedigeringsomraadeAktig<Periode> periodeRedigeringsomraade;
    private PeriodetypeEnum periodetypeEnum;
    private PostServiceMal postService;
    private PeriodepostServiceMal periodepostService;
    private KategoriService kategoriService;
    private PeriodeoversiktpostService periodeoversiktpostService;

    public PeriodeServiceMal() {
    }

    public void initier(RedigeringsomraadeAktig<Periode> periodeRedingeringsomraade,
                        PeriodetypeEnum periodetypeEnum,
                        PeriodepostServiceMal periodepostServiceMal,
                        PostServiceMal postService) {
        this.periodeRedigeringsomraade = periodeRedingeringsomraade;
        this.periodetypeEnum = periodetypeEnum;
        this.periodepostService = periodepostServiceMal;
        this.postService = postService;

        this.periodeRepository = Allvitekyklop.hent().getPeriodeRepository();
        this.kategoriService = Allvitekyklop.hent().getKategoriService();
        this.periodeoversiktpostService = Allvitekyklop.hent().getPeriodeoversiktpostService();

        super.initEntitetserviceMal(Periode.class, periodeRepository);
    }

    public List<Periode> finnAlleEgnedePerioder(PeriodetypeEnum periodetypeEnum) {
        return periodeRepository.findByPeriodetypeEnumOrderByDatoFraLocalDateDesc(periodetypeEnum);
    }

    public List<Periode> finnEtterPeriodetypeOgFradato(PeriodetypeEnum periodetypeEnum, LocalDate datoFraLocalDate) {
        return periodeRepository.findByPeriodetypeEnumAndDatoFraLocalDate(periodetypeEnum, datoFraLocalDate);
    }

    public List<Periode> finnEtterPeriodetypeOgFraTilDato(PeriodetypeEnum periodetypeEnum, LocalDate datoFra, LocalDate datoTil) {
        return periodeRepository.findByPeriodetypeEnumAndDatoFraLocalDateGreaterThanEqualAndDatoTilLocalDateLessThanEqual(periodetypeEnum, datoFra, datoTil);
    }

    @Override
    public Periode opprettEntitet() {
        Periode periode = leggTilUUID(new Periode());
        periode.setPeriodetypeEnum(this.periodetypeEnum);
        return periode;
    }


    /**
     * Denne brukes hver gang en budsjettpost tildeles, og må gå raskt
     */
    public void oppdaterSummerEtterTildelingAvBudsjettpost(Periodepost periodepost) {

        periodepostService.oppdaterOgLagreSummerForVanligePeriodeposter(periodepost);
        oppdaterOgLagreSummer(periodeRedigeringsomraade.getEntitet());
        periodeRedigeringsomraade.lesBean();
        periodeRedigeringsomraade.instansOppdaterEkstraRedigeringsfelter();
    }

    /**
     * Dette er hovedprosedyren for å oppdatere periodepostene og deres summer
     * Periodepostene bygges opp per kategori fra budsjettposter og normalposter.
     * Det skal også opprettes periodepost om det bare finnes budsjettposter som ikke er tildelt
     */
    public void oppdaterPeriodensPeriodeposterOgSummer(Periode periode) {

        oppdaterPeriodensPeriodeposterOgSummer_SlettDeHeltUtenPoster(periode);
        oppdaterPeriodensPeriodeposterOgSummer_LeggTilManglendeOgOppdaterSummerForDen(periode);
        oppdaterPeriodensPeriodeposterOgSummer_LeggTilOgOppdaterEllerFjernUkategorisert(periode);
        oppdaterPeriodensPeriodeposterOgSummer_OppdaterHovedperiodeposteneUnntattUkategorisert(periode);
        oppdaterPeriodensPeriodeposterOgSummer_oppdaterKostnadspakker(periode);
        oppdaterOgLagreSummer(periode);
        //lagre(periode);

        periodeRedigeringsomraade.hentView().oppdaterRedigeringsomraade();
        periodeRedigeringsomraade.hentView().oppdaterSoekeomraadeEtterRedigeringAvEntitet();
    }

    public void oppdaterPeriodensPeriodeposterOgSummer() {
        oppdaterPeriodensPeriodeposterOgSummer(periodeRedigeringsomraade.getEntitet());
    }

    private void oppdaterPeriodensPeriodeposterOgSummer_OppdaterHovedperiodeposteneUnntattUkategorisert(Periode periode) {
        List<Periodepost> periodepostArrayList = periodepostService.finnHovedperiodeposter(periode);
        for (Periodepost periodepost : periodepostArrayList) {
            if (periodepost.getKategori().getKategoriType()!= KategoriType.UKATEGORISERT) {
                periodepostService.oppdaterOgLagreSummerForVanligePeriodeposter(periodepost);
            }
        }
    }

    /**
     * Hvis det finnes ukategoriserte poster, skal det legges til en periodepost for det. Hvis ikke, skal den fjernes.
     * Periodepostene oppdateres her, fordi de har en avvikende måte å samle inn poster på.
     * @param periode aktuelle periode
     */
    private void oppdaterPeriodensPeriodeposterOgSummer_LeggTilOgOppdaterEllerFjernUkategorisert(Periode periode) {
        Kategori ukategorisertInnKategori = kategoriService.finnEllerOpprettKategoriUKATEGORISERT(KategoriRetning.INN);
        Integer sumUkategorisertInnInteger = postService.sumInnNormalposterEtterPeriodeOgUkategorisert(periode);
        if (sumUkategorisertInnInteger!=null && sumUkategorisertInnInteger>0) {
            Periodepost ukategorisertInnPeriodepost = periodepostService.finnEllerOpprettOgOppdaterPeriodepostUkategorisert(periode, ukategorisertInnKategori);
            periodepostService.lagre(ukategorisertInnPeriodepost);
        } else {
            periodepostService.slettUkategorisertForPeriode(periode, KategoriRetning.INN);
        }

        Kategori ukategorisertUtKategori = kategoriService.finnEllerOpprettKategoriUKATEGORISERT(KategoriRetning.UT);
        Integer sumUkategorisertUtInteger = postService.sumInnNormalposterEtterPeriodeOgUkategorisert(periode);
        if (sumUkategorisertUtInteger!=null && sumUkategorisertUtInteger>0) {
            Periodepost ukategorisertUtPeriodepost = periodepostService.finnEllerOpprettOgOppdaterPeriodepostUkategorisert(periode, ukategorisertUtKategori);
            periodepostService.lagre(ukategorisertUtPeriodepost);
        } else {
            periodepostService.slettUkategorisertForPeriode(periode, KategoriRetning.UT);
        }

    }


    public void oppdaterPeriodensPeriodeposterOgSummer_oppdaterKostnadspakker(Periode periode) {
        List<Periodepost> kostnadspakkeList = periodeoversiktpostService.hentKostnadspakkerForPerioden(periode);
        for (Periodepost kostnadspakke : kostnadspakkeList) {
            periodeoversiktpostService.oppdaterSumUtgifterFraTilknyttedePoster(kostnadspakke);
            periodeoversiktpostService.lagre(kostnadspakke);
        }
    }

    /**
     * Sletter periodeposter som ikke lenger er aktuelle
     *
     * @param periode perioden som skal oppdateres
     * kategoriNivaa Nivå 0 = hovedkategorier, (Nivå 1 = kategorier bruk i poster
     */
    public void oppdaterPeriodensPeriodeposterOgSummer_SlettDeHeltUtenPoster(Periode periode) {
        if (periode == null) {
            return;
        }
        PostServiceMal postService = Allvitekyklop.hent().getNormalpostService();
        ArrayList<Periodepost> periodeposterSomSkalSlettesArrayList = new ArrayList<>();

        List<Periodepost> periodepostList = periodepostService.hentRepository().finnEtterPeriodeOgKategorinivaa(periode.getUuid(), 0); // Vi bruker bare nivå 0 på standard periodeposter. Nivå 1 er for kostnadspakker
        for (Periodepost periodepost : periodepostList) {
            Kategori kategori =periodepost.getKategori();
            if (kategori==null) {
                periodepostService.slett(periodepost);
            } else {
                KategoriType kategoriType = kategori.getKategoriType();
                if (kategoriType == KategoriType.SKAL_IKKE_KATEGORISERES) {                     //KategoriType.UKATEGORISERT oppdateres senere.
                    Loggekyklop.bruk().loggINFO("Slettet periodepost med kategori SKAL_IKKE_KATEGORISERES, som ikke skulle vært opprettet. Vask periodepostene under Verktøy");
//                     periodepostService.slett(periodepost); //denne ga feilmelding     Unable to find com.hallvardlaerum.periodepost.Periodepost with id 17274df3-154f-4625-97ce-a3626b0fb835
                } else {
                    // Alle de vanlige inkl. overføring og sparing skal sjekkes
                    List<Post> postList = postService.finnEtterFradatoOgTilDatoOgHovedkategori(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate(), periodepost.getKategori());
                    if (postList.isEmpty()) {
                        periodeposterSomSkalSlettesArrayList.add(periodepost);
                    }
                }
            }
        }


        // Be om bekreftelse før de overflødige slettes.
        if (!periodeposterSomSkalSlettesArrayList.isEmpty()) {
            hentBekreftSlettePeriodeposterDialog(periodeposterSomSkalSlettesArrayList).open();
        }
    }



    public void oppdaterPeriodensPeriodeposterOgSummer_LeggTilManglendeOgOppdaterSummerForDen(Periode periode) {
        List<Kategori> kategoriListHarPoster = kategoriService.finnHovedKategorierDetFinnesPosterForFraDatoTilDato(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate());

        for (Kategori kategori : kategoriListHarPoster) {
            Periodepost periodepost  = periodepostService.finnStandardFraPeriodeOgKategori(periode, kategori);
            if (periodepost==null) {
                periodepost = periodepostService.opprettEntitet();
                periodepost.setPeriode(periode);
                periodepost.setKategori(kategori);
                periodepost.setPeriodepostTypeEnum(periodetypeEnum.getPeriodepostTypeEnum());
                periodepostService.oppdaterOgLagreSummerForVanligePeriodeposter(periodepost);
                periodepostService.lagre(periodepost);
            }
        }

    }

    @NotNull
    private ConfirmDialog hentBekreftSlettePeriodeposterDialog(List<Periodepost> tomperiodepostList) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Slette overflødige periodeposter?");
        dialog.setText("Det finnes " + tomperiodepostList.size() + " periodeposter som er tomme. " +
                "(" + periodepostService.presenterPeriodeposterMenIkkeVisPeriode(tomperiodepostList) + ")." +
                "Skal jeg fjerne dem?");
        dialog.setRejectable(true);
        dialog.setCancelText("Nei, avbryt");
        dialog.setConfirmText("Ok, slett dem");
        dialog.addConfirmListener(e -> slettPeriodeposter(tomperiodepostList));
        return dialog;
    }

    //Litt usikkert om denne hører til her, men beholdes siden den oppdaterer i redigeringsområdet for Perioden-
    private void slettPeriodeposter(List<Periodepost> periodepostList) {
        periodepostService.slettAlle(periodepostList);
        Periode periode = finnEtterUUID(periodeRedigeringsomraade.getEntitet().getUuid().toString());
        periodeRedigeringsomraade.setEntitet(periode);
        periodeRedigeringsomraade.lesBean();
        periodeRedigeringsomraade.instansOppdaterEkstraRedigeringsfelter();

    }


    public void oppdaterOgLagreSummer(Periode periode) {
        periode = finnEtterUUID(periode.getUuid().toString()); //for å oppdatere mtp., at enkelte periodeposter kan ha blitt slettet.
        periodeRedigeringsomraade.setEntitet(periode);

        oppdaterLagredeSummer_RegnskapMedOverfoeringer(periode);  // === Regnskap med overføringer ===
        oppdaterLagredeSummer_Regnskap(periode);  // == Regnskap uten overføringer ===
        oppdaterLagredeSummer_Budsjett(periode);  // === Budsjett ===
        oppdaterLagredeSummer_Ukategorisert(periode); // === Vi vet ikke om poster som ikke er kategorisert er overføringer eller vanlige poster. Må derfor settes for seg.

        lagre(periode);
        periodeRedigeringsomraade.hentView().oppdaterRedigeringsomraade();

    }

    private void oppdaterLagredeSummer_Ukategorisert(Periode periode) {
        periode.setSumUkategorisertInnInteger(postService.sumInnNormalposterEtterPeriodeOgUkategorisert(periode));
        periode.setSumUkategorisertUtInteger(postService.sumUtNormalposterEtterPeriodeOgUkategorisert(periode));
    }

    private void oppdaterLagredeSummer_Budsjett(Periode periode) {
        List<Tuple> tuples = postService.sumInnUtFradatoTilDatoTildelteBudsjettposter(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate());
        if (tuples.size() == 1) {
            Tuple tuple = tuples.getFirst();
            if (tuple != null && tuple.get(0, BigDecimal.class) != null) {
                Integer sumInn = HelTallMester.konverterBigdecimalTilInteger(tuple.get(0, BigDecimal.class));
                sumInn = sumInn == null ? 0 : sumInn;
                Integer sumUt = HelTallMester.konverterBigdecimalTilInteger(tuple.get(1, BigDecimal.class));
                sumUt = sumUt == null ? 0 : sumUt;

                periode.setSumBudsjettInntektInteger(sumInn);
                periode.setSumBudsjettUtgifterInteger(sumUt);
                periode.setSumBudsjettResultatInteger(sumInn - sumUt);
            } else {
                periode.setSumBudsjettInntektInteger(0);
                periode.setSumBudsjettUtgifterInteger(0);
                periode.setSumBudsjettResultatInteger(0);
            }
        }
    }

    private void oppdaterLagredeSummer_Regnskap(Periode periode) {
        Integer sumRegnskapInntektInteger = postService.sumInnFradatoTilDatoNormalposterUtenOverfoeringer(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate());
        if (sumRegnskapInntektInteger == null) {
            sumRegnskapInntektInteger = 0;
        }
        periode.setSumRegnskapInntektInteger(sumRegnskapInntektInteger);
        Integer sumRegnskapUtgifterInteger = postService.sumUtFradatoTilDatoNormalposterUtenOverfoeringer(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate());
        if (sumRegnskapUtgifterInteger == null) {
            sumRegnskapUtgifterInteger = 0;
        }
        periode.setSumRegnskapUtgifterInteger(sumRegnskapUtgifterInteger);
        periode.setSumRegnskapResultatInteger(sumRegnskapInntektInteger - sumRegnskapUtgifterInteger);
    }

    protected void oppdaterLagredeSummer_RegnskapMedOverfoeringer(Periode periode){
        Integer sumRegnskapInntekterMedOverfoeringerInteger = postService.sumInnFradatoTilDatoNormalposterMedOverfoeringer(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate());
        if (sumRegnskapInntekterMedOverfoeringerInteger == null) {
            sumRegnskapInntekterMedOverfoeringerInteger = 0;
        }
        periode.setSumRegnskapInntektMedOverfoeringerInteger(sumRegnskapInntekterMedOverfoeringerInteger);

        Integer sumRegnskapUtgifterMedOverfoeringerInteger = postService.sumUtFradatoTilDatoNormalposterMedOverfoeringer(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate());
        if (sumRegnskapUtgifterMedOverfoeringerInteger == null) {
            sumRegnskapUtgifterMedOverfoeringerInteger = 0;
        }
        periode.setSumRegnskapUtgifterMedOverfoeringerInteger(sumRegnskapUtgifterMedOverfoeringerInteger);
        periode.setSumRegnskapResultatMedOverfoeringerInteger(sumRegnskapInntekterMedOverfoeringerInteger - sumRegnskapUtgifterMedOverfoeringerInteger);
    }


    protected void slettAllePerioderMedPeriodeposter(PeriodetypeEnum periodetypeEnum) {
        List<Periode> perioder = hentRepository().findByPeriodetypeEnum(periodetypeEnum);
        for (Periode periode:perioder) {
            periodepostService.slettAlle(periode.getPeriodeposterList());
        }
        slettAlle(perioder);
        flush();
    }

    public void slettAlle(List<Periode> periodeList) {
        periodeRepository.deleteAll(periodeList);
    }



}
