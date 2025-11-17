package com.hallvardlaerum.grunndata.ui;

import com.hallvardlaerum.grunndata.data.Kategori;
import com.hallvardlaerum.grunndata.data.KategoriType;
import com.hallvardlaerum.grunndata.service.KategoriService;
import com.hallvardlaerum.libs.felter.TekstKyklop;
import com.hallvardlaerum.libs.ui.BooleanCombobox;
import com.hallvardlaerum.libs.ui.MasterDetailViewmal;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.Route;



@Route("kategori")
@Menu(title = "Kategori", order = 50)
public class KategoriView extends MasterDetailViewmal<Kategori> {
    private KategoriService kategoriService;
    private KategoriRedigeringsomraade kategoriRedigeringsomraade;

    private TextField tittelFilterTextField;
    private BooleanCombobox brukesTilBudsjettFilterBooleanCombobox;
    private BooleanCombobox brukestilRegnskapFilterBooleanCombobox;
    private BooleanCombobox erAktivFilterBooleanCombobox;
    private ComboBox<KategoriType> kategoriTypeFilterComboBox;

    public KategoriView(KategoriService kategoriService) {
        super();
        this.kategoriService = kategoriService;
        this.kategoriRedigeringsomraade = (KategoriRedigeringsomraade) kategoriService.hentRedigeringsomraadeAktig();
        kategoriRedigeringsomraade.setDelAvView(this);
        opprettLayout(kategoriService, kategoriRedigeringsomraade);
    }



    @Override
    public void settFilter() {
        GridListDataView<Kategori> listDataView = hentGrid().getListDataView();
        listDataView.removeFilters();


        if (!tittelFilterTextField.getValue().isEmpty()) {
            listDataView.addFilter(k -> TekstKyklop.hent().inneholderTekst(k.getTittel(), tittelFilterTextField.getValue()));
        }


        if (brukesTilBudsjettFilterBooleanCombobox.getValue()!=null) {
            listDataView.addFilter(k -> k.getBrukesTilBudsjett().equals(brukesTilBudsjettFilterBooleanCombobox.getValue()));
        }

        if (brukestilRegnskapFilterBooleanCombobox.getValue()!=null) {
            listDataView.addFilter(k -> k.getBrukesTilRegnskap().equals(brukestilRegnskapFilterBooleanCombobox.getValue()));
        }

        if (erAktivFilterBooleanCombobox.getValue()!=null) {
            listDataView.addFilter(k -> k.getErAktiv().equals(erAktivFilterBooleanCombobox.getValue()));
        }

        if (kategoriTypeFilterComboBox.getValue()!=null) {
            listDataView.addFilter(k -> {
                if (k.getKategoriType()!=null) {
                    return k.getKategoriType().equals(kategoriTypeFilterComboBox.getValue());
                } else {
                    return false;
                }
            });
        }

    }

    @Override
    public void instansOpprettGrid() {
        Grid<Kategori> grid = hentGrid();

        grid.addColumn(Kategori::getTittel).setHeader("Tittel");
        grid.addColumn(Kategori::getBrukesTilBudsjett).setHeader("Budsjett");
        grid.addColumn(Kategori::getBrukesTilRegnskap).setHeader("Regnskap");
        grid.addColumn(Kategori::getErAktiv).setHeader("Aktiv");
        grid.addColumn(k-> {
            if (k.getKategoriType()==null) {
                return "";
            } else {
                return k.getKategoriType().getTittel();
            }
        }).setHeader("Type");

    }

    @Override
    public void instansOpprettFilterFelter() {
        tittelFilterTextField = leggTilFilterfelt(0,new TextField(),"tekst");
        brukesTilBudsjettFilterBooleanCombobox = leggTilFilterfelt(1, new BooleanCombobox(),"Velg");
        brukestilRegnskapFilterBooleanCombobox = leggTilFilterfelt(2, new BooleanCombobox(),"Velg");
        erAktivFilterBooleanCombobox = leggTilFilterfelt(3, new BooleanCombobox(),"Velg");
        kategoriTypeFilterComboBox = leggTilFilterfelt(4,new ComboBox<>(),"Velg");
        kategoriTypeFilterComboBox.setItems(KategoriType.values());
        kategoriTypeFilterComboBox.setItemLabelGenerator(KategoriType::getTittel);
    }
}
