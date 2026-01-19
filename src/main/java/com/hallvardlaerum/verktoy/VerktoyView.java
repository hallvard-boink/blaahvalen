package com.hallvardlaerum.verktoy;

import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.verktoy.testing.TestDataFabrikk;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;


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
        leggTilKnapper_VaskDataButton();

    }

    private void leggTilKnapper_VaskDataButton() {
        Button button = new Button("Vask data");
        button.addClickListener(e -> new DataDoktor().vaskData());
        add(button);
    }


    private void leggTilKnapper_OpprettTestDataButton() {
        Button opprettTestdataButton = new Button("Opprett testdata");
        opprettTestdataButton.addClickListener(e -> testDataFabrikk.opprettTestData(2050));
        add(opprettTestdataButton);
    }

    private void leggTilKnapper_SlettTestDataButton() {
        Button slettTestdataButton = new Button("Slett testdata");
        slettTestdataButton.addClickListener(e -> testDataFabrikk.slettTestData(2050));
        add(slettTestdataButton);
    }




}
