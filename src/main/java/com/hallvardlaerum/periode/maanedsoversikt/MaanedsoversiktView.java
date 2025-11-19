package com.hallvardlaerum.periode.maanedsoversikt;

import com.hallvardlaerum.periode.Periode;
import com.hallvardlaerum.periode.PeriodeViewMal;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.Route;
import org.springframework.data.domain.Sort;

@Route("maanedsoversikt")
@Menu(order=20, title="Månedsoversikt")
public class MaanedsoversiktView extends PeriodeViewMal {
    private MaanedsoversiktService maanedsoversiktService;

    public MaanedsoversiktView(MaanedsoversiktService maanedsoversiktService) {
        super(maanedsoversiktService);
        this.maanedsoversiktService = maanedsoversiktService;
        hentVindutittel().setText("Månedsoversikt");
        initierGridMedPagedSearch();
        leggTilOgTilpassKnapper();

    }

    private void leggTilOgTilpassKnapper() {
        Button oppdaterSummerButton = new Button("Oppdater summer");
        oppdaterSummerButton.addClickListener(e -> maanedsoversiktService.oppdaterSummer());
        hentKnapperadRedigeringsfelt().add(oppdaterSummerButton);

        hentNyButton().addClickListener(e -> {
           Periode periode = (Periode)hentEntitet();
           periode.setPeriodetypeEnum(PeriodetypeEnum.MAANEDSOVERSIKT);
           hentRedigeringsomraadeAktig().lesBean();
        });
    }




    private void initierGridMedPagedSearch() {
        super.initierCallbackDataProviderIGrid(
                query -> maanedsoversiktService.finnEntiteterMedSpecification(
                        query.getOffset(),
                        query.getLimit(),
                        maanedsoversiktService.getEntityFilterSpecification(),
                        Sort.by("datoFraLocalDate").descending()
                ),

                query -> maanedsoversiktService.tellAntallMedSpecification(
                        query.getOffset(),
                        query.getLimit(),
                        maanedsoversiktService.getEntityFilterSpecification())
        );
    }

}
