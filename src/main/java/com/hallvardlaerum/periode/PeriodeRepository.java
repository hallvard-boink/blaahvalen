package com.hallvardlaerum.periode;

import com.hallvardlaerum.libs.database.RepositoryTillegg;
import jakarta.persistence.Tuple;
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


}
