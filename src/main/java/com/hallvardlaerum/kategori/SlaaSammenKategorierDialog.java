package com.hallvardlaerum.kategori;

import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.periodepost.Periodepost;
import com.hallvardlaerum.periodepost.maanedsoversiktpost.MaanedsoversiktpostService;
import com.hallvardlaerum.post.Post;
import com.hallvardlaerum.post.normalpost.NormalpostService;
import com.hallvardlaerum.skalTilHavaara.HallvardsSpan;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.List;

public class SlaaSammenKategorierDialog extends Dialog {
    private KategoriService kategoriService;
    private MaanedsoversiktpostService maanedsoversiktpostService;
    private NormalpostService normalpostService;

    private ConfirmDialog confirmDialog;
    private Kategori beholdesKategori;
    private Kategori fjernesKategori;

    public SlaaSammenKategorierDialog() {
        kategoriService = Allvitekyklop.hent().getKategoriService();
        maanedsoversiktpostService = Allvitekyklop.hent().getMaanedsoversiktpostService();
        normalpostService = Allvitekyklop.hent().getNormalpostService();

        byggOppInnhold();
        initierConfirmDialog();
    }

    private void initierConfirmDialog() {
        confirmDialog = new ConfirmDialog("Slå sammen kategorier?",
                "Den ene kategorien vil bli fjernet, og alle data knyttet til den vil bli knyttet til den andre. Vil du fortsette?",
                "Ja, kjør på",
                e -> slaaSammenKategorier(),
                "Nei, avbryt",
                e -> confirmDialog.close());
    }


    private void byggOppInnhold(){
        byggOppInnhold_leggTilKategoriCombobokser();
        byggOppInnhold_leggTilForklaring();
        byggOppInnhold_leggTilOkOgAvbryt();
    }

    private void byggOppInnhold_leggTilForklaring() {
        HallvardsSpan span = new HallvardsSpan("Denne funksjonen gjør omfattende endringer i dataene. " +
                "Det kan være like greit å gjøre den uønskede kategorien inaktive.");
        add(span);
    }

    private void byggOppInnhold_leggTilOkOgAvbryt() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();

        Button okButton = new Button("Ok, slå sammen");
        okButton.addClickListener(e-> {
            if (kontrollertValgAvKategorier()) {
                confirmDialog.open();
            }
        });
        okButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button avbrytButton = new Button("Avbryt");
        avbrytButton.addClickListener(e -> this.close());

        horizontalLayout.add(okButton, avbrytButton);
        add(horizontalLayout);
    }

    private boolean kontrollertValgAvKategorier(){
        beholdesKategori = beholdesKategoriComboBox.getValue();
        fjernesKategori = fjernesKategoriComboBox.getValue();
        if (beholdesKategori==null || fjernesKategori==null) {
            visMelding("Velg en kategori i begge nedslippsmenyene først.");
            return false;
        } else if (beholdesKategori.getKategoriRetning()!=fjernesKategori.getKategoriRetning()) {
            visMelding("Kategoriene må ha samme retning.");
            return false;
        } else if (beholdesKategori.getKategoriType()!=fjernesKategori.getKategoriType()) {
            visMelding("Kategoriene må ha samme type.");
            return false;
        } else if (!beholdesKategori.getNivaa().equals(fjernesKategori.getNivaa())) {
            visMelding("Kategoriene må ha samme nivå.");
            return false;
        }
        return true;
    }

    private void visMelding(String meldingString) {
        Notification.show(meldingString,3000, Notification.Position.MIDDLE);
    }

    private void slaaSammenKategorier() {


        slaaSammenKategorier_poster();
        slaaSammenKategorier_periodeposter();
        oppdaterPeriodeposter();
        slettOverfloedigePeriodeposter();
        slettKategoriSomSkalFjernes();

        Allvitekyklop.hent().getKategoriView().oppdaterSoekeomraadeFinnAlleRader();
        this.close();
    }

    private void slettOverfloedigePeriodeposter() {
        List<Periodepost> periodeposter = maanedsoversiktpostService.finnPeriodeposterEtterKategori(fjernesKategori);
        maanedsoversiktpostService.slettAlle(periodeposter);


    }

    private void slettKategoriSomSkalFjernes() {
        kategoriService.slett(fjernesKategori);
    }

    private void oppdaterPeriodeposter() {
        List<Periodepost> periodeposter = maanedsoversiktpostService.finnPeriodeposterEtterKategori(beholdesKategori);
        for (Periodepost periodepost:periodeposter) {
            maanedsoversiktpostService.oppdaterOgLagreSummerForVanligePeriodeposter(periodepost);
        }

    }

    private void slaaSammenKategorier_periodeposter() {
        List<Periodepost> periodeposter = maanedsoversiktpostService.finnPeriodeposterEtterKategori(fjernesKategori);
        for (Periodepost periodepost:periodeposter) {
            periodepost.setKategori(beholdesKategori);
        }
        maanedsoversiktpostService.lagreAlle(periodeposter);
        logg("Endret " + periodeposter.size() + " periodeposter");

    }

    private void slaaSammenKategorier_poster() {
        List<Post> poster = normalpostService.finnPosterEtterKategori(fjernesKategori); //finner også budsjettposter
        for (Post post:poster) {
            post.setKategori(beholdesKategori);
        }
        Allvitekyklop.hent().getNormalpostService().lagreAlle(poster);
        logg("Endret " + poster.size() + " poster");
    }

    private void logg(String meldingString){
        Loggekyklop.bruk().loggINFO("Slå sammen " + beholdesKategori.hentKortnavn() + " (beholds) med " + fjernesKategori.hentKortnavn() + " (fjernes): " + meldingString);
    }

    private ComboBox<Kategori> beholdesKategoriComboBox;
    private ComboBox<Kategori> fjernesKategoriComboBox;
    private void byggOppInnhold_leggTilKategoriCombobokser() {

        HallvardsSpan beholdesSpan = new HallvardsSpan("Kategori som skal bevares");
        beholdesSpan.settTekstfarge(HallvardsSpan.Farge.GRØNN);
        beholdesSpan.settBold(true);
        beholdesKategoriComboBox = new ComboBox<>();
        beholdesKategoriComboBox.setItemLabelGenerator(Kategori::hentBeskrivendeNavn);
        beholdesKategoriComboBox.setItems(kategoriService.finnAlle());
        beholdesKategoriComboBox.setWidth("500px");

        HallvardsSpan fjernesSpan = new HallvardsSpan("Kategori som skal fjernes");
        fjernesSpan.settTekstfarge(HallvardsSpan.Farge.RØD);
        fjernesKategoriComboBox = new ComboBox<>();
        fjernesKategoriComboBox.setItemLabelGenerator(Kategori::hentBeskrivendeNavn);
        fjernesKategoriComboBox.setItems(kategoriService.finnAlle());
        fjernesKategoriComboBox.setWidth("500px");

        FormLayout formLayout = new FormLayout();
        formLayout.add(beholdesSpan,beholdesKategoriComboBox);
        formLayout.setColspan(beholdesKategoriComboBox,2);
        formLayout.add(fjernesSpan,fjernesKategoriComboBox);
        formLayout.setColspan(fjernesKategoriComboBox,2);

        add(formLayout);
    }
}
