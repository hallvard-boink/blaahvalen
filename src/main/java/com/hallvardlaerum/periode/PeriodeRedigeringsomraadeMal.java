package com.hallvardlaerum.periode;

import com.hallvardlaerum.libs.eksportimport.ExcelEksportkyklop;
import com.hallvardlaerum.libs.felter.Datokyklop;
import com.hallvardlaerum.libs.felter.HelTallMester;
import com.hallvardlaerum.libs.ui.*;
import com.hallvardlaerum.periodepost.Periodepost;
import com.hallvardlaerum.periodepost.PeriodepostRedigeringsomraadeMal;
import com.hallvardlaerum.periodepost.PeriodepostServiceMal;
import com.hallvardlaerum.periodepost.PeriodepostTypeEnum;
import com.hallvardlaerum.skalTilHavaara.HallvardsIntegerSpan;
import com.hallvardlaerum.verktoy.testing.PeriodeTester;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class PeriodeRedigeringsomraadeMal extends RedigeringsomraadeMal<Periode> implements RedigeringsomraadeAktig<Periode> {
    protected PeriodetypeEnum periodetypeEnum;
    protected PeriodepostTypeEnum periodepostTypeEnum;
    protected String hovedtabString = "Hoved";
    protected String kategoriertabString = "Kategorier";

    // === Andre objekter som trengs her ===
    protected RedigerEntitetDialog<Periodepost, Periode> periodepostRedigerEntitetDialog;
    protected PeriodepostRedigeringsomraadeMal periodepostRedigeringsomraadeTilDialog;
    protected PeriodepostServiceMal periodepostService;
    protected PeriodeServiceMal periodeService;

    // === GRID ===
    protected Grid<Periodepost> kategorierGrid;


    // === FELTER ===
    protected ComboBox<PeriodetypeEnum> periodetypeComboBox;
    protected DatePicker datoFraDatePicker;
    protected DatePicker datoTilDatePicker;

    protected TextArea beskrivelseTextArea;

    protected HallvardsIntegerSpan sumBudsjettInntekterSpan;
    protected HallvardsIntegerSpan sumBudsjettUtgifterSpan;
    protected HallvardsIntegerSpan sumBudsjettResultatSpan;

    protected HallvardsIntegerSpan sumRegnskapInntekterSpan;
    protected HallvardsIntegerSpan sumRegnskapUtgifterSpan;
    protected HallvardsIntegerSpan sumRegnskapResultatSpan;
    protected HallvardsIntegerSpan sumUkategorisertInnSpan;
    protected HallvardsIntegerSpan sumUkategorisertUtSpan;

    protected HallvardsIntegerSpan sumDifferanseBudsjettRegnskapInntekterSpan;
    protected HallvardsIntegerSpan sumDifferanseBudsjettRegnskapUtgifterSpan;
    protected HallvardsIntegerSpan sumDifferanseBudsjettRegnskapResultatSpan;

    protected HallvardsIntegerSpan sumRegnskapInntekterMedOverfoeringerSpan;
    protected HallvardsIntegerSpan sumRegnskapUtgifterMedOverfoeringerSpan;
    protected HallvardsIntegerSpan sumRegnskapResultatMedOverfoeringerSpan;


    protected PeriodetittelHorizontalLayout periodetittelHorizontalLayout;



// ===========================
// region 0 Constructor og init
// ===========================



    public PeriodeRedigeringsomraadeMal() {

    }



    public void initierPeriodeRedigeringsomraadeMal(PeriodetypeEnum periodetypeEnum,
                                                    PeriodepostServiceMal periodepostService,
                                                    PeriodepostRedigeringsomraadeMal periodepostRedigeringsomraadeTilDialog,
                                                    PeriodeServiceMal periodeService,
                                                    PeriodepostTypeEnum periodepostTypeEnum,
                                                    ViewmalAktig<Periode, PeriodeRepository> viewmalAktig) {
        this.periodetypeEnum = periodetypeEnum;
        this.periodepostService = periodepostService;
        this.periodeService = periodeService;
        this.periodepostRedigeringsomraadeTilDialog = periodepostRedigeringsomraadeTilDialog;
        this.periodepostTypeEnum = periodepostTypeEnum;
        this.settView(viewmalAktig);

        super.initRedigeringsomraadeMal();

        if (beskrivelseTextArea == null) {
            instansOpprettFelter();
            periodetittelHorizontalLayout = leggTilAndrefelterOver(new PeriodetittelHorizontalLayout(periodetypeEnum));
            instansByggOppBinder();


            this.periodepostRedigerEntitetDialog = new RedigerEntitetDialog<>(this.periodepostService,
                    this.periodeService,
                    "Rediger " + periodepostTypeEnum.getTittel() + " fra " + periodetypeEnum.getTittel(),
                    "",
                    this.periodepostRedigeringsomraadeTilDialog,
                    this
            );

        }
    }

// endregion





// ===========================
// region 1 Opprett felter
// ===========================


    @Override
    public void instansOpprettFelter() {
        instansOpprettFelter_leggTilHovedTab();
        instansOpprettFelter_leggTilKategorierTab();
        instansOpprettFelter_leggTilEkstraTab();
        //testing_leggTilSjekkSummerButton();
        settFokusKomponent(beskrivelseTextArea);

    }



    protected void instansOpprettFelter_leggTilKategorierTab() {
        kategorierGrid = new Grid<>();
        kategorierGrid.addColumn(p -> p.getKategori() != null ? p.getKategori().getTittel() : "")
                .setHeader("Kategori").setWidth("150px");
        kategorierGrid.addColumn(Periodepost::getSumBudsjettInteger).setHeader("Budsjett").setWidth("150px").setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.END).setRenderer(opprettSumBudsjettRenderer());
        kategorierGrid.addColumn(Periodepost::getSumRegnskapInteger).setHeader("Regnskap").setWidth("150px").setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.END).setRenderer(opprettSumRegnskapRenderer());
        kategorierGrid.addColumn(Periodepost::getBeskrivelseString).setHeader("Beskrivelse");
        kategorierGrid.setSizeFull();
        kategorierGrid.addItemDoubleClickListener(e -> periodepostRedigerEntitetDialog.vis(e.getItem(), "Rediger periodepost for kategorien " + e.getItem().getKategori().getTittel(),null));

        Gridkyklop.hent().tilpassKolonnerIFastradGrid(kategorierGrid);
        kategorierGrid.setMultiSort(true, Grid.MultiSortPriority.APPEND);


        leggTilRedigeringsfelter(kategoriertabString, new VerticalLayout(opprettTilEksporterGridraderTilExcelButton(), kategorierGrid));
        hentFormLayoutFraTab(kategoriertabString).setSizeFull();
    }

    protected ComponentRenderer<Span, Periodepost> opprettSumRegnskapRenderer() {
        return new ComponentRenderer<>(periodepost -> opprettSpanFraInteger(periodepost.getSumRegnskapInteger()));
    }

    protected ComponentRenderer<Span, Periodepost> opprettSumBudsjettRenderer() {
        return new ComponentRenderer<>(periodepost -> opprettSpanFraInteger(periodepost.getSumBudsjettInteger()));
    }



    protected void instansOpprettFelter_leggTilHovedTab() {


        Span innMerkelappSpan = new Span("Inn");
        innMerkelappSpan.addClassNames(LumoUtility.TextAlignment.RIGHT);

        Span utMerkelappSpan = new Span("Ut");
        utMerkelappSpan.addClassNames(LumoUtility.TextAlignment.RIGHT);

        Span resultatMerkelappSpan = new Span("Resultat");
        resultatMerkelappSpan.addClassNames(LumoUtility.TextAlignment.RIGHT);


        Span regnskapMerkelappSpan = new Span("Regnskap");
        regnskapMerkelappSpan.addClassNames(LumoUtility.TextAlignment.RIGHT, LumoUtility.TextColor.TERTIARY);

        Span budsjettMerkelappSpan = new Span("Budsjett");
        budsjettMerkelappSpan.addClassNames(LumoUtility.TextAlignment.RIGHT, LumoUtility.TextColor.TERTIARY);

        Span differanseMerkelappSpan = new Span("Differanse");
        differanseMerkelappSpan.addClassNames(LumoUtility.TextAlignment.RIGHT, LumoUtility.TextColor.TERTIARY);

        Span ukategorisertMerkelappSpan = new Span("Ukategorisert");
        ukategorisertMerkelappSpan.addClassNames(LumoUtility.TextAlignment.RIGHT, LumoUtility.TextColor.TERTIARY);

        Span regnskapMedOverfoeringerMerkelappSpan = new Span("Regnskap med overføringer");
        regnskapMedOverfoeringerMerkelappSpan.addClassNames(LumoUtility.TextAlignment.RIGHT, LumoUtility.TextColor.TERTIARY);

        sumBudsjettInntekterSpan = new HallvardsIntegerSpan();
        sumBudsjettUtgifterSpan = new HallvardsIntegerSpan();
        sumBudsjettResultatSpan = new HallvardsIntegerSpan();

        sumRegnskapInntekterSpan = new HallvardsIntegerSpan();
        sumRegnskapUtgifterSpan = new HallvardsIntegerSpan();
        sumRegnskapResultatSpan = new HallvardsIntegerSpan();

        sumDifferanseBudsjettRegnskapInntekterSpan = new HallvardsIntegerSpan();
        sumDifferanseBudsjettRegnskapInntekterSpan.visNegativeTalliGroent(true);
        sumDifferanseBudsjettRegnskapUtgifterSpan = new HallvardsIntegerSpan();
        sumDifferanseBudsjettRegnskapResultatSpan = new HallvardsIntegerSpan();

        sumUkategorisertInnSpan = new HallvardsIntegerSpan();
        sumUkategorisertUtSpan = new HallvardsIntegerSpan();

        sumRegnskapInntekterMedOverfoeringerSpan = new HallvardsIntegerSpan();
        sumRegnskapUtgifterMedOverfoeringerSpan = new HallvardsIntegerSpan();
        sumRegnskapResultatMedOverfoeringerSpan = new HallvardsIntegerSpan();

        leggTilRedigeringsfelter(hovedtabString, new Span(""));
        leggTilRedigeringsfelter(hovedtabString, new Span(""), budsjettMerkelappSpan, regnskapMerkelappSpan, differanseMerkelappSpan, ukategorisertMerkelappSpan, regnskapMedOverfoeringerMerkelappSpan);
        leggTilRedigeringsfelter(hovedtabString, innMerkelappSpan, sumBudsjettInntekterSpan, sumRegnskapInntekterSpan, sumDifferanseBudsjettRegnskapInntekterSpan, sumUkategorisertInnSpan, sumRegnskapInntekterMedOverfoeringerSpan);
        leggTilRedigeringsfelter(hovedtabString, utMerkelappSpan, sumBudsjettUtgifterSpan, sumRegnskapUtgifterSpan, sumDifferanseBudsjettRegnskapUtgifterSpan, sumUkategorisertUtSpan, sumRegnskapUtgifterMedOverfoeringerSpan);
        leggTilRedigeringsfelter(hovedtabString, resultatMerkelappSpan, sumBudsjettResultatSpan, sumRegnskapResultatSpan, sumDifferanseBudsjettRegnskapResultatSpan, new Span(""), sumRegnskapResultatMedOverfoeringerSpan);

        beskrivelseTextArea = leggTilRedigeringsfelt(hovedtabString, new TextArea("Beskrivelse"));
        beskrivelseTextArea.setMinRows(4);

        settColspan(beskrivelseTextArea, 5);

    }

    protected void instansOpprettFelter_leggTilEkstraTab() {
        String ekstratabString = "Ekstra";
        periodetypeComboBox = new ComboBox<>("Periodetype");
        periodetypeComboBox.setItems(PeriodetypeEnum.values());
        periodetypeComboBox.setItemLabelGenerator(PeriodetypeEnum::getTittel);
        periodetypeComboBox.setEnabled(false);
        datoFraDatePicker = new DatePicker("Fra");
        datoFraDatePicker.addValueChangeListener(e -> {
            if (e.getValue() == null) {
                return;
            }

            //Flytt dato til første i måneden og datoTil til siste i måneden
            LocalDate dato = e.getValue();
            datoFraDatePicker.setValue(LocalDate.of(dato.getYear(), dato.getMonth(), 1));
            if (periodetypeEnum == PeriodetypeEnum.MAANEDSOVERSIKT) {
                datoTilDatePicker.setValue(Datokyklop.hent().finnSisteIMaaneden(datoFraDatePicker.getValue()));
            } else if (periodetypeEnum == PeriodetypeEnum.AARSOVERSIKT) {
                datoTilDatePicker.setValue(LocalDate.of(e.getValue().getYear(), 12, 31));
            }
        });

        datoTilDatePicker = new DatePicker("Til");
        leggTilRedigeringsfelter(ekstratabString, periodetypeComboBox, datoFraDatePicker, datoTilDatePicker);

        leggTilDatofeltTidOpprettetOgRedigert(ekstratabString);
    }

