package com.hallvardlaerum.periodepost.maanedsoversiktpost;

import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import com.hallvardlaerum.periodepost.PeriodepostRedigeringsomraadeMal;
import com.hallvardlaerum.periodepost.PeriodepostTypeEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@UIScope
public class MaanedsoversiktpostRedigeringsomraade extends PeriodepostRedigeringsomraadeMal implements InitieringsEgnet {
    private boolean erInitiert = false;




    public MaanedsoversiktpostRedigeringsomraade() {
        super();
    }

    @Override
    public boolean erInitiert() {
        return erInitiert;
    }

    public void init() {
        if (!erInitiert) {
            super.initierPeriodepostRedigeringsomraadeMal(
                    PeriodepostTypeEnum.MAANEDSOVERSIKTPOST,
                    Allvitekyklop.hent().getMaanedsoversiktService(),
                    PeriodetypeEnum.MAANEDSOVERSIKT
            );

            erInitiert = true;
        }
    }


}
