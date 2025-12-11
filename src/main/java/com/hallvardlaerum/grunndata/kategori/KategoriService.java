package com.hallvardlaerum.grunndata.kategori;

import com.hallvardlaerum.libs.database.EntitetserviceMal;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeAktig;
import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import org.springframework.stereotype.Service;

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

    @Override
    public Kategori opprettEntitet() {
        return leggTilUUID(new Kategori());
    }


    @Override
    @Deprecated
    public RedigeringsomraadeAktig<Kategori> hentRedigeringsomraadeAktig() {
        return kategoriRedigeringsomraade;
    }


    public Kategori finnEtterTittel(String tittel) {
        Optional<Kategori> kategoriOptional = kategoriRepository.findByTittel(tittel);
        if (kategoriOptional.isEmpty()) {
            return null;
        } else {
            return kategoriOptional.get();
        }
    }
}
