package com.hallvardlaerum.periodepost.kostnadspakke;

import com.hallvardlaerum.periodepost.Periodepost;

public class PeriodedelAvKostnadspakkeRad {
    private Periodepost kostnadspakke;
    private Integer sumForDenneMaaned;


    public PeriodedelAvKostnadspakkeRad(Periodepost kostnadspakke, Integer sumForDenneMaaned) {
        this.kostnadspakke = kostnadspakke;
        this.sumForDenneMaaned = sumForDenneMaaned;
    }

    public Periodepost getKostnadspakke() {
        return kostnadspakke;
    }

    public void setKostnadspakke(Periodepost kostnadspakke) {
        this.kostnadspakke = kostnadspakke;
    }

    public Integer getSumForDenneMaaned() {
        return sumForDenneMaaned;
    }

    public void setSumForDenneMaaned(Integer sumForDenneMaaned) {
        this.sumForDenneMaaned = sumForDenneMaaned;
    }

    public Integer getSumTotalt() {
        if (kostnadspakke == null) {
            return 0;
        } else {
            return kostnadspakke.getSumRegnskapInteger();
        }
    }

    public String getTittel() {
        if (kostnadspakke == null) {
            return "";
        } else {
            return kostnadspakke.getTittelString();
        }
    }
}