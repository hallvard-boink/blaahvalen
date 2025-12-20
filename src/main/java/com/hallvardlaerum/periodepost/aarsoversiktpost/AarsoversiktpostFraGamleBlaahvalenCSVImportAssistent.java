package com.hallvardlaerum.periodepost.aarsoversiktpost;

import com.hallvardlaerum.grunndata.kategori.Kategori;
import com.hallvardlaerum.grunndata.kategori.KategoriService;
import com.hallvardlaerum.libs.eksportimport.CSVImportassistentMal;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.periode.Periode;
import com.hallvardlaerum.periode.aarsoversikt.AarsoversiktService;
import com.hallvardlaerum.periodepost.Periodepost;
import com.hallvardlaerum.verktoy.Allvitekyklop;

import java.util.ArrayList;
import java.util.Arrays;

public class AarsoversiktpostFraGamleBlaahvalenCSVImportAssistent extends CSVImportassistentMal<Periodepost> {
    private AarsoversiktpostService aarsoversiktpostService;
    private AarsoversiktService aarsoversiktService;
    private KategoriService kategoriService;

    @Override
    public void forberedImport() {
        aarsoversiktpostService = Allvitekyklop.hent().getAarsoversiktpostService();
        aarsoversiktService = Allvitekyklop.hent().getAarsoversiktService();
        kategoriService = Allvitekyklop.hent().getKategoriService();

        Loggekyklop.bruk().forberedTilImportloggTilFil();

    }

    @Override
    public Periodepost konverterFraTekstRadOgLagre(ArrayList<String> feltnavnCSVArrayList, String[] celler) {
        super.lesInnFeltnavnogCeller(feltnavnCSVArrayList,celler);
        Periodepost aarsoversiktpost = aarsoversiktpostService.opprettEntitet();

        Periode aarsoversikt = aarsoversiktService.PeriodefinnAarsoversiktFraAarString(hentVerdi("Aarsoversikt"));
        if (aarsoversikt==null) {
            Loggekyklop.bruk().loggINFO("Fant ikke årsoversikt som passet, importerer ikke raden " + hentImportRadString());
            return null;
        } else {
            aarsoversiktpost.setPeriode(aarsoversikt);
        }

        Kategori kategori = kategoriService.finnHovedKategoriEtterTittel(hentVerdi("KategoriNavn"));
        if (kategori==null) {
            Loggekyklop.bruk().loggINFO("Fant ikke kategorien " + hentVerdi("KategoriNavn") + ", importerer ikke raden " + hentImportRadString());
            return null;
        } else {
            aarsoversiktpost.setKategori(kategori);
        }

        aarsoversiktpost.setBeskrivelseString(hentVerdi("Kommentar"));

        aarsoversiktpostService.lagre(aarsoversiktpost);
        return aarsoversiktpost;
    }

    @Override
    public void ryddOppEtterImport() {
        Allvitekyklop.hent().getAarsoversiktpostView().oppdaterSoekeomraade();
        Loggekyklop.bruk().avsluttImportloggTilFil();
    }

    public String lagCellerStringFraArray(String[] celler) {
        StringBuilder sb = new StringBuilder();
        for (String celle:celler) {
            sb.append(celle).append("|");
        }
        return sb.toString();
    }
}
