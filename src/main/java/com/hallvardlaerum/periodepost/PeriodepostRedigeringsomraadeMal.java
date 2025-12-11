package com.hallvardlaerum.periodepost;

import com.hallvardlaerum.grunndata.kategori.Kategori;
import com.hallvardlaerum.grunndata.kategori.KategoriService;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.felter.HelTallMester;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeAktig;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeMal;
import com.hallvardlaerum.periode.Periode;
import com.hallvardlaerum.periode.PeriodeServiceMal;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import com.hallvardlaerum.post.Post;
import com.hallvardlaerum.post.PostServiceMal;
import com.hallvardlaerum.post.PostklasseEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import org.apache.catalina.valves.AbstractAccessLogValve;

import java.lang.reflect.Field;
import java.util.List;

public class PeriodepostRedigeringsomraadeMal extends RedigeringsomraadeMal<Periodepost> implements RedigeringsomraadeAktig<Periodepost> {
    private PeriodepostTypeEnum periodepostTypeEnum;
    private PeriodetypeEnum periodetypeEnum;
    private KategoriService kategoriService;
    private PeriodeServiceMal periodeServiceMal;
    private PostServiceMal postService;

    //== FELTENE ===
    private ComboBox<PeriodepostTypeEnum> periodeposttypeEnumComboBox = null;
    private ComboBox<Kategori> kategoriComboBox;
    private TextField sumBudsjettIntegerTextField;
    private TextField sumRegnskapIntegerTextField;
    private ComboBox<Periode> periodeComboBox;
    private TextArea beskrivelseTextArea;
    private TextArea beskrivelseRegnskapTextArea;

    private Grid<Post> normalposterGrid;


    public PeriodepostRedigeringsomraadeMal() {
        super();

    }

    public void initierPeriodepostRedigeringsomraadeMal(PeriodepostTypeEnum periodepostTypeEnum,
                                                        PeriodeServiceMal periodeServiceMal,
                                                        PeriodetypeEnum periodetypeEnum) {
        this.periodepostTypeEnum = periodepostTypeEnum;
        this.periodeServiceMal = periodeServiceMal;
        this.periodetypeEnum = periodetypeEnum;
        this.postService = Allvitekyklop.hent().getNormalpostService();
        this.kategoriService = Allvitekyklop.hent().getKategoriService();


//        if (periodepostTypeEnum==PeriodepostTypeEnum.MAANEDSOVERSIKTPOST) {
//            this.settView(Allvitekyklop.hent().getMaanedsoversiktpostView());
//        } else if (periodepostTypeEnum==PeriodepostTypeEnum.AARSOVERSIKTPOST) {
//            this.settView(Allvitekyklop.hent().getAarsoversiktpostView());
//        }

        if (periodeposttypeEnumComboBox==null) {
            super.initRedigeringsomraadeMal(); //Ny binder
            instansOpprettFelter();
            instansByggOppBinder();
        }
    }


    @Override
    public void instansOppdaterEkstraRedigeringsfelter() {
        Periodepost periodepost = getEntitet();
        List<Post> normalposterList = postService.hentPosterFradatoTilDato(
            periodepost.getPeriode().getDatoFraLocalDate(),
            periodepost.getPeriode().getDatoTilLocalDate(),
            PostklasseEnum.NORMALPOST,
            periodepost.getKategori()
        );
        normalposterGrid.setItems(normalposterList);
        Loggekyklop.hent().loggFEIL("instansOppdaterEkstraRedigeringsfelter: binder er "+ super.hentBinder().toString());
    }



