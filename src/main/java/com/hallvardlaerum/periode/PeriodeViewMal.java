package com.hallvardlaerum.periode;

import com.hallvardlaerum.libs.database.EntitetserviceAktig;
import com.hallvardlaerum.libs.database.SearchCriteria;
import com.hallvardlaerum.libs.felter.Datokyklop;
import com.hallvardlaerum.libs.felter.DatopresisjonEnum;
import com.hallvardlaerum.libs.felter.HelTallMester;
import com.hallvardlaerum.libs.ui.MasterDetailViewmal;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeAktig;
import com.hallvardlaerum.libs.ui.ViewmalAktig;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;


public class PeriodeViewMal extends MasterDetailViewmal<Periode, PeriodeRepository> {
    private Grid<Periode> grid;
    private EntitetserviceAktig<Periode, PeriodeRepository> periodeservice;
    private RedigeringsomraadeAktig<Periode> redigeringsomraade;
    private PeriodetypeEnum periodetypeEnum;

    private DatePicker fraDatoFilterDatePicker;
    private TextField beskrivelseFilterTextField;
    private IntegerField resultatFilterIntegerField;


    public PeriodeViewMal() {
        super();

    }

    /**
     * Initiering med defaultverdi på plassering av delelinje i SplitLayout (33 % til grid)
     * @param periodetypeEnum
     * @param viewmalAktig
     * @param periodeservice
     * @param redigeringsomraade
     */
    public void initPeriodeViewMal(PeriodetypeEnum periodetypeEnum,
                                   ViewmalAktig<Periode, ?> viewmalAktig,
                                   EntitetserviceAktig<Periode,PeriodeRepository> periodeservice,
                                   RedigeringsomraadeAktig<Periode> redigeringsomraade
                                   ) {

        initPeriodeViewMal(periodetypeEnum,
                viewmalAktig,
                periodeservice,
                redigeringsomraade,
        33D);

    }

    /**
     * Faktisk initieringsmetode
     *
     * @param periodetypeEnum
     * @param viewmalAktig
     * @param periodeservice
     * @param redigeringsomraade
     * @param splittPlasseringDouble
     */
    public void initPeriodeViewMal(PeriodetypeEnum periodetypeEnum,
                                   ViewmalAktig<Periode, ?> viewmalAktig,
                                   EntitetserviceAktig<Periode,PeriodeRepository> periodeservice,
                                   RedigeringsomraadeAktig<Periode> redigeringsomraade,
                                   Double splittPlasseringDouble) {
        this.periodetypeEnum = periodetypeEnum;
        this.redigeringsomraade = redigeringsomraade;
        this.redigeringsomraade.settView(viewmalAktig);
        this.periodeservice = periodeservice;

        super.opprettLayout(this.periodeservice, redigeringsomraade, SplitLayout.Orientation.HORIZONTAL, splittPlasseringDouble);

        hentVindutittel().setText(periodetypeEnum.getTittel());
        initierGridMedPagedSearch();
    }



    @Override
    public void instansTilpassNyopprettetEntitet(){
        Periode periode = hentEntitet();
        periode.setPeriodetypeEnum(periodetypeEnum);
    }


    public void initierGridMedPagedSearch() {
        super.initierCallbackDataProviderIGrid(
                query -> periodeservice.finnEntiteterMedSpecification(
                        query.getOffset(),
                        query.getLimit(),
                        periodeservice.getEntityFilterSpecification(),
                        Sort.by("datoFraLocalDate").descending()
                ),

                query -> periodeservice.tellAntallMedSpecification(
                        query.getOffset(),
                        query.getLimit(),
                        periodeservice.getEntityFilterSpecification())
        );
    }

    @Override
    public void settFilter() {
        ArrayList<SearchCriteria> searchCriteriaArrayList = new ArrayList<>();

        if (fraDatoFilterDatePicker.getValue()!=null) {
            searchCriteriaArrayList.add(new SearchCriteria("datoFraLocalDate","<", fraDatoFilterDatePicker.getValue()));
        }


        if (beskrivelseFilterTextField.getValue()!=null && !beskrivelseFilterTextField.getValue().isEmpty()) {
            searchCriteriaArrayList.add(new SearchCriteria("beskrivelseString",":", beskrivelseFilterTextField.getValue()));
        }

        if (resultatFilterIntegerField.getValue()!=null) {
            searchCriteriaArrayList.add(new SearchCriteria("sumRegnskapResultatInteger","<",resultatFilterIntegerField.getValue()));
        }

        searchCriteriaArrayList.add(new SearchCriteria("periodetypeEnum",":",periodetypeEnum));
        super.brukFiltreIDataprovider(searchCriteriaArrayList);
    }


    @Override
    public void instansOpprettGrid() {
        grid = super.hentGrid();
        grid.addColumn(Periode::getDatoFraLocalDate).setHeader("Dato").setRenderer(opprettDatoRenderer()).setWidth("100px").setFlexGrow(0);
        grid.addColumn(Periode::getBeskrivelseString).setHeader("Beskrivelse");
        grid.addColumn(Periode::getSumRegnskapResultatInteger).setHeader("Resultat").setRenderer(opprettResultatRenderer()).setTextAlign(ColumnTextAlign.END).setWidth("150px").setFlexGrow(0);

    }



    private ComponentRenderer<Span, Periode> opprettResultatRenderer() {
        return new ComponentRenderer<>(periode -> {
            Span span = new Span();
            if (periode.getSumRegnskapResultatInteger()!=null) {
                span.setText(HelTallMester.integerFormatertSomStortTall(periode.getSumRegnskapResultatInteger()));
                if (periode.getSumRegnskapResultatInteger()<0) {
                    span.addClassName(LumoUtility.TextColor.ERROR);
                }
            }
            return span;
        });
    }

    private ComponentRenderer<Span, Periode> opprettDatoRenderer() {
        return new ComponentRenderer<>(periode -> {
            DatopresisjonEnum datopresisjonEnum = null;
            if (periode.getPeriodetypeEnum()==PeriodetypeEnum.MAANEDSOVERSIKT) {
                datopresisjonEnum = DatopresisjonEnum.MAANED;
            } else if (periode.getPeriodetypeEnum()==PeriodetypeEnum.AARSOVERSIKT) {
                datopresisjonEnum = DatopresisjonEnum.AAR;
            } else {
                datopresisjonEnum = DatopresisjonEnum.FULL_DATO;
            }
            return new Span(Datokyklop.hent().formaterLocalDateMedPresisjon(periode.getDatoFraLocalDate(), datopresisjonEnum));
        });
    }

    @Override
    public void instansOpprettFilterFelter() {
        fraDatoFilterDatePicker = leggTilFilterfelt(0,new DatePicker(),"<dato");
        beskrivelseFilterTextField = leggTilFilterfelt(1, new TextField(),"tekst");
        resultatFilterIntegerField = leggTilFilterfelt(2, new IntegerField(),"<tall");
    }
}
