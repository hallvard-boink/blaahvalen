package com.hallvardlaerum.post.normalpost;

import com.hallvardlaerum.kategori.Kategori;
import com.hallvardlaerum.kategori.KategoriService;
import com.hallvardlaerum.libs.database.SearchCriteria;
import com.hallvardlaerum.libs.eksportimport.CSVImportmester;
import com.hallvardlaerum.libs.felter.Datokyklop;
import com.hallvardlaerum.libs.ui.MasterDetailViewmal;
import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.periodepost.Periodepost;
import com.hallvardlaerum.periodepost.kostnadspakke.KostnadspakkeService;
import com.hallvardlaerum.post.Post;
import com.hallvardlaerum.post.PostRepository;
import com.hallvardlaerum.post.PostServiceMal;
import com.hallvardlaerum.post.PostklasseEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;

@Route("normalpost")
@UIScope
//@Menu(order = 10, title = "Poster")
public class NormalpostView extends MasterDetailViewmal<Post, PostRepository> implements InitieringsEgnet {
    private Grid<Post> grid;
    private PostServiceMal postService;
    private KostnadspakkeService kostnadspakkeService;
    private NormalpostRedigeringsomraade normalPostRedigeringsomraade;
    private boolean erInitiert = false;

    private DatePicker datoFilterDatePicker;
    private TextField tekstfrabankenFilterTextField;
    private TextField egenbeskrivelseFilterTextField;
    private IntegerField innpaakontoFilterIntegerField;
    private IntegerField utfrakontoFilterIntegerField;
    private ComboBox<NormalpoststatusEnum> normalpoststatusFilterCombobox;
    private ComboBox<NormalposttypeEnum> normalposttypeFilterCombobox;
    private ComboBox<Kategori> kategoriFilterComboBox;
    private ComboBox<Periodepost> kostnadspakkeFilterComboBox;

    private KategoriService kategoriService;
    private NormaldelpostViewMester normaldelpostViewMester;

    protected  ConfirmDialog slettNormalposterconfirmDialog;



// ===========================
// region 0 Constructor og Init
// ===========================


    public NormalpostView() {
        super();
        Allvitekyklop.hent().setNormalpostView(this);
        init();
    }

    @Override
    public void init(){
        if (!erInitiert) {
            this.postService = Allvitekyklop.hent().getNormalpostService();
            this.kategoriService = Allvitekyklop.hent().getKategoriService();
            this.kostnadspakkeService = Allvitekyklop.hent().getKostnadspakkeService();
            this.normalPostRedigeringsomraade = Allvitekyklop.hent().getNormalpostRedigeringsomraade();
            this.normalPostRedigeringsomraade.settView(this);

            super.opprettLayout(postService, normalPostRedigeringsomraade, SplitLayout.Orientation.VERTICAL);
            initierGridMedNormalSoek();
            hentVindutittel().setText("Poster");
            normaldelpostViewMester = new NormaldelpostViewMester(this,  normalPostRedigeringsomraade, postService);

            leggTilImporterCSVFraHandelsbankenButton();


            //Tilpasning av verktøymeny er gjort ved å overkjøre super.opprettSoekeomraade()



            erInitiert = true;
        }
    }

    @Override
    public boolean erInitiert() {
        return erInitiert;
    }


// endregion



// ===========================
// region 1 Opprett søkeområde
// ===========================


    @Override
    protected VerticalLayout opprettSoekeomraade() {
        super.opprettSoekeomraade_leggTilTittel();
        super.opprettSoekeomraade_leggTilVerktoyMeny();
        super.opprettSoekeomraade_leggTilVerktoyMeny_opprettTestDataMenuItem();
        super.opprettSoekeomraade_leggTilVerktoyMeny_opprettEksporterTilCSVMenuItem();
        super.opprettSoekeomraade_leggTilVerktoyMeny_opprettImporterFraCSVMenuItem();
        opprettSoekeomraade_leggTilVerktoyMeny_opprettImporterCSVFraBlaahvalenMenuItem();
        opprettSoekeomraade_leggTilVerktoyMeny_opprettSlettAlleNormalposterMenuItem();

        super.opprettSoekeomraade_leggTilVerktoyMeny_opprettSeparator();
        super.opprettSoekeomraade_leggTilVerktoyMeny_byttOrienteringAvSplitLayoutMenuItem();
        super.opprettSoekeomraade_leggTilSoekeGrid();
        return this.opprettSoeomraade_settSammenDetHele();
    }

