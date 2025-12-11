package com.hallvardlaerum.periodepost.aarsoversiktpost;

import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import com.hallvardlaerum.periodepost.PeriodepostTypeEnum;
import com.hallvardlaerum.periodepost.PeriodepostViewMal;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import org.apache.xmlbeans.impl.xb.xsdschema.All;


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

            super.initierPeriodepostViewMal(PeriodepostTypeEnum.AARSOVERSIKTPOST,
                    this,
                    PeriodetypeEnum.AARSOVERSIKT,
                    aarsoversiktpostRedigeringsomraade,
                    aarsoversiktpostService,
                    Allvitekyklop.hent().getAarsoversiktService());

            if (oppdaterSummerButton == null) {
                leggTilOgTilpassKnapper();
            }
            erInitiert = true;
        }

    }

    private void leggTilOgTilpassKnapper() {
        oppdaterSummerButton = new Button("Oppdater summer");
        oppdaterSummerButton.addClickListener(e ->  aarsoversiktpostService.oppdaterSummer());
        hentKnapperadRedigeringsfelt().addToEnd(oppdaterSummerButton);
    }
}