// endregion





// ===========================
// region 2 Oppdatering og Binder
// ===========================


    @Override
    public void instansByggOppBinder() {
        Binder<Periode> binder = hentBinder();
        binder.bind(periodetypeComboBox, Periode::getPeriodetypeEnum, Periode::setPeriodetypeEnum);
        binder.bind(datoFraDatePicker, Periode::getDatoFraLocalDate, Periode::setDatoFraLocalDate);
        binder.bind(datoTilDatePicker, Periode::getDatoTilLocalDate, Periode::setDatoTilLocalDate);
        binder.bind(beskrivelseTextArea, Periode::getBeskrivelseString, Periode::setBeskrivelseString);
    }



    @Override
    public void instansOppdaterEkstraRedigeringsfelter() {
        instansOppdaterEkstraRedigeringsfelter_oppdaterTittelMedTidsperiode();
        instansOppdaterEkstraRedigeringsfelter_oppdaterPeriodepostGrid();
        instansOppdaterEkstraRedigeringsfelter_hentSummer();
    }

    private void instansOppdaterEkstraRedigeringsfelter_oppdaterTittelMedTidsperiode() {
        if (hentEntitet() == null) {
            periodetittelHorizontalLayout.oppdaterTittel("");
        } else {
            periodetittelHorizontalLayout.oppdaterTittel(hentEntitet().getDatoFraLocalDate());
        }
    }

    private void instansOppdaterEkstraRedigeringsfelter_oppdaterPeriodepostGrid() {
        if (hentEntitet() == null) {
            kategorierGrid.setItems(new ArrayList<>());
        } else {
            kategorierGrid.setItems(periodepostService.finnHovedperiodeposter(hentEntitet()));
        }
    }

    private void instansOppdaterEkstraRedigeringsfelter_hentSummer() {
        Periode periode = hentEntitet();
        if (periode == null) {
            sumBudsjettInntekterSpan.settInteger(null);
            sumBudsjettUtgifterSpan.settInteger(null);
            sumBudsjettResultatSpan.settInteger(null);

            sumRegnskapInntekterSpan.settInteger(null);
            sumRegnskapUtgifterSpan.settInteger(null);
            sumRegnskapResultatSpan.settInteger(null);

            sumDifferanseBudsjettRegnskapInntekterSpan.settInteger(null);
            sumDifferanseBudsjettRegnskapUtgifterSpan.settInteger(null);
            sumDifferanseBudsjettRegnskapResultatSpan.settInteger(null);

            sumUkategorisertInnSpan.settInteger(null);
            sumUkategorisertUtSpan.settInteger(null);

            sumRegnskapInntekterMedOverfoeringerSpan.settInteger(null);
            sumRegnskapUtgifterMedOverfoeringerSpan.settInteger(null);
            sumRegnskapResultatMedOverfoeringerSpan.settInteger(null);

        } else {
            sumBudsjettInntekterSpan.settInteger(periode.getSumBudsjettInntektInteger());
            sumBudsjettUtgifterSpan.settInteger(periode.getSumBudsjettUtgifterInteger());
            sumBudsjettResultatSpan.settInteger(periode.getSumBudsjettResultatInteger());

            sumRegnskapInntekterSpan.settInteger(periode.getSumRegnskapInntektInteger());
            sumRegnskapUtgifterSpan.settInteger(periode.getSumRegnskapUtgifterInteger());
            sumRegnskapResultatSpan.settInteger(periode.getSumRegnskapResultatInteger());

            sumDifferanseBudsjettRegnskapInntekterSpan.settInteger(periode.getSumDifferanseBudsjettRegnskapInntekter());
            sumDifferanseBudsjettRegnskapUtgifterSpan.settInteger(periode.getSumDifferanseBudsjettRegnskapUtgifter());
            sumDifferanseBudsjettRegnskapResultatSpan.settInteger(periode.getSumDifferanseBudsjettRegnskapResultat());

            sumUkategorisertInnSpan.settInteger(periode.getSumUkategorisertInnInteger());
            sumUkategorisertUtSpan.settInteger(periode.getSumUkategorisertUtInteger());

            sumRegnskapInntekterMedOverfoeringerSpan.settInteger(periode.getSumRegnskapInntektMedOverfoeringerInteger());
            sumRegnskapUtgifterMedOverfoeringerSpan.settInteger(periode.getSumRegnskapUtgifterMedOverfoeringerInteger());
            sumRegnskapResultatMedOverfoeringerSpan.settInteger(periode.getSumRegnskapResultatMedOverfoeringerInteger());
        }
    }

