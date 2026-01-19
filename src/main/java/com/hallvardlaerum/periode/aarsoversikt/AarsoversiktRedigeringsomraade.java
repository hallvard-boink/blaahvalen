package com.hallvardlaerum.periode.aarsoversikt;

import com.hallvardlaerum.kategori.Kategori;
import com.hallvardlaerum.kategori.KategoriMedSumOgAntall;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.felter.HelTallMester;
import com.hallvardlaerum.libs.ui.Gridkyklop;
import com.hallvardlaerum.libs.ui.RedigerEntitetDialog;
import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.periode.Periode;
import com.hallvardlaerum.periode.PeriodeRedigeringsomraadeMal;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import com.hallvardlaerum.periodepost.Periodepost;
import com.hallvardlaerum.periodepost.PeriodepostTypeEnum;
import com.hallvardlaerum.periodepost.aarsoversiktpost.AarsoversiktpostRedigeringsomraade;
import com.hallvardlaerum.periodepost.kostnadspakke.KostnadspakkeRedigeringsomraade;
import com.hallvardlaerum.post.Post;
import com.hallvardlaerum.post.budsjettpost.BudsjettpostRedigeringsomraade;
import com.hallvardlaerum.post.budsjettpost.BudsjettpostService;
import com.hallvardlaerum.skalTilHavaara.FrekvensPerAarEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSelectionModel;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.grid.dnd.GridDropMode;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Årsoversikt: Faste utgifter</h1>
 * Denne tab'en skal støtte redigering av faste utgifter over et år. Den har tre grid'er:
 * <br/><br/>
 *
 * <h3>Grid 1: UnderkategorierGrid</h3>
 * Viser alle underkategorier som har knyttet budsjettposter eller poster til seg. Klikk oppdaterer grid2
 * <br/><br/>
 *
 * <h3>Grid 2: BudsjettposterGrid</h3>
 * Viser budsjettpostgruppene knyttet til valgte kategori. Klikk oppdaterer et forenklet redigeringsområde for budsjettposten
 * Kan filtrere på beskrivelse
 * <br/>
 * <br/><br/>
 * <h3>Trykknapper</h3>
 * <ul>
 *     <li>CRUD budsjettposter</li>
 *     <li>Oppdater sum og beskrivelse for resten av de viste budsjettpostene</li>
 *     <li>Lag en kopi per måned</li>
 *     <li>Lag en kopi per kvartal</li>
 * </ul>
 * <br/><br/>
 */
@Component
@UIScope
public class AarsoversiktRedigeringsomraade extends PeriodeRedigeringsomraadeMal implements InitieringsEgnet {
    private boolean erInitiert = false;
    private Grid<Periodepost> kostnadspakkerGrid;
    private Grid<KategoriMedSumOgAntall> kategoriMedSumOgAntallGrid;
    private Grid<Post> budsjettpostGrid;
    private RedigerEntitetDialog<Periodepost, Periode> kostnadspakkeRedigerEntitetDialog;
    private RedigerEntitetDialog<Post, Periode> budsjettpostRedigerEntitetDialog;
    private BudsjettpostService budsjettpostService;


    public AarsoversiktRedigeringsomraade() {
        super();
    }

    @Override
    public void instansOppdaterEkstraRedigeringsfelter() {
        super.instansOppdaterEkstraRedigeringsfelter();
        oppdaterFasteUtgifterTab_KategoriGrid();
        oppdaterKostnadspakkerTab();
    }

    private void oppdaterFasteUtgifterTab_KategoriGrid() {
        KategoriMedSumOgAntall markertKategoriMedSumOgAntall;
        if (kategoriMedSumOgAntallGrid.getSelectionModel().getFirstSelectedItem().isPresent()) {
            markertKategoriMedSumOgAntall = kategoriMedSumOgAntallGrid.getSelectionModel().getFirstSelectedItem().get();
        } else {
            markertKategoriMedSumOgAntall = null;
        }
        kategoriMedSumOgAntallGrid.setItems(finnKategorierMedSumOgAntall());
        if (markertKategoriMedSumOgAntall != null) {
            markerRadIKategoriMedSumOgAntallGrid(markertKategoriMedSumOgAntall);
            oppdaterBudsjettpostgridMedValgteKategori();
        }
    }

