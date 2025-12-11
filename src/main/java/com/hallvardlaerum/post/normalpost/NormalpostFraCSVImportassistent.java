package com.hallvardlaerum.post.normalpost;

import com.hallvardlaerum.libs.eksportimport.CSVImportassistentAktig;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.felter.Datokyklop;
import com.hallvardlaerum.libs.felter.HelTallMester;
import com.hallvardlaerum.post.Post;
import com.hallvardlaerum.post.PostServiceMal;
import com.hallvardlaerum.post.PostklasseEnum;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NormalpostFraCSVImportassistent implements CSVImportassistentAktig {
    private PostServiceMal postService;
    private ArrayList<String> cellerArrayList;
    private Post post;
    private String cellerString = null;
    private String feltnavneneString = null;
    private ArrayList<String>feltnavnCSVArrayList ;
    private NormalpostView normalpostView;

    public NormalpostFraCSVImportassistent(PostServiceMal postService, NormalpostView normalpostView) {
        this.postService = postService;
        this.normalpostView = normalpostView;
    }

    @Override
    public void forberedImport() {

    }

    @Override
    public void ryddOppEtterImport() {
        normalpostView.oppdaterSoekeomraade();
    }

    @Override
    public Post konverterFraTekstRadOgLagre(ArrayList<String> feltnavnCSVArrayList, String[] celler) {
        post = postService.opprettEntitet();
        cellerArrayList = new ArrayList<>(List.of(celler));
        this.feltnavnCSVArrayList = feltnavnCSVArrayList;

        String statusString = hentVerdi("Status");
        if (statusString.equalsIgnoreCase("Reservert")) {  //Denne skal ikke importeres
            return null;
        }

        post.setPostklasseEnum(PostklasseEnum.NORMALPOST);
        post.setNormalPoststatusEnum(NormalpoststatusEnum.UBEHANDLET);
        post.setNormalposttypeEnum(NormalposttypeEnum.NORMAL);

        String datoString = hentVerdi("Utført dato");
        LocalDate dato = Datokyklop.hent().opprettDatoSom_DDpMMpYYYY(datoString);
        //LocalDate dato = Datokyklop.hent().opprettDatoSomYYYY_MM_DD(datoString);
        post.setDatoLocalDate(dato);
        post.setTekstFraBankenString(hentVerdier(false,false,"Beskrivelse","Melding/KID/Fakt.nr"));
        post.setInnPaaKontoInteger(parseInt(hentVerdi("Beløp inn")));
        post.setUtFraKontoInteger(parseInt(hentVerdi("Beløp ut")));
        post.setEkstraInfoString(hentVerdier(true,true,
                "Type", "Undertype","Fra konto","Avsender","Til konto","Mottakernavn","Valuta"));

        postService.lagre(post);
        return post;
    }



    private Integer parseInt(String integerString) {
        if (integerString==null || integerString=="") {
            return null;
        } else {
            Integer tallInteger = HelTallMester.konverterStrengMedDesimalTilInteger(integerString);
            if (tallInteger<0) {
                tallInteger = -tallInteger;
            }
            return tallInteger;
        }
    }

    private String hentVerdier(boolean setterInnLinjeskift, boolean setterInnFeltnavn, String... feltnavnene){
        if (feltnavnene==null) {
            return null;
        }

        int i = 0;
        StringBuilder sb = new StringBuilder();
        for (String feltnavnString:feltnavnene) {
            String verdi = hentVerdi(feltnavnString);
            if (verdi!=null) {
                if (setterInnFeltnavn) {
                    sb.append(feltnavnString).append(": ");
                }
                sb.append(verdi);
                if (i<feltnavnene.length-1) {
                    if (setterInnLinjeskift) {
                        sb.append("\n");
                    } else {
                        sb.append(", ");
                    }
                }
            }
            i++;
        }

        return sb.toString();
    }

    private String hentVerdi(String feltnavn) {
        Integer posInteger = finnPosisjon(feltnavn);
        if (posInteger==null) {
            Loggekyklop.hent().loggTilFilINFO("Fant ikke '"+ feltnavn +"' i "  + hentFeltnavnCSVString());
            return "";
        } else {
            if (posInteger>cellerArrayList.size()-1) {
                return "";
            } else {
                return cellerArrayList.get(posInteger);
            }
        }
    }

    private String hentFeltnavnCSVString(){
        if (feltnavneneString==null) {
            StringBuilder sb = new StringBuilder();
            for (String feltnavn:feltnavnCSVArrayList) {
                sb.append(feltnavn).append(" ");
            }
            feltnavneneString = sb.toString();
        }
        return feltnavneneString;
    }



    private Integer finnPosisjon(String feltnavn) {
        for (int i = 0; i<feltnavnCSVArrayList.size(); i++) {
            if (feltnavn.equalsIgnoreCase(feltnavnCSVArrayList.get(i))) {
                return i;
            }
        }
        return null;
    }

    private String hentcellerString(){
        StringBuilder sb = new StringBuilder();
        for (String celleString:cellerArrayList) {
            sb.append(celleString).append(" ");
        }
        cellerString = sb.toString();
        return cellerString;
    }


}

/**
 *
 * Utført dato	Bokført dato	Rentedato	Beskrivelse                         	Type	    Undertype	Fra konto	Avsender	Til konto	Mottakernavn	Beløp inn	Beløp ut	Valuta	Status	Melding/KID/Fakt.nr
 * 22.11.2025			                    REMA 1000 KJELSAS  	                    Varekjøp	       	              9051 08 17531	Felleskonto				-371.750	NOK	Reservert	REMA 1000 KJELSAS
 * 21.11.2025	21.11.2025	21.11.2025	    SIGNAL FOUNDATION	                    Varekjøp	Varekjøp debetkort	  9051 08 17531	Hallvard Lærum				-100	NOK	Bokført	*6371 20.11 NOK 100.00 SIGNAL FOUNDATION Kurs: 1.0000
 * 21.11.2025	21.11.2025	21.11.2025	    Helsedirektoratet	                    Betaling innland Overføring fra annen konto	7694 05 61503	Helsedirektoratet	9051 08 17531	Hallvard Lærum	310		NOK	Bokført	Fra: Helsedirektoratet Betalt: 21.11.25
 * 20.11.2025	20.11.2025	20.11.2025	    19.11 NORMAL OSLO VITAMINVEIEN OSLO	    Varekjøp	Varekjøp debetkort	9051 08 17531	Hallvard Lærum				-256	NOK	Bokført	19.11 NORMAL OSLO VITAMINVEIEN OSLO
 * 20.11.2025	20.11.2025	20.11.2025    	Fredrikstad kommune (51220577191)	    Betaling innland - Avtalegiro med eFaktura	Betaling med KID innland	9051 08 17531	Felleskonto	5122 05 77191	Fredrikstad kommune		-1105	NOK	Bokført	FREDRIKSTAD KOMMUNE
 * 20.11.2025	20.11.2025	20.11.2025	    19.11 BAKER HANSEN NYDALSVEIEN  OSLO    Varekjøp	Varekjøp debetkort	9051 08 17531	Hallvard Lærum				-79	NOK	Bokført	19.11 BAKER HANSEN NYDALSVEIEN  OSLO
 * 20.11.2025	20.11.2025	20.11.2025	    19.11 130 JERNIA STORO OSLO	            Varekjøp	Varekjøp debetkort	9051 08 17531	Hallvard Lærum				-189	NOK	Bokført	19.11 130 JERNIA STORO OSLO
 *
 */