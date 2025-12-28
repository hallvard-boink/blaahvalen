package com.hallvardlaerum.post.normalpost;

import com.hallvardlaerum.libs.database.EnumAktig;

//TODO: Fjerne kredittkortpost?
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

    public static NormalposttypeEnum hentFraTittel(String tittel) {
        for (NormalposttypeEnum type:NormalposttypeEnum.values()) {
            if (type.getTittel().equals(tittel)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String getTittelIImportfil() {
        return tittel;
    }


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