    @Override
    public void instansOpprettFelter() {
        //super.instansOpprettFelter();
        instansOpprettFelter_leggTilHovedTab();
        instansOpprettFelter_leggTilKategorierTab();
        instansOpprettFelter_leggTilFasteUtgifterTab();
        instansOpprettFelter_leggTilKostnadspakkerTab();
        instansOpprettFelter_leggTilEkstraTab();
        super.testing_leggTilSjekkSummerButton();
    }


    private void instansOpprettFelter_leggTilFasteUtgifterTab() {
        String redigerFastUtgifterTabString = "Faste utgifter";

        instansOpprettFelter_leggTilFasteUtgifterTab_opprettKategoriMedSumOgAntalLGrid();
        instansOpprettFelter_leggTilFasteUtgifterTab_opprettBudsjettpostGrid();

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(instansOpprettFelter_leggTilFasteUtgifterTab_opprettKnapperad());
        verticalLayout.add(budsjettpostGrid);
        leggTilRedigeringsfelter(redigerFastUtgifterTabString, kategoriMedSumOgAntallGrid, verticalLayout);

        hentFormLayoutFraTab(redigerFastUtgifterTabString).setSizeFull();
    }

    private HorizontalLayout instansOpprettFelter_leggTilFasteUtgifterTab_opprettKnapperad() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setAlignItems(Alignment.END);
        horizontalLayout.setWidthFull();

        horizontalLayout.add(instansOpprettFelter_leggTilFasteUtgifterTab_opprettKnapperad_leggTilOpprettNyBudsjettpostButton());
        horizontalLayout.add(instansOpprettFelter_leggTilFasteUtgifterTab_opprettKnapperad_leggTilRedigerBudsjettpostButton());
        horizontalLayout.add(instansOpprettFelter_leggTilFasteUtgifterTab_opprettKnapperad_leggTilGjoerMaanedligButton());
        horizontalLayout.add(instansOpprettFelter_leggTilFasteUtgifterTab_opprettKnapperad_leggTilGjorKvartalsvisButton());
        horizontalLayout.add(instansOpprettFelter_leggTilFasteUtgifterTab_opprettKnapperad_leggTilSlettBudsjettpostButton());
        horizontalLayout.add(instansOpprettFelter_leggTilFasteUtgifterTab_opprettKnapperad_leggTilKopierTilDeAndreButton());

