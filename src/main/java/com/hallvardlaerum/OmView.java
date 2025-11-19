package com.hallvardlaerum;

import com.hallvardlaerum.basis.Versjonskyklop;
import com.hallvardlaerum.grunndata.kategori.KategoriService;
import com.hallvardlaerum.grunndata.kategori.KategoriRedigeringsomraade;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.filerogopplasting.Filkyklop;
import com.hallvardlaerum.libs.ui.MainViewmal;
import com.hallvardlaerum.libs.verktoy.Backupkyklop;
import com.hallvardlaerum.periode.maanedsoversikt.MaanedsoversiktRedigeringsomraade;
import com.hallvardlaerum.periode.maanedsoversikt.MaanedsoversiktService;
import com.hallvardlaerum.post.PostService;
import com.hallvardlaerum.post.normalpost.NormalpostRedigeringsomraade;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route("")
@PermitAll
@Menu(title = "Om...", order = 99)
public class OmView extends MainViewmal {

    public OmView(PostService postService, NormalpostRedigeringsomraade normalPostRedigeringsomraade,
                  KategoriService kategoriService, KategoriRedigeringsomraade kategoriRedigeringsomraade,
                  MaanedsoversiktService maanedsoversiktService, MaanedsoversiktRedigeringsomraade maanedsoversiktRedigeringsomraade
      ) {
        super();

        UI.getCurrent().access(() -> {  // Ensure we're attaching components in a UI thread
            Loggekyklop.hent().settNivaaFEIL();
            Filkyklop.hent().initierRotmappeFile("blaahvalen");
            Versjonskyklop.hent().initier(); // for å initiere versjonskyklop

//TODO: Er det behov for å hente redigeringsomraade fra serviceklassen? Sjekk havaara mot childTable


            kategoriService.initier(kategoriRedigeringsomraade);
            maanedsoversiktService.initier(maanedsoversiktRedigeringsomraade);
            postService.initier(normalPostRedigeringsomraade, kategoriService);

            Backupkyklop.hent().leggTilEntitetservice(kategoriService);
            Backupkyklop.hent().leggTilEntitetservice(maanedsoversiktService);
            Backupkyklop.hent().leggTilEntitetservice(postService);

            opprettLayout(Versjonskyklop.hent());
        });


    }



    public static void showOmView() { // Også for å unngå "not in context"-feilmeldingene
        UI current = UI.getCurrent();
        if (current != null) {
            current.access(() -> current.navigate(OmView.class));
        }
    }

}



