package com.hallvardlaerum.regnskap.ui;

import com.hallvardlaerum.felles.Post;
import com.hallvardlaerum.libs.database.EntityFilterSpecification;
import com.hallvardlaerum.libs.database.SearchCriteria;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.ui.Ikonkyklop;
import com.hallvardlaerum.regnskap.data.NormalpoststatusEnum;
import com.hallvardlaerum.regnskap.data.NormalposttypeEnum;
import com.hallvardlaerum.regnskap.service.PostService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class PostViewDelpostMester {
    private PostView postView;
    private Post hovedPost;

    private Button leggTilNyDelPostButton;
    private Button visDelposterButton;
    private Button settTilRestButton;
    private Boolean viserDelposterBoolean = false;
    private PostService postService;
    private String originalvinduTittelString;

    public PostViewDelpostMester(PostView postView, PostService postService) {
        this.postView = postView;
        this.postService = postService;
        opprettKnapperOgIntitierForDelposter();

    }


    private void opprettKnapperOgIntitierForDelposter() {
        visDelposterButton = new Button("Vis delposter");
        visDelposterButton.addClickListener(e -> skiftModus());
        visDelposterButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        leggTilNyDelPostButton = new Button("Legg til ny delpost");
        leggTilNyDelPostButton.addClickListener(e -> leggTilNyDelpost());
        leggTilNyDelPostButton.setVisible(false);



        settTilRestButton = new Button("Sett til rest");
        settTilRestButton.addClickListener(e -> settTilRest());
        settTilRestButton.setVisible(false);



        postView.hentKnapperadRedigeringsfelt().add(visDelposterButton, leggTilNyDelPostButton, settTilRestButton);

        originalvinduTittelString = postView.hentVindutittel().getText();

    }

    public void aktiverKnapperForEntity(Boolean blnAktiver){
        if(viserDelposterBoolean){
            settTilRestButton.setEnabled(blnAktiver);
        }
    }


    private void skiftModus(){
        if (viserDelposterBoolean == false) { // Er normal, skift til delpostmodus
            if (!hentHovedpost()) {
                Loggekyklop.hent().loggFEIL("Ingen hovedpost funnet, går ikke i delpostmodus");
                return;
            }

            viserDelposterBoolean = true;
            visDelposterButton.setText("Lukk delposter");
            leggTilNyDelPostButton.setVisible(true);
            postView.hentNyButton().setVisible(false);

            postView.hentVindutittel().setText("Redigere delposter");
            postView.hentVindutittel().setClassName(LumoUtility.TextColor.SUCCESS);
            settFilterForDelpost();

            postView.hentRedigeringsomraadeAktig().setEntitet(null);
            postView.hentRedigeringsomraadeAktig().lesBean();
            postView.hentRedigeringsomraadeAktig().aktiver(false);


        } else {  // Er delpostmodus, skift tilbake til normal
            viserDelposterBoolean = false;
            visDelposterButton.setText("Vis delposter");
            leggTilNyDelPostButton.setVisible(false);
            settTilRestButton.setVisible(false);
            postView.hentNyButton().setVisible(true);
            postView.settFilter();
            postView.hentVindutittel().setText(originalvinduTittelString);
        }
    }

    private boolean hentHovedpost(){
        hovedPost = postView.hentEntitet();
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
        Post delpost = postView.hentEntitet();
        ArrayList<Post> delposter = new ArrayList<>(postView.hentGrid().getGenericDataView().getItems().toList());

        Integer sumUtFraKonto = 0;
        for (Post post:delposter) {
            sumUtFraKonto = sumUtFraKonto + post.getUtFraKontoInteger();
        }
        sumUtFraKonto = sumUtFraKonto - hovedPost.getUtFraKontoInteger() - delpost.getUtFraKontoInteger();
        Integer restInteger = hovedPost.getUtFraKontoInteger() - sumUtFraKonto;
        if (restInteger>0) {
            delpost.setUtFraKontoInteger(restInteger);
        }

        if (hovedPost.getInnPaaKontoInteger()!=null && hovedPost.getInnPaaKontoInteger()>0) {
            Integer sumInnPaaKonto = postView.hentGrid().getGenericDataView().getItems().map(Post::getInnPaaKontoInteger).reduce(0,Integer::sum)
                    - hovedPost.getInnPaaKontoInteger() - hovedPost.getUtFraKontoInteger();
            if (sumInnPaaKonto<0) {
                delpost.setInnPaaKontoInteger(-sumInnPaaKonto);
            }
        }
        postView.hentRedigeringsomraadeAktig().lesBean();

    }



    private void settFilterForDelpost(){
        ArrayList<SearchCriteria> filtre = new ArrayList<>();
        filtre.add(new SearchCriteria("uuid",":",hovedPost.getUuid()));
        filtre.add(new SearchCriteria("forelderPostUUID",":",hovedPost.getUuid().toString()));

        postView.brukFiltreIDataprovider(filtre, EntityFilterSpecification.OperatorEnum.OR);
    }

    private void leggTilNyDelpost() {
        Post delPost = postService.opprettEntitet();
        delPost.setDatoLocalDate(hovedPost.getDatoLocalDate());
        delPost.setTekstFraBankenString(hovedPost.getTekstFraBankenString());
        delPost.setNormalPoststatusEnum(NormalpoststatusEnum.UBEHANDLET);
        delPost.setNormalPosttypeEnum(NormalposttypeEnum.DELPOST);
        delPost.setForelderPostUUID(hovedPost.getUuidString());
        postView.hentRedigeringsomraadeAktig().setEntitet(delPost);
        postService.lagre(delPost);
        postView.oppdaterRedigeringsomraade();
        //postView.oppdaterSoekeomraadeEtterRedigeringAvEntitet();
    }


}
