package com.hallvardlaerum.periodepost.aarsoversiktpost;

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


@Route("aarsoversiktpost")
@UIScope
//@Menu(order=25, title="Årsoversiktpost")
public class AarsoversiktpostView extends PeriodepostViewMal implements InitieringsEgnet {
    private AarsoversiktpostService aarsoversiktpostService;
    private Button oppdaterSummerButton;
    private boolean erInitiert = false;

    public AarsoversiktpostView() {
        super();
        Allvitekyklop.hent().setAarsoversiktpostView(this);
        init();
    }


    @Override
    public boolean erInitiert() {
        return erInitiert;
    }

    @Override
    public void init(){
        if (!erInitiert) {
            this.aarsoversiktpostService = Allvitekyklop.hent().getAarsoversiktpostService();
            AarsoversiktpostRedigeringsomraade aarsoversiktpostRedigeringsomraade = Allvitekyklop.hent().getAarsoversiktpostRedigeringsomraade();
            aarsoversiktpostRedigeringsomraade.settView(this);
            AarsoversiktpostRedigeringsomraade aarsoversiktpostRedigeringsomraadeTilDialog = Allvitekyklop.hent().getAarsoversiktpostRedigeringsomraadeTilDialog();
            aarsoversiktpostRedigeringsomraadeTilDialog.settView(this);

            super.initierPeriodepostViewMal(
                    PeriodepostTypeEnum.AARSOVERSIKTPOST,
                    this,
                    PeriodetypeEnum.AARSOVERSIKT,
                    aarsoversiktpostRedigeringsomraade,
                    aarsoversiktpostService,
                    Allvitekyklop.hent().getAarsoversiktService());


            leggTilOgTilpassKnapper();
            //Verktøymenyen er håndtert ved å overkjøre super.opprettSoekeomraade()

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
        opprettSoekeomraade_leggTilVerktoyMeny_opprettSlettAlleAarsoversiktposterMenuItem();

        super.opprettSoekeomraade_leggTilVerktoyMeny_opprettSeparator();
        super.opprettSoekeomraade_leggTilVerktoyMeny_byttOrienteringAvSplitLayoutMenuItem();
        super.opprettSoekeomraade_leggTilSoekeGrid();
        return super.opprettSoeomraade_settSammenDetHele();
    }

    private void opprettSoekeomraade_leggTilVerktoyMeny_opprettImporterCSVFraBlaahvalenMenuItem() {
        hentVerktoeySubMeny().addItem("Importer CSV fra gamle Blåhvalen", e -> importCSVFraGamleBlaahvalen());
    }

    private void opprettSoekeomraade_leggTilVerktoyMeny_opprettSlettAlleAarsoversiktposterMenuItem() {
        slettAlleMenuItem = super.verktoeySubMenu.addItem("Slett alle årsoversiktposter");
        slettAlleMenuItem.addClickListener(e -> new ConfirmDialog(
                "Slette alle årsoversiktposter?",
                "Vil du virkelig slette alle årsoversiktpostene i databasen?",
                "Ja, sett i gang",
                ee -> {
                    aarsoversiktpostService.slettAlleAarsoversiktposter();
                    oppdaterSoekeomraadeFinnAlleRader();
                    oppdaterRedigeringsomraade();
                },
                "Nei, er du GAL!",
                null).open());
    }

    private void importCSVFraGamleBlaahvalen() {
        new CSVImportmester(new AarsoversiktpostFraGamleBlaahvalenCSVImportAssistent()).velgImportfilOgKjoerImport(aarsoversiktpostService);
    }

    private void leggTilOgTilpassKnapper() {
        oppdaterSummerButton = new Button("Oppdater summer");
        oppdaterSummerButton.setEnabled(false);
        oppdaterSummerButton.addClickListener(e ->  aarsoversiktpostService.oppdaterOgLagreSummerForValgteVanligePeriodepost());
        hentKnapperadRedigeringsfelt().addToEnd(oppdaterSummerButton);
    }

    public void instansAktiverKnapperadRedigeringsfelt(Boolean aktiverBoolean) {
        super.instansAktiverKnapperadRedigeringsfelt(aktiverBoolean);
        oppdaterSummerButton.setEnabled(aktiverBoolean);
    }
}
