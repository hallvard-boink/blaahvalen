package com.hallvardlaerum.verktoy.testing;

import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.periode.Periode;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import com.hallvardlaerum.periodepost.Periodepost;
import com.hallvardlaerum.periodepost.PeriodepostTypeEnum;
import com.hallvardlaerum.post.Post;
import com.hallvardlaerum.post.PostklasseEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;

import java.util.List;

public class PeriodepostTester {
    public Periodepost periodepost;
    public Periode periode;
    public Integer vellykkedeTesterInteger;

    public PeriodepostTester(Periodepost periodepost) {
        this.periodepost = periodepost;
        Loggekyklop.bruk().settNivaaINFO();

        periode = periodepost.getPeriode();
        vellykkedeTesterInteger = 0;

        sjekkPeriodetypeVersusPeriodeposttype();


        switch (periodepost.getPeriodepostTypeEnum()) {
            case AARSOVERSIKTPOST -> sjekkPoster_Aarsoversiktpost();
            case MAANEDSOVERSIKTPOST -> sjekkPoster_Maanedsoversiktpost();
            case PERIODEOVERSIKTPOST -> sjekkPoster_Kostnadspakke();
        }

        Loggekyklop.bruk().loggINFO("Antall vellykkede tester: " + vellykkedeTesterInteger + " av 2");

    }

    private void sjekkPeriodetypeVersusPeriodeposttype() {

        if (periode==null) {
            Loggekyklop.bruk().loggFEIL("Periodeposten " + periodepost.hentBeskrivendeNavn() + " er ikke koblet til periode.");
            return;
        }

        if (periode.getPeriodetypeEnum()==null) {
            Loggekyklop.bruk().loggFEIL("Periodeposten " + periodepost.hentBeskrivendeNavn() + " sin periode har ikke PeriodetypeEnum.");
            return;
        }

        if (periodepost.getPeriodepostTypeEnum() == null) {
            Loggekyklop.bruk().loggFEIL("Periodeposten " + periodepost.hentBeskrivendeNavn() + " har ikke satt PeriodeposttypeEnum.");
            return;
        }

        if (periodepost.getPeriodepostTypeEnum() == PeriodepostTypeEnum.AARSOVERSIKTPOST && periode.getPeriodetypeEnum()!=PeriodetypeEnum.AARSOVERSIKT) {
            Loggekyklop.bruk().loggFEIL("Periodeposten " + periodepost.hentBeskrivendeNavn() + " har periodeposttype " + periodepost.getPeriodepostTypeEnum() +
                ", mens tilknyttet periode har periodetype " + periode.getPeriodetypeEnum() + ". Den skal være AARSOVERSIKT");
            return;

        } else if (periodepost.getPeriodepostTypeEnum() == PeriodepostTypeEnum.MAANEDSOVERSIKTPOST && periode.getPeriodetypeEnum() != PeriodetypeEnum.MAANEDSOVERSIKT) {
            Loggekyklop.bruk().loggFEIL("Periodeposten " + periodepost.hentBeskrivendeNavn() + " har periodeposttype " + periodepost.getPeriodepostTypeEnum() +
                ", mens tilknyttet periode har periodetype " + periode.getPeriodetypeEnum() + ". Den skal være MAANEDSOVERSIKT");
            return;

        } else if (periodepost.getPeriodepostTypeEnum() == PeriodepostTypeEnum.PERIODEOVERSIKTPOST && periode.getPeriodetypeEnum() != PeriodetypeEnum.AARSOVERSIKT) {
            Loggekyklop.bruk().loggFEIL("Periodeposten " + periodepost.hentBeskrivendeNavn() + " har periodeposttype " + periodepost.getPeriodepostTypeEnum() +
                ", mens tilknyttet periode har periodetype " + periode.getPeriodetypeEnum() + ". Den skal være AARSOVERSIKT");
            return;
        }

        Loggekyklop.bruk().loggINFO("Test sjekkPeriodetypeVersusPeriodeposttype OK");
        vellykkedeTesterInteger += 1;

    }

    private void sjekkPoster_Kostnadspakke() {

    }

    private void sjekkPoster_Maanedsoversiktpost() {

    }


    private void sjekkPoster_Aarsoversiktpost() {
        boolean gikkBra = true;
        List<Post> normalposter = Allvitekyklop.hent().getNormalpostService().finnEtterFraDatoTilDatoPostklasseHovedkategori(
                    periodepost.getPeriode().getDatoFraLocalDate(),
                    periodepost.getPeriode().getDatoTilLocalDate(),
                    PostklasseEnum.NORMALPOST,
                    periodepost.getKategori()
            );

        List<Post> budsjettposter = Allvitekyklop.hent().getBudsjettpostService().finnEtterFraDatoTilDatoPostklasseHovedkategori(
                    periodepost.getPeriode().getDatoFraLocalDate(),
                    periodepost.getPeriode().getDatoTilLocalDate(),
                    PostklasseEnum.BUDSJETTPOST,
                    periodepost.getKategori()
            );


        for (Post normalpost:normalposter) {
            //sjekk postklasse
            //sjekk kategori != null og kategoritittel
            //sjekk dato
            //sjekk

        }


        if (gikkBra) {
            vellykkedeTesterInteger += 1;
        }
    }


}
