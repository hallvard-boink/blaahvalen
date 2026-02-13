package com.hallvardlaerum.post.budsjettpost;

import com.hallvardlaerum.libs.database.EnumAktig;

import java.util.List;

public enum BudsjettpoststatusEnum implements EnumAktig<BudsjettpoststatusEnum> {
    FORESLAATT("Foreslått","Tenkt på, ønsket, kanskje planlagt, men ikke satt inn i et konkret budsjett"),
    TILDELT("Tildelt","Tildelt i et konkret månedsbudsjett");

    private String tittel;
    private String beskrivelse;

    BudsjettpoststatusEnum(String tittel, String beskrivelse) {
        this.tittel = tittel;
        this.beskrivelse = beskrivelse;
    }

    public static BudsjettpoststatusEnum hentFraTittel(String tittel) {
        for (BudsjettpoststatusEnum b:BudsjettpoststatusEnum.values()) {
            if (b.getTittel().equals(tittel)) {
                return b;
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
    public List<BudsjettpoststatusEnum> hentVerdier() {
        return List.of(BudsjettpoststatusEnum.values());
    }


    public String getTittelMedBeskrivelse() {
        return tittel + " (" + beskrivelse + ")";
    }

    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }



}
