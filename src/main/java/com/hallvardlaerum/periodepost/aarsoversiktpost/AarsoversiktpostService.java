package com.hallvardlaerum.periodepost.aarsoversiktpost;

import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.periodepost.Periodepost;
import com.hallvardlaerum.periodepost.PeriodepostServiceMal;
import com.hallvardlaerum.periodepost.PeriodepostTypeEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AarsoversiktpostService extends PeriodepostServiceMal implements InitieringsEgnet {
    private boolean erInitiert = false;

    public AarsoversiktpostService(){
    }


    @Override
    public void init() {
        initier();
    }

    @Override
    public boolean erInitiert() {
        return erInitiert;
    }

    public void initier() {
        if (!erInitiert) {
            super.initPeriodepostServiceMal(
                    Allvitekyklop.hent().getAarsoversiktpostRedigeringsomraade(),
                    PeriodepostTypeEnum.AARSOVERSIKTPOST
                    );

            erInitiert = true;
        }
    }

    public void slettAlleAarsoversiktposter() {
        List<Periodepost> aarsoversiktposter = hentRepository().findByPeriodepostTypeEnum(PeriodepostTypeEnum.AARSOVERSIKTPOST);
        hentRepository().deleteAll(aarsoversiktposter);
    }
}
