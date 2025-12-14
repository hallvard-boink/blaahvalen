package com.hallvardlaerum.grunndata.kategori;

import com.hallvardlaerum.libs.database.EntitetserviceMal;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeAktig;
import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import org.checkerframework.checker.units.qual.K;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class KategoriService extends EntitetserviceMal<Kategori, KategoriRepository> implements InitieringsEgnet {
    private KategoriRepository kategoriRepository;
    private KategoriRedigeringsomraade kategoriRedigeringsomraade;
    private boolean erInitiert = false;

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
}
