package com.hallvardlaerum.regnskap.ui;


import com.hallvardlaerum.grunndata.data.Kategori;
import com.hallvardlaerum.grunndata.service.KategoriService;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeAktig;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeMal;

import com.hallvardlaerum.regnskap.data.Post;
import com.hallvardlaerum.regnskap.data.PoststatusEnum;
import com.hallvardlaerum.regnskap.data.PosttypeEnum;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import org.springframework.stereotype.Component;

@Component
public class PostRedigeringsomraade extends RedigeringsomraadeMal<Post> implements RedigeringsomraadeAktig<Post>{
    private DatePicker datoDatePicker;
    private TextField tekstFraBankenTextField;
    private TextField meldingKIDFaktnrTextField;
    private TextField egenbeskrivelseTextField;
    private IntegerField innPaaKontoIntegerField;
    private IntegerField utFraKontoIntegerField;
    private ComboBox<PosttypeEnum> posttypeComboBox;
    private ComboBox<PoststatusEnum> poststatusComboBox;
    private ComboBox<Kategori> kategoriComboBox;
    private KategoriService kategoriService;
    private TextArea ekstraInfoTextArea;

    public void initier(KategoriService kategoriservice) {
        this.kategoriService = kategoriservice;
        if (datoDatePicker == null) {
            instansOpprettFelter();
            instansByggOppBinder();
        }
    }

    public PostRedigeringsomraade() {
        super();
    }

    @Override
    public void instansOppdaterEkstraRedigeringsfelter() {

    }

    @Override
    public void instansOpprettFelter() {
        datoDatePicker = super.leggTilRedigeringsfelt(new DatePicker("Dato"));
        tekstFraBankenTextField = super.leggTilRedigeringsfelt(new TextField("Tekst fra banken"));
        meldingKIDFaktnrTextField = super.leggTilRedigeringsfelt(new TextField("Melding/KID/Fakturanr"));
        egenbeskrivelseTextField = super.leggTilRedigeringsfelt(new TextField("Egen beskrivelse"));
        innPaaKontoIntegerField = super.leggTilRedigeringsfelt(new IntegerField("Inn på konto"));
        utFraKontoIntegerField = super.leggTilRedigeringsfelt(new IntegerField("Ut fra konto"));

        posttypeComboBox = super.leggTilRedigeringsfelt(new ComboBox<>("Posttype"));
        posttypeComboBox.setItemLabelGenerator(PosttypeEnum::getTittel);
        posttypeComboBox.setItems(PosttypeEnum.values());

        poststatusComboBox = super.leggTilRedigeringsfelt(new ComboBox<>("Poststatus"));
        poststatusComboBox.setItemLabelGenerator(PoststatusEnum::getTittel);
        poststatusComboBox.setItems(PoststatusEnum.values());

        kategoriComboBox = super.leggTilRedigeringsfelt(new ComboBox<>("Kategori"));
        kategoriComboBox.setItems(kategoriService.finnAlle());
        kategoriComboBox.setItemLabelGenerator(Kategori::getTittel);

        ekstraInfoTextArea = super.leggTilRedigeringsfelt(new TextArea("Ekstra info"));

        setFokusComponent(datoDatePicker);
    }

    @Override
    public void instansByggOppBinder() {
        Binder<Post> binder = hentBinder();
        binder.bind(datoDatePicker, Post::getDatoLocalDate, Post::setDatoLocalDate);
        binder.bind(tekstFraBankenTextField, Post::getTekstFraBankenString, Post::setTekstFraBankenString);
        binder.bind(meldingKIDFaktnrTextField, Post::getMeldingKIDFaktnrString, Post::setMeldingKIDFaktnrString);
        binder.bind(egenbeskrivelseTextField, Post::getEgenbeskrivelseString, Post::setEgenbeskrivelseString);
        binder.bind(innPaaKontoIntegerField, Post::getInnPaaKontoInteger, Post::setInnPaaKontoInteger);
        binder.bind(utFraKontoIntegerField, Post::getUtFraKontoInteger, Post::setUtFraKontoInteger);
        binder.bind(posttypeComboBox, Post::getPosttypeEnum, Post::setPosttypeEnum);
        binder.bind(poststatusComboBox, Post::getPoststatusEnum, Post::setPoststatusEnum);
        binder.bind(kategoriComboBox, Post::getKategori, Post::setKategori);
        binder.bind(ekstraInfoTextArea, Post::getEkstraInfoString, Post::setEkstraInfoString);

    }
}
