package com.hallvardlaerum.post.normalpost;

import com.hallvardlaerum.libs.eksportimport.CSVImportassistentMal;
import com.hallvardlaerum.libs.felter.Datokyklop;
import com.hallvardlaerum.post.Post;
import com.hallvardlaerum.post.PostServiceMal;
import com.hallvardlaerum.post.PostklasseEnum;

import java.time.LocalDate;
import java.util.ArrayList;

public class NormalpostFraHandelsbankensCSVImportassistent extends CSVImportassistentMal<Post> {
    private PostServiceMal postService;
    private Post post;
    private NormalpostView normalpostView;

    public NormalpostFraHandelsbankensCSVImportassistent(PostServiceMal postService, NormalpostView normalpostView) {
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
        super.lesInnFeltnavnogCeller(feltnavnCSVArrayList, celler);

        post = postService.opprettEntitet();

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