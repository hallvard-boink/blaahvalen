package com.hallvardlaerum.periodepost;

import com.hallvardlaerum.kategori.Kategori;
import com.hallvardlaerum.libs.database.RepositoryTillegg;
import com.hallvardlaerum.periode.Periode;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.NativeQuery;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PeriodepostRepository extends JpaRepository<Periodepost, UUID>,
        JpaSpecificationExecutor<Periodepost>,
        RepositoryTillegg<Periodepost> {


    List<Periodepost> findByPeriodeAndKategori(Periode periode, Kategori kategori);

    Periodepost findByTittelString(String kostnadspakketittelString);

    List<Periodepost> findByPeriodepostTypeEnumOrderByTittelStringDesc(PeriodepostTypeEnum periodepostTypeEnum);

    List<Periodepost> findByPeriodepostTypeEnumAndPeriode(PeriodepostTypeEnum periodepostTypeEnum, Periode periode);

    @NativeQuery(value = "SELECT p.postklasse_enum, sum(p.inn_paa_konto_integer)+sum(p.ut_fra_konto_integer) " +
            "FROM post p LEFT JOIN kategori k ON p.kategori_uuid = k.uuid " +
            "WHERE p.dato_local_date >= ?1 AND " +
            "p.dato_local_date <= ?2 AND " +
            "p.normalposttype_enum != 2 AND " +
            "k.uuid = ?3"
    )  //Postklasse 0 = Normalpost, Normalposttype 2 = Utelates
    List<Tuple> sumPosterFradatoTilDatoKategori(LocalDate fraOgMedLocalDate, LocalDate tilOgMedLocalDate, UUID kategoriUUID);

    @NativeQuery(value = "SELECT p.postklasse_enum, sum(p.inn_paa_konto_integer), sum(p.ut_fra_konto_integer) " +
            "FROM post p LEFT JOIN kategori k ON p.kategori_uuid = k.uuid " +
            "WHERE p.dato_local_date >= ?1 AND " +
            "p.dato_local_date <= ?2 AND " +
            "(p.normalposttype_enum IS NULL OR p.normalposttype_enum != 2) AND " +
            "k.nivaa = 1 AND " +
            "k.tittel = ?3 " +
            "GROUP BY p.postklasse_enum "
    )  //Postklasse 0 = Normalpost, Normalposttype 2 = Utelates
    List<Tuple> sumPosterFradatoTilDatoKategoritittel(LocalDate fraOgMedLocalDate, LocalDate tilOgMedLocalDate, String kategoritittel);

    @NativeQuery(value = "SELECT pp.* " +
            "FROM periodepost pp " +
                "LEFT JOIN kategori k ON pp.kategori_uuid = k.uuid " +
                "LEFT JOIN periode p ON pp.periode_uuid = p.uuid " +
            "WHERE " +
                "p.uuid = ?1 " +
                "AND k.nivaa = ?2 " +
            "ORDER BY " +
                "pp.sum_regnskap_integer DESC," +
                "pp.sum_budsjett_integer DESC;"
    )
    List<Periodepost> finnEtterPeriodeOgKategorinivaa(UUID periodeUUID, Integer kategoriNivaa);

    @NativeQuery(value =
        "SELECT " +
            "pp.*  " +
        "FROM " +
            "periodepost pp " +
            "LEFT JOIN kategori k ON pp.kategori_uuid = k.uuid " +
            "LEFT JOIN periode p ON pp.periode_uuid = p.uuid " +
        "WHERE " +
            "p.dato_fra_local_date = ?1 " +
            "AND k.tittel = ?2")
    List<Periodepost> finnFraPeriodedatostartOgKategoritittel(LocalDate datoFra, String kategoritittel);

    @NativeQuery(value =
        "SELECT " +
            "pp.uuid, sum(p.inn_paa_konto_integer) , sum(p.ut_fra_konto_integer) " +
        "FROM " +
            "post p LEFT JOIN periodepost pp ON p.kostnads_pakke_uuid = pp.uuid " +
        "WHERE " +
            "p.dato_local_date >=?1 AND p.dato_local_date <=?2 " +
            "AND pp.periodepost_type_enum = 2 " +
        "GROUP BY " +
            "pp.uuid")
    List<Tuple> finnOgOppsummerKostnadspakkerForDatospenn(LocalDate datoFra, LocalDate datoTil);
}
