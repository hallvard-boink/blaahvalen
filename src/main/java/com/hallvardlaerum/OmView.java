package com.hallvardlaerum;

import com.hallvardlaerum.basis.Versjonskyklop;
import com.hallvardlaerum.kategori.KategoriRepository;
import com.hallvardlaerum.kategori.KategoriService;
import com.hallvardlaerum.kategori.KategoriRedigeringsomraade;
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
import com.hallvardlaerum.periodepost.kostnadspakke.KostnadspakkeService;
import com.hallvardlaerum.periodepost.maanedsoversiktpost.MaanedsoversiktpostRedigeringsomraade;
import com.hallvardlaerum.periodepost.maanedsoversiktpost.MaanedsoversiktpostService;
import com.hallvardlaerum.periodepost.kostnadspakke.KostnadspakkeRedigeringsomraade;
import com.hallvardlaerum.post.PostRepository;
import com.hallvardlaerum.post.budsjettpost.BudsjettpostRedigeringsomraade;
import com.hallvardlaerum.post.budsjettpost.BudsjettpostService;
import com.hallvardlaerum.post.normalpost.NormalpostRedigeringsomraade;
import com.hallvardlaerum.post.normalpost.NormalpostService;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Image;
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
                  MaanedsoversiktpostService maanedsoversiktpostService, MaanedsoversiktpostRedigeringsomraade maanedsoversiktpostRedigeringsomraade,
                  AarsoversiktpostService aarsoversiktpostService, AarsoversiktpostRedigeringsomraade aarsoversiktpostRedigeringsomraade,
                  NormalpostService normalpostService, NormalpostRedigeringsomraade normalPostRedigeringsomraade,
                  BudsjettpostService budsjettpostService, BudsjettpostRedigeringsomraade budsjettpostRedigeringsomraade,
                  KostnadspakkeService kostnadspakkeService, KostnadspakkeRedigeringsomraade kostnadspakkeRedigeringsomraade
      )
    {
        super();


        UI.getCurrent().access(() -> {  // Ensure we're attaching components in a UI thread
            if (!erInitiert) {
                erInitiert = true;

                Loggekyklop.hent().settNivaaDEBUG();
                Filkyklop.hent().initierRotmappeFile("blaahvalen");
                Versjonskyklop.hent().initier(); // for å initiere versjonskyklop

                Allvitekyklop ak = Allvitekyklop.hent();

                ak.setKategoriRepository(kategoriRepository);
                ak.setKategoriService(kategoriService);
                ak.setKategoriRedigeringsomraade(kategoriRedigeringsomraade);

                ak.setPeriodeRepository(periodeRepository);
                ak.setPeriodepostRepository(periodepostRepository);
                ak.setPostRepository(postRepository);

                ak.setAarsoversiktService(aarsoversiktService);
                ak.setAarsoversiktRedigeringsomraade(aarsoversiktRedigeringsomraade);

                ak.setMaanedsoversiktService(maanedsoversiktService);
                ak.setMaanedsoversiktRedigeringsomraade(maanedsoversiktRedigeringsomraade);

                ak.setAarsoversiktpostService(aarsoversiktpostService);
                ak.setAarsoversiktpostRedigeringsomraade(aarsoversiktpostRedigeringsomraade);

                ak.setMaanedsoversiktpostService(maanedsoversiktpostService);
                ak.setMaanedsoversiktpostRedigeringsomraade(maanedsoversiktpostRedigeringsomraade);

                ak.setNormalpostService(normalpostService);
                ak.setNormalpostRedigeringsomraade(normalPostRedigeringsomraade);

                ak.setBudsjettpostService(budsjettpostService);
                ak.setBudsjettpostRedigeringsomraade(budsjettpostRedigeringsomraade);

                ak.setKostnadspakkeService(kostnadspakkeService);
                ak.setKostnadspakkeRedigeringsomraade(kostnadspakkeRedigeringsomraade);


                Backupkyklop.hent().leggTilEntitetservice(kategoriService);
                Backupkyklop.hent().leggTilEntitetservice(maanedsoversiktService); //denne når alle, også årsoversikter
                Backupkyklop.hent().leggTilEntitetservice(maanedsoversiktpostService);  //denne når alle, også årsoversiktposter
                Backupkyklop.hent().leggTilEntitetservice(normalpostService);

                Allvitekyklop.hent().initierAlleObjekter();

                opprettLayout(Versjonskyklop.hent());
                leggTilBanner();

            }

            //TODO: Når er det behov for å hente redigeringsomraade fra serviceklassen? Sjekk havaara mot childTable

        });




    }

    private void leggTilBanner() {
        Image img = new Image("images/banner.jpeg", "Banner");
        img.setAlt("Banner");
        img.setWidth("1000px"); // optional
        img.setHeight("400px");
        hentBannerplassHorizontalLayout().add(img);
    }


}



