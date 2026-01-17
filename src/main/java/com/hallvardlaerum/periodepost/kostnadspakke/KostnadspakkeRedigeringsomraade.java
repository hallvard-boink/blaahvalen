package com.hallvardlaerum.periodepost.kostnadspakke;

import com.hallvardlaerum.libs.ui.RedigerEntitetDialog;
import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import com.hallvardlaerum.periodepost.Periodepost;
import com.hallvardlaerum.periodepost.PeriodepostRedigeringsomraadeMal;
import com.hallvardlaerum.periodepost.PeriodepostTypeEnum;
import com.hallvardlaerum.post.Post;
import com.hallvardlaerum.post.budsjettpost.BudsjettpostRedigeringsomraade;
import com.hallvardlaerum.post.normalpost.NormalpostRedigeringsomraade;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@UIScope
public class KostnadspakkeRedigeringsomraade extends PeriodepostRedigeringsomraadeMal implements InitieringsEgnet {
    private boolean erInitiert = false;
    private RedigerEntitetDialog<Post,Periodepost> normalpostRedigerEntitetDialog;


    @Override
    public void init() {
        if (!erInitiert) {
            super.initierPeriodepostRedigeringsomraadeMal(
                    PeriodepostTypeEnum.PERIODEOVERSIKTPOST,
                    Allvitekyklop.hent().getAarsoversiktService(),
                    PeriodetypeEnum.AARSOVERSIKT
            );

            erInitiert = true;
        }
    }

    @Override
    public void instansOppdaterEkstraRedigeringsfelter() {
        Periodepost periodepost = hentEntitet();
        sumRegnskapSpan.settInteger(periodepost.getSumRegnskapInteger());
        List<Post> normalposterList = Allvitekyklop.hent().getNormalpostService().finnPosterIKostnadspakken(periodepost);
        normalposterGrid.setItems(normalposterList);
    }



    @Override
    public void instansOpprettFelter() {
        super.instansOpprettFelter_opprettFellesFelter();
        instansOpprettFelter_leggTilOverfelter_Kostnadspakke();
        super.instansOpprettFelter_opprettNormalposterTab();
        instansOpprettFelter_opprettEkstraTab_Kostnadspakke();

        instansOpprettFelter_leggTilDialoger();

        settFokusKomponent(tittelTextField);

    }

    private void instansOpprettFelter_leggTilDialoger() {
        NormalpostRedigeringsomraade normalpostRedigeringsomraadeTilDialog = new NormalpostRedigeringsomraade();
        normalpostRedigeringsomraadeTilDialog.init();
        normalpostRedigerEntitetDialog = new RedigerEntitetDialog<>(
                Allvitekyklop.hent().getNormalpostService(),
                Allvitekyklop.hent().getKostnadspakkeService(),
                "Redigere normalpost",
                "",
                normalpostRedigeringsomraadeTilDialog,
                this
        );
        super.normalposterGrid.addItemDoubleClickListener(e -> normalpostRedigerEntitetDialog.vis(e.getItem()));



    }

    private void instansOpprettFelter_opprettEkstraTab_Kostnadspakke() {
        leggTilRedigeringsfelter(ekstratabString, periodeposttypeEnumComboBox, periodeComboBox);
    }

    protected void instansOpprettFelter_leggTilOverfelter_Kostnadspakke() {
        VerticalLayout bakgrunnVerticalLayout = new VerticalLayout();
        bakgrunnVerticalLayout.setSizeFull();
        HorizontalLayout rad1HorizontalLayout = new HorizontalLayout();
        rad1HorizontalLayout.setWidthFull();
        rad1HorizontalLayout.setDefaultVerticalComponentAlignment(Alignment.END);

        tittelTextField.setWidthFull();
        kategoriComboBox.setWidth("400px");
        sumRegnskapLabelSpan.setWidth("200px");
        sumRegnskapSpan.setWidth("200px");
        rad1HorizontalLayout.add(kategoriComboBox, tittelTextField, sumRegnskapLabelSpan, sumRegnskapSpan);

        beskrivelseTextArea.setLabel("Beskrivelse");

        bakgrunnVerticalLayout.add(rad1HorizontalLayout);
        bakgrunnVerticalLayout.add(beskrivelseTextArea);

        leggTilAndrefelterOver(bakgrunnVerticalLayout);

    }

    public KostnadspakkeRedigeringsomraade() {
        super();
    }

    @Override
    public boolean erInitiert() {
        return erInitiert;
    }
}
