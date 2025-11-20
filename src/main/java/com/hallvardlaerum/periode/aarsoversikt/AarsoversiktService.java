package com.hallvardlaerum.periode.aarsoversikt;

import com.hallvardlaerum.periode.PeriodeRedigeringsomraade;
import com.hallvardlaerum.periode.PeriodeRepository;
import com.hallvardlaerum.periode.PeriodeService;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import org.springframework.stereotype.Service;

@Service
public class AarsoversiktService extends PeriodeService {

    public AarsoversiktService(PeriodeRepository periodeRepository) {
        super(periodeRepository);
    }

    public void initier(PeriodeRedigeringsomraade periodeRedigeringsomraade) {
        super.initier(periodeRedigeringsomraade, PeriodetypeEnum.AARSOVERSIKT);
        periodeRedigeringsomraade.initier(PeriodetypeEnum.AARSOVERSIKT);
    }
}
