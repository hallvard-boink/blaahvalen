package com.hallvardlaerum.periode;

import com.hallvardlaerum.grunndata.kategori.Kategori;
import com.hallvardlaerum.grunndata.kategori.KategoriService;
import com.hallvardlaerum.libs.database.EntitetserviceMal;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.felter.DesimalMester;
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

    public List<Periode> finnAlleEgndePerioder(PeriodetypeEnum periodetypeEnum){
        if (periodeRepository==null) {
            periodeRepository = Allvitekyklop.hent().getPeriodeRepository();
        }
        return periodeRepository.findByPeriodetypeEnumOrderByDatoFraLocalDateDesc(periodetypeEnum);
    }

    @Override
    public Periode opprettEntitet() {
        Periode periode = leggTilUUID(new Periode());
        periode.setPeriodetypeEnum(this.periodetypeEnum);
        return periode;
    }

    @Override
    @Deprecated
    public RedigeringsomraadeAktig<Periode> hentRedigeringsomraadeAktig() {
        return periodeRedigeringsomraade;
    }


    public void oppdaterOverordnetPeriodensPeriodeposterOgSummer() {
        Periode periode = periodeRedigeringsomraade.getEntitet();
        oppdaterPeriodensPeriodeposterOgSummer_SlettDeUtenPosterOgOppdaterDeMed(periode, 0);
        oppdaterPeriodensPeriodeposterOgSummer_LeggTilManglende(periode,0);

        oppdaterSummer(periode);
        periodeRedigeringsomraade.lesBean();
        periodeRedigeringsomraade.instansOppdaterEkstraRedigeringsfelter();

    }

    public void oppdaterDetaljertPeriodensPeriodeposterOgSummer(){
        Periode periode = periodeRedigeringsomraade.getEntitet();

        oppdaterPeriodensPeriodeposterOgSummer_SlettDeUtenPosterOgOppdaterDeMed(periode, 1);
        oppdaterPeriodensPeriodeposterOgSummer_LeggTilManglende(periode,1 );
        oppdaterPeriodensPeriodeposterOgSummer_oppdaterKostnadspakker(periode);

        oppdaterSummer(periode);
        periodeRedigeringsomraade.lesBean();
        periodeRedigeringsomraade.instansOppdaterEkstraRedigeringsfelter();
    }


    public void oppdaterPeriodensPeriodeposterOgSummer_oppdaterKostnadspakker(Periode periode) {
        List<Periodepost> kostnadspakkeList = periodepostService.finnEtterPeriode(periode);
        for (Periodepost kostnadspakke:kostnadspakkeList) {
            periodeoversiktpostService.oppdaterSumUtgifterFraTilknyttedePoster(kostnadspakke);
            periodeoversiktpostService.lagre(kostnadspakke);

        }

    }

    public void oppdaterPeriodensPeriodeposterOgSummer_SlettDeUtenPosterOgOppdaterDeMed(Periode periode, Integer kategoriNivaa){
        if (periode==null) {
            return;
        }

        List<Periodepost> periodepostList = periodepostService.hentRepository().finnEtterPeriodeOgKategorinivaa(periode.getUuid(), kategoriNivaa);

        PostServiceMal postService = Allvitekyklop.hent().getNormalpostService();

        for (Periodepost periodepost:periodepostList) {
            List<Post> postList = postService.finnPosterFradatoTilDatoOgKategoriOgNivaa(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate(), periodepost.getKategori(), kategoriNivaa);
            if (postList.isEmpty()) {
                periodepostService.slett(periodepost);
            } else {
                periodepostService.oppdaterOgLagreSummerForVanligePeriodeposter(periodepost);
            }
        }
    }

    public void oppdaterPeriodensPeriodeposterOgSummer_LeggTilManglende(Periode periode, Integer kategoriNivaa) {
        List<Kategori> kategoriListHarPoster;
        if (kategoriNivaa == 0 ) {
            List<Tuple> tuples= postService.hentRepository().hentHovedKategorierDetFinnesPosterForFraDatoTilDato(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate());
            kategoriListHarPoster = new ArrayList<>();
            for (Tuple tuple:tuples) {
                String uuidString = tuple.get(0, UUID.class).toString();
                Kategori kategori = kategoriService.finnEtterUUID(uuidString);
                if (kategori==null) {
                    Loggekyklop.bruk().loggINFO("Fant ikke kategorien " + tuple.get(1,String.class));
                } else {
                    kategoriListHarPoster.add(kategori);
                }
            }

        } else {
            kategoriListHarPoster = postService.hentRepository().hentKategorierDetFinnesPosterForFraDatoTilDato(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate());
        }

        for (Kategori kategori:kategoriListHarPoster) {
            List<Periodepost> periodepostList = periodepostService.finnFraPeriodeOgKategori(periode,kategori);
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
    private ConfirmDialog getConfirmDialog(List<Periodepost> tomperiodepostList) {
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



    private void slettPeriodeposter(List<Periodepost> periodepostList) {
        periodepostService.slettAlle(periodepostList);
        periodeRedigeringsomraade.lesBean();
    }

    public void oppdaterSummer() {
        Periode periode = periodeRedigeringsomraade.getEntitet();
        oppdaterSummer(periode);
    }

    public void oppdaterSummer(Periode periode) {

        // === Regnskap med overføringer ===
        Integer sumRegnskapInntekterMedOverfoeringerInteger = periodeRepository.sumInnFradatoTilDatoNormalposterMedOverfoeringer(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate());
        if (sumRegnskapInntekterMedOverfoeringerInteger==null) {
            sumRegnskapInntekterMedOverfoeringerInteger=0;
        }
        periode.setSumRegnskapInntektMedOverfoeringerInteger(sumRegnskapInntekterMedOverfoeringerInteger);

        Integer sumRegnskapUtgifterMedOverfoeringerInteger = periodeRepository.sumUtFradatoTilDatoNormalposterMedOverfoeringer(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate());
        if (sumRegnskapUtgifterMedOverfoeringerInteger==null) {
            sumRegnskapUtgifterMedOverfoeringerInteger=0;
        }
        periode.setSumRegnskapUtgifterMedOverfoeringerInteger(sumRegnskapUtgifterMedOverfoeringerInteger);

        periode.setSumRegnskapResultatMedOverfoeringerInteger(sumRegnskapInntekterMedOverfoeringerInteger - sumRegnskapUtgifterMedOverfoeringerInteger);

        // == Regnskap uten overføringer ===
        Integer sumRegnskapInntektInteger = periodeRepository.sumUtFradatoTilDatoNormalposterUtenOverfoeringer(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate());
        if (sumRegnskapInntektInteger == null) {
            sumRegnskapInntektInteger = 0;
        }
        periode.setSumRegnskapInntektInteger(sumRegnskapInntektInteger);
        Integer sumRegnskapUtgifterInteger = periodeRepository.sumInnFradatoTilDatoNormalposterUtenOverfoeringer(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate());
        if (sumRegnskapUtgifterInteger == null) {
            sumRegnskapUtgifterInteger = 0;
        }
        periode.setSumRegnskapUtgifterInteger(sumRegnskapUtgifterInteger);
        periode.setSumRegnskapResultatInteger(sumRegnskapInntektInteger - sumRegnskapUtgifterInteger);

        // === Budsjett ===
        List<Tuple> tuples = periodeRepository.sumInnUtFradatoTilDatoTildelteBudsjettposter(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate());
        if (tuples.size()==1) {
            Tuple tuple = tuples.getFirst();
            if (tuple!=null && tuple.get(0, BigDecimal.class)!=null) {
                Integer sumInn = DesimalMester.konverterBigdecimalTilInteger(tuple.get(0, BigDecimal.class));
                sumInn = sumInn==null ? 0 : sumInn;
                Integer sumUt = DesimalMester.konverterBigdecimalTilInteger(tuple.get(1, BigDecimal.class));
                sumUt = sumUt==null ? 0 : sumUt;

                periode.setSumBudsjettInntektInteger(sumInn);
                periode.setSumBudsjettUtgifterInteger(sumUt);
                periode.setSumBudsjettResultatInteger(sumInn-sumUt);
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
