package com.hallvardlaerum.periode.maanedsbudsjettmal;

import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.periode.PeriodeViewMal;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;

@Route("maanedsbudsjettmal")
@UIScope
public class MaanedsbudsjettmalView extends PeriodeViewMal implements InitieringsEgnet {
    private boolean erInitiert = false;

    @Override
    public void init() {
        if (!erInitiert) {
            super.initPeriodeViewMal(PeriodetypeEnum.MAANEDSBUDSJETTMAL,
                    this,
                    Allvitekyklop.hent().getMaanedsbudsjettmalService(),
                    Allvitekyklop.hent().getMaanedsbudsjettmalRedigeringsomraade()
                    );
            hentVindutittel().setText("Månedsbudsjettmaler");
            erInitiert = true;
        }
    }

    public MaanedsbudsjettmalView() {
        super();
        Allvitekyklop.hent().setMaanedsbudsjettmalView(this);
        init();
    }

    @Override
    public boolean erInitiert() {
        return false;
    }
}