    @Override
    public void instansOpprettGrid() {
        grid = super.hentGrid();
        grid.addColumn(Post::getDatoLocalDate).setHeader("Dato").setRenderer(opprettDatoRenderer()).setWidth("150px").setFlexGrow(0);
        grid.addColumn(Post::getTekstFraBankenString).setHeader("Tekst fra banken").setRenderer(opprettTekstFraBankenRenderer());
        grid.addColumn(Post::getInnPaaKontoInteger).setHeader("Inn på konto").setRenderer(opprettInnPaaKontoRenderer()).setWidth("150px").setTextAlign(ColumnTextAlign.END).setFlexGrow(0);
        grid.addColumn(Post::getUtFraKontoInteger).setHeader("Ut fra konto").setRenderer(opprettUtFraKontoRenderer()).setWidth("150px").setTextAlign(ColumnTextAlign.END).setFlexGrow(0);
        grid.addColumn(Post::getBeskrivelseString).setHeader("Egen beskrivelse").setRenderer(opprettEgenbeskrivelseRenderer());
        grid.addColumn(Post::getKategori).setHeader("Kategori").setRenderer(opprettKategoriRenderer()).setWidth("200px").setFlexGrow(0);
        grid.addColumn(Post::getKostnadsPakke).setHeader("Kostnadspakke").setRenderer(opprettKostnadspakkeRenderer());
        grid.addColumn(Post::getNormalPoststatusEnum).setHeader("Status").setRenderer(opprettPoststatusRenderer()).setWidth("100px").setFlexGrow(0);
        grid.addColumn(Post::getNormalPosttypeEnum).setHeader("Type").setRenderer(opprettPosttypeRenderer()).setWidth("100px").setFlexGrow(0);

    }



    public void initierGridMedNormalSoek(){
        super.initierCallbackDataProviderIGrid(
                q -> postService.finnEntiteterMedSpecification(
                        q.getOffset(),
                        q.getLimit(),
                        postService.getEntityFilterSpecification(),
                        Sort.by("datoLocalDate").descending().and(Sort.by("tekstFraBankenString").and(Sort.by("normalposttypeEnum").descending()))
                ),

                query -> postService.tellAntallMedSpecification(
                        query.getOffset(),
                        query.getLimit(),
                        postService.getEntityFilterSpecification())
        );

    }


    // ===========================
    // region 1.1 Grid: Stil og rendering
    // ===========================

    private void settStil(Span span, Post post) {
        if (span == null || post == null) {
            return;
        }

        if (post.getNormalPoststatusEnum()== NormalpoststatusEnum.UBEHANDLET) {
            span.addClassName(LumoUtility.TextColor.ERROR);
            //span.setClassName(LumoUtility.Background.ERROR);  //Tok bare teksten, ikke hele cellen
        } else if (post.getNormalPosttypeEnum()== NormalposttypeEnum.UTELATES) {
            span.addClassName(LumoUtility.TextColor.TERTIARY);
//        } else if (post.getNormalPosttypeEnum()== NormalposttypeEnum.DELPOST) {  //Trengs ikke, blir for mye markering
//            span.setClassName(LumoUtility.TextColor.PRIMARY);
        }
    }


    private ComponentRenderer<Span,Post> opprettKostnadspakkeRenderer(){
        return new ComponentRenderer<>(post -> {
            Span span = post.getKostnadsPakke() != null ? new Span(post.getKostnadsPakke().hentKortnavn()) : new Span("");
            //span.setClassName(LumoUtility.TextAlignment.RIGHT);
            settStil(span, post);
            return span;
        });
    }

    private ComponentRenderer<Span,Post> opprettUtFraKontoRenderer(){
        return new ComponentRenderer<>(post -> {
            Span span = post.getUtFraKontoInteger() != null ? new Span(post.getUtFraKontoInteger().toString()) : new Span("");
            span.setClassName(LumoUtility.TextAlignment.RIGHT);
            settStil(span, post);
            return span;
        });
    }

    private ComponentRenderer<Span,Post> opprettInnPaaKontoRenderer(){
        return new ComponentRenderer<>(post -> {
            Span span = post.getInnPaaKontoInteger() != null ? new Span(post.getInnPaaKontoInteger().toString()) : new Span("");
            span.setClassName(LumoUtility.TextAlignment.RIGHT);
            settStil(span, post);
            return span;
        });
    }

    private ComponentRenderer<Span,Post> opprettEgenbeskrivelseRenderer(){
        return new ComponentRenderer<>(post -> {
            Span span = new Span(post.getBeskrivelseString());
            settStil(span, post);
            return span;
        });
    }


