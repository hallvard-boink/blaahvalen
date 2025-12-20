package com.hallvardlaerum.periode;

import com.hallvardlaerum.libs.database.RepositoryTillegg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.NativeQuery;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PeriodeRepository extends JpaRepository<Periode, UUID>,
        JpaSpecificationExecutor<Periode>,
        RepositoryTillegg<Periode> {

    List<Periode> findByPeriodetypeEnumOrderByDatoFraLocalDateDesc(PeriodetypeEnum periodetypeEnum);
    List<Periode> findByPeriodetypeEnumAndDatoFraLocalDate(PeriodetypeEnum periodetypeEnum, LocalDate datoFraLocalDate);
    List<Periode> findByPeriodetypeEnumAndDatoFraLocalDateGreaterThanEqualAndDatoTilLocalDateLessThanEqual(PeriodetypeEnum periodetypeEnum, LocalDate datoFra, LocalDate datoTil);

    @NativeQuery(value = "SELECT sum(p.ut_fra_konto_integer) FROM post p " +
            "WHERE p.dato_local_date >= ?1 AND " +
            "p.dato_local_date <= ?2 AND " +
            "p.postklasse_enum = 0 AND " +
            "p.normalposttype_enum != 2"
            )  //Postklasse 0 = Normalpost, Normalposttype 2 = Utelates
    Integer sumUtFradatoTilDatoNormalposterMedOverfoeringer(LocalDate fraOgMedLocalDate, LocalDate tilOgMedLocalDate);

    @NativeQuery(value = "SELECT sum(p.inn_paa_konto_integer) FROM post p " +
            "WHERE p.dato_local_date >= ?1 AND " +
            "p.dato_local_date <= ?2 AND " +
            "p.postklasse_enum = 0 " +
            "AND p.normalposttype_enum != 2"
            )  //Postklasse 0 = Normalpost, Normalposttype 2 = Utelates
    Integer sumInnFradatoTilDatoNormalposterMedOverfoeringer(LocalDate fraOgMedLocalDate, LocalDate tilOgMedLocalDate);

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



}
