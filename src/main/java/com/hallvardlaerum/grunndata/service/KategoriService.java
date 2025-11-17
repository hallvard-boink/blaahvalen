package com.hallvardlaerum.grunndata.service;

import com.hallvardlaerum.grunndata.data.Kategori;
import com.hallvardlaerum.grunndata.ui.KategoriRedigeringsomraade;
import com.hallvardlaerum.libs.database.EntitetserviceMal;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeAktig;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Optional;

@Service
public class KategoriService extends EntitetserviceMal<Kategori, KategoriRepository> {
    private KategoriRepository kategoriRepository;
    private KategoriRedigeringsomraade kategoriRedigeringsomraade;

    public KategoriService(KategoriRepository kategoriRepository) {
        super(Kategori.class, kategoriRepository);
        this.kategoriRepository = kategoriRepository;
    }

    public void initier(KategoriRedigeringsomraade kategoriRedigeringsomraade){
        this.kategoriRedigeringsomraade = kategoriRedigeringsomraade;
        this.kategoriRedigeringsomraade.initier();
    }

    @Override
    public Kategori opprettEntitet() {
        return leggTilUUID(new Kategori());
    }


    @Override
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
