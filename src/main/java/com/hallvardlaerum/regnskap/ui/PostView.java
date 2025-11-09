package com.hallvardlaerum.regnskap.ui;

import com.hallvardlaerum.grunndata.data.Kategori;
import com.hallvardlaerum.grunndata.service.KategoriService;
import com.hallvardlaerum.libs.database.SearchCriteria;
import com.hallvardlaerum.libs.felter.Datokyklop;
import com.hallvardlaerum.libs.felter.HelTallMester;
import com.hallvardlaerum.regnskap.data.*;
import com.hallvardlaerum.libs.ui.MasterDetailViewmal;
import com.hallvardlaerum.regnskap.service.PostService;
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
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;

@Route("post")
@Menu(order = 10, title = "Poster")
public class PostView extends MasterDetailViewmal<Post> {
    private Grid<Post> grid;
    private PostService postService;
    private PostRedigeringsomraade postRedigeringsomraade;

    private DatePicker datoFilterDatePicker;
    private TextField tekstfrabankenFilterTextField;
    private TextField egenbeskrivelseFilterTextField;
    private IntegerField innpaakontoFilterIntegerField;
    private IntegerField utfrakontoFilterIntegerField;
    private ComboBox<PoststatusEnum> poststatusFilterCombobox;
    private ComboBox<PosttypeEnum> posttypeFilterCombobox;
    private ComboBox<Kategori> kategoriFilterComboBox;
    private KategoriService kategoriService;


    public PostView(PostService postService, KategoriService kategoriService) {
        super();
        this.postService = postService;
        this.kategoriService = kategoriService;
        this.postRedigeringsomraade = (PostRedigeringsomraade) postService.hentRedigeringsomraadeAktig();
        postRedigeringsomraade.setDelAvView(this);
        opprettLayout(postService, postRedigeringsomraade, SplitLayout.Orientation.VERTICAL);

        super.initierCallbackDataProviderIGrid(
                q -> postService.finnEntiteterMedSpecification(
                        q.getOffset(),
                        q.getLimit(),
                        postService.getEntityFilterSpecification(),
                        Sort.by("datoLocalDate").descending().and(Sort.by("tekstFraBankenString"))
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

        if (poststatusFilterCombobox.getValue()!=null) {
            filtre.add(new SearchCriteria("poststatusEnum",":",poststatusFilterCombobox.getValue()));
        }

        if (posttypeFilterCombobox.getValue()!=null) {
            filtre.add(new SearchCriteria("posttypeEnum",":",posttypeFilterCombobox.getValue()));
        }

        if (kategoriFilterComboBox.getValue()!=null) {
            filtre.add(new SearchCriteria("kategori",":",kategoriFilterComboBox.getValue()));
        }

        super.brukFiltreIDataprovider(filtre);

    }

    @Override
    public void instansOpprettGrid() {
        grid = super.hentGrid();
        grid.addColumn(Post::getDatoLocalDate).setHeader("Dato").setRenderer(opprettDatoRenderer());
        grid.addColumn(Post::getTekstFraBankenString).setHeader("Tekst fra banken").setRenderer(opprettTekstFraBankenRenderer());
        grid.addColumn(Post::getEgenbeskrivelseString).setHeader("Egen beskrivelse").setRenderer(opprettEgenbeskrivelseRenderer());
        grid.addColumn(Post::getKategori).setHeader("Kategori").setRenderer(opprettKategoriRenderer());
        //grid.addColumn(Post::getInnPaaKontoInteger).setHeader("Inn på konto").setTextAlign(ColumnTextAlign.END);
        grid.addColumn(Post::getInnPaaKontoInteger).setHeader("Inn på konto").setRenderer(opprettInnPaaKontoRenderer());
        grid.addColumn(Post::getUtFraKontoInteger).setHeader("Ut fra konto").setRenderer(opprettUtFraKontoRenderer());
        grid.addColumn(Post::getPoststatusEnum).setHeader("Status").setRenderer(opprettPoststatusRenderer());
        grid.addColumn(Post::getPosttypeEnum).setHeader("Type").setRenderer(opprettPosttypeRenderer());

        // Denne klarer å sette bakgrunnen i hele raden
        grid.setPartNameGenerator(post -> {
            if (post.getPoststatusEnum() == PoststatusEnum.UBEHANDLET) {
                return "ubehandlet";
            } else if (post.getPosttypeEnum() == PosttypeEnum.UTELATES) {
                return "utelates";
            } else if (post.getPosttypeEnum() == PosttypeEnum.DELPOST) {
                return "delpost";
            } else {
                return "";
            }
        });
    }

    private void settStil(Span span, Post post) {
        if (span == null || post == null) {
            return;
        }

        if (post.getPoststatusEnum()==PoststatusEnum.UBEHANDLET) {
            span.setClassName(LumoUtility.TextColor.ERROR);
            //span.setClassName(LumoUtility.Background.ERROR);  //Tok bare teksten, ikke hele cellen
        } else if (post.getPosttypeEnum()==PosttypeEnum.UTELATES) {
            span.setClassName(LumoUtility.TextColor.TERTIARY);
            span.setClassName(LumoUtility.FontWeight.BOLD);
        } else if (post.getPosttypeEnum()==PosttypeEnum.DELPOST) {
            span.setClassName(LumoUtility.TextColor.PRIMARY);
        }
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
            Span span = new Span(post.getEgenbeskrivelseString());
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
            Span span = post.getPoststatusEnum()!=null ? new Span(post.getPosttypeEnum().getTittel()) : new Span("");
            settStil(span, post);
            return span;
        });
    }

    private ComponentRenderer<Span,Post> opprettPosttypeRenderer(){
        return new ComponentRenderer<>(post -> {
            Span span = post.getPosttypeEnum() != null ? new Span(post.getPosttypeEnum().getTittel()) : new Span("");
            settStil(span, post);
            return span;
        });
    }



    @Override
    public void instansOpprettFilterFelter() {
        datoFilterDatePicker = leggTilFilterfelt(0, new DatePicker(),"< dato");
        tekstfrabankenFilterTextField = leggTilFilterfelt(1, new TextField(),"tekst");
        egenbeskrivelseFilterTextField = leggTilFilterfelt(2,new TextField(), "tekst");
        kategoriFilterComboBox = leggTilFilterfelt(3, new ComboBox<>(),"Velg");
        kategoriFilterComboBox.setItems(kategoriService.finnAlle());
        kategoriFilterComboBox.setItemLabelGenerator(Kategori::hentBeskrivendeNavn);

        innpaakontoFilterIntegerField = leggTilFilterfelt(4, new IntegerField(),"> tall");
        utfrakontoFilterIntegerField = leggTilFilterfelt(5, new IntegerField(), "> tall");

        poststatusFilterCombobox = leggTilFilterfelt(6, new ComboBox<>(),"Velg");
        poststatusFilterCombobox.setItems(PoststatusEnum.values());
        poststatusFilterCombobox.setItemLabelGenerator(PoststatusEnum::getTittel);

        posttypeFilterCombobox = leggTilFilterfelt(7,new ComboBox<>(),"Velg");
        posttypeFilterCombobox.setItems(PosttypeEnum.values());
        posttypeFilterCombobox.setItemLabelGenerator(PosttypeEnum::getTittel);

    }


}
