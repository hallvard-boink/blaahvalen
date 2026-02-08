package com.hallvardlaerum.post.normalpost;

import com.hallvardlaerum.kategori.Kategori;
import com.hallvardlaerum.libs.database.SearchCriteria;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.felter.Datokyklop;
import com.hallvardlaerum.libs.ui.GridInnholdsTypeEnum;
import com.hallvardlaerum.libs.ui.HallvardsGrid;
import com.hallvardlaerum.periodepost.Periodepost;
import com.hallvardlaerum.post.Post;
import com.hallvardlaerum.post.PostRepository;
import com.hallvardlaerum.post.PostServiceMal;
import com.hallvardlaerum.post.PostklasseEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;

public class NormalpostGrid extends HallvardsGrid<Post, PostRepository> {
    private DatePicker datoFilterDatePicker;
    private TextField tekstfrabankenFilterTextField;
    private TextField egenbeskrivelseFilterTextField;
    private IntegerField innpaakontoFilterIntegerField;
    private IntegerField utfrakontoFilterIntegerField;
    private ComboBox<NormalpoststatusEnum> normalpoststatusFilterCombobox;
    private ComboBox<NormalposttypeEnum> normalposttypeFilterCombobox;
    private ComboBox<Kategori> kategoriFilterComboBox;
    private ComboBox<Periodepost> kostnadspakkeFilterComboBox;
    private Type type;


// ===========================
// region 0 Constructor og Init
// ===========================


    public NormalpostGrid(Type type) {
        super();
        this.type = type;
        super.init(GridInnholdsTypeEnum.PORSJONSVIS, Allvitekyklop.hent().getNormalpostService());
        opprettKolonner();
        initierPorsjonsvisSoek();

    }

    private void initierPorsjonsvisSoek() {
        NormalpostService normalpostService = Allvitekyklop.hent().getNormalpostService();
        super.initierCallbackDataProviderIGrid(
                q -> normalpostService.finnEntiteterMedSpecification(
                        q.getOffset(),
                        q.getLimit(),
                        normalpostService.getEntityFilterSpecification(),
                        Sort.by("datoLocalDate").descending().and(Sort.by("tekstFraBankenString").and(Sort.by("normalposttypeEnum").descending()))
                ),

                query -> normalpostService.tellAntallMedSpecification(
                        query.getOffset(),
                        query.getLimit(),
                        normalpostService.getEntityFilterSpecification())
        );
    }

// endregion



// ===========================
// region 1 Opprett kolonner og filterfelter
// ===========================



    private void opprettKolonner(){
        if (type==Type.FOR_KATEGORISERING) {
            datoFilterDatePicker = opprettDatoKolonneMedFilterfelt(Post::getDatoLocalDate, "Dato");
            tekstfrabankenFilterTextField = opprettTekstKolonneMedFilterfelt(Post::getTekstFraBankenString, "Tekst fra banken");
            innpaakontoFilterIntegerField = opprettIntegerKolonneMedFilterfelt(Post::getInnPaaKontoInteger, "Inn");
            utfrakontoFilterIntegerField = opprettIntegerKolonneMedFilterfelt(Post::getUtFraKontoInteger, "Ut");
            egenbeskrivelseFilterTextField = opprettTekstKolonneMedFilterfelt(Post::getBeskrivelseString, "Beskrivelse");

            opprettKategoriComboboxKolonneMedFilterfelt();
            opprettKostnadspakkeComboboxKolonneMedFilterfelt();

            normalpoststatusFilterCombobox = opprettComboBoxForEnumKolonneMedFilterfelt(Post::getNormalPoststatusEnum, "Status");
            normalpoststatusFilterCombobox.setItems(NormalpoststatusEnum.values());
            normalposttypeFilterCombobox = opprettComboBoxForEnumKolonneMedFilterfelt(Post::getNormalposttypeEnum, "Type");
            normalposttypeFilterCombobox.setItems(NormalposttypeEnum.values());

            //addColumn(Post::getDatoLocalDate).setHeader("Dato").setRenderer(opprettDatoRenderer()).setWidth("150px").setFlexGrow(0);
            //addColumn(Post::getTekstFraBankenString).setHeader("Tekst fra banken").setRenderer(opprettTekstFraBankenRenderer());
            //addColumn(Post::getInnPaaKontoInteger).setHeader("Inn på konto").setRenderer(opprettInnPaaKontoRenderer()).setWidth("150px").setTextAlign(ColumnTextAlign.END).setFlexGrow(0);
            //addColumn(Post::getUtFraKontoInteger).setHeader("Ut fra konto").setRenderer(opprettUtFraKontoRenderer()).setWidth("150px").setTextAlign(ColumnTextAlign.END).setFlexGrow(0);
            //addColumn(Post::getBeskrivelseString).setHeader("Egen beskrivelse").setRenderer(opprettEgenbeskrivelseRenderer());
            //addColumn(Post::getKategori).setHeader("Kategori").setRenderer(opprettKategoriRenderer()).setWidth("200px").setFlexGrow(0);
            //addColumn(Post::getKostnadsPakke).setHeader("Kostnadspakke").setRenderer(opprettKostnadspakkeRenderer());
            //addColumn(Post::getNormalPoststatusEnum).setHeader("Status").setRenderer(opprettPoststatusRenderer()).setWidth("100px").setFlexGrow(0);
            //addColumn(Post::getNormalPosttypeEnum).setHeader("Type").setRenderer(opprettPosttypeRenderer()).setWidth("100px").setFlexGrow(0);
        } else if (type ==Type.FOR_SOEK){
            datoFilterDatePicker = opprettDatoKolonneMedFilterfelt(Post::getDatoLocalDate, "Dato");
            tekstfrabankenFilterTextField = opprettTekstKolonneMedFilterfelt(Post::getTekstFraBankenString, "Tekst fra banken");
            opprettKategoriComboboxKolonneMedFilterfelt();
            egenbeskrivelseFilterTextField = opprettTekstKolonneMedFilterfelt(Post::getBeskrivelseString, "Beskrivelse");
            innpaakontoFilterIntegerField = opprettIntegerKolonneMedFilterfelt(Post::getInnPaaKontoInteger, "Inn");
            utfrakontoFilterIntegerField = opprettIntegerKolonneMedFilterfelt(Post::getUtFraKontoInteger, "Ut");
            opprettKostnadspakkeComboboxKolonneMedFilterfelt();

        } else {
            Loggekyklop.bruk().loggFEIL("Type ikke satt for NormalpostGrid");
        }

    }

