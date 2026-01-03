package com.hallvardlaerum.post.budsjettpost;

import com.hallvardlaerum.kategori.Kategori;
import com.hallvardlaerum.kategori.KategoriService;
import com.hallvardlaerum.libs.eksportimport.CSVImportassistentMal;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.felter.Datokyklop;
import com.hallvardlaerum.libs.felter.DatopresisjonEnum;
import com.hallvardlaerum.post.Post;
import com.hallvardlaerum.post.PostklasseEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;


import java.util.ArrayList;
import java.util.Optional;

public class BudsjettpostFraGamleBlaahvalenCSVImportassistent extends CSVImportassistentMal<Post> {
    private BudsjettpostService budsjettpostService;
    private KategoriService kategoriService;



    @Override
    public void forberedImport() {
        budsjettpostService = Allvitekyklop.hent().getBudsjettpostService();
        kategoriService = Allvitekyklop.hent().getKategoriService();

        Loggekyklop.hent().huskStatus();
        Loggekyklop.hent().initierLoggfil();
        Loggekyklop.hent().settNivaaINFO();
    }

    @Override
    public Post konverterFraTekstRadOgLagre(ArrayList<String> feltnavnCSVArrayList, String[] celler) {
        super.lesInnFeltnavnogCeller(feltnavnCSVArrayList, celler);

        Post budsjettpost = budsjettpostService.opprettEntitet();

        budsjettpost.setPostklasseEnum(PostklasseEnum.BUDSJETTPOST);
        budsjettpost.setDatoLocalDate(Datokyklop.hent().opprettDatoSom_DDpMMpYYYY(hentVerdi("datoLocalDate")));
        budsjettpost.setDatopresisjonEnum(DatopresisjonEnum.hentFraTittel(hentVerdi("datopresisjonEnum")));
        budsjettpost.setBeskrivelseString(hentVerdi("beskrivelseString"));
        Integer innPaaKontoInteger = parseInt(hentVerdi("innPaaKontoInteger"));
        if (innPaaKontoInteger!=null) {
            budsjettpost.setInnPaaKontoInteger(innPaaKontoInteger);
        }
        Integer utFraKontoInteger = parseInt(hentVerdi("utFraKontoInteger"));
        if (utFraKontoInteger!=null) {
            budsjettpost.setUtFraKontoInteger(utFraKontoInteger);
        }
        budsjettpost.setEstimatpresisjonEnum(EstimatpresisjonEnum.hentFraTittel(hentVerdi("estimatpresisjonEnum")));
        budsjettpost.setPrioritetEnum(PrioritetEnum.hentFraTittel(hentVerdi("prioritetEnum")));
        budsjettpost.setRekkefoelgeInteger(parseInt(hentVerdi("rekkefoelgeInteger")));
        budsjettpost.setErRegelmessigBoolean(Boolean.valueOf(hentVerdi("erRegelmessigBoolean")));

        budsjettpost.setBudsjettpoststatusEnum(BudsjettpoststatusEnum.hentFraTittel(hentVerdi("budsjettpoststatusEnum")));
        budsjettpost.setKategori(finnKategori(hentVerdi("KategoriNavn")));

        budsjettpostService.lagre(budsjettpost);
        return budsjettpost;
    }

    public Kategori finnKategori(String kategoriNavn) {
        if (kategoriNavn==null || kategoriNavn.isEmpty()) {
            return null;
        }
        Optional<Kategori> kategoriOptional = kategoriService.finnEtterTittelOgUnderTittel(kategoriNavn,"-");
        if (kategoriOptional.isPresent()) {
            return kategoriOptional.get();
        } else {
            Loggekyklop.hent().loggTilFilINFO("Fant ikke kategori med tittel " + kategoriNavn + " og undertittel '-'");
            return null;
        }

    }

    @Override
    public void ryddOppEtterImport() {
        Loggekyklop.hent().tilbakestillStatus();
        Loggekyklop.hent().lukkLoggfil();
        Allvitekyklop.hent().getBudsjettpostView().oppdaterSoekeomraadeFinnAlleRader();
    }

//    private Kategori finnKategoriFraBudsjettpostgruppe(String kategoriTittel, String budsjettpostgruppeTittel) {
//        if (kategoriTittel==null || kategoriTittel.isEmpty()) {
//            return null;
//        }
//
//        if (budsjettpostgruppeTittel==null || budsjettpostgruppeTittel.isEmpty()) {
//            return finnHovedkategoriFraTittel(kategoriTittel);
//        }
//
//        int intStart = budsjettpostgruppeTittel.indexOf(kategoriTittel) + kategoriTittel.length() + 1;
//        if (intStart>budsjettpostgruppeTittel.length()) {
//            Loggekyklop.hent().loggTilFilINFO("Budsjettpostgruppenavn har ikke kategoritittel i seg. Kategoritittel: " + kategoriTittel +
//                    " Budsjettpostgruppetittel:" + budsjettpostgruppeTittel);
//            return null;
//        }
//        String underTittel = budsjettpostgruppeTittel.substring(intStart);
//
//        Optional<Kategori> kategoriOptional = kategoriService.finnEtterTittelOgUnderTittel(kategoriTittel,underTittel);
//        if (kategoriOptional.isPresent()) {
//            return kategoriOptional.get();
//        } else {
//            Loggekyklop.hent().loggTilFilINFO("Fant ikke kategori med tittel " + kategoriTittel + " og undertittel " + budsjettpostgruppeTittel);
//            return null;
//        }
//    }




}
