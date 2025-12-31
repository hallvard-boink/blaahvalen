package com.hallvardlaerum.periodepost.periodeoversiktpost;

import com.hallvardlaerum.grunndata.kategori.KategoriService;
import com.hallvardlaerum.libs.eksportimport.CSVImportassistentMal;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.periode.aarsoversikt.AarsoversiktService;
import com.hallvardlaerum.periodepost.Periodepost;
import com.hallvardlaerum.verktoy.Allvitekyklop;

import java.util.ArrayList;

public class PeriodeoversiktpostFraGamleBlaahvalenCSVImportassistent extends CSVImportassistentMal<Periodepost> {
    private PeriodeoversiktpostService periodeoversiktpostService;
    private AarsoversiktService aarsoversiktService;
    private KategoriService kategoriService;

    @Override
    public void forberedImport() {
        Loggekyklop.bruk().forberedTilImportloggTilFil();
        periodeoversiktpostService = Allvitekyklop.hent().getPeriodeoversiktpostService();
        aarsoversiktService = Allvitekyklop.hent().getAarsoversiktService();
        kategoriService = Allvitekyklop.hent().getKategoriService();
    }

    @Override
    public Periodepost konverterFraTekstRadOgLagre(ArrayList<String> feltnavnCSVArrayList, String[] celler) {
        lesInnFeltnavnogCeller(feltnavnCSVArrayList,celler);

        Periodepost periodeoversiktpost = periodeoversiktpostService.opprettEntitet();

        periodeoversiktpost.setPeriode(aarsoversiktService.finnAarsoversiktFraAarString(hentVerdi("år")));
        periodeoversiktpost.setTittelString(hentVerdi("tittel"));
        periodeoversiktpost.setBeskrivelseString(hentVerdi("beskrivelse"));
        periodeoversiktpost.setKategori(kategoriService.finnEtterUndertittel(hentVerdi("kategori.undertittel")));

        periodeoversiktpostService.lagre(periodeoversiktpost);
        return periodeoversiktpost;
    }

    @Override
    public void ryddOppEtterImport() {
        Loggekyklop.bruk().avsluttImportloggTilFil();
        Allvitekyklop.hent().getPeriodeoversiktpostView().oppdaterSoekeomraadeFinnAlleRader();
    }
}
