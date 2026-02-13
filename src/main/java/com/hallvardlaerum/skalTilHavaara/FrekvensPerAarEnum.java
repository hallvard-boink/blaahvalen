package com.hallvardlaerum.skalTilHavaara;

public enum FrekvensPerAarEnum {
    MAANEDLIG(12),
    HVERT_KVARTAL(4),
    HVERT_HALVAAR(2),
    EN_GANG(1);

    private Integer antallPerAar;

    FrekvensPerAarEnum(Integer antallPerAar) {
        this.antallPerAar = antallPerAar;
    }

    public Integer getAntallPerAar() {
        return antallPerAar;
    }
}

