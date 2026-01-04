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
            "pp.uuid, sum(p.inn_paa_konto_integer) , sum(p.ut_fra_konto_integer) " +
        "FROM " +
            "post p LEFT JOIN periodepost pp ON p.kostnads_pakke_uuid = pp.uuid " +
        "WHERE " +
            "p.dato_local_date >=?1 AND p.dato_local_date <=?2 " +
            "AND pp.periodepost_type_enum = 2 " +
        "GROUP BY " +
            "pp.uuid")
    List<Tuple> finnOgOppsummerKostnadspakkerForDatospenn(LocalDate datoFra, LocalDate datoTil);


    List<Periodepost> findByPeriodepostTypeEnum(PeriodepostTypeEnum periodepostTypeEnum);
}
