package com.hallvardlaerum.periode;

import com.hallvardlaerum.libs.database.EnumAktig;
import com.hallvardlaerum.libs.felter.DatopresisjonEnum;
import com.hallvardlaerum.periodepost.PeriodepostTypeEnum;

public enum PeriodetypeEnum implements EnumAktig {

    AARSOVERSIKT("Årsoversikt","Oversikt over regnskap og budsjett for et helt år",
            DatopresisjonEnum.AAR,PeriodepostTypeEnum.AARSOVERSIKTPOST),
    MAANEDSOVERSIKT("Månedsoversikt","Oversikt over regnskap og budsjett i en gitt måned",
            DatopresisjonEnum.MAANED, PeriodepostTypeEnum.MAANEDSOVERSIKTPOST),
    MAANEDSBUDSJETTMAL("Budsjettmal","Utkast til budsjett for en gitt måned og år",
            DatopresisjonEnum.MAANED, PeriodepostTypeEnum.MAANEDSOVERSIKTPOST);


    private String tittel;
    private String beskrivelse;
    private DatopresisjonEnum datopresisjonEnum;
    private PeriodepostTypeEnum periodepostTypeEnum;


    PeriodetypeEnum(String tittel, String beskrivelse, DatopresisjonEnum datopresisjonEnum, PeriodepostTypeEnum periodepostTypeEnum) {
        this.tittel = tittel;
        this.beskrivelse = beskrivelse;
        this.datopresisjonEnum = datopresisjonEnum;
        this.periodepostTypeEnum = periodepostTypeEnum;
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

    public DatopresisjonEnum getDatopresisjonEnum() {
        return datopresisjonEnum;
    }

    public void setDatopresisjonEnum(DatopresisjonEnum datopresisjonEnum) {
        this.datopresisjonEnum = datopresisjonEnum;
    }

    public PeriodepostTypeEnum getPeriodepostTypeEnum() {
        return periodepostTypeEnum;
    }

    public void setPeriodepostTypeEnum(PeriodepostTypeEnum periodepostTypeEnum) {
        this.periodepostTypeEnum = periodepostTypeEnum;
    }
}
