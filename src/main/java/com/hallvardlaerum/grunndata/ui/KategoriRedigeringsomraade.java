package com.hallvardlaerum.grunndata.ui;

import com.hallvardlaerum.grunndata.data.Kategori;
import com.hallvardlaerum.grunndata.data.KategoriRetning;
import com.hallvardlaerum.grunndata.data.KategoriType;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeAktig;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeMal;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import org.springframework.stereotype.Component;

@Component
public class KategoriRedigeringsomraade extends RedigeringsomraadeMal<Kategori> implements RedigeringsomraadeAktig<Kategori> {
    private TextField tittelTextField;
    private TextArea beskrivelseTextArea;
    private Checkbox brukesTilBudsjettCheckbox;
    private Checkbox brukesTilFastePosterCheckbox;
    private Checkbox brukesTilRegnskapCheckbox;
    private ComboBox<KategoriRetning> kategoriRetningComboBox;
    private ComboBox<KategoriType> kategoriTypeComboBox;
    private IntegerField rekkefoelgeIntegerField;
    private Checkbox erAktivCheckbox;


    public void initier(){
        if (tittelTextField==null) {
            instansOpprettFelter();
            instansByggOppBinder();
        }
    }

    @Override
    public void instansOppdaterEkstraRedigeringsfelter() {

    }

    @Override
    public void instansOpprettFelter() {
        tittelTextField = leggTilRedigeringsfelt(new TextField("Tittel"));
        beskrivelseTextArea = leggTilRedigeringsfelt(new TextArea("Beskrivelse"));
        brukesTilBudsjettCheckbox = leggTilRedigeringsfelt(new Checkbox("Brukes til budsjett"));
        brukesTilFastePosterCheckbox = leggTilRedigeringsfelt(new Checkbox("Brukes til faste poster"));
        brukesTilRegnskapCheckbox = leggTilRedigeringsfelt(new Checkbox("Brukes til regnskap"));

        kategoriRetningComboBox = leggTilRedigeringsfelt(new ComboBox<>("Retning"));
        kategoriRetningComboBox.setItems(KategoriRetning.values());
        kategoriRetningComboBox.setItemLabelGenerator(KategoriRetning::getTittel);

        kategoriTypeComboBox = leggTilRedigeringsfelt(new ComboBox<>("Type"));
        kategoriTypeComboBox.setItems(KategoriType.values());
        kategoriTypeComboBox.setItemLabelGenerator(KategoriType::getTittel);

        rekkefoelgeIntegerField = leggTilRedigeringsfelt(new IntegerField("Rekkefølge"));
        erAktivCheckbox = leggTilRedigeringsfelt(new Checkbox("Er aktiv"));

        setFokusComponent(tittelTextField);

    }

    @Override
    public void instansByggOppBinder() {
        Binder<Kategori> binder = hentBinder();
        binder.bind(tittelTextField, Kategori::getTittel, Kategori::setTittel);
        binder.bind(beskrivelseTextArea, Kategori::getBeskrivelse, Kategori::setBeskrivelse);
        binder.bind(brukesTilBudsjettCheckbox, Kategori::getBrukesTilBudsjett, Kategori::setBrukesTilBudsjett);
        binder.bind(brukesTilFastePosterCheckbox, Kategori::getBrukesTilFastePoster, Kategori::setBrukesTilFastePoster);
        binder.bind(brukesTilRegnskapCheckbox, Kategori::getBrukesTilRegnskap, Kategori::setBrukesTilRegnskap);
        binder.bind(kategoriRetningComboBox, Kategori::getKategoriRetning, Kategori::setKategoriRetning);
        binder.bind(kategoriTypeComboBox, Kategori::getKategoriType, Kategori::setKategoriType);
        binder.bind(rekkefoelgeIntegerField, Kategori::getRekkefoelge, Kategori::setRekkefoelge);
        binder.bind(erAktivCheckbox, Kategori::getErAktiv, Kategori::setErAktiv);

    }
}
