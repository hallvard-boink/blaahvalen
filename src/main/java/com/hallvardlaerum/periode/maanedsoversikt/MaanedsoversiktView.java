package com.hallvardlaerum.periode.maanedsoversikt;

import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.periode.*;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.hallvardlaerum.verktoy.RapportMester;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;


@Route("maanedsoversikt")
@UIScope
//@Menu(order=20, title="Månedsoversikt")
public class MaanedsoversiktView extends PeriodeViewMal implements InitieringsEgnet {
    private MaanedsoversiktService maanedsoversiktService;
    private MaanedsoversiktRedigeringsomraade maanedsoversiktRedigeringsomraade;
    private Button oppdaterSummerButton;
    private Button skrivUtMaanedsoversiktButton;
    private boolean erInitiert = false;

    public MaanedsoversiktView() {
        super();
        Allvitekyklop.hent().setMaanedsoversiktView(this);
        init();
    }

    @Override
    public boolean erInitiert() {
        return erInitiert;
    }

    @Override
    public void init(){
        if (!erInitiert) {
            this.maanedsoversiktService = Allvitekyklop.hent().getMaanedsoversiktService();
            this.maanedsoversiktRedigeringsomraade = Allvitekyklop.hent().getMaanedsoversiktRedigeringsomraade();
            this.maanedsoversiktRedigeringsomraade.settView(this);
            Allvitekyklop.hent().getMaanedsoversiktpostRedigeringsomraadeTilDialog().settView(this);
            super.initPeriodeViewMal(PeriodetypeEnum.MAANEDSOVERSIKT,
                    this,
                    maanedsoversiktService,
                    maanedsoversiktRedigeringsomraade
                    );
            leggTilOgTilpassKnapper();

            erInitiert = true;
        }
    }


    private void leggTilOgTilpassKnapper() {
        oppdaterSummerButton = new Button("Oppdater månedsoversikt");
        oppdaterSummerButton.addClickListener(e -> maanedsoversiktService.oppdaterPeriodensPeriodeposterOgSummer());
        oppdaterSummerButton.setEnabled(false);
        hentKnapperadRedigeringsfelt().addToEnd(oppdaterSummerButton);

        skrivUtMaanedsoversiktButton = new Button("Lagre PDF");
        skrivUtMaanedsoversiktButton.addClickListener(e -> {
            Periode periode = (Periode)hentRedigeringsomraadeAktig().getEntitet();
            PeriodeRedigeringsomraadeMal redigeringsomraade = (PeriodeRedigeringsomraadeMal)hentRedigeringsomraadeAktig();
            new RapportMester().lagrePeriodeSomPDF(periode, redigeringsomraade.hentPeriodepostListSortert(periode));
        });
        skrivUtMaanedsoversiktButton.setEnabled(false);
        hentKnapperadRedigeringsfelt().addToEnd(skrivUtMaanedsoversiktButton);

        hentVerktoeySubMeny().addItem("Opprett månedsoversikter",
                e -> maanedsoversiktService.opprettMaanedsoversikterForHeleAaret()
        );


    }


    @Override
    public void instansAktiverKnapperadRedigeringsfelt(Boolean aktiverBoolean) {
        super.instansAktiverKnapperadRedigeringsfelt(aktiverBoolean);
        skrivUtMaanedsoversiktButton.setEnabled(aktiverBoolean);
        oppdaterSummerButton.setEnabled(aktiverBoolean);
    }

}
