package com.hallvardlaerum.periodepost;

import com.hallvardlaerum.grunndata.kategori.Kategori;
import com.hallvardlaerum.grunndata.kategori.KategoriService;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeAktig;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeMal;
import com.hallvardlaerum.periode.Periode;
import com.hallvardlaerum.periode.PeriodeServiceMal;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import com.hallvardlaerum.post.Post;
import com.hallvardlaerum.post.PostklasseEnum;
import com.hallvardlaerum.post.normalpost.NormalpostService;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import java.util.ArrayList;
import java.util.List;

public class PeriodepostRedigeringsomraadeMal extends RedigeringsomraadeMal<Periodepost> implements RedigeringsomraadeAktig<Periodepost> {
    private PeriodepostTypeEnum periodepostTypeEnum;
    private PeriodetypeEnum periodetypeEnum;
    private KategoriService kategoriService;
    private PeriodeServiceMal periodeServiceMal;
    private NormalpostService normalpostService;

    //== FELTENE ===
    private ComboBox<PeriodepostTypeEnum> periodeposttypeEnumComboBox = null;
    private ComboBox<Kategori> kategoriComboBox;
    private HallvardsSpan sumBudsjettSpan;
    private HallvardsSpan sumRegnskapSpan;
    private HallvardsSpan sumDifferanseSpan;
    private ComboBox<Periode> periodeComboBox;
    private TextField tittelTextField;
    private TextArea beskrivelseTextArea;

    private Grid<Post> normalposterGrid;
    private Grid<Post> budsjettposterGrid;


    public PeriodepostRedigeringsomraadeMal() {
        super();

    }

    public void initierPeriodepostRedigeringsomraadeMal(PeriodepostTypeEnum periodepostTypeEnum,
                                                        PeriodeServiceMal periodeServiceMal,
                                                        PeriodetypeEnum periodetypeEnum) {
        this.periodepostTypeEnum = periodepostTypeEnum;
        this.periodeServiceMal = periodeServiceMal;
        this.periodetypeEnum = periodetypeEnum;
        this.normalpostService = Allvitekyklop.hent().getNormalpostService();
        this.kategoriService = Allvitekyklop.hent().getKategoriService();


        if (periodeposttypeEnumComboBox==null) {
            super.initRedigeringsomraadeMal(); //Ny binder
            instansOpprettFelter();
            instansByggOppBinder();
        }
    }


    @Override
    public void instansOppdaterEkstraRedigeringsfelter() {
        Periodepost periodepost = hentEntitet();
        sumBudsjettSpan.settInteger(periodepost.getSumBudsjettInteger());
        sumRegnskapSpan.settInteger(periodepost.getSumRegnskapInteger());
        sumDifferanseSpan.settDifferanseInteger(periodepost.getSumBudsjettInteger(), periodepost.getSumRegnskapInteger());

        List<Post> normalposterList = new ArrayList<>();
        List<Post> budsjettposterList = new ArrayList<>();

        if (periodepostTypeEnum== PeriodepostTypeEnum.PERIODEOVERSIKTPOST) {
            normalposterList = normalpostService.finnPosterIKostnadspakken(periodepost);
            normalposterGrid.setItems(normalposterList);

        } else {
            if (periodepost.getPeriode()!=null) {
                normalposterList = normalpostService.finnPosterFraDatoTilDatoPostklasseHovedkategori(
                        periodepost.getPeriode().getDatoFraLocalDate(),
                        periodepost.getPeriode().getDatoTilLocalDate(),
                        PostklasseEnum.NORMALPOST,
                        periodepost.getKategori()
                );
                budsjettposterList = normalpostService.finnPosterFraDatoTilDatoPostklasseHovedkategori(
                        periodepost.getPeriode().getDatoFraLocalDate(),
                        periodepost.getPeriode().getDatoTilLocalDate(),
                        PostklasseEnum.BUDSJETTPOST,
                        periodepost.getKategori()
                );
            }
            normalposterGrid.setItems(normalposterList);
            budsjettposterGrid.setItems(budsjettposterList);
        }


    }



    @Override
    public void instansOpprettFelter() {

        instansOpprettFelter_leggTilOverfelter();
        instansOpprettFelter_opprettNormalposterTab();
        if (periodepostTypeEnum!= PeriodepostTypeEnum.PERIODEOVERSIKTPOST) {
            instansOpprettFelter_opprettBudsjettposterTab();
        }
        instansOpprettFelter_opprettEkstraTab();
        //TODO: Her dukker det opp en "Hoved"-tab vi ikke skal ha. Se på logikken rundt tabe'er i Vaadin en gang til.

        setFokusComponent(beskrivelseTextArea);
    }

