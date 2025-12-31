package com.hallvardlaerum.periode.aarsoversikt;

import com.hallvardlaerum.grunndata.kategori.Kategori;
import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.periode.PeriodeRedigeringsomraadeMal;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import com.hallvardlaerum.periodepost.Periodepost;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;

@Component
@UIScope
public class AarsoversiktRedigeringsomraade extends PeriodeRedigeringsomraadeMal implements InitieringsEgnet {
    private boolean erInitiert = false;
    private Grid<Periodepost> kostnadspakkerGrid;


    @Override
    public void instansOpprettFelter(){
        super.instansOpprettFelter();
        instansOpprettFelter_leggTilBudsjettTab_FasteUtgifter();
        instansOpprettFelter_leggTilKostnadspakkerTab();

    }



    private void instansOpprettFelter_leggTilKostnadspakkerTab() {
        String kostnadspakketabString = "Kostnadspakker";
        kostnadspakkerGrid = new Grid<>();
        kostnadspakkerGrid.addColumn(pp -> {
            return pp.getKategori()!=null? pp.getKategori().hentKortnavn() : "";
        }).setHeader("Kategori").setWidth("250px").setFlexGrow(0);
        kostnadspakkerGrid.addColumn(Periodepost::getTittelString).setHeader("Tittel");
        kostnadspakkerGrid.addColumn(Periodepost::getSumRegnskapInteger).setHeader("Sum regnskap").setWidth("150px").setFlexGrow(0);

        leggTilRedigeringsfelt(kostnadspakketabString,kostnadspakkerGrid);
        hentFormLayoutFraTab(kostnadspakketabString).setSizeFull();
    }
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
     *
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
    private void instansOpprettFelter_leggTilBudsjettTab_FasteUtgifter() {
        String redigerFastUtgifterTabString = "Faste utgifter";

        Grid<Kategori> kategoriGrid = new Grid<>();
        kategoriGrid.addColumn(Kategori::getTittel).setHeader("Tittel");
        kategoriGrid.addColumn(Kategori::getUndertittel).setHeader("Undertittel");
        kategoriGrid.setItems(Allvitekyklop.hent().getKategoriService().finnAlle());

        leggTilRedigeringsfelt(redigerFastUtgifterTabString,kategoriGrid);
        hentFormLayoutFraTab(redigerFastUtgifterTabString).setSizeFull();

    }


    @Override
    public void instansOppdaterEkstraRedigeringsfelter(){
        super.instansOppdaterEkstraRedigeringsfelter();
        oppdaterKostnadspakkerTab();
    }

    private void oppdaterKostnadspakkerTab() {
        kostnadspakkerGrid.setItems(Allvitekyklop.hent().getPeriodeoversiktpostService().finnEtterPeriode(hentEntitet()));
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
            super.initierPeriodeRedigeringsomraadeMal(PeriodetypeEnum.AARSOVERSIKT,
                    Allvitekyklop.hent().getAarsoversiktpostService(),
                    Allvitekyklop.hent().getAarsoversiktpostRedigeringsomraadeTilDialog(),
                    Allvitekyklop.hent().getAarsoversiktService()
            );

            erInitiert=true;
        }
    }
}
