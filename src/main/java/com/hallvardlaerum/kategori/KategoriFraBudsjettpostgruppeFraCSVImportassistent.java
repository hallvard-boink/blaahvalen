package com.hallvardlaerum.kategori;

import com.hallvardlaerum.libs.eksportimport.CSVImportassistentMal;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import java.util.ArrayList;
import java.util.List;
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


        List<Kategori> kategoriList;
        if (undertittelString==null || undertittelString.isEmpty()) {
             kategoriList = kategoriService.finnEtterTittel(tittelString);
        } else {
            kategoriList = kategoriService.finnEtterTittelOgUnderTittel(tittelString, undertittelString);
        }

        if (kategoriList.isEmpty()) {
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
            if (kategoriList.size()>1) {
                Loggekyklop.bruk().loggADVARSEL("Fant mer enn en kategori med tittel " + tittelString + " og undertittel " + undertittelString + ". Bruker første i lista, og fortsetter.");
            }
            return kategoriList.getFirst();
        }

    }

    @Override
    public void ryddOppEtterImport() {
        Allvitekyklop.hent().getKategoriView().oppdaterSoekeomraadeFinnAlleRader();
    }
}
