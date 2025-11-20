package com.hallvardlaerum.periode;

import com.hallvardlaerum.libs.felter.Datokyklop;
import com.hallvardlaerum.libs.felter.HelTallMester;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeAktig;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeMal;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.time.LocalDate;


public class PeriodeRedigeringsomraade extends RedigeringsomraadeMal<Periode> implements RedigeringsomraadeAktig<Periode> {
    private PeriodetypeEnum periodetypeEnum;

    // === FELTENE ===
    private ComboBox<PeriodetypeEnum> periodetypeComboBox;
    private DatePicker datoFraDatePicker;
    private DatePicker datoTilDatePicker;

    private TextArea beskrivelseTextArea;

    private TextField sumBudsjettInntekterTextField;
    private TextField sumBudsjettUtgifterTextField;
    private TextField sumBudsjettResultatTextField;

    private TextField sumRegnskapInntekterTextField;
    private TextField sumRegnskapUtgifterTextField;
    private TextField sumRegnskapResultatTextField;

    private TextField sumDifferanseResultatBudsjettRegnskapTextField;

    private TextField sumRegnskapInntekterMedOverfoeringerTextField;
    private TextField sumRegnskapUtgifterMedOverfoeringerTextField;
    private TextField sumRegnskapResultatMedOverfoeringerTextField;


    private PeriodetittelHorizontalLayout periodetittelHorizontalLayout;

    public PeriodeRedigeringsomraade() {
        super();
    }

    public void initier(PeriodetypeEnum periodetypeEnum){
        this.periodetypeEnum = periodetypeEnum;
        if (beskrivelseTextArea==null) {
            instansOpprettFelter();
            periodetittelHorizontalLayout = leggTilAndrefelterOver(new PeriodetittelHorizontalLayout(periodetypeEnum));
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

        Span innSpan = new Span("Inn");
        innSpan.addClassName(LumoUtility.TextAlignment.RIGHT);
        Span utSpan = new Span("Ut");
        utSpan.addClassName(LumoUtility.TextAlignment.RIGHT);
        Span resultatSpan = new Span("Resultat");
        resultatSpan.addClassName(LumoUtility.TextAlignment.RIGHT);
        Span regnskapSpan = new Span("Regnskap");
        regnskapSpan.addClassName(LumoUtility.TextAlignment.RIGHT);
        Span budsjettSpan = new Span("Budsjett");
        budsjettSpan.addClassName(LumoUtility.TextAlignment.RIGHT);
        Span differanseSpan = new Span("Differanse");
        differanseSpan.addClassName(LumoUtility.TextAlignment.RIGHT);
        Span tomSpan = new Span(" ");
        Span regnskapMedOverfoeringerSpan = new Span("Regnskap med overføringer");
        regnskapMedOverfoeringerSpan.addClassName(LumoUtility.TextAlignment.RIGHT);

        sumBudsjettInntekterTextField = new TextField();
        sumBudsjettInntekterTextField.setReadOnly(true);
        sumBudsjettInntekterTextField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);

        sumRegnskapInntekterTextField = new TextField();
        sumRegnskapInntekterTextField.setReadOnly(true);
        sumRegnskapInntekterTextField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);

