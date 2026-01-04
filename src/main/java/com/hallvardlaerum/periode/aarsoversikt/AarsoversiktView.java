package com.hallvardlaerum.periode.aarsoversikt;

import com.hallvardlaerum.libs.eksportimport.CSVImportmester;
import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.periode.PeriodeViewMal;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;

/**
 * <h1>Årsoversikt</h1>
 * Dette vinduet brukes til å redigere Årsoversikter. En årsoversikt er oppsummerte inntekter, utgifter og overføringer
 * i budsjett og regnskap. Den skal vise om vi går med over- eller underskudd, og i hvilken grad opppsparte midler har
 * blitt økt eller redusert.
 * <br/><br/>
 * Årsoversikten skal også brukes til å sette opp faste utgifter for året (altså budsjettposter), og planlegge eventuelle
 * store utgifter. Hvis vi dermed bruker for mye en måned, kan vi legge inn tilsvarende lavere budsjett i måneden(e)
 * etter, slik at resultatet blir bra til slutt.
 * <br/><br/>
 */
@Route("aarsoversikt")
@UIScope
//@Menu(order=30, title="Årsoversikt")
public class AarsoversiktView extends PeriodeViewMal implements InitieringsEgnet {
    private AarsoversiktService aarsoversiktService;
    private Button oppdaterSummerButton;
    private Button lagrePDFButton;
    private boolean erInitiert = false;

    public AarsoversiktView() {
        super();
        Allvitekyklop.hent().setAarsoversiktView(this);
        init();
    }


    @Override
    public boolean erInitiert() {
        return erInitiert;
    }

    @Override
    public void init(){
        if (!erInitiert) {
            this.aarsoversiktService = Allvitekyklop.hent().getAarsoversiktService();
            AarsoversiktRedigeringsomraade aarsoversiktRedigeringsomraade = Allvitekyklop.hent().getAarsoversiktRedigeringsomraade();
            aarsoversiktRedigeringsomraade.settView(this);
            Allvitekyklop.hent().getAarsoversiktpostRedigeringsomraadeTilDialog().settView(this);

            this.aarsoversiktService = Allvitekyklop.hent().getAarsoversiktService();
            super.initPeriodeViewMal(PeriodetypeEnum.AARSOVERSIKT,
                    this,
                    aarsoversiktService,
                    aarsoversiktRedigeringsomraade);

            leggTilOgTilpassKnapper();
            //verktøymenyen håndteres med overkjøring av opprettSoekeomraade()

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
        opprettSoekeomraade_leggTilVerktoyMeny_opprettSlettAlleMaanedsoversikterMenuItem();

        super.opprettSoekeomraade_leggTilVerktoyMeny_opprettSeparator();
        super.opprettSoekeomraade_leggTilVerktoyMeny_byttOrienteringAvSplitLayoutMenuItem();
        super.opprettSoekeomraade_leggTilSoekeGrid();
        return super.opprettSoeomraade_settSammenDetHele();
    }

    private void opprettSoekeomraade_leggTilVerktoyMeny_opprettSlettAlleMaanedsoversikterMenuItem() {
        slettAlleMenuItem = super.verktoeySubMenu.addItem("Slett alle månedsoversikter");
        slettAlleMenuItem.addClickListener(e -> new ConfirmDialog(
                "Slette alle årsoversikter?",
                "Vil du virkelig slette årsoversikter med årssoversiktposter?",
                "Ja, sett i gang",
                ee -> {
                    aarsoversiktService.slettAlleAarsoversikter();
                    oppdaterSoekeomraadeFinnAlleRader();
                    oppdaterRedigeringsomraade();
                },
                "Nei, er du GAL!",
                null).open());
    }

    private void opprettSoekeomraade_leggTilVerktoyMeny_opprettImporterCSVFraBlaahvalenMenuItem(){
        hentVerktoeySubMeny().addItem("Importer årsoversikter fra gamle Blåhvalen", e -> importerAarsoversikterFraGamleBlaahvalenCSV());
    }

    private void importerAarsoversikterFraGamleBlaahvalenCSV() {
        AarsoversiktFraGamleBlaahvalenCSVImportassistent aarsoversiktFraGamleBlaahvalenCSVImportassistent = new AarsoversiktFraGamleBlaahvalenCSVImportassistent();
        CSVImportmester csvImportmester = new CSVImportmester(aarsoversiktFraGamleBlaahvalenCSVImportassistent);
        csvImportmester.velgImportfilOgKjoerImport(aarsoversiktService);
    }

    private void leggTilOgTilpassKnapper() {
        oppdaterSummerButton = new Button("Oppdater årsoversikt");
        oppdaterSummerButton.addClickListener(e -> aarsoversiktService.oppdaterDetaljertPeriodensPeriodeposterOgSummer());
        oppdaterSummerButton.setEnabled(false);
        hentKnapperadRedigeringsfelt().addToEnd(oppdaterSummerButton);

        lagrePDFButton = new Button("Lagre PDF");
        lagrePDFButton.addClickListener(e -> super.skrivUtPerioderapport());
        lagrePDFButton.setEnabled(false);
        hentKnapperadRedigeringsfelt().addToEnd(lagrePDFButton);
    }

    @Override
    public void instansAktiverKnapperadRedigeringsfelt(Boolean aktiverBoolean) {
        super.instansAktiverKnapperadRedigeringsfelt(aktiverBoolean);
        oppdaterSummerButton.setEnabled(aktiverBoolean);
        lagrePDFButton.setEnabled(aktiverBoolean);
    }
}
