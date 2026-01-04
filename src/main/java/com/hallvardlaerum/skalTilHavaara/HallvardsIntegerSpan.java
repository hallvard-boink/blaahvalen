package com.hallvardlaerum.skalTilHavaara;

import com.hallvardlaerum.libs.felter.HelTallMester;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class HallvardsIntegerSpan extends Span {


    public HallvardsIntegerSpan() {
        setClassName(LumoUtility.TextAlignment.RIGHT);
    }

    public HallvardsIntegerSpan(Builder builder){
        setClassName(LumoUtility.TextAlignment.RIGHT);
        settBold(builder.erBold);
    }

    public void settInteger(Integer nyInteger){
        if (nyInteger==null) {
            setText("-");
            getStyle().set("color", "black");
        } else {
            String nyTekst = HelTallMester.formaterIntegerSomStortTall(nyInteger);
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

    public void settBold(boolean erBold){
        if (erBold) {
            addClassName(LumoUtility.FontWeight.BOLD);
        } else {
            removeClassName(LumoUtility.FontWeight.BOLD);
        }
    }


    public static class Builder{
        private boolean erBold;

        public Builder settErBold(boolean erBold){
            this.erBold = erBold;
            return this;
        }

        public HallvardsIntegerSpan build(){
            return new HallvardsIntegerSpan(this);
        }
    }



}
