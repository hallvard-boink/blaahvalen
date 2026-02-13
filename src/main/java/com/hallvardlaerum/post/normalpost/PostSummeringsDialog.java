package com.hallvardlaerum.post.normalpost;

import com.hallvardlaerum.libs.ui.HallvardsGrid;
import com.hallvardlaerum.periodepost.Periodepost;
import com.hallvardlaerum.periodepost.PeriodepostGrid;
import com.hallvardlaerum.post.Post;
import com.hallvardlaerum.post.PostRepository;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.IntegerField;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class PostSummeringsDialog extends Dialog {
    protected NormalpostGrid normalpostGrid;
    protected Grid<Periodepost> periodepostGrid;
    protected NormalpostService postService;
    protected IntegerField sumIntegerField;
    protected IntegerField gjennomsnittIntegerField;
    private SummeringsDialogEgnet summeringsDialogEgnet;

// ===========================
// region 0 Constructor og init
// ===========================


    public PostSummeringsDialog() {
        super();
    }


    public void init(SummeringsDialogEgnet summeringsDialogEgnet) {
        this.summeringsDialogEgnet = summeringsDialogEgnet;
        this.setResizable(true);
        this.setDraggable(true);
        this.setWidth("85vw");
        this.setHeight("85vh");

        postService = Allvitekyklop.hent().getNormalpostService();

        opprettGrid_normalposter();
        opprettGrid_periodeposter();

        opprettFellesLayout();

    }

// endregion


// ===========================
// region 1 Opprett Layout og komponenter
// ===========================



    private void opprettGrid_periodeposter() {
        periodepostGrid = new PeriodepostGrid();
        periodepostGrid.addItemClickListener(e -> {
            Periodepost valgtPeriodepost = e.getItem();
            if (periodepostGrid.getSelectionModel().isSelected(valgtPeriodepost)) {
                periodepostGrid.deselect(valgtPeriodepost);
            } else {
                periodepostGrid.select(valgtPeriodepost);
            }
            oppdaterSumOgGjennomsnittFraMarkerteRader_PeriodepostGrid();
        });

        periodepostGrid.setSelectionMode(Grid.SelectionMode.MULTI);
    }


    private void opprettGrid_normalposter() {
        normalpostGrid = new NormalpostGrid(NormalpostGrid.Type.FOR_SOEK);
        normalpostGrid.addItemClickListener(e -> {
            Post valgtPost = e.getItem();
            if (normalpostGrid.getSelectionModel().isSelected(valgtPost)) {
                normalpostGrid.deselect(valgtPost);
            } else {
                normalpostGrid.select(valgtPost);
            }
            oppdaterSumOgGjennomsnittFraMarkerteRader_PostGrid(normalpostGrid);
        });

        normalpostGrid.setSelectionMode(Grid.SelectionMode.MULTI);
    }

    private void opprettFellesLayout() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        add(verticalLayout);

        // Tittelstripe
        verticalLayout.add(new H2("Finne sum ut fra gamle poster"));
        verticalLayout.add(new Span("Bruk dette vinduet til å søke frem relevante poster, for eksempel for å oppdatere faste poster."));

        // Faneseilstripe
        TabSheet tabSheet = new TabSheet();
        tabSheet.add("Normalposter", normalpostGrid);
        tabSheet.add("Periodeposter", periodepostGrid);
        tabSheet.setSizeFull();
        verticalLayout.add(tabSheet);

        // Sum og Gjennomsnittsfelter
        HorizontalLayout felterHorizontalLayout = new HorizontalLayout();
        felterHorizontalLayout.setWidthFull();
        sumIntegerField = new IntegerField("Sum av poster");
        gjennomsnittIntegerField = new IntegerField("Gjennomsnitt av poster");
        felterHorizontalLayout.add(sumIntegerField, gjennomsnittIntegerField);
        verticalLayout.add(felterHorizontalLayout);

        // Knapper
        HorizontalLayout knapperHorizontalLayout = new HorizontalLayout();
        knapperHorizontalLayout.setWidthFull();
        knapperHorizontalLayout.add(
                opprettButton_okSettInnSum(),
                opprettButton_okSettInnGjennomsnitt(),
                opprettButton_fjernAlleMarkeringer(),
                opprettButton_avbryt());
        verticalLayout.add(knapperHorizontalLayout);

    }


    private Button opprettButton_avbryt() {
        Button avbrytButton = new Button("Avbryt");
        avbrytButton.addClickListener(e -> this.close());
        return avbrytButton;
    }

    private Button opprettButton_fjernAlleMarkeringer() {
        Button fjernAlleMarkeringer = new Button("Fjern alle markeringer");
        fjernAlleMarkeringer.addClickListener(e -> {
            normalpostGrid.getSelectionModel().deselectAll();
            periodepostGrid.getSelectionModel().deselectAll();
            oppdaterSumOgGjennomsnittFraMarkerteRader_PostGrid(normalpostGrid);
        });
        return fjernAlleMarkeringer;
    }

    private Button opprettButton_okSettInnGjennomsnitt() {
        Button okSettInnGjsnButton = new Button("Ok, sett inn gjennomsnitt");
        okSettInnGjsnButton.addClickListener(e -> {
            summeringsDialogEgnet.oppdaterEtterSummeringsDialog(gjennomsnittIntegerField.getValue());
            this.close();
        });
        okSettInnGjsnButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return okSettInnGjsnButton;
    }

    private @NonNull Button opprettButton_okSettInnSum() {
        Button okSettInnSumButton = new Button("Ok, sett inn sum");
        okSettInnSumButton.addClickListener(e -> {
            summeringsDialogEgnet.oppdaterEtterSummeringsDialog(sumIntegerField.getValue());
            this.close();
        });
        okSettInnSumButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return okSettInnSumButton;
    }



// endregion


// ===========================
// region 2 Søk og oppdatering
// ===========================



    private void oppdaterSumOgGjennomsnittFraMarkerteRader_PostGrid(HallvardsGrid<Post, PostRepository> grid) {

        int sumInteger = 0;
        List<Post> valgtePoster = grid.getSelectionModel().getSelectedItems().stream().toList();
        for (Post post : valgtePoster) {
            sumInteger = sumInteger + post.getUtFraKontoInteger();
        }
        sumIntegerField.setValue(sumInteger);
        if (valgtePoster.isEmpty()) {
            gjennomsnittIntegerField.setValue(0);
        } else {
            gjennomsnittIntegerField.setValue(sumInteger / valgtePoster.size());
        }
    }

    private void oppdaterSumOgGjennomsnittFraMarkerteRader_PeriodepostGrid() {
        int sumInteger = 0;
        List<Periodepost> valgtePoster = periodepostGrid.getSelectionModel().getSelectedItems().stream().toList();
        for (Periodepost periodepost : valgtePoster) {
            if (periodepost.getSumRegnskapInteger()>0) {
                sumInteger = sumInteger + periodepost.getSumRegnskapInteger();
            } else {
                sumInteger = sumInteger + periodepost.getSumBudsjettInteger();
            }
        }
        sumIntegerField.setValue(sumInteger);
        gjennomsnittIntegerField.setValue(sumInteger / valgtePoster.size());
    }



    // endregion


}
