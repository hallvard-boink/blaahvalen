package com.hallvardlaerum.post.budsjettpost;

import com.hallvardlaerum.libs.database.EnumAktig;

public enum EstimatpresisjonEnum implements EnumAktig {
    LAV("Lav","F.eks. gjettet på"),

    MIDDELS("Middels","F.eks. estimert med regnestykke med sammenligningsgrunnlag, "+
            "eller oppgitt som veiledende sum fra leverandør eller annen kilde"),

    HOY("Høy","F.eks. hentet fra tidligere registreringer i regnskapet, "+
            "eller oppgitt som presis sum i konkret tilbud fra leverandør")
    ;

    private String tittel;
    private String beskrivelse;

    public static EstimatpresisjonEnum hentFraTittel(String tittel) {
        for (EstimatpresisjonEnum e:EstimatpresisjonEnum.values()) {
            if (e.getTittel().equals(tittel)) {
                return e;
            }
        }
        return null;
    }

    EstimatpresisjonEnum(String tittel, String beskrivelse) {
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


    public String getTittelMedBeskrivelse() {
        return tittel + " (" + beskrivelse + ")";
    }

    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }
}
