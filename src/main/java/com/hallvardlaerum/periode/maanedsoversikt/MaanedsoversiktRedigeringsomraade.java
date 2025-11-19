package com.hallvardlaerum.periode.maanedsoversikt;

import com.hallvardlaerum.libs.felter.DatopresisjonEnum;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeAktig;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeMal;
import com.hallvardlaerum.periode.Periode;
import com.hallvardlaerum.periode.PeriodetittelHorizontalLayout;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MaanedsoversiktRedigeringsomraade extends RedigeringsomraadeMal<Periode> implements RedigeringsomraadeAktig<Periode> {


    // === FELTENE ===
    private ComboBox<PeriodetypeEnum> periodetypeComboBox;
    private DatePicker datoFraDatePicker;
    private DatePicker datoTilDatePicker;

    private TextArea beskrivelseTextArea;

    private IntegerField sumBudsjettInntektIntegerField;
    private IntegerField sumBudsjettUtgifterIntegerField;
    private IntegerField sumBudsjettResultatIntegerField;
    private IntegerField sumRegnskapInntektIntegerField;
    private IntegerField sumRegnskapUtgifterIntegerField;
    private IntegerField sumRegnskapResultatIntegerField;
    private IntegerField sumDifferanseResultatBudsjettRegnskapIntegerField;


    private PeriodetittelHorizontalLayout periodetittelHorizontalLayout;

    public void initier(){
        if (beskrivelseTextArea==null) {
            instansOpprettFelter();
            periodetittelHorizontalLayout = leggTilAndrefelterOver(new PeriodetittelHorizontalLayout(DatopresisjonEnum.MAANED));
            instansByggOppBinder();
        }
    }


    @Override
    public void instansOppdaterEkstraRedigeringsfelter() {
        periodetittelHorizontalLayout.oppdaterTittel(getEntitet().getDatoFraLocalDate());
    }

    @Override
    public void instansOpprettFelter() {
        String hovedtabString ="Hoved";
        String ekstratabString = "Ekstra";

        //TODO Endre IntegerField til TextField med konverter i binder

        beskrivelseTextArea = leggTilRedigeringsfelt(new TextArea("Beskrivelse"), hovedtabString);
        settColspan(beskrivelseTextArea,3);

        sumBudsjettInntektIntegerField = new IntegerField("Budsjett inn");

        sumRegnskapInntektIntegerField = new IntegerField("Regnskap inn");
        sumRegnskapInntektIntegerField.addClassName(LumoUtility.TextAlignment.RIGHT);

        leggTilRedigeringsfelter(hovedtabString, sumBudsjettInntektIntegerField, sumRegnskapInntektIntegerField);

        sumBudsjettUtgifterIntegerField = new IntegerField("Budsjett ut");
        sumRegnskapUtgifterIntegerField = new IntegerField("Regnskap ut");
        leggTilRedigeringsfelter(hovedtabString, sumBudsjettUtgifterIntegerField, sumRegnskapUtgifterIntegerField);

        sumBudsjettResultatIntegerField = new IntegerField("Budsjett resultat");
        sumRegnskapResultatIntegerField = new IntegerField("Regnskap resultat");
        sumDifferanseResultatBudsjettRegnskapIntegerField = new IntegerField("Differanse");
        leggTilRedigeringsfelter(hovedtabString, sumBudsjettResultatIntegerField, sumRegnskapResultatIntegerField, sumDifferanseResultatBudsjettRegnskapIntegerField);

        periodetypeComboBox = new ComboBox<>("Periodetype");
        periodetypeComboBox.setItems(PeriodetypeEnum.values());
        periodetypeComboBox.setItemLabelGenerator(PeriodetypeEnum::getTittel);
        periodetypeComboBox.setEnabled(false);
        datoFraDatePicker = new DatePicker("Fra");
        datoTilDatePicker = new DatePicker("Til");
        leggTilRedigeringsfelter(ekstratabString, periodetypeComboBox, datoFraDatePicker, datoTilDatePicker);

        leggTilDatofeltTidOpprettetOgRedigert(ekstratabString);


        setFokusComponent(beskrivelseTextArea);

    }

    @Override
    public void instansByggOppBinder() {
        Binder<Periode> binder = hentBinder();
        binder.bind(periodetypeComboBox, Periode::getPeriodetypeEnum, Periode::setPeriodetypeEnum);
        binder.bind(datoFraDatePicker, Periode::getDatoFraLocalDate, Periode::setDatoFraLocalDate);
        binder.bind(datoTilDatePicker, Periode::getDatoTilLocalDate, Periode::setDatoTilLocalDate);
        binder.bind(beskrivelseTextArea, Periode::getBeskrivelseString, Periode::setBeskrivelseString);
        binder.bind(sumBudsjettInntektIntegerField, Periode::getSumBudsjettInntektInteger, Periode::setSumBudsjettInntektInteger);
        binder.bind(sumBudsjettUtgifterIntegerField, Periode::getSumBudsjettInntektInteger, Periode::setSumBudsjettInntektInteger);
        binder.bind(sumRegnskapInntektIntegerField, Periode::getSumRegnskapInntektInteger, Periode::setSumRegnskapInntektInteger);
        binder.bind(sumRegnskapUtgifterIntegerField, Periode::getSumRegnskapUtgifterInteger, Periode::setSumRegnskapUtgifterInteger);
        binder.bind(sumBudsjettResultatIntegerField, Periode::getSumBudsjettResultatInteger, Periode::setSumBudsjettResultatInteger);
        binder.bind(sumRegnskapResultatIntegerField, Periode::getSumRegnskapResultatInteger, Periode::setSumRegnskapResultatInteger);
        binder.bind(sumDifferanseResultatBudsjettRegnskapIntegerField, Periode::getSumDifferanseResultatBudsjettRegnskap, Periode::setSumDifferanseResultatBudsjettRegnskap);

    }
}
