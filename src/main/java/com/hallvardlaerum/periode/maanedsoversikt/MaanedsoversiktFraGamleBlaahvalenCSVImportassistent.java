package com.hallvardlaerum.periode.maanedsoversikt;

import com.hallvardlaerum.libs.eksportimport.CSVImportassistentMal;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.felter.Datokyklop;
import com.hallvardlaerum.periode.Periode;
import com.hallvardlaerum.verktoy.Allvitekyklop;

import java.time.LocalDate;
import java.util.ArrayList;

public class MaanedsoversiktFraGamleBlaahvalenCSVImportassistent extends CSVImportassistentMal<Periode> {
    private MaanedsoversiktService maanedsoversiktService;
    private MaanedsoversiktView maanedsoversiktView;

    @Override
    public void forberedImport() {
        maanedsoversiktService = Allvitekyklop.hent().getMaanedsoversiktService();
        maanedsoversiktView = Allvitekyklop.hent().getMaanedsoversiktView();
        Loggekyklop.bruk().forberedTilImportloggTilFil();
    }

    @Override
    public Periode konverterFraTekstRadOgLagre(ArrayList<String> feltnavnCSVArrayList, String[] celler) {
        super.lesInnFeltnavnogCeller(feltnavnCSVArrayList, celler);

        Periode periode = maanedsoversiktService.opprettEntitet();
        Integer aarInteger = parseInt(hentVerdi("Aar"));
        Integer maanedInteger = parseInt(hentVerdi("Maaned nr"));
        if (aarInteger==null || aarInteger<=0 || maanedInteger==null || maanedInteger<=0 ) {
            Loggekyklop.bruk().loggINFO("Månedsoversikten ble ikke importert, fordi den hadde år " + hentVerdi("Aar") + " og måned nr " + hentVerdi("Maaned nr"));
            return null;
        } else {
            LocalDate fraLocalDate = LocalDate.of(aarInteger, maanedInteger, 1);
            LocalDate tilLocalDate = Datokyklop.hent().finnSisteIMaaneden(fraLocalDate);
            periode.setDatoFraLocalDate(fraLocalDate);
            periode.setDatoTilLocalDate(tilLocalDate);
        }

        periode.setBeskrivelseString(hentVerdi("Vurdering"));


        maanedsoversiktService.lagre(periode);

        return periode;
    }

    @Override
    public void ryddOppEtterImport() {
        maanedsoversiktView.oppdaterSoekeomraade();
        Loggekyklop.bruk().avsluttImportloggTilFil();
    }
}
