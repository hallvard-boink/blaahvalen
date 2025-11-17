package com.hallvardlaerum.felles;

import com.hallvardlaerum.libs.database.EnumAktig;

public enum PeriodepostTypeEnum implements EnumAktig {

    AARSOVERSIKTPOST("Årsoversiktpost",""),
    AARSOVERSIKTBUDSJETTPOST("Årsoversiktbudsjettpost",""),
    MAANEDSOVERSIKTPOST("Månedsoversiktpost",""),
    MAANEDSOVERSIKTBUDSJETTPOST("Månedsoversiktbudsjettpost",""),
    OVERSIKTNORMALPOST("Kostnadspakke",""),
    OVERSIKTBUDSJETTPOST("Budsjettpostgruppe","")

    ;

    private String tittel;
    private String beskrivelse;

    PeriodepostTypeEnum(String tittel, String beskrivelse) {
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
