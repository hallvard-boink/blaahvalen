package com.hallvardlaerum.periode.maanedsoversikt;

import com.hallvardlaerum.grunndata.kategori.KategoriRetning;
import com.hallvardlaerum.grunndata.kategori.KategoriService;
import com.hallvardlaerum.libs.ui.Gridkyklop;
import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.periode.PeriodeRedigeringsomraadeMal;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import com.hallvardlaerum.periode.KategoriBudsjettAntallposterSumInnUt;
import com.hallvardlaerum.post.Post;
import com.hallvardlaerum.post.budsjettpost.BudsjettpostService;
import com.hallvardlaerum.post.budsjettpost.BudsjettpoststatusEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;

@Component
@UIScope
public class MaanedsoversiktRedigeringsomraade extends PeriodeRedigeringsomraadeMal implements InitieringsEgnet {
    private boolean erInitiert=false;
    private Grid<KategoriBudsjettAntallposterSumInnUt> kategorierMedTildelteBudsjettposterGrid;
    private Grid<KategoriBudsjettAntallposterSumInnUt> kategoriermedForeslaatteBudsjettposterGrid;
    private Grid<Post> tildelteBudsjettposterGrid;
    private Grid<Post> foreslaatteBudsjettposterGrid;
    private KategoriService kategoriService;
    private BudsjettpostService budsjettpostService;


    @Override
    public void instansOppdaterEkstraRedigeringsfelter() {
        super.instansOppdaterEkstraRedigeringsfelter();
        oppdaterKategoriMedBudsjettpostGrid();

    }



    @Override
    public void instansOpprettFelter(){
        super.instansOpprettFelter();
        instansOpprettFelter_leggTilBudsjettTab_Maanedsoversikt();
    }

    /**
     * <h1>Rediger budsjett</h1>
     * I denne tab'en skal det være mulig å legge til og trekke fra budsjettposter for et månedsbudsjett, og redigere summen i hver budsjettpost.<br/><br/>
     *
     * <h2>kategorierMedTildelteBudsjettposterGrid</h2>
     * Tabell over detaljerte kategorier som det finnes tildelte budsjettposter for.
     * Ved klikk på denne fylles tabellen tildelteBudsjettposterGrid<br/><br/>
     *
     * <h2>kategoriermedForeslaatteBudsjettposterGrid</h2>
     * Tabell over detaljerte kategorier som det finnes foreslåtte budsjettposter for.
     * Ved klikk på denne fylles tabellen foreslaatteBudsjettposterGrid<br/><br/>
     *
     * <h2>tildelteBudsjettposterGrid</h2>
     * Tabell over budsjettposter med valgte kategori som er tildelt til månedens budsjett.
     * Dobbelklikk her åpner budsjettposten for redigering. SHIFT-klikk fjerner budsjettposten fra budsjettet,
     * ved å endre  status for budsjettposten til 'Foreslått'<br/><br/>
     *
     * <h2>foreslaatteBudsjettposterGrid</h2>
     * Tabell over budsjettposter med valgte kategori som er foreslått, men ikke tildelt til månedens budsjett.
     * Dobbelktklikk her åpner budsjettposten for redigeirng. SHIFT-klikk endrer tildeler budsjettposten<br/><br/>
     *
     *
     */
    private void instansOpprettFelter_leggTilBudsjettTab_Maanedsoversikt() {
        String redigerBudsjetttabString = "Rediger budsjett";


        kategorierMedTildelteBudsjettposterGrid = opprettGeneriskKategoriMedBudsjettpostGrid();  // Kategorier med budsjettposter som er inkluderte
        kategoriermedForeslaatteBudsjettposterGrid = opprettGeneriskKategoriMedBudsjettpostGrid();  // Kategorier med budsjettposter som er ekskluderte
        leggTilRedigeringsfelter(redigerBudsjetttabString, kategorierMedTildelteBudsjettposterGrid, kategoriermedForeslaatteBudsjettposterGrid);

        tildelteBudsjettposterGrid = opprettGeneriskBudsjettpostGrid();  // Inkluderte Budsjettposter for markert kategori
        foreslaatteBudsjettposterGrid = opprettGeneriskBudsjettpostGrid(); // Ekskluderte budsjettposter for markert katgori
        leggTilRedigeringsfelter(redigerBudsjetttabString, tildelteBudsjettposterGrid, foreslaatteBudsjettposterGrid);

        hentFormLayoutFraTab(redigerBudsjetttabString).setSizeFull();

    }