    @Override
    public void instansOpprettFelter() {

        periodeposttypeEnumComboBox = new ComboBox<>("Type");
        periodeposttypeEnumComboBox.setItemLabelGenerator(PeriodepostTypeEnum::getTittel);
        periodeposttypeEnumComboBox.setItems(PeriodepostTypeEnum.values());

        periodeComboBox = new ComboBox<>("Periode");
        periodeComboBox.setItemLabelGenerator(Periode::hentBeskrivendeNavn);
        periodeComboBox.setItems(periodeServiceMal.finnAlleEgndePerioder(periodetypeEnum));

        kategoriComboBox = new ComboBox<>("Kategori");
        kategoriComboBox.setItemLabelGenerator(Kategori::getTittel);
        kategoriComboBox.setItems(kategoriService.finnAlle());

        sumBudsjettIntegerTextField = new TextField("Budsjett");
        sumRegnskapIntegerTextField = new TextField("Regnskap");

        beskrivelseTextArea = new TextArea("Beskrivelse");
        beskrivelseTextArea.setMinRows(2);
        beskrivelseTextArea.addValueChangeListener(e -> beskrivelseRegnskapTextArea.setValue(e.getValue()));


        beskrivelseRegnskapTextArea = new TextArea("Beskrivelse");
        beskrivelseRegnskapTextArea.addValueChangeListener(e -> beskrivelseTextArea.setValue(e.getValue()));
        opprettNormaposterGrid();

        String hovedTabString = "Hoved";
        String normalposterTabString = "Normalposter";
        String budsjettposterTabString = "Budsjettposter";

        leggTilRedigeringsfelter(hovedTabString, periodeposttypeEnumComboBox, kategoriComboBox, periodeComboBox);
        leggTilRedigeringsfelter(hovedTabString, sumBudsjettIntegerTextField, sumRegnskapIntegerTextField);
        leggTilRedigeringsfelt(hovedTabString, beskrivelseTextArea);

        leggTilRedigeringsfelt(normalposterTabString, beskrivelseRegnskapTextArea);
        leggTilRedigeringsfelt(normalposterTabString, normalposterGrid);

        settColspan(beskrivelseTextArea,3);

        setFokusComponent(beskrivelseTextArea);
    }


    private void opprettNormaposterGrid(){
        normalposterGrid = new Grid<>();
        normalposterGrid.addColumn(Post::getDatoLocalDate).setHeader("Dato");
        normalposterGrid.addColumn(Post::getInnPaaKontoInteger).setHeader("Inn");
        normalposterGrid.addColumn(Post::getUtFraKontoInteger).setHeader("Ut");
        normalposterGrid.addColumn(Post::getBeskrivelseString).setHeader("Beskrivelse");

    }

    @Override
    public void instansByggOppBinder() {
        Binder<Periodepost> binder = super.hentBinder();
        binder.bind(periodeposttypeEnumComboBox, Periodepost::getPeriodepostTypeEnum, Periodepost::setPeriodepostTypeEnum);
        binder.bind(periodeComboBox, Periodepost::getPeriode, Periodepost::setPeriode);
        binder.bind(kategoriComboBox, Periodepost::getKategori, Periodepost::setKategori);
        binder.bind(sumBudsjettIntegerTextField, p -> HelTallMester.integerFormatertSomStortTall(p.getSumBudsjettInteger()), null);
        binder.bind(sumRegnskapIntegerTextField, p-> HelTallMester.integerFormatertSomStortTall(p.getSumRegnskapInteger()), null);
        binder.bind(beskrivelseTextArea, Periodepost::getBeskrivelseString, Periodepost::setBeskrivelseString);
        binder.bind(beskrivelseRegnskapTextArea, Periodepost::getBeskrivelseString, Periodepost::setBeskrivelseString);

        //Loggekyklop.hent().loggFEIL("PeriodepostRedigeringsomraadeMal/instansByggOppBinder binder:" + binder.toString());
        //Loggekyklop.hent().loggDEBUG("Binder har bean med periodeposttype " + binder.getBean().getPeriodepostTypeEnum().getTittel());
        //Loggekyklop.hent().loggFEIL("Binder har antall fields " + binder.getFields().toList().size());


    }


}
