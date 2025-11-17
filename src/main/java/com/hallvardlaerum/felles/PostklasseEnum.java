package com.hallvardlaerum.felles;

import com.hallvardlaerum.libs.database.EnumAktig;

public enum PostklasseEnum implements EnumAktig {

    NORMALPOST("Normalpost","Vanlig post fra bankens kontoutskrifter, eller delposter vi har lagt til selv"),
    BUDSJETTPOST("Budsjettpost","Post som beskriver forventede utgifter eller inntekter");

    private String tittel;
    private String beskrivelse;

    PostklasseEnum(String tittel, String beskrivelse) {
        this.tittel = tittel;
        this.beskrivelse = beskrivelse;
    }

    public String getTittel() {
        return tittel;
    }

    public void setTittel(String tittel) {
        this.tittel = tittel;
    }

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
