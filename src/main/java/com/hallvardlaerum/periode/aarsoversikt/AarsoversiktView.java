package com.hallvardlaerum.periode.aarsoversikt;

import com.hallvardlaerum.libs.eksportimport.CSVImportmester;
import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.periode.PeriodeViewMal;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;

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
            hentVerktoeySubMeny().addItem("Importer årsoversikter fra gamle Blåhvalen", e -> importerAarsoversikterFraGamleBlaahvalenCSV());

            erInitiert = true;
        }
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
