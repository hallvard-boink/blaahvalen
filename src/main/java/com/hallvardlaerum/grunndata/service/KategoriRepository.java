package com.hallvardlaerum.grunndata.service;

import com.hallvardlaerum.grunndata.data.Kategori;
import com.hallvardlaerum.libs.database.RepositoryTillegg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface KategoriRepository extends JpaRepository<Kategori, UUID>, JpaSpecificationExecutor<Kategori>, RepositoryTillegg<Kategori> {
    Optional<Kategori> findByTittel(String tittel);
}
