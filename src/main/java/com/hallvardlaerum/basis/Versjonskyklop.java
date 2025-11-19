package com.hallvardlaerum.basis;

import com.hallvardlaerum.libs.feiloglogging.Versjonskyklopmal;

public class Versjonskyklop extends Versjonskyklopmal {
    private static Versjonskyklop versjonskyklop;


    @Override
    public void byggOppVersjoner() {
        super.leggTilVersjon("0.3.2","2025-11-19","Før TekstField i stedet for IntegerField","");
        super.leggTilVersjon("0.3.1","2025-11-17","Delposter","Utvidelse av PostView og korreksjon av import.");
        super.leggTilVersjon("0.3","2025-11-09","Budsjettposter","Datastruktur og import");
        super.leggTilVersjon("0.2.1","2025-11-08","Stil på lista med poster","Triksing med Lumo og css.");
        super.leggTilVersjon("0.2","2025-11-06","Importert kategorier og poster","Hentet fra eksporterte excel-data fra Mendix");
        super.leggTilVersjon("0.1","2025-11-03","Opprettet kategorier og poster","De første entitetene");
        super.leggTilVersjon("0.0.1","2025-10-25","Skjelett","Få rammeverket opp og stå");
    }


    private  Versjonskyklop() {
        super();
    }

    public static Versjonskyklop hent(){
        if (versjonskyklop == null) {
            versjonskyklop = new Versjonskyklop();
        }
        return versjonskyklop;
    }

    public void initier(){
        versjonskyklop.setApplikasjonsNavnString("Blåhvalen 2025");
        versjonskyklop.setApplikasjonsKortnavnString("blaahvalen");
        versjonskyklop.setApplikasjonsBeskrivelseString("Regnskap og budsjett for Tina og Hallvard.");
        byggOppVersjoner();
    }
}
