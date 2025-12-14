package com.hallvardlaerum;

import com.hallvardlaerum.basis.Versjonskyklop;
import com.hallvardlaerum.grunndata.kategori.KategoriRepository;
import com.hallvardlaerum.grunndata.kategori.KategoriService;
import com.hallvardlaerum.grunndata.kategori.KategoriRedigeringsomraade;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.filerogopplasting.Filkyklop;
import com.hallvardlaerum.libs.ui.MainViewmal;
import com.hallvardlaerum.libs.verktoy.Backupkyklop;
import com.hallvardlaerum.periode.PeriodeRepository;
import com.hallvardlaerum.periode.aarsoversikt.AarsoversiktRedigeringsomraade;
import com.hallvardlaerum.periode.aarsoversikt.AarsoversiktService;
import com.hallvardlaerum.periode.maanedsoversikt.MaanedsoversiktRedigeringsomraade;
import com.hallvardlaerum.periode.maanedsoversikt.MaanedsoversiktService;
import com.hallvardlaerum.periodepost.PeriodepostRepository;
import com.hallvardlaerum.periodepost.aarsoversiktpost.AarsoversiktpostRedigeringsomraade;
import com.hallvardlaerum.periodepost.aarsoversiktpost.AarsoversiktpostService;
import com.hallvardlaerum.periodepost.maanedsoversiktpost.MaanedsoversiktpostRedigeringsomraade;
import com.hallvardlaerum.periodepost.maanedsoversiktpost.MaanedsoversiktpostService;
import com.hallvardlaerum.post.PostRepository;
import com.hallvardlaerum.post.budsjettpost.BudsjettpostRedigeringsomraade;
import com.hallvardlaerum.post.budsjettpost.BudsjettpostService;
import com.hallvardlaerum.post.normalpost.NormalpostRedigeringsomraade;
import com.hallvardlaerum.post.normalpost.NormalpostService;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.security.PermitAll;


@Route("")
@PermitAll
//@Menu(title = "Om...", order = 99)
@UIScope
public class OmView extends MainViewmal {
    private Boolean erInitiert = false;

    public OmView(PeriodeRepository periodeRepository, PeriodepostRepository periodepostRepository, PostRepository postRepository,
                  KategoriRepository kategoriRepository, KategoriService kategoriService, KategoriRedigeringsomraade kategoriRedigeringsomraade,
                  AarsoversiktService aarsoversiktService, AarsoversiktRedigeringsomraade aarsoversiktRedigeringsomraade,
                  MaanedsoversiktService maanedsoversiktService, MaanedsoversiktRedigeringsomraade maanedsoversiktRedigeringsomraade,
                  MaanedsoversiktpostService maanedsoversiktpostService, MaanedsoversiktpostRedigeringsomraade maanedsoversiktpostRedigeringsomraade, MaanedsoversiktpostRedigeringsomraade maanedsoversiktpostRedigeringsomraadeTilDialog,
                  AarsoversiktpostService aarsoversiktpostService, AarsoversiktpostRedigeringsomraade aarsoversiktpostRedigeringsomraade, AarsoversiktpostRedigeringsomraade aarsoversiktpostRedigeringsomraadeTilDialog,
                  NormalpostService normalpostService, NormalpostRedigeringsomraade normalPostRedigeringsomraade,
                  BudsjettpostService budsjettpostService, BudsjettpostRedigeringsomraade budsjettpostRedigeringsomraade
      )
    {
        super();

        //TODO MaanedsoversiktpostRedigeringsomraade og MaanedsoversiktpostRedigeringsomraadeTilDialog er samme instans. Bruk Spring Boot annotering (@Qualifier?) til å skille dem

        UI.getCurrent().access(() -> {  // Ensure we're attaching components in a UI thread
            if (!erInitiert) {
                erInitiert = true;

                Loggekyklop.hent().settNivaaFEIL();
                Filkyklop.hent().initierRotmappeFile("blaahvalen");
                Versjonskyklop.hent().initier(); // for å initiere versjonskyklop

                Allvitekyklop.hent().setKategoriRepository(kategoriRepository);
                Allvitekyklop.hent().setKategoriService(kategoriService);
                Allvitekyklop.hent().setKategoriRedigeringsomraade(kategoriRedigeringsomraade);

                Allvitekyklop.hent().setPeriodeRepository(periodeRepository);
                Allvitekyklop.hent().setPeriodepostRepository(periodepostRepository);
                Allvitekyklop.hent().setPostRepository(postRepository);

                Allvitekyklop.hent().setAarsoversiktService(aarsoversiktService);
                Allvitekyklop.hent().setAarsoversiktRedigeringsomraade(aarsoversiktRedigeringsomraade);

                Allvitekyklop.hent().setMaanedsoversiktService(maanedsoversiktService);
                Allvitekyklop.hent().setMaanedsoversiktRedigeringsomraade(maanedsoversiktRedigeringsomraade);

                Allvitekyklop.hent().setAarsoversiktpostService(aarsoversiktpostService);
                Allvitekyklop.hent().setAarsoversiktpostRedigeringsomraade(aarsoversiktpostRedigeringsomraade);
                Allvitekyklop.hent().setAarsoversiktpostRedigeringsomraadeTilDialog(aarsoversiktpostRedigeringsomraadeTilDialog);

                Allvitekyklop.hent().setMaanedsoversiktpostService(maanedsoversiktpostService);
                Allvitekyklop.hent().setMaanedsoversiktpostRedigeringsomraade(maanedsoversiktpostRedigeringsomraade);
                Allvitekyklop.hent().setMaanedsoversiktpostRedigeringsomraadeTilDialog(maanedsoversiktpostRedigeringsomraadeTilDialog);

                Allvitekyklop.hent().setNormalpostService(normalpostService);
                Allvitekyklop.hent().setNormalpostRedigeringsomraade(normalPostRedigeringsomraade);

                Allvitekyklop.hent().setBudsjettpostService(budsjettpostService);
                Allvitekyklop.hent().setBudsjettpostRedigeringsomraade(budsjettpostRedigeringsomraade);

                Backupkyklop.hent().leggTilEntitetservice(kategoriService);
                Backupkyklop.hent().leggTilEntitetservice(maanedsoversiktService); //sjekk at alle poster (også årsoversikter) blir eksportert
                Backupkyklop.hent().leggTilEntitetservice(maanedsoversiktpostService);  //sjekk at alle poster (også årsoversiktposter) blir eksportert
                //Backupkyklop.hent().leggTilEntitetservice(aarsoversiktService);
                //Backupkyklop.hent().leggTilEntitetservice(aarsoversiktpostService);
                Backupkyklop.hent().leggTilEntitetservice(normalpostService);

                Allvitekyklop.hent().initierAlleObjekter();

                opprettLayout(Versjonskyklop.hent());
            }

            //TODO: Når er det behov for å hente redigeringsomraade fra serviceklassen? Sjekk havaara mot childTable

        });


    }



    public static void showOmView() { // Også for å unngå "not in context"-feilmeldingene
        UI current = UI.getCurrent();
        if (current != null) {
            current.access(() -> current.navigate(OmView.class));
        }
    }

}



