package com.hallvardlaerum.periode.aarsoversikt;

import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.periode.PeriodeRedigeringsomraadeMal;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@UIScope
public class AarsoversiktRedigeringsomraade extends PeriodeRedigeringsomraadeMal implements InitieringsEgnet {
    private boolean erInitiert = false;


    public AarsoversiktRedigeringsomraade() {
        super();
    }

    @Override
    public boolean erInitiert() {
        return erInitiert;
    }

    @Override
    public void init() {
        if (!erInitiert) {
            super.initierPeriodeRedigeringsomraadeMal(PeriodetypeEnum.AARSOVERSIKT,
                    Allvitekyklop.hent().getAarsoversiktpostService(),
                    Allvitekyklop.hent().getAarsoversiktpostRedigeringsomraadeTilDialog(),
                    Allvitekyklop.hent().getAarsoversiktService()
            );
            erInitiert=true;
        }
    }
}
