package com.hallvardlaerum.kategori;

import com.hallvardlaerum.libs.database.EntitetserviceMal;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class KategoriService extends EntitetserviceMal<Kategori, KategoriRepository> implements InitieringsEgnet {
    private KategoriRepository kategoriRepository;
    private boolean erInitiert = false;


    public KategoriService() {
    }

    public List<Kategori> finnHovedKategorierDetFinnesPosterForFraDatoTilDato(LocalDate fraLocalDate, LocalDate tilLocalDate) {
        return kategoriRepository.finnHovedKategorierDetFinnesPosterForFraDatoTilDato(fraLocalDate,tilLocalDate);
    }

    public List<Kategori> finnAlleHovedkategorier() {
        return kategoriRepository.findByNivaaOrderByErAktivDescTittelAscUndertittelAsc(0);
    }

    public List<Kategori> finnAlleOppsummerendeUnderkategorier() {
        return kategoriRepository.findByErOppsummerendeUnderkategoriOrderByTittelAscUndertittelAsc(true);
    }

    public List<Kategori> finnAlleUnderkategorier() {
        return kategoriRepository.findByNivaaOrderByErAktivDescTittelAscUndertittelAsc(1);
    }

    public List<Kategori> finnUnderkategorier(String hovedtittel) {
        return kategoriRepository.findByTittelAndNivaaOrderByUndertittel(hovedtittel, 1);
    }


    @Override
    public boolean erInitiert() {
        return erInitiert;
    }

    public void init() {
        if (!erInitiert) {
            super.initEntitetserviceMal(Kategori.class, Allvitekyklop.hent().getKategoriRepository());
            kategoriRepository = Allvitekyklop.hent().getKategoriRepository();
            erInitiert = true;
        }
    }

    public Kategori finnHovedKategoriEtterTittel(String tittel) {
        List<Kategori> kategoriList = kategoriRepository.findByTittelAndNivaaOrderByUndertittel(tittel, 0);
        if (kategoriList.isEmpty()) {
            return null;
        } else if (kategoriList.size() == 1) {
            return kategoriList.getFirst();
        } else {
            Loggekyklop.hent().loggINFO("Fant flere enn en hovedkategori med tittel " + tittel);
            return kategoriList.getFirst();
        }
    }

    @Override
    public Kategori opprettEntitet() {
        return leggTilUUID(new Kategori());
    }


    @Override
    public ArrayList<Kategori> finnAlle() {
        return new ArrayList<>(kategoriRepository.findAllByOrderByErAktivDescTittelAscUndertittelAsc());
    }

    public Optional<Kategori> finnEtterTittel(String tittel) {
        return kategoriRepository.findByTittel(tittel);
    }

    public Optional<Kategori> finnEtterTittelOgUnderTittel(String tittel, String undertittel) {
        return kategoriRepository.findByTittelAndUndertittel(tittel, undertittel);

    }


    public Kategori finnEtterUndertittel(String undertittel) {
        if (undertittel == null || undertittel.isEmpty()) {
            return null;
        }

        return kategoriRepository.findByUndertittelAndNivaa(undertittel, 1);

    }

    public Optional<Kategori> finnOppsummerendeUnderkategori(String kategoriString) {
        return kategoriRepository.findByTittelAndErOppsummerendeUnderkategori(kategoriString, true);
    }

    public Kategori finnUKATEGORISERT(KategoriRetning kategoriRetning) {
        List<Kategori> kategoriList = kategoriRepository.findByKategoriTypeAndKategoriRetning(KategoriType.UKATEGORISERT, kategoriRetning);
        if (kategoriList.isEmpty()) {
            return null;
        } else {
            if (kategoriList.size()>1) {
                Loggekyklop.bruk().loggADVARSEL("Fant mer enn en kategori med kategoritype UKATEGORISERT og retning " + kategoriRetning + ". Rydd opp i kategoriene");
            }
            return kategoriList.getFirst();
        }
    }

    public Kategori finnEllerOpprettKategoriUKATEGORISERT(KategoriRetning kategoriRetning) {
        Kategori kategoriUkategorisert = finnUKATEGORISERT(kategoriRetning);
        if (kategoriUkategorisert==null) {
            kategoriUkategorisert = opprettEntitet();
            kategoriUkategorisert.setTittel("[Ukategorisert " + kategoriRetning.getTittel() + "]");
            kategoriUkategorisert.setKategoriType(KategoriType.UKATEGORISERT);
            kategoriUkategorisert.setKategoriRetning(kategoriRetning);
            lagre(kategoriUkategorisert);
            return kategoriUkategorisert;
        } else {
            return kategoriUkategorisert;
        }
    }
}
