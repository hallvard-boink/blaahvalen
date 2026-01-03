package com.hallvardlaerum.periode.maanedsoversikt;

import com.hallvardlaerum.kategori.KategoriRetning;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.periode.Periode;
import com.hallvardlaerum.periode.PeriodeRedigeringsomraadeMal;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import com.hallvardlaerum.periodepost.HallvardsIntegerSpan;
import com.hallvardlaerum.periodepost.PeriodepostTypeEnum;
import com.hallvardlaerum.periodepost.periodeoversiktpost.PeriodedelAvKostnadspakkeRad;
import com.hallvardlaerum.post.Post;
import com.hallvardlaerum.post.budsjettpost.BudsjettpostService;
import com.hallvardlaerum.post.budsjettpost.BudsjettpoststatusEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.stereotype.Component;

@Component
@UIScope
public class MaanedsoversiktRedigeringsomraade extends PeriodeRedigeringsomraadeMal implements InitieringsEgnet {
    private boolean erInitiert = false;
    private Grid<Post> tildelteBudsjettposterGrid;
    private Grid<Post> foreslaatteBudsjettposterGrid;
    private Grid<PeriodedelAvKostnadspakkeRad> kostnadspakkeGrid;
    private BudsjettpostService budsjettpostService;
    private HallvardsIntegerSpan innSpan;
    private HallvardsIntegerSpan utSpan;
    private HallvardsIntegerSpan resultatSpan;

    @Override
    public void instansOppdaterEkstraRedigeringsfelter() {
        super.instansOppdaterEkstraRedigeringsfelter();
        oppdaterKostnadspakkeTab();
        oppdaterRedigerbudsjettTabMedInnhold();

    }

    /**
     * Sum kostnader i denne måneden regnes ut for hver kostnadspakke, og vises sammen med total sum for hver kostnadspakke.
     */
    private void oppdaterKostnadspakkeTab() {
        kostnadspakkeGrid.setItems(
                Allvitekyklop.hent().getPeriodeoversiktpostService().hentKostnadspakkerForPeriodenMedPeriodensSum(hentEntitet())
        );
    }


    private void oppdaterRedigerbudsjettTabMedInnhold() {
        Periode periode = hentEntitet();
        if (periode == null) {
            Loggekyklop.bruk().loggADVARSEL("Periode er null, oppdaterer ikke innholdet");
            return;
        }
        innSpan.settInteger(periode.getSumBudsjettInntektInteger());
        utSpan.settInteger(periode.getSumBudsjettUtgifterInteger());
        resultatSpan.settInteger(periode.getSumBudsjettResultatInteger());

        if (periode.getSumRegnskapResultatInteger() != null && periode.getSumBudsjettResultatInteger() < 0) {
            resultatSpan.getStyle().set("color", "red");
        } else {
            resultatSpan.getStyle().set("color", "black");
        }

        tildelteBudsjettposterGrid.setItems(budsjettpostService.finnFraPeriodeOgBudsjettstatus(hentEntitet(), BudsjettpoststatusEnum.TILDELT));
        foreslaatteBudsjettposterGrid.setItems(budsjettpostService.finnFraPeriodeOgBudsjettstatus(hentEntitet(), BudsjettpoststatusEnum.FORESLAATT));
    }


    @Override
    public void instansOpprettFelter() {
        super.instansOpprettFelter();
        instansOpprettFelter_leggTilKostnadspakkerTab_Maanedsoversikt();
        instansOpprettFelter_leggTilBudsjettTab_Maanedsoversikt();
    }

    private void instansOpprettFelter_leggTilKostnadspakkerTab_Maanedsoversikt() {
        String kostnadspakkeTabString = "Kostnadspakker";
        kostnadspakkeGrid = new Grid<>();
        kostnadspakkeGrid.addColumn(PeriodedelAvKostnadspakkeRad::getTittel).setHeader("Kostnadspakke");
        kostnadspakkeGrid.addColumn(PeriodedelAvKostnadspakkeRad::getSumForDenneMaaned).setHeader("Sum for måneden")
                .setWidth("150px").setFlexGrow(0).setTextAlign(ColumnTextAlign.END).setRenderer(opprettMaanedsSumRenderer());
        kostnadspakkeGrid.addColumn(PeriodedelAvKostnadspakkeRad::getSumTotalt).setHeader("Sum totalt")
                .setWidth("150px").setFlexGrow(0).setTextAlign(ColumnTextAlign.END).setRenderer(opprettTotalSumRenderer());

        super.leggTilRedigeringsfelt(kostnadspakkeTabString, kostnadspakkeGrid);
    }

    private ComponentRenderer<Span, PeriodedelAvKostnadspakkeRad> opprettMaanedsSumRenderer(){
        return new ComponentRenderer<>(periodedelAvKostnadspakkeRad -> opprettSpanFraInteger(periodedelAvKostnadspakkeRad.getSumForDenneMaaned()));
    }

    private ComponentRenderer<Span, PeriodedelAvKostnadspakkeRad> opprettTotalSumRenderer(){
        return new ComponentRenderer<>(periodedelAvKostnadspakkeRad -> opprettSpanFraInteger(periodedelAvKostnadspakkeRad.getSumTotalt()));
    }

