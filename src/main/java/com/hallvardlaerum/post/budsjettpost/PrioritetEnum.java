package com.hallvardlaerum.post.budsjettpost;

import com.hallvardlaerum.libs.database.EnumAktig;

import java.util.List;

public enum PrioritetEnum implements EnumAktig<PrioritetEnum> {
    MAA("1 - Må","Denne må gjennomføres uansett"),
    BOER("2 - Bør","Vi kan klare oss uten, men bør helst ta denne"),
    KAN("3 - Kan","Denne kan vi fint vente med");

    private String tittel;
    private String beskrivelse;

    PrioritetEnum(String tittel, String beskrivelse) {
        this.tittel = tittel;
        this.beskrivelse = beskrivelse;
    }

    public static PrioritetEnum hentFraTittel(String tittel) {
        for (PrioritetEnum p:PrioritetEnum.values()) {
            if (p.getTittel().equals(tittel)) {
                return p;
            }
        }
        return null;

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
    public String hentTooltip() {
        return EnumAktig.opprettTooltip(hentVerdier());
    }

    @Override
    public List<PrioritetEnum> hentVerdier() {
        return List.of(PrioritetEnum.values());
    }


    public String getTittelMedBeskrivelse() {
        return tittel + " (" + beskrivelse + ")";
    }

    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }


}
