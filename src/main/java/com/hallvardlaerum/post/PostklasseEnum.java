package com.hallvardlaerum.post;

import com.hallvardlaerum.libs.database.EnumAktig;

import java.util.List;

public enum PostklasseEnum implements EnumAktig<PostklasseEnum> {

    NORMALPOST("Normalpost","Vanlig post fra bankens kontoutskrifter, eller delposter vi har lagt til selv"),
    BUDSJETTPOST("Budsjettpost","Post som beskriver forventede utgifter eller inntekter");

    private String tittel;
    private String beskrivelse;

    PostklasseEnum(String tittel, String beskrivelse) {
        this.tittel = tittel;
        this.beskrivelse = beskrivelse;
    }

    public static PostklasseEnum konverterFraByte(Byte kodeByte) {
        if (kodeByte==null) {
            return null;
        }
        for (PostklasseEnum postklasseEnum:PostklasseEnum.values()) {
            if (postklasseEnum.ordinal()==kodeByte.intValue()){
                return postklasseEnum;
            }
        }
        return null;
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
    public String hentTooltip() {
        return EnumAktig.opprettTooltip(hentVerdier());
    }

    @Override
    public List<PostklasseEnum> hentVerdier() {
        return List.of(PostklasseEnum.values());
    }


    public String getTittelMedBeskrivelse() {
        return tittel + " (" + beskrivelse + ")";
    }

    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }



}
