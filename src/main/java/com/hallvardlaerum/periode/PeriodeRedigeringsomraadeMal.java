package com.hallvardlaerum.periode;

import com.hallvardlaerum.libs.felter.Datokyklop;
import com.hallvardlaerum.libs.felter.HelTallMester;
import com.hallvardlaerum.libs.ui.*;
import com.hallvardlaerum.periodepost.Periodepost;
import com.hallvardlaerum.periodepost.PeriodepostRedigeringsomraadeMal;
import com.hallvardlaerum.periodepost.PeriodepostServiceMal;
import com.hallvardlaerum.periodepost.PeriodepostTypeEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.time.LocalDate;
import java.util.ArrayList;


public class PeriodeRedigeringsomraadeMal extends RedigeringsomraadeMal<Periode> implements RedigeringsomraadeAktig<Periode> {
    private PeriodetypeEnum periodetypeEnum;
    private PeriodepostTypeEnum periodepostTypeEnum;


    // === Andre objekter som trengs her ===
    private RedigerEntitetDialog<Periodepost, Periode> periodepostRedigerEntitetDialog;
    private PeriodepostRedigeringsomraadeMal periodepostRedigeringsomraadeTilDialog;
    private PeriodepostServiceMal periodepostService;
    private PeriodeServiceMal periodeService;

    // === GRID ===
    private Grid<Periodepost> hovedKategorierGrid;


    // === FELTER ===
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


    public PeriodeRedigeringsomraadeMal() {

    }

    public void initierPeriodeRedigeringsomraadeMal(PeriodetypeEnum periodetypeEnum,
                                                    PeriodepostServiceMal periodepostService,
                                                    PeriodepostRedigeringsomraadeMal periodepostRedigeringsomraadeTilDialog,
                                                    PeriodeServiceMal periodeService,
                                                    PeriodepostTypeEnum periodepostTypeEnum,
                                                    ViewmalAktig<Periode, PeriodeRepository> viewmalAktig){
        this.periodetypeEnum = periodetypeEnum;
        this.periodepostService = periodepostService;
        this.periodeService = periodeService;
        this.periodepostRedigeringsomraadeTilDialog = periodepostRedigeringsomraadeTilDialog;
        this.periodepostTypeEnum = periodepostTypeEnum;
        this.settView(viewmalAktig);

        super.initRedigeringsomraadeMal();

        if (beskrivelseTextArea==null) {
            instansOpprettFelter();
            periodetittelHorizontalLayout = leggTilAndrefelterOver(new PeriodetittelHorizontalLayout(periodetypeEnum));
            instansByggOppBinder();

            this.periodepostRedigeringsomraadeTilDialog.initierPeriodepostRedigeringsomraadeMal(this.periodepostTypeEnum,
                    this.periodeService,
                    this.periodetypeEnum);

            this.periodepostRedigerEntitetDialog = new RedigerEntitetDialog<>(this.periodepostService,
                    this.periodeService,
                    "Rediger periodepost",
                    "",
                    this.periodepostRedigeringsomraadeTilDialog,
                    this
            );

        }
    }


    @Override
    public void instansOppdaterEkstraRedigeringsfelter() {
        if (hentEntitet()==null) {
            periodetittelHorizontalLayout.oppdaterTittel("");
        } else {
            periodetittelHorizontalLayout.oppdaterTittel(hentEntitet().getDatoFraLocalDate());
        }
        instansOppdaterEkstraRedigeringsfelter_oppdaterPeriodepostGrid();
    }

    private void instansOppdaterEkstraRedigeringsfelter_oppdaterPeriodepostGrid() {
        if(hentEntitet()==null) {
            hovedKategorierGrid.setItems(new ArrayList<>());
        } else {
            hovedKategorierGrid.setItems(periodepostService.finnHovedperiodeposter(hentEntitet()));
        }
    }



    @Override
    public void instansOpprettFelter() {

        instansOpprettFelter_leggTilHovedTab();
        instansOpprettFelter_leggTilHovedkategorierTab();
        instansOpprettFelter_leggTilEkstraTab();

        settFokusKomponent(beskrivelseTextArea);

    }




    private void instansOpprettFelter_leggTilHovedkategorierTab() {
        String regnskaptabString = "Kategorier";

        hovedKategorierGrid = new Grid<>();
        hovedKategorierGrid.addColumn(p -> {
            return p.getKategori()!=null? p.getKategori().getTittel() : "";
        }).setHeader("Kategori").setWidth("150px");
        hovedKategorierGrid.addColumn(Periodepost::getSumBudsjettInteger).setHeader("Budsjett").setWidth("100px").setFlexGrow(0);
        hovedKategorierGrid.addColumn(Periodepost::getSumRegnskapInteger).setHeader("Regnskap").setWidth("100px").setFlexGrow(0);
        hovedKategorierGrid.addColumn(Periodepost::getBeskrivelseString).setHeader("Beskrivelse");
        hovedKategorierGrid.setSizeFull();


        hovedKategorierGrid.addItemDoubleClickListener(e -> {
            periodepostRedigerEntitetDialog.vis(e.getItem());
        });


        Gridkyklop.hent().tilpassKolonnerIFastradGrid(hovedKategorierGrid);
        hovedKategorierGrid.setMultiSort(true, Grid.MultiSortPriority.APPEND);

        leggTilRedigeringsfelter(regnskaptabString, hovedKategorierGrid);
        hentFormLayoutFraTab(regnskaptabString).setSizeFull();
    }

    private void instansOpprettFelter_leggTilHovedTab() {
        String hovedtabString ="Hoved";
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

        beskrivelseTextArea = leggTilRedigeringsfelt(hovedtabString, new TextArea("Beskrivelse"));
        beskrivelseTextArea.setMinRows(4);
        settColspan(beskrivelseTextArea,5);
    }

    private void instansOpprettFelter_leggTilEkstraTab() {
        String ekstratabString = "Ekstra";
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
            if (periodetypeEnum==PeriodetypeEnum.MAANEDSOVERSIKT) {
                datoTilDatePicker.setValue(Datokyklop.hent().finnSisteIMaaneden(datoFraDatePicker.getValue()));
            } else if (periodetypeEnum==PeriodetypeEnum.AARSOVERSIKT) {
                datoTilDatePicker.setValue(LocalDate.of(e.getValue().getYear(), 12,31));
            }
        });

        datoTilDatePicker = new DatePicker("Til");
        leggTilRedigeringsfelter(ekstratabString, periodetypeComboBox, datoFraDatePicker, datoTilDatePicker);

        leggTilDatofeltTidOpprettetOgRedigert(ekstratabString);
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
