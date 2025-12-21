package com.hallvardlaerum.periodepost;

import com.hallvardlaerum.libs.felter.HelTallMester;
import com.vaadin.flow.component.html.Span;

public class HallvardsSpan extends Span {

    public void settInteger(Integer nyInteger){
        if (nyInteger==null) {
            setText("-");
            getStyle().set("color", "black");
        } else {
            String nyTekst = HelTallMester.integerFormatertSomStortTall(nyInteger);
            setText(nyTekst);
            if (nyInteger<0) {
                getStyle().set("color", "red");
            } else {
                getStyle().set("color", "black");
            }
        }
    }

    public void settDifferanseInteger(Integer aInteger, Integer bInteger){
        if (aInteger==null) {
            aInteger = 0;
        }
        if (bInteger==null){
            bInteger = 0;
        }

        settInteger(aInteger-bInteger);
    }

}
