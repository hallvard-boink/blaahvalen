package com.hallvardlaerum.budsjett;

import com.hallvardlaerum.libs.database.EnumAktig;

public enum BudsjettpoststatusEnum implements EnumAktig {
    FORESLAATT("Foreslått","Tenkt på, ønsket, kanskje planlagt, men ikke satt inn i et konkret budsjett"),
    TILDELT("Tildelt","Tildelt i et konkret månedsbudsjett");

    private String tittel;
    private String beskrivelse;

    BudsjettpoststatusEnum(String tittel, String beskrivelse) {
        this.tittel = tittel;
        this.beskrivelse = beskrivelse;
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
    public String getTittelMedBeskrivelse() {
        return tittel + " (" + beskrivelse + ")";
    }

    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }
}