        sumBudsjettUtgifterTextField = new TextField();
        sumBudsjettUtgifterTextField.setReadOnly(true);
        sumBudsjettUtgifterTextField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);

        sumRegnskapUtgifterTextField = new TextField();
        sumRegnskapUtgifterTextField.setReadOnly(true);
        sumRegnskapUtgifterTextField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);

        sumBudsjettResultatTextField = new TextField();
        sumBudsjettResultatTextField.setReadOnly(true);
        sumBudsjettResultatTextField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);

        sumRegnskapResultatTextField = new TextField();
        sumRegnskapResultatTextField.setReadOnly(true);
        sumRegnskapResultatTextField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);

        sumDifferanseResultatBudsjettRegnskapTextField = new TextField();
        sumDifferanseResultatBudsjettRegnskapTextField.setReadOnly(true);
        sumDifferanseResultatBudsjettRegnskapTextField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);

        sumRegnskapInntekterMedOverfoeringerTextField = new TextField();
        sumRegnskapInntekterMedOverfoeringerTextField.setReadOnly(true);
        sumRegnskapInntekterMedOverfoeringerTextField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);

        sumRegnskapUtgifterMedOverfoeringerTextField = new TextField();
        sumRegnskapUtgifterMedOverfoeringerTextField.setReadOnly(true);
        sumRegnskapUtgifterMedOverfoeringerTextField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);

        sumRegnskapResultatMedOverfoeringerTextField = new TextField();
        sumRegnskapResultatMedOverfoeringerTextField.setReadOnly(true);
        sumRegnskapResultatMedOverfoeringerTextField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);

        leggTilRedigeringsfelter(hovedtabString, new Span(""));
        leggTilRedigeringsfelter(hovedtabString, new Span(""), budsjettSpan, regnskapSpan, differanseSpan, regnskapMedOverfoeringerSpan);
        leggTilRedigeringsfelter(hovedtabString, innSpan, sumBudsjettInntekterTextField, sumRegnskapInntekterTextField, new Span(""), sumRegnskapInntekterMedOverfoeringerTextField);
        leggTilRedigeringsfelter(hovedtabString, utSpan, sumBudsjettUtgifterTextField, sumRegnskapUtgifterTextField, new Span(""), sumRegnskapUtgifterMedOverfoeringerTextField);
        leggTilRedigeringsfelter(hovedtabString, resultatSpan, sumBudsjettResultatTextField, sumRegnskapResultatTextField, sumDifferanseResultatBudsjettRegnskapTextField, sumRegnskapResultatMedOverfoeringerTextField);

        beskrivelseTextArea = leggTilRedigeringsfelt(new TextArea("Beskrivelse"), hovedtabString);
        beskrivelseTextArea.setMinRows(4);
        settColspan(beskrivelseTextArea,4);

        periodetypeComboBox = new ComboBox<>("Periodetype");
        periodetypeComboBox.setItems(PeriodetypeEnum.values());
        periodetypeComboBox.setItemLabelGenerator(PeriodetypeEnum::getTittel);
        periodetypeComboBox.setEnabled(false);
        datoFraDatePicker = new DatePicker("Fra");
        datoFraDatePicker.addValueChangeListener(e ->{
            if (e.getValue()==null) {
                return;
            }

            //Flytt dato til første i måneden og datoTil til siste i måneden
            LocalDate dato = e.getValue();
            datoFraDatePicker.setValue(LocalDate.of(dato.getYear(), dato.getMonth(),1));
            datoTilDatePicker.setValue(Datokyklop.hent().finnSisteIMaaneden(datoFraDatePicker.getValue()));
        });

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
        binder.bind(sumBudsjettInntekterTextField, periode -> HelTallMester.integerFormatertSomStortTall(periode.getSumBudsjettInntektInteger()), null);
        binder.bind(sumBudsjettUtgifterTextField, periode -> HelTallMester.integerFormatertSomStortTall(periode.getSumBudsjettUtgifterInteger()), null);
        binder.bind(sumRegnskapInntekterTextField, periode -> HelTallMester.integerFormatertSomStortTall(periode.getSumRegnskapInntektInteger()), null);
        binder.bind(sumRegnskapUtgifterTextField, periode -> HelTallMester.integerFormatertSomStortTall(periode.getSumRegnskapUtgifterInteger()), null);
        binder.bind(sumBudsjettResultatTextField, periode -> HelTallMester.integerFormatertSomStortTall(periode.getSumBudsjettResultatInteger()), null);
        binder.bind(sumRegnskapResultatTextField, periode -> HelTallMester.integerFormatertSomStortTall(periode.getSumRegnskapResultatInteger()), null);
        binder.bind(sumDifferanseResultatBudsjettRegnskapTextField, periode -> HelTallMester.integerFormatertSomStortTall(periode.getSumDifferanseResultatBudsjettRegnskap()), null);

        binder.bind(sumRegnskapInntekterMedOverfoeringerTextField, periode -> HelTallMester.integerFormatertSomStortTall(periode.getSumRegnskapInntektMedOverfoeringerInteger()), null);
        binder.bind(sumRegnskapUtgifterMedOverfoeringerTextField, periode -> HelTallMester.integerFormatertSomStortTall(periode.getSumRegnskapUtgifterMedOverfoeringerInteger()), null);
        binder.bind(sumRegnskapResultatMedOverfoeringerTextField, periode -> HelTallMester.integerFormatertSomStortTall(periode.getSumRegnskapResultatMedOverfoeringerInteger()), null);
    }
}
