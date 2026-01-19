package com.hallvardlaerum.verktoy;

import com.hallvardlaerum.kategori.Kategori;
import com.hallvardlaerum.kategori.KategoriRetning;
import com.hallvardlaerum.kategori.KategoriService;
import com.hallvardlaerum.kategori.KategoriType;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.felter.Datokyklop;
import com.hallvardlaerum.periodepost.Periodepost;
import com.hallvardlaerum.periodepost.PeriodepostRepository;
import com.hallvardlaerum.periodepost.PeriodepostServiceMal;
import com.hallvardlaerum.post.Post;
import com.hallvardlaerum.post.PostRepository;
import com.hallvardlaerum.post.normalpost.NormalpostService;
import com.hallvardlaerum.post.normalpost.NormalposttypeEnum;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class DataDoktor {
    private KategoriService kategoriService;
    private NormalpostService normalpostService;

    public void vaskData(){
        Loggekyklop.bruk().settNivaaINFO();

        kategoriService = Allvitekyklop.hent().getKategoriService();
        normalpostService = Allvitekyklop.hent().getNormalpostService();

        leggTilManglendeKategorier();
        slettPeriodeposterMedKategoriType_SKAL_IKKE_KATEGORISERES();
        reparerNormalposterSomSkulleVaertUtelatt();
        korrigerNormalposter();

        Loggekyklop.bruk().loggINFO("Datavask ferdig.");
    }



    private static void reparerNormalposterSomSkulleVaertUtelatt() {
        PostRepository postRepository = Allvitekyklop.hent().getPostRepository();
        List<Post> poster = postRepository.finnPosterSomSkalKorrigeres_FeilNormalposttypeSelvOmKategoriErType4SkalIkkekategoriseres();
        for (Post post : poster) {
            post.setNormalposttypeEnum(NormalposttypeEnum.UTELATES); // 2
        }
        postRepository.saveAll(poster);
        postRepository.flush();
    }

    private static void slettPeriodeposterMedKategoriType_SKAL_IKKE_KATEGORISERES() {
        PeriodepostServiceMal periodepostService = Allvitekyklop.hent().getAarsoversiktpostService();
        PeriodepostRepository periodepostRepository = Allvitekyklop.hent().getPeriodepostRepository();
        List<Periodepost> periodeposter = periodepostRepository.finnEtterKategoriType(KategoriType.SKAL_IKKE_KATEGORISERES);
        periodepostService.slettAlle(periodeposter);
        periodepostRepository.flush();
    }


    private void korrigerNormalposter() {

        settInnUt("2022-07-13", "14.07 EXTRA TVERLANDE KVILUT 24 TVERLANDET", "blomster,søppelsekk", null, 1394);
        settInnUt("2018-11-14", "*1686 13.11 NOK 736.00 RUTER AS - APP Kurs: 1.0000", "", null, 736);
        settInnUt("2018-03-12", "Tilbakeføring Betalt: 10.03.18", "Overført fra sparekto", null, 12000);
        settInnUt("2018-04-10", "Narvesen", "Overført til kontanter", null, 50);

        fjernInnPaaKonto("2019-04-24", "Matildes forsikring Betalt: 24.04.19");
        fjernInnPaaKonto("2021-09-09", "08.09 Nokas 7-Eleven Grefsen 0492 Oslo");
        fjernInnPaaKonto("2019-04-11", "Fast sparebeløp Betalt: 11.04.19");
        fjernInnPaaKonto("2019-03-11", "Fast sparebeløp Betalt: 11.03.19");

        settkategoriAlle("2022-10-31", "28.10 Nokas Storo senter MB 2 0485 Oslo", "Overføring ut");

        settKategori("2020-09-28", "26.09 JERNIA STADION SOGNSVN. 75 OSLO", "Andre planlagte utgifter");
        settKategori("2019-08-15", "Skatt Betalt: 15.08.19", "Andre inntekter");
        settKategori("2018-04-10", "Narvesen", "Restaurant, kiosk og kafe");
        settKategori("2018-12-31", "KREDITRENTER", "Andre inntekter");
        settKategori("2021-12-26", "Til: Marek Lukaszewicz", "Porto og gebyrer");
        settKategori("2018-06-04", "Fra: Frances Emely Opsahl Taylor", "Refusjon inn");
        settKategori("2020-02-06", "*1854 05.02 NOK 75.00 HELTHJEM Kurs: 1.0000", "Refusjon inn");
        settKategori("2019-02-04", "03.02 BUTIKKDRIFT HER GREFSENVEIEN OSLO", "[Kategori ikke funnet - Ut]");

        settKategori("2018-12-03", "*1686 30.11 NOK 100.00 Vipps Kurs: 1.0000", "[Kategori ikke funnet - Ut]");
        settKategori("2018-12-19", "Nettgiro til: 1503.41.11534 Betalt: 19.12.18", "[Kategori ikke funnet - Ut]");
        settKategori("2018-12-10", "*1686 08.12 NOK 300.00 Vipps *Godt Og Hjemme Kurs: 1.0000", "[Kategori ikke funnet - Ut]");
        settKategori("2018-12-13", "12.12 TORGGATA FRUKT TORGGT. 21 OSLO", "[Kategori ikke funnet - Ut]");
        settKategori("2018-12-20", "19.12 NEDRE LØKKA THORVALD MEY OSLO", "[Kategori ikke funnet - Ut]");
        settKategori("2018-12-10", "08.12 Bakken Øvre Går BAKKEN ØVRE LØTEN", "[Kategori ikke funnet - Ut]");
        settKategori("2018-12-12", "Nettgiro til: 6129.06.31962 Betalt: 12.12.18", "[Kategori ikke funnet - Ut]");

        settKategori("2018-11-02", "01.11 POINT 5019014 OSL LUFTHAVN GARDERMOEN", "[Kategori ikke funnet - Ut]");
        settKategori("2018-11-02", "*1686 30.10 NOK 300.00 Vipps *Engebraaten Bo Kurs: 1.0000", "[Kategori ikke funnet - Ut]");
        settKategori("2018-11-07", "06.11 777 FLESLAND GLADENGVEIEN OSLO", "[Kategori ikke funnet - Ut]");
        settKategori("2018-11-12", "10.11 ARK, LINDERUD ERICH MOGENS OSLO", "[Kategori ikke funnet - Ut]");
        settKategori("2018-11-13", "*1686 09.11 NOK 402.50 Pervaco AS Kurs: 1.0000", "[Kategori ikke funnet - Ut]");

        settKategori("2018-10-05", "04.10 MARITA BRUKTHAN MARKVEIEN 67 OSLO", "[Kategori ikke funnet - Ut]");
        settKategori("2018-10-08", "*1686 02.10 NOK 24.00 ULLEVAL SYKEHUS Kurs: 1.0000", "[Kategori ikke funnet - Ut]");
        settKategori("2018-10-15", "Nettgiro til: 1214.36.85320 Betalt: 15.10.18", "[Kategori ikke funnet - Ut]");
        settKategori("2018-10-15", "14.10 11005 SENTRUM P C.J.HAMBROS OSLO", "[Kategori ikke funnet - Ut]");
        settKategori("2018-10-23", "*1686 20.10 SEK 49.86 Wirelane Sweden Kurs: 0.9340", "[Kategori ikke funnet - Ut]");
        settKategori("2018-10-23", "*9926 20.10 SEK 500.00 BANKOMAT Kopcentrum N Kurs: 0.9341", "[Kategori ikke funnet - Ut]");
        settKategori("2018-10-23", "*1686 20.10 SEK 0.03 Wirelane Sweden Kurs: 1.0000", "[Kategori ikke funnet - Ut]");

        settKategori("2018-07-02", "Nettgiro til: 1506.08.80864 Betalt: 01.07.18", "[Kategori ikke funnet - Ut]");
        settKategori("2018-07-03", "*1686 30.06 SEK 158.90 OOB NORDBY 193 Kurs: 0.9298", "[Kategori ikke funnet - Ut]");
        settKategori("2018-07-16", "16.07 BLG SANDAKER SE SANDAKERVEIE OSLO", "[Kategori ikke funnet - Ut]");
        settKategori("2018-07-16", "Nettgiro til: 1654.10.82755 Betalt: 16.07.18", "[Kategori ikke funnet - Ut]");
        settKategori("2018-08-02", "*1686 29.07 NOK 75.00 KINGWINN AS Kurs: 1.0000", "[Kategori ikke funnet - Ut]");
        settKategori("2018-08-10", "Nettgiro til: 1150.10.34535 Betalt: 10.08.18", "[Kategori ikke funnet - Ut]");
        settKategori("2018-08-14", "13.08 MATKROKEN . TRONES", "[Kategori ikke funnet - Ut]");
        settKategori("2018-09-04", "04.09 NORLI UNIVERSIT UNIVERSITETS OSLO", "[Kategori ikke funnet - Ut]");
        settKategori("2018-09-11", "10.09 PANDURO SANDAKERVN 3 OSLO", "[Kategori ikke funnet - Ut]");
        settKategori("2018-09-11", "10.09 BLG SANDAKER SE SANDAKERVEIE OSLO", "[Kategori ikke funnet - Ut]");
        settKategori("2018-09-21", "*1686 18.09 NOK 5.00 Vipps *Engebraaten Bo Kurs: 1.0000", "[Kategori ikke funnet - Ut]");


    }

    private void leggTilManglendeKategorier() {
        kategoriService.finnEllerOpprettFraKategoriTypeOgKategoriretningOgNivaa(KategoriType.UKATEGORISERT, KategoriRetning.INN, 0);
        kategoriService.finnEllerOpprettFraKategoriTypeOgKategoriretningOgNivaa(KategoriType.UKATEGORISERT, KategoriRetning.UT, 0);
        kategoriService.finnEllerOpprettFraKategoriTypeOgKategoriretningOgNivaa(KategoriType.KATEGORI_IKKE_FUNNET, KategoriRetning.INN, 0);
        kategoriService.finnEllerOpprettFraKategoriTypeOgKategoriretningOgNivaa(KategoriType.KATEGORI_IKKE_FUNNET, KategoriRetning.INN, 1);
        kategoriService.finnEllerOpprettFraKategoriTypeOgKategoriretningOgNivaa(KategoriType.KATEGORI_IKKE_FUNNET, KategoriRetning.UT, 0);
        kategoriService.finnEllerOpprettFraKategoriTypeOgKategoriretningOgNivaa(KategoriType.KATEGORI_IKKE_FUNNET, KategoriRetning.UT, 1);

    }


    private void settInnUt(String datoString, String tekstFraBankenString, String beskrivelseString, Integer innInteger, Integer utInteger) {
        Post post = finnPostFraDatoOgTekstFraBankenOgBeskrivelse(datoString, tekstFraBankenString, beskrivelseString);
        if (post == null) {
            return;
        }

        boolean skalEndres = false;
        if (innInteger != null) {
            if (post.getInnPaaKontoInteger() == null || !post.getInnPaaKontoInteger().equals(innInteger)) {
                skalEndres = true;
            }
        }
        if (utInteger != null) {
            if (post.getUtFraKontoInteger() == null || !post.getUtFraKontoInteger().equals(utInteger)) {
                skalEndres = true;
            }
        }


        if (skalEndres) {
            String gammelVerdi = " inn:" + post.getInnPaaKontoInteger() + " ut:" + post.getUtFraKontoInteger();
            post.setInnPaaKontoInteger(innInteger);
            post.setUtFraKontoInteger(utInteger);
            normalpostService.lagre(post);
            Loggekyklop.bruk().loggINFO(post.hentBeskrivendeNavn() + ": Endret inn/ut fra " + gammelVerdi + " til inn:" + post.getInnPaaKontoInteger() + " ut:" + post.getUtFraKontoInteger());
        }

    }

    private Post finnPostFraDatoOgTekstFraBankenOgBeskrivelse(String datoString, String tekstFraBankenString, String beskrivelseString) {
        if (datoString == null || tekstFraBankenString == null) {
            return null;
        }

        LocalDate dato = Datokyklop.hent().opprettDatoSomYYYY_MM_DD(datoString);
        if (dato == null) {
            Loggekyklop.bruk().loggADVARSEL("Klarte ikke å konvertere datoString " + datoString + ", avbryter oppdatering.");
            return null;
        }

        return Allvitekyklop.hent().getNormalpostService().finnEtterDatoOgTekstfrabankenOgBeskrivelseNormalposttypeenum(dato, tekstFraBankenString, beskrivelseString);
    }

    private void fjernInnPaaKonto(String datoString, String tekstFraBankenString) {
        LocalDate dato = Datokyklop.hent().opprettDatoSomYYYY_MM_DD(datoString);
        if (dato == null) {
            Loggekyklop.bruk().loggADVARSEL("Klarte ikke å konvertere datoString " + datoString + ", avbryter oppdatering.");
            return;
        }

        Post post = finnPostFraDatoOgTekstFraBanken(dato, tekstFraBankenString);
        if (post.getInnPaaKontoInteger()!=null){
            String gammelVerdi = " inn:" + post.getInnPaaKontoInteger() + " ut:" + post.getUtFraKontoInteger();
            post.setInnPaaKontoInteger(null);
            normalpostService.lagre(post);
            Loggekyklop.bruk().loggINFO(post.hentBeskrivendeNavn() + ": Fjernet innPaaKonto, fra (" + gammelVerdi + ") til (inn:" + post.getInnPaaKontoInteger() + " ut:" + post.getUtFraKontoInteger() + ")");
        }

    }


    private void byttInnUt(String datoString, String tekstFraBankenString) {
        LocalDate dato = Datokyklop.hent().opprettDatoSomYYYY_MM_DD(datoString);
        if (dato == null) {
            Loggekyklop.bruk().loggADVARSEL("Klarte ikke å konvertere datoString " + datoString + ", avbryter oppdatering.");
            return;
        }

        Post post = finnPostFraDatoOgTekstFraBanken(dato, tekstFraBankenString);
        if (post != null) {
            String beskrivendenavnString = post.hentBeskrivendeNavn();

            Integer innInteger = post.getInnPaaKontoInteger();
            Integer utInteger = post.getUtFraKontoInteger();
            post.setInnPaaKontoInteger(utInteger);
            post.setUtFraKontoInteger(innInteger);

            // Erstatte 0 med null
            if (post.getInnPaaKontoInteger() != null && post.getInnPaaKontoInteger() == 0 && post.getUtFraKontoInteger() != null && post.getUtFraKontoInteger() > 0) {
                post.setInnPaaKontoInteger(null);
            } else if (post.getUtFraKontoInteger() != null && post.getUtFraKontoInteger() == 0 && post.getInnPaaKontoInteger() != null && post.getInnPaaKontoInteger() > 0) {
                post.setUtFraKontoInteger(null);
            }

            normalpostService.lagre(post);
            Loggekyklop.bruk().loggINFO(beskrivendenavnString + ": Byttet om på inn og ut : " + innInteger + " <-> " + utInteger +
                    " til inn:" + post.getInnPaaKontoInteger() + " ut:" + post.getUtFraKontoInteger());
        }
    }


    private void settKategori(String datoString, String tekstFraBankenString, String tittelNyKategoriString) {
        LocalDate dato = Datokyklop.hent().opprettDatoSomYYYY_MM_DD(datoString);
        if (dato == null) {
            Loggekyklop.bruk().loggADVARSEL("Klarte ikke å konvertere datoString " + datoString + ", avbryter oppdatering.");
            return;
        }

        Post post = finnPostFraDatoOgTekstFraBanken(dato, tekstFraBankenString);
        if (post != null) {
            String beskrivendenavnString = post.hentBeskrivendeNavn();

            Optional<Kategori> kategoriOptional = kategoriService.finnEtterTittelOgUnderTittel(tittelNyKategoriString, "-");
            if (kategoriOptional.isEmpty()) {
                Loggekyklop.bruk().loggADVARSEL("Fant ikke kategori med tittel " + tittelNyKategoriString + ", avbryter oppdatering.");
            } else {
                Kategori kategori = kategoriOptional.get();
                if (!post.getKategori().equals(kategori)) {
                    post.setKategori(kategoriOptional.get());
                    normalpostService.lagre(post);
                    Loggekyklop.bruk().loggINFO(beskrivendenavnString + ": Endret kategori til " + tittelNyKategoriString);
                }
            }
        }

    }


    private void settkategoriAlle(String datoString, String tekstFraBankenString, String tittelNyKategoriString) {
        LocalDate dato = Datokyklop.hent().opprettDatoSomYYYY_MM_DD(datoString);
        if (dato == null) {
            Loggekyklop.bruk().loggADVARSEL("Klarte ikke å konvertere datoString " + datoString + ", avbryter oppdatering.");
            return;
        }

        List<Post> poster = finnPosterFraDatoOgTekstFraBanken(dato, tekstFraBankenString);
        if (!poster.isEmpty()) {
            String beskrivendenavnString = poster.getFirst().hentBeskrivendeNavn();

            Optional<Kategori> kategoriOptional = kategoriService.finnEtterTittelOgUnderTittel(tittelNyKategoriString, "-");
            if (kategoriOptional.isEmpty()) {
                Loggekyklop.bruk().loggADVARSEL("Fant ikke kategori med tittel " + tittelNyKategoriString + ", avbryter oppdatering.");
            } else {
                Kategori kategori = kategoriOptional.get();
                for (Post post : poster) {
                    if (!post.getKategori().equals(kategori)) {
                        post.setKategori(kategoriOptional.get());
                        normalpostService.lagre(post);
                        Loggekyklop.bruk().loggINFO(beskrivendenavnString + ": Endret kategori til " + tittelNyKategoriString);
                    }
                }
            }
        } else {
            Loggekyklop.bruk().loggADVARSEL("Fant ikke post med dato " + datoString + " og tekstFraBanken " + tekstFraBankenString);
        }
    }

    private List<Post> finnPosterFraDatoOgTekstFraBanken(LocalDate dato, String tekstFraBankenString) {
        if (dato == null || tekstFraBankenString == null) {
            return null;
        }
        return Allvitekyklop.hent().getNormalpostService().finnEtterDatoOgTekstfrabankenOgNormalposttypeenum(dato, tekstFraBankenString, NormalposttypeEnum.NORMAL);
    }

    private Post finnPostFraDatoOgTekstFraBanken(LocalDate dato, String tekstFraBankenString) {
        if (dato == null || tekstFraBankenString == null) {
            return null;
        }

        List<Post> poster = Allvitekyklop.hent().getNormalpostService().finnEtterDatoOgTekstfrabankenOgNormalposttypeenum(dato, tekstFraBankenString, NormalposttypeEnum.NORMAL);
        if (!poster.isEmpty()) {
            if (poster.size() > 1) {
                Loggekyklop.bruk().loggADVARSEL("Fant mer enn en normalpost som passet med søkekriteriet dato: " + dato + " tekst fra banken:" + tekstFraBankenString + ". Avbryter oppdateringen.");
                return null;
            } else {
                return poster.getFirst();
            }
        } else {
            return null;
        }


    }

}
