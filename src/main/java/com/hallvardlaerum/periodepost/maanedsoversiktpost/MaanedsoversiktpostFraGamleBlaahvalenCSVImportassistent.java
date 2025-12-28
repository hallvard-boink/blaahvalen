package com.hallvardlaerum.periodepost.maanedsoversiktpost;

import com.hallvardlaerum.grunndata.kategori.Kategori;
import com.hallvardlaerum.grunndata.kategori.KategoriService;
import com.hallvardlaerum.libs.eksportimport.CSVImportassistentMal;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.periode.Periode;
import com.hallvardlaerum.periode.maanedsoversikt.MaanedsoversiktService;
import com.hallvardlaerum.periodepost.Periodepost;
import com.hallvardlaerum.verktoy.Allvitekyklop;

import java.util.ArrayList;

public class MaanedsoversiktpostFraGamleBlaahvalenCSVImportassistent extends CSVImportassistentMal<Periodepost> {
    private MaanedsoversiktpostService maanedsoversiktpostService;
    private MaanedsoversiktService maanedsoversiktService;
    private KategoriService kategoriService;

    @Override
    public void forberedImport() {
        Loggekyklop.bruk().forberedTilImportloggTilFil();
        maanedsoversiktpostService = Allvitekyklop.hent().getMaanedsoversiktpostService();
        maanedsoversiktService = Allvitekyklop.hent().getMaanedsoversiktService();
        kategoriService = Allvitekyklop.hent().getKategoriService();
    }

    @Override
    public Periodepost konverterFraTekstRadOgLagre(ArrayList<String> feltnavnCSVArrayList, String[] celler) {
        super.lesInnFeltnavnogCeller(feltnavnCSVArrayList,celler);

        Periodepost maanedsoversiktpost = maanedsoversiktpostService.opprettEntitet();

        Periode maanedsoversikt = maanedsoversiktService.finnMaanedsoversiktFraAarMnd(hentVerdi("MaanedsoversiktKortnavn"));
        if (maanedsoversikt==null) {
            Loggekyklop.bruk().loggINFO("Fant ikke månedsoversikt med aar-mnd " + hentVerdi("MaanedsoversiktKortnavn") + ", lagrer ikke. Importradstreng: " + hentImportRadString());
            return null;
        } else {
            maanedsoversiktpost.setPeriode(maanedsoversikt);
        }

        Kategori kategori = kategoriService.finnHovedKategoriEtterTittel(hentVerdi("KategoriNavn"));
        if (kategori==null) {
            Loggekyklop.bruk().loggINFO("Fant ikke kategori med navn " + hentVerdi("KategoriNavn") + " importerer ikke raden. Importrad: " + hentImportRadString());
        } else {
            maanedsoversiktpost.setKategori(kategori);
        }

        maanedsoversiktpost.setBeskrivelseString(hentVerdi("Kommentar"));

        maanedsoversiktpostService.lagre(maanedsoversiktpost);

        return maanedsoversiktpost;
    }

    @Override
    public void ryddOppEtterImport() {
        Allvitekyklop.hent().getMaanedsoversiktpostView().oppdaterSoekeomraadeFinnAlleRader();
        Loggekyklop.bruk().avsluttImportloggTilFil();
    }
}
