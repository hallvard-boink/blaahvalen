package com.hallvardlaerum.periodepost;

import com.hallvardlaerum.grunndata.kategori.Kategori;
import com.hallvardlaerum.grunndata.kategori.KategoriService;
import com.hallvardlaerum.libs.database.SearchCriteria;
import com.hallvardlaerum.libs.ui.MasterDetailViewmal;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeAktig;
import com.hallvardlaerum.libs.ui.ViewmalAktig;
import com.hallvardlaerum.periode.Periode;
import com.hallvardlaerum.periode.PeriodeServiceMal;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;

public class PeriodepostViewMal extends MasterDetailViewmal<Periodepost, PeriodepostRepository> {
    private Grid<Periodepost> grid;
    private PeriodepostServiceMal periodepostService;
    private PeriodepostTypeEnum periodepostTypeEnum;
    private PeriodeServiceMal periodeService;
    private PeriodetypeEnum periodetypeEnum;
    private RedigeringsomraadeAktig<Periodepost> redigeringsomraade;
    private KategoriService kategoriService;

    private ComboBox<Periode> periodeFilterComboBox;
    private ComboBox<Kategori> kategoriFilterComboBox;
    private IntegerField sumBudsjettFilterIntegerField;
    private IntegerField sumRegnskapFilterIntegerField;
    private TextField beskrivelseFilterTextField;


    public PeriodepostViewMal() {
        super();
    }

    public void initierPeriodepostViewMal(PeriodepostTypeEnum periodepostTypeEnum,
                                          ViewmalAktig<Periodepost, PeriodepostRepository> periodepostView,
                                          PeriodetypeEnum periodetypeEnum,
                                          RedigeringsomraadeAktig<Periodepost> redigeringsomraade,
                                          PeriodepostServiceMal periodepostService,
                                          PeriodeServiceMal periodeService) {
        this.periodepostTypeEnum = periodepostTypeEnum;
        this.periodetypeEnum = periodetypeEnum;
        this.redigeringsomraade = redigeringsomraade;
        this.redigeringsomraade.settView(periodepostView);
        this.periodepostService = periodepostService;
        this.periodeService = periodeService;
        kategoriService = Allvitekyklop.hent().getKategoriService();

        redigeringsomraade.settView(periodepostView);

        super.opprettLayout(this.periodepostService, this.redigeringsomraade, SplitLayout.Orientation.HORIZONTAL);
        hentVindutittel().setText(periodepostTypeEnum.getTittel());
        initierGridMedPagedSearch();
    }

    @Override
    public void settFilter() {
        ArrayList<SearchCriteria> searchCriteriaArrayList = new ArrayList<>();

        if (periodeFilterComboBox.getValue()!=null) {
            searchCriteriaArrayList.add(new SearchCriteria("periode",":", periodeFilterComboBox.getValue()));
        }

        if (kategoriFilterComboBox.getValue()!=null) {
            searchCriteriaArrayList.add(new SearchCriteria("kategori",":",kategoriFilterComboBox.getValue()));
        }

        if (sumBudsjettFilterIntegerField.getValue()!=null) {
            searchCriteriaArrayList.add(new SearchCriteria("sumBudsjettInteger",">",sumBudsjettFilterIntegerField.getValue()));
        }

        if (sumRegnskapFilterIntegerField.getValue()!=null) {
            searchCriteriaArrayList.add(new SearchCriteria("sumRegnskapInteger",">",sumRegnskapFilterIntegerField.getValue()));
        }

        if (beskrivelseFilterTextField.getValue()!=null && !beskrivelseFilterTextField.getValue().isEmpty()) {
            searchCriteriaArrayList.add(new SearchCriteria("beskrivelseString",":",beskrivelseFilterTextField.getValue()));
        }

        searchCriteriaArrayList.add(new SearchCriteria("periodepostTypeEnum",":",periodepostTypeEnum));
        super.brukFiltreIDataprovider(searchCriteriaArrayList);
    }

    public void initierGridMedPagedSearch() {
        super.initierCallbackDataProviderIGrid(
                query -> periodepostService.finnEntiteterMedSpecification(
                        query.getOffset(),
                        query.getLimit(),
                        periodepostService.getEntityFilterSpecification(),
                        Sort.by("periode").descending()
                ),

                query -> periodepostService.tellAntallMedSpecification(
                        query.getOffset(),
                        query.getLimit(),
                        periodepostService.getEntityFilterSpecification())
        );
    }

    @Override
    public void instansOpprettGrid() {
        grid = hentGrid();
        grid.addColumn(p-> p.getPeriode()!=null ? p.getPeriode().hentBeskrivendeNavn() : "" ).setHeader("Periode");
        grid.addColumn(p -> p.getKategori()!=null? p.getKategori().getTittel():"").setHeader("Kategori");
        grid.addColumn(Periodepost::getSumBudsjettInteger).setHeader("Budsjett");
        grid.addColumn(Periodepost::getSumRegnskapInteger).setHeader("Regnskap");
        grid.addColumn(Periodepost::getBeskrivelseString).setHeader("Beskrivelse");
    }

    @Override
    public void instansOpprettFilterFelter() {
        periodeFilterComboBox = leggTilFilterfelt(0,new ComboBox<>(),"valg");
        periodeFilterComboBox.setItemLabelGenerator(Periode::hentBeskrivendeNavn);
        periodeFilterComboBox.setItems(periodeService.finnAlleEgndePerioder(periodetypeEnum));
        kategoriFilterComboBox = leggTilFilterfelt(1, new ComboBox<>(),"valg");
        kategoriFilterComboBox.setItemLabelGenerator(Kategori::getTittel);
        kategoriFilterComboBox.setItems(kategoriService.finnAlle());
        sumBudsjettFilterIntegerField = leggTilFilterfelt(2, new IntegerField(),">tall");
        sumRegnskapFilterIntegerField = leggTilFilterfelt(3, new IntegerField(),">tall");
        beskrivelseFilterTextField = leggTilFilterfelt(4, new TextField(),"tekst");

    }
}
