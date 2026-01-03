package com.hallvardlaerum.kategori;

import com.hallvardlaerum.libs.eksportimport.CSVImportassistentMal;
import com.hallvardlaerum.libs.felter.BoolskMester;
import com.hallvardlaerum.verktoy.Allvitekyklop;

import java.util.ArrayList;
import java.util.List;

/**
 * Hjelpeklasse for å importere standard kategorier fra gamle Blaahvalen.
 * Regner med at tabellen Kategori er tom, sjekker ikke om posten finnes allerede.
 *
 */
public class KategoriFraBlaahvalenCSVImportassistent extends CSVImportassistentMal<Kategori> {
    private KategoriService kategoriService;

    @Override
    public void forberedImport() {
        kategoriService = Allvitekyklop.hent().getKategoriService();
    }

    @Override
    public Kategori konverterFraTekstRadOgLagre(ArrayList<String> feltnavnCSVArrayList, String[] celler) {
        lesInnFeltnavnogCeller(feltnavnCSVArrayList,celler);

        Kategori kategori = kategoriService.opprettEntitet();

        kategori.setTittel(hentVerdi("tittel"));
        kategori.setUndertittel(hentVerdi("undertittel"));
        kategori.setNivaa(parseInt(hentVerdi("nivaa")));
        kategori.setBeskrivelse(hentVerdi("beskrivelse"));
        kategori.setKategoriType(KategoriType.hentFraTittel(hentVerdi("kategoritype")));
        kategori.setKategoriRetning(KategoriRetning.hentFraTittel(hentVerdi("kategoriretning")));
        kategori.setErAktiv(konverterBoolskFraStreng(hentVerdi("erAktiv")));
        kategori.setBrukesTilBudsjett(konverterBoolskFraStreng(hentVerdi("brukesTilBudsjett")));
        kategori.setBrukesTilFastePoster(konverterBoolskFraStreng(hentVerdi("brukesTilFasteposter")));
        kategori.setBrukesTilRegnskap(konverterBoolskFraStreng(hentVerdi("brukesTilRegnskap")));
        kategori.setRekkefoelge(parseInt(hentVerdi("rekkefoelge")));
        kategori.setErOppsummerendeUnderkategori(konverterBoolskFraStreng(hentVerdi("erOppsummerendeUnderkategori")));

        kategoriService.lagre(kategori);
        return kategori;

    }

    //TODO: Denne skal til CSVIMportassistentMal så snart den er testet
    public Boolean konverterBoolskFraStreng(String boolString) {
        return BoolskMester.konverterBoolskFraStreng(boolString);

//        if (boolString==null || boolString.isEmpty()) {
//            return null;
//        }
//
//        return switch (boolString.toLowerCase(Locale.ROOT)) {
//            case "yes", "1", "ja", "true", "sann" -> true;
//            case "no", "0", "nei", "false", "usann" -> false;
//            default -> null;
//        };
    }

    @Override
    public void ryddOppEtterImport() {
        //Koble underkategorier til hovedkategori
        List<Kategori> underkategoriList = kategoriService.finnAlleUnderkategorier();
        for (Kategori underkategori:underkategoriList){
            Kategori hovedkategori = kategoriService.finnHovedKategoriEtterTittel(underkategori.getTittel());
            underkategori.setHovedKategori(hovedkategori);
            kategoriService.lagre(underkategori);
        }

        Allvitekyklop.hent().getKategoriView().oppdaterSoekeomraadeFinnAlleRader();
    }
}
