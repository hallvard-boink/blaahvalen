package com.hallvardlaerum.periodepost.maanedsoversiktpost;

import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.periode.maanedsoversikt.MaanedsoversiktService;
import com.hallvardlaerum.periodepost.PeriodepostServiceMal;
import com.hallvardlaerum.periodepost.PeriodepostTypeEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import org.springframework.stereotype.Service;

@Service
public class MaanedsoversiktpostService extends PeriodepostServiceMal implements InitieringsEgnet {
    private MaanedsoversiktService maanedsoversiktService;
    private boolean erInitiert = false;




    public MaanedsoversiktpostService(){
    }

    @Override
    public boolean erInitiert() {
        return erInitiert;
    }

    @Override
    public void init() {
        if (!erInitiert) {
            super.initPeriodepostServiceMal(Allvitekyklop.hent().getMaanedsoversiktpostRedigeringsomraade(),
                    PeriodepostTypeEnum.MAANEDSOVERSIKTPOST,
                    Allvitekyklop.hent().getMaanedsoversiktService());
            erInitiert = true;
        }
    }
}
