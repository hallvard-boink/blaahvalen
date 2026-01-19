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

// =================
// === ENKLE SØK ===
// =================

    List<Post> findByDatoLocalDateAndTekstFraBankenStringAndNormalposttypeEnum(LocalDate datoLocalDate, String tekstFraBankenString, NormalposttypeEnum normalposttypeEnum);

    List<Post> findByDatoLocalDateAndTekstFraBankenStringAndKategori(LocalDate datoLocalDate, String tekstFraBankenString, Kategori kategori);

    List<Post> findByDatoLocalDateBetweenAndBudsjettpoststatusEnumAndPostklasseEnumOrderByInnPaaKontoIntegerDescUtFraKontoIntegerDesc(
            LocalDate fraLocalDate, LocalDate tilLocalDate, BudsjettpoststatusEnum budsjettpoststatusEnum, PostklasseEnum postklasseEnum);

    List<Post> findByKostnadsPakke(Periodepost kostnadspakke);

    List<Post> findByPostklasseEnum(PostklasseEnum postklasseEnum);

    List<Post> findByDatoLocalDateBetweenAndPostklasseEnum(LocalDate fraLocalDate, LocalDate tilLocalDate, PostklasseEnum postklasseEnum);

    List<Post> findByDatoLocalDateBetweenAndKategoriUuidAndPostklasseEnumOrderByDatoLocalDateAsc(LocalDate datoFraLocalDate, LocalDate datoTilLocalDate, UUID uuid, PostklasseEnum postklasseEnum);

// =====================
// === KOMPLEKSE SØK ===
// =====================

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
    List<Post> finnEtterFraDatoTilDatoOgPostklasseOgKategoritittel(LocalDate datoFraLocalDate, LocalDate datoTilLocalDate, PostklasseEnum postklasseEnum, String kategoriTittel);

    /**
     * Finn poster i et dataspenn som er kategorisert med en gruppe kategorier (ut fra "hovedtittel")
     * Brukes bl.a. til å sjekke om det finnes normalposter eller budsjettposter for en hovedkategori, eller om den kan slettes
     * <br/>
     * @param datoFraLocalDate fra og med dato
     * @param datoTilLocalDate til og med dato
     * @param kategoriTittel "hovedtittel" (heter bare tittel)
     * @return Liste med aktuelle poster
     */
    @NativeQuery(
            "SELECT p.* " +
            "FROM post p LEFT JOIN kategori k ON p.kategori_uuid = k.uuid " +
            "WHERE " +
                "p.dato_local_date >= ?1 AND p.dato_local_date <= ?2 " +
                "AND k.tittel = ?3" )
    List<Post> finnEtterFraDatoTilDatoOgKategoritittelOgPostklasseEnum(LocalDate datoFraLocalDate, LocalDate datoTilLocalDate, String kategoriTittel);


    /**
     * Finner poster som skulle vært utelatt fra alle utregninger, men blir likevel tatt med
     * Poster som har en  kategori med type 4 [Skal ikke kategoriseres] er typisk original til delposter
     * Disse skal ikke tas med i utregninger, fordi de er erstattet med delposter.
     *
     * @return liste av poster hvor normalposttype skal settes til 2 [Utelates]
     */
    @NativeQuery(value = "SELECT p.* " +
            "FROM post p LEFT JOIN kategori k ON p.kategori_uuid = k.uuid " +
            "WHERE " +
            "p.postklasse_enum = 0 AND " +
            "k.kategori_type = 4 AND p.normalposttype_enum !=2 ")
    List<Post> finnPosterSomSkalKorrigeres_FeilNormalposttypeSelvOmKategoriErType4SkalIkkekategoriseres();

