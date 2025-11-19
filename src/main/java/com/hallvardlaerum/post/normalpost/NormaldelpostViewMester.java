package com.hallvardlaerum.post.normalpost;

import com.hallvardlaerum.post.Post;
import com.hallvardlaerum.libs.database.EntityFilterSpecification;
import com.hallvardlaerum.libs.database.SearchCriteria;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.post.PostService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.ArrayList;


public class NormaldelpostViewMester {
    private NormalpostView normalpostView;
    private Post hovedPost;

    private Button leggTilNyDelPostButton;
    private Button visDelposterButton;
    private Button settTilRestButton;
    private Boolean viserDelposterBoolean = false;
    private PostService postService;
    private String originalvinduTittelString;

    public NormaldelpostViewMester(NormalpostView normalpostView, PostService postService) {
        this.normalpostView = normalpostView;
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



        normalpostView.hentKnapperadRedigeringsfelt().add(visDelposterButton, leggTilNyDelPostButton, settTilRestButton);

        originalvinduTittelString = normalpostView.hentVindutittel().getText();

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
            normalpostView.hentNyButton().setVisible(false);

            normalpostView.hentVindutittel().setText("Redigere delposter");
            normalpostView.hentVindutittel().setClassName(LumoUtility.TextColor.SUCCESS);
            settFilterForDelpost();

            normalpostView.hentRedigeringsomraadeAktig().setEntitet(null);
            normalpostView.hentRedigeringsomraadeAktig().lesBean();
            normalpostView.hentRedigeringsomraadeAktig().aktiver(false);


        } else {  // Er delpostmodus, skift tilbake til normal
            viserDelposterBoolean = false;
            visDelposterButton.setText("Vis delposter");
            leggTilNyDelPostButton.setVisible(false);
            settTilRestButton.setVisible(false);
            normalpostView.hentNyButton().setVisible(true);
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
            sumUtFraKonto = sumUtFraKonto + post.getUtFraKontoInteger();
        }
        sumUtFraKonto = sumUtFraKonto - hovedPost.getUtFraKontoInteger() - delpost.getUtFraKontoInteger();
        Integer restInteger = hovedPost.getUtFraKontoInteger() - sumUtFraKonto;
        if (restInteger>0) {
            delpost.setUtFraKontoInteger(restInteger);
        }

        if (hovedPost.getInnPaaKontoInteger()!=null && hovedPost.getInnPaaKontoInteger()>0) {
            Integer sumInnPaaKonto = normalpostView.hentGrid().getGenericDataView().getItems().map(Post::getInnPaaKontoInteger).reduce(0,Integer::sum)
                    - hovedPost.getInnPaaKontoInteger() - hovedPost.getUtFraKontoInteger();
            if (sumInnPaaKonto<0) {
                delpost.setInnPaaKontoInteger(-sumInnPaaKonto);
            }
        }
        normalpostView.hentRedigeringsomraadeAktig().lesBean();

    }



    private void settFilterForDelpost(){
        ArrayList<SearchCriteria> filtre = new ArrayList<>();
        filtre.add(new SearchCriteria("uuid",":",hovedPost.getUuid()));
        filtre.add(new SearchCriteria("forelderPostUUID",":",hovedPost.getUuid().toString()));

        normalpostView.brukFiltreIDataprovider(filtre, EntityFilterSpecification.OperatorEnum.OR);
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
        //postView.oppdaterSoekeomraadeEtterRedigeringAvEntitet();
    }


}
