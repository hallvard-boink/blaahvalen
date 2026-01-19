package com.hallvardlaerum.post;


import com.hallvardlaerum.kategori.Kategori;
import com.hallvardlaerum.kategori.KategoriRepository;
import com.hallvardlaerum.kategori.KategoriService;
import com.hallvardlaerum.libs.database.EntitetserviceMedForelderMal;
import com.hallvardlaerum.periode.Periode;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import jakarta.persistence.Tuple;

import java.time.LocalDate;
import java.util.List;


public abstract class PostServiceMal extends EntitetserviceMedForelderMal<Post, Kategori, PostRepository, KategoriRepository> {
    private Boolean erInitiert = false;
    private PostklasseEnum postklasseEnum;
    private PostRepository postRepository;



    // ===================
    // === FINN POSTER ===


    public List<Post> finnPostEtterDatoOgTekstfrabankenOgKategoriOgPostklasseEnum(
            LocalDate datoLocalDate, String tekstFraBankenString, Kategori kategori, PostklasseEnum postklasseEnum
    ) {
        return postRepository.findByDatoLocalDateAndTekstFraBankenStringAndKategori(datoLocalDate, tekstFraBankenString, kategori);
    }


    public List<Post> finnPostEtterFradatoOgTilDatoOgHovedkategori(LocalDate datoFraLocalDate, LocalDate datoTilLocalDate, Kategori kategori) {
        return postRepository.finnEtterFraDatoTilDatoOgKategoritittelOgPostklasseEnum(datoFraLocalDate, datoTilLocalDate, kategori.getTittel());
    }

    public List<Post> finnPostEtterFraDatoTilDatoPostklasseHovedkategori(LocalDate datoFraLocalDate, LocalDate datoTilLocalDate, PostklasseEnum postklasseEnum, Kategori kategori) {
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

    public List<Post> finnPosterEtterKategori(Kategori kategori) {
        return postRepository.findByKategoriUuid(kategori.getUuid());
    }


    // ===================
    // == SLETT POSTER ===
    public void slettAllePosterAvDennePostklasseEnum(){
        List<Post> alleNormalposter = postRepository.findByPostklasseEnum(postklasseEnum);
        postRepository.deleteAll(alleNormalposter);
        postRepository.flush();
        //refresh gjøres i View
    }

    public void slettAllePoster(List<Post> poster) {
        postRepository.deleteAll(poster);
    }


    // =================================
    // == OPPSUMMER INNHOLD I POSTER ===

    public Integer sumInnFradatoTilDatoNormalposterMedOverfoeringer(LocalDate fraOgMedLocalDate, LocalDate tilOgMedLocalDate) {
        return postRepository.sumInnFradatoTilDatoKategoriserteNormalposterMedOverfoeringer(fraOgMedLocalDate,tilOgMedLocalDate);
    }

    public Integer sumInnFradatoTilDatoNormalposterUtenOverfoeringer(LocalDate fraOgMedLocalDate, LocalDate tilOgMedLocalDate) {
        return postRepository.sumInnFradatoTilDatoKategoriserteNormalposterUtenOverfoeringer(fraOgMedLocalDate,tilOgMedLocalDate);
    }

    public Integer sumInnNormalposterEtterPeriodeOgUkategorisert(Periode periode) {
        return postRepository.sumInnFraDatoTilDatoNormalposterUtenKategori(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate());
    }

    public Integer sumUtNormalposterEtterPeriodeOgUkategorisert(Periode periode) {
        return postRepository.sumUtFraDatoTilDatoNormalposterUtenKategori(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate());
    }

    public List<Tuple> sumInnUtFradatoTilDatoTildelteBudsjettposter(LocalDate fraDato, LocalDate tilDato) {
        return postRepository.sumInnUtFradatoTilDatoTildelteBudsjettposter(fraDato,tilDato);
    }

    public Integer sumUtFradatoTilDatoNormalposterMedOverfoeringer(LocalDate fraOgMedLocalDate, LocalDate tilOgMedLocalDate) {
        return postRepository.sumUtFradatoTilDatoKategoriserteNormalposterMedOverfoeringer(fraOgMedLocalDate,tilOgMedLocalDate);
    }

    public Integer sumUtFradatoTilDatoNormalposterUtenOverfoeringer(LocalDate fraOgMedLocalDate, LocalDate tilOgMedLocalDate) {
        return postRepository.sumUtFradatoTilDatoKategoriserteNormalposterUtenOverfoeringer(fraOgMedLocalDate,tilOgMedLocalDate);
    }



    // ===========================
    // === Init og constructor ===

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


}
