package com.hallvardlaerum.kategori;

import com.hallvardlaerum.libs.database.EntitetserviceMal;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.felter.DesimalMester;
import com.hallvardlaerum.libs.felter.HelTallMester;
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


    public KategoriService() {
    }

    public List<KategoriBudsjettAntallposterSumInnUt> byggKategoriMedBudsjettpostList(Periode periode, BudsjettpoststatusEnum budsjettpoststatusEnum) {
        List<Tuple> tuples = kategoriRepository.byggKategoriMedBudsjettpostList(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate(), budsjettpoststatusEnum.ordinal());
        ArrayList<KategoriBudsjettAntallposterSumInnUt> kategoriBudsjettAntallposterSumInnUtArrayList = new ArrayList<>();
        for (Tuple tuple:tuples) {

            String kategoriUUIDString = tuple.get(0, UUID.class).toString();
            Integer antallInteger = HelTallMester.konverterLongTilInteger(tuple.get(1, Long.class));
            Integer innPaaKontoInteger = HelTallMester.konverterBigdecimalTilInteger(tuple.get(2, BigDecimal.class));
            Integer utFraKontoInteger = HelTallMester.konverterBigdecimalTilInteger(tuple.get(3, BigDecimal.class));
            KategoriBudsjettAntallposterSumInnUt kategoriBudsjettAntallposterSumInnUt = new KategoriBudsjettAntallposterSumInnUt(kategoriUUIDString, antallInteger, innPaaKontoInteger, utFraKontoInteger);
            kategoriBudsjettAntallposterSumInnUtArrayList.add(kategoriBudsjettAntallposterSumInnUt);
        }
        return kategoriBudsjettAntallposterSumInnUtArrayList;
    }

    public List<Kategori> finnAlleHovedkategorier(){
        return kategoriRepository.findByNivaaOrderByTittelAscUndertittelAsc(0);
    }

    public List<Kategori> finnAlleOppsummerendeUnderkategorier(){
        return kategoriRepository.findByErOppsummerendeUnderkategoriOrderByTittelAscUndertittelAsc(true);
    }

    public List<Kategori> finnAlleUnderkategorier(){
        return kategoriRepository.findByNivaaOrderByTittelAscUndertittelAsc(1);
    }

    public List<Kategori> finnUnderkategorier(String hovedtittel){
        return kategoriRepository.findByTittelAndNivaaOrderByUndertittel(hovedtittel,1);
    }

    @Override
    public boolean erInitiert() {
        return erInitiert;
    }

    public void init() {
        if (!erInitiert) {
            super.initEntitetserviceMal(Kategori.class, Allvitekyklop.hent().getKategoriRepository());
            this.kategoriRedigeringsomraade = Allvitekyklop.hent().getKategoriRedigeringsomraade();
            kategoriRepository = Allvitekyklop.hent().getKategoriRepository();
            erInitiert = true;
        }
    }

    public Kategori finnHovedKategoriEtterTittel(String tittel) {
        List<Kategori> kategoriList = kategoriRepository.findByTittelAndNivaaOrderByUndertittel(tittel, 0);
        if (kategoriList.isEmpty()) {
            return null;
        } else if (kategoriList.size()==1) {
            return kategoriList.getFirst();
        } else {
            Loggekyklop.hent().loggINFO("Fant flere enn en hovedkategori med tittel " + tittel );
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
        return new ArrayList<>(kategoriRepository.findAllByOrderByErAktivDescTittelAscUndertittelAsc());
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

    public Kategori finnEtterUndertittel(String undertittel) {
        if (undertittel==null || undertittel.isEmpty()){
            return null;
        }

        return kategoriRepository.findByUndertittelAndNivaa(undertittel,1);

    }

    public Optional<Kategori> finnOppsummerendeUnderkategori(String kategoriString) {
        return kategoriRepository.findByTittelAndErOppsummerendeUnderkategori(kategoriString,true);
    }
}
