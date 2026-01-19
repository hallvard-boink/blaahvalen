package com.hallvardlaerum.periodepost;

import com.hallvardlaerum.kategori.Kategori;
import com.hallvardlaerum.kategori.KategoriService;
import com.hallvardlaerum.libs.ui.Gridkyklop;
import com.hallvardlaerum.libs.ui.RedigerEntitetDialog;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeAktig;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeMal;
import com.hallvardlaerum.periode.Periode;
import com.hallvardlaerum.periode.PeriodeServiceMal;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import com.hallvardlaerum.post.Post;
import com.hallvardlaerum.post.PostklasseEnum;
import com.hallvardlaerum.post.normalpost.NormalpostService;
import com.hallvardlaerum.skalTilHavaara.HallvardsIntegerSpan;
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
    protected PeriodepostTypeEnum periodepostTypeEnum;
    protected PeriodetypeEnum periodetypeEnum;
    protected KategoriService kategoriService;
    protected PeriodeServiceMal periodeServiceMal;
    protected NormalpostService normalpostService;
    protected RedigerEntitetDialog<Post,Periodepost> normalPostRedigerEntitetDialog;
    protected RedigerEntitetDialog<Post,Periodepost> budsjettPostRedigerEntitetDialog;

    // === TABS OG GRIDS ===
    protected final String ekstratabString = "Ekstra";
    protected final String normalposterTabString = "Regnskap";
    protected Grid<Post> normalposterGrid;
    protected Grid<Post> budsjettposterGrid;

    // === FELTER ===
    protected ComboBox<PeriodepostTypeEnum> periodeposttypeEnumComboBox = null;
    protected ComboBox<Kategori> kategoriComboBox;
    protected HallvardsIntegerSpan sumBudsjettSpan;
    protected HallvardsIntegerSpan sumRegnskapSpan;
    protected HallvardsIntegerSpan sumDifferanseSpan;
    protected ComboBox<Periode> periodeComboBox;
    protected TextField tittelTextField;
    protected TextArea beskrivelseTextArea;

    // === MERKELAPPER ===
    protected final Span sumBudsjettLabelSpan = new Span("Sum budsjett");
    protected final Span sumRegnskapLabelSpan = new Span("Sum regnskap");
    protected final Span sumDifferanseLabelSpan = new Span("Differanse");


    public PeriodepostRedigeringsomraadeMal() {
        super();
    }

    public void initierPeriodepostRedigeringsomraadeMal(
        PeriodepostTypeEnum periodepostTypeEnum, PeriodeServiceMal periodeServiceMal,
        PeriodetypeEnum periodetypeEnum)
    {
        this.periodepostTypeEnum = periodepostTypeEnum;
        this.periodeServiceMal = periodeServiceMal;
        this.periodetypeEnum = periodetypeEnum;
        this.normalpostService = Allvitekyklop.hent().getNormalpostService();
        this.kategoriService = Allvitekyklop.hent().getKategoriService();

        if (periodeposttypeEnumComboBox == null) {
            super.initRedigeringsomraadeMal(); //Ny binder
            instansOpprettFelter();
            instansByggOppBinder();
        }
    }


    @Override
    public void instansOppdaterEkstraRedigeringsfelter() {
        Periodepost periodepost = hentEntitet();
        if (periodepost==null) {
            sumBudsjettSpan.settInteger(0);
            sumRegnskapSpan.settInteger(0);
            sumDifferanseSpan.settInteger(0);
            normalposterGrid.setItems(new ArrayList<>());
            budsjettposterGrid.setItems(new ArrayList<>());
        } else {
            sumBudsjettSpan.settInteger(periodepost.getSumBudsjettInteger());
            sumRegnskapSpan.settInteger(periodepost.getSumRegnskapInteger());
            sumDifferanseSpan.settDifferanseInteger(periodepost.getSumBudsjettInteger(), periodepost.getSumRegnskapInteger());

            List<Post> normalposterList = new ArrayList<>();
            List<Post> budsjettposterList = new ArrayList<>();

            if (periodepost.getPeriode() != null) {
                normalposterList = normalpostService.finnPostEtterFraDatoTilDatoPostklasseHovedkategori(
                        periodepost.getPeriode().getDatoFraLocalDate(),
                        periodepost.getPeriode().getDatoTilLocalDate(),
                        PostklasseEnum.NORMALPOST,
                        periodepost.getKategori()
                );
                budsjettposterList = normalpostService.finnPostEtterFraDatoTilDatoPostklasseHovedkategori(
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
        instansOpprettFelter_opprettFellesFelter();
        instansOpprettFelter_leggTilOverfelter();
        instansOpprettFelter_opprettNormalposterTab();
        instansOpprettFelter_opprettBudsjettposterTab();
        instansOpprettFelter_opprettEkstraTab();

        settFokusKomponent(beskrivelseTextArea);

    }

    protected void instansOpprettFelter_opprettFellesFelter() {
        periodeposttypeEnumComboBox = new ComboBox<>("Type");
        periodeposttypeEnumComboBox.setItemLabelGenerator(PeriodepostTypeEnum::getTittel);
        periodeposttypeEnumComboBox.setItems(PeriodepostTypeEnum.values());

        periodeComboBox = new ComboBox<>("Periode");
        periodeComboBox.setItemLabelGenerator(Periode::hentBeskrivendeNavn);
        periodeComboBox.setItems(periodeServiceMal.finnAlleEgnedePerioder(periodetypeEnum));

        kategoriComboBox = new ComboBox<>("Kategori");
        kategoriComboBox.setItemLabelGenerator(Kategori::hentKortnavn);
        if (periodepostTypeEnum==PeriodepostTypeEnum.PERIODEOVERSIKTPOST) {
            kategoriComboBox.setItems(kategoriService.finnAlleUnderkategorier());
        } else {
            kategoriComboBox.setItems(kategoriService.finnAlleHovedkategorier());
        }

        sumBudsjettSpan = new HallvardsIntegerSpan();
        sumRegnskapSpan = new HallvardsIntegerSpan();
        sumDifferanseSpan = new HallvardsIntegerSpan();

        beskrivelseTextArea = new TextArea();
        beskrivelseTextArea.setMinRows(2);
        beskrivelseTextArea.setSizeFull();

        tittelTextField = new TextField("Tittel");
    }


    protected void instansOpprettFelter_leggTilOverfelter() {
        beskrivelseTextArea.setPlaceholder("Beskrivelse");

        HorizontalLayout kol1HorizontalLayout = new HorizontalLayout();
        kol1HorizontalLayout.setWidth("400px");
        kol1HorizontalLayout.setFlexGrow(0);

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
        kol2VerticalLayout.add(beskrivelseTextArea);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeFull();
        horizontalLayout.add(kol1HorizontalLayout, kol2VerticalLayout);

        leggTilAndrefelterOver(horizontalLayout);
    }


    protected void instansOpprettFelter_opprettBudsjettposterTab() {
        String budsjettpostertabString = "Budsjett";
        budsjettposterGrid = opprettStandardPostGrid();
        leggTilRedigeringsfelt(budsjettpostertabString, budsjettposterGrid);
        hentFormLayoutFraTab(budsjettpostertabString).setSizeFull();
    }

    protected void instansOpprettFelter_opprettNormalposterTab() {
        normalposterGrid = opprettStandardPostGrid();
        normalposterGrid.setSizeFull();
        leggTilRedigeringsfelt(normalposterTabString, normalposterGrid);
        hentFormLayoutFraTab(normalposterTabString).setSizeFull();
    }

    protected Grid<Post> opprettStandardPostGrid() {
        Grid<Post> postGrid = new Grid<>();
        postGrid.addColumn(p -> p.getKategori() != null ? p.getKategori().hentKortnavn() : "").setHeader("Kategori").setWidth("250px").setFlexGrow(0);
        postGrid.addColumn(Post::getDatoLocalDate).setHeader("Dato").setWidth("150px").setFlexGrow(0);
        postGrid.addColumn(Post::getTekstFraBankenString).setHeader("Tekst fra banken");
        postGrid.addColumn(p -> {
            if (p.getInnPaaKontoInteger() != null && p.getInnPaaKontoInteger() > 0) {
                return p.getInnPaaKontoInteger();
            } else {
                return p.getUtFraKontoInteger();
            }
        }).setHeader("Sum").setWidth("100px").setFlexGrow(0).setTextAlign(ColumnTextAlign.END);
        postGrid.addColumn(Post::getBeskrivelseString).setHeader("Beskrivelse");
        Gridkyklop.hent().tilpassKolonnerIFastradGrid(postGrid);
        postGrid.setSizeFull();
        return postGrid;
    }

    protected void instansOpprettFelter_opprettEkstraTab() {
        leggTilRedigeringsfelter(ekstratabString, periodeposttypeEnumComboBox, kategoriComboBox, periodeComboBox);
    }

    @Override
    public void instansByggOppBinder() {
        Binder<Periodepost> binder = super.hentBinder();
        binder.bind(periodeposttypeEnumComboBox, Periodepost::getPeriodepostTypeEnum, Periodepost::setPeriodepostTypeEnum);
        binder.bind(periodeComboBox, Periodepost::getPeriode, Periodepost::setPeriode);
        binder.bind(kategoriComboBox, Periodepost::getKategori, Periodepost::setKategori);
        binder.bind(beskrivelseTextArea, Periodepost::getBeskrivelseString, Periodepost::setBeskrivelseString);
        binder.bind(tittelTextField, Periodepost::getTittelString, Periodepost::setTittelString);
    }


}
