package com.hallvardlaerum.basis;

import com.hallvardlaerum.libs.feiloglogging.Versjonskyklopmal;
import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;

public class Versjonskyklop extends Versjonskyklopmal implements InitieringsEgnet {
    private static Versjonskyklop versjonskyklop;
    private boolean erInitiert = false;


    @Override
    public void byggOppVersjoner() {
        super.leggTilVersjon(
                "1.0.0",
                "2026-01-31",
                "Første release",
                "Denne versjonen er ganske godt testet, og mange bugs er fjernet. Den skal tåle å ha skarpe poster.");

        super.leggTilVersjon(
                "0.9.0",
                "2026-01-19",
                "Nesten produksjonsklar",
                "Denne versjonen har all funksjonalitet for 1.0 på plass, inkludert kostnadspakker og budsjettposter");


        super.leggTilVersjon("0.5.6","2026-01-01","Før rydding i serviceklasser og repositories","");
        super.leggTilVersjon("0.5.5","2025-12-31","Kostnadspakkemester","Støtte for HentSiste og HentNestSiste kostnadspakke. Og ymse bugfiks.");
        super.leggTilVersjon("0.5.4","2025-12-29","Finpusset kategorisering av poster","Hoved- og underkategorier, samt kostnadspakker");
        super.leggTilVersjon("0.5.3","2025-12-22","Opprettet kostnadspakker","Endret datastrukturen, med kobling mellom post og periodepost.");
        super.leggTilVersjon("0.5.2","2025-12-21","Normalpostlister og busjettposterlister","La til oversikter i Perioderedigeringsmaler");
        super.leggTilVersjon("0.5.1","2025-12-20","To-nivå budsjettering","Begynte på mekanismer for bygging av budsjett i to nivåer. Virket for tungvint.");
        super.leggTilVersjon("0.5.0","2025-12-14","Importerte Årsoversikter og Månedsoversikter","Utvidet også kategorier med budsjettpostgrupper.");
        super.leggTilVersjon("0.4.3","2025-12-13","Ny arkitektur","Fjernet månedsbudsjettmal, bygget opp kategorier i to nivåer.");
        super.leggTilVersjon("0.4.2","2025-12-13","Månedsbudsjettmal","La til månedsbudsjettmal, og jobbet med overordnet håndtering av budsjettposter, faste utgifter og maler.");
        super.leggTilVersjon("0.4.1","2025-12-10","Budsjettposter","La til støtte for budsjettposter");
        super.leggTilVersjon("0.4.0","2025-12-10","Laget dialogbokser for periodeposter","Fikset vanskelig bug med scope og binder");
        super.leggTilVersjon("0.3.5","2025-11-20","Lagt til Periodepost og Maanedsoversiktpost","");
        super.leggTilVersjon("0.3.4","2025-11-20","Lagt til Årsoversikt","");
        super.leggTilVersjon("0.3.3","2025-11-19","Generalisere PeriodeView","");
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
        versjonskyklop.setApplikasjonsNavnString("Rådyret");
        versjonskyklop.setApplikasjonsKortnavnString("blaahvalen");
        versjonskyklop.setApplikasjonsBeskrivelseString("Regnskap og budsjett for Tina og Hallvard.");

        byggOppVersjoner();
        erInitiert =true;
    }

    @Override
    public void init() {
        this.initier();
    }

    @Override
    public boolean erInitiert() {
        return erInitiert;
    }
}
