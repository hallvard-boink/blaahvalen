package com.hallvardlaerum.post.budsjettpost;

import com.hallvardlaerum.grunndata.kategori.Kategori;
import com.hallvardlaerum.grunndata.kategori.KategoriService;
import com.hallvardlaerum.libs.ui.BooleanCombobox;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeAktig;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeMal;
import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.post.Post;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;

@Component
@UIScope
public class BudsjettpostRedigeringsomraade extends RedigeringsomraadeMal<Post> implements RedigeringsomraadeAktig<Post>, InitieringsEgnet {
    private boolean erInitiert = false;
    private KategoriService kategoriService;

    private DatePicker datoDatePicker;
    private ComboBox<Kategori> kategoriComboBox;
    private TextField beskrivelseTextField;
    private IntegerField innPaaKontoIntegerField;
    private IntegerField utFraKontoIntegerField;
    private ComboBox<BudsjettpoststatusEnum> budsjettpoststatusEnumCombobox;
    private ComboBox<EstimatpresisjonEnum> estimatpresisjonEnumComboBox;
    private ComboBox<PrioritetEnum> prioritetEnumComboBox;
    private IntegerField rekkefoelgeIntegerField;
    private BooleanCombobox erRegelmessigBooleanCombobox;


    public BudsjettpostRedigeringsomraade() {
        super();
    }

    @Override
    public void instansOppdaterEkstraRedigeringsfelter() {

    }

    @Override
    public void instansOpprettFelter() {
        String hovedtabString = "Hoved";
        String ekstratabString = "Ekstra";

        datoDatePicker = new DatePicker("Dato");
        kategoriComboBox = new ComboBox<>("Kategori");
        kategoriComboBox.setItemLabelGenerator(Kategori::hentBeskrivendeNavn);
        kategoriComboBox.setItems(kategoriService.finnAlle());
        leggTilRedigeringsfelter(hovedtabString, datoDatePicker, kategoriComboBox);

        beskrivelseTextField = leggTilRedigeringsfelt(hovedtabString, new TextField("Beskrivelse"));
        settColspan(beskrivelseTextField, 2);

        innPaaKontoIntegerField = new IntegerField("Inn på konto");
        utFraKontoIntegerField = new IntegerField("Ut fra konto");
        leggTilRedigeringsfelter(hovedtabString, innPaaKontoIntegerField, utFraKontoIntegerField);

        prioritetEnumComboBox = new ComboBox<>("Prioritet");
        prioritetEnumComboBox.setItems(PrioritetEnum.values());
        prioritetEnumComboBox.setItemLabelGenerator(PrioritetEnum::getTittel);

        erRegelmessigBooleanCombobox = new BooleanCombobox();
        erRegelmessigBooleanCombobox.setLabel("Er regelmessig");
        leggTilRedigeringsfelter(hovedtabString, prioritetEnumComboBox, erRegelmessigBooleanCombobox);

        budsjettpoststatusEnumCombobox = new ComboBox<>("Status");
        budsjettpoststatusEnumCombobox.setItems(BudsjettpoststatusEnum.values());
        budsjettpoststatusEnumCombobox.setItemLabelGenerator(BudsjettpoststatusEnum::getTittel);
        estimatpresisjonEnumComboBox = new ComboBox<>("Presisjon på estimat");
        estimatpresisjonEnumComboBox.setItems(EstimatpresisjonEnum.values());
        estimatpresisjonEnumComboBox.setItemLabelGenerator(EstimatpresisjonEnum::getTittel);
        leggTilRedigeringsfelter(ekstratabString, budsjettpoststatusEnumCombobox, estimatpresisjonEnumComboBox);

        rekkefoelgeIntegerField = new IntegerField("Rekkefølge");
        leggTilRedigeringsfelt(ekstratabString, rekkefoelgeIntegerField);

        super.setFokusComponent(beskrivelseTextField);

    }

    @Override
    public void instansByggOppBinder() {
        Binder<Post> binder = super.hentBinder();
        binder.bind(datoDatePicker, Post::getDatoLocalDate, Post::setDatoLocalDate);
        binder.bind(kategoriComboBox, Post::getKategori, Post::setKategori);
        binder.bind(beskrivelseTextField, Post::getBeskrivelseString, Post::setBeskrivelseString);
        binder.bind(innPaaKontoIntegerField, Post::getInnPaaKontoInteger, Post::setInnPaaKontoInteger);
        binder.bind(utFraKontoIntegerField, Post::getUtFraKontoInteger, Post::setUtFraKontoInteger);
        binder.bind(budsjettpoststatusEnumCombobox, Post::getBudsjettpoststatusEnum, Post::setBudsjettpoststatusEnum);
        binder.bind(estimatpresisjonEnumComboBox, Post::getEstimatpresisjonEnum, Post::setEstimatpresisjonEnum);
        binder.bind(prioritetEnumComboBox, Post::getPrioritetEnum, Post::setPrioritetEnum);
        binder.bind(rekkefoelgeIntegerField, Post::getRekkefoelgeInteger, Post::setRekkefoelgeInteger);
        binder.bind(erRegelmessigBooleanCombobox, Post::getErRegelmessigBoolean, Post::setErRegelmessigBoolean);

    }

    @Override
    public void init() {
        if (!erInitiert) {
            super.initRedigeringsomraadeMal();
            this.kategoriService = Allvitekyklop.hent().getKategoriService();
            instansOpprettFelter();
            instansByggOppBinder();
        }
    }

    @Override
    public boolean erInitiert() {
        return erInitiert;
    }
}
