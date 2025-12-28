package com.hallvardlaerum.periodepost;

import com.hallvardlaerum.libs.database.EnumAktig;

public enum PeriodepostTypeEnum implements EnumAktig {

    AARSOVERSIKTPOST("Årsoversiktpost","Sum budsjetterte og registrerte utgifter og inntekter med samme kategori innenfor et år"),
    MAANEDSOVERSIKTPOST("Månedsoversiktpost","Sum budsjetterte og registrerte utgifter og inntekter med samme kategori innenfor en måned"),
    PERIODEOVERSIKTPOST("Periodeoversiktpost","Sum budsjetterte og registrerte utgifter og inntekter med samme kategori innenfor en periode, utenom år eller måned. " +
            "Brukes til å regne på grupperte kostnader av samme type, for eksempel ferieturer. Erstatter kostnadspakke."),
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


    public String getTittelMedBeskrivelse() {
        return tittel + " (" + beskrivelse + ")";
    }

    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }
}
