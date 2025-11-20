package com.hallvardlaerum.grunndata.kategori;

import com.hallvardlaerum.libs.felter.TekstKyklop;
import com.hallvardlaerum.libs.ui.BooleanCombobox;
import com.hallvardlaerum.libs.ui.MasterDetailViewmal;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;


@Route("kategori")
@Menu(title = "Kategori", order = 50)
public class KategoriView extends MasterDetailViewmal<Kategori> {
    private KategoriService kategoriService;
    private KategoriRedigeringsomraade kategoriRedigeringsomraade;

    private TextField tittelFilterTextField;
    private BooleanCombobox brukesTilBudsjettFilterBooleanCombobox;
    private BooleanCombobox brukestilRegnskapFilterBooleanCombobox;
    private BooleanCombobox brukesTilFastePosterFilterBooleanCombobox;
    private BooleanCombobox erAktivFilterBooleanCombobox;
    private ComboBox<KategoriType> kategoriTypeFilterComboBox;

    public KategoriView(KategoriService kategoriService) {
        super();
        this.kategoriService = kategoriService;
        this.kategoriRedigeringsomraade = (KategoriRedigeringsomraade) kategoriService.hentRedigeringsomraadeAktig();
        kategoriRedigeringsomraade.settView(this);
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

        grid.addColumn(Kategori::getTittel).setHeader("Tittel").setRenderer(opprettKategoriTittelRenderer());
        //grid.addColumn(Kategori::getBrukesTilBudsjett).setHeader("Budsjett");

        grid.addComponentColumn(kategori -> opprettJaNeiIkon(kategori.getBrukesTilBudsjett(), kategori))
                .setTooltipGenerator(kategori -> kategori.getBrukesTilBudsjett().toString())
                .setHeader("Brukes til budsjett").setTextAlign(ColumnTextAlign.CENTER);

        grid.addComponentColumn(kategori -> opprettJaNeiIkon(kategori.getBrukesTilFastePoster(), kategori))
                .setTooltipGenerator(kategori -> kategori.getBrukesTilFastePoster().toString())
                .setHeader("Brukes til faste poster").setTextAlign(ColumnTextAlign.CENTER);

        grid.addComponentColumn(kategori -> opprettJaNeiIkon(kategori.getBrukesTilRegnskap(), kategori))
                .setTooltipGenerator(kategori -> kategori.getBrukesTilRegnskap().toString())
                .setHeader("Brukes til regnskap").setTextAlign(ColumnTextAlign.CENTER);

        grid.addComponentColumn(kategori -> opprettJaNeiIkon(kategori.getErAktiv(), kategori))
                .setTooltipGenerator(kategori -> kategori.getErAktiv().toString())
                .setHeader("Er aktiv").setTextAlign(ColumnTextAlign.CENTER);

        grid.addColumn(Kategori::getKategoriType).setHeader("Type").setRenderer(opprettKategoriTypeRenderer());

    }

    private ComponentRenderer<Span, Kategori> opprettKategoriTypeRenderer(){
        return new ComponentRenderer<>(k -> {
            Span span = new Span();
            if (k.getKategoriType()!=null) {
                span.setText(k.getKategoriType().getTittel());
            }
            if (!k.getErAktiv()) {
                span.addClassName(LumoUtility.TextColor.TERTIARY);
            }
            return span;
        });
    }

    private ComponentRenderer<Span, Kategori> opprettKategoriTittelRenderer(){
        return new ComponentRenderer<>(k -> {
           Span span = new Span(k.getTittel());
           if (!k.getErAktiv()) {
               span.addClassName(LumoUtility.TextColor.TERTIARY);
           }
           return span;
        });
    }

    private Component opprettJaNeiIkon(Boolean avkrysset, Kategori kategori) {

        Icon icon;

        if (!kategori.getErAktiv()) {
            icon = VaadinIcon.BAN.create();
            icon.getElement().getThemeList().add("badge contrast");
        } else {
            if (avkrysset) {
                icon = VaadinIcon.CHECK.create();
                icon.getElement().getThemeList().add("badge success");
            } else {
                icon = VaadinIcon.CLOSE_SMALL.create();
                icon.getElement().getThemeList().add("badge error");
            }
        }
        icon.getStyle().set("padding", "var(--lumo-space-xs");
        icon.addClassName(LumoUtility.Background.CONTRAST_30);

        return icon;
    }


    @Override
    public void instansOpprettFilterFelter() {
        tittelFilterTextField = leggTilFilterfelt(0,new TextField(),"tekst");
        brukesTilBudsjettFilterBooleanCombobox = leggTilFilterfelt(1, new BooleanCombobox(),"Velg");
        brukesTilFastePosterFilterBooleanCombobox = leggTilFilterfelt(2, new BooleanCombobox(),"Velg");
        brukestilRegnskapFilterBooleanCombobox = leggTilFilterfelt(3, new BooleanCombobox(),"Velg");
        erAktivFilterBooleanCombobox = leggTilFilterfelt(4, new BooleanCombobox(),"Velg");
        kategoriTypeFilterComboBox = leggTilFilterfelt(5,new ComboBox<>(),"Velg");
        kategoriTypeFilterComboBox.setItems(KategoriType.values());
        kategoriTypeFilterComboBox.setItemLabelGenerator(KategoriType::getTittel);
    }
}
