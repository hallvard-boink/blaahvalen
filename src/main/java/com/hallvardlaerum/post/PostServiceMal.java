package com.hallvardlaerum.post;


import com.hallvardlaerum.kategori.Kategori;
import com.hallvardlaerum.kategori.KategoriRepository;
import com.hallvardlaerum.kategori.KategoriService;
import com.hallvardlaerum.libs.database.EntitetserviceMedForelderMal;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;

import com.hallvardlaerum.post.budsjettpost.BudsjettpoststatusEnum;
import com.hallvardlaerum.post.normalpost.NormalposttypeEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.PageRequest;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;


public abstract class PostServiceMal extends EntitetserviceMedForelderMal<Post, Kategori, PostRepository, KategoriRepository> {
    private KategoriService kategoriService;
    private Boolean erInitiert = false;
    private PostklasseEnum postklasseEnum;
    private PostRepository postRepository;

    public PostServiceMal() {

    }

    public void initPostServiceMal(PostklasseEnum postklasseEnum) {
        if (!erInitiert) {
            this.postklasseEnum = postklasseEnum;
            this.kategoriService = Allvitekyklop.hent().getKategoriService();
            super.initEntitetserviceMal(Post.class, Allvitekyklop.hent().getPostRepository());
            super.initierEntitetserviceMedForelderMal(Kategori.class, kategoriService);
            postRepository = Allvitekyklop.hent().getPostRepository();
            erInitiert = true;
        }
    }

    public List<Tuple> sumPosterFradatoTilDatoKategoritittel(LocalDate fraOgMedLocalDate, LocalDate tilOgMedLocalDate, String kategoritittel) {
        return postRepository.sumPosterFradatoTilDatoKategoritittel(fraOgMedLocalDate,tilOgMedLocalDate,kategoritittel);
    }

    public Integer sumUtFradatoTilDatoNormalposterMedOverfoeringer(LocalDate fraOgMedLocalDate, LocalDate tilOgMedLocalDate) {
        return postRepository.sumUtFradatoTilDatoNormalposterMedOverfoeringer(fraOgMedLocalDate,tilOgMedLocalDate);
    }

    public Integer sumInnFradatoTilDatoNormalposterMedOverfoeringer(LocalDate fraOgMedLocalDate, LocalDate tilOgMedLocalDate) {
        return postRepository.sumInnFradatoTilDatoNormalposterMedOverfoeringer(fraOgMedLocalDate,tilOgMedLocalDate);
    }

    public Integer sumInnFradatoTilDatoNormalposterUtenOverfoeringer(LocalDate fraOgMedLocalDate, LocalDate tilOgMedLocalDate) {
        return postRepository.sumInnFradatoTilDatoNormalposterUtenOverfoeringer(fraOgMedLocalDate,tilOgMedLocalDate);
    }

    public Integer sumUtFradatoTilDatoNormalposterUtenOverfoeringer(LocalDate fraOgMedLocalDate, LocalDate tilOgMedLocalDate) {
        return postRepository.sumUtFradatoTilDatoNormalposterUtenOverfoeringer(fraOgMedLocalDate,tilOgMedLocalDate);
    }

    public List<Tuple> sumInnUtFradatoTilDatoTildelteBudsjettposter(LocalDate fraDato, LocalDate tilDato) {
        return postRepository.sumInnUtFradatoTilDatoTildelteBudsjettposter(fraDato,tilDato);
    }

    public List<Post> findByDatoLocalDateAndTekstFraBankenStringAndNormalposttypeEnum(
            LocalDate datoLocalDate, String tekstFraBankenString, NormalposttypeEnum normalposttypeEnum
    ) {
        return postRepository.findByDatoLocalDateAndTekstFraBankenStringAndNormalposttypeEnum(datoLocalDate, tekstFraBankenString, normalposttypeEnum);
    }


    public List<Tuple> sumPosterFradatoTilDatoKategori(LocalDate fraOgMedLocalDate, LocalDate tilOgMedLocalDate, UUID kategoriUUID) {
        return postRepository.sumPosterFradatoTilDatoKategori(fraOgMedLocalDate, tilOgMedLocalDate, kategoriUUID);
    }

    public List<Post> finnPosterFradatoTilDatoPostklasseenumKategori(LocalDate fraDatoLocalDate, LocalDate tiDatoLocalDate, PostklasseEnum postklasseEnum, Kategori kategori) {
        return postRepository.findByDatoLocalDateBetweenAndPostklasseEnumAndKategoriOrderByInnPaaKontoIntegerDescUtFraKontoIntegerDesc(
                fraDatoLocalDate, tiDatoLocalDate, postklasseEnum, kategori
        );
    }

    public List<Post> finnBudsjettPosterFradatoTilDatoKategoriBudsjettpoststatus(LocalDate fraDatoLocalDate, LocalDate tilDatoLocalDate, Kategori kategori, BudsjettpoststatusEnum budsjettpoststatusEnum) {
        return postRepository.findByDatoLocalDateBetweenAndKategoriAndBudsjettpoststatusEnumAndPostklasseEnumOrderByInnPaaKontoIntegerDescUtFraKontoIntegerDesc(
                fraDatoLocalDate, tilDatoLocalDate, kategori, budsjettpoststatusEnum, PostklasseEnum.BUDSJETTPOST
        );
    }

    public List<Post> findByDatoLocalDateAndTekstFraBankenStringAndKategori(
            LocalDate datoLocalDate, String tekstFraBankenString, Kategori kategori
    ) {
        return postRepository.findByDatoLocalDateAndTekstFraBankenStringAndKategori(datoLocalDate, tekstFraBankenString, kategori);
    }



    public Stream<Post> finnAlleSomStream(PageRequest springPageRequest) {
        return postRepository.findAll(springPageRequest).stream();
    }

    /**
     * Både budsjettposter og normalposter
     *
     * @param datoFraLocalDate Dato Fra
     * @param datoTilLocalDate Dato til
     * @param kategori kategori
     * @return Liste av poster
     */
    public List<Post> finnPosterFradatoTilDatoKategori(LocalDate datoFraLocalDate, LocalDate datoTilLocalDate, Kategori kategori) {
        return postRepository.findByDatoLocalDateBetweenAndKategori(datoFraLocalDate, datoTilLocalDate, kategori);
    }

    public List<Post> finnPosterFradatoTilDatoOgKategoriOgNivaa(LocalDate datoFraLocalDate, LocalDate datoTilLocalDate, Kategori kategori, Integer kategoriNivaa) {
        if (kategoriNivaa == 1) { //detaljert
            return postRepository.findByDatoLocalDateBetweenAndKategori(datoFraLocalDate, datoTilLocalDate, kategori);
        } else if (kategoriNivaa == 0) { //hovedkategorier
            return postRepository.finnEtterFraDatoTilDatoOgKategoritittel(datoFraLocalDate, datoTilLocalDate, kategori.getTittel());
        } else {
            return new ArrayList<>();
        }

    }

    public List<Post> finnPosterFraDatoTilDatoPostklasseHovedkategori(LocalDate datoFraLocalDate, LocalDate datoTilLocalDate, PostklasseEnum postklasseEnum, Kategori kategori) {
        return postRepository.finnEtterFraDatoTilDatoOgPostklasseOgKategoritittel(
                datoFraLocalDate,
                datoTilLocalDate,
                postklasseEnum,
                kategori.getTittel()
        );
    }
}
