package com.hallvardlaerum.post.normalpost;

import com.hallvardlaerum.libs.database.*;
import com.hallvardlaerum.libs.ui.ViewCRUDStatusEnum;
import com.hallvardlaerum.post.Post;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.post.PostServiceMal;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.data.provider.ListDataView;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.ArrayList;


public class NormaldelpostViewMester {
    private final NormalpostView normalpostView;
    private Post hovedPost;

    private Button leggTilNyDelPostButton;
    private Button lagreDelPostButton;
    private Button slettDelPostButton;
    private Button visDelposterButton;
    private Button settTilRestButton;
    private Boolean viserDelposterBoolean = false;
    private final PostServiceMal postService;
    private String originalvinduTittelString;
    private final NormalpostRedigeringsomraade redigeringsomraade;

    public NormaldelpostViewMester(NormalpostView normalpostView,
                                   NormalpostRedigeringsomraade redigeringsomraade,
                                   PostServiceMal postService) {
        this.normalpostView = normalpostView;
        this.postService = postService;
        this.redigeringsomraade = redigeringsomraade;
        opprettKnapperOgIntitierForDelposter();

    }


    private void opprettKnapperOgIntitierForDelposter() {
        visDelposterButton = new Button("Vis delposter");
        visDelposterButton.addClickListener(e -> skiftModus());
        visDelposterButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        leggTilNyDelPostButton = new Button("Legg til ny delpost");
        leggTilNyDelPostButton.addClickListener(e -> leggTilNyDelpost());
        leggTilNyDelPostButton.setVisible(false);

        lagreDelPostButton = new Button("Lagre delpost");
        lagreDelPostButton.addClickListener( e -> lagreDelpost());
        lagreDelPostButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        lagreDelPostButton.setVisible(false);

        slettDelPostButton = new Button("Slett delpost");
        slettDelPostButton.addClickListener(e -> slettDelpost());
        slettDelPostButton.setVisible(false);
        slettDelPostButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        settTilRestButton = new Button("Sett til rest");
        settTilRestButton.addClickListener(e -> settTilRest());
        settTilRestButton.setVisible(false);



        normalpostView.hentKnapperadRedigeringsfelt().add(visDelposterButton, leggTilNyDelPostButton, lagreDelPostButton, settTilRestButton, slettDelPostButton);

        originalvinduTittelString = normalpostView.hentVindutittel().getText();

    }

    private void slettDelpost() {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Slette " + postService.hentEntitetsnavn().toLowerCase() + "?");

        dialog.setText("Er du sikker på at du vil slette " + postService.hentVisningsnavn(normalpostView.hentEntitet()) + "?");
        dialog.addConfirmListener((e) -> {
            Grid<Post> grid = normalpostView.hentGrid();
            if (grid.getGenericDataView() instanceof ListDataView) {
                grid.getListDataView().removeItem(redigeringsomraade.hentEntitet());
            }

            postService.slett(redigeringsomraade.hentEntitet());
            redigeringsomraade.settEntitet(null);
            normalpostView.oppdaterRedigeringsomraade();
            normalpostView.setViewCRUDStatus(ViewCRUDStatusEnum.ER_SLETTET);
            settFilterForDelpost();

        });
        dialog.open();
    }

    private void lagreDelpost() {
        redigeringsomraade.skrivBean();
        postService.lagre(redigeringsomraade.hentEntitet());
        redigeringsomraade.lesBean();
        ViewCRUDStatusEnum viewCRUDStatus = normalpostView.getViewCRUDStatus();
        //normalpostView.oppdaterAntallRaderNederstIGrid();  // venter med denne

        if (viewCRUDStatus == ViewCRUDStatusEnum.NY) {
            settFilterForDelpost();
            normalpostView.markerEntitetiGrid();
        } else if (viewCRUDStatus == ViewCRUDStatusEnum.POST_KAN_REDIGERES ) {
            normalpostView.oppdaterMarkerteRadiGrid();
        }
        normalpostView.setViewCRUDStatus(ViewCRUDStatusEnum.POST_KAN_REDIGERES);
    }



    private void settFilterForDelpost(){
        ArrayList<SearchCriteria> filtre = new ArrayList<>();
        filtre.add(new SearchCriteria("uuid",":",hovedPost.getUuid()));
        filtre.add(new SearchCriteria("forelderPostUUID",":",hovedPost.getUuid().toString()));

        normalpostView.brukFiltreIDataprovider(filtre, EntityFilterSpecification.OperatorEnum.OR);
        oppdaterAntallRaderNederstIGrid();
    }

    private void oppdaterAntallRaderNederstIGrid() {
        Grid<Post> grid = normalpostView.hentGrid();
        Grid.Column<Post> column = grid.getColumns().getFirst();
        if (grid.getDataProvider() instanceof GridListDataView) {
            column.setFooter("Antall: " + grid.getListDataView().getItemCount());
        } else {
            int antallRader = postService.tellAntallMedSpecification();
            column.setFooter("Antall: " + antallRader);
        }

    }

