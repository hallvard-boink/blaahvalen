package com.hallvardlaerum.periode.aarsoversikt;

import com.hallvardlaerum.periode.PeriodeRedigeringsomraade;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import org.springframework.stereotype.Component;

@Component
public class AarsoversiktRedigeringsomraade extends PeriodeRedigeringsomraade {

    public AarsoversiktRedigeringsomraade() {
        super();
        super.initier(PeriodetypeEnum.AARSOVERSIKT);
    }
}
