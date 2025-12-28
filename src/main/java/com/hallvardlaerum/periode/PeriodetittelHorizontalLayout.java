package com.hallvardlaerum.periode;

import com.hallvardlaerum.libs.felter.Datokyklop;
import com.hallvardlaerum.libs.felter.DatopresisjonEnum;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.time.LocalDate;

public class PeriodetittelHorizontalLayout extends HorizontalLayout {
    private DatopresisjonEnum datopresisjonEnum;
    private H3 datoH3;

    public PeriodetittelHorizontalLayout(PeriodetypeEnum periodetypeEnum) {
        switch (periodetypeEnum) {
            case MAANEDSOVERSIKT -> datopresisjonEnum = DatopresisjonEnum.MAANED;
            case AARSOVERSIKT -> datopresisjonEnum = DatopresisjonEnum.AAR;
            default -> datopresisjonEnum = DatopresisjonEnum.FULL_DATO;
        }

        byggLayout();
    }

    private void byggLayout() {
        datoH3 = new H3();
        datoH3.addClassName(LumoUtility.TextColor.PRIMARY);
        //datoH3.addClassName(LumoUtility.TextAlignment.CENTER); // virker ikke
        //this.addClassName(LumoUtility.AlignItems.CENTER); //virker ikke
        //setAlignItems(Alignment.CENTER); // virker ikke
        add(datoH3);
    }

    public void oppdaterTittel(String tittel) {
        datoH3.setText(tittel);
    }

    public void oppdaterTittel(LocalDate dato) {
        String periodetittelString="";
        if (datopresisjonEnum == DatopresisjonEnum.AAR) {
            periodetittelString = Datokyklop.hent().formaterLocalDate_YYYY(dato);
        } else if (datopresisjonEnum == DatopresisjonEnum.MAANED) {
            periodetittelString = Datokyklop.hent().formaterLocalDate_MaanedsnavnAar(dato);
        } else {
            periodetittelString = Datokyklop.hent().formaterDato(dato);
        }
        datoH3.setText(periodetittelString);

    }
}
