package com.hallvardlaerum.kategori;

import com.hallvardlaerum.libs.database.EnumAktig;

import java.util.List;

public enum KategoriRetning implements EnumAktig<KategoriRetning> {
    INN ("Inn","Inntekter, lønn og salg av ting"),
    UT("Ut","Utgifter, kostnader, alt sånn"),
    UKJENT("Ukjent","Vet ikke om det er inntekter eller utgifter. Brukes på Ukategorisert eller Skal ikke kategoriseres");

    private String tittel;
    private String beskrivelse;

    KategoriRetning(String tittel, String beskrivelse) {
        this.tittel = tittel;
        this.beskrivelse = beskrivelse;
    }

    public static KategoriRetning hentFraTittel(String tittel) {
        return EnumAktig.hentFraTittel(KategoriRetning.class,tittel);
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
    public List<KategoriRetning> hentVerdier() {
        return List.of(KategoriRetning.values());
    }


    public String getTittelMedBeskrivelse() {
        return tittel + ": " + beskrivelse;
    }

    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }


}
