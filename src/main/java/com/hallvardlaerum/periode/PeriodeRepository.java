package com.hallvardlaerum.periode;

import com.hallvardlaerum.libs.database.RepositoryTillegg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.NativeQuery;

import java.time.LocalDate;
import java.util.UUID;

public interface PeriodeRepository extends JpaRepository<Periode, UUID>,
        JpaSpecificationExecutor<Periode>,
        RepositoryTillegg<Periode> {


        @NativeQuery(value = "SELECT sum(p.ut_fra_konto_integer) FROM post p WHERE p.dato_local_date >= ?1 AND p.dato_local_date <= ?2 " +
                "AND p.postklasse_enum = 0 AND p.normalposttype_enum != 2")  //Postklasse 0 = Normalpost, Normalposttype 2 = Utelates
        Integer sumUtFradatoTilDatoNormalposter(LocalDate fraOgMedLocalDate, LocalDate tilOgMedLocalDate);

        @NativeQuery(value = "SELECT sum(p.inn_paa_konto_integer) FROM post p WHERE p.dato_local_date >= ?1 AND p.dato_local_date <= ?2 " +
                "AND p.postklasse_enum = 0 AND p.normalposttype_enum != 2")  //Postklasse 0 = Normalpost, Normalposttype 2 = Utelates
        Integer sumInnFradatoTilDatoNormalposter(LocalDate fraOgMedLocalDate, LocalDate tilOgMedLocalDate);

}
