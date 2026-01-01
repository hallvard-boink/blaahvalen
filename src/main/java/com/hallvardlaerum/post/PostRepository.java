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

    List<Post> findByDatoLocalDateBetweenAndPostklasseEnumAndKategoriOrderByInnPaaKontoIntegerDescUtFraKontoIntegerDesc(
        LocalDate datoFraOgMedLocalDate, LocalDate datoTilOgMedLocalDate, PostklasseEnum postklasseEnum, Kategori kategori
    );

    List<Post> findByDatoLocalDateBetweenAndKategoriAndBudsjettpoststatusEnumAndPostklasseEnumOrderByInnPaaKontoIntegerDescUtFraKontoIntegerDesc(
        LocalDate datoFraOgMedLocalDate, LocalDate datoTilOgMedLocalDate, Kategori kategori, BudsjettpoststatusEnum budsjettpoststatusEnum, PostklasseEnum postklasseEnum
    );


    List<Post> findByDatoLocalDateAndTekstFraBankenStringAndKategori(
        LocalDate datoLocalDate, String tekstFraBankenString, Kategori kategori
    );

    List<Post> findByDatoLocalDateBetweenAndKategori(LocalDate fraLocalDate, LocalDate tilLocalDate, Kategori kategori);

    List<Post> findByDatoLocalDateBetweenAndBudsjettpoststatusEnumAndPostklasseEnumOrderByInnPaaKontoIntegerDescUtFraKontoIntegerDesc(LocalDate fraLocalDate, LocalDate tilLocalDate, BudsjettpoststatusEnum budsjettpoststatusEnum, PostklasseEnum postklasseEnum);

    List<Post> findByKostnadsPakke(Periodepost kostnadspakke);

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

    @NativeQuery(value = "SELECT sum(p.inn_paa_konto_integer) FROM post p " +
            "WHERE p.dato_local_date >= ?1 AND " +
            "p.dato_local_date <= ?2 AND " +
            "p.postklasse_enum = 0 " +
            "AND p.normalposttype_enum != 2"
    )  //Postklasse 0 = Normalpost, Normalposttype 2 = Utelates
    Integer sumInnFradatoTilDatoNormalposterMedOverfoeringer(LocalDate fraOgMedLocalDate, LocalDate tilOgMedLocalDate);

    @NativeQuery(value = "SELECT sum(p.ut_fra_konto_integer) FROM post p " +
            "WHERE p.dato_local_date >= ?1 AND " +
            "p.dato_local_date <= ?2 AND " +
            "p.postklasse_enum = 0 AND " +
            "p.normalposttype_enum != 2"
    )  //Postklasse 0 = Normalpost, Normalposttype 2 = Utelates
    Integer sumUtFradatoTilDatoNormalposterMedOverfoeringer(LocalDate fraOgMedLocalDate, LocalDate tilOgMedLocalDate);

    @NativeQuery(value = "SELECT sum(p.inn_paa_konto_integer) " +
            "FROM post p LEFT JOIN kategori k ON p.kategori_uuid = k.uuid " +
            "WHERE p.dato_local_date >= ?1 AND " +
            "p.dato_local_date <= ?2 AND " +
            "p.postklasse_enum = 0 AND " +
            "p.normalposttype_enum != 2 AND " +
            "k.kategori_type != 2"
    )  //Postklasse 0 = Normalpost, Normalposttype 2 = Utelates, Kategoritype 2 = Overføring
    Integer sumInnFradatoTilDatoNormalposterUtenOverfoeringer(LocalDate fraOgMedLocalDate, LocalDate tilOgMedLocalDate);

    @NativeQuery(value = "SELECT sum(p.ut_fra_konto_integer) FROM post p LEFT JOIN kategori k ON p.kategori_uuid = k.uuid " +
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




    // === FORELDETE ===

    /**
     * @deprecated Bruk heller metoden i KategoriRepository
     *
     */
    @Deprecated
    @NativeQuery(
        "SELECT " +
            "k.uuid, COUNT(p.uuid) " +
        "FROM " +
            "post p LEFT JOIN kategori k ON p.kategori_uuid  = k.uuid " +
        "WHERE " +
            "p.dato_local_date >= ?1 AND p.dato_local_date <= ?2 " +
            "AND p.postklasse_enum = ?3 AND p.normalposttype_enum!=2 " +
            "GROUP BY k.uuid ")
    List<Tuple> hentKategorierDetFinnesPosterForFraDatoTilDatoPostklasse(LocalDate fraLocalDate, LocalDate tilLocalDate, PostklasseEnum postklasseEnum);


    /**
     * @deprecated Bruk heller metoden i KategoriRepository
     *
     */
    @Deprecated
    @NativeQuery(
        "SELECT " +
            "k.* FROM post p LEFT JOIN kategori k ON p.kategori_uuid  = k.uuid " +
        "WHERE " +
            "p.dato_local_date >= ?1 AND p.dato_local_date <= ?2 " +
        "GROUP BY " +
            "k.uuid ")
    List<Kategori> hentKategorierDetFinnesPosterForFraDatoTilDato(LocalDate fraLocalDate, LocalDate tilLocalDate);



    /**
     * @deprecated Bruk heller metoden i KategoriRepository
     *
     */
    @Deprecated
    @NativeQuery(
        "SELECT " +
            "k2.uuid, k2.tittel " +
        "FROM " +
            "(" +
                "SELECT " +
                    "k.tittel, count(p.uuid) " +
                "FROM " +
                    "post p LEFT JOIN kategori k ON p.kategori_uuid  = k.uuid " +
                "WHERE " +
                    "p.dato_local_date >=?1 AND p.dato_local_date <= ?2 " +
                "GROUP BY " +
                    "k.tittel" +
            ") as kjerne " +
            "LEFT JOIN kategori k2 ON kjerne.tittel = k2.tittel " +
        "WHERE " +
            "k2.nivaa =0;"
    )
    List<Tuple> hentHovedKategorierDetFinnesPosterForFraDatoTilDato(LocalDate fraLocalDate, LocalDate tilLocalDate);





}

