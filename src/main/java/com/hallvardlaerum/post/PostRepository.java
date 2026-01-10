package com.hallvardlaerum.post;

import com.hallvardlaerum.kategori.Kategori;
import com.hallvardlaerum.libs.database.RepositoryTillegg;
import com.hallvardlaerum.periodepost.Periodepost;
import com.hallvardlaerum.post.budsjettpost.BudsjettpoststatusEnum;
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


    List<Post> findByDatoLocalDateAndTekstFraBankenStringAndKategori(
        LocalDate datoLocalDate, String tekstFraBankenString, Kategori kategori
    );

    List<Post> findByDatoLocalDateBetweenAndKategori(LocalDate fraLocalDate, LocalDate tilLocalDate, Kategori kategori);

    List<Post> findByDatoLocalDateBetweenAndBudsjettpoststatusEnumAndPostklasseEnumOrderByInnPaaKontoIntegerDescUtFraKontoIntegerDesc(
            LocalDate fraLocalDate, LocalDate tilLocalDate, BudsjettpoststatusEnum budsjettpoststatusEnum, PostklasseEnum postklasseEnum);

    List<Post> findByKostnadsPakke(Periodepost kostnadspakke);

    List<Post> findByPostklasseEnum(PostklasseEnum postklasseEnum);


    @NativeQuery(
            "SELECT " +
                    "p.* " +
                    "FROM " +
                    "post p LEFT JOIN kategori k ON p.kategori_uuid = k.uuid " +
                    "WHERE " +
                    "p.dato_local_date >= ?1 AND p.dato_local_date <= ?2 " +
                    "AND p.postklasse_enum = ?3 " +
                    "AND k.tittel = ?4"
    )
    List<Post> finnEtterFraDatoTilDatoOgPostklasseOgKategoritittel(LocalDate datoFraLocalDate, LocalDate datoTilLocalDate, PostklasseEnum postklasseEnum,  String kategoriTittel);

    @NativeQuery(
            "SELECT " +
                    "p.* " +
                    "FROM " +
                    "post p LEFT JOIN kategori k ON p.kategori_uuid = k.uuid " +
                    "WHERE " +
                    "p.dato_local_date >= ?1 AND p.dato_local_date <= ?2 " +
                    "AND k.tittel = ?3"
    )
    List<Post> finnEtterFraDatoTilDatoOgKategoritittel(LocalDate datoFraLocalDate, LocalDate datoTilLocalDate, String kategoriTittel);

    List<Post> findByDatoLocalDateBetweenAndPostklasseEnum(LocalDate fraLocalDate, LocalDate tilLocalDate, PostklasseEnum postklasseEnum);


    @NativeQuery(value = "SELECT sum(p.inn_paa_konto_integer) " +
            "FROM post p " +
            "WHERE p.dato_local_date >= ?1 AND p.dato_local_date <= ?2 AND " +
            "p.postklasse_enum = 0 AND p.kategori_uuid IS NOT NULL " +
            "AND p.normalposttype_enum != 2"
    )  //Postklasse 0 = Normalpost, Normalposttype 2 = Utelates
    Integer sumInnFradatoTilDatoNormalposterMedOverfoeringer(LocalDate fraOgMedLocalDate, LocalDate tilOgMedLocalDate);

    @NativeQuery(value = "SELECT sum(p.ut_fra_konto_integer) FROM post p " +
            "WHERE p.dato_local_date >= ?1 AND p.dato_local_date <= ?2 AND " +
            "p.postklasse_enum = 0 AND p.kategori_uuid IS NOT NULL AND " +
            "p.normalposttype_enum != 2"
    )  //Postklasse 0 = Normalpost, Normalposttype 2 = Utelates
    Integer sumUtFradatoTilDatoNormalposterMedOverfoeringer(LocalDate fraOgMedLocalDate, LocalDate tilOgMedLocalDate);

    @NativeQuery(value = "SELECT sum(p.inn_paa_konto_integer) " +
            "FROM post p LEFT JOIN kategori k ON p.kategori_uuid = k.uuid " +
            "WHERE p.dato_local_date >= ?1 AND p.dato_local_date <= ?2 AND " +
            "p.postklasse_enum = 0 AND " +
            "p.normalposttype_enum != 2 AND " +
            "k.kategori_type != 2"
    )  //Postklasse 0 = Normalpost, Normalposttype 2 = Utelates, Kategoritype 2 = Overføring
    Integer sumInnFradatoTilDatoNormalposterUtenOverfoeringer(LocalDate fraOgMedLocalDate, LocalDate tilOgMedLocalDate);

    @NativeQuery(value = "SELECT sum(p.ut_fra_konto_integer) " +
            "FROM post p LEFT JOIN kategori k ON p.kategori_uuid = k.uuid " +
            "WHERE p.dato_local_date >= ?1 AND " +
            "p.dato_local_date <= ?2 AND " +
            "p.postklasse_enum = 0 AND " +
            "p.normalposttype_enum != 2 AND " +
            "k.kategori_type != 2"
    )  //Postklasse 0 = Normalpost, Normalposttype 2 = Utelates, , Kategoritype 2 = Overføring
    Integer sumUtFradatoTilDatoNormalposterUtenOverfoeringer(LocalDate fraOgMedLocalDate, LocalDate tilOgMedLocalDate);

    @NativeQuery(value = "SELECT sum(p.inn_paa_konto_integer), sum(p.ut_fra_konto_integer) FROM post p " +
            "WHERE p.dato_local_date >= ?1 AND p.dato_local_date <= ?2 AND " +
            "p.postklasse_enum = 1 AND p.budsjettpoststatus_enum = 1"
    )
    List<Tuple> sumInnUtFradatoTilDatoTildelteBudsjettposter(LocalDate fraDato, LocalDate tilDato);


    @NativeQuery(value = "SELECT sum(p.inn_paa_konto_integer) " +
            "FROM post p WHERE p.dato_local_date >= ?1 AND p.dato_local_date <= ?2 AND " +
            "p.postklasse_enum = 0 AND p.kategori_uuid IS NULL")
    Integer sumInnFraDatoTilDatoNormalposterUtenKategori(LocalDate datoFraLocalDate, LocalDate datoTilLocalDate);

    @NativeQuery(value = "SELECT sum(p.ut_fra_konto_integer) " +
            "FROM post p WHERE p.dato_local_date >= ?1 AND p.dato_local_date <= ?2 AND " +
            "p.postklasse_enum = 0 AND p.kategori_uuid IS NULL")
    Integer sumUtFraDatoTilDatoNormalposterUtenKategori(LocalDate datoFraLocalDate, LocalDate datoTilLocalDate);

    @Deprecated
    //Postklasse 0 = Normalpost, Normalposttype 2 = Utelates, Budsjettpoststatus != 0, dvs foreslått
    @NativeQuery(value = "SELECT p.postklasse_enum, sum(p.inn_paa_konto_integer), sum(p.ut_fra_konto_integer) " +
            "FROM post p LEFT JOIN kategori k ON p.kategori_uuid = k.uuid " +
            "WHERE p.dato_local_date >= ?1 AND " +
            "p.dato_local_date <= ?2 AND " +
            "p.normalposttype_enum != 2 AND p.budsjettpoststatus_enum != 0 AND " +
            "k.nivaa = 1 AND " +
            "k.tittel = ?3 " +
            "GROUP BY p.postklasse_enum "
    )
    List<Tuple> sumPosterFradatoTilDatoKategoritittel(LocalDate fraOgMedLocalDate, LocalDate tilOgMedLocalDate, String kategoritittel);


    @NativeQuery(value = "SELECT sum(p.inn_paa_konto_integer), sum(p.ut_fra_konto_integer) " +
            "FROM post p LEFT JOIN kategori k ON p.kategori_uuid = k.uuid " +
            "WHERE " +
                "p.postklasse_enum = 0 AND " +
                "p.dato_local_date >= ?1 AND p.dato_local_date <= ?2 AND " +
                "p.normalposttype_enum != 2 AND " +
                "k.tittel = ?3 "
    )
    Tuple sumNormalPosterFradatoTilDatoKategoritittel(LocalDate fraOgMedLocalDate, LocalDate tilOgMedLocalDate, String kategoritittel);

    @NativeQuery(value = "SELECT sum(p.inn_paa_konto_integer), sum(p.ut_fra_konto_integer) " +
            "FROM post p LEFT JOIN kategori k ON p.kategori_uuid = k.uuid " +
            "WHERE " +
            "p.postklasse_enum = 1 AND " +
            "p.dato_local_date >= ?1 AND p.dato_local_date <= ?2 AND " +
            "p.budsjettpoststatus_enum = 1 AND " +
            "k.nivaa = 1 AND k.tittel = ?3 "
    )
    Tuple sumBudsjettPosterFradatoTilDatoKategoritittel(LocalDate fraOgMedLocalDate, LocalDate tilOgMedLocalDate, String kategoritittel);

}

