package com.hallvardlaerum.periode.maanedsoversikt;

import com.hallvardlaerum.libs.database.EntitetserviceMal;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeAktig;
import com.hallvardlaerum.periode.Periode;
import com.hallvardlaerum.periode.PeriodeRepository;
import org.springframework.stereotype.Service;

@Service
public class MaanedsoversiktService extends EntitetserviceMal<Periode, PeriodeRepository> {
    private PeriodeRepository periodeRepository;
    private MaanedsoversiktRedigeringsomraade maanedsoversiktRedigeringsomraade;

    public MaanedsoversiktService(PeriodeRepository periodeRepository) {
        super(Periode.class, periodeRepository);
        this.periodeRepository = periodeRepository;
    }

    public void initier(MaanedsoversiktRedigeringsomraade maanedsoversiktRedigeringsomraade) {
        this.maanedsoversiktRedigeringsomraade = maanedsoversiktRedigeringsomraade;
        this.maanedsoversiktRedigeringsomraade.initier();
    }




    @Override
    public Periode opprettEntitet() {
        return leggTilUUID(new Periode());
    }

    @Override
    public RedigeringsomraadeAktig<Periode> hentRedigeringsomraadeAktig() {
        return maanedsoversiktRedigeringsomraade;
    }


    public void oppdaterSummer() {
        Periode periode = maanedsoversiktRedigeringsomraade.getEntitet();

        Integer sumRegnskapUtgifterInteger = periodeRepository.sumUtFradatoTilDatoNormalposter(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate());
        if (sumRegnskapUtgifterInteger==null) {
            sumRegnskapUtgifterInteger=0;
        }
        periode.setSumRegnskapUtgifterInteger(sumRegnskapUtgifterInteger);

        Integer sumRegnskapInntektInteger = periodeRepository.sumInnFradatoTilDatoNormalposter(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate());
        if (sumRegnskapInntektInteger==null) {
            sumRegnskapInntektInteger=0;
        }
        periode.setSumRegnskapInntektInteger(sumRegnskapInntektInteger);


        Integer sumRegnskapResultatInteger =  periode.getSumRegnskapInntektInteger()-periode.getSumRegnskapUtgifterInteger();
        periode.setSumRegnskapResultatInteger(sumRegnskapResultatInteger);

        maanedsoversiktRedigeringsomraade.lesBean();
    }
}