    private void instansOpprettFelter_leggTilOverfelter() {
        Span sumBudsjettLabelSpan = new Span("Sum budsjett");
        Span sumRegnskapLabelSpan = new Span("Sum regnskap");
        Span sumDifferanseLabelSpan = new Span("Differanse");

        sumBudsjettSpan = new HallvardsSpan();
        sumRegnskapSpan = new HallvardsSpan();
        sumDifferanseSpan = new HallvardsSpan();

        if (periodepostTypeEnum==PeriodepostTypeEnum.PERIODEOVERSIKTPOST) {
            tittelTextField = new TextField();
            tittelTextField.setPlaceholder("Tittel");
            tittelTextField.setWidthFull();
        }
        beskrivelseTextArea = new TextArea();
        beskrivelseTextArea.setMinRows(2);
        beskrivelseTextArea.setPlaceholder("Beskrivelse");
        beskrivelseTextArea.setSizeFull();

        HorizontalLayout kol1HorizontalLayout = new HorizontalLayout();
        kol1HorizontalLayout.setWidth("400px");
        kol1HorizontalLayout.setFlexGrow(0);
        //kol1HorizontalLayout.setHeight("50px");


        VerticalLayout kol1aVerticalLayout = new VerticalLayout();
        kol1aVerticalLayout.setSizeFull();
        kol1aVerticalLayout.add(sumBudsjettLabelSpan, sumRegnskapLabelSpan, sumDifferanseLabelSpan);

        VerticalLayout kol1bVerticalLayout = new VerticalLayout();
        kol1bVerticalLayout.setSizeFull();
        kol1bVerticalLayout.setAlignItems(Alignment.END);
        kol1bVerticalLayout.add(sumBudsjettSpan, sumRegnskapSpan, sumDifferanseSpan);


        kol1HorizontalLayout.add(kol1aVerticalLayout, kol1bVerticalLayout);

        VerticalLayout kol2VerticalLayout = new VerticalLayout();
        kol2VerticalLayout.setSizeFull();

        if (periodepostTypeEnum==PeriodepostTypeEnum.PERIODEOVERSIKTPOST) {
            kol2VerticalLayout.add(tittelTextField);
        }
        kol2VerticalLayout.add(beskrivelseTextArea);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeFull();
        horizontalLayout.add(kol1HorizontalLayout,kol2VerticalLayout);

        leggTilAndrefelterOver(horizontalLayout);

    }

    private void instansOpprettFelter_opprettBudsjettposterTab() {
        String budsjettpostertabString = "Budsjett";
        budsjettposterGrid = opprettStandardPostGrid();
        leggTilRedigeringsfelt(budsjettpostertabString, budsjettposterGrid);

    }

    private void instansOpprettFelter_opprettNormalposterTab(){
        String normalposterTabString = "Regnskap";
        normalposterGrid = opprettStandardPostGrid();
        leggTilRedigeringsfelt(normalposterTabString, normalposterGrid);
    }

    private Grid<Post> opprettStandardPostGrid() {
        Grid<Post> postGrid = new Grid<>();
        postGrid.addColumn(p -> p.getKategori()!=null? p.getKategori().hentKortnavn() : "").setHeader("Kategori");
        postGrid.addColumn(Post::getDatoLocalDate).setHeader("Dato").setWidth("150px").setFlexGrow(0);
        postGrid.addColumn(p -> {
            if (p.getInnPaaKontoInteger()!=null && p.getInnPaaKontoInteger()>0) {
                return p.getInnPaaKontoInteger();
            } else {
                return p.getUtFraKontoInteger();
            }
        } ).setHeader("Sum").setWidth("100px").setFlexGrow(0).setTextAlign(ColumnTextAlign.END);
        postGrid.addColumn(Post::getBeskrivelseString).setHeader("Beskrivelse");
        return postGrid;
    }

    private void instansOpprettFelter_opprettEkstraTab() {
        String ekstratabString = "Ekstra";

        periodeposttypeEnumComboBox = new ComboBox<>("Type");
        periodeposttypeEnumComboBox.setItemLabelGenerator(PeriodepostTypeEnum::getTittel);
        periodeposttypeEnumComboBox.setItems(PeriodepostTypeEnum.values());

        periodeComboBox = new ComboBox<>("Periode");
        periodeComboBox.setItemLabelGenerator(Periode::hentBeskrivendeNavn);
        periodeComboBox.setItems(periodeServiceMal.finnAlleEgndePerioder(periodetypeEnum));

        kategoriComboBox = new ComboBox<>("Kategori");
        kategoriComboBox.setItemLabelGenerator(Kategori::hentKortnavn);
        kategoriComboBox.setItems(kategoriService.finnAlle());

        leggTilRedigeringsfelter(ekstratabString, periodeposttypeEnumComboBox, kategoriComboBox, periodeComboBox);
    }

    @Override
    public void instansByggOppBinder() {
        Binder<Periodepost> binder = super.hentBinder();
        binder.bind(periodeposttypeEnumComboBox, Periodepost::getPeriodepostTypeEnum, Periodepost::setPeriodepostTypeEnum);
        binder.bind(periodeComboBox, Periodepost::getPeriode, Periodepost::setPeriode);
        binder.bind(kategoriComboBox, Periodepost::getKategori, Periodepost::setKategori);
        binder.bind(beskrivelseTextArea, Periodepost::getBeskrivelseString, Periodepost::setBeskrivelseString);
        if (periodepostTypeEnum==PeriodepostTypeEnum.PERIODEOVERSIKTPOST) {
            binder.bind(tittelTextField, Periodepost::getTittelString, Periodepost::setTittelString);
        }
    }


}
