package com.hallvardlaerum.periode.aarsoversikt;

import com.hallvardlaerum.libs.database.EntitetserviceAktig;
import com.hallvardlaerum.periode.Periode;
import com.hallvardlaerum.periode.PeriodeViewMal;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.Route;

@Route("aarsoversikt")
@Menu(order=30, title="Årsoversikt")
public class AarsoversiktView extends PeriodeViewMal {
    private AarsoversiktService aarsoversiktService;

    public AarsoversiktView(AarsoversiktService aarsoversiktService) {
        super(aarsoversiktService);
        this.aarsoversiktService = aarsoversiktService;
        super.initier(PeriodetypeEnum.AARSOVERSIKT,this);
        leggTilOgTilpassKnapper();
    }

    private void leggTilOgTilpassKnapper() {
        Button oppdaterSummerButton = new Button("Oppdater summer");
        oppdaterSummerButton.addClickListener(e -> aarsoversiktService.oppdaterSummer());
        hentKnapperadRedigeringsfelt().addToEnd(oppdaterSummerButton);

    }
}
