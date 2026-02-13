package com.hallvardlaerum.periodepost;

import com.hallvardlaerum.kategori.Kategori;
import com.hallvardlaerum.libs.database.SearchCriteria;
import com.hallvardlaerum.libs.ui.GridInnholdsTypeEnum;
import com.hallvardlaerum.libs.ui.HallvardsGrid;
import com.hallvardlaerum.periode.Periode;
import com.hallvardlaerum.periodepost.maanedsoversiktpost.MaanedsoversiktpostService;
import com.hallvardlaerum.post.normalpost.NormalpostService;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;

public class PeriodepostGrid extends HallvardsGrid<Periodepost, PeriodepostRepository> {
    MaanedsoversiktpostService maanedsoversiktpostService;


    private ComboBox<PeriodepostTypeEnum> periodepostTypeEnumComboBox;
    private ComboBox<Periode> periodeFilterComboBox;
    private ComboBox<Kategori> kategoriFilterComboBox;
    private IntegerField sumBudsjettFilterIntegerField;
    private IntegerField sumRegnskapFilterIntegerField;
    private TextField tittelFilterTextField;
    private TextField beskrivelseFilterTextField;

    public PeriodepostGrid() {
        super();
        maanedsoversiktpostService = Allvitekyklop.hent().getMaanedsoversiktpostService();
        super.init(GridInnholdsTypeEnum.PORSJONSVIS, maanedsoversiktpostService);
        opprettKolonner();
        initierPorsjonsvisSoek();
    }

    private void opprettKolonner() {
        periodepostTypeEnumComboBox = opprettComboBoxForEnumKolonneMedFilterfelt(Periodepost::getPeriodepostTypeEnum,"Periodeposttype");
        periodepostTypeEnumComboBox.setItems(PeriodepostTypeEnum.values());
        periodeFilterComboBox = opprettComboBoxForEntitetKolonneMedFilterfelt(Periodepost::getPeriode, "Periode");
        periodeFilterComboBox.setItems(Allvitekyklop.hent().getAarsoversiktService().finnAlleSortertEtterDatoFallende());
        kategoriFilterComboBox = opprettComboBoxForEntitetKolonneMedFilterfelt(Periodepost::getKategori, "Kategori");
        kategoriFilterComboBox.setItems(Allvitekyklop.hent().getKategoriService().finnAlleHovedkategorier());
        sumBudsjettFilterIntegerField = opprettIntegerKolonneMedFilterfelt(Periodepost::getSumBudsjettInteger,"Budsjett");
        sumRegnskapFilterIntegerField = opprettIntegerKolonneMedFilterfelt(Periodepost::getSumRegnskapInteger,"Regnskap");
        tittelFilterTextField = opprettTekstKolonneMedFilterfelt(Periodepost::getTittelString,"Tittel");
        beskrivelseFilterTextField = opprettTekstKolonneMedFilterfelt(Periodepost::getBeskrivelseString,"Beskrivelse");

    }


    private void initierPorsjonsvisSoek() {
        super.initierCallbackDataProviderIGrid(
                q -> maanedsoversiktpostService.finnEntiteterMedSpecification(
                        q.getOffset(),
                        q.getLimit(),
                        maanedsoversiktpostService.getEntityFilterSpecification(),
                        Sort.by("periode.datoFraLocalDate").descending().and(Sort.by("kategori.tittel").and(Sort.by("kategori.undertittel").ascending()))
                ),

                query -> maanedsoversiktpostService.tellAntallMedSpecification(
                        query.getOffset(),
                        query.getLimit(),
                        maanedsoversiktpostService.getEntityFilterSpecification())
        );
    }


    @Override
    public void settFilter() {
        ArrayList<SearchCriteria> searchCriteriaArrayList = new ArrayList<>();

        if (periodepostTypeEnumComboBox.getValue() != null) {
            searchCriteriaArrayList.add(new SearchCriteria("periodepostTypeEnum", ":", periodepostTypeEnumComboBox.getValue()));
        }

        if (periodeFilterComboBox.getValue() != null) {
            searchCriteriaArrayList.add(new SearchCriteria("periode", ":", periodeFilterComboBox.getValue()));
        }

        if (kategoriFilterComboBox.getValue() != null) {
            searchCriteriaArrayList.add(new SearchCriteria("kategori", ":", kategoriFilterComboBox.getValue()));
        }

        if (sumRegnskapFilterIntegerField.getValue() != null) {
            searchCriteriaArrayList.add(new SearchCriteria("sumRegnskapInteger", ">", sumRegnskapFilterIntegerField.getValue()));
        }


        if (tittelFilterTextField.getValue() != null && !tittelFilterTextField.getValue().isEmpty()) {
            searchCriteriaArrayList.add(new SearchCriteria("tittelString", ":", tittelFilterTextField.getValue()));
        }


        if (sumBudsjettFilterIntegerField.getValue() != null) {
            searchCriteriaArrayList.add(new SearchCriteria("sumBudsjettInteger", ">", sumBudsjettFilterIntegerField.getValue()));
        }

        if (beskrivelseFilterTextField.getValue() != null && !beskrivelseFilterTextField.getValue().isEmpty()) {
            searchCriteriaArrayList.add(new SearchCriteria("beskrivelseString", ":", beskrivelseFilterTextField.getValue()));
        }

        super.brukFiltreIDataprovider(searchCriteriaArrayList);
    }
}