    private ComponentRenderer<Span,Post> opprettTekstFraBankenRenderer(){
        return new ComponentRenderer<>(post -> {
            Span span = new Span(post.getTekstFraBankenString());
            settStil(span, post);
            return span;
        });
    }

    private ComponentRenderer<Span,Post> opprettDatoRenderer(){
        return new ComponentRenderer<>(post -> {
            Span span = post.getDatoLocalDate() != null ? new Span(Datokyklop.hent().formaterDato(post.getDatoLocalDate())) : new Span("");
            settStil(span, post);
            return span;
        });
    }

    private ComponentRenderer<Span, Post> opprettKategoriRenderer(){
        return new ComponentRenderer<>(post -> {
            Span span = post.getKategori() != null ? new Span(post.getKategori().hentKortnavn()) : new Span("");
            settStil(span, post);
            return span;
        });
    }

    private ComponentRenderer<Span,Post> opprettPoststatusRenderer(){
        return new ComponentRenderer<>(post -> {
            Span span = post.getNormalPoststatusEnum()!=null ? new Span(post.getNormalPoststatusEnum().getTittel()) : new Span("");
            settStil(span, post);
            return span;
        });
    }

    private ComponentRenderer<Span,Post> opprettPosttypeRenderer(){
        return new ComponentRenderer<>(post -> {
            Span span = post.getNormalPosttypeEnum() != null ? new Span(post.getNormalPosttypeEnum().getTittel()) : new Span("");
            settStil(span, post);
            return span;
        });
    }

    // endregion


    // ===========================
    // region 1.2 Tilpass knapper og menyvalg
    // ===========================



    private void leggTilImporterCSVFraHandelsbankenButton(){
        Button importerCSVFraHandelsbankenButton = new Button("Importer CSV fra Handelsbanken");
        importerCSVFraHandelsbankenButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        importerCSVFraHandelsbankenButton.addClickListener(e -> importerCSVFraHandelsbanken());
        hentKnapperadSoekefelt().add(importerCSVFraHandelsbankenButton);
    }

    private void opprettSoekeomraade_leggTilVerktoyMeny_opprettImporterCSVFraBlaahvalenMenuItem() {
        hentVerktoeySubMeny().addItem("Importer CSV fra Gamle Blåhvalen", e -> importerCSVFraGamleBlaahvalen());
    }


    private void opprettSoekeomraade_leggTilVerktoyMeny_opprettSlettAlleNormalposterMenuItem(){
        slettNormalposterconfirmDialog = new ConfirmDialog(
                "Slette alle normalposter?",
                "Vil du virkelig slette normalpostene?",
                "Ja, sett i gang",
                ee -> {
                    postService.slettAllePosterAvDennePostklasseEnum();
                    oppdaterSoekeomraadeFinnAlleRader();
                    oppdaterRedigeringsomraade();
                },
                "Nei, er du GAL!",
                e -> lukkDialogslettNormalposter());
        hentVerktoeySubMeny().addItem("Slett alle normalposter",e -> slettNormalposterconfirmDialog.open());
    }


    // endregion

// endregion




// ===========================
// region 2 Aktivering og oppdatering
// ===========================


    public void oppdaterMarkerteRadiGrid(){
        grid.getDataProvider().refreshItem(normalPostRedigeringsomraade.hentEntitet());
    }

    public void markerEntitetiGrid(){
        grid.select(normalPostRedigeringsomraade.hentEntitet());
    }

    public void aktiverDelpostknapperHvisAktuelt(Boolean blnAktiver){
        if (normaldelpostViewMester !=null) {
            normaldelpostViewMester.aktiverKnapperForEntity(blnAktiver);
        }
    }

// endregion



// ===========================
// region 3 Søk og filtrering
// ===========================


