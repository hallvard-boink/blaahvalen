package com.hallvardlaerum.post.budsjettpost;

import com.hallvardlaerum.grunndata.kategori.Kategori;
import com.hallvardlaerum.grunndata.kategori.KategoriService;
import com.hallvardlaerum.libs.database.SearchCriteria;
import com.hallvardlaerum.libs.ui.MasterDetailViewmal;
import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.post.Post;
import com.hallvardlaerum.post.PostRepository;
import com.hallvardlaerum.post.PostServiceMal;
import com.hallvardlaerum.post.PostklasseEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;

@Route("budsjettpost")
@UIScope
public class BudsjettpostView extends MasterDetailViewmal<Post, PostRepository> implements InitieringsEgnet {
    private Grid<Post> grid;
    private PostServiceMal postService;
    private BudsjettpostRedigeringsomraade budsjettpostRedigeringsomraade;
    private boolean erInitiert = false;
    private KategoriService kategoriService;

    private DatePicker datoFilterDatePicker;
    private ComboBox<Kategori> kategoriFilterComboBox;
    private TextField beskrivelseFilterTextField;
    private IntegerField innpaakontoFilterIntegerField;
    private IntegerField utfrakontoFilterIntegerField;



    @Override
    public void settFilter() {
        ArrayList<SearchCriteria> filtre = new ArrayList<>();
        filtre.add(new SearchCriteria("postklasseEnum",":", PostklasseEnum.BUDSJETTPOST));

        if (datoFilterDatePicker.getValue()!=null) {
            filtre.add(new SearchCriteria("datoLocalDate","<",datoFilterDatePicker.getValue()));
        }

        if (kategoriFilterComboBox.getValue()!=null) {
            filtre.add(new SearchCriteria("kategori",":",kategoriFilterComboBox.getValue()));
        }

        if (!beskrivelseFilterTextField.getValue().isEmpty()) {
            filtre.add(new SearchCriteria("beskrivelseString",":", beskrivelseFilterTextField.getValue()));
        }

        if (innpaakontoFilterIntegerField.getValue()!=null) {
            filtre.add(new SearchCriteria("innPaaKontoInteger",">",innpaakontoFilterIntegerField.getValue()));
        }

        if (utfrakontoFilterIntegerField.getValue()!= null) {
            filtre.add(new SearchCriteria("utFraKontoInteger",">",utfrakontoFilterIntegerField.getValue()));
        }

        super.brukFiltreIDataprovider(filtre);
    }

    @Override
    public void instansOpprettGrid() {
        grid = super.hentGrid();
        grid.addColumn(Post::getDatoLocalDate).setHeader("Dato");
        grid.addColumn(Post::getKategori).setHeader("Kategori");
        grid.addColumn(Post::getBeskrivelseString).setHeader("Beskrivelse");
        grid.addColumn(Post::getInnPaaKontoInteger).setHeader("Inn på konto");
        grid.addColumn(Post::getUtFraKontoInteger).setHeader("Ut fra konto");

    }

    @Override
    public void instansOpprettFilterFelter() {
        datoFilterDatePicker = leggTilFilterfelt(0, new DatePicker(),"< dato");
        kategoriFilterComboBox = leggTilFilterfelt(1, new ComboBox<>(),"Velg");
        kategoriFilterComboBox.setItems(kategoriService.finnAlle());
        kategoriFilterComboBox.setItemLabelGenerator(Kategori::hentBeskrivendeNavn);
        beskrivelseFilterTextField = leggTilFilterfelt(2, new TextField(),"tekst");
        innpaakontoFilterIntegerField = leggTilFilterfelt(3, new IntegerField(),"> tall");
        utfrakontoFilterIntegerField = leggTilFilterfelt(4, new IntegerField(), "> tall");

    }

    @Override
    public void init() {
        if (!erInitiert) {
            this.postService = Allvitekyklop.hent().getBudsjettpostService();
            this.kategoriService = Allvitekyklop.hent().getKategoriService();
            this.budsjettpostRedigeringsomraade = Allvitekyklop.hent().getBudsjettpostRedigeringsomraade();
            this.budsjettpostRedigeringsomraade.settView(this);

            super.opprettLayout(postService, budsjettpostRedigeringsomraade, SplitLayout.Orientation.VERTICAL);
            initierGridMedNormalSoek();
            hentVindutittel().setText("Budsjettposter");

            erInitiert = true;
        }
    }

    public void initierGridMedNormalSoek(){
        super.initierCallbackDataProviderIGrid(
                q -> postService.finnEntiteterMedSpecification(
                        q.getOffset(),
                        q.getLimit(),
                        postService.getEntityFilterSpecification(),
                        Sort.by("datoLocalDate").descending().and(Sort.by("kategori").and(Sort.by("beskrivelseString").descending()))
                ),

                query -> postService.tellAntallMedSpecification(
                        query.getOffset(),
                        query.getLimit(),
                        postService.getEntityFilterSpecification())
        );

    }

    @Override
    public boolean erInitiert() {
        return false;
    }

    public BudsjettpostView() {
        super();
        Allvitekyklop.hent().setBudsjettpostView(this);
        init();
    }
}
