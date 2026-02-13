package com.hallvardlaerum.post.budsjettpost;

import com.hallvardlaerum.kategori.Kategori;
import com.hallvardlaerum.kategori.KategoriMedSumOgAntall;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.felter.Datokyklop;
import com.hallvardlaerum.libs.felter.HelTallMester;
import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.periode.Periode;
import com.hallvardlaerum.post.Post;
import com.hallvardlaerum.post.PostRepository;
import com.hallvardlaerum.post.PostServiceMal;
import com.hallvardlaerum.post.PostklasseEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import com.vaadin.flow.component.notification.Notification;
import jakarta.persistence.Tuple;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class BudsjettpostService extends PostServiceMal implements InitieringsEgnet {
    private boolean erInitiert = false;
    private PostRepository postRepository;


    public List<Post> finnFraPeriodeOgBudsjettstatus(Periode periode, BudsjettpoststatusEnum budsjettpoststatusEnum) {
        return postRepository.findByDatoLocalDateBetweenAndBudsjettpoststatusEnumAndPostklasseEnumOrderByInnPaaKontoIntegerDescUtFraKontoIntegerDesc(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate(), budsjettpoststatusEnum, PostklasseEnum.BUDSJETTPOST);
    }


    public Integer sumInnEllerUtFradatoTildatoKategoritittel(LocalDate fraDato, LocalDate tilDato, String kategoritittel) {
        Tuple tuple = postRepository.sumBudsjettPosterFradatoTilDatoKategoritittel(fraDato, tilDato, kategoritittel);
        if (tuple == null) {
            return 0;
        }

        Integer innInteger = HelTallMester.konverterBigdecimalTilInteger(tuple.get(0, BigDecimal.class), true);
        Integer utInteger = HelTallMester.konverterBigdecimalTilInteger(tuple.get(1, BigDecimal.class), true);

        return innInteger + utInteger;
    }

    public LocalDate opprettPassendeDatoFraPeriode(Periode periode){
        if (periode == null) {
            return LocalDate.now();
        } else {
            if (periode.getDatoFraLocalDate()!=null) {
                return periode.getDatoFraLocalDate();
            } else {
                return LocalDate.now();
            }
        }
    }

    // === Standardmetoder ===
    public BudsjettpostService() {
    }


    @Override
    public void init() {
        if (!erInitiert) {
            super.initPostServiceMal(PostklasseEnum.BUDSJETTPOST);

            postRepository = Allvitekyklop.hent().getPostRepository();

            erInitiert = true;
        }
    }

    @Override
    public boolean erInitiert() {
        return erInitiert;
    }

    @Override
    public Post opprettEntitetMedForelder() {
        return opprettEntitet();
    }

    @Override
    public Post opprettEntitet() {
        Post budsjettpost = leggTilUUID(new Post());
        budsjettpost.setPostklasseEnum(PostklasseEnum.BUDSJETTPOST);
        budsjettpost.setBudsjettpoststatusEnum(BudsjettpoststatusEnum.FORESLAATT);
        return budsjettpost;
    }

    public List<Post> finnEtterPeriodeOgKategori(Periode periode, Kategori kategori) {
        if (periode == null || kategori == null) {
            return new ArrayList<>();
        }
        return postRepository.findByDatoLocalDateBetweenAndKategoriUuidAndPostklasseEnumOrderByDatoLocalDateAsc(
                periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate(), kategori.getUuid(), PostklasseEnum.BUDSJETTPOST);

    }

    public KategoriMedSumOgAntall opprettKategoriMedSumOgAntallBudsjettposter(LocalDate fraDatoLocalDate, LocalDate tilDatoLocalDate, Kategori kategori) {
        Tuple tuple = postRepository.sumInnOgUtOgAntallFradatoTildatoKategori(fraDatoLocalDate, tilDatoLocalDate, kategori.getUuid());
        KategoriMedSumOgAntall kategoriMedSumOgAntall = new KategoriMedSumOgAntall(kategori);
        Integer sumInnInteger = HelTallMester.konverterBigdecimalTilInteger(tuple.get(0, BigDecimal.class), true);
        Integer sumUtInteger = HelTallMester.konverterBigdecimalTilInteger(tuple.get(1, BigDecimal.class), true);

        kategoriMedSumOgAntall.setSumInteger(sumInnInteger + sumUtInteger);
        kategoriMedSumOgAntall.setAntallInteger(HelTallMester.konverterLongTilInteger(tuple.get(2, Long.class)));

        return kategoriMedSumOgAntall;

    }

    /**
     * Denne henter selv markerte Aarsoversikt, eller bruker årets
     */
    public void kopierFasteUtgifterFraIForrigeAar() {
        Periode iaarPeriode = Allvitekyklop.hent().getAarsoversiktRedigeringsomraade().hentEntitet();
        if (iaarPeriode == null) {
            iaarPeriode = Allvitekyklop.hent().getAarsoversiktService().finnAarsoversiktFraAarString(Integer.toString(LocalDate.now().getYear()));
            if (iaarPeriode == null) {
                Notification.show("Du må opprette årets årsoversikt, ellers virker ikke dette. Avbryter.",4000, Notification.Position.MIDDLE);
                return;
            }
        }

        int ifjorInteger = iaarPeriode.getDatoFraLocalDate().getYear()-1;
        Periode ifjorPeriode = Allvitekyklop.hent().getAarsoversiktService().finnAarsoversiktFraAarString(Integer.toString(ifjorInteger));
        if (ifjorPeriode == null) {
            Notification.show("Du må ha en årsoversikt fra " + ifjorInteger + " også, ellers virker ikke dette. Avbryter.",4000, Notification.Position.MIDDLE);
            return;
        }


        List<Post> fasteUtgifterList = Allvitekyklop.hent().getBudsjettpostService().finnFasteUtgifterIPeriode(ifjorPeriode);
        ArrayList<Post> nyePosterArrayList = new ArrayList<>();
        for (Post fastUtgiftPost:fasteUtgifterList) {
            Post nyPost = opprettDuplikat(fastUtgiftPost);
            nyPost.setDatoLocalDate(nyPost.getDatoLocalDate().plusYears(1));
            nyePosterArrayList.add(nyPost);
        }
        lagreAlle(nyePosterArrayList);

    }

    private List<Post> finnFasteUtgifterIPeriode(Periode aarsoversiktPeriode) {
        if (aarsoversiktPeriode==null) {
            Loggekyklop.bruk().loggADVARSEL("Aarsoversiktperiode er null, avbryter");
            return new ArrayList<>();
        } else if (aarsoversiktPeriode.getDatoFraLocalDate()==null || aarsoversiktPeriode.getDatoTilLocalDate()==null) {
            Loggekyklop.bruk().loggADVARSEL("Aarsoversiktperiode har feil dato:" + aarsoversiktPeriode.hentBeskrivendeNavn());
            return new ArrayList<>();
        }

        return Allvitekyklop.hent().getPostRepository().finnBudsjettposterFraDatoTilDatoKategoriFastUtgift(aarsoversiktPeriode.getDatoFraLocalDate(), aarsoversiktPeriode.getDatoTilLocalDate());
    }

    public Post opprettDuplikat(Post budsjettpost){
        Post nyPost = opprettEntitet();
        kopierDataMellomBudsjettposter(budsjettpost,nyPost);
        return nyPost;
    }


    public void kopierDataMellomBudsjettposter(Post fraBudsjettpost, Post tilBudsjettpost) {
        if (fraBudsjettpost != null && tilBudsjettpost != null) {
            tilBudsjettpost.setPostklasseEnum(PostklasseEnum.BUDSJETTPOST);
            tilBudsjettpost.setBudsjettpoststatusEnum(fraBudsjettpost.getBudsjettpoststatusEnum());
            tilBudsjettpost.setKategori(fraBudsjettpost.getKategori());
            tilBudsjettpost.setBeskrivelseString(fraBudsjettpost.getBeskrivelseString());
            tilBudsjettpost.setDatoLocalDate(fraBudsjettpost.getDatoLocalDate());
            tilBudsjettpost.setErRegelmessigBoolean(fraBudsjettpost.getErRegelmessigBoolean());
            tilBudsjettpost.setEstimatpresisjonEnum(fraBudsjettpost.getEstimatpresisjonEnum());
            tilBudsjettpost.setInnPaaKontoInteger(fraBudsjettpost.getInnPaaKontoInteger());
            tilBudsjettpost.setUtFraKontoInteger(fraBudsjettpost.getUtFraKontoInteger());
            tilBudsjettpost.setPrioritetEnum(fraBudsjettpost.getPrioritetEnum());
            tilBudsjettpost.setRekkefoelgeInteger(fraBudsjettpost.getRekkefoelgeInteger());
        }
    }
}
