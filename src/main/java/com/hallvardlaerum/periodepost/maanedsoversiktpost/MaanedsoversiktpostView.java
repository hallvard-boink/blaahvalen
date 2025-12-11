package com.hallvardlaerum.periodepost.maanedsoversiktpost;

import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import com.hallvardlaerum.periodepost.PeriodepostTypeEnum;
import com.hallvardlaerum.periodepost.PeriodepostViewMal;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.Menu;
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
            if (oppdaterSummerButton==null){
                leggTilOgTilpassKnapper();
            }
            erInitiert = true;
        }
    }

    private void leggTilOgTilpassKnapper() {
        oppdaterSummerButton = new Button("Oppdater summer");
        oppdaterSummerButton.addClickListener(e -> maanedsoversiktpostService.oppdaterSummer());
        oppdaterSummerButton.setEnabled(false);
        super.hentKnapperadRedigeringsfelt().addToEnd(oppdaterSummerButton);

    }

}
