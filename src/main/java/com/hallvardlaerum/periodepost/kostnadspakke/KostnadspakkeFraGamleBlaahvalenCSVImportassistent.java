package com.hallvardlaerum.periodepost.kostnadspakke;

import com.hallvardlaerum.kategori.KategoriService;
import com.hallvardlaerum.libs.eksportimport.CSVImportassistentMal;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.periode.aarsoversikt.AarsoversiktService;
import com.hallvardlaerum.periodepost.Periodepost;
import com.hallvardlaerum.verktoy.Allvitekyklop;

import java.util.ArrayList;

public class KostnadspakkeFraGamleBlaahvalenCSVImportassistent extends CSVImportassistentMal<Periodepost> {
    private KostnadspakkeService kostnadspakkeService;
    private AarsoversiktService aarsoversiktService;
    private KategoriService kategoriService;

    @Override
    public void forberedImport() {
        Loggekyklop.bruk().forberedTilImportloggTilFil();
        kostnadspakkeService = Allvitekyklop.hent().getKostnadspakkeService();
        aarsoversiktService = Allvitekyklop.hent().getAarsoversiktService();
        kategoriService = Allvitekyklop.hent().getKategoriService();
    }

    @Override
    public Periodepost konverterFraTekstRadOgLagre(ArrayList<String> feltnavnCSVArrayList, String[] celler) {
        lesInnFeltnavnogCeller(feltnavnCSVArrayList,celler);

        Periodepost periodeoversiktpost = kostnadspakkeService.opprettEntitet();

        periodeoversiktpost.setPeriode(aarsoversiktService.finnAarsoversiktFraAarString(hentVerdi("år")));
        periodeoversiktpost.setTittelString(hentVerdi("tittel"));
        periodeoversiktpost.setBeskrivelseString(hentVerdi("beskrivelse"));
        periodeoversiktpost.setKategori(kategoriService.finnEtterUndertittel(hentVerdi("kategori.undertittel")));

        kostnadspakkeService.lagre(periodeoversiktpost);
        return periodeoversiktpost;
    }

    @Override
    public void ryddOppEtterImport() {
        Loggekyklop.bruk().avsluttImportloggTilFil();
        Allvitekyklop.hent().getPeriodeoversiktpostView().oppdaterSoekeomraadeFinnAlleRader();
    }
}
