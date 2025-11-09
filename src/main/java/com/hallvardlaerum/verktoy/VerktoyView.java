package com.hallvardlaerum.verktoy;

import com.hallvardlaerum.regnskap.data.Post;
import com.hallvardlaerum.regnskap.service.PostService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.Route;

@Route("verktoy")
@Menu(title = "Verktøy", order = 80)
public class VerktoyView extends VerticalLayout {
    private PostService postService;

    public VerktoyView(PostService postService) {
        this.postService = postService;
        leggTilImporterEkstraFelterButton();
    }

    private void leggTilImporterEkstraFelterButton(){
        Button importerEkstraFelterButton = new Button("Importer felter utenom entiteten Post");
        importerEkstraFelterButton.addClickListener(e -> postService.importerInnholdIEkstraFeltArrayList());
        add(importerEkstraFelterButton);
    }


}
