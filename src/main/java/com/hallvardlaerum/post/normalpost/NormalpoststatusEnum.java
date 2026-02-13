package com.hallvardlaerum.post.normalpost;

import com.hallvardlaerum.libs.database.EnumAktig;

import java.util.List;

public enum NormalpoststatusEnum implements EnumAktig<NormalpoststatusEnum> {

    UBEHANDLET("Ubehandlet","Posten er akkurat blitt importert, og er ikke bearbeidet ennå."),
    UNDER_ARBEID("Under arbeid","Posten er ikke ferdig bearbeidet, må fortsette senere."),
    FERDIG("Ferdig","Posten er ferdig bearbeidet, og kan tas med i utregninger"),
    SKAL_REFUNDERES("Skal refunderes","Posten er et utlegg som skal refunderes fra andre (registreres i egen post)"),
    FERDIG_REFUNDERT("Ferdig refundert","Posten er ferdig refundert");


    private String tittel;
    private String beskrivelse;

    NormalpoststatusEnum(String tittel, String beskrivelse) {
        this.tittel = tittel;
        this.beskrivelse = beskrivelse;
    }

    public static NormalpoststatusEnum hentFraTittel(String tittel) {
        for (NormalpoststatusEnum status:NormalpoststatusEnum.values()) {
            if (status.getTittel().equals(tittel)) {
                return status;
            }
        }
        return null;
    }

    @Override
    public String getTittel() {
        return tittel;
    }

    public void setTittel(String tittel) {
        this.tittel = tittel;
    }

    @Override
    public String getBeskrivelse() {
        return beskrivelse;
    }

    @Override
    public String getTittelIImportfil() {
        return tittel;
    }

    @Override
    public String hentTooltip() {
        return EnumAktig.opprettTooltip(hentVerdier());
    }

    @Override
    public List<NormalpoststatusEnum> hentVerdier() {
        return List.of(NormalpoststatusEnum.values());
    }


    public String getTittelMedBeskrivelse() {
        return tittel + ": " + beskrivelse;
    }

    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }


}
