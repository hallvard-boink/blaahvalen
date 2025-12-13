package com.hallvardlaerum.post.normalpost;

import com.hallvardlaerum.libs.eksportimport.CSVImportmester;
import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.post.Post;
import com.hallvardlaerum.grunndata.kategori.Kategori;
import com.hallvardlaerum.grunndata.kategori.KategoriService;
import com.hallvardlaerum.libs.database.SearchCriteria;
import com.hallvardlaerum.libs.felter.Datokyklop;
import com.hallvardlaerum.libs.ui.MasterDetailViewmal;
import com.hallvardlaerum.post.PostRepository;
import com.hallvardlaerum.post.PostServiceMal;
import com.hallvardlaerum.post.PostklasseEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Menu;
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


    private KategoriService kategoriService;
    private NormaldelpostViewMester normaldelpostViewMester;

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
            this.normalPostRedigeringsomraade = Allvitekyklop.hent().getNormalpostRedigeringsomraade();
            this.normalPostRedigeringsomraade.settView(this);

            super.opprettLayout(postService, normalPostRedigeringsomraade, SplitLayout.Orientation.VERTICAL);
            initierGridMedNormalSoek();
            hentVindutittel().setText("Poster");
            normaldelpostViewMester = new NormaldelpostViewMester(this,  normalPostRedigeringsomraade, postService);
            leggTilImporterCSVFraHandelsbankenButton();

            erInitiert = true;
        }
    }

    @Override
    public boolean erInitiert() {
        return erInitiert;
    }

    private void leggTilImporterCSVFraHandelsbankenButton(){
        Button importerCSVFraHandelsbankenButton = new Button("Importer CSV fra Handelsbanken");
        importerCSVFraHandelsbankenButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        importerCSVFraHandelsbankenButton.addClickListener(e -> importerCSVFraHandelsbanken());
        hentKnapperadSoekefelt().add(importerCSVFraHandelsbankenButton);
    }

    private void importerCSVFraHandelsbanken() {
        NormalpostFraCSVImportassistent normalpostFraCSVImportassistent = new NormalpostFraCSVImportassistent(postService, this);
        CSVImportmester csvImportmester = new CSVImportmester(normalpostFraCSVImportassistent);
        csvImportmester.setLesCharsetString("ISO-8859-15");
        csvImportmester.velgImportfilOgKjoerImport(postService);

    }


    public void oppdaterMarkerteRadiGrid(){
        grid.getDataProvider().refreshItem(normalPostRedigeringsomraade.getEntitet());
    }

    public void markerEntitetiGrid(){
        grid.select(normalPostRedigeringsomraade.getEntitet());
    }

    public void aktiverDelpostknapperHvisAktuelt(Boolean blnAktiver){
        if (normaldelpostViewMester !=null) {
            normaldelpostViewMester.aktiverKnapperForEntity(blnAktiver);
        }
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




    @Override
    public void settFilter() {
        ArrayList<SearchCriteria> filtre = new ArrayList<>();

        //Oppdater PostSpecification her

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

        super.brukFiltreIDataprovider(filtre);

    }

    @Override
    public void instansOpprettGrid() {
        grid = super.hentGrid();
        grid.addColumn(Post::getDatoLocalDate).setHeader("Dato").setRenderer(opprettDatoRenderer()).setWidth("60px");
        grid.addColumn(Post::getTekstFraBankenString).setHeader("Tekst fra banken").setRenderer(opprettTekstFraBankenRenderer()).setWidth("400px");
        grid.addColumn(Post::getInnPaaKontoInteger).setHeader("Inn på konto").setRenderer(opprettInnPaaKontoRenderer()).setWidth("60px").setTextAlign(ColumnTextAlign.END);
        grid.addColumn(Post::getUtFraKontoInteger).setHeader("Ut fra konto").setRenderer(opprettUtFraKontoRenderer()).setWidth("60px").setTextAlign(ColumnTextAlign.END);

        grid.addColumn(Post::getKategori).setHeader("Kategori").setRenderer(opprettKategoriRenderer()).setWidth("100px");
        grid.addColumn(Post::getBeskrivelseString).setHeader("Egen beskrivelse").setRenderer(opprettEgenbeskrivelseRenderer()).setWidth("200px");
        grid.addColumn(Post::getNormalPoststatusEnum).setHeader("Status").setRenderer(opprettPoststatusRenderer()).setWidth("50px");
        grid.addColumn(Post::getNormalPosttypeEnum).setHeader("Type").setRenderer(opprettPosttypeRenderer()).setWidth("50px");

        // Denne klarer å sette bakgrunnen i hele raden
//        grid.setPartNameGenerator(post -> {
//            if (post.getNormalPoststatusEnum() == NormalpoststatusEnum.UBEHANDLET) {
//                return "ubehandlet";
//            } else if (post.getNormalPosttypeEnum() == NormalposttypeEnum.UTELATES) {
//                return "utelates";
//            } else if (post.getNormalPosttypeEnum() == NormalposttypeEnum.DELPOST) {
//                //return "delpost"; //for mye markering
//                return "";
//            } else {
//                return "";
//            }
//        });
    }

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


    @Override
    public void instansOpprettFilterFelter() {
        datoFilterDatePicker = leggTilFilterfelt(0, new DatePicker(),"< dato");
        tekstfrabankenFilterTextField = leggTilFilterfelt(1, new TextField(),"tekst");

        innpaakontoFilterIntegerField = leggTilFilterfelt(2, new IntegerField(),"> tall");
        utfrakontoFilterIntegerField = leggTilFilterfelt(3, new IntegerField(), "> tall");

        kategoriFilterComboBox = leggTilFilterfelt(4, new ComboBox<>(),"Velg");
        kategoriFilterComboBox.setItems(kategoriService.finnAlle());
        kategoriFilterComboBox.setItemLabelGenerator(Kategori::hentBeskrivendeNavn);

        egenbeskrivelseFilterTextField = leggTilFilterfelt(5,new TextField(), "tekst");

        normalpoststatusFilterCombobox = leggTilFilterfelt(6, new ComboBox<>(),"Velg");
        normalpoststatusFilterCombobox.setItems(NormalpoststatusEnum.values());
        normalpoststatusFilterCombobox.setItemLabelGenerator(NormalpoststatusEnum::getTittel);

        normalposttypeFilterCombobox = leggTilFilterfelt(7,new ComboBox<>(),"Velg");
        normalposttypeFilterCombobox.setItems(NormalposttypeEnum.values());
        normalposttypeFilterCombobox.setItemLabelGenerator(NormalposttypeEnum::getTittel);

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
            Span span = post.getKategori() != null ? new Span(post.getKategori().getTittel()) : new Span("");
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

}
