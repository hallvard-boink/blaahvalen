package com.hallvardlaerum.post.normalpost;

import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.ui.RedigerEntitetDialog;
import com.hallvardlaerum.periode.Periode;
import com.hallvardlaerum.periode.aarsoversikt.AarsoversiktService;
import com.hallvardlaerum.periodepost.Periodepost;
import com.hallvardlaerum.periodepost.kostnadspakke.KostnadspakkeRedigeringsomraade;
import com.hallvardlaerum.post.Post;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import org.apache.xmlbeans.impl.xb.xsdschema.All;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Locale;

public class KostnadspakkeMester {
    private LinkedHashSet<Periodepost> benyttedeKostnadspakkerHashSet;
    private ConfirmDialog ingenKostnadspakkeDialog;
    private ConfirmDialog ikkeNokKostnadspakkerDialog;
    private static String ingenKostnadspakkeString = "Du har ikke benyttet noen kostnadspakker i denne omgangen ennå. Lukk denne dialogboksen, og velg en fra nedslippsmenyen 'Kostnadspakke'.";
    private static String ikkeNokKostnadspakkerString = "I denne omgangen har du så langt bare benyttet en kostnadspakke, og da finnes ikke den nest første. Lukk denne dialogboksen, og velg heller en fra nedslippsmenyen 'Kostnadspakke'.";
    private RedigerEntitetDialog<Periodepost, Post> kostnadspakkeRedigerEntitetDialog;
    private KostnadspakkeRedigeringsomraade kostnadspakkeRedigeringsomraadeTilDialog;

    public KostnadspakkeMester() {
        benyttedeKostnadspakkerHashSet = new LinkedHashSet<>();
        String tittelDialogString = "Ingen kostnadspakke";
        ingenKostnadspakkeDialog = new ConfirmDialog(tittelDialogString,ingenKostnadspakkeString,"Ok",e -> ingenKostnadspakkeDialog.close());
        ikkeNokKostnadspakkerDialog = new ConfirmDialog(tittelDialogString, ikkeNokKostnadspakkerString,"Ok", e -> ikkeNokKostnadspakkerDialog.close());

        kostnadspakkeRedigeringsomraadeTilDialog = new KostnadspakkeRedigeringsomraade();
        //kostnadspakkeRedigeringsomraadeTilDialog.init();
        kostnadspakkeRedigerEntitetDialog = new RedigerEntitetDialog<>(
                Allvitekyklop.hent().getKostnadspakkeService(),
                Allvitekyklop.hent().getNormalpostService(),
                "Rediger kostnadspakke",
                "",
                kostnadspakkeRedigeringsomraadeTilDialog,
                Allvitekyklop.hent().getNormalpostRedigeringsomraade(),
                Allvitekyklop.hent().getNormalpostRedigeringsomraade()
        );
    }

    public void oppdaterBenyttedeKostnadspakker(Periodepost periodepost) {
        benyttedeKostnadspakkerHashSet.add(periodepost);
    }

    public Periodepost hentSistBenyttedeKostnadspakke(){
        if (!benyttedeKostnadspakkerHashSet.isEmpty()) {
            return benyttedeKostnadspakkerHashSet.getFirst();
        } else {
            ingenKostnadspakkeDialog.open();
            return null;
        }
    }

    public Periodepost hentNestsisteBenyttedeKostnadspakke(){
        if (benyttedeKostnadspakkerHashSet.isEmpty()) {
            ingenKostnadspakkeDialog.open();
            return null;
        } else if (benyttedeKostnadspakkerHashSet.size()==1) {
            ikkeNokKostnadspakkerDialog.open();
            return null;
        } else {
            return (benyttedeKostnadspakkerHashSet.stream().skip(1).findFirst().orElse(null));
        }
    }

    public void leggTilNyKostnadspakke(Post aktuelleNormalpost){
        Periodepost kostnadspakke = Allvitekyklop.hent().getKostnadspakkeService().opprettEntitet();
        AarsoversiktService aarsoversiktService = Allvitekyklop.hent().getAarsoversiktService();

        String aarString;
        if (aktuelleNormalpost==null) {
            aarString= String.valueOf(LocalDate.now().getYear());
        } else {
            aarString = String.valueOf(aktuelleNormalpost.getDatoLocalDate().getYear());
            kostnadspakke.setKategori(aktuelleNormalpost.getKategori());
        }

        Periode aarsOversikt = aarsoversiktService.finnAarsoversiktFraAarString(aarString);
        if (aarsOversikt==null) {
            Loggekyklop.bruk().loggADVARSEL("Fant ikke årsoversikt for årstallet " + aarString + ", avbryter");
            return;
        }
        kostnadspakke.setPeriode(aarsOversikt);
        if (!kostnadspakkeRedigeringsomraadeTilDialog.erInitiert()) {
            kostnadspakkeRedigeringsomraadeTilDialog.init();
        }

        kostnadspakkeRedigerEntitetDialog.vis(kostnadspakke);
    }
}
