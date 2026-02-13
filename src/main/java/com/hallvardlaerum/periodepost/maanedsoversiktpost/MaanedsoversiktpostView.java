package com.hallvardlaerum.periodepost.maanedsoversiktpost;

import com.hallvardlaerum.libs.eksportimport.CSVImportmester;
import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import com.hallvardlaerum.periodepost.PeriodepostTypeEnum;
import com.hallvardlaerum.periodepost.PeriodepostViewMal;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;

@Route("maanedsoversiktpost")
@UIScope
//@Menu(order=20, title="Månedsoversiktpost")
public class MaanedsoversiktpostView extends PeriodepostViewMal implements InitieringsEgnet {
    private Button oppdaterSummerButton;
    private MaanedsoversiktpostService maanedsoversiktpostService;
    private boolean erInitiert = false;

    public MaanedsoversiktpostView() {
        super();
        Allvitekyklop.hent().setMaanedsoversiktpostView(this);
        init();
    }

    @Override
    public boolean erInitiert() {
        return erInitiert;
    }

    @Override
    public void init(){
        if (!erInitiert) {
            maanedsoversiktpostService = Allvitekyklop.hent().getMaanedsoversiktpostService();
            super.initierPeriodepostViewMal(PeriodepostTypeEnum.MAANEDSOVERSIKTPOST,
                    this,
                    PeriodetypeEnum.MAANEDSOVERSIKT,
                    Allvitekyklop.hent().getMaanedsoversiktpostRedigeringsomraade(),
                    maanedsoversiktpostService,
                    Allvitekyklop.hent().getMaanedsoversiktService()
                    );

            leggTilOgTilpassKnapper();
            // tilpasning er håndtert ved å overkjøre opprettSoekeomraade()

            erInitiert = true;
        }
    }

    @Override
    protected VerticalLayout opprettSoekeomraade(){
        super.opprettSoekeomraade_leggTilTittel();
        super.opprettSoekeomraade_leggTilVerktoyMeny();

        super.opprettSoekeomraade_leggTilVerktoyMeny_opprettEksporterTilCSVMenuItem();
        super.opprettSoekeomraade_leggTilVerktoyMeny_opprettImporterFraCSVMenuItem();
        opprettSoekeomraade_leggTilVerktoyMeny_opprettImporterCSVFraBlaahvalenMenuItem();
        opprettSoekeomraade_leggTilVerktoyMeny_opprettSlettAlleMaanedssoversiktposterMenuItem();

        super.opprettSoekeomraade_leggTilVerktoyMeny_opprettSeparator();
        super.opprettSoekeomraade_leggTilVerktoyMeny_byttOrienteringAvSplitLayoutMenuItem();
        super.opprettSoekeomraade_leggTilSoekeGrid();
        return super.opprettSoeomraade_settSammenDetHele();
    }

    private void opprettSoekeomraade_leggTilVerktoyMeny_opprettImporterCSVFraBlaahvalenMenuItem() {
        hentVerktoeySubMeny().addItem("Importer CSV fra gamle Blåhvalen", e -> importerCSVFraGamleBlaahvalen());
    }

    private void opprettSoekeomraade_leggTilVerktoyMeny_opprettSlettAlleMaanedssoversiktposterMenuItem() {
        slettAlleMenuItem = super.verktoeySubMenu.addItem("Slette alle månedsoversiktposter");
        slettAlleMenuItem.addClickListener(e -> new ConfirmDialog(
                "Slette alle månedsoversiktposter?",
                "Vil du virkelig slette alle månedsoversiktpostene i databasen?",
                "Ja, sett i gang",
                ee -> {
                    maanedsoversiktpostService.slettAlleMaanedsoversiktposter();
                    oppdaterSoekeomraadeFinnAlleRader();
                    oppdaterRedigeringsomraade();
                },
                "Nei, er du GAL!",
                ee -> {}).open());
    }


    private void importerCSVFraGamleBlaahvalen() {
        new CSVImportmester(new MaanedsoversiktpostFraGamleBlaahvalenCSVImportassistent()).velgImportfilOgKjoerImport(maanedsoversiktpostService);
    }

    private void leggTilOgTilpassKnapper() {
        oppdaterSummerButton = new Button("Oppdater summer");
        oppdaterSummerButton.addClickListener(e -> maanedsoversiktpostService.oppdaterOgLagreSummerForValgteVanligePeriodepost());
        oppdaterSummerButton.setEnabled(false);
        super.hentKnapperadRedigeringsfelt().addToEnd(oppdaterSummerButton);
    }

    public void instansAktiverKnapperadRedigeringsfelt(Boolean aktiverBoolean) {
        super.instansAktiverKnapperadRedigeringsfelt(aktiverBoolean);
        oppdaterSummerButton.setEnabled(aktiverBoolean);
    }

}
