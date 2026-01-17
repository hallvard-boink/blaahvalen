package com.hallvardlaerum.periode.aarsoversikt;

import com.hallvardlaerum.kategori.Kategori;
import com.hallvardlaerum.libs.felter.HelTallMester;
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
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;

/**
 * <h1>Årsoversikt: Faste utgifter</h1>
 * Denne tab'en skal støtte redigering av faste utgifter over et år. Den har tre grid'er:
 *<br/><br/>
 *
 * <h3>Grid 1: UnderkategorierGrid</h3>
 * Viser alle underkategorier som har knyttet budsjettposter eller poster til seg. Klikk oppdaterer grid2
 *<br/><br/>
 *
 * <h3>Grid 2: BudsjettposterGrid</h3>
 * Viser budsjettpostgruppene knyttet til valgte kategori. Klikk oppdaterer et forenklet redigeringsområde for budsjettposten
 * Kan filtrere på beskrivelse
 *<br/>
 *<br/><br/>
 *<h3>Trykknapper</h3>
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
    private Grid<Post> budsjettposterGrid;
    private RedigerEntitetDialog<Periodepost, Periode> kostnadspakkeRedigerEntitetDialog;
    private RedigerEntitetDialog<Post, Periode> budsjettRedigerEntitetDialog;

    @Override
    public void instansOpprettFelter(){
        //super.instansOpprettFelter();
        instansOpprettFelter_leggTilHovedTab();
        instansOpprettFelter_leggTilKategorierTab();
        instansOpprettFelter_leggTilBudsjettTab_FasteUtgifter();
        instansOpprettFelter_leggTilKostnadspakkerTab();
        instansOpprettFelter_leggTilEkstraTab();
        super.testing_leggTilSjekkSummerButton();
    }


    private void instansOpprettFelter_leggTilKostnadspakkerTab() {
        String kostnadspakketabString = "Kostnadspakker";
        kostnadspakkerGrid = new Grid<>();

        kostnadspakkerGrid.addColumn(pp -> pp.getKategori()!=null? pp.getKategori().hentKortnavn() : "").setHeader("Kategori");
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


        leggTilRedigeringsfelt(kostnadspakketabString,kostnadspakkerGrid);
        hentFormLayoutFraTab(kostnadspakketabString).setSizeFull();
    }


    private void instansOpprettFelter_leggTilBudsjettTab_FasteUtgifter() {
        String redigerFastUtgifterTabString = "Faste utgifter";

        Grid<Kategori> kategoriGrid = new Grid<>();
        kategoriGrid.addColumn(Kategori::getTittel).setHeader("Tittel");
        kategoriGrid.addColumn(Kategori::getUndertittel).setHeader("Undertittel");
        kategoriGrid.setItems(Allvitekyklop.hent().getKategoriService().finnAlleUnderkategorier());
        kategoriGrid.addSelectionListener(e -> oppdaterBudsjettpostgrid(e.getAllSelectedItems().stream().toList().getFirst()));
        kategoriGrid.setSizeFull();

        budsjettposterGrid = new Grid<>();
        budsjettposterGrid.addColumn(Post::getDatoLocalDate).setHeader("Dato");
        budsjettposterGrid.addColumn(Post::getBeskrivelseString).setHeader("Beskrivelse");
        budsjettposterGrid.addColumn(Post::getInnPaaKontoInteger).setHeader("Inn");
        budsjettposterGrid.addColumn(Post::getUtFraKontoInteger).setHeader("Ut");
        budsjettposterGrid.setSizeFull();

        BudsjettpostRedigeringsomraade budsjettpostRedigeringsomraadeTilDialog = new BudsjettpostRedigeringsomraade();
        budsjettpostRedigeringsomraadeTilDialog.init();

        budsjettRedigerEntitetDialog = new RedigerEntitetDialog<>(
                Allvitekyklop.hent().getBudsjettpostService(),
                Allvitekyklop.hent().getAarsoversiktService(),
                "Rediger fast utgift",
                "",
                budsjettpostRedigeringsomraadeTilDialog,
                this
        );
        budsjettposterGrid.addItemDoubleClickListener(e -> budsjettRedigerEntitetDialog.vis(e.getItem()));

        leggTilRedigeringsfelter(redigerFastUtgifterTabString,kategoriGrid,budsjettposterGrid);

        hentFormLayoutFraTab(redigerFastUtgifterTabString).setSizeFull();

    }

    private void oppdaterBudsjettpostgrid(Kategori kategori) {
        if (kategori==null) {
            return;
        }

        budsjettposterGrid.setItems(Allvitekyklop.hent().getBudsjettpostService().finnEtterPeriodeOgKategori(hentEntitet(), kategori));

    }


    @Override
    public void instansOppdaterEkstraRedigeringsfelter(){
        super.instansOppdaterEkstraRedigeringsfelter();
        oppdaterKostnadspakkerTab();
    }

    private void oppdaterKostnadspakkerTab() {
        kostnadspakkerGrid.setItems(Allvitekyklop.hent().getKostnadspakkeService().hentKostnadspakkerForPerioden(hentEntitet()));
    }


    public AarsoversiktRedigeringsomraade() {
        super();
    }

    @Override
    public boolean erInitiert() {
        return erInitiert;
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

            erInitiert=true;
        }
    }
}
