package com.hallvardlaerum.grunndata.kategori;

import com.hallvardlaerum.libs.database.EntitetserviceMal;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeAktig;
import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.periode.Periode;
import com.hallvardlaerum.periode.KategoriBudsjettAntallposterSumInnUt;
import com.hallvardlaerum.post.budsjettpost.BudsjettpoststatusEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import jakarta.persistence.Tuple;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class KategoriService extends EntitetserviceMal<Kategori, KategoriRepository> implements InitieringsEgnet {
    private KategoriRepository kategoriRepository;
    private KategoriRedigeringsomraade kategoriRedigeringsomraade;
    private boolean erInitiert = false;




    public List<KategoriBudsjettAntallposterSumInnUt> byggKategoriMedBudsjettpostList(Periode periode, BudsjettpoststatusEnum budsjettpoststatusEnum) {
        List<Tuple> tuples = kategoriRepository.byggKategoriMedBudsjettpostList(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate(), budsjettpoststatusEnum.ordinal());
        ArrayList<KategoriBudsjettAntallposterSumInnUt> kategoriBudsjettAntallposterSumInnUtArrayList = new ArrayList<>();
        for (Tuple tuple:tuples) {

            String kategoriUUIDString = tuple.get(0, UUID.class).toString();
            Integer antallInteger = konverterFraLong(tuple.get(1, Long.class));
            Integer innPaaKontoInteger = konverterFraBigDecimal(tuple.get(2, BigDecimal.class));
            Integer utFraKontoInteger = konverterFraBigDecimal(tuple.get(3, BigDecimal.class));
            KategoriBudsjettAntallposterSumInnUt kategoriBudsjettAntallposterSumInnUt = new KategoriBudsjettAntallposterSumInnUt(kategoriUUIDString, antallInteger, innPaaKontoInteger, utFraKontoInteger);
            kategoriBudsjettAntallposterSumInnUtArrayList.add(kategoriBudsjettAntallposterSumInnUt);
        }
        return kategoriBudsjettAntallposterSumInnUtArrayList;
    }

    private Integer konverterFraBigDecimal(BigDecimal bigDecimal) {
        if (bigDecimal==null) {
            return null;
        } else {
            BigDecimal rounded = bigDecimal.setScale(0, RoundingMode.HALF_UP);
            return rounded.intValueExact();
        }
    }

    private Integer konverterFraLong(Long valueLong) {
        if (valueLong==null){
            return null;
        } else {
            return valueLong.intValue();
        }
    }

    public List<Kategori> finnAlleHovedkategorier(){
        return kategoriRepository.finnEtterEkskludertKategoriType(KategoriType.DETALJERT);
    }

    public List<Kategori> finnDelkategorier(String hovedtittel){
        return kategoriRepository.findByTittelAndKategoriTypeOrderByUndertittel(hovedtittel,KategoriType.DETALJERT);
    }

    public KategoriService() {
    }

    @Override
    public boolean erInitiert() {
        return erInitiert;
    }

    public void init(){
        if(!erInitiert) {
            super.initEntitetserviceMal(Kategori.class, Allvitekyklop.hent().getKategoriRepository());
            this.kategoriRedigeringsomraade = Allvitekyklop.hent().getKategoriRedigeringsomraade();
            kategoriRepository = Allvitekyklop.hent().getKategoriRepository();
            erInitiert = true;
        }
    }

    public Kategori finnHovedKategoriEtterTittel(String tittel) {
        List<Kategori> kategoriList = kategoriRepository.finnEtterTittelOgEkskludertKategoriType(tittel, KategoriType.DETALJERT);
        if (kategoriList.isEmpty()) {
            return null;
        } else if (kategoriList.size()==1) {
            return kategoriList.getFirst();
        } else {
            Loggekyklop.hent().loggINFO("Fant flere enn en kategori med tittel " + tittel +
                    " som ikke har type " + KategoriType.DETALJERT.getTittel());
            return kategoriList.getFirst();
        }
    }

    @Override
    public Kategori opprettEntitet() {
        return leggTilUUID(new Kategori());
    }



    @Override
    @Deprecated
    public RedigeringsomraadeAktig<Kategori> hentRedigeringsomraadeAktig() {
        return kategoriRedigeringsomraade;
    }

    @Override
    public ArrayList<Kategori> finnAlle() {
        return new ArrayList<>(kategoriRepository.findAllByOrderByTittelAscUndertittelAsc());
    }

    public Optional<Kategori> finnEtterTittel(String tittel) {
        return kategoriRepository.findByTittel(tittel);
    }

    public Optional<Kategori> finnEtterTittelOgUnderTittel(String tittel, String undertittel){
        return kategoriRepository.findByTittelAndUndertittel(tittel, undertittel);

    }


    public void oppdaterNivaaAlleKategorier() {
        List<Kategori> kategoriList = hentRepository().findAll();
        for (Kategori kategori:kategoriList) {
            if (kategori.getUndertittel()==null || kategori.getUndertittel().isEmpty()) {
                kategori.setNivaa(1);
            } else {
                kategori.setNivaa(2);
            }
        }
    }
}
