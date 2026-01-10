package com.hallvardlaerum.verktoy;

import com.hallvardlaerum.kategori.Kategori;
import com.hallvardlaerum.libs.eksportimport.ExcelEksportkyklop;
import com.hallvardlaerum.libs.ui.Gridkyklop;
import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.verktoy.testing.TestDataFabrikk;
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
    private TestDataFabrikk testDataFabrikk;


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
            testDataFabrikk = new TestDataFabrikk();

            leggTilKnapper();

            setSizeFull();

            erInitiert = true;
        }
    }

    private void leggTilKnapper(){
        leggTilKnapper_OpprettTestDataButton();
        leggTilKnapper_SlettTestDataButton();

    }

    private void leggTilKnapper_OpprettTestDataButton() {
        Button opprettTestdataButton = new Button("Opprett testdata");
        opprettTestdataButton.addClickListener(e -> testDataFabrikk.produserData(2050));
        add(opprettTestdataButton);
    }

    private void leggTilKnapper_SlettTestDataButton() {
        Button slettTestdataButton = new Button("Slett testdata");
        slettTestdataButton.addClickListener(e -> testDataFabrikk.slettTestData());
        add(slettTestdataButton);
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
