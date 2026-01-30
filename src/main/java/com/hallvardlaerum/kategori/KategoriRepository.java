package com.hallvardlaerum.kategori;

import com.hallvardlaerum.libs.database.RepositoryTillegg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.NativeQuery;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface KategoriRepository extends JpaRepository<Kategori, UUID>, JpaSpecificationExecutor<Kategori>, RepositoryTillegg<Kategori> {
    List<Kategori> findByTittel(String tittel);
    List<Kategori> findByTittelAndUndertittel(String tittel, String undertittel);

    List<Kategori> findAllByOrderByErAktivDescTittelAscUndertittelAsc();
    List<Kategori> findByKategoriType(KategoriType kategoriType);

    List<Kategori> findByTittelAndNivaaOrderByUndertittel(String tittel, Integer nivaa);

    List<Kategori> findByNivaaOrderByErAktivDescTittelAscUndertittelAsc(int nivaa);

    Kategori findByUndertittelAndNivaa(String undertittel, int nivaa);

    Optional<Kategori> findByTittelAndErOppsummerendeUnderkategori(String kategoriString, boolean erOppsummerendeunderkategori);

    List<Kategori> findByErOppsummerendeUnderkategoriOrderByTittelAscUndertittelAsc(boolean erOppsummerende);

    List<Kategori> findByKategoriTypeAndKategoriRetningAndNivaa(KategoriType kategoriType, KategoriRetning kategoriRetning, Integer nivaa);

    @NativeQuery(
            "SELECT k2.* " +
            "FROM " +
                "(" +
                    "SELECT k.tittel, count(p.uuid) " +
                    "FROM post p LEFT JOIN kategori k ON p.kategori_uuid  = k.uuid " +
                    "WHERE " +
                        "p.dato_local_date >=?1 AND p.dato_local_date <= ?2 AND k.kategori_type != 3 " +
                    "GROUP BY k.tittel" +
                ") as kjerne " +
                "LEFT JOIN kategori k2 ON kjerne.tittel = k2.tittel " +
            "WHERE " +
                "k2.nivaa =0;")
    List<Kategori> finnHovedKategorierDetFinnesPosterForFraDatoTilDato(LocalDate fraLocalDate, LocalDate tilLocalDate);

    List<Kategori> findAllByBrukesTilFastePosterAndErAktivOrderByTittelAscUndertittelAsc(boolean brukesTilFastePoster, boolean erAktiv);


}
