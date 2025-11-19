package com.hallvardlaerum.periode;

import com.hallvardlaerum.libs.database.EntitetserviceAktig;
import com.hallvardlaerum.libs.database.SearchCriteria;
import com.hallvardlaerum.libs.felter.Datokyklop;
import com.hallvardlaerum.libs.felter.DatopresisjonEnum;
import com.hallvardlaerum.libs.felter.DesimalMester;
import com.hallvardlaerum.libs.felter.HelTallMester;
import com.hallvardlaerum.libs.ui.MasterDetailViewmal;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeAktig;
import com.hallvardlaerum.periode.maanedsoversikt.MaanedsoversiktRedigeringsomraade;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.ArrayList;


public class PeriodeViewMal extends MasterDetailViewmal<Periode> {
    private Grid<Periode> grid;
    private EntitetserviceAktig<Periode> periodeservice;
    private RedigeringsomraadeAktig<Periode> redigeringsomraade;

    private DatePicker fraDatoFilterDatePicker;
    private TextField beskrivelseFilterTextField;
    private IntegerField resultatFilterIntegerField;


    public PeriodeViewMal(EntitetserviceAktig<Periode> periodeservice) {
        super();
        this.periodeservice = periodeservice;
        redigeringsomraade = periodeservice.hentRedigeringsomraadeAktig();
        redigeringsomraade.settView(this);
        opprettLayout(periodeservice, redigeringsomraade, SplitLayout.Orientation.HORIZONTAL);

    }



    @Override
    public void settFilter() {
        ArrayList<SearchCriteria> searchCriteriaArrayList = new ArrayList<>();

        if (fraDatoFilterDatePicker.getValue()!=null) {
            searchCriteriaArrayList.add(new SearchCriteria("datoFraLocalDate","<", fraDatoFilterDatePicker.getValue()));
        }

        if (beskrivelseFilterTextField.getValue()!=null) {
            searchCriteriaArrayList.add(new SearchCriteria("beskrivelseString",":", beskrivelseFilterTextField.getValue()));
        }

        if (resultatFilterIntegerField.getValue()!=null) {
            searchCriteriaArrayList.add(new SearchCriteria("sumRegnskapResultatInteger","<",resultatFilterIntegerField.getValue()));
        }

        super.brukFiltreIDataprovider(searchCriteriaArrayList);
    }

    @Override
    public void instansOpprettGrid() {
        grid = super.hentGrid();
        grid.addColumn(Periode::getDatoFraLocalDate).setHeader("Dato").setRenderer(opprettDatoRenderer());
        grid.addColumn(Periode::getBeskrivelseString).setHeader("Beskrivelse");
        grid.addColumn(Periode::getSumRegnskapResultatInteger).setHeader("Resultat");
    }

    private ComponentRenderer<Span, Periode> opprettResultatRenderer() {
        return new ComponentRenderer<>(periode -> {
            Span span = new Span();
            if (periode.getSumRegnskapResultatInteger()!=null) {
                span.setText(DesimalMester.konverterDoubleTilFormatertStreng(periode.getSumRegnskapResultatInteger()));
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