    private void opprettKostnadspakkeComboboxKolonneMedFilterfelt() {
        kostnadspakkeFilterComboBox = opprettComboBoxForEntitetKolonneMedFilterfelt(Post::getKostnadsPakke, "KostnadsPakke");
        kostnadspakkeFilterComboBox.setItemLabelGenerator(Periodepost::hentBeskrivendeNavn);
        kostnadspakkeFilterComboBox.setItems(Allvitekyklop.hent().getKostnadspakkeService().finnAlleKostnadspakker());
    }


    private void opprettKategoriComboboxKolonneMedFilterfelt(){
        kategoriFilterComboBox = opprettComboBoxForEntitetKolonneMedFilterfelt(Post::getKategori, "Kategori");
        kategoriFilterComboBox.setItemLabelGenerator(Kategori::hentKortnavn);
        kategoriFilterComboBox.setItems(Allvitekyklop.hent().getKategoriService().finnAlleUnderkategorier());
    }


// endregion



// ===========================
// region 2 Sett filter og oppdater innhold
// ===========================



    @Override
    public void settFilter() {
        ArrayList<SearchCriteria> filtre = new ArrayList<>();

        filtre.add(new SearchCriteria("postklasseEnum",":", PostklasseEnum.NORMALPOST));


        if (datoFilterDatePicker.getValue()!=null) {
            filtre.add(new SearchCriteria("datoLocalDate","<",datoFilterDatePicker.getValue()));
        }

        if (!tekstfrabankenFilterTextField.getValue().isEmpty()) {
            filtre.add(new SearchCriteria("tekstFraBankenString",":",tekstfrabankenFilterTextField.getValue()));
        }

        if (!egenbeskrivelseFilterTextField.getValue().isEmpty()) {
            filtre.add(new SearchCriteria("beskrivelseString",":",egenbeskrivelseFilterTextField.getValue()));
        }

        if (innpaakontoFilterIntegerField.getValue()!=null) {
            filtre.add(new SearchCriteria("innPaaKontoInteger",">",innpaakontoFilterIntegerField.getValue()));
        }

        if (utfrakontoFilterIntegerField.getValue()!= null) {
            filtre.add(new SearchCriteria("utFraKontoInteger",">",utfrakontoFilterIntegerField.getValue()));
        }

        if (kategoriFilterComboBox.getValue()!=null) {
            filtre.add(new SearchCriteria("kategori",":",kategoriFilterComboBox.getValue()));
        }

        if (kostnadspakkeFilterComboBox.getValue()!=null) {
            filtre.add(new SearchCriteria("kostnadsPakke",":",kostnadspakkeFilterComboBox.getValue()));
        }

        if (type == Type.FOR_KATEGORISERING) {

            if (normalpoststatusFilterCombobox.getValue()!=null) {
                filtre.add(new SearchCriteria("normalpoststatusEnum",":", normalpoststatusFilterCombobox.getValue()));
            }

            if (normalposttypeFilterCombobox.getValue()!=null) {
                filtre.add(new SearchCriteria("normalposttypeEnum",":", normalposttypeFilterCombobox.getValue()));
            }
        }

        super.brukFiltreIDataprovider(filtre);


    }