// =================================
// === Utregning av en og en sum ===
// =================================
    // Disse er enklere å håndtere enn søk som returnerer Tuple, fordi klassen er gitt før kjøring

    @NativeQuery(value = "SELECT sum(p.inn_paa_konto_integer) " +
            "FROM post p JOIN kategori k ON p.kategori_uuid = k.uuid " +
            "WHERE p.dato_local_date >= ?1 AND p.dato_local_date <= ?2 AND " +
            "p.postklasse_enum = 0 AND " +
            "p.normalposttype_enum != 2 AND " +
            "k.kategori_type != 4")
    Integer sumInnFradatoTilDatoKategoriserteNormalposterMedOverfoeringer(LocalDate fraOgMedLocalDate, LocalDate tilOgMedLocalDate);
    //Postklasse 0 = Normalpost, Normalposttype 2 = Utelates


    @NativeQuery(value = "SELECT sum(p.ut_fra_konto_integer) " +
            "FROM post p JOIN kategori k ON p.kategori_uuid = k.uuid " +
            "WHERE p.dato_local_date >= ?1 AND p.dato_local_date <= ?2 AND " +
            "p.postklasse_enum = 0 AND " +
            "p.normalposttype_enum != 2 AND " +
            "k.kategori_type != 4")
    Integer sumUtFradatoTilDatoKategoriserteNormalposterMedOverfoeringer(LocalDate fraOgMedLocalDate, LocalDate tilOgMedLocalDate);
    //Postklasse 0 = Normalpost, Normalposttype 2 = Utelates


    @NativeQuery(value = "SELECT sum(p.inn_paa_konto_integer) " +
            "FROM post p LEFT JOIN kategori k ON p.kategori_uuid = k.uuid " +
            "WHERE p.dato_local_date >= ?1 AND p.dato_local_date <= ?2 AND " +
            "p.postklasse_enum = 0 AND " +
            "p.normalposttype_enum != 2 AND " +
            "k.kategori_type != 2 AND k.kategori_type != 4")
    Integer sumInnFradatoTilDatoKategoriserteNormalposterUtenOverfoeringer(LocalDate fraOgMedLocalDate, LocalDate tilOgMedLocalDate);
    //Postklasse 0 = Normalpost, Normalposttype 2 = Utelates, Kategoritype 2 = Overføring


    @NativeQuery(value = "SELECT sum(p.ut_fra_konto_integer) " +
            "FROM post p LEFT JOIN kategori k ON p.kategori_uuid = k.uuid " +
            "WHERE p.dato_local_date >= ?1 AND " +
            "p.dato_local_date <= ?2 AND " +
            "p.postklasse_enum = 0 AND " +
            "p.normalposttype_enum != 2 AND " +
            "k.kategori_type != 2 AND k.kategori_type != 4")
    Integer sumUtFradatoTilDatoKategoriserteNormalposterUtenOverfoeringer(LocalDate fraOgMedLocalDate, LocalDate tilOgMedLocalDate);
    //Postklasse 0 = Normalpost, Normalposttype 2 = Utelates, , Kategoritype 2 = Overføring


    @NativeQuery(value = "SELECT sum(p.inn_paa_konto_integer), sum(p.ut_fra_konto_integer) FROM post p " +
            "WHERE p.dato_local_date >= ?1 AND p.dato_local_date <= ?2 AND " +
            "p.postklasse_enum = 1 AND p.budsjettpoststatus_enum = 1")
    List<Tuple> sumInnUtFradatoTilDatoTildelteBudsjettposter(LocalDate fraDato, LocalDate tilDato);


    @NativeQuery(value = "SELECT sum(p.inn_paa_konto_integer) " +
            "FROM post p WHERE p.dato_local_date >= ?1 AND p.dato_local_date <= ?2 AND " +
            "p.postklasse_enum = 0 AND p.normalposttype_enum!= 2 AND p.kategori_uuid IS NULL")
    Integer sumInnFraDatoTilDatoNormalposterUtenKategori(LocalDate datoFraLocalDate, LocalDate datoTilLocalDate);


    @NativeQuery(value = "SELECT sum(p.ut_fra_konto_integer) " +
            "FROM post p WHERE p.dato_local_date >= ?1 AND p.dato_local_date <= ?2 AND " +
            "p.postklasse_enum = 0 AND p.normalposttype_enum!= 2 AND p.kategori_uuid IS NULL")
    Integer sumUtFraDatoTilDatoNormalposterUtenKategori(LocalDate datoFraLocalDate, LocalDate datoTilLocalDate);


// ==========================================
// === Utregning av flere summer samtidig ===
// ==========================================
    // Disse søkene returnerer en enkelt Tuple (som er et sett av verdier), eller en av liste av dem.


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


    List<Post> findByDatoLocalDateAndTekstFraBankenStringAndBeskrivelseString(LocalDate dato, String tekstFraBankenString, String beskrivelseString);


    @NativeQuery(value = "SELECT sum(p.inn_paa_konto_integer), sum(p.ut_fra_konto_integer), count(p.uuid) " +
        "FROM post p " +
        "WHERE " +
        "p.postklasse_enum = 1 AND " +
        "p.dato_local_date >= ?1 AND p.dato_local_date <= ?2 AND " +
        "p.kategori_uuid = ?3"
    )
    Tuple sumInnOgUtOgAntallFradatoTildatoKategori(LocalDate fraDatoLocalDate, LocalDate tilDatoLocalDate, UUID kategori_uuid);

    List<Post> findByKategoriUuid(UUID kategori_uuid);
}




