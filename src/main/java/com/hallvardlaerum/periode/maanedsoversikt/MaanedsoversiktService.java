package com.hallvardlaerum.periode.maanedsoversikt;


import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.felter.Datokyklop;
import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.periode.*;
import com.hallvardlaerum.periode.aarsoversikt.AarsoversiktService;
import com.hallvardlaerum.periodepost.maanedsoversiktpost.MaanedsoversiktpostService;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MaanedsoversiktService extends PeriodeServiceMal implements InitieringsEgnet {
    private AarsoversiktService aarsoversiktService;
    private boolean erInitiert = false;


    public Periode finnMaanedsoversiktFraAarMnd(String aarMnd){
        if (aarMnd == null || aarMnd.isEmpty()) {
            return null;
        }

        String datoString = aarMnd + "-01";
        LocalDate datoFra = LocalDate.parse(datoString);

        List<Periode> periodeList = hentRepository().findByPeriodetypeEnumAndDatoFraLocalDate(PeriodetypeEnum.MAANEDSOVERSIKT, datoFra);
        if (periodeList.isEmpty()) {
            return null;
        } else {
            if (periodeList.size()>1) {
                Loggekyklop.bruk().loggINFO("Fant mer enn en måned med Aar-mnd " + aarMnd + ", men fortsetter");
            }
            return periodeList.getFirst();
        }

    }

    public MaanedsoversiktService() {
        super();
    }

    @Override
    public boolean erInitiert() {
        return erInitiert;
    }

    @Override
    public void init() {
        if (!erInitiert) {
            super.initier(
                    Allvitekyklop.hent().getMaanedsoversiktRedigeringsomraade(),
                    PeriodetypeEnum.MAANEDSOVERSIKT,
                    Allvitekyklop.hent().getMaanedsoversiktpostService(),
                    Allvitekyklop.hent().getNormalpostService()
            );
            this.aarsoversiktService = Allvitekyklop.hent().getAarsoversiktService();
            erInitiert = true;
        }
    }




    /**
     * Det opprettes maanedsoversiktposter, men de vises ikke i oversikten - bare de som er opprettet manuelt.
     */

    public void opprettMaanedsoversikterForHeleAaret() {
        Periode aarsoversiktPeriode = aarsoversiktService.finnAarsoversiktFraMaanedsoversikt(hentRedigeringsomraadeAktig().getEntitet());
        if (aarsoversiktPeriode==null) {
            Loggekyklop.hent().loggADVARSEL("Fant ikke årsoversikt som passet med månedsoversikten med datoFra " +
                    hentRedigeringsomraadeAktig().getEntitet().getDatoFraLocalDate());
            return;
        }

        List<Periode> maanedsoversiktList = hentRepository().findByPeriodetypeEnumAndDatoFraLocalDateGreaterThanEqualAndDatoTilLocalDateLessThanEqual(PeriodetypeEnum.MAANEDSOVERSIKT,
                aarsoversiktPeriode.getDatoFraLocalDate(), aarsoversiktPeriode.getDatoTilLocalDate());
        for (int i =1; i<=12; i++) {
            int finalI = i;
            List<Periode> funnedeMaanedsoversikter = maanedsoversiktList.stream().filter(p -> p.getDatoFraLocalDate().getMonth().getValue()== finalI).toList();
            if (funnedeMaanedsoversikter.isEmpty()) {
                Periode periode = opprettEntitet();
                periode.setPeriodetypeEnum(PeriodetypeEnum.MAANEDSOVERSIKT);
                periode.setDatoFraLocalDate(LocalDate.of(aarsoversiktPeriode.getDatoFraLocalDate().getYear(), i,1));
                periode.setDatoTilLocalDate(Datokyklop.hent().finnSisteIMaaneden(periode.getDatoFraLocalDate()));
                lagre(periode);
            }
        }
        Allvitekyklop.hent().getMaanedsoversiktView().oppdaterSoekeomraade();
        //hentRedigeringsomraadeAktig().hentView().oppdaterSoekeomraade();

    }

}
