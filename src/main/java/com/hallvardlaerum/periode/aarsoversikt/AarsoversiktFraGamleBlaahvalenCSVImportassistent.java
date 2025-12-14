package com.hallvardlaerum.periode.aarsoversikt;

import com.hallvardlaerum.libs.eksportimport.CSVImportassistentMal;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.felter.Datokyklop;
import com.hallvardlaerum.periode.Periode;
import com.hallvardlaerum.verktoy.Allvitekyklop;

import java.time.LocalDate;
import java.util.ArrayList;

public class AarsoversiktFraGamleBlaahvalenCSVImportassistent extends CSVImportassistentMal<Periode> {
    private AarsoversiktService aarsoversiktService;
    private AarsoversiktView aarsoversiktView;

    @Override
    public void forberedImport() {
        aarsoversiktService = Allvitekyklop.hent().getAarsoversiktService();
        aarsoversiktView = Allvitekyklop.hent().getAarsoversiktView();
        Loggekyklop.bruk().forberedTilImportloggTilFil();
    }

    @Override
    public Periode konverterFraTekstRadOgLagre(ArrayList<String> feltnavnCSVArrayList, String[] celler) {
        super.lesInnFeltnavnogCeller(feltnavnCSVArrayList, celler);

        Periode periode = aarsoversiktService.opprettEntitet();

        Integer aarInteger = parseInt(hentVerdi("Aar"));
        if (aarInteger!=null && aarInteger>0) {
            LocalDate datoFra = LocalDate.of(aarInteger,1,1);
            periode.setDatoFraLocalDate(datoFra);
            periode.setDatoTilLocalDate(Datokyklop.hent().finnSisteIAaret(datoFra));
        } else {
            Loggekyklop.bruk().loggINFO("Årsoversikten hadde år " + hentVerdi("Aar") + ", og ble derfor ikke importert.");
        }

        periode.setBeskrivelseString(hentVerdi("Beskrivelse"));
        aarsoversiktService.lagre(periode);

        return periode;
    }

    @Override
    public void ryddOppEtterImport() {
        aarsoversiktView.oppdaterSoekeomraade();
        Loggekyklop.bruk().avsluttImportloggTilFil();
    }
}
