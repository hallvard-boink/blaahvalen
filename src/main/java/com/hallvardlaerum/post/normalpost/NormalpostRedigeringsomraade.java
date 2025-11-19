package com.hallvardlaerum.post.normalpost;


import com.hallvardlaerum.grunndata.kategori.Kategori;
import com.hallvardlaerum.grunndata.kategori.KategoriService;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeAktig;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeMal;

import com.hallvardlaerum.post.Post;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import org.springframework.stereotype.Component;

@Component
public class NormalpostRedigeringsomraade extends RedigeringsomraadeMal<Post> implements RedigeringsomraadeAktig<Post>{
    private DatePicker datoDatePicker;
    private TextField tekstFraBankenTextField;
    private TextField meldingKIDFaktnrTextField;
    private TextField egenbeskrivelseTextField;
    private IntegerField innPaaKontoIntegerField;
    private IntegerField utFraKontoIntegerField;
    private ComboBox<NormalposttypeEnum> normalposttypeComboBox;
    private ComboBox<NormalpoststatusEnum> normalpoststatusComboBox;
    private ComboBox<Kategori> kategoriComboBox;
    private KategoriService kategoriService;
    private TextArea ekstraInfoTextArea;
    private TextField uuidTextField;
    private TextField forelderPostUUID;

    public void initier(KategoriService kategoriservice) {
        this.kategoriService = kategoriservice;
        if (datoDatePicker == null) {
            instansOpprettFelter();
            instansByggOppBinder();
        }
    }

    public NormalpostRedigeringsomraade() {
        super();
    }

    @Override
    public void aktiver(Boolean blnAktiver) {
        super.aktiver(blnAktiver);
        ((NormalpostView)hentView()).aktiverDelpostknapperHvisAktuelt(blnAktiver);
    }

    @Override
    public void instansOppdaterEkstraRedigeringsfelter() {

    }

    @Override
    public void instansOpprettFelter() {
        String hovedtabString= "Hoved";
        String ekstratabString = "Ekstra";

        tekstFraBankenTextField = super.leggTilRedigeringsfelt(new TextField("Tekst fra banken"),hovedtabString);
        settColspan(tekstFraBankenTextField,3);

        datoDatePicker = new DatePicker("Dato");
        innPaaKontoIntegerField = new IntegerField("Inn på konto");
        utFraKontoIntegerField = new IntegerField("Ut fra konto");

        datoDatePicker = new DatePicker("Dato");
        innPaaKontoIntegerField = new IntegerField("Inn på konto");
        utFraKontoIntegerField = new IntegerField("Ut fra konto");
        super.leggTilRedigeringsfelter(hovedtabString, datoDatePicker, innPaaKontoIntegerField, utFraKontoIntegerField);

        egenbeskrivelseTextField = super.leggTilRedigeringsfelt(new TextField("Egen beskrivelse"),hovedtabString);
        settColspan(egenbeskrivelseTextField,3);

        kategoriComboBox = new ComboBox<>("Kategori");
        kategoriComboBox.setItems(kategoriService.finnAlle());
        kategoriComboBox.setItemLabelGenerator(Kategori::getTittel);
        kategoriComboBox.addValueChangeListener(kategori -> {
            if (kategori != null && getEntitet()!=null) {
                if (normalpoststatusComboBox.getValue()==NormalpoststatusEnum.UBEHANDLET) {
                    normalpoststatusComboBox.setValue(NormalpoststatusEnum.FERDIG);
                }
            }
        });

        normalposttypeComboBox = new ComboBox<>("Posttype");
        normalposttypeComboBox.setItemLabelGenerator(NormalposttypeEnum::getTittel);
        normalposttypeComboBox.setItems(NormalposttypeEnum.values());

        normalpoststatusComboBox = new ComboBox<>("Poststatus");
        normalpoststatusComboBox.setItemLabelGenerator(NormalpoststatusEnum::getTittel);
        normalpoststatusComboBox.setItems(NormalpoststatusEnum.values());
        super.leggTilRedigeringsfelter(hovedtabString, kategoriComboBox, normalposttypeComboBox, normalpoststatusComboBox);


        meldingKIDFaktnrTextField = super.leggTilRedigeringsfelt(new TextField("Melding/KID/Fakturanr"),ekstratabString);
        ekstraInfoTextArea = super.leggTilRedigeringsfelt(new TextArea("Ekstra info"),ekstratabString);
        uuidTextField = super.leggTilRedigeringsfelt(new TextField("UUID"),ekstratabString);
        forelderPostUUID = super.leggTilRedigeringsfelt(new TextField("ForelderpostUUID"),ekstratabString);

        super.leggTilDatofeltTidOpprettetOgRedigert(ekstratabString);

        setFokusComponent(egenbeskrivelseTextField);
    }

    @Override
    public void instansByggOppBinder() {
        Binder<Post> binder = hentBinder();
        binder.bind(datoDatePicker, Post::getDatoLocalDate, Post::setDatoLocalDate);
        binder.bind(tekstFraBankenTextField, Post::getTekstFraBankenString, Post::setTekstFraBankenString);
        binder.bind(meldingKIDFaktnrTextField, Post::getMeldingKIDFaktnrString, Post::setMeldingKIDFaktnrString);
        binder.bind(egenbeskrivelseTextField, Post::getBeskrivelseString, Post::setBeskrivelseString);
        binder.bind(innPaaKontoIntegerField, Post::getInnPaaKontoInteger, Post::setInnPaaKontoInteger);
        binder.bind(utFraKontoIntegerField, Post::getUtFraKontoInteger, Post::setUtFraKontoInteger);
        binder.bind(normalposttypeComboBox, Post::getNormalPosttypeEnum, Post::setNormalPosttypeEnum);
        binder.bind(normalpoststatusComboBox, Post::getNormalPoststatusEnum, Post::setNormalPoststatusEnum);
        binder.bind(kategoriComboBox, Post::getKategori, Post::setKategori);
        binder.bind(ekstraInfoTextArea, Post::getEkstraInfoString, Post::setEkstraInfoString);
        binder.bind(uuidTextField, Post::getUuidString, Post::setUuidStringFake);
        binder.bind(forelderPostUUID, Post::getForelderPostUUID, Post::setForelderPostUUID);
    }
}
