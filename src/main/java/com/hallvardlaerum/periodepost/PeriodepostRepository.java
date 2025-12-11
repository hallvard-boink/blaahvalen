package com.hallvardlaerum.periodepost;

import com.hallvardlaerum.grunndata.kategori.Kategori;
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


    @NativeQuery(value = "SELECT p.postklasse_enum, sum(p.inn_paa_konto_integer)+sum(p.ut_fra_konto_integer) " +
            "FROM post p LEFT JOIN kategori k ON p.kategori_uuid = k.uuid " +
            "WHERE p.dato_local_date >= ?1 AND " +
            "p.dato_local_date <= ?2 AND " +
            "p.normalposttype_enum != 2 AND " +
            "k.uuid = ?3"
    )  //Postklasse 0 = Normalpost, Normalposttype 2 = Utelates
    List<Tuple> sumPosterFradatoTilDatoKategori(LocalDate fraOgMedLocalDate, LocalDate tilOgMedLocalDate, UUID kategoriUUID);

    List<Periodepost> findByPeriodeAndKategori(Periode periode, Kategori kategori);



}