    /**
     * <h1>Rediger budsjett</h1>
     * I denne tab'en skal det være mulig å legge til og trekke fra budsjettposter for et månedsbudsjett, og redigere summen i hver budsjettpost.<br/><br/>
     *
     *
     * <h2>TildelteBudsjettposterGrid</h2>
     * Tabell over budsjettposter med valgte kategori som er tildelt til månedens budsjett.
     * Dobbelklikk her åpner budsjettposten for redigering. SHIFT-klikk fjerner budsjettposten fra budsjettet,
     * ved å endre  status for budsjettposten til 'Foreslått'<br/><br/>
     *
     * <h2>ForeslaatteBudsjettposterGrid</h2>
     * Tabell over budsjettposter med valgte kategori som er foreslått, men ikke tildelt til månedens budsjett.
     * Dobbelktklikk her åpner budsjettposten for redigeirng. SHIFT-klikk endrer tildeler budsjettposten<br/><br/>
     *
     *
     */
    private void instansOpprettFelter_leggTilBudsjettTab_Maanedsoversikt() {
        String redigerBudsjetttabString = "Rediger budsjett";
        Span innMerkelappSpan = new Span("Budsjetterte inntekter:");
        Span utMerkelappSpan = new Span("Budsjetterte utgifter:");
        Span resultatMerkelappSpan = new Span("Budsjettert resultat:");

        innSpan = new HallvardsIntegerSpan();
        utSpan = new HallvardsIntegerSpan();
        resultatSpan = new HallvardsIntegerSpan();
        resultatSpan.addClassName(LumoUtility.FontWeight.BOLD);

        tildelteBudsjettposterGrid = opprettGeneriskBudsjettpostGrid(BudsjettpoststatusEnum.TILDELT);  // Inkluderte Budsjettposter for markert kategori
        tildelteBudsjettposterGrid.setHeightFull();
        tildelteBudsjettposterGrid.addItemClickListener(e -> {
            if (e.isShiftKey()) {
                tildelEllerFjernBudsjettpost(e);
            }
        });

        foreslaatteBudsjettposterGrid = opprettGeneriskBudsjettpostGrid(BudsjettpoststatusEnum.FORESLAATT); // Ekskluderte budsjettposter for markert katgori
        foreslaatteBudsjettposterGrid.addItemClickListener(e -> {
            if (e.isShiftKey()) {
                tildelEllerFjernBudsjettpost(e);
            }
        });

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(innMerkelappSpan, innSpan, new Span(), utMerkelappSpan, utSpan, new Span(), resultatMerkelappSpan, resultatSpan);
        leggTilRedigeringsfelter(redigerBudsjetttabString, horizontalLayout, new Span("SHIFT-klikk på en rad for å flytte mellom listene."));
        leggTilRedigeringsfelter(redigerBudsjetttabString, tildelteBudsjettposterGrid, foreslaatteBudsjettposterGrid);


        hentFormLayoutFraTab(redigerBudsjetttabString).setSizeFull();

    }

    private void tildelEllerFjernBudsjettpost(ItemClickEvent<Post> e) {
        Post budsjettpost = e.getItem();
        if (budsjettpost.getBudsjettpoststatusEnum() == BudsjettpoststatusEnum.TILDELT) {
            budsjettpost.setBudsjettpoststatusEnum(BudsjettpoststatusEnum.FORESLAATT);
        } else {
            budsjettpost.setBudsjettpoststatusEnum(BudsjettpoststatusEnum.TILDELT);
        }
        budsjettpostService.lagre(budsjettpost);

        Allvitekyklop.hent().getMaanedsoversiktService().oppdaterOverordnetPeriodensPeriodeposterOgSummer();
        oppdaterRedigerbudsjettTabMedInnhold();
    }

    private Grid<Post> opprettGeneriskBudsjettpostGrid(BudsjettpoststatusEnum budsjettpoststatusEnum) {
        String kategoriTittelString = budsjettpoststatusEnum == BudsjettpoststatusEnum.TILDELT ? "Tildelte budsjettposter" : "Foreslåtte budsjettposter";

        Grid<Post> grid = new Grid<>();
        grid.addColumn(p -> p.getKategori() != null ? p.getKategori().hentKortnavn() : "").setHeader(kategoriTittelString);

        grid.addColumn(Post::getBeskrivelseString).setHeader("Beskrivelse");
        grid.addColumn(p -> {
            if (p.getKategori() != null) {
                return p.getKategori().getKategoriRetning() == KategoriRetning.INN ? p.getInnPaaKontoInteger() : p.getUtFraKontoInteger();
            } else {
                return "";
            }
        }).setHeader("Sum");
        return grid;
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
                    Allvitekyklop.hent().getMaanedsoversiktService(),
                    PeriodepostTypeEnum.MAANEDSOVERSIKTPOST,
                    Allvitekyklop.hent().getMaanedsoversiktView()
            );
            budsjettpostService = Allvitekyklop.hent().getBudsjettpostService();
            erInitiert = true;
        }
    }


}
