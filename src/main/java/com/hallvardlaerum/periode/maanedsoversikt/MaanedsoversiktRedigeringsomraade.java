package com.hallvardlaerum.periode.maanedsoversikt;

import com.hallvardlaerum.kategori.KategoriRetning;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.ui.RedigerEntitetDialog;
import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.periode.Periode;
import com.hallvardlaerum.periode.PeriodeRedigeringsomraadeMal;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import com.hallvardlaerum.periodepost.Periodepost;
import com.hallvardlaerum.periodepost.kostnadspakke.KostnadspakkeRedigeringsomraade;
import com.hallvardlaerum.periodepost.maanedsoversiktpost.MaanedsoversiktpostRedigeringsomraade;
import com.hallvardlaerum.skalTilHavaara.HallvardsIntegerSpan;
import com.hallvardlaerum.periodepost.PeriodepostTypeEnum;
import com.hallvardlaerum.periodepost.kostnadspakke.PeriodedelAvKostnadspakkeRad;
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
    private RedigerEntitetDialog<Periodepost, Periode> redigerKostnadspakkeDialog;
    private RedigerEntitetDialog<Post, Periode> redigerBudsjettpostDialog;



// ===========================
// region 0 Constructor og Init
// ===========================

    public MaanedsoversiktRedigeringsomraade() {
    }

    @Override
    public boolean erInitiert() {
        return erInitiert;
    }

    public void init() {
        if (!erInitiert) {
            MaanedsoversiktpostRedigeringsomraade maanedsoversiktpostRedigeringsomraadeTilDialog = new MaanedsoversiktpostRedigeringsomraade();
            maanedsoversiktpostRedigeringsomraadeTilDialog.init();
            super.initierPeriodeRedigeringsomraadeMal(PeriodetypeEnum.MAANEDSOVERSIKT,
                    Allvitekyklop.hent().getMaanedsoversiktpostService(),
                    maanedsoversiktpostRedigeringsomraadeTilDialog,
                    Allvitekyklop.hent().getMaanedsoversiktService(),
                    PeriodepostTypeEnum.MAANEDSOVERSIKTPOST,
                    Allvitekyklop.hent().getMaanedsoversiktView()
            );
            budsjettpostService = Allvitekyklop.hent().getBudsjettpostService();
            erInitiert = true;
        }
    }


// endregion


// ===========================
// region 1 Opprett felter
// ===========================


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

        KostnadspakkeRedigeringsomraade kostnadspakkeRedigeringsomraade = new KostnadspakkeRedigeringsomraade();
        kostnadspakkeRedigeringsomraade.init();

        redigerKostnadspakkeDialog = new RedigerEntitetDialog<>(
                Allvitekyklop.hent().getKostnadspakkeService(),
                Allvitekyklop.hent().getMaanedsoversiktService(),
                "Rediger kostnadspakke",
                "",
                kostnadspakkeRedigeringsomraade,
                this
        );
        kostnadspakkeGrid.addItemDoubleClickListener(e -> redigerKostnadspakkeDialog.vis(e.getItem().getKostnadspakke(),
                "Rediger kostnadspakke med kategori " + e.getItem().getKostnadspakke().getKategori().hentBeskrivendeNavn(), null));


        super.leggTilRedigeringsfelt(kostnadspakkeTabString, kostnadspakkeGrid);
    }

    private ComponentRenderer<Span, PeriodedelAvKostnadspakkeRad> opprettMaanedsSumRenderer() {
        return new ComponentRenderer<>(periodedelAvKostnadspakkeRad -> opprettSpanFraInteger(periodedelAvKostnadspakkeRad.getSumForDenneMaaned()));
    }

    private ComponentRenderer<Span, PeriodedelAvKostnadspakkeRad> opprettTotalSumRenderer() {
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
     * Dobbelktklikk her åpner budsjettposten for redigering. CTRL-klikk endrer tildeler budsjettposten<br/><br/>
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

        redigerBudsjettpostDialog = new RedigerEntitetDialog<>(
                Allvitekyklop.hent().getBudsjettpostService(),
                Allvitekyklop.hent().getMaanedsoversiktService(),
                "Rediger budsjettpost",
                "",
                Allvitekyklop.hent().getBudsjettpostRedigeringsomraade(),
                Allvitekyklop.hent().getMaanedsoversiktRedigeringsomraade()
        );

        tildelteBudsjettposterGrid = opprettGeneriskBudsjettpostGrid(BudsjettpoststatusEnum.TILDELT);  // Inkluderte Budsjettposter for markert kategori
        tildelteBudsjettposterGrid.setHeightFull();
        tildelteBudsjettposterGrid.addItemClickListener(e -> {
            if (e.isCtrlKey()) {
                tildelEllerFjernBudsjettpost(e);
            }
        });
        tildelteBudsjettposterGrid.addItemDoubleClickListener(e -> redigerBudsjettpostDialog.vis(e.getItem()));

        foreslaatteBudsjettposterGrid = opprettGeneriskBudsjettpostGrid(BudsjettpoststatusEnum.FORESLAATT); // Ekskluderte budsjettposter for markert katgori
        foreslaatteBudsjettposterGrid.addItemClickListener(e -> {
            if (e.isCtrlKey()) {
                tildelEllerFjernBudsjettpost(e);
            }
        });
        foreslaatteBudsjettposterGrid.addItemDoubleClickListener(e -> redigerBudsjettpostDialog.vis(e.getItem()));

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(innMerkelappSpan, innSpan, new Span(), utMerkelappSpan, utSpan, new Span(), resultatMerkelappSpan, resultatSpan);
        Span infoSpan = new Span("CTRL-klikk på en rad for å flytte mellom listene.");
        infoSpan.addClassName(LumoUtility.TextAlignment.RIGHT);
        leggTilRedigeringsfelter(redigerBudsjetttabString, horizontalLayout, infoSpan);
        leggTilRedigeringsfelter(redigerBudsjetttabString, tildelteBudsjettposterGrid, foreslaatteBudsjettposterGrid);


        hentFormLayoutFraTab(redigerBudsjetttabString).setSizeFull();

    }

// endregion


// ===========================
// region Oppdatering og CRUD
// ===========================


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
                Allvitekyklop.hent().getKostnadspakkeService().hentKostnadspakkerForPeriodenMedPeriodensSum(hentEntitet())
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

    // endregion


    // ===========================
    // region 5 Rediger månedsbudsjett
    // ===========================


    private void tildelEllerFjernBudsjettpost(ItemClickEvent<Post> e) {
        Post budsjettpost = e.getItem();
        boolean blirTildelt;
        if (budsjettpost.getBudsjettpoststatusEnum() == BudsjettpoststatusEnum.TILDELT) {
            blirTildelt = false;
            budsjettpost.setBudsjettpoststatusEnum(BudsjettpoststatusEnum.FORESLAATT);
        } else {
            budsjettpost.setBudsjettpoststatusEnum(BudsjettpoststatusEnum.TILDELT);
            blirTildelt = true;
        }
        budsjettpostService.lagre(budsjettpost);
        Periodepost maanedsoversiktpost = Allvitekyklop.hent().getMaanedsoversiktpostService().finnStandardFraPeriodeOgKategori(hentEntitet(), budsjettpost.getKategori());
        Allvitekyklop.hent().getMaanedsoversiktService().oppdaterSummerEtterTildelingAvBudsjettpost(maanedsoversiktpost);
        oppdaterRedigerbudsjettTabMedInnhold();
        if (blirTildelt) {
            tildelteBudsjettposterGrid.select(e.getItem());
        } else {
            tildelteBudsjettposterGrid.select(e.getItem());
        }

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


}