    public void aktiverKnapperForEntity(Boolean blnAktiver){
        if(viserDelposterBoolean){
            settTilRestButton.setEnabled(blnAktiver);
            lagreDelPostButton.setEnabled(blnAktiver);

        }
    }


    private void skiftModus(){
        if (viserDelposterBoolean == false) { // Er normal, skift til delpostmodus
            if (!hentHovedpost()) {
                Loggekyklop.hent().loggFEIL("Ingen hovedpost funnet, går ikke i delpostmodus");
                return;
            }

            normalpostView.hentVindutittel().setText("Redigere delposter");
            normalpostView.hentVindutittel().setClassName(LumoUtility.TextColor.SUCCESS);
            settFilterForDelpost();

            viserDelposterBoolean = true;
            visDelposterButton.setText("Lukk delposter");

            leggTilNyDelPostButton.setVisible(true);
            lagreDelPostButton.setVisible(true);
            settTilRestButton.setVisible(true);
            slettDelPostButton.setVisible(true);

            normalpostView.hentNyButton().setVisible(false);
            normalpostView.hentLagreButton().setVisible(false);
            normalpostView.hentSlettButton().setVisible(false);

            normalpostView.hentRedigeringsomraadeAktig().setEntitet(null);
            normalpostView.hentRedigeringsomraadeAktig().lesBean();
            normalpostView.hentRedigeringsomraadeAktig().aktiver(false);


        } else {  // Er delpostmodus, skift tilbake til normal
            viserDelposterBoolean = false;
            visDelposterButton.setText("Vis delposter");
            leggTilNyDelPostButton.setVisible(false);
            lagreDelPostButton.setVisible(false);
            settTilRestButton.setVisible(false);
            slettDelPostButton.setVisible(false);

            normalpostView.hentNyButton().setVisible(true);
            normalpostView.hentLagreButton().setVisible(true);
            normalpostView.hentSlettButton().setVisible(true);
            normalpostView.settFilter();
            normalpostView.hentVindutittel().setText(originalvinduTittelString);
        }
    }

    private boolean hentHovedpost(){
        hovedPost = normalpostView.hentEntitet();
        if (hovedPost == null) {
            Loggekyklop.hent().loggFEIL("Ingen post markert, avbryter");
            return false;
        }
        if (hovedPost.getNormalPosttypeEnum()==NormalposttypeEnum.DELPOST) {
            hovedPost = postService.finnEtterUUID(hovedPost.getForelderPostUUID());
            if (hovedPost==null) {
                Loggekyklop.hent().loggFEIL("Fant ikke aktuell hovedpost, avbryter");
                return false;
            }
        }
        if (hovedPost.getNormalPosttypeEnum() != NormalposttypeEnum.UTELATES) {
            hovedPost.setNormalPosttypeEnum(NormalposttypeEnum.UTELATES);
            postService.lagre(hovedPost);
        }
        return true;
    }


    private void settTilRest() {
        Post delpost = normalpostView.hentEntitet();
        ArrayList<Post> delposter = new ArrayList<>(normalpostView.hentGrid().getGenericDataView().getItems().toList());

        Integer sumUtFraKonto = 0;
        for (Post post:delposter) {
            sumUtFraKonto = sumUtFraKonto + (post.getUtFraKontoInteger()!=null? post.getUtFraKontoInteger():0);
        }
        sumUtFraKonto = sumUtFraKonto - hovedPost.getUtFraKontoInteger() - (delpost.getUtFraKontoInteger()!=null?delpost.getUtFraKontoInteger():0);
        int restInteger = hovedPost.getUtFraKontoInteger() - sumUtFraKonto;
        if (restInteger>0) {
            redigeringsomraade.oppdaterUtFraKontoIntegerField(restInteger);
        }

        if (hovedPost.getInnPaaKontoInteger()!=null && hovedPost.getInnPaaKontoInteger()>0) {
            int sumInnPaaKonto = normalpostView.hentGrid().getGenericDataView().getItems().map(Post::getInnPaaKontoInteger).reduce(0,Integer::sum)
                    - hovedPost.getInnPaaKontoInteger() - hovedPost.getUtFraKontoInteger();
            if (sumInnPaaKonto<0) {
                redigeringsomraade.oppdaterInnPaaKontoIntegerField(-sumInnPaaKonto);
            }
        }

    }




    private void leggTilNyDelpost() {

        Post delPost = postService.opprettEntitet();
        delPost.setDatoLocalDate(hovedPost.getDatoLocalDate());
        delPost.setTekstFraBankenString(hovedPost.getTekstFraBankenString());
        delPost.setNormalPoststatusEnum(NormalpoststatusEnum.UBEHANDLET);
        delPost.setNormalPosttypeEnum(NormalposttypeEnum.DELPOST);
        delPost.setForelderPostUUID(hovedPost.getUuidString());
        normalpostView.hentRedigeringsomraadeAktig().setEntitet(delPost);
        postService.lagre(delPost);
        normalpostView.oppdaterRedigeringsomraade();
        normalpostView.setViewCRUDStatus(ViewCRUDStatusEnum.NY);


        //postView.oppdaterSoekeomraadeEtterRedigeringAvEntitet();


    }




}
