package com.hallvardlaerum.kategori;

import com.hallvardlaerum.libs.database.EnumAktig;

public enum KategoriType implements EnumAktig {
    STANDARD("Standard","De fleste kategoriene"), // [0]
    KREDITTKORT("Kredittkort","Tina eller Hallvards kredittkortregninger"), // [1]
    OVERFOERING("Overføring","Alle type refusjoner, utlegg, overføring mellom konti m.m."),  // [2]
    UKATEGORISERT("Ukategorisert","Ikke kategorisert ennå"), // [3]
    SKAL_IKKE_KATEGORISERES("Skal ikke kategoriseres","Poster som det ikke gir mening å kategorisere"),  // [4]
    KATEGORI_IKKE_FUNNET("Kategori ikke funnet","Poster vi ikke har klart å finne kategori til"),  // [5]
    TIL_SPARING("Til sparing","Kategorier som handler om å sette av penger."),  // [6]
    ;


    private String tittel;
    private String beskrivelse;

    KategoriType(String tittel, String beskrivelse) {
        this.tittel = tittel;
        this.beskrivelse = beskrivelse;
    }

    public static KategoriType hentFraTittel(String tittel){
        return EnumAktig.hentFraTittel(KategoriType.class,tittel);
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


    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }
}
