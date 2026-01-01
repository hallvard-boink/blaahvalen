package com.hallvardlaerum.periode.maanedsoversikt;

import com.hallvardlaerum.libs.eksportimport.CSVImportmester;
import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.periode.*;
import com.hallvardlaerum.periodepost.PeriodepostServiceMal;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.hallvardlaerum.verktoy.PeriodeRapportMester;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;

import java.util.ArrayList;


@Route("maanedsoversikt")
@UIScope
//@Menu(order=20, title="Månedsoversikt")
public class MaanedsoversiktView extends PeriodeViewMal implements InitieringsEgnet {
    private MaanedsoversiktService maanedsoversiktService;
    private MaanedsoversiktRedigeringsomraade maanedsoversiktRedigeringsomraade;
    private Button oppdaterSummerButton;
    private Button oppdaterSummerOgPeriodeposterButton;
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
                    maanedsoversiktRedigeringsomraade,
                    40D
                    );
            leggTilOgTilpassKnapper();
            hentVerktoeySubMeny().addItem("Importer CSV fra gamle Blåhvalen", e -> importerCSVFraGamleBlaahvalen());


            erInitiert = true;
        }
    }

    private void importerCSVFraGamleBlaahvalen() {
        new CSVImportmester(new MaanedsoversiktFraGamleBlaahvalenCSVImportassistent()).velgImportfilOgKjoerImport(maanedsoversiktService);
    }

    private void leggTilOgTilpassKnapper() {
        oppdaterSummerOgPeriodeposterButton = new Button("Oppdater summer");
        oppdaterSummerOgPeriodeposterButton.addClickListener(e -> maanedsoversiktService.oppdaterDetaljertPeriodensPeriodeposterOgSummer());
        oppdaterSummerOgPeriodeposterButton.setEnabled(false);
        hentKnapperadRedigeringsfelt().addToEnd(oppdaterSummerOgPeriodeposterButton);

        skrivUtMaanedsoversiktButton = new Button("Lagre PDF");
        skrivUtMaanedsoversiktButton.addClickListener(e -> {
            Periode periode = (Periode)hentRedigeringsomraadeAktig().getEntitet();
            PeriodeRedigeringsomraadeMal redigeringsomraade = (PeriodeRedigeringsomraadeMal)hentRedigeringsomraadeAktig();
            PeriodepostServiceMal periodeservice = Allvitekyklop.hent().getMaanedsoversiktpostService();
            new PeriodeRapportMester().lagrePeriodeSomPDF(periode, new ArrayList<>(periodeservice.finnHovedperiodeposter(periode)));

        });
        skrivUtMaanedsoversiktButton.setEnabled(false);
        hentKnapperadRedigeringsfelt().addToEnd(skrivUtMaanedsoversiktButton);
        hentVerktoeySubMeny().addItem("Opprett månedsoversikter", e -> maanedsoversiktService.opprettMaanedsoversikterForHeleAaret());

    }


    @Override
    public void instansAktiverKnapperadRedigeringsfelt(Boolean aktiverBoolean) {
        super.instansAktiverKnapperadRedigeringsfelt(aktiverBoolean);
        skrivUtMaanedsoversiktButton.setEnabled(aktiverBoolean);
        oppdaterSummerOgPeriodeposterButton.setEnabled(aktiverBoolean);
    }

}
