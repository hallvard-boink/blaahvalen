package com.hallvardlaerum.periodepost.kostnadspakke;

import com.hallvardlaerum.libs.eksportimport.CSVImportmester;
import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import com.hallvardlaerum.periodepost.Periodepost;
import com.hallvardlaerum.periodepost.PeriodepostTypeEnum;
import com.hallvardlaerum.periodepost.PeriodepostViewMal;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;

/**
 * Dette vinduet kalles kostnadspakker for brukeren, av historiske årsaker.
 */
@Route("periodeoversiktpost")
@UIScope
public class KostnadspakkeView extends PeriodepostViewMal implements InitieringsEgnet {
    private boolean erInitiert;
    private KostnadspakkeService kostnadspakkeService;

    @Override
    public void init() {
        if (!erInitiert) {
            super.initierPeriodepostViewMal(
                PeriodepostTypeEnum.PERIODEOVERSIKTPOST,
                this,
                PeriodetypeEnum.AARSOVERSIKT,
                Allvitekyklop.hent().getKostnadspakkeRedigeringsomraade(),
                Allvitekyklop.hent().getKostnadspakkeService(),
                Allvitekyklop.hent().getAarsoversiktService()
            );
            kostnadspakkeService = Allvitekyklop.hent().getKostnadspakkeService();

            super.hentVindutittel().setText("Kostnadspakker");
            hentVerktoeySubMeny().addItem("Importer CSV fra gamle Blaahvalen", e->importerCSVFraGamleBlaahvalen());

            leggTilOppdaterSummerButton();

            erInitiert=true;
        }
    }


    @Override
    protected VerticalLayout opprettSoekeomraade(){
        super.opprettSoekeomraade_leggTilTittel();
        super.opprettSoekeomraade_leggTilVerktoyMeny();

        super.opprettSoekeomraade_leggTilVerktoyMeny_opprettEksporterTilCSVMenuItem();
        super.opprettSoekeomraade_leggTilVerktoyMeny_opprettImporterFraCSVMenuItem();
        opprettSoekeomraade_leggTilVerktoyMeny_opprettImporterCSVFraBlaahvalenMenuItem();
        opprettSoekeomraade_leggTilVerktoyMeny_opprettSlettAlleKostnadspakkerMenuItem();

        super.opprettSoekeomraade_leggTilVerktoyMeny_opprettSeparator();
        super.opprettSoekeomraade_leggTilVerktoyMeny_byttOrienteringAvSplitLayoutMenuItem();
        super.opprettSoekeomraade_leggTilSoekeGrid();
        return super.opprettSoeomraade_settSammenDetHele();
    }

    private void opprettSoekeomraade_leggTilVerktoyMeny_opprettImporterCSVFraBlaahvalenMenuItem() {
        hentVerktoeySubMeny().addItem("Importer CSV fra gamle Blaahvalen", e->importerCSVFraGamleBlaahvalen());
    }

    private void opprettSoekeomraade_leggTilVerktoyMeny_opprettSlettAlleKostnadspakkerMenuItem() {
        slettAlleMenuItem = super.verktoeySubMenu.addItem("Slett alle kostnadspakker");
        slettAlleMenuItem.addClickListener(e -> new ConfirmDialog(
                "Slette alle kostnadspakker?",
                "Vil du virkelig slette alle kostnadspakkene i databasen?",
                "Ja, sett i gang",
                ee -> {
                    kostnadspakkeService.slettAlleKostnadspakker();
                    oppdaterSoekeomraadeFinnAlleRader();
                    oppdaterRedigeringsomraade();
                },
                "Nei, er du GAL!",
                null).open());
    }

    private void leggTilOppdaterSummerButton() {
        Button oppdaterSummerButton = new Button("Oppdater summer");
        oppdaterSummerButton.addClickListener(e -> oppdaterSummer());
        hentKnapperadRedigeringsfelt().add(oppdaterSummerButton);
    }

    private void oppdaterSummer() {
        Periodepost kostnadspakke = Allvitekyklop.hent().getPeriodeoversiktpostView().hentEntitet();
        kostnadspakkeService.oppdaterOgLagreSummerForValgteVanligePeriodepost();
        //periodeoversiktpostService.oppdaterSumUtgifterFraTilknyttedePoster(kostnadspakke);
        Allvitekyklop.hent().getPeriodeoversiktpostView().oppdaterRedigeringsomraade();
        Allvitekyklop.hent().getPeriodeoversiktpostView().oppdaterSoekeomraadeFinnAlleRader();
    }


    private void importerCSVFraGamleBlaahvalen() {
        new CSVImportmester(new KostnadspakkeFraGamleBlaahvalenCSVImportassistent()).velgImportfilOgKjoerImport(
                Allvitekyklop.hent().getKostnadspakkeService()
        );
    }


    public KostnadspakkeView() {
        super();
        Allvitekyklop.hent().setPeriodeoversiktpostView(this);
        init();
    }

    @Override
    public boolean erInitiert() {
        return erInitiert;
    }
}
