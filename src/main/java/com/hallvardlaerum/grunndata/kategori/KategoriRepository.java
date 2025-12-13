package com.hallvardlaerum.grunndata.kategori;

import com.hallvardlaerum.libs.database.RepositoryTillegg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface KategoriRepository extends JpaRepository<Kategori, UUID>, JpaSpecificationExecutor<Kategori>, RepositoryTillegg<Kategori> {
    Optional<Kategori> findByTittel(String tittel);
    List<Kategori> findAllByOrderByTittelAscUndertittelAsc();
}
