package com.hallvardlaerum.post.normalpost;

import com.hallvardlaerum.periodepost.Periodepost;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;

import java.util.LinkedHashSet;

public class KostnadspakkeMester {
    private LinkedHashSet<Periodepost> benyttedeKostnadspakkerHashSet;
    private ConfirmDialog ingenKostnadspakkeDialog;
    private ConfirmDialog ikkeNokKostnadspakkerDialog;
    private static String ingenKostnadspakkeString = "Du har ikke benyttet noen kostnadspakker i denne omgangen ennå. Lukk denne dialogboksen, og velg en fra nedslippsmenyen 'Kostnadspakke'.";
    private static String ikkeNokKostnadspakkerString = "I denne omgangen har du så langt bare benyttet en kostnadspakke, og da finnes ikke den nest første. Lukk denne dialogboksen, og velg heller en fra nedslippsmenyen 'Kostnadspakke'.";


    public KostnadspakkeMester() {
        benyttedeKostnadspakkerHashSet = new LinkedHashSet<>();
        String tittelDialogString = "Ingen kostnadspakke";
        ingenKostnadspakkeDialog = new ConfirmDialog(tittelDialogString,ingenKostnadspakkeString,"Ok",e -> ingenKostnadspakkeDialog.close());
        ikkeNokKostnadspakkerDialog = new ConfirmDialog(tittelDialogString, ikkeNokKostnadspakkerString,"Ok", e -> ikkeNokKostnadspakkerDialog.close());
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

    public void leggTilNyKostnadspakke(){
        //TODO: Skal implementeres;
    }
}
