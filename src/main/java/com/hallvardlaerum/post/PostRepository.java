package com.hallvardlaerum.post;

import com.hallvardlaerum.grunndata.kategori.Kategori;
import com.hallvardlaerum.libs.database.RepositoryTillegg;
import com.hallvardlaerum.post.normalpost.NormalposttypeEnum;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.NativeQuery;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID>,
        JpaSpecificationExecutor<Post>,
        RepositoryTillegg<Post> {
    List<Post> findByDatoLocalDateAndTekstFraBankenStringAndNormalposttypeEnum(
            LocalDate datoLocalDate, String tekstFraBankenString, NormalposttypeEnum normalposttypeEnum
    );

    List<Post> findByDatoLocalDateBetweenAndPostklasseEnumAndKategoriOrderByInnPaaKontoIntegerDescUtFraKontoIntegerDesc(
            LocalDate datoFraOgMedLocalDate, LocalDate datoTilOgMedLocalDate, PostklasseEnum postklasseEnum, Kategori kategori
    );

    List<Post> findByDatoLocalDateAndTekstFraBankenStringAndKategori(
            LocalDate datoLocalDate, String tekstFraBankenString, Kategori kategori
    );



    @NativeQuery("SELECT k.uuid, COUNT(p.uuid) FROM post p LEFT JOIN kategori k ON p.kategori_uuid  = k.uuid " +
            "WHERE p.dato_local_date >= ?1 AND p.dato_local_date <= ?2 AND p.postklasse_enum = ?3 AND p.normalposttype_enum!=2 " +
            "GROUP BY k.uuid ")
    List<Tuple> hentKategorierDetFinnesPosterMedFraDatoTilDato(LocalDate fraLocalDate, LocalDate tilLocalDate, PostklasseEnum postklasseEnum);

}

