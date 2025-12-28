package com.hallvardlaerum.periode.maanedsoversikt;

import com.hallvardlaerum.grunndata.kategori.KategoriRetning;
import com.hallvardlaerum.grunndata.kategori.KategoriService;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.felter.HelTallMester;
import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.periode.Periode;
import com.hallvardlaerum.periode.PeriodeRedigeringsomraadeMal;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import com.hallvardlaerum.periodepost.HallvardsSpan;
import com.hallvardlaerum.post.Post;
import com.hallvardlaerum.post.budsjettpost.BudsjettpostService;
import com.hallvardlaerum.post.budsjettpost.BudsjettpoststatusEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;

@Component
@UIScope
public class MaanedsoversiktRedigeringsomraade extends PeriodeRedigeringsomraadeMal implements InitieringsEgnet {
    private boolean erInitiert=false;
    private Grid<Post> tildelteBudsjettposterGrid;
    private Grid<Post> foreslaatteBudsjettposterGrid;
    private KategoriService kategoriService;
    private BudsjettpostService budsjettpostService;
    private HallvardsSpan innSpan;
    private HallvardsSpan utSpan;
    private HallvardsSpan resultatSpan;

    @Override
    public void instansOppdaterEkstraRedigeringsfelter() {
        super.instansOppdaterEkstraRedigeringsfelter();
        oppdaterRedigerbudsjettTabMedInnhold();

    }

    private void oppdaterRedigerbudsjettTabMedInnhold() {
        Periode periode = hentEntitet();
        if (periode==null) {
            Loggekyklop.bruk().loggADVARSEL("Periode er null, oppdaterer ikke innholdet");
            return;
        }
        innSpan.settInteger(periode.getSumBudsjettInntektInteger());
        utSpan.settInteger(periode.getSumBudsjettUtgifterInteger());
        resultatSpan.settInteger(periode.getSumBudsjettResultatInteger());

        if (periode.getSumRegnskapResultatInteger()!=null && periode.getSumBudsjettResultatInteger()<0) {
            resultatSpan.getStyle().set("color","red");
        } else {
            resultatSpan.getStyle().set("color","black");
        }

        tildelteBudsjettposterGrid.setItems(budsjettpostService.finnFraPeriodeOgBudsjettstatus(hentEntitet(), BudsjettpoststatusEnum.TILDELT));
        foreslaatteBudsjettposterGrid.setItems(budsjettpostService.finnFraPeriodeOgBudsjettstatus(hentEntitet(), BudsjettpoststatusEnum.FORESLAATT));
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
        Span innLabelSpan = new Span("Budsjetterte inntekter:");
        Span utLabelSpan = new Span("Budsjetterte utgifter:");
        Span resultatLabelSpan = new Span("Budsjettert resultat:");

        innSpan = new HallvardsSpan();
        utSpan = new HallvardsSpan();
        resultatSpan = new HallvardsSpan();

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
        horizontalLayout.add(innLabelSpan, innSpan, new Span(), utLabelSpan, utSpan, new Span(),  resultatLabelSpan, resultatSpan);
        leggTilRedigeringsfelter(redigerBudsjetttabString,horizontalLayout, new Span("SHIFT-klikk på en rad for å flytte mellom listene."));
        leggTilRedigeringsfelter(redigerBudsjetttabString, tildelteBudsjettposterGrid, foreslaatteBudsjettposterGrid);


        hentFormLayoutFraTab(redigerBudsjetttabString).setSizeFull();

    }

    private void tildelEllerFjernBudsjettpost(ItemClickEvent<Post> e) {
        Post budsjettpost = e.getItem();
        if (budsjettpost.getBudsjettpoststatusEnum()==BudsjettpoststatusEnum.TILDELT) {
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
        grid.addColumn(p -> p.getKategori()!=null? p.getKategori().hentKortnavn() : "").setHeader(kategoriTittelString);

        grid.addColumn(Post::getBeskrivelseString).setHeader("Beskrivelse");
        grid.addColumn(p -> p.getKategori().getKategoriRetning()== KategoriRetning.INN? p.getInnPaaKontoInteger() : p.getUtFraKontoInteger()).setHeader("Sum");
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
                    Allvitekyklop.hent().getMaanedsoversiktService());
            kategoriService = Allvitekyklop.hent().getKategoriService();
            budsjettpostService = Allvitekyklop.hent().getBudsjettpostService();
            erInitiert = true;
        }
    }


}
