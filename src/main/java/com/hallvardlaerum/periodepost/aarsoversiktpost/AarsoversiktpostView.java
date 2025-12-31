package com.hallvardlaerum.periodepost.aarsoversiktpost;

import com.hallvardlaerum.libs.eksportimport.CSVImportmester;
import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import com.hallvardlaerum.periodepost.PeriodepostTypeEnum;
import com.hallvardlaerum.periodepost.PeriodepostViewMal;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;


@Route("aarsoversiktpost")
@UIScope
//@Menu(order=25, title="Årsoversiktpost")
public class AarsoversiktpostView extends PeriodepostViewMal implements InitieringsEgnet {
    private AarsoversiktpostService aarsoversiktpostService;
    private AarsoversiktpostRedigeringsomraade aarsoversiktpostRedigeringsomraade;
    private AarsoversiktpostRedigeringsomraade aarsoversiktpostRedigeringsomraadeTilDialog;
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
            this.aarsoversiktpostRedigeringsomraade = Allvitekyklop.hent().getAarsoversiktpostRedigeringsomraade();
            this.aarsoversiktpostRedigeringsomraade.settView(this);
            this.aarsoversiktpostRedigeringsomraadeTilDialog = Allvitekyklop.hent().getAarsoversiktpostRedigeringsomraadeTilDialog();
            this.aarsoversiktpostRedigeringsomraadeTilDialog.settView(this);

            super.initierPeriodepostViewMal(
                    PeriodepostTypeEnum.AARSOVERSIKTPOST,
                    this,
                    PeriodetypeEnum.AARSOVERSIKT,
                    aarsoversiktpostRedigeringsomraade,
                    aarsoversiktpostService,
                    Allvitekyklop.hent().getAarsoversiktService());


            leggTilOgTilpassKnapper();
            hentVerktoeySubMeny().addItem("Importer CSV fra gamle Blåhvalen", e -> importCSVFraGamleBlaahvalen());

            erInitiert = true;
        }

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
