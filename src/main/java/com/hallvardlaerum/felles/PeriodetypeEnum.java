package com.hallvardlaerum.felles;

import com.hallvardlaerum.libs.database.EnumAktig;

public enum PeriodetypeEnum implements EnumAktig {

    AARSOVERSIKT("Full dato","Hele datoen er kjent"),
    MAANEDSOVERSIKT("Månedsoversikt","Måned og år er kjent"),
    MAANEDSBUDSJETTMAL("Budsjettmal","Utkast til budsjett for en gitt måned og år");


    private String tittel;
    private String beskrivelse;



    PeriodetypeEnum(String tittel, String beskrivelse) {
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