// endregion





// ===========================
// region 9 Testing
// ===========================

    protected void testing_leggTilSjekkSummerButton() {
        Button sjekkSummerButton = new Button("Sjekk summer");
        sjekkSummerButton.addClickListener(e -> new PeriodeTester(hentEntitet()));
        leggTilRedigeringsfelter(hovedtabString, sjekkSummerButton);
    }



// endregion




// ===========================
// region 8 Hjelpeprosedyrer
// ===========================



    private Button opprettTilEksporterGridraderTilExcelButton() {
        Button eksporerGridraderTilExcelButton = new Button("Eksporter grid til Excel");
        eksporerGridraderTilExcelButton.addClickListener(e -> {
            List<Periodepost> rader = kategorierGrid.getListDataView().getItems().toList();
            ExcelEksportkyklop.hent().eksporterArrayListAvEntiteterSomXLS(new ArrayList<>(rader), "GridInnhold_Periodeposter_" + Datokyklop.hent().hentNaavaerendeTidspunktSomDatoTidSekund() + ".xlsx");
        });
        return eksporerGridraderTilExcelButton;
    }

    protected Span opprettSpanFraInteger(Integer integer) {
        if (integer == null) {
            return new Span("");
        } else {
            return new Span(HelTallMester.formaterIntegerSomStortTall(integer));
        }
    }


// endregion











}
