package com.hallvardlaerum.grunndata.kategori;

import com.hallvardlaerum.libs.eksportimport.CSVImportmester;
import com.hallvardlaerum.libs.felter.TekstKyklop;
import com.hallvardlaerum.libs.ui.BooleanCombobox;
import com.hallvardlaerum.libs.ui.MasterDetailViewmal;
import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.MenuItem;
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
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;


@Route("kategori")
@UIScope
//@Menu(title = "Kategori", order = 50)
public class KategoriView extends MasterDetailViewmal<Kategori, KategoriRepository> implements InitieringsEgnet {
    private KategoriService kategoriService;
    private KategoriRedigeringsomraade kategoriRedigeringsomraade;

    private TextField tittelFilterTextField;
    private TextField undertittelFilterTextField;
    private BooleanCombobox brukesTilBudsjettFilterBooleanCombobox;
    private BooleanCombobox brukestilRegnskapFilterBooleanCombobox;
    private BooleanCombobox brukesTilFastePosterFilterBooleanCombobox;
    private BooleanCombobox erAktivFilterBooleanCombobox;
    private ComboBox<KategoriType> kategoriTypeFilterComboBox;
    private boolean erInitiert;

    public KategoriView() {
        super();
        Allvitekyklop.hent().setKategoriView(this);
        init();
    }

    @Override
    public boolean erInitiert() {
        return false;
    }

    public void init(){
        if (!erInitiert) {
            this.kategoriService = Allvitekyklop.hent().getKategoriService();
            this.kategoriRedigeringsomraade = Allvitekyklop.hent().getKategoriRedigeringsomraade();
            this.kategoriRedigeringsomraade.settView(this);

            opprettLayout(kategoriService, kategoriRedigeringsomraade);
            leggTilEkstraMenyValg();
            erInitiert = true;
        }
    }

    private void leggTilEkstraMenyValg() {

        hentVerktoeySubMeny().addItem("Importer fra Budsjettpostgrupper (CSV)", e -> importerKategorierFraBudsjettpostgrupperCSV());

    }

    private void importerKategorierFraBudsjettpostgrupperCSV() {
        KategoriFraBudsjettpostgruppeFraCSVImportassistent kategoriFraBudsjettpostgruppeFraCSVImportassistent =
                new KategoriFraBudsjettpostgruppeFraCSVImportassistent();
        CSVImportmester csvImportmester = new CSVImportmester(kategoriFraBudsjettpostgruppeFraCSVImportassistent);
        csvImportmester.velgImportfilOgKjoerImport(Allvitekyklop.hent().getKategoriService());

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

        grid.addColumn(Kategori::getUndertittel).setHeader("Undertittel").setRenderer(opprettKategoriUnderTittelRenderer());
        //grid.addColumn(Kategori::getBrukesTilBudsjett).setHeader("Budsjett");

        grid.addComponentColumn(kategori -> opprettJaNeiIkon(kategori.getBrukesTilBudsjett(), kategori))
                .setTooltipGenerator(kategori -> kategori.getBrukesTilBudsjett()!=null? kategori.getBrukesTilBudsjett().toString() : "")
                .setHeader("Brukes til budsjett").setTextAlign(ColumnTextAlign.CENTER);

        grid.addComponentColumn(kategori -> opprettJaNeiIkon(kategori.getBrukesTilFastePoster(), kategori))
                .setTooltipGenerator(kategori -> kategori.getBrukesTilFastePoster()!=null? kategori.getBrukesTilFastePoster().toString() : "")
                .setHeader("Brukes til faste poster").setTextAlign(ColumnTextAlign.CENTER);

        grid.addComponentColumn(kategori -> opprettJaNeiIkon(kategori.getBrukesTilRegnskap(), kategori))
                .setTooltipGenerator(kategori -> kategori.getBrukesTilRegnskap()!=null? kategori.getBrukesTilRegnskap().toString() : "")
                .setHeader("Brukes til regnskap").setTextAlign(ColumnTextAlign.CENTER);

        grid.addComponentColumn(kategori -> opprettJaNeiIkon(kategori.getErAktiv(), kategori))
                .setTooltipGenerator(kategori -> kategori.getErAktiv()!=null? kategori.getErAktiv().toString() : "")
                .setHeader("Er aktiv").setTextAlign(ColumnTextAlign.CENTER);

        grid.addColumn(Kategori::getKategoriType).setHeader("Type").setRenderer(opprettKategoriTypeRenderer());
        grid.setItems(kategoriService.finnAlle());
    }

    private ComponentRenderer<Span, Kategori> opprettKategoriTypeRenderer(){
        return new ComponentRenderer<>(k -> {
            Span span = new Span();
            if (k.getKategoriType()!=null) {
                span.setText(k.getKategoriType().getTittel());
            }
            if (k.getErAktiv()!=null && !k.getErAktiv()) {
                span.addClassName(LumoUtility.TextColor.TERTIARY);
            }
            return span;
        });
    }

    private ComponentRenderer<Span, Kategori> opprettKategoriTittelRenderer(){
        return new ComponentRenderer<>(k -> {
           Span span = new Span(k.getTittel());
           if (k.getErAktiv()!=null && !k.getErAktiv()) {
               span.addClassName(LumoUtility.TextColor.TERTIARY);
           }
           return span;
        });
    }

    private ComponentRenderer<Span, Kategori> opprettKategoriUnderTittelRenderer(){
        return new ComponentRenderer<>(k -> {
            Span span = new Span(k.getUndertittel());
            if (k.getErAktiv()!=null && !k.getErAktiv()) {
                span.addClassName(LumoUtility.TextColor.TERTIARY);
            }
            return span;
        });
    }

    private Component opprettJaNeiIkon(Boolean avkryssetBoolean, Kategori kategori) {

        Icon icon;

        if (kategori.getErAktiv()!=null && !kategori.getErAktiv()) {
            icon = VaadinIcon.BAN.create();
            icon.getElement().getThemeList().add("badge contrast");
        } else {
            if (avkryssetBoolean!=null && avkryssetBoolean) {
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
        undertittelFilterTextField = leggTilFilterfelt(1, new TextField(),"tekst");
        brukesTilBudsjettFilterBooleanCombobox = leggTilFilterfelt(2, new BooleanCombobox(),"Velg");
        brukesTilFastePosterFilterBooleanCombobox = leggTilFilterfelt(3, new BooleanCombobox(),"Velg");
        brukestilRegnskapFilterBooleanCombobox = leggTilFilterfelt(4, new BooleanCombobox(),"Velg");
        erAktivFilterBooleanCombobox = leggTilFilterfelt(5, new BooleanCombobox(),"Velg");
        kategoriTypeFilterComboBox = leggTilFilterfelt(6,new ComboBox<>(),"Velg");
        kategoriTypeFilterComboBox.setItems(KategoriType.values());
        kategoriTypeFilterComboBox.setItemLabelGenerator(KategoriType::getTittel);
    }
}
