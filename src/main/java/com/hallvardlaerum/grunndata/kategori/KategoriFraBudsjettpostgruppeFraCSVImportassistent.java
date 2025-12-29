package com.hallvardlaerum.grunndata.kategori;

import com.hallvardlaerum.libs.eksportimport.CSVImportassistentMal;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import java.util.ArrayList;
import java.util.Optional;

/**
@deprecated
Denne klassen baserer seg på at budsjettpostgrupper skal brukes som underkategorier. Det er feil.
Bruk heller {@link KategoriFraBlaahvalenCSVImportassistent}
 */
@Deprecated
public class KategoriFraBudsjettpostgruppeFraCSVImportassistent extends CSVImportassistentMal<Kategori> {
    private KategoriService kategoriService;

    @Override
    public void forberedImport() {
        kategoriService = Allvitekyklop.hent().getKategoriService();
    }

    @Override
    public Kategori konverterFraTekstRadOgLagre(ArrayList<String> feltnavnCSVArrayList, String[] celler) {
        lesInnFeltnavnogCeller(feltnavnCSVArrayList, celler);

        String tittelString = hentVerdi("Kategori");
        String undertittelString = hentVerdi("Tittel");

        Optional<Kategori> kategoriOptional;
        if (undertittelString==null || undertittelString.isEmpty()) {
            kategoriOptional = kategoriService.finnEtterTittel(tittelString);
        } else {
            kategoriOptional = kategoriService.finnEtterTittelOgUnderTittel(tittelString, undertittelString);
        }

        if (kategoriOptional.isEmpty()) {
            Kategori kategori = kategoriService.opprettEntitet();

            kategori.setTittel(tittelString);
            kategori.setUndertittel(undertittelString);
            kategori.setBeskrivelse(hentVerdi("Beskrivelse"));
            kategori.setErAktiv(true);
            kategori.setKategoriRetning(KategoriRetning.UT);
            kategori.setBrukesTilBudsjett(true);
            kategori.setBrukesTilFastePoster(true);
            kategori.setBrukesTilRegnskap(true);
            //kategori.setKategoriType(KategoriType.DETALJERT);

            kategoriService.lagre(kategori);
            return kategori;

        } else {
            return null;
        }

    }

    @Override
    public void ryddOppEtterImport() {
        Allvitekyklop.hent().getKategoriView().oppdaterSoekeomraadeFinnAlleRader();
    }
}
