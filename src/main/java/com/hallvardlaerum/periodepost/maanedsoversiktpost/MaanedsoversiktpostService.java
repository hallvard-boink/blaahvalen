package com.hallvardlaerum.periodepost.maanedsoversiktpost;

import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.periodepost.Periodepost;
import com.hallvardlaerum.periodepost.PeriodepostServiceMal;
import com.hallvardlaerum.periodepost.PeriodepostTypeEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaanedsoversiktpostService extends PeriodepostServiceMal implements InitieringsEgnet {
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
            super.initPeriodepostServiceMal(
                    Allvitekyklop.hent().getMaanedsoversiktpostRedigeringsomraade(),
                    PeriodepostTypeEnum.MAANEDSOVERSIKTPOST
                    );
            erInitiert = true;
        }
    }

    public void slettAlleMaanedsoversiktposter() {
        List<Periodepost> maanedsoversiktposter = hentRepository().findByPeriodepostTypeEnum(PeriodepostTypeEnum.AARSOVERSIKTPOST);
        hentRepository().deleteAll(maanedsoversiktposter);
    }
}
