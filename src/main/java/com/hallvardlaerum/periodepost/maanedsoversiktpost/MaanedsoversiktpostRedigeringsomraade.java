package com.hallvardlaerum.periodepost.maanedsoversiktpost;

import com.hallvardlaerum.libs.ui.RedigerEntitetDialog;
import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import com.hallvardlaerum.periodepost.PeriodepostRedigeringsomraadeMal;
import com.hallvardlaerum.periodepost.PeriodepostTypeEnum;
import com.hallvardlaerum.post.budsjettpost.BudsjettpostRedigeringsomraade;
import com.hallvardlaerum.post.normalpost.NormalpostRedigeringsomraade;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@UIScope
public class MaanedsoversiktpostRedigeringsomraade extends PeriodepostRedigeringsomraadeMal implements InitieringsEgnet {
    private boolean erInitiert = false;




    public MaanedsoversiktpostRedigeringsomraade() {
        super();
    }

    @Override
    public boolean erInitiert() {
        return erInitiert;
    }

    public void init() {
        if (!erInitiert) {
            super.initierPeriodepostRedigeringsomraadeMal(
                    PeriodepostTypeEnum.MAANEDSOVERSIKTPOST,
                    Allvitekyklop.hent().getMaanedsoversiktService(),
                    PeriodetypeEnum.MAANEDSOVERSIKT
            );
            this.opprettRedigerEntitetDialoger();
            erInitiert = true;
        }
    }


    private void opprettRedigerEntitetDialoger() {
        NormalpostRedigeringsomraade normalpostRedigeringsomraadeTilDialog = new NormalpostRedigeringsomraade();
        normalpostRedigeringsomraadeTilDialog.init();

        BudsjettpostRedigeringsomraade budsjettpostRedigeringsomraadeTilDialog = new BudsjettpostRedigeringsomraade();
        budsjettpostRedigeringsomraadeTilDialog.init();

        normalPostRedigerEntitetDialog = new RedigerEntitetDialog<>(
                Allvitekyklop.hent().getNormalpostService(),
                Allvitekyklop.hent().getMaanedsoversiktpostService(),
                "Rediger normalpost",
                "Her kan du redigere innholdet i en vanlig post.",
                normalpostRedigeringsomraadeTilDialog,
                this
        );

        normalposterGrid.addItemDoubleClickListener(e -> {
            normalPostRedigerEntitetDialog.vis(e.getItem());
        });

        budsjettPostRedigerEntitetDialog = new RedigerEntitetDialog<>(
                Allvitekyklop.hent().getBudsjettpostService(),
                Allvitekyklop.hent().getMaanedsoversiktpostService(),
                "Rediger budsjettpost",
                "Her kan du redigere innholdet i en budsjettpost.",
                budsjettpostRedigeringsomraadeTilDialog,
                this
        );

        budsjettposterGrid.addItemDoubleClickListener(e -> {
            budsjettPostRedigerEntitetDialog.vis(e.getItem());
        });


    }
}
