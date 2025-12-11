package com.hallvardlaerum.periode.aarsoversikt;

import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.periode.*;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AarsoversiktService extends PeriodeServiceMal implements InitieringsEgnet {
    private boolean erInitiert=false;

    public AarsoversiktService() {

    }

    @Override
    public boolean erInitiert() {
        return erInitiert;
    }

    public void init() {
        if (!erInitiert) {
            super.initier(Allvitekyklop.hent().getAarsoversiktRedigeringsomraade(),
                    PeriodetypeEnum.AARSOVERSIKT,
                    Allvitekyklop.hent().getAarsoversiktpostService(),
                    Allvitekyklop.hent().getNormalpostService()
            );
            erInitiert = true;
        }
    }

    public Periode finnAarsoversiktFraMaanedsoversikt(Periode maanedsOversikt) {
        if (maanedsOversikt==null) {
            return null;
        }

        LocalDate fraLocalDate = LocalDate.of(maanedsOversikt.getDatoFraLocalDate().getYear(),1,1);

        List<Periode> aarsoversiktList = hentRepository().findByPeriodetypeEnumAndDatoFraLocalDate(PeriodetypeEnum.AARSOVERSIKT, fraLocalDate);
        if (aarsoversiktList.isEmpty()) {
            return null;
        } else {
            if (aarsoversiktList.size()>1) {
                Loggekyklop.hent().loggADVARSEL("Fant mer enn en årsoversikt med fradato " + fraLocalDate );
            }
            return aarsoversiktList.getFirst();
        }


    }




}
