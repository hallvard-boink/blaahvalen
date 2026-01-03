package com.hallvardlaerum.kategori;

import com.hallvardlaerum.libs.ui.RedigeringsomraadeAktig;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeMal;
import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;

@Component
@UIScope
public class KategoriRedigeringsomraade extends RedigeringsomraadeMal<Kategori> implements RedigeringsomraadeAktig<Kategori>, InitieringsEgnet {
    private TextField tittelTextField;
    private TextField undertittelTextField;
    private TextArea beskrivelseTextArea;
    private Checkbox brukesTilBudsjettCheckbox;
    private Checkbox brukesTilFastePosterCheckbox;
    private Checkbox brukesTilRegnskapCheckbox;
    private ComboBox<KategoriRetning> kategoriRetningComboBox;
    private ComboBox<KategoriType> kategoriTypeComboBox;
    private IntegerField rekkefoelgeIntegerField;
    private Checkbox erOppsummerendeUnderkategoriCheckbox;
    private Checkbox erAktivCheckbox;
    private ComboBox<Kategori> hovedKategoriComboBox;


    private boolean erInitiert = false;

    public KategoriRedigeringsomraade() {

    }

    @Override
    public void init(){
        if (!erInitiert) {
            super.initRedigeringsomraadeMal();
            instansOpprettFelter();
            instansByggOppBinder();
            erInitiert=true;
        }
    }

    @Override
    public boolean erInitiert() {
        return erInitiert;
    }

    @Override
    public void instansOppdaterEkstraRedigeringsfelter() {

    }

    @Override
    public void instansOpprettFelter() {
        String hovedtabString = "Hoved";
        String ekstratabString = "Ekstra";

        tittelTextField = new TextField("Tittel");
        undertittelTextField = new TextField("Undertittel");
        leggTilRedigeringsfelter(hovedtabString, tittelTextField, undertittelTextField);
        settColspan(tittelTextField,2);
        settColspan(undertittelTextField,2);

        beskrivelseTextArea = leggTilRedigeringsfelt(new TextArea("Beskrivelse"));
        leggTilRedigeringsfelter(hovedtabString, beskrivelseTextArea);
        settColspan(beskrivelseTextArea,4);

        erAktivCheckbox = new Checkbox("Er aktiv");
        brukesTilBudsjettCheckbox = new Checkbox("Brukes til budsjett");
        brukesTilFastePosterCheckbox = new Checkbox("Brukes til faste poster");
        brukesTilRegnskapCheckbox = new Checkbox("Brukes til regnskap");
        erOppsummerendeUnderkategoriCheckbox = new Checkbox("Er oppsummerende underkategori");
        leggTilRedigeringsfelter(hovedtabString,erAktivCheckbox, brukesTilBudsjettCheckbox,  brukesTilFastePosterCheckbox, brukesTilRegnskapCheckbox, erOppsummerendeUnderkategoriCheckbox);

        kategoriRetningComboBox = new ComboBox<>("Retning");
        kategoriRetningComboBox.setItems(KategoriRetning.values());
        kategoriRetningComboBox.setItemLabelGenerator(KategoriRetning::getTittel);

        kategoriTypeComboBox = new ComboBox<>("Type");
        kategoriTypeComboBox.setItems(KategoriType.values());
        kategoriTypeComboBox.setItemLabelGenerator(KategoriType::getTittel);

        rekkefoelgeIntegerField = new IntegerField("Rekkefølge");

        hovedKategoriComboBox = new ComboBox<>("Hovedkategori");
        hovedKategoriComboBox.setItems(Allvitekyklop.hent().getKategoriService().finnAlleHovedkategorier());
        hovedKategoriComboBox.setItemLabelGenerator(Kategori::getTittel);

        leggTilRedigeringsfelter(hovedtabString, kategoriRetningComboBox, kategoriTypeComboBox);

        leggTilRedigeringsfelter(ekstratabString, rekkefoelgeIntegerField, hovedKategoriComboBox);
        leggTilDatofeltTidOpprettetOgRedigert(ekstratabString);

        settFokusKomponent(tittelTextField);

    }

    @Override
    public void instansByggOppBinder() {
        Binder<Kategori> binder = hentBinder();
        binder.bind(tittelTextField, Kategori::getTittel, Kategori::setTittel);
        binder.bind(undertittelTextField, Kategori::getUndertittel, Kategori::setUndertittel);
        binder.bind(beskrivelseTextArea, Kategori::getBeskrivelse, Kategori::setBeskrivelse);
        binder.bind(brukesTilBudsjettCheckbox, Kategori::getBrukesTilBudsjett, Kategori::setBrukesTilBudsjett);
        binder.bind(brukesTilFastePosterCheckbox, Kategori::getBrukesTilFastePoster, Kategori::setBrukesTilFastePoster);
        binder.bind(brukesTilRegnskapCheckbox, Kategori::getBrukesTilRegnskap, Kategori::setBrukesTilRegnskap);
        binder.bind(erOppsummerendeUnderkategoriCheckbox, Kategori::getErOppsummerendeUnderkategori, Kategori::setErOppsummerendeUnderkategori);
        binder.bind(kategoriRetningComboBox, Kategori::getKategoriRetning, Kategori::setKategoriRetning);
        binder.bind(kategoriTypeComboBox, Kategori::getKategoriType, Kategori::setKategoriType);
        binder.bind(rekkefoelgeIntegerField, Kategori::getRekkefoelge, Kategori::setRekkefoelge);
        binder.bind(erAktivCheckbox, Kategori::getErAktiv, Kategori::setErAktiv);

    }
}
