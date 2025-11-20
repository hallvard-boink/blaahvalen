package com.hallvardlaerum.periode;

import com.hallvardlaerum.libs.database.EntitetserviceMal;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeAktig;

public class PeriodeService extends EntitetserviceMal<Periode, PeriodeRepository> {
    private PeriodeRepository periodeRepository;
    private RedigeringsomraadeAktig<Periode> periodeRedigeringsomraade;
    private PeriodetypeEnum periodetypeEnum;

    public PeriodeService(PeriodeRepository periodeRepository) {
        super(Periode.class, periodeRepository);
        this.periodeRepository = periodeRepository;
    }

    public void initier(RedigeringsomraadeAktig<Periode> periodeRedingeringsomraade, PeriodetypeEnum periodetypeEnum) {
        this.periodeRedigeringsomraade = periodeRedingeringsomraade;
        this.periodetypeEnum = periodetypeEnum;
    }

    @Override
    public Periode opprettEntitet() {
        Periode periode = leggTilUUID(new Periode());
        periode.setPeriodetypeEnum(this.periodetypeEnum);
        return periode;
    }

    @Override
    public RedigeringsomraadeAktig<Periode> hentRedigeringsomraadeAktig() {
        return periodeRedigeringsomraade;
    }

    public void oppdaterSummer() {
        Periode periode = periodeRedigeringsomraade.getEntitet();


        // === Regnskap med overføringer ===
        Integer sumRegnskapInntekterMedOverfoeringerInteger = periodeRepository.sumInnFradatoTilDatoNormalposterMedOverfoeringer(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate());
        if (sumRegnskapInntekterMedOverfoeringerInteger==null) {
            sumRegnskapInntekterMedOverfoeringerInteger=0;
        }
        periode.setSumRegnskapInntektMedOverfoeringerInteger(sumRegnskapInntekterMedOverfoeringerInteger);

        Integer sumRegnskapUtgifterMedOverfoeringerInteger = periodeRepository.sumUtFradatoTilDatoNormalposterMedOverfoeringer(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate());
        if (sumRegnskapUtgifterMedOverfoeringerInteger==null) {
            sumRegnskapUtgifterMedOverfoeringerInteger=0;
        }
        periode.setSumRegnskapUtgifterMedOverfoeringerInteger(sumRegnskapUtgifterMedOverfoeringerInteger);

        periode.setSumRegnskapResultatMedOverfoeringerInteger(sumRegnskapInntekterMedOverfoeringerInteger - sumRegnskapUtgifterMedOverfoeringerInteger);

        // == Regnskap uten overføringer ===
        Integer sumRegnskapInntektInteger = periodeRepository.sumUtFradatoTilDatoNormalposterUtenOverfoeringer(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate());
        if (sumRegnskapInntektInteger == null) {
            sumRegnskapInntektInteger = 0;
        }
        periode.setSumRegnskapInntektInteger(sumRegnskapInntektInteger);
        Integer sumRegnskapUtgifterInteger = periodeRepository.sumInnFradatoTilDatoNormalposterUtenOverfoeringer(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate());
        if (sumRegnskapUtgifterInteger == null) {
            sumRegnskapUtgifterInteger = 0;
        }
        periode.setSumRegnskapUtgifterInteger(sumRegnskapUtgifterInteger);

        periode.setSumRegnskapResultatInteger(sumRegnskapInntektInteger - sumRegnskapUtgifterInteger);

        periodeRedigeringsomraade.lesBean();
    }
}
