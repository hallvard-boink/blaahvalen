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
        leggTilKnapper_KorrigerDataButton1();
        leggTilKnapper_KorrigerDataButton2();
        leggTilKnapper_KorrigerDataButton3();

    }

    private void leggTilKnapper_KorrigerDataButton3() {
        Button button = new Button("Korriger inkonsistente poster");
        button.addClickListener(e -> new DataDoktor().korrigerKonkretePoster());
        add(button);
    }

    private void leggTilKnapper_KorrigerDataButton2() {
        Button korrigerDataButton = new Button("Slett periodeposter med kategoritype SKAL_IKKE_KATEGORISERES");
        korrigerDataButton.addClickListener(e -> DataDoktor.slettPeriodeposterMedKategoriType_SKAL_IKKE_KATEGORISERES());
        add(korrigerDataButton);
    }

    private void leggTilKnapper_KorrigerDataButton1() {
        Button korrigerDataButton = new Button("Korriger normalposter som skulle vært utelatt");
        korrigerDataButton.addClickListener(e -> DataDoktor.reparerNormalposterSomSkulleVaertUtelatt());
        add(korrigerDataButton);
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




}
