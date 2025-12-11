package com.hallvardlaerum.verktoy;

import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.post.normalpost.NormalpostService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;

@Route("verktoy")
@UIScope
//@Menu(title = "Verktøy", order = 80)
public class VerktoyView extends VerticalLayout implements InitieringsEgnet {
    private NormalpostService normalpostService;
    private boolean erInitiert = false;
    private FormLayout formLayout;
    private String hovedTabnavnString;
    private String ekstraTabnavnString;
    private TabSheet tabSheet;

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
            this.normalpostService = Allvitekyklop.hent().getNormalpostService();
            leggTilKnapper();
            erInitiert = true;
        }
    }

    private void leggTilKnapper(){
        Button importerEkstraFelterButton = new Button("Importer felter utenom entiteten Post");
        importerEkstraFelterButton.addClickListener(e -> normalpostService.importerInnholdIEkstraFeltArrayList());
        add(importerEkstraFelterButton);

        Button skrivUtPDFTestButton = new Button("Skriv ut Test-PDF");
        skrivUtPDFTestButton.addClickListener(e -> RapportMester.printTestPDF());
        add(skrivUtPDFTestButton);

        Button opprettTabsButton = new Button("Opprett Tabs i FormLayout");
        opprettTabsButton.addClickListener(e -> opprettTabs());
        add(opprettTabsButton);

        Button loggDebugButton = new Button("Logg debug");
        loggDebugButton.addClickListener(e -> Loggekyklop.hent().loggDEBUG("DEBUUUUG"));
        add(loggDebugButton);


    }

    private void opprettTabs() {
        //Uten tabs, øverst:
//        formLayout = new FormLayout();
//        TextField navnTextField = new TextField("Navn");
//        formLayout.add(navnTextField);
//        formLayout.setWidthFull();
//        this.add(formLayout);


        //Tabs:
        tabSheet = new TabSheet();
        tabSheet.setSizeFull();
        FormLayout hovedFormLayout = new FormLayout();
        Tab tabHoved = tabSheet.add(hovedTabnavnString,hovedFormLayout);
        tabHoved.setLabel("Hoved");
        TextField adresseTextField = new TextField("Adresse");
        hovedFormLayout.add(adresseTextField);
        TextField postadresseTextField = new TextField("Postadresse");
        hovedFormLayout.add(postadresseTextField);


        FormLayout ekstraFormLayout = new FormLayout();
        Tab tabEkstra = tabSheet.add(ekstraTabnavnString,ekstraFormLayout);
        tabEkstra.setLabel("Ekstra");
        TextField beskrivelseTextField =new TextField("Beskrivelse");
        ekstraFormLayout.add(beskrivelseTextField);
        TextField historieTextField =new TextField("Historie");
        ekstraFormLayout.add(historieTextField);

        this.add(tabSheet);

    }


}
