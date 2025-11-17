package com.hallvardlaerum.regnskap.data;

import com.hallvardlaerum.libs.database.EnumAktig;

public enum NormalposttypeEnum implements EnumAktig {
    NORMAL("Normal","Vanlig post, slik den fremgår av kontoutskriften fra banken"),
    DELPOST("Delpost","Er splittet ut fra en annen post, og er mer detaljert enn en vanlig post. Forelderposten skal være satt til status 'Utelates'"),
    UTELATES("Utelates","Tas med for historikkens del, men skal ikke tas med i utregninger."),
    KREDITTKORTPOST("Kredittkortpost","Delpost fra en kredittkortregning. Forelderposten skal være satt til status 'Utelales'.");

    private String tittel;
    private String beskrivelse;

    NormalposttypeEnum(String tittel, String beskrivelse) {
        this.tittel = tittel;
        this.beskrivelse = beskrivelse;
    }


    @Override
    public String getTittelIImportfil() {
        return tittel;
    }

    @Override
    public String getTittelMedBeskrivelse() {
        return tittel + ": " + beskrivelse;
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

    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }
}
