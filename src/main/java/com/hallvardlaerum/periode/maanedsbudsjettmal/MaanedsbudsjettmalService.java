package com.hallvardlaerum.periode.maanedsbudsjettmal;

import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.periode.PeriodeServiceMal;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import org.springframework.stereotype.Service;

@Service
public class MaanedsbudsjettmalService extends PeriodeServiceMal implements InitieringsEgnet {
    private boolean erInitiert = false;



    @Override
    public void init() {
        if (!erInitiert) {
            super.initier(
                    Allvitekyklop.hent().getMaanedsoversiktRedigeringsomraade(),
                    PeriodetypeEnum.MAANEDSOVERSIKT,
                    Allvitekyklop.hent().getMaanedsoversiktpostService(),
                    Allvitekyklop.hent().getNormalpostService()
            );

            erInitiert = true;
        }
    }

    public MaanedsbudsjettmalService() {
        super();
    }

    @Override
    public boolean erInitiert() {
        return erInitiert;
    }
}
