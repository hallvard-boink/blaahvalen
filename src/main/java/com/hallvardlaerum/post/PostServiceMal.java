package com.hallvardlaerum.post;



import com.hallvardlaerum.grunndata.kategori.Kategori;
import com.hallvardlaerum.grunndata.kategori.KategoriRepository;
import com.hallvardlaerum.grunndata.kategori.KategoriService;
import com.hallvardlaerum.libs.database.EntitetserviceMedForelderMal;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;

import com.hallvardlaerum.post.budsjettpost.BudsjettpoststatusEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.PageRequest;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;


public abstract class PostServiceMal extends EntitetserviceMedForelderMal<Post,Kategori, PostRepository, KategoriRepository>  {
    private KategoriService kategoriService;
    private Boolean erInitiert = false;
    private PostklasseEnum postklasseEnum;

    public PostServiceMal() {

    }

    public void initPostServiceMal(PostklasseEnum postklasseEnum) {
        if (!erInitiert) {
            this.postklasseEnum = postklasseEnum;
            this.kategoriService = Allvitekyklop.hent().getKategoriService();
            super.initEntitetserviceMal(Post.class, Allvitekyklop.hent().getPostRepository());
            super.initierEntitetserviceMedForelderMal(Kategori.class, kategoriService);
            erInitiert=true;
        }
    }

    public List<Post> finnPosterFradatoTilDatoPostklasseenumKategori(LocalDate fraDatoLocalDate, LocalDate tiDatoLocalDate, PostklasseEnum postklasseEnum, Kategori kategori) {
        return hentRepository().findByDatoLocalDateBetweenAndPostklasseEnumAndKategoriOrderByInnPaaKontoIntegerDescUtFraKontoIntegerDesc(
                fraDatoLocalDate, tiDatoLocalDate, postklasseEnum, kategori
        );
    }

    public List<Post> finnBudsjettPosterFradatoTilDatoKategoriBudsjettpoststatus(LocalDate fraDatoLocalDate, LocalDate tiDatoLocalDate, Kategori kategori, BudsjettpoststatusEnum budsjettpoststatusEnum) {
        return hentRepository().findByDatoLocalDateBetweenAndKategoriAndBudsjettpoststatusEnumAndPostklasseEnumOrderByInnPaaKontoIntegerDescUtFraKontoIntegerDesc(
                fraDatoLocalDate, tiDatoLocalDate, kategori, budsjettpoststatusEnum, PostklasseEnum.BUDSJETTPOST
                );
    }

    public ArrayList<Kategori> finnKategorierDetFinnesPosterForFraDatoTilDato(LocalDate fraLocalDate, LocalDate tilLocalDate, PostklasseEnum postklasseEnum) {
        List<Tuple> tupleList = super.hentRepository().hentKategorierDetFinnesPosterForFraDatoTilDatoPostklasse(fraLocalDate, tilLocalDate, postklasseEnum);
        ArrayList<Kategori> kategoriList = new ArrayList<>();
        if (tupleList.isEmpty()) {
            return kategoriList;
        }

        for (Tuple tuple:tupleList) {
            UUID uuid = tuple.get(0, UUID.class);
            if (uuid != null) {
                Kategori kategori = kategoriService.finnEtterUUID(uuid.toString());
                if (kategori==null) {
                    Loggekyklop.hent().loggFEIL("Fant ikke kategorien med uuid " + uuid.toString());
                } else {
                    kategoriList.add(kategori);
                }
            }
        }
        return kategoriList;
    }


    public Stream<Post> finnAlleSomStream(PageRequest springPageRequest) {
        return super.hentRepository().findAll(springPageRequest).stream();
    }

    /**
     * Både budsjettposter og normalposter
     * @param datoFraLocalDate
     * @param datoTilLocalDate
     * @param kategori
     * @return
     */
    public List<Post> finnPosterFradatoTilDatoKategori(LocalDate datoFraLocalDate, LocalDate datoTilLocalDate, Kategori kategori) {
        return hentRepository().findByDatoLocalDateBetweenAndKategori(datoFraLocalDate, datoTilLocalDate, kategori);
    }

    public List<Post> finnPosterFradatoTilDatoOgKategoriOgNivaa(LocalDate datoFraLocalDate, LocalDate datoTilLocalDate, Kategori kategori, Integer kategoriNivaa) {
        if (kategoriNivaa == 1) { //detaljert
            return hentRepository().findByDatoLocalDateBetweenAndKategori(datoFraLocalDate, datoTilLocalDate, kategori);
        } else if (kategoriNivaa == 0) { //hovedkategorier
            return hentRepository().finnEtterFraDatoTilDatoOgKategoritittel(datoFraLocalDate, datoTilLocalDate,kategori.getTittel());
        } else {
            return new ArrayList<>();
        }

    }

    public List<Post> finnPosterFraDatoTilDatoPostklasseHovedkategori(LocalDate datoFraLocalDate, LocalDate datoTilLocalDate, PostklasseEnum postklasseEnum, Kategori kategori) {
        return hentRepository().finnEtterFraDatoTilDatoOgPostklasseOgKategoritittel(
                datoFraLocalDate,
                datoTilLocalDate,
                postklasseEnum,
                kategori.getTittel()
        );
    }
}
