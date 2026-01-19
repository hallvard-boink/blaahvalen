package com.hallvardlaerum.post.normalpost;


import com.hallvardlaerum.kategori.Kategori;
import com.hallvardlaerum.kategori.KategoriService;
import com.hallvardlaerum.libs.ui.RedigerEntitetDialogEgnet;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeAktig;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeMal;

import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.periodepost.Periodepost;
import com.hallvardlaerum.periodepost.kostnadspakke.KostnadspakkeService;
import com.hallvardlaerum.post.Post;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@UIScope
public class NormalpostRedigeringsomraade extends RedigeringsomraadeMal<Post>
        implements RedigeringsomraadeAktig<Post>, InitieringsEgnet, RedigerEntitetDialogEgnet<Periodepost> {
    private boolean erInitiert = false;
    private KostnadspakkeService kostnadspakkeService;


    private String hovedtabString= "Hoved";
    private String ekstratabString = "Ekstra";

    private DatePicker datoDatePicker;
    private TextField tekstFraBankenTextField;
    private TextField egenbeskrivelseTextField;
    private IntegerField innPaaKontoIntegerField;
    private IntegerField utFraKontoIntegerField;
    private ComboBox<NormalposttypeEnum> normalposttypeComboBox;
    private ComboBox<NormalpoststatusEnum> normalpoststatusComboBox;
    private ComboBox<Kategori> kategoriComboBox;
    private ComboBox<Kategori> kategoriDetaljComboBox;
    private KategoriService kategoriService;
    private TextArea ekstraInfoTextArea;
    private TextField uuidTextField;
    private TextField forelderPostUUID;
    private ComboBox<Periodepost> kostnadspakkeComboBox;
    private KostnadspakkeMester kostnadspakkeMester;
    private HorizontalLayout kostnadspakkeHaandteringHorizontalLayout;

    public NormalpostRedigeringsomraade() {
        super();
    }

    @Override
    public void init() {
        if (!erInitiert) {
            super.initRedigeringsomraadeMal();
            this.kategoriService = Allvitekyklop.hent().getKategoriService();
            kostnadspakkeService = Allvitekyklop.hent().getKostnadspakkeService();
            kostnadspakkeMester = new KostnadspakkeMester();

            instansOpprettFelter();
            instansByggOppBinder();

            erInitiert = true;
        }
    }

    @Override
    public boolean erInitiert() {
        return erInitiert;
    }

    public void oppdaterInnPaaKontoIntegerField(Integer innInteger) {
        innPaaKontoIntegerField.setValue(innInteger);
    }

    public void oppdaterUtFraKontoIntegerField(Integer utInteger) {
        utFraKontoIntegerField.setValue(utInteger);
    }

    @Override
    public void aktiver(Boolean blnAktiver) {
        super.aktiver(blnAktiver);
        if (Allvitekyklop.hent().getNormalpostView()!=null) {
            Allvitekyklop.hent().getNormalpostView().aktiverDelpostknapperHvisAktuelt(blnAktiver);
        }
    }

    @Override
    public void instansOppdaterEkstraRedigeringsfelter() {
        if (hentEntitet()!=null) {
            Kategori kategori = hentEntitet().getKategori();
            kategoriDetaljCombobox_OppdaterUtvalgOgSettTilOppsummerendeUnderkategori(kategori);

        }
    }



    @Override
    public void instansOpprettFelter() {

        instansOpprettFelter_opprettOgLeggTilHovedfelter(hovedtabString);

        instansOpprettFelter_opprettKategoriCombobox();
        instansOpprettFelter_opprettKategoriDetaljComboBox();
        instansOpprettFelter_opprettKostnadspakkeHaandteringHorizontallLayout();
        super.leggTilRedigeringsfelter(hovedtabString, kategoriComboBox, kategoriDetaljComboBox, kostnadspakkeHaandteringHorizontalLayout);

        instansOpprettFelter_opprettEkstraTabMedFelter(ekstratabString);

        settFokusKomponent(egenbeskrivelseTextField);
    }



    private void instansOpprettFelter_opprettOgLeggTilHovedfelter(String hovedtabString) {
        tekstFraBankenTextField = super.leggTilRedigeringsfelt(hovedtabString, new TextField("Tekst fra banken"));
        settColspan(tekstFraBankenTextField,3);

        datoDatePicker = new DatePicker("Dato");
        innPaaKontoIntegerField = new IntegerField("Inn på konto");
        utFraKontoIntegerField = new IntegerField("Ut fra konto");
        super.leggTilRedigeringsfelter(hovedtabString, datoDatePicker, innPaaKontoIntegerField, utFraKontoIntegerField);

        egenbeskrivelseTextField = super.leggTilRedigeringsfelt(hovedtabString, new TextField("Egen beskrivelse"));
        settColspan(egenbeskrivelseTextField,3);
    }

    private void instansOpprettFelter_opprettKategoriCombobox() {
        kategoriComboBox = new ComboBox<>("Kategori");
        kategoriComboBox.setItems(kategoriService.finnAlleOppsummerendeUnderkategorier());
        kategoriComboBox.setItemLabelGenerator(Kategori::getTittel);
        kategoriComboBox.addValueChangeListener(event -> {
            if (event != null && hentEntitet()!=null) {
                if (event.isFromClient()) {
                    kategoriDetaljCombobox_OppdaterUtvalgOgSettTilOppsummerendeUnderkategori(event.getValue());
                    kategoriDetaljComboBox.setValue(kategoriComboBox.getValue());
                    setEventueltNormalpoststatusEnumTilFERDIG();
                }
            }
        });
    }

    private void setEventueltNormalpoststatusEnumTilFERDIG(){
        if (normalpoststatusComboBox.getValue()==NormalpoststatusEnum.UBEHANDLET) {
            normalpoststatusComboBox.setValue(NormalpoststatusEnum.FERDIG);
        }
    }

    private void instansOpprettFelter_opprettKategoriDetaljComboBox() {
        kategoriDetaljComboBox = new ComboBox<>("Underkategori");
        kategoriDetaljComboBox.setItems(kategoriService.finnAlleUnderkategorier());
        kategoriDetaljComboBox.setItemLabelGenerator(Kategori::getUndertittel);
        kategoriDetaljComboBox.addValueChangeListener(event -> {
           if (event != null && hentEntitet()!=null & event.isFromClient())  {
               setEventueltNormalpoststatusEnumTilFERDIG();
               kategoriComboBox.setValue(kategoriDetaljComboBox.getValue());
           }

        });
    }

    private void instansOpprettFelter_opprettKostnadspakkeHaandteringHorizontallLayout() {
        kostnadspakkeComboBox = new ComboBox<>("Kostnadspakke");
        kostnadspakkeComboBox.setItemLabelGenerator(Periodepost::hentKortnavn);
        kostnadspakkeComboBox.setItems(kostnadspakkeService.finnAlleKostnadspakker());
        kostnadspakkeComboBox.addValueChangeListener(event -> {
            if (event != null && hentEntitet() != null && event.isFromClient() && event.getValue()!=null) {
                Kategori kostnadspakkensKategori =event.getValue().getKategori();
                if (kostnadspakkensKategori!=null) {
                    kategoriComboBox.setValue(kostnadspakkensKategori);
                    kategoriDetaljComboBox.setValue(kostnadspakkensKategori);
                    setEventueltNormalpoststatusEnumTilFERDIG();
                }
                kostnadspakkeMester.oppdaterBenyttedeKostnadspakker(event.getValue());
            }
        });
        kostnadspakkeComboBox.setWidthFull();

        kostnadspakkeHaandteringHorizontalLayout = new HorizontalLayout();
        kostnadspakkeHaandteringHorizontalLayout.setDefaultVerticalComponentAlignment(Alignment.END);
        kostnadspakkeHaandteringHorizontalLayout.setWidthFull();

        Button brukSisteKostnadspakkeButton = new Button(new Icon(VaadinIcon.CARET_LEFT));
        brukSisteKostnadspakkeButton.setTooltipText("Benytt først brukte kostnadspakke");
        brukSisteKostnadspakkeButton.addClickListener(e -> {
            brukKostnadspakke(kostnadspakkeMester.hentSistBenyttedeKostnadspakke());
        });

        Button brukNestsisteKostnadspakkeButton = new Button(new Icon(VaadinIcon.BACKWARDS));
        brukNestsisteKostnadspakkeButton.setTooltipText("Benytt nest først brukte kostnadspakke");
        brukNestsisteKostnadspakkeButton.addClickListener(e -> {
            brukKostnadspakke(kostnadspakkeMester.hentNestsisteBenyttedeKostnadspakke());
        });

        Button leggTilNyKostnadspakkeButton = new Button(new Icon(VaadinIcon.PLUS));
        leggTilNyKostnadspakkeButton.setTooltipText("Legg til ny kostnadspakke");
        leggTilNyKostnadspakkeButton.addClickListener(e -> leggTilNyKostnadspakke());

        kostnadspakkeHaandteringHorizontalLayout.add(kostnadspakkeComboBox, brukSisteKostnadspakkeButton,brukNestsisteKostnadspakkeButton, leggTilNyKostnadspakkeButton);

    }

    private void leggTilNyKostnadspakke() {
        Allvitekyklop.hent().getNormalpostView().lagreEntitet();
        kostnadspakkeMester.leggTilNyKostnadspakke(hentEntitet());
    }


    private void brukKostnadspakke(Periodepost kostnadspakke){
        if (kostnadspakke!=null) {
            kostnadspakkeComboBox.setValue(kostnadspakke);
            Kategori kostnadspakkenskategori = kostnadspakke.getKategori();
            if (kostnadspakkenskategori != null) {
                kategoriComboBox.setValue(kostnadspakkenskategori);
                kategoriDetaljComboBox.setValue(kostnadspakkenskategori);
                setEventueltNormalpoststatusEnumTilFERDIG();
            }
        }
    }

    private void instansOpprettFelter_opprettEkstraTabMedFelter(String ekstratabString) {
        normalposttypeComboBox = new ComboBox<>("Posttype");
        normalposttypeComboBox.setItemLabelGenerator(NormalposttypeEnum::getTittel);
        normalposttypeComboBox.setItems(NormalposttypeEnum.values());

        normalpoststatusComboBox = new ComboBox<>("Poststatus");
        normalpoststatusComboBox.setItemLabelGenerator(NormalpoststatusEnum::getTittel);
        normalpoststatusComboBox.setItems(NormalpoststatusEnum.values());

        super.leggTilRedigeringsfelter(ekstratabString, normalposttypeComboBox, normalpoststatusComboBox);

        ekstraInfoTextArea = super.leggTilRedigeringsfelt(ekstratabString, new TextArea("Ekstra info"));
        settColspan(ekstraInfoTextArea,2);
        uuidTextField = super.leggTilRedigeringsfelt(ekstratabString, new TextField("UUID"));
        forelderPostUUID = super.leggTilRedigeringsfelt(ekstratabString, new TextField("ForelderpostUUID"));

        super.leggTilDatofeltTidOpprettetOgRedigert(ekstratabString);
    }

    private void kategoriDetaljCombobox_OppdaterUtvalgOgSettTilOppsummerendeUnderkategori(Kategori oppsummerendeunderkategori) {
        ArrayList<Kategori> underkategorierList = new ArrayList<>();
        if (oppsummerendeunderkategori!=null) {
            underkategorierList = new ArrayList<>(kategoriService.finnUnderkategorier(oppsummerendeunderkategori.getTittel()));
        }
        kategoriDetaljComboBox.setItems(underkategorierList);
        kategoriDetaljComboBox.setValue(oppsummerendeunderkategori);
    }

    @Override
    public void instansByggOppBinder() {
        Binder<Post> binder = hentBinder();
        binder.bind(datoDatePicker, Post::getDatoLocalDate, Post::setDatoLocalDate);
        binder.bind(tekstFraBankenTextField, Post::getTekstFraBankenString, Post::setTekstFraBankenString);
        binder.bind(egenbeskrivelseTextField, Post::getBeskrivelseString, Post::setBeskrivelseString);
        binder.bind(innPaaKontoIntegerField, Post::getInnPaaKontoInteger, Post::setInnPaaKontoInteger);
        binder.bind(utFraKontoIntegerField, Post::getUtFraKontoInteger, Post::setUtFraKontoInteger);
        binder.bind(normalposttypeComboBox, Post::getNormalPosttypeEnum, Post::setNormalPosttypeEnum);
        binder.bind(normalpoststatusComboBox, Post::getNormalPoststatusEnum, Post::setNormalPoststatusEnum);
        binder.bind(kategoriComboBox, Post::getKategori, Post::setKategori);
        //binder.bind(kategoriDetaljComboBox, Post::getKategori, Post::setKategori);
        binder.bind(ekstraInfoTextArea, Post::getEkstraInfoString, Post::setEkstraInfoString);
        binder.bind(uuidTextField, Post::getUuidString, Post::setUuidStringFake);
        binder.bind(forelderPostUUID, Post::getForelderPostUUID, Post::setForelderPostUUID);
        binder.bind(kostnadspakkeComboBox, Post::getKostnadsPakke, Post::setKostnadsPakke);
    }

    @Override
    public void oppdaterEtterLagringFraDialog(Periodepost kostnadspakke) {
        kostnadspakkeComboBox.setItems(kostnadspakkeService.finnAlleKostnadspakker());
        kostnadspakkeComboBox.setValue(kostnadspakke);
    }
}