    @Override
    public void instansOpprettFilterFelter() {
        datoFilterDatePicker = leggTilFilterfelt(0, new DatePicker(),"< dato");
        tekstfrabankenFilterTextField = leggTilFilterfelt(1, new TextField(),"tekst");

        innpaakontoFilterIntegerField = leggTilFilterfelt(2, new IntegerField(),"> tall");
        utfrakontoFilterIntegerField = leggTilFilterfelt(3, new IntegerField(), "> tall");

        egenbeskrivelseFilterTextField = leggTilFilterfelt(4,new TextField(), "tekst");

        kategoriFilterComboBox = leggTilFilterfelt(5, new ComboBox<>(),"Velg");
        kategoriFilterComboBox.setItems(kategoriService.finnAlle());
        kategoriFilterComboBox.setItemLabelGenerator(Kategori::hentBeskrivendeNavn);

        kostnadspakkeFilterComboBox = leggTilFilterfelt(6, new ComboBox<>(),"Velg");
        kostnadspakkeFilterComboBox.setItems(kostnadspakkeService.finnAlleKostnadspakker());
        kostnadspakkeFilterComboBox.setItemLabelGenerator(Periodepost::getTittelString);

        normalpoststatusFilterCombobox = leggTilFilterfelt(7, new ComboBox<>(),"Velg");
        normalpoststatusFilterCombobox.setItems(NormalpoststatusEnum.values());
        normalpoststatusFilterCombobox.setItemLabelGenerator(NormalpoststatusEnum::getTittel);

        normalposttypeFilterCombobox = leggTilFilterfelt(8,new ComboBox<>(),"Velg");
        normalposttypeFilterCombobox.setItems(NormalposttypeEnum.values());
        normalposttypeFilterCombobox.setItemLabelGenerator(NormalposttypeEnum::getTittel);


    }



    @Override
    public void settFilter() {
        ArrayList<SearchCriteria> filtre = new ArrayList<>();


        filtre.add(new SearchCriteria("postklasseEnum",":", PostklasseEnum.NORMALPOST));

        if (datoFilterDatePicker.getValue()!=null) {
            filtre.add(new SearchCriteria("datoLocalDate","<",datoFilterDatePicker.getValue()));
        }

        if (!tekstfrabankenFilterTextField.getValue().isEmpty()) {
            filtre.add(new SearchCriteria("tekstFraBankenString",":",tekstfrabankenFilterTextField.getValue()));
        }

        if (!egenbeskrivelseFilterTextField.getValue().isEmpty()) {
            filtre.add(new SearchCriteria("egenbeskrivelseString",":",egenbeskrivelseFilterTextField.getValue()));
        }

        if (innpaakontoFilterIntegerField.getValue()!=null) {
            filtre.add(new SearchCriteria("innPaaKontoInteger",">",innpaakontoFilterIntegerField.getValue()));
        }

        if (utfrakontoFilterIntegerField.getValue()!= null) {
            filtre.add(new SearchCriteria("utFraKontoInteger",">",utfrakontoFilterIntegerField.getValue()));
        }

        if (normalpoststatusFilterCombobox.getValue()!=null) {
            filtre.add(new SearchCriteria("normalpoststatusEnum",":", normalpoststatusFilterCombobox.getValue()));
        }

        if (normalposttypeFilterCombobox.getValue()!=null) {
            filtre.add(new SearchCriteria("normalposttypeEnum",":", normalposttypeFilterCombobox.getValue()));
        }

        if (kategoriFilterComboBox.getValue()!=null) {
            filtre.add(new SearchCriteria("kategori",":",kategoriFilterComboBox.getValue()));
        }

        if (kostnadspakkeFilterComboBox.getValue()!=null) {
            filtre.add(new SearchCriteria("kostnadsPakke",":",kostnadspakkeFilterComboBox.getValue()));
        }

        super.brukFiltreIDataprovider(filtre);
        //super.oppdaterAntallRaderNederstIGrid();

    }

// endregion




// ===========================
// region 5.Hjelpeprosedyrer
// ===========================

    private void lukkDialogslettNormalposter(){
        slettNormalposterconfirmDialog.close();
    }

    private void importerCSVFraGamleBlaahvalen() {
        NormalpostFraGamleBlaahvalenCSVImportassistent normalpostFraGamleBlaahvalenCSVImportassistent = new NormalpostFraGamleBlaahvalenCSVImportassistent();
        CSVImportmester csvImportmester = new CSVImportmester(normalpostFraGamleBlaahvalenCSVImportassistent);
        csvImportmester.velgImportfilOgKjoerImport(postService);
    }



    private void importerCSVFraHandelsbanken() {
        NormalpostFraHandelsbankensCSVImportassistent normalpostFraHandelsbankensCSVImportassistent = new NormalpostFraHandelsbankensCSVImportassistent(postService, this);
        CSVImportmester csvImportmester = new CSVImportmester(normalpostFraHandelsbankensCSVImportassistent);
        csvImportmester.setLesCharsetString("ISO-8859-15");
        csvImportmester.velgImportfilOgKjoerImport(postService);

    }

// endregion







}
