package com.hallvardlaerum.periode.maanedsoversikt;

import com.hallvardlaerum.libs.eksportimport.CSVImportmester;
import com.hallvardlaerum.libs.ui.RedigerEntitetDialog;
import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.periode.Periode;
import com.hallvardlaerum.periode.PeriodeViewMal;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;


@Route("maanedsoversikt")
@UIScope
//@Menu(order=20, title="Månedsoversikt")
public class MaanedsoversiktView extends PeriodeViewMal implements InitieringsEgnet {
    private MaanedsoversiktService maanedsoversiktService;
    private boolean erInitiert = false;
    private RedigerEntitetDialog<Periode, Periode> redigerMaanedsoversiktDialog;

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
            Allvitekyklop.hent().getMaanedsoversiktRedigeringsomraade().settView(this);

            maanedsoversiktService = Allvitekyklop.hent().getMaanedsoversiktService();
            super.initPeriodeViewMal(
                    PeriodetypeEnum.MAANEDSOVERSIKT,
                    this,
                    maanedsoversiktService,
                    Allvitekyklop.hent().getMaanedsoversiktRedigeringsomraade(),
                    40D
                    );

            super.tilpassKnapperadRedigeringsfelt();
            //Har overkjørt super.opprettSoekeomraade() og tilpasser verktøymenyen der.

            MaanedsoversiktRedigeringsomraade maanedsoversiktRedigeringsomraadeTilDialog = new MaanedsoversiktRedigeringsomraade();
            maanedsoversiktRedigeringsomraadeTilDialog.init();
            redigerMaanedsoversiktDialog = new RedigerEntitetDialog<>(
                    Allvitekyklop.hent().getMaanedsoversiktService(),
                    Allvitekyklop.hent().getMaanedsoversiktService(),
                    "Redigere månedsoversikt",
                    "",
                    maanedsoversiktRedigeringsomraadeTilDialog,
                    Allvitekyklop.hent().getMaanedsoversiktRedigeringsomraade()
            );
            super.hentGrid().addItemDoubleClickListener(e -> redigerMaanedsoversiktDialog.vis(e.getItem()));

            erInitiert = true;
        }
    }

    private void opprettSoekeomraade_leggTilVerktoyMeny_opprettImporterCSVFraBlaahvalenMenuItem() {
        hentVerktoeySubMeny().addItem("Importer CSV fra gamle Blåhvalen", e -> importerCSVFraGamleBlaahvalen());

    }

    @Override
    protected VerticalLayout opprettSoekeomraade(){
        super.opprettSoekeomraade_leggTilTittel();
        super.opprettSoekeomraade_leggTilVerktoyMeny();

        super.opprettSoekeomraade_leggTilVerktoyMeny_opprettEksporterTilCSVMenuItem();
        super.opprettSoekeomraade_leggTilVerktoyMeny_opprettImporterFraCSVMenuItem();
        opprettSoekeomraade_leggTilVerktoyMeny_opprettImporterCSVFraBlaahvalenMenuItem();
        opprettSoekeomraade_leggTilVerktoyMeny_opprettMaanedsoversikter();
        opprettSoekeomraade_leggTilVerktoyMeny_opprettSlettAlleMaanedsoversikterMenuItem();

        super.opprettSoekeomraade_leggTilVerktoyMeny_opprettSeparator();
        super.opprettSoekeomraade_leggTilVerktoyMeny_byttOrienteringAvSplitLayoutMenuItem();
        super.opprettSoekeomraade_leggTilSoekeGrid();
        return super.opprettSoeomraade_settSammenDetHele();
    }

    private void opprettSoekeomraade_leggTilVerktoyMeny_opprettMaanedsoversikter() {
        hentVerktoeySubMeny().addItem("Opprett månedsoversikter", e -> maanedsoversiktService.opprettMaanedsoversikterForHeleAaret());
    }


    private void opprettSoekeomraade_leggTilVerktoyMeny_opprettSlettAlleMaanedsoversikterMenuItem() {
        slettAlleMenuItem = super.verktoeySubMenu.addItem("Slett alle månedsoversikter");
        slettAlleMenuItem.addClickListener(e -> new ConfirmDialog(
                "Slette alle månedsoversikter?",
                "Vil du virkelig slette månedsoversikter med månedsoversiktposter?",
                "Ja, sett i gang",
                ee -> {
                    maanedsoversiktService.slettAlleMaanedsoversikter();
                    oppdaterSoekeomraadeFinnAlleRader();
                    oppdaterRedigeringsomraade();
                },
                "Nei, er du GAL!",
                null).open());
    }



    private void importerCSVFraGamleBlaahvalen() {
        new CSVImportmester(new MaanedsoversiktFraGamleBlaahvalenCSVImportassistent()).velgImportfilOgKjoerImport(maanedsoversiktService);
    }


    @Override
    public void instansAktiverKnapperadRedigeringsfelt(Boolean aktiverBoolean) {
        super.instansAktiverKnapperadRedigeringsfelt(aktiverBoolean);
        super.lastNedPDFAnchor.setEnabled(aktiverBoolean);
        super.oppdaterSummerOgPeriodeposterButton.setEnabled(aktiverBoolean);
    }

}