        horizontalLayout.add(kategoriMedSumOgAntallGrid);
        return horizontalLayout;
    }

    private Button instansOpprettFelter_leggTilFasteUtgifterTab_opprettKnapperad_leggTilKopierTilDeAndreButton() {
        Button button = new Button("Kopier til de andre");
        button.addClickListener(e -> kopierTilAndreBudsjettposterMedSammeKategori());
        return button;
    }

    private void kopierTilAndreBudsjettposterMedSammeKategori() {
        if (budsjettpostGrid.getSelectionModel().getFirstSelectedItem().isPresent()) {
            Post markertPost = budsjettpostGrid.getSelectionModel().getFirstSelectedItem().get();
            Kategori kategori = markertPost.getKategori();

            ArrayList<Post> andreposterArrayList = new ArrayList<>(budsjettpostService.finnEtterPeriodeOgKategori(hentEntitet(), kategori));
            andreposterArrayList.remove(markertPost);
            for (Post budsjettpost : andreposterArrayList) {
                budsjettpost.setBeskrivelseString(markertPost.getBeskrivelseString());
                budsjettpost.setInnPaaKontoInteger(markertPost.getInnPaaKontoInteger());
                budsjettpost.setUtFraKontoInteger(markertPost.getUtFraKontoInteger());
            }
            budsjettpostService.lagreAlle(andreposterArrayList);


            // Oppdater listene
            KategoriMedSumOgAntall markertKategoriMSA;
            if (kategoriMedSumOgAntallGrid.getSelectionModel().getFirstSelectedItem().isPresent()) {
                markertKategoriMSA = kategoriMedSumOgAntallGrid.getSelectionModel().getFirstSelectedItem().get();
            } else {
                markertKategoriMSA = null;
            }
            instansOppdaterEkstraRedigeringsfelter();
            markerRadIKategoriMedSumOgAntallGrid(markertKategoriMSA);
            budsjettpostGrid.select(markertPost);

        } else {
            Notification.show("Marker en rad først", 3, Notification.Position.MIDDLE);
        }
    }

    private void markerRadIKategoriMedSumOgAntallGrid(KategoriMedSumOgAntall markertKategoriMSA) {
        if (markertKategoriMSA==null) {
            return;
        }

        List<KategoriMedSumOgAntall> kategoriMSAer = kategoriMedSumOgAntallGrid.getListDataView().getItems().toList();
        for (KategoriMedSumOgAntall kategoriMedSumOgAntall:kategoriMSAer) {
            if (kategoriMedSumOgAntall.getKategori().equals(markertKategoriMSA.getKategori())) {
                kategoriMedSumOgAntallGrid.select(kategoriMedSumOgAntall);
                break;
            }
        }
    }



    private Button instansOpprettFelter_leggTilFasteUtgifterTab_opprettKnapperad_leggTilSlettBudsjettpostButton() {
        Button button = new Button(new Icon(VaadinIcon.CLOSE_CIRCLE));
        button.setTooltipText("Slett markerte budsjettposter");
        button.addClickListener(e -> {
            if (budsjettpostGrid.getSelectionModel().getFirstSelectedItem().isPresent()) {
                List<Post> markertePoster = budsjettpostGrid.getSelectionModel().getSelectedItems().stream().toList();
                budsjettpostService.slettAllePoster(markertePoster);
                instansOppdaterEkstraRedigeringsfelter();
            } else {
                Notification.show("Marker en budsjettpost først").setPosition(Notification.Position.MIDDLE);
            }
        });
        return button;
    }

    private Button instansOpprettFelter_leggTilFasteUtgifterTab_opprettKnapperad_leggTilRedigerBudsjettpostButton() {
        Button redigerButton = new Button(new Icon(VaadinIcon.PENCIL));
        redigerButton.setTooltipText("Rediger markerte budsjettpost. Du kan også dobbeltklikke på en rad i lista for å redigere.");
        redigerButton.addClickListener(e -> {
            if (budsjettpostGrid.getSelectionModel().getFirstSelectedItem().isPresent()) {
                budsjettpostRedigerEntitetDialog.vis(budsjettpostGrid.getSelectionModel().getFirstSelectedItem().get());
            }
        });
        return redigerButton;
    }

    private Button instansOpprettFelter_leggTilFasteUtgifterTab_opprettKnapperad_leggTilOpprettNyBudsjettpostButton() {
        Button button = new Button(new Icon(VaadinIcon.PLUS));
        button.setTooltipText("Legg til en ny budsjettpost");
        button.addClickListener(e -> opprettEnEllerFlereBudsjettposter(FrekvensPerAarEnum.EN_GANG));
        return button;
    }

    private Button instansOpprettFelter_leggTilFasteUtgifterTab_opprettKnapperad_leggTilGjoerMaanedligButton() {
        Button button = new Button(new Icon(VaadinIcon.CALENDAR));
        button.setTooltipText("Legg til en budsjettpost for hver måned, eventuelt basert på den markerte budsjettposten.");
        button.addClickListener(e -> opprettEnEllerFlereBudsjettposter(FrekvensPerAarEnum.MAANEDLIG));
        return button;
    }

    private Button instansOpprettFelter_leggTilFasteUtgifterTab_opprettKnapperad_leggTilGjorKvartalsvisButton() {
        Button button = new Button(new Icon(VaadinIcon.GRID_BIG));
        button.setTooltipText("Legg til en budsjettpost for hvert kvartal, eventuelt basert på den markerte budsjettposten.");
        button.addClickListener(e -> opprettEnEllerFlereBudsjettposter(FrekvensPerAarEnum.HVERT_KVARTAL));
        return button;
    }


    private void opprettEnEllerFlereBudsjettposter(FrekvensPerAarEnum frekvensPerAarEnum) {
        Integer antallPerAar = frekvensPerAarEnum.getAntallPerAar();
        ArrayList<Post> posterSomSkalOpprettesArrayList = new ArrayList<>();

        Kategori kategori;
        if (kategoriMedSumOgAntallGrid.getSelectionModel().getFirstSelectedItem().isPresent()) {
            kategori = kategoriMedSumOgAntallGrid.getSelectionModel().getFirstSelectedItem().get().getKategori();
        } else {
            Notification.show("Ingen kategori er valgt, avbryter").setPosition(Notification.Position.MIDDLE);
            return;
        }

        Post markertBudsjettpost = null;
        if (budsjettpostGrid.getSelectionModel().getFirstSelectedItem().isPresent()) {
            markertBudsjettpost = budsjettpostGrid.getSelectionModel().getFirstSelectedItem().get();
        }

        Integer aarInteger = hentEntitet().getDatoFraLocalDate().getYear();

        int startInteger;
        if (markertBudsjettpost != null && frekvensPerAarEnum == FrekvensPerAarEnum.MAANEDLIG) { //virker bare med månedlige poster
            startInteger = markertBudsjettpost.getDatoLocalDate().getMonthValue();
        } else {
            startInteger = 0;
        }

        for (int i = startInteger; i < antallPerAar; i++) {
            Post budsjettpost = budsjettpostService.opprettEntitet();

            budsjettpost.setKategori(kategori);
            budsjettpost.setDatoLocalDate(opprettDato(aarInteger, i + 1, frekvensPerAarEnum));

            if (markertBudsjettpost != null) {
                budsjettpost.setInnPaaKontoInteger(markertBudsjettpost.getInnPaaKontoInteger());
                budsjettpost.setUtFraKontoInteger(markertBudsjettpost.getUtFraKontoInteger());
                budsjettpost.setBeskrivelseString(markertBudsjettpost.getBeskrivelseString());
            }

            posterSomSkalOpprettesArrayList.add(budsjettpost);
        }

        budsjettpostService.lagreAlle(posterSomSkalOpprettesArrayList);
        instansOppdaterEkstraRedigeringsfelter();
        //Post skalMarkeresBudsjettpost = posterSomSkalOpprettesArrayList.getFirst();
        //oppdaterBudsjettpostgridOgMarkerPost(skalMarkeresBudsjettpost);
    }

    private LocalDate opprettDato(Integer aar, Integer nr, FrekvensPerAarEnum frekvensPerAarEnum) {
        LocalDate dato = null;
        switch (frekvensPerAarEnum) {
            case EN_GANG -> dato = LocalDate.of(aar, 1, 1);
            case MAANEDLIG -> dato = LocalDate.of(aar, nr, 1);
            case HVERT_KVARTAL -> dato = LocalDate.of(aar, ((nr - 1) * 3) + 1, 1);
            case HVERT_HALVAAR -> dato = LocalDate.of(aar, ((nr - 1) * 6) + 1, 1);
        }
        return dato;
    }

    private List<Post> draggedItemsList;
    private void instansOpprettFelter_leggTilFasteUtgifterTab_opprettBudsjettpostGrid() {
        budsjettpostGrid = new Grid<>();
        budsjettpostGrid.addColumn(Post::getDatoLocalDate).setHeader("Dato");
        budsjettpostGrid.addColumn(Post::getBeskrivelseString).setHeader("Beskrivelse");
        budsjettpostGrid.addColumn(Post::getInnPaaKontoInteger).setHeader("Inn");
        budsjettpostGrid.addColumn(Post::getUtFraKontoInteger).setHeader("Ut");

        budsjettpostGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        budsjettpostGrid.addItemClickListener(this::budsjettpostGrid_markerRader);

        budsjettpostGrid.setSizeFull();
        Gridkyklop.hent().tilpassKolonnerIFastradGrid(budsjettpostGrid);

        budsjettpostGrid.setRowsDraggable(true);
        budsjettpostGrid.addDragStartListener(e -> {
            draggedItemsList = e.getDraggedItems();
            kategoriMedSumOgAntallGrid.setDropMode(GridDropMode.ON_TOP);
        });

        budsjettpostGrid.addDragEndListener(e -> {
           draggedItemsList=null;
           budsjettpostGrid.setDropMode(null);
        });
        instansOpprettFelter_leggTilFasteUtgifterTab_opprettBudsjettpostGrid_opprettOgKobleRedigeringsdialog();
    }

    private void budsjettpostGrid_markerRader(ItemClickEvent<Post> e) {
        Post klikketBudsjettpost = e.getItem();
        GridSelectionModel<Post> sm = budsjettpostGrid.getSelectionModel();

        if (e.isCtrlKey()) {
            budsjettpostGrid_markerRader_markerMange(klikketBudsjettpost, !sm.isSelected(klikketBudsjettpost));
        } else {
            if (sm.isSelected(klikketBudsjettpost)) {
                sm.deselect(klikketBudsjettpost);
            } else {
                sm.select(klikketBudsjettpost);
            }
        }
    }

    private void budsjettpostGrid_markerRader_markerMange(Post klikketBudsjettpost, boolean skalMarkeres) {
        Post startBudsjettpost = budsjettpostGrid.getSelectionModel().getFirstSelectedItem().orElse(budsjettpostGrid.getListDataView().getItem(0));
        int startIndeksInteger = budsjettpostGrid.getListDataView().getItemIndex(startBudsjettpost).orElse(0);
        int sluttIndeksInteger = budsjettpostGrid.getListDataView().getItemIndex(klikketBudsjettpost).orElse(0);
        for (int i=startIndeksInteger; i<sluttIndeksInteger; i++) {
            Post post = budsjettpostGrid.getListDataView().getItem(i);
            if (skalMarkeres) {
                budsjettpostGrid.getSelectionModel().select(post);
            } else {
                budsjettpostGrid.getSelectionModel().deselect(post);
            }
        }

    }


    private void instansOpprettFelter_leggTilFasteUtgifterTab_opprettBudsjettpostGrid_opprettOgKobleRedigeringsdialog() {
        BudsjettpostRedigeringsomraade budsjettpostRedigeringsomraadeTilDialog = new BudsjettpostRedigeringsomraade();
        budsjettpostRedigeringsomraadeTilDialog.init();

        budsjettpostRedigerEntitetDialog = new RedigerEntitetDialog<>(
                Allvitekyklop.hent().getBudsjettpostService(),
                Allvitekyklop.hent().getAarsoversiktService(),
                "Rediger fast utgift",
                "",
                budsjettpostRedigeringsomraadeTilDialog,
                this
        );
        budsjettpostGrid.addItemDoubleClickListener(e -> budsjettpostRedigerEntitetDialog.vis(e.getItem()));
    }

    private void instansOpprettFelter_leggTilFasteUtgifterTab_opprettKategoriMedSumOgAntalLGrid() {
        kategoriMedSumOgAntallGrid = new Grid<>();
        kategoriMedSumOgAntallGrid.addColumn(KategoriMedSumOgAntall::getTittel).setHeader("Tittel");
        kategoriMedSumOgAntallGrid.addColumn(KategoriMedSumOgAntall::getUndertittel).setHeader("Undertittel");
        kategoriMedSumOgAntallGrid.addColumn(KategoriMedSumOgAntall::getSumInteger).setHeader("Sum");
        kategoriMedSumOgAntallGrid.addColumn(KategoriMedSumOgAntall::getAntallInteger).setHeader("Antall");

        //kategoriGrid.setItems(finnKategorierMedSumOgAntall());
        kategoriMedSumOgAntallGrid.addSelectionListener(e -> {
            if (e.getFirstSelectedItem().isPresent()) {
                oppdaterBudsjettpostgrid(e.getFirstSelectedItem().get().getKategori());
            }
        });

        // drag and drop
        kategoriMedSumOgAntallGrid.setRowsDraggable(true);

        kategoriMedSumOgAntallGrid.addDropListener(e -> {
            if (e.getDropTargetItem().isPresent()) {
                oppdaterBudsjettposterMedNyKategori(e.getDropTargetItem().get().getKategori(), draggedItemsList);
            }
        });

        kategoriMedSumOgAntallGrid.addDragEndListener(e -> {
           draggedItemsList=null;
           kategoriMedSumOgAntallGrid.setDropMode(null);
        });



        kategoriMedSumOgAntallGrid.setSizeFull();
    }


    private void oppdaterBudsjettposterMedNyKategori(Kategori nyKategori, List<Post> budsjettposter){
        if (nyKategori==null || budsjettposter==null) {
            return;
        }

        for (Post budsjettpost:budsjettposter) {
            budsjettpost.setKategori(nyKategori);
        }
        budsjettpostService.lagreAlle(budsjettposter);
        instansOppdaterEkstraRedigeringsfelter();

    }

    private ArrayList<KategoriMedSumOgAntall> finnKategorierMedSumOgAntall() {
        BudsjettpostService budsjettpostService = Allvitekyklop.hent().getBudsjettpostService();
        Periode aarsoversikt = hentEntitet();

        List<Kategori> kategorier = Allvitekyklop.hent().getKategoriService().finnAlleUnderkategorier();
        ArrayList<KategoriMedSumOgAntall> kategoriMedSumOgAntallArrayList = new ArrayList<>();
        for (Kategori kategori : kategorier) {
            kategoriMedSumOgAntallArrayList.add(budsjettpostService.opprettKategoriMedSumOgAntallBudsjettposter(aarsoversikt.getDatoFraLocalDate(), aarsoversikt.getDatoTilLocalDate(), kategori));
        }
        return kategoriMedSumOgAntallArrayList;
    }




    private void oppdaterBudsjettpostgridMedValgteKategori() {
        if (kategoriMedSumOgAntallGrid.getSelectionModel().getFirstSelectedItem().isPresent()) {
            oppdaterBudsjettpostgrid(kategoriMedSumOgAntallGrid.getSelectionModel().getFirstSelectedItem().get().getKategori());
        } else {
            Loggekyklop.bruk().loggADVARSEL("Ingen markert rad i kategoriMedSumOgAntallGrid, fortsetter likevel");
        }
    }

    private void oppdaterBudsjettpostgrid(Kategori kategori) {
        if (kategori == null) {
            return;
        }

        budsjettpostGrid.setItems(budsjettpostService.finnEtterPeriodeOgKategori(hentEntitet(), kategori));
    }


    private void instansOpprettFelter_leggTilKostnadspakkerTab() {
        String kostnadspakketabString = "Kostnadspakker";
        kostnadspakkerGrid = new Grid<>();

        kostnadspakkerGrid.addColumn(pp -> pp.getKategori() != null ? pp.getKategori().hentKortnavn() : "").setHeader("Kategori");
        kostnadspakkerGrid.addColumn(Periodepost::getTittelString).setHeader("Tittel");
        kostnadspakkerGrid.addColumn(pp -> HelTallMester.formaterIntegerSomStortTall(pp.getSumRegnskapInteger())).setHeader("Sum regnskap").setWidth("150px").setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.END);
        kostnadspakkerGrid.addColumn(Periodepost::getBeskrivelseString).setHeader("Beskrivelse");

        KostnadspakkeRedigeringsomraade kostnadspakkeRedigeringsomraadeTilDialog = new KostnadspakkeRedigeringsomraade();
        kostnadspakkeRedigeringsomraadeTilDialog.init();

        kostnadspakkeRedigerEntitetDialog = new RedigerEntitetDialog<>(
                Allvitekyklop.hent().getKostnadspakkeService(),
                Allvitekyklop.hent().getAarsoversiktService(),
                "Rediger kostnadspakke",
                "",
                kostnadspakkeRedigeringsomraadeTilDialog,
                this
        );
        kostnadspakkerGrid.addItemDoubleClickListener(e -> kostnadspakkeRedigerEntitetDialog.vis(e.getItem()));


        leggTilRedigeringsfelt(kostnadspakketabString, kostnadspakkerGrid);
        hentFormLayoutFraTab(kostnadspakketabString).setSizeFull();
    }


    private void oppdaterKostnadspakkerTab() {
        kostnadspakkerGrid.setItems(Allvitekyklop.hent().getKostnadspakkeService().hentKostnadspakkerForPerioden(hentEntitet()));
    }

    @Override
    public void init() {
        if (!erInitiert) {
            AarsoversiktpostRedigeringsomraade aarsoversiktpostRedigeringsomraade = new AarsoversiktpostRedigeringsomraade();
            aarsoversiktpostRedigeringsomraade.init();
            super.initierPeriodeRedigeringsomraadeMal(PeriodetypeEnum.AARSOVERSIKT,
                    Allvitekyklop.hent().getAarsoversiktpostService(),
                    aarsoversiktpostRedigeringsomraade,
                    Allvitekyklop.hent().getAarsoversiktService(),
                    PeriodepostTypeEnum.AARSOVERSIKTPOST,
                    Allvitekyklop.hent().getAarsoversiktView()
            );
            budsjettpostService = Allvitekyklop.hent().getBudsjettpostService();
            erInitiert = true;
        }
    }

    @Override
    public boolean erInitiert() {
        return erInitiert;
    }


}
