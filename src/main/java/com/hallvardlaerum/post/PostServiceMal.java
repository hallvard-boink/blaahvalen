package com.hallvardlaerum.post;


import com.hallvardlaerum.kategori.Kategori;
import com.hallvardlaerum.kategori.KategoriRepository;
import com.hallvardlaerum.kategori.KategoriService;
import com.hallvardlaerum.libs.database.EntitetserviceMedForelderMal;
import com.hallvardlaerum.periode.Periode;
import com.hallvardlaerum.post.normalpost.NormalposttypeEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import jakarta.persistence.Tuple;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public abstract class PostServiceMal extends EntitetserviceMedForelderMal<Post, Kategori, PostRepository, KategoriRepository> {
    private Boolean erInitiert = false;
    private PostklasseEnum postklasseEnum;
    private PostRepository postRepository;

    public PostServiceMal() {

    }

    public void initPostServiceMal(PostklasseEnum postklasseEnum) {
        if (!erInitiert) {
            this.postklasseEnum = postklasseEnum;
            KategoriService kategoriService = Allvitekyklop.hent().getKategoriService();
            super.initEntitetserviceMal(Post.class, Allvitekyklop.hent().getPostRepository());
            super.initierEntitetserviceMedForelderMal(Kategori.class, kategoriService);
            postRepository = Allvitekyklop.hent().getPostRepository();
            erInitiert = true;
        }
    }


    // === FINN POSTER ===

    public List<Post> finnEtterDatoOgTekstfrabankenOgNormalposttypeenum(
            LocalDate datoLocalDate, String tekstFraBankenString, NormalposttypeEnum normalposttypeEnum
    ) {
        return postRepository.findByDatoLocalDateAndTekstFraBankenStringAndNormalposttypeEnum(datoLocalDate, tekstFraBankenString, normalposttypeEnum);
    }


    public List<Post> finnEtterDatoOgTekstfrabankenOgKategori(
            LocalDate datoLocalDate, String tekstFraBankenString, Kategori kategori
    ) {
        return postRepository.findByDatoLocalDateAndTekstFraBankenStringAndKategori(datoLocalDate, tekstFraBankenString, kategori);
    }


    public List<Post> finnEtterFradatoOgTilDatoOgKategoriOgNivaa(LocalDate datoFraLocalDate, LocalDate datoTilLocalDate, Kategori kategori, Integer kategoriNivaa) {
        if (kategoriNivaa == 1) { //detaljert
            return postRepository.findByDatoLocalDateBetweenAndKategori(datoFraLocalDate, datoTilLocalDate, kategori);
        } else if (kategoriNivaa == 0) { //hovedkategorier
            return postRepository.finnEtterFraDatoTilDatoOgKategoritittel(datoFraLocalDate, datoTilLocalDate, kategori.getTittel());
        } else {
            return new ArrayList<>();
        }

    }

    public List<Post> finnEtterFraDatoTilDatoPostklasseHovedkategori(LocalDate datoFraLocalDate, LocalDate datoTilLocalDate, PostklasseEnum postklasseEnum, Kategori kategori) {
        return postRepository.finnEtterFraDatoTilDatoOgPostklasseOgKategoritittel(
                datoFraLocalDate,
                datoTilLocalDate,
                postklasseEnum,
                kategori.getTittel()
        );
    }

    public List<Post> finnPosterFraDatoTilDatoPostklasse(LocalDate fraLocalDate, LocalDate tilLocalDate, PostklasseEnum postklasseEnum) {
        return postRepository.findByDatoLocalDateBetweenAndPostklasseEnum(fraLocalDate, tilLocalDate, postklasseEnum);
    }


    // == SLETT POSTER ===
    public void slettAllePosterAvDennePostklasseEnum(){
        List<Post> alleNormalposter = postRepository.findByPostklasseEnum(postklasseEnum);
        postRepository.deleteAll(alleNormalposter);
        postRepository.flush();
        //refresh gjøres i View
    }

    public void slettAlle(List<Post> poster) {
        postRepository.deleteAll(poster);
    }


    // == OPPSUMMER INNHOLD I POSTER ===

    public List<Tuple> sumPosterFradatoTilDatoKategoritittel(LocalDate fraOgMedLocalDate, LocalDate tilOgMedLocalDate, String kategoritittel) {
        return postRepository.sumPosterFradatoTilDatoKategoritittel(fraOgMedLocalDate,tilOgMedLocalDate,kategoritittel);
    }

    public Integer sumInnFradatoTilDatoNormalposterMedOverfoeringer(LocalDate fraOgMedLocalDate, LocalDate tilOgMedLocalDate) {
        return postRepository.sumInnFradatoTilDatoNormalposterMedOverfoeringer(fraOgMedLocalDate,tilOgMedLocalDate);
    }

    public Integer sumInnFradatoTilDatoNormalposterUtenOverfoeringer(LocalDate fraOgMedLocalDate, LocalDate tilOgMedLocalDate) {
        return postRepository.sumInnFradatoTilDatoNormalposterUtenOverfoeringer(fraOgMedLocalDate,tilOgMedLocalDate);
    }

    public Integer sumInnFradatoTildatoNormalposterUkategorisert(Periode periode) {
        return postRepository.sumInnFraDatoTilDatoNormalposterUtenKategori(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate());
    }

    public Integer sumUtFradatoTildatoNormalposterUkategorisert(Periode periode) {
        return postRepository.sumUtFraDatoTilDatoNormalposterUtenKategori(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate());
    }

    public List<Tuple> sumInnUtFradatoTilDatoTildelteBudsjettposter(LocalDate fraDato, LocalDate tilDato) {
        return postRepository.sumInnUtFradatoTilDatoTildelteBudsjettposter(fraDato,tilDato);
    }

    public Integer sumUtFradatoTilDatoNormalposterMedOverfoeringer(LocalDate fraOgMedLocalDate, LocalDate tilOgMedLocalDate) {
        return postRepository.sumUtFradatoTilDatoNormalposterMedOverfoeringer(fraOgMedLocalDate,tilOgMedLocalDate);
    }

    public Integer sumUtFradatoTilDatoNormalposterUtenOverfoeringer(LocalDate fraOgMedLocalDate, LocalDate tilOgMedLocalDate) {
        return postRepository.sumUtFradatoTilDatoNormalposterUtenOverfoeringer(fraOgMedLocalDate,tilOgMedLocalDate);
    }
}
