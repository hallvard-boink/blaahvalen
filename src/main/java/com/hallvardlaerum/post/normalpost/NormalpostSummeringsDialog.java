package com.hallvardlaerum.post.normalpost;

import com.hallvardlaerum.kategori.Kategori;
import com.hallvardlaerum.libs.database.EntityFilterSpecification;
import com.hallvardlaerum.libs.database.SearchCriteria;
import com.hallvardlaerum.libs.felter.Datokyklop;
import com.hallvardlaerum.libs.ui.BooleanCombobox;
import com.hallvardlaerum.libs.ui.Gridkyklop;
import com.hallvardlaerum.post.Post;
import com.hallvardlaerum.post.PostklasseEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasPlaceholder;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NormalpostSummeringsDialog extends Dialog {
    protected Grid<Post> normalposterGrid;
    protected NormalpostService postService;
    protected IntegerField sumIntegerField;
    protected IntegerField gjennomsnittIntegerField;

    private IntegerField aarFilterIntegerField;
    private ComboBox<Kategori> kategoriFilterComboBox;
    private TextField tekstfrabankenFilterTextField;
    private TextField egenbeskrivelseFilterTextField;
    private IntegerField innpaakontoFilterIntegerField;
    private IntegerField utfrakontoFilterIntegerField;
    protected HeaderRow headerRowFilterfelter;

    private ConfigurableFilterDataProvider<Post, Void, String> filterProvider;
    private SummeringsDialogEgnet summeringsDialogEgnet;

    // ===========================
    // region 0 Constructor og init
    // ===========================


    public NormalpostSummeringsDialog() {
        super();
    }


    public void init(SummeringsDialogEgnet summeringsDialogEgnet) {
        this.summeringsDialogEgnet = summeringsDialogEgnet;
        this.setResizable(true);
        this.setDraggable(true);
        this.setWidth("90vw");
        this.setHeight("90vh");

        postService = Allvitekyklop.hent().getNormalpostService();

        opprettSoekefelt_opprettGrid();
        headerRowFilterfelter = Gridkyklop.hent().porsjonsviseRaderTilpassKolonnerOgOpprettFilteradIGrid(normalposterGrid);
        opprettFilterFelter();
        tilpassFilterfelterIGrid();
        opprettLayout();
        initierGridMedNormalSoek();
    }

    private void opprettLayout() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        add(verticalLayout);
        verticalLayout.add(new H2("Finne sum ut fra gamle poster"));
        verticalLayout.add(new Span("Bruk dette vinduet til å søke frem relevante poster, for eksempel for å oppdatere faste poster."));

        verticalLayout.add(normalposterGrid);

        HorizontalLayout felterHorizontalLayout = new HorizontalLayout();
        felterHorizontalLayout.setWidthFull();
        sumIntegerField = new IntegerField("Sum av poster");
        gjennomsnittIntegerField = new IntegerField("Gjennomsnitt av poster");
        felterHorizontalLayout.add(sumIntegerField,gjennomsnittIntegerField);
        verticalLayout.add(felterHorizontalLayout);

        HorizontalLayout knapperHorizontalLayout = new HorizontalLayout();
        knapperHorizontalLayout.setWidthFull();
        Button okSettInnSumButton = new Button("Ok, sett inn sum");
        okSettInnSumButton.addClickListener(e -> {
            summeringsDialogEgnet.oppdaterEtterSummeringsDialog(sumIntegerField.getValue());
            this.close();
        });
        okSettInnSumButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button okSettInnGjsnButton = new Button("Ok, sett inn gjennomsnitt");
        okSettInnGjsnButton.addClickListener(e -> {
            summeringsDialogEgnet.oppdaterEtterSummeringsDialog(gjennomsnittIntegerField.getValue());
            this.close();
        });

        Button fjernAlleMarkeringer = new Button("Fjern alle markeringer");
        fjernAlleMarkeringer.addClickListener(e -> {
            normalposterGrid.getSelectionModel().deselectAll();
            summerMarkerteRaderINormalposterGrid();
        });
        Button  avbrytButton = new Button("Avbryt");
        avbrytButton.addClickListener(e -> this.close());
        knapperHorizontalLayout.add(okSettInnSumButton, okSettInnGjsnButton,  fjernAlleMarkeringer, avbrytButton);
        verticalLayout.add(knapperHorizontalLayout);


    }




    private void opprettSoekefelt_opprettGrid() {
        normalposterGrid = new Grid<>();

        normalposterGrid.addColumn(Post::getDatoLocalDate).setHeader("Dato");
        normalposterGrid.addColumn(Post::getKategori).setHeader("Kategori");
        normalposterGrid.addColumn(Post::getTekstFraBankenString).setHeader("Tekst fra banken");
        normalposterGrid.addColumn(Post::getBeskrivelseString).setHeader("Beskrivelse");
        normalposterGrid.addColumn(Post::getInnPaaKontoInteger).setHeader("Inn");
        normalposterGrid.addColumn(Post::getUtFraKontoInteger).setHeader("Ut");

        //TODO: Denne kalles ikke - bruk annen listener? Sjekk andre grids

        normalposterGrid.addItemClickListener(e -> {
                Post valgtPost = e.getItem();
                if (normalposterGrid.getSelectionModel().isSelected(valgtPost)) {
                    normalposterGrid.deselect(valgtPost);
                } else {
                    normalposterGrid.select(valgtPost);
                }
                summerMarkerteRaderINormalposterGrid();
        });

        normalposterGrid.setSizeFull();
        normalposterGrid.setSelectionMode(Grid.SelectionMode.MULTI);
    }

    private void summerMarkerteRaderINormalposterGrid() {

        Integer sumInteger = 0;
        List<Post> valgtePoster = normalposterGrid.getSelectionModel().getSelectedItems().stream().toList();
        for (Post post : valgtePoster) {
            sumInteger = post.getUtFraKontoInteger() + sumInteger;
        }
        sumIntegerField.setValue(sumInteger);
        gjennomsnittIntegerField.setValue(sumInteger/valgtePoster.size());
    }

    public void opprettFilterFelter() {
        aarFilterIntegerField = leggTilFilterfelt(0, new IntegerField(),"År");

        kategoriFilterComboBox = leggTilFilterfelt(1, new ComboBox<>(),"Velg");
        kategoriFilterComboBox.setItems(Allvitekyklop.hent().getKategoriService().finnAlleUnderkategorier());
        kategoriFilterComboBox.setItemLabelGenerator(Kategori::hentKortnavn);

        tekstfrabankenFilterTextField = leggTilFilterfelt(2, new TextField(),"tekst");
        egenbeskrivelseFilterTextField = leggTilFilterfelt(3,new TextField(), "tekst");

        innpaakontoFilterIntegerField = leggTilFilterfelt(4, new IntegerField(),"> tall");
        utfrakontoFilterIntegerField = leggTilFilterfelt(5, new IntegerField(), "> tall");

    }

    private <C extends Component> C leggTilFilterfelt(Integer cellIndex, C component, String feltnavn) {
        if (component instanceof HasPlaceholder) {
            ((HasPlaceholder)component).setPlaceholder(feltnavn);
        }

        if (component instanceof DatePicker) {
            Datokyklop.hent().fiksDatoformat((DatePicker)component);
        } else if (component instanceof DateTimePicker) {
            Datokyklop.hent().fiksDatotidformat((DateTimePicker)component);
        }

        ((HeaderRow.HeaderCell)normalposterGrid.getHeaderRows().get(1).getCells().get(cellIndex)).setComponent(component);
        return component;
    }

    protected void tilpassFilterfelterIGrid() {
        List<HeaderRow> headerRows = this.normalposterGrid.getHeaderRows();

        for(HeaderRow.HeaderCell cell : ((HeaderRow)headerRows.getLast()).getCells()) {
            Component component = cell.getComponent();
            if (component instanceof TextField textFieldFilter) {
                textFieldFilter.setValueChangeMode(ValueChangeMode.LAZY);
                textFieldFilter.setWidthFull();
                textFieldFilter.setClearButtonVisible(true);
                textFieldFilter.addValueChangeListener((e) -> this.settFilter());
            } else if (cell.getComponent() instanceof ComboBox) {
                ComboBox comboBox = (ComboBox)cell.getComponent();
                comboBox.setWidthFull();
                comboBox.setClearButtonVisible(true);
                comboBox.addValueChangeListener((e) -> this.settFilter());
            } else {
                component = cell.getComponent();
                if (component instanceof DatePicker) {
                    DatePicker datePicker = (DatePicker)component;
                    datePicker.setWidthFull();
                    datePicker.setLocale(Locale.forLanguageTag("no"));
                    datePicker.setClearButtonVisible(true);
                    datePicker.addValueChangeListener((e) -> this.settFilter());
                } else if (cell.getComponent() instanceof Checkbox) {
                    BooleanCombobox booleanCombobox = new BooleanCombobox();
                    booleanCombobox.setWidthFull();
                    booleanCombobox.addValueChangeListener((e) -> this.settFilter());
                    booleanCombobox.setClearButtonVisible(true);
                    cell.setComponent(booleanCombobox);
                } else {
                    component = cell.getComponent();
                    if (component instanceof IntegerField) {
                        IntegerField integerField = (IntegerField)component;
                        integerField.setValueChangeMode(ValueChangeMode.LAZY);
                        integerField.setWidthFull();
                        integerField.setClearButtonVisible(true);
                        integerField.addValueChangeListener((e) -> this.settFilter());
                    } else {
                        component = cell.getComponent();
                        if (component instanceof NumberField) {
                            NumberField numberField = (NumberField)component;
                            numberField.setValueChangeMode(ValueChangeMode.LAZY);
                            numberField.setWidthFull();
                            numberField.setClearButtonVisible(true);
                            numberField.addValueChangeListener((e) -> this.settFilter());
                        }
                    }
                }
            }
        }
    }



    // endregion



    // ===========================
    // region 1 Opprett soekefelt
    // ===========================



    public void initierGridMedNormalSoek(){

        initierCallbackDataProviderIGrid(
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

    public void initierCallbackDataProviderIGrid(@NotNull CallbackDataProvider.FetchCallback<Post, String> fetchCallback, @NotNull CallbackDataProvider.CountCallback<Post, String> countCallback) {
        CallbackDataProvider<Post, String> dataProvider = DataProvider.fromFilteringCallbacks(fetchCallback, countCallback);
        this.filterProvider = dataProvider.withConfigurableFilter();
        this.normalposterGrid.setItems(this.filterProvider);
        this.settFilter();
    }

    // endregion




    // ===========================
    // region 2 Søk og oppdatering
    // ===========================

    public void settFilter() {
        ArrayList<SearchCriteria> filtre = new ArrayList<>();

        filtre.add(new SearchCriteria("postklasseEnum",":", PostklasseEnum.NORMALPOST));

        if (aarFilterIntegerField.getValue()!=null) {
            filtre.add(new SearchCriteria("datoLocalDate",">=", LocalDate.of(aarFilterIntegerField.getValue(),1,1)));
            filtre.add(new SearchCriteria("datoLocalDate","<=", LocalDate.of(aarFilterIntegerField.getValue(),12,31)));
        }

        if (!tekstfrabankenFilterTextField.getValue().isEmpty()) {
            filtre.add(new SearchCriteria("tekstFraBankenString",":",tekstfrabankenFilterTextField.getValue()));
        }

        if (!egenbeskrivelseFilterTextField.getValue().isEmpty()) {
            filtre.add(new SearchCriteria("beskrivelseString",":",egenbeskrivelseFilterTextField.getValue()));
        }

        if (innpaakontoFilterIntegerField.getValue()!=null) {
            filtre.add(new SearchCriteria("innPaaKontoInteger",">",innpaakontoFilterIntegerField.getValue()));
        }

        if (utfrakontoFilterIntegerField.getValue()!= null) {
            filtre.add(new SearchCriteria("utFraKontoInteger",">",utfrakontoFilterIntegerField.getValue()));
        }

        if (kategoriFilterComboBox.getValue()!=null) {
            filtre.add(new SearchCriteria("kategori",":",kategoriFilterComboBox.getValue()));
        }


        brukFiltreIDataprovider(filtre);
        //super.oppdaterAntallRaderNederstIGrid();

    }

    public void brukFiltreIDataprovider(ArrayList<SearchCriteria> filtre) {
        this.brukFiltreIDataprovider(filtre, EntityFilterSpecification.OperatorEnum.AND);
    }

    public void brukFiltreIDataprovider(ArrayList<SearchCriteria> filtre, EntityFilterSpecification.OperatorEnum operatorEnum) {
        postService.setEntityFilterSpecification(new EntityFilterSpecification(filtre, operatorEnum));
        filterProvider.setFilter("");
        normalposterGrid.getDataProvider().refreshAll();

        this.oppdaterAntallRaderNederstIGrid();
    }

    public void oppdaterAntallRaderNederstIGrid() {
        Grid.Column<Post> column = (Grid.Column)this.normalposterGrid.getColumns().getFirst();
        int antallRader = postService.tellAntallMedSpecification();
        column.setFooter("Antall: " + antallRader);
    }


    // endregion






}
