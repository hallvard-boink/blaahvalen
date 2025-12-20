package com.hallvardlaerum.periode;

import com.hallvardlaerum.grunndata.kategori.Kategori;
import com.hallvardlaerum.libs.database.EntitetserviceMal;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeAktig;
import com.hallvardlaerum.periodepost.Periodepost;
import com.hallvardlaerum.periodepost.PeriodepostServiceMal;
import com.hallvardlaerum.post.Post;
import com.hallvardlaerum.post.PostServiceMal;
import com.hallvardlaerum.post.PostklasseEnum;
import com.hallvardlaerum.post.normalpost.NormalpostService;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PeriodeServiceMal extends EntitetserviceMal<Periode, PeriodeRepository> {
    private PeriodeRepository periodeRepository;
    private RedigeringsomraadeAktig<Periode> periodeRedigeringsomraade;
    private PeriodetypeEnum periodetypeEnum;
    private PostServiceMal postService;
    private PeriodepostServiceMal periodepostService;

    public PeriodeServiceMal() {

    }

    public void initier(RedigeringsomraadeAktig<Periode> periodeRedingeringsomraade,
                        PeriodetypeEnum periodetypeEnum,
                        PeriodepostServiceMal periodepostServiceMal,
                        PostServiceMal postService) {
        this.periodeRepository = Allvitekyklop.hent().getPeriodeRepository();
        this.periodeRedigeringsomraade = periodeRedingeringsomraade;
        this.periodetypeEnum = periodetypeEnum;
        this.periodepostService = periodepostServiceMal;
        this.postService = postService;
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

    public void oppdaterPeriodensPeriodeposterOgSummer(){
        Periode periode = periodeRedigeringsomraade.getEntitet();

        oppdaterPeriodensPeriodeposterOgSummer_SlettDeUtenPosterOgOppdaterDeMed(periode);
        oppdaterPeriodensPeriodeposterOgSummer_LeggTilManglende(periode);

        oppdaterSummer(periode);
        periodeRedigeringsomraade.lesBean();
        periodeRedigeringsomraade.instansOppdaterEkstraRedigeringsfelter();
    }

    public void oppdaterPeriodensPeriodeposterOgSummer_SlettDeUtenPosterOgOppdaterDeMed(Periode periode){
        if (periode==null) {
            return;
        }

        List<Periodepost> periodepostList = periode.getPeriodeposterList();
        NormalpostService normalpostService = Allvitekyklop.hent().getNormalpostService();
        for (Periodepost periodepost:periodepostList) {
            List<Post> postList = normalpostService.finnPosterFradatoTilDatoKategori(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate(), periodepost.getKategori());
            if (postList.isEmpty()) {
                periodepostService.slett(periodepost);
            } else {
                periodepostService.oppdaterSummer(periodepost);
            }
        }
    }

    public void oppdaterPeriodensPeriodeposterOgSummer_LeggTilManglende(Periode periode) {
        List<Kategori> kategoriListHarPoster = postService.hentRepository().hentKategorierDetFinnesPosterForFraDatoTilDato(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate());
        for (Kategori kategori:kategoriListHarPoster) {
            List<Periodepost> periodepostList = periodepostService.finnFraPeriodeOgKategori(periode,kategori);
            if (periodepostList.isEmpty()) {
                Periodepost periodepost = periodepostService.opprettEntitet();
                periodepost.setPeriode(periode);
                periodepost.setKategori(kategori);
                periodepost.setPeriodepostTypeEnum(periodetypeEnum.getPeriodepostTypeEnum());
                periodepostService.oppdaterSummer(periodepost);
                periodepostService.lagre(periodepost);
            }
        }

    }


    public void oppdaterPeriodeposter_gammel(Periode periode){

        ArrayList<Kategori> kategoriArrayList = postService.hentKategorierDetFinnesPosterForFraDatoTilDato(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate(), PostklasseEnum.NORMALPOST);
        if (kategoriArrayList.isEmpty()) {
            return;
        }

        ArrayList<Periodepost> periodeposterSomSkalSlettes = new ArrayList<>();
        List<Periodepost> eksisterendePeriodeposterList = periode.getPeriodeposterList();
        if (eksisterendePeriodeposterList!=null) {
            for (Periodepost periodepost:eksisterendePeriodeposterList) {
                if (!kategoriArrayList.contains(periodepost.getKategori())) {
                    periodeposterSomSkalSlettes.add(periodepost);
                }
            }
        }

        for (Kategori kategori:kategoriArrayList) {
            List<Periodepost> funnetPeriodeposterForKategorienList = periodepostService.finnFraPeriodeOgKategori(periode, kategori);

            Periodepost periodepost;
            if (funnetPeriodeposterForKategorienList.isEmpty()) { //Ikke funnet, opprett ny periodepost
                //opprett periodepost og oppdater
                periodepost = periodepostService.opprettEntitet();
                periodepost.setPeriode(periode);
                periodepost.setKategori(kategori);
                periodepost.setPeriodepostTypeEnum(periodetypeEnum.getPeriodepostTypeEnum());

            } else if (funnetPeriodeposterForKategorienList.size()==1) {  // Ble funnet
                periodepost = funnetPeriodeposterForKategorienList.getFirst();

            } else  {
                Loggekyklop.hent().loggADVARSEL("Fant mer enn en forekomst av periodepost for perioden " +
                        periode.hentBeskrivendeNavn() + " med kategori " + kategori.hentBeskrivendeNavn() +
                        ". Fortsetter likevel, men dette bør feilsøkes");
                periodepost = funnetPeriodeposterForKategorienList.getFirst();
            }
            periodepostService.oppdaterSummer(periodepost);
            periodepostService.lagre(periodepost);
        }

        if (!periodeposterSomSkalSlettes.isEmpty()) {
            ConfirmDialog dialog = getConfirmDialog(periodeposterSomSkalSlettes);
            dialog.open();
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

        periodeRedigeringsomraade.lesBean();
    }



}
