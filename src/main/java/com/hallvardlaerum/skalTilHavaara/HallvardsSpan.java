package com.hallvardlaerum.skalTilHavaara;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class HallvardsSpan extends Span {

    public HallvardsSpan() {
        super();
    }

    public HallvardsSpan(String tekst){
        super(tekst);
    }

    public void settTekstfarge(Farge farge) {
        switch (farge) {
            case RØD -> getStyle().set("color","red");
            case GRØNN -> getStyle().set("color","green");
            case SVART -> getStyle().set("color","black");
            case BLÅ -> getStyle().set("color","blue");
        }
    }

    public void settBold(boolean erBold){
        if (erBold) {
            addClassName(LumoUtility.FontWeight.BOLD);
        } else {
            removeClassName(LumoUtility.FontWeight.BOLD);
        }
    }

    public enum Farge{
        RØD,
        GRØNN,
        SVART,
        BLÅ
    }

}
