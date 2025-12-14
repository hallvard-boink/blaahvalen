package com.hallvardlaerum.periode.aarsoversikt;

import com.hallvardlaerum.libs.eksportimport.CSVImportmester;
import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.periode.Periode;
import com.hallvardlaerum.periode.PeriodeRedigeringsomraadeMal;
import com.hallvardlaerum.periode.PeriodeViewMal;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.hallvardlaerum.verktoy.PeriodeRapportMester;
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
    private AarsoversiktRedigeringsomraade aarsoversiktRedigeringsomraade;

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
            this.aarsoversiktRedigeringsomraade = Allvitekyklop.hent().getAarsoversiktRedigeringsomraade();
            this.aarsoversiktRedigeringsomraade.settView(this);

            Allvitekyklop.hent().getAarsoversiktpostRedigeringsomraadeTilDialog().settView(this);

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
        oppdaterSummerButton.addClickListener(e -> aarsoversiktService.oppdaterPeriodensPeriodeposterOgSummer());
        oppdaterSummerButton.setEnabled(false);
        hentKnapperadRedigeringsfelt().addToEnd(oppdaterSummerButton);

        lagrePDFButton = new Button("Lagre PDF");
        lagrePDFButton.addClickListener(e -> {
            Periode periode = (Periode)hentRedigeringsomraadeAktig().getEntitet();
            PeriodeRedigeringsomraadeMal redigeringsomraade = (PeriodeRedigeringsomraadeMal)hentRedigeringsomraadeAktig();
            new PeriodeRapportMester().lagrePeriodeSomPDF(periode, redigeringsomraade.hentPeriodepostListSortert(periode));
        });
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
