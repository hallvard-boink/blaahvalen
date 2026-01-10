package com.hallvardlaerum.skalTilHavaara;

import com.hallvardlaerum.libs.felter.HelTallMester;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class HallvardsIntegerSpan extends Span {
    private boolean visNegativeTalliGroent;
    private boolean visPositiveTalliGroent;
    private Integer verdiInteger;

    public HallvardsIntegerSpan() {
        setClassName(LumoUtility.TextAlignment.RIGHT);
        visNegativeTalliGroent = false;
        visPositiveTalliGroent = false;
    }

    public void settInteger(Integer nyInteger) {
        verdiInteger = nyInteger;
        if (nyInteger == null) {
            setText("-");
        } else {
            String nyTekst = HelTallMester.formaterIntegerSomStortTall(nyInteger);
            setText(nyTekst);
        }
        oppdaterStil();
    }

    public void settDifferanseInteger(Integer aInteger, Integer bInteger) {
        if (aInteger == null) {
            aInteger = 0;
        }
        if (bInteger == null) {
            bInteger = 0;
        }
        settInteger(aInteger - bInteger);
    }

    private void oppdaterStil(){
        if (verdiInteger == null) {
            getStyle().set("color", "black");
        } else {
            if (verdiInteger<0) {
                if (visNegativeTalliGroent) {
                    getStyle().set("color", "green");
                } else {
                    getStyle().set("color", "red");
                }
            } else {
                if (visPositiveTalliGroent) {
                    getStyle().set("color", "green");
                } else {
                    getStyle().set("color", "black");
                }
            }
        }
    }

    public void settBold(boolean erBold) {
        if (erBold) {
            addClassName(LumoUtility.FontWeight.BOLD);
        } else {
            removeClassName(LumoUtility.FontWeight.BOLD);
        }
    }

    public void visNegativeTalliGroent(Boolean visGroent){
        visNegativeTalliGroent = visGroent;
    }

    public void visPositiveTalliGroent(Boolean visGroent) {
        visPositiveTalliGroent = visGroent;
    }


}
