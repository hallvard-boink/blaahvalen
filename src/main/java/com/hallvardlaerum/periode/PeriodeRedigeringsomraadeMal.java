package com.hallvardlaerum.periode;

import com.hallvardlaerum.libs.felter.Datokyklop;
import com.hallvardlaerum.libs.felter.HelTallMester;
import com.hallvardlaerum.libs.ui.RedigerEntitetDialog;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeAktig;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeMal;
import com.hallvardlaerum.periodepost.Periodepost;
import com.hallvardlaerum.periodepost.PeriodepostRedigeringsomraadeMal;
import com.hallvardlaerum.periodepost.PeriodepostServiceMal;
import com.hallvardlaerum.periodepost.PeriodepostTypeEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class PeriodeRedigeringsomraadeMal extends RedigeringsomraadeMal<Periode> implements RedigeringsomraadeAktig<Periode> {
    private PeriodetypeEnum periodetypeEnum;
    private Grid<Periodepost> periodepostGrid;
    private RedigerEntitetDialog<Periodepost, Periode> periodepostRedigerEntitetDialog;
    private PeriodepostRedigeringsomraadeMal periodepostRedigeringsomraadeTilDialog;
    private PeriodepostServiceMal periodepostService;
    private PeriodeServiceMal periodeService;

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

    public PeriodeRedigeringsomraadeMal() {

    }

    public void initierPeriodeRedigeringsomraadeMal(PeriodetypeEnum periodetypeEnum,
                                                    PeriodepostServiceMal periodepostService,
                                                    PeriodepostRedigeringsomraadeMal periodepostRedigeringsomraadeTilDialog,
                                                    PeriodeServiceMal periodeService){
        this.periodetypeEnum = periodetypeEnum;
        this.periodepostService = periodepostService;
        this.periodeService = periodeService;
        this.periodepostRedigeringsomraadeTilDialog = periodepostRedigeringsomraadeTilDialog;

        super.initRedigeringsomraadeMal();

        if (beskrivelseTextArea==null) {
            opprettPeriodepostGrid();
            instansOpprettFelter();
            periodetittelHorizontalLayout = leggTilAndrefelterOver(new PeriodetittelHorizontalLayout(periodetypeEnum));
            instansByggOppBinder();

            PeriodepostTypeEnum periodepostTypeEnum;
            if (periodetypeEnum == PeriodetypeEnum.AARSOVERSIKT) {
                periodepostTypeEnum = PeriodepostTypeEnum.AARSOVERSIKTPOST;
                settView(Allvitekyklop.hent().getAarsoversiktView());
            } else if (periodetypeEnum == PeriodetypeEnum.MAANEDSOVERSIKT) {
                periodepostTypeEnum = PeriodepostTypeEnum.MAANEDSOVERSIKTPOST;
                settView(Allvitekyklop.hent().getAarsoversiktView());
            } else {
                periodepostTypeEnum = null;
            }
            this.periodepostRedigeringsomraadeTilDialog.initierPeriodepostRedigeringsomraadeMal(periodepostTypeEnum,
                    periodeService,
                    periodetypeEnum);

            this.periodepostRedigerEntitetDialog = new RedigerEntitetDialog<>(periodepostService,
                    periodeService,
                    "Rediger periodepost",
                    "",
                    this.periodepostRedigeringsomraadeTilDialog
            );

        }
    }


    public void opprettPeriodepostGrid(){
        periodepostGrid = new Grid<>();
        periodepostGrid.addColumn(p -> {
            if(p.getKategori()!=null) {
                return p.getKategori().hentKortnavn();
            } else {
                return "";
            }
        }).setHeader("Kategori").setWidth("100px");
        periodepostGrid.addColumn(Periodepost::getSumBudsjettInteger).setHeader("Budsjett").setWidth("40px");
        periodepostGrid.addColumn(Periodepost::getSumRegnskapInteger).setHeader("Regnskap").setWidth("40px");
        periodepostGrid.addColumn(Periodepost::getBeskrivelseString).setHeader("Beskrivelse").setWidth("200px");
        periodepostGrid.setSizeFull();


        periodepostGrid.addItemDoubleClickListener(e -> {
            periodepostRedigerEntitetDialog.vis(e.getItem());
        });
    }


    @Override
    public void instansOppdaterEkstraRedigeringsfelter() {
        if (getEntitet()==null) {
            periodetittelHorizontalLayout.oppdaterTittel("");
        } else {
            periodetittelHorizontalLayout.oppdaterTittel(getEntitet().getDatoFraLocalDate());
        }
        oppdaterPeriodepostGrid();
    }

    private void oppdaterPeriodepostGrid() {
        if(getEntitet()==null) {
            periodepostGrid.setItems(new ArrayList<>());
            return;
        } else {
            periodepostGrid.setItems(hentPeriodepostListSortert(getEntitet()));
        }
    }

    public ArrayList<Periodepost> hentPeriodepostListSortert(Periode periode) {
        List<Periodepost> periodeposter = getEntitet().getPeriodeposterList();
        if (periodeposter==null) {
            return new ArrayList<>();
        } else {
            return new ArrayList<>(periodeposter
                    .stream()
                    .sorted(Comparator.comparing(Periodepost::getSumRegnskapInteger, Comparator.nullsLast(Comparator.reverseOrder()))
                            .thenComparing(Periodepost::getSumBudsjettInteger, Comparator.nullsLast(Comparator.reverseOrder())))
                    .toList());
        }
    }

    @Override
    public void instansOpprettFelter() {
        String hovedtabString ="Hoved";
        String postertabString = "Poster";
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

        beskrivelseTextArea = leggTilRedigeringsfelt(hovedtabString, new TextArea("Beskrivelse"));
        beskrivelseTextArea.setMinRows(4);
        settColspan(beskrivelseTextArea,5);

        leggTilRedigeringsfelter(postertabString,periodepostGrid);
        hentFormLayoutFraTab(postertabString).setSizeFull();

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
