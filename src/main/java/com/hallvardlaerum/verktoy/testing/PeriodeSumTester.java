package com.hallvardlaerum.verktoy.testing;

import com.hallvardlaerum.kategori.Kategori;
import com.hallvardlaerum.kategori.KategoriRetning;
import com.hallvardlaerum.kategori.KategoriType;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.periode.Periode;
import com.hallvardlaerum.periodepost.Periodepost;
import com.hallvardlaerum.verktoy.Allvitekyklop;

import java.util.List;

/**
 * Denne sjekker om tallene fra periodepostene stemmer med totalpostene
 */
public class PeriodeSumTester {
    private final Periode periode;


    public PeriodeSumTester(Periode periodeFaktisk) {
        this.periode = periodeFaktisk;

        boolean gikkBra;
        gikkBra = sjekkSummer_sumPeriodeposter_vs_totalSum();

        if (gikkBra) {
            Loggekyklop.bruk().loggINFO("All sjekking av summer i periode " + periode.hentBeskrivendeNavn() + " gikk bra.");
        }
    }


    private boolean sjekkSummer_sumPeriodeposter_vs_totalSum() {
        List<Periodepost> periodeposter = Allvitekyklop.hent().getMaanedsoversiktpostService().finnEtterPeriode(periode);
        //List<Periodepost> periodeposter = periode.getPeriodeposterList(); //Denne inneholder ofte gamle data

        int sumBudsjettInn = 0;
        int sumRegnskapInn = 0;
        int sumBudsjettUt = 0;
        int sumRegnskapUt = 0;
        int sumRegnskapInklOverfoeringerInn = 0;
        int sumRegnskapInklOverfoeringerUt = 0;

        for (Periodepost periodepost : periodeposter) {
            if (periodepost.getKategori() != null) {
                Kategori k = periodepost.getKategori();
                if (k.getNivaa() > 0) {
                    Loggekyklop.bruk().loggINFO("Periodepostens kategor har nivå over 0: " + periodepost.hentBeskrivendeNavn());
                } else {
                    if (k.getKategoriType() == KategoriType.OVERFOERING) {
                        if (k.getKategoriRetning() == KategoriRetning.INN) {
                            sumRegnskapInklOverfoeringerInn += periodepost.getSumRegnskapInteger();
                        } else if (k.getKategoriRetning() == KategoriRetning.UT) {
                            sumRegnskapInklOverfoeringerUt += periodepost.getSumRegnskapInteger();
                        } else {
                            Loggekyklop.bruk().loggINFO("Kategoriens retning er tom eller feil " + periodepost.hentBeskrivendeNavn());
                        }
                    } else if (k.getKategoriType() != KategoriType.UKATEGORISERT) {
                        if (k.getKategoriRetning() == KategoriRetning.INN) {
                            sumBudsjettInn += periodepost.getSumBudsjettInteger() == null ? 0 : periodepost.getSumBudsjettInteger();
                            sumRegnskapInn += periodepost.getSumRegnskapInteger() == null ? 0 : periodepost.getSumRegnskapInteger();
                        } else if (k.getKategoriRetning() == KategoriRetning.UT) {
                            sumBudsjettUt += periodepost.getSumBudsjettInteger() == null ? 0 : periodepost.getSumBudsjettInteger();
                            sumRegnskapUt += periodepost.getSumRegnskapInteger() == null ? 0 : periodepost.getSumRegnskapInteger();
                        } else {
                            Loggekyklop.bruk().loggINFO("Kategoriens retning er tom eller feil " + periodepost.hentBeskrivendeNavn());
                        }
                    }
                }
            } else {
                Loggekyklop.bruk().loggINFO("Periodepost uten kategori: " + periodepost.hentBeskrivendeNavn());
            }
        }


        sumRegnskapInklOverfoeringerInn += sumRegnskapInn;
        sumRegnskapInklOverfoeringerUt += sumRegnskapUt;

        int antallFeil = 0;
        antallFeil += returner1HvisDeIkkeErLike("sumBudsjettInntekt", periode.getSumBudsjettInntektInteger(), sumBudsjettInn);
        antallFeil += returner1HvisDeIkkeErLike("sumBudsjettUtgift", periode.getSumBudsjettUtgifterInteger(), sumBudsjettUt);
        antallFeil += returner1HvisDeIkkeErLike("sumRegnskapInn", periode.getSumRegnskapInntektInteger(), sumRegnskapInn);
        antallFeil += returner1HvisDeIkkeErLike("sumRegnskapUt", periode.getSumRegnskapUtgifterInteger(), sumRegnskapUt);
        antallFeil += returner1HvisDeIkkeErLike("sumRegnskapInklOverfoeringInn", periode.getSumRegnskapInntektMedOverfoeringerInteger(), sumRegnskapInklOverfoeringerInn);
        antallFeil += returner1HvisDeIkkeErLike("sumRegnskapInklOverfoeringUt", periode.getSumRegnskapUtgifterMedOverfoeringerInteger(), sumRegnskapInklOverfoeringerUt);

        return antallFeil == 0;
    }

    private Integer returner1HvisDeIkkeErLike(String variabelnavnString, Integer faktiskInteger, Integer utregnetInteger) {
        if (faktiskInteger.equals(utregnetInteger)) {
            return 0;
        } else {
            Loggekyklop.bruk().loggINFO("Perioden " + periode.hentBeskrivendeNavn() + " hadde forskjell mellom sum fra poster: " + faktiskInteger + " og fra periodeposter: " + utregnetInteger + " av " + variabelnavnString);
            return 1;
        }
    }


}