    private Grid<KategoriBudsjettAntallposterSumInnUt> opprettGeneriskKategoriMedBudsjettpostGrid(){
        Grid<KategoriBudsjettAntallposterSumInnUt> grid = new Grid<>();
        grid.addColumn(p -> {
            if(p.getKategori()!=null) {
                return p.getKategori().hentKortnavn();
            } else {
                return "";
            }
        }).setHeader("Kategori").setWidth("100px");
        grid.addColumn(KategoriBudsjettAntallposterSumInnUt::getAntallBudsjettposter).setHeader("Antall").setWidth("40px");
        grid.addColumn(KategoriBudsjettAntallposterSumInnUt::getSumBudsjettInnPaaKonto).setHeader("Inn på konto").setWidth("100px");
        grid.addColumn(KategoriBudsjettAntallposterSumInnUt::getSumBudsjettUtFraKonto).setHeader("Ut fra konto").setWidth("100px");
        grid.setSizeFull();
        Gridkyklop.hent().alleRaderTilpassKolonnerOgOpprettFilteradIGrid(grid);
        grid.addItemClickListener(e -> oppdaterBudsjettposterGrid(e.getItem()));
        return grid;
    }

    private void oppdaterKategoriMedBudsjettpostGrid(){
        kategorierMedTildelteBudsjettposterGrid.setItems(kategoriService.byggKategoriMedBudsjettpostList(getEntitet(), BudsjettpoststatusEnum.TILDELT));
        kategoriermedForeslaatteBudsjettposterGrid.setItems(kategoriService.byggKategoriMedBudsjettpostList(getEntitet(),BudsjettpoststatusEnum.FORESLAATT));
    }



    private Grid<Post> opprettGeneriskBudsjettpostGrid() {
        Grid<Post> grid = new Grid<>();
        grid.addColumn(Post::getDatoLocalDate).setHeader("Dato");
        grid.addColumn(Post::getBeskrivelseString).setHeader("Beskrivelse");
        grid.addColumn(p -> p.getKategori().getKategoriRetning()== KategoriRetning.INN? p.getInnPaaKontoInteger() : p.getUtFraKontoInteger()).setHeader("Sum");
        return grid;
    }

    private void oppdaterBudsjettposterGrid(KategoriBudsjettAntallposterSumInnUt kategoriBudsjettAntallposterSumInnUt) {

        tildelteBudsjettposterGrid.setItems(budsjettpostService.finnBudsjettposterFraPeriodeOgKategoriOgBudsjettstatus(
                getEntitet(),
                kategoriBudsjettAntallposterSumInnUt.getKategori(),
                BudsjettpoststatusEnum.TILDELT
        ));

        foreslaatteBudsjettposterGrid.setItems(budsjettpostService.finnBudsjettposterFraPeriodeOgKategoriOgBudsjettstatus(
                getEntitet(),
                kategoriBudsjettAntallposterSumInnUt.getKategori(),
                BudsjettpoststatusEnum.FORESLAATT
        ));
    }

    private void inaktiverBudsjettpost(KategoriBudsjettAntallposterSumInnUt kategoriBudsjettAntallposterSumInnUt) {


    }


    public MaanedsoversiktRedigeringsomraade() {
    }

    @Override
    public boolean erInitiert() {
        return erInitiert;
    }

    public void init() {
        if (!erInitiert) {
            super.initierPeriodeRedigeringsomraadeMal(PeriodetypeEnum.MAANEDSOVERSIKT,
                    Allvitekyklop.hent().getMaanedsoversiktpostService(),
                    Allvitekyklop.hent().getMaanedsoversiktpostRedigeringsomraadeTilDialog(),
                    Allvitekyklop.hent().getMaanedsoversiktService());
            kategoriService = Allvitekyklop.hent().getKategoriService();
            budsjettpostService = Allvitekyklop.hent().getBudsjettpostService();
            erInitiert = true;
        }
    }


}
