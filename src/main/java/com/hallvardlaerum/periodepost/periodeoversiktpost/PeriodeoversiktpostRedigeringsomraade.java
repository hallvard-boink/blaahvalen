package com.hallvardlaerum.periodepost.periodeoversiktpost;

import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import com.hallvardlaerum.periodepost.Periodepost;
import com.hallvardlaerum.periodepost.PeriodepostRedigeringsomraadeMal;
import com.hallvardlaerum.periodepost.PeriodepostTypeEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.stereotype.Component;

@Component
@UIScope
public class PeriodeoversiktpostRedigeringsomraade extends PeriodepostRedigeringsomraadeMal implements InitieringsEgnet {
    private boolean erInitiert = false;



    @Override
    public void init() {
        if (!erInitiert) {
            super.initierPeriodepostRedigeringsomraadeMal(
                    PeriodepostTypeEnum.PERIODEOVERSIKTPOST,
                    Allvitekyklop.hent().getAarsoversiktService(),
                    PeriodetypeEnum.AARSOVERSIKT
            );

            erInitiert = true;
        }
    }


    public PeriodeoversiktpostRedigeringsomraade() {
        super();
    }

    @Override
    public boolean erInitiert() {
        return erInitiert;
    }
}
