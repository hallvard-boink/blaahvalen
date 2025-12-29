package com.hallvardlaerum.post.normalpost;


import com.hallvardlaerum.grunndata.kategori.Kategori;
import com.hallvardlaerum.grunndata.kategori.KategoriService;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeAktig;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeMal;

import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.periodepost.Periodepost;
import com.hallvardlaerum.periodepost.periodeoversiktpost.PeriodeoversiktpostService;
import com.hallvardlaerum.post.Post;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@UIScope
public class NormalpostRedigeringsomraade extends RedigeringsomraadeMal<Post> implements RedigeringsomraadeAktig<Post>, InitieringsEgnet {
    private boolean erInitiert = false;
    private PeriodeoversiktpostService periodeoversiktpostService;

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


    public NormalpostRedigeringsomraade() {
        super();
    }

    @Override
    public void init() {
        if (!erInitiert) {
            super.initRedigeringsomraadeMal();
            this.kategoriService = Allvitekyklop.hent().getKategoriService();
            periodeoversiktpostService = Allvitekyklop.hent().getPeriodeoversiktpostService();

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
        Allvitekyklop.hent().getNormalpostView().aktiverDelpostknapperHvisAktuelt(blnAktiver);
    }

    @Override
    public void instansOppdaterEkstraRedigeringsfelter() {
        Kategori kategori = hentEntitet().getKategori();
        kategoriDetaljCombobox_OppdaterUtvalgOgSettTilOppsummerendeUnderkategori(kategori);

    }

    @Override
    public void instansOpprettFelter() {
        String hovedtabString= "Hoved";
        String ekstratabString = "Ekstra";

        tekstFraBankenTextField = super.leggTilRedigeringsfelt(hovedtabString, new TextField("Tekst fra banken"));
        settColspan(tekstFraBankenTextField,3);

        datoDatePicker = new DatePicker("Dato");
        innPaaKontoIntegerField = new IntegerField("Inn på konto");
        utFraKontoIntegerField = new IntegerField("Ut fra konto");

        datoDatePicker = new DatePicker("Dato");
        innPaaKontoIntegerField = new IntegerField("Inn på konto");
        utFraKontoIntegerField = new IntegerField("Ut fra konto");
        super.leggTilRedigeringsfelter(hovedtabString, datoDatePicker, innPaaKontoIntegerField, utFraKontoIntegerField);

        egenbeskrivelseTextField = super.leggTilRedigeringsfelt(hovedtabString, new TextField("Egen beskrivelse"));
        settColspan(egenbeskrivelseTextField,3);

        kategoriComboBox = new ComboBox<>("Kategori");
        kategoriComboBox.setItems(kategoriService.finnAlleOppsummerendeUnderkategorier());
        kategoriComboBox.setItemLabelGenerator(Kategori::getTittel);
        kategoriComboBox.addValueChangeListener(event -> {
            if (event != null && hentEntitet()!=null) {

                if (event.isFromClient()) {
                    kategoriDetaljCombobox_OppdaterUtvalgOgSettTilOppsummerendeUnderkategori(event.getValue());
                    kategoriDetaljComboBox.setValue(kategoriComboBox.getValue());
                    if (normalpoststatusComboBox.getValue()==NormalpoststatusEnum.UBEHANDLET) {
                        normalpoststatusComboBox.setValue(NormalpoststatusEnum.FERDIG);
                    }
                }
                //Loggekyklop.bruk().loggINFO("Valuechange i kategoriCombobox");
            }
        });

        kategoriDetaljComboBox = new ComboBox<>("Underkategori");
        kategoriDetaljComboBox.setItems(kategoriService.finnAlleUnderkategorier());
        kategoriDetaljComboBox.setItemLabelGenerator(Kategori::getUndertittel);
        kategoriDetaljComboBox.addValueChangeListener(event -> {
           if (event != null && hentEntitet()!=null & event.isFromClient())  {
                if (normalpoststatusComboBox.getValue()==NormalpoststatusEnum.UBEHANDLET) {
                    normalpoststatusComboBox.setValue(NormalpoststatusEnum.FERDIG);
                }
                kategoriComboBox.setValue(kategoriDetaljComboBox.getValue());
           }

        });


        kostnadspakkeComboBox = new ComboBox<>("Kostnadspakke");
        kostnadspakkeComboBox.setItemLabelGenerator(p -> {
            if (p.getTittelString()==null) {
                return "(mangler tittel)" + p.getUuid();
            } else {
                return p.getTittelString();
            }
        });
        kostnadspakkeComboBox.setItems(periodeoversiktpostService.finnAlleKostnadspakker());
        kostnadspakkeComboBox.addValueChangeListener(event -> {
            if (event != null && hentEntitet() != null && event.isFromClient() && event.getValue()!=null) {
                Kategori kostnadspakkensKategori =event.getValue().getKategori();
                if (kostnadspakkensKategori!=null) {
                    kategoriComboBox.setValue(kostnadspakkensKategori);
                    kategoriDetaljComboBox.setValue(kostnadspakkensKategori);
                }
            }
        });

        super.leggTilRedigeringsfelter(hovedtabString, kategoriComboBox, kategoriDetaljComboBox, kostnadspakkeComboBox);

        normalposttypeComboBox = new ComboBox<>("Posttype");
        normalposttypeComboBox.setItemLabelGenerator(NormalposttypeEnum::getTittel);
        normalposttypeComboBox.setItems(NormalposttypeEnum.values());

        normalpoststatusComboBox = new ComboBox<>("Poststatus");
        normalpoststatusComboBox.setItemLabelGenerator(NormalpoststatusEnum::getTittel);
        normalpoststatusComboBox.setItems(NormalpoststatusEnum.values());

        super.leggTilRedigeringsfelter(ekstratabString, normalposttypeComboBox, normalpoststatusComboBox);

        ekstraInfoTextArea = super.leggTilRedigeringsfelt(ekstratabString, new TextArea("Ekstra info"));
        uuidTextField = super.leggTilRedigeringsfelt(ekstratabString, new TextField("UUID"));
        forelderPostUUID = super.leggTilRedigeringsfelt(ekstratabString, new TextField("ForelderpostUUID"));

        super.leggTilDatofeltTidOpprettetOgRedigert(ekstratabString);

        settFokusKomponent(egenbeskrivelseTextField);
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
}
