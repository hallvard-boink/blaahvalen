package com.hallvardlaerum.verktoy.testing;

import com.hallvardlaerum.kategori.Kategori;
import com.hallvardlaerum.kategori.KategoriRetning;
import com.hallvardlaerum.kategori.KategoriType;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.periode.Periode;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import com.hallvardlaerum.periodepost.Periodepost;
import com.hallvardlaerum.verktoy.Allvitekyklop;

import java.util.List;
import java.util.Objects;

/**
 * Denne sjekker om tallene fra periodepostene stemmer med totalpostene
 */
public class PeriodeTester {
    private final Periode periode;


    public PeriodeTester(Periode periodeFaktisk) {
        this.periode = periodeFaktisk;
        Loggekyklop.bruk().forberedTilImportloggTilFil();

        boolean gikkBra = true;
        gikkBra = sjekk_sumPeriodeposter_vs_totalSum() && gikkBra;
        gikkBra = sjekk_sumMaanedsoversikter_vs_Aarsoversikt() && gikkBra;

        if (gikkBra) {
            Loggekyklop.bruk().loggINFO("All sjekking av summer i periode " + periode.hentBeskrivendeNavn() + " gikk bra.");
        }

        Loggekyklop.bruk().avsluttImportloggTilFil();
    }

    private boolean sjekk_sumMaanedsoversikter_vs_Aarsoversikt() {
        if (periode.getPeriodetypeEnum() != PeriodetypeEnum.AARSOVERSIKT) {
            return true;
        }

        List<Periode> maanedsoversikter = Allvitekyklop.hent().getMaanedsoversiktService().finnEtterPeriodetypeOgFraTilDato(PeriodetypeEnum.MAANEDSOVERSIKT, periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate());
        if (maanedsoversikter.size() != 12) {
            Loggekyklop.bruk().loggFEIL("Årsoversikten " + periode.hentBeskrivendeNavn() + " har feil antall månedsoversikter: " + maanedsoversikter.size());
            return false;
        }

        Periode testAarsoversikt = new Periode(); //til å lagre testsummer
        leggAlleSummerTiltestPeriode(testAarsoversikt, maanedsoversikter);

        return sammenlignSummerMedPeriode(testAarsoversikt);



    }

    private boolean sammenlignSummerMedPeriode(Periode testAarsoversikt) {
        boolean gikkBra = true;
        gikkBra = sammenlignSum("Budsjett Inn", periode.getSumBudsjettInntektInteger(), testAarsoversikt.getSumBudsjettInntektInteger()) && gikkBra;
        gikkBra = sammenlignSum("Budsjett Ut", periode.getSumBudsjettUtgifterInteger(), testAarsoversikt.getSumBudsjettUtgifterInteger()) && gikkBra;
        gikkBra = sammenlignSum("Regnskap Inn", periode.getSumRegnskapInntektInteger(), testAarsoversikt.getSumRegnskapInntektInteger()) && gikkBra;
        gikkBra = sammenlignSum("Regnskap Ut", periode.getSumRegnskapUtgifterInteger(), testAarsoversikt.getSumRegnskapUtgifterInteger()) && gikkBra;
        return gikkBra;
    }

    private boolean sammenlignSum(String feltnavn, Integer sumLagret, Integer sumUtregnet) {
        if (!Objects.equals(sumLagret, sumUtregnet)) {
            Loggekyklop.bruk().loggFEIL("Feltet " + feltnavn + " hadde lagret verdien " + sumLagret + ", men skulle antagelig hatt verdien " + sumUtregnet);
            return false;
        } else {
            return true;
        }
    }



    private void leggAlleSummerTiltestPeriode(Periode sumPeriode, List<Periode> perioder) {
        sumPeriode.setSumBudsjettInntektInteger(perioder.stream().mapToInt(p -> nulltil0(p.getSumBudsjettInntektInteger())).sum());
        sumPeriode.setSumBudsjettUtgifterInteger(perioder.stream().mapToInt(p -> nulltil0(p.getSumBudsjettUtgifterInteger())).sum());
        sumPeriode.setSumBudsjettResultatInteger(perioder.stream().mapToInt(p -> nulltil0(p.getSumBudsjettResultatInteger())).sum());

        sumPeriode.setSumRegnskapInntektInteger(perioder.stream().mapToInt(p -> nulltil0(p.getSumRegnskapInntektInteger())).sum());
        sumPeriode.setSumRegnskapUtgifterInteger(perioder.stream().mapToInt(p -> nulltil0(p.getSumRegnskapUtgifterInteger())).sum());
        sumPeriode.setSumRegnskapResultatInteger(perioder.stream().mapToInt(p -> nulltil0(p.getSumRegnskapResultatInteger())).sum());

        sumPeriode.setSumRegnskapInntektMedOverfoeringerInteger(perioder.stream().mapToInt(p -> nulltil0(p.getSumRegnskapInntektMedOverfoeringerInteger())).sum());
        sumPeriode.setSumRegnskapUtgifterMedOverfoeringerInteger(perioder.stream().mapToInt(p -> nulltil0(p.getSumRegnskapUtgifterMedOverfoeringerInteger())).sum());
        sumPeriode.setSumRegnskapInntektMedOverfoeringerInteger(perioder.stream().mapToInt(p -> nulltil0(p.getSumRegnskapInntektMedOverfoeringerInteger())).sum());

    }

    private Integer nulltil0 (Integer tall){
        if (tall==null) {
            return 0;
        } else {
            return tall;
        }
    }


    private boolean sjekk_sumPeriodeposter_vs_totalSum() {
        List<Periodepost> periodeposter = Allvitekyklop.hent().getMaanedsoversiktpostService().finnHovedperiodeposter(periode);
        //List<Periodepost> periodeposter = periode.getPeriodeposterList(); //Denne inneholder ofte gamle data

        int sumBudsjettInn = 0;
        int sumRegnskapInn = 0;
        int sumBudsjettUt = 0;
        int sumRegnskapUt = 0;
        int sumRegnskapInklOverfoeringerInn = 0;
        int sumRegnskapInklOverfoeringerUt = 0;
        int sumUkategorisertInn = 0;
        int sumUkategorisertUt = 0;

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
            Loggekyklop.bruk().loggINFO("Perioden " + periode.hentBeskrivendeNavn() + " hadde forskjell på " + (faktiskInteger-utregnetInteger) + " mellom sum fra poster: " + faktiskInteger + " og fra periodeposter: " + utregnetInteger + " av " + variabelnavnString);
            return 1;
        }
    }


}
