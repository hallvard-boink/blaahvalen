package com.hallvardlaerum.periode.maanedsoversikt;

import com.hallvardlaerum.periode.PeriodeRedigeringsomraade;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import org.springframework.stereotype.Component;

@Component
public class MaanedsoversiktRedigeringsomraade extends PeriodeRedigeringsomraade {

    public MaanedsoversiktRedigeringsomraade() {
        super();
        super.initier(PeriodetypeEnum.MAANEDSOVERSIKT);
    }
}
