package com.hallvardlaerum.verktoy;

import com.hallvardlaerum.kategori.Kategori;
import com.hallvardlaerum.libs.eksportimport.ExcelEksportkyklop;
import com.hallvardlaerum.libs.ui.Gridkyklop;
import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;

import java.util.ArrayList;
import java.util.List;


@Route("verktoy")
@UIScope
//@Menu(title = "Verktøy", order = 80)
public class VerktoyView extends VerticalLayout implements InitieringsEgnet {
    private boolean erInitiert = false;
    private Grid<Kategori> grid;


    public VerktoyView() {
        Allvitekyklop.hent().setVerktoyView(this);
        init();
    }

    @Override
    public boolean erInitiert() {
        return erInitiert;
    }

    @Override
    public void init(){
        if (!erInitiert) {
            leggTilKnapper();
            leggTilGrid();
            setSizeFull();

            erInitiert = true;
        }
    }

    private void leggTilKnapper(){
        Button leggTilButtonIGrid = new Button("Eksporter til excel");
        leggTilButtonIGrid.addClickListener(e -> eksporterGridTilExcel());
        add(leggTilButtonIGrid);
    }

    private void eksporterGridTilExcel() {
        List<Kategori> kategoriList = grid.getListDataView().getItems().toList();
        ExcelEksportkyklop.hent().eksporterArrayListAvEntiteterSomXLS(new ArrayList<>(kategoriList), "KategoriFraGrid.xlsx");

    }


    private void leggTilGrid(){
        grid = new Grid<>();
        grid.appendHeaderRow();
        grid.addColumn(Kategori::getTittel).setHeader("Tittel");
        grid.addColumn(Kategori::getUndertittel).setHeader("Undertittel");

        Gridkyklop.hent().tilpassKolonnerIFastradGrid(grid);

        grid.setSizeFull();

        List<Kategori> kategorier = Allvitekyklop.hent().getKategoriService().finnAlle();
        grid.setItems(kategorier);



        add(grid);

    }




}
