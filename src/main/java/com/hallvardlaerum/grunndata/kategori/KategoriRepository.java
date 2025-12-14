package com.hallvardlaerum.grunndata.kategori;

import com.hallvardlaerum.libs.database.RepositoryTillegg;
import com.hallvardlaerum.post.PostklasseEnum;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.NativeQuery;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface KategoriRepository extends JpaRepository<Kategori, UUID>, JpaSpecificationExecutor<Kategori>, RepositoryTillegg<Kategori> {
    Optional<Kategori> findByTittel(String tittel);
    Optional<Kategori> findByTittelAndUndertittel(String tittel, String undertittel);
    List<Kategori> findAllByOrderByTittelAscUndertittelAsc();
    List<Kategori> findByKategoriType(KategoriType kategoriType);


    @NativeQuery("SELECT * FROM kategori " +
            "WHERE tittel = ?1 AND kategori_type != ?2")
    List<Kategori> finnEtterTittelOgEkskludertKategoriType(String tittel, KategoriType kategoriTypeSomEkskluderes);

}
