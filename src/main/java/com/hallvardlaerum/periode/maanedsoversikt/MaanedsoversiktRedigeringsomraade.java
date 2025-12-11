package com.hallvardlaerum.periode.maanedsoversikt;

import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.periode.PeriodeRedigeringsomraadeMal;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@UIScope
public class MaanedsoversiktRedigeringsomraade extends PeriodeRedigeringsomraadeMal implements InitieringsEgnet {
    private boolean erInitiert=false;

    public MaanedsoversiktRedigeringsomraade() {
    }

    @Override
    public boolean erInitiert() {
        return erInitiert;
    }

    public void init() {
        if (!erInitiert) {
            super.initierPeriodeRedigeringsomraadeMal(PeriodetypeEnum.MAANEDSOVERSIKT,
                    Allvitekyklop.hent().getMaanedsoversiktpostService(),
                    Allvitekyklop.hent().getMaanedsoversiktpostRedigeringsomraadeTilDialog(),
                    Allvitekyklop.hent().getMaanedsoversiktService());
            erInitiert = true;
        }
    }


}
