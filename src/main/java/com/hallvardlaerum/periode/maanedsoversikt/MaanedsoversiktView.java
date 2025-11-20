package com.hallvardlaerum.periode.maanedsoversikt;

import com.hallvardlaerum.periode.PeriodeViewMal;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.Route;


@Route("maanedsoversikt")
@Menu(order=20, title="Månedsoversikt")
public class MaanedsoversiktView extends PeriodeViewMal {
    private MaanedsoversiktService maanedsoversiktService;

    public MaanedsoversiktView(MaanedsoversiktService maanedsoversiktService) {
        super(maanedsoversiktService);
        this.maanedsoversiktService = maanedsoversiktService;
        super.initier(PeriodetypeEnum.MAANEDSOVERSIKT, this);

        leggTilOgTilpassKnapper();

    }



    private void leggTilOgTilpassKnapper() {
        Button oppdaterSummerButton = new Button("Oppdater summer");
        oppdaterSummerButton.addClickListener(e -> maanedsoversiktService.oppdaterSummer());
        hentKnapperadRedigeringsfelt().addToEnd(oppdaterSummerButton);

    }



}
