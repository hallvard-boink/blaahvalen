package com.hallvardlaerum.periode;

import com.hallvardlaerum.kategori.Kategori;
import com.hallvardlaerum.kategori.KategoriService;
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
import java.util.UUID;

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


    public void oppdaterOverordnetPeriodensPeriodeposterOgSummer() {
        Periode periode = periodeRedigeringsomraade.getEntitet();
        oppdaterPeriodensPeriodeposterOgSummer_SlettDeUtenPosterOgOppdaterDeMed(periode, 0);
        oppdaterPeriodensPeriodeposterOgSummer_LeggTilManglende(periode, 0);

        oppdaterLagredeSummer(periode);
        periodeRedigeringsomraade.lesBean();
        periodeRedigeringsomraade.instansOppdaterEkstraRedigeringsfelter();
    }

    public void oppdaterDetaljertPeriodensPeriodeposterOgSummer() {
        Periode periode = periodeRedigeringsomraade.getEntitet();

        oppdaterPeriodensPeriodeposterOgSummer_SlettDeUtenPosterOgOppdaterDeMed(periode, 1);
        //periode = periodeRedigeringsomraade.getEntitet(); //hvis det å slette periodepost setter den til null
        oppdaterPeriodensPeriodeposterOgSummer_LeggTilManglende(periode, 1);
        oppdaterPeriodensPeriodeposterOgSummer_OppdaterHovedperiodepostene(periode);
        oppdaterPeriodensPeriodeposterOgSummer_oppdaterKostnadspakker(periode);

        oppdaterLagredeSummer(periode);
        lagre(periode);
        periodeRedigeringsomraade.hentView().oppdaterRedigeringsomraade();
        periodeRedigeringsomraade.hentView().oppdaterSoekeomraadeEtterRedigeringAvEntitet();
        //periodeRedigeringsomraade.lesBean();
        //periodeRedigeringsomraade.instansOppdaterEkstraRedigeringsfelter();
    }


    private void oppdaterPeriodensPeriodeposterOgSummer_OppdaterHovedperiodepostene(Periode periode) {
        List<Periodepost> periodepostArrayList = periodepostService.finnHovedperiodeposter(periode);
        for (Periodepost periodepost : periodepostArrayList) {
            periodepostService.oppdaterOgLagreSummerForVanligePeriodeposter(periodepost);
        }
    }


    public void oppdaterPeriodensPeriodeposterOgSummer_oppdaterKostnadspakker(Periode periode) {
        List<Periodepost> kostnadspakkeList = periodepostService.finnEtterPeriode(periode);
        for (Periodepost kostnadspakke : kostnadspakkeList) {
            periodeoversiktpostService.oppdaterSumUtgifterFraTilknyttedePoster(kostnadspakke);
            periodeoversiktpostService.lagre(kostnadspakke);
        }
    }

    public void oppdaterPeriodensPeriodeposterOgSummer_SlettDeUtenPosterOgOppdaterDeMed(Periode periode, Integer kategoriNivaa) {
        if (periode == null) {
            return;
        }

        ArrayList<Periodepost> periodeposterSomSkalSlettesArrayList = new ArrayList<>();
        List<Periodepost> periodepostList = periodepostService.hentRepository().finnEtterPeriodeOgKategorinivaa(periode.getUuid(), kategoriNivaa);

        PostServiceMal postService = Allvitekyklop.hent().getNormalpostService();

        for (Periodepost periodepost : periodepostList) {
            List<Post> postList = postService.finnPosterFradatoTilDatoOgKategoriOgNivaa(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate(), periodepost.getKategori(), kategoriNivaa);
            if (postList.isEmpty()) {
                periodeposterSomSkalSlettesArrayList.add(periodepost);
            } else {
                periodepostService.oppdaterOgLagreSummerForVanligePeriodeposter(periodepost);
            }
        }

        if (!periodeposterSomSkalSlettesArrayList.isEmpty()) {
            hentBekreftSlettePeriodeposterDialog(periodeposterSomSkalSlettesArrayList).open();
        }
    }

    public void oppdaterPeriodensPeriodeposterOgSummer_LeggTilManglende(Periode periode, Integer kategoriNivaa) {
        List<Kategori> kategoriListHarPoster;
        if (kategoriNivaa == 0) {
            List<Tuple> tuples = kategoriService.hentHovedKategorierDetFinnesPosterForFraDatoTilDato(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate());
            kategoriListHarPoster = new ArrayList<>();
            for (Tuple tuple : tuples) {
                String uuidString = tuple.get(0, UUID.class).toString();
                Kategori kategori = kategoriService.finnEtterUUID(uuidString);
                if (kategori == null) {
                    Loggekyklop.bruk().loggINFO("Fant ikke kategorien " + tuple.get(1, String.class));
                } else {
                    kategoriListHarPoster.add(kategori);
                }
            }

        } else {
            kategoriListHarPoster = kategoriService.hentKategorierDetFinnesPosterForFraDatoTilDato(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate());
        }

        for (Kategori kategori : kategoriListHarPoster) {
            List<Periodepost> periodepostList = periodepostService.finnFraPeriodeOgKategori(periode, kategori);
            if (periodepostList.isEmpty()) {
                Periodepost periodepost = periodepostService.opprettEntitet();
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
        periodeRedigeringsomraade.lesBean();
    }


    public void oppdaterLagredeSummer(Periode periode) {

        // === Regnskap med overføringer ===
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

        // == Regnskap uten overføringer ===
        Integer sumRegnskapInntektInteger = postService.sumUtFradatoTilDatoNormalposterUtenOverfoeringer(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate());
        if (sumRegnskapInntektInteger == null) {
            sumRegnskapInntektInteger = 0;
        }
        periode.setSumRegnskapInntektInteger(sumRegnskapInntektInteger);
        Integer sumRegnskapUtgifterInteger = postService.sumInnFradatoTilDatoNormalposterUtenOverfoeringer(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate());
        if (sumRegnskapUtgifterInteger == null) {
            sumRegnskapUtgifterInteger = 0;
        }
        periode.setSumRegnskapUtgifterInteger(sumRegnskapUtgifterInteger);
        periode.setSumRegnskapResultatInteger(sumRegnskapInntektInteger - sumRegnskapUtgifterInteger);

        // === Budsjett ===
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


        lagre(periode);
        periodeRedigeringsomraade.lesBean();
    }

}