    // endregion



    // ===========================
    // region 2.1 Grid: Stil og rendering
    // ===========================

    private void settStil(Span span, Post post) {
        if (span == null || post == null) {
            return;
        }

        if (post.getNormalPoststatusEnum()== NormalpoststatusEnum.UBEHANDLET) {
            span.addClassName(LumoUtility.TextColor.ERROR);
            //span.setClassName(LumoUtility.Background.ERROR);  //Tok bare teksten, ikke hele cellen
        } else if (post.getNormalPosttypeEnum()== NormalposttypeEnum.UTELATES) {
            span.addClassName(LumoUtility.TextColor.TERTIARY);
//        } else if (post.getNormalPosttypeEnum()== NormalposttypeEnum.DELPOST) {  //Trengs ikke, blir for mye markering
//            span.setClassName(LumoUtility.TextColor.PRIMARY);
        }
    }


    private ComponentRenderer<Span,Post> opprettKostnadspakkeRenderer(){
        return new ComponentRenderer<>(post -> {
            Span span = post.getKostnadsPakke() != null ? new Span(post.getKostnadsPakke().hentKortnavn()) : new Span("");
            //span.setClassName(LumoUtility.TextAlignment.RIGHT);
            settStil(span, post);
            return span;
        });
    }

    private ComponentRenderer<Span,Post> opprettUtFraKontoRenderer(){
        return new ComponentRenderer<>(post -> {
            Span span = post.getUtFraKontoInteger() != null ? new Span(post.getUtFraKontoInteger().toString()) : new Span("");
            span.setClassName(LumoUtility.TextAlignment.RIGHT);
            settStil(span, post);
            return span;
        });
    }

    private ComponentRenderer<Span,Post> opprettInnPaaKontoRenderer(){
        return new ComponentRenderer<>(post -> {
            Span span = post.getInnPaaKontoInteger() != null ? new Span(post.getInnPaaKontoInteger().toString()) : new Span("");
            span.setClassName(LumoUtility.TextAlignment.RIGHT);
            settStil(span, post);
            return span;
        });
    }

    private ComponentRenderer<Span,Post> opprettEgenbeskrivelseRenderer(){
        return new ComponentRenderer<>(post -> {
            Span span = new Span(post.getBeskrivelseString());
            settStil(span, post);
            return span;
        });
    }


    private ComponentRenderer<Span,Post> opprettTekstFraBankenRenderer(){
        return new ComponentRenderer<>(post -> {
            Span span = new Span(post.getTekstFraBankenString());
            settStil(span, post);
            return span;
        });
    }

    private ComponentRenderer<Span,Post> opprettDatoRenderer(){
        return new ComponentRenderer<>(post -> {
            Span span = post.getDatoLocalDate() != null ? new Span(Datokyklop.hent().formaterDato(post.getDatoLocalDate())) : new Span("");
            settStil(span, post);
            return span;
        });
    }

    private ComponentRenderer<Span, Post> opprettKategoriRenderer(){
        return new ComponentRenderer<>(post -> {
            Span span = post.getKategori() != null ? new Span(post.getKategori().hentKortnavn()) : new Span("");
            settStil(span, post);
            return span;
        });
    }

    private ComponentRenderer<Span,Post> opprettPoststatusRenderer(){
        return new ComponentRenderer<>(post -> {
            Span span = post.getNormalPoststatusEnum()!=null ? new Span(post.getNormalPoststatusEnum().getTittel()) : new Span("");
            settStil(span, post);
            return span;
        });
    }

    private ComponentRenderer<Span,Post> opprettPosttypeRenderer(){
        return new ComponentRenderer<>(post -> {
            Span span = post.getNormalPosttypeEnum() != null ? new Span(post.getNormalPosttypeEnum().getTittel()) : new Span("");
            settStil(span, post);
            return span;
        });
    }

    // endregion

    public enum Type {
        FOR_KATEGORISERING,  //Brukes til å finne frem og vise poster som ikke er kategorisert ennå
        FOR_SOEK; //Brukes for søk, for eksempel til å finne oppdaterte summer til budsjettposter
    }

}
