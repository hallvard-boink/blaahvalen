package com.hallvardlaerum.kategori;

import com.hallvardlaerum.libs.database.RepositoryTillegg;
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
    List<Kategori> findAllByOrderByErAktivDescTittelAscUndertittelAsc();
    List<Kategori> findByKategoriType(KategoriType kategoriType);

    List<Kategori> findByTittelAndNivaaOrderByUndertittel(String tittel, Integer nivaa);

    List<Kategori> findByNivaaOrderByTittelAscUndertittelAsc(int nivaa);

    Kategori findByUndertittelAndNivaa(String undertittel, int nivaa);

    Optional<Kategori> findByTittelAndErOppsummerendeUnderkategori(String kategoriString, boolean erOppsummerendeunderkategori);

    List<Kategori> findByErOppsummerendeUnderkategoriOrderByTittelAscUndertittelAsc(boolean erOppsummerende);

    List<Kategori> findByKategoriTypeAndKategoriRetning(KategoriType kategoriType, KategoriRetning kategoriRetning);

//
//    @NativeQuery(
//            "SELECT " +
//                    "k.uuid, COUNT(p.uuid) " +
//                    "FROM " +
//                    "post p LEFT JOIN kategori k ON p.kategori_uuid  = k.uuid " +
//                    "WHERE " +
//                    "p.dato_local_date >= ?1 AND p.dato_local_date <= ?2 " +
//                    "AND p.postklasse_enum = ?3 AND p.normalposttype_enum!=2 " +
//                    "GROUP BY k.uuid ")
//    List<Tuple> hentKategorierDetFinnesPosterForFraDatoTilDatoPostklasse(LocalDate fraLocalDate, LocalDate tilLocalDate, PostklasseEnum postklasseEnum);
//
//
//    @NativeQuery("SELECT * FROM kategori " +
//            "WHERE tittel = ?1 AND kategori_type != ?2 " +
//            "ORDER BY tittel, undertittel")
//    List<Kategori> finnEtterTittelOgEkskludertKategoriType(String tittel, KategoriType kategoriTypeSomEkskluderes);
//
//
//    @NativeQuery("SELECT * FROM kategori " +
//            "WHERE kategori_type != ?1 " +
//            "ORDER BY tittel, undertittel")
//    List<Kategori> finnEtterEkskludertKategoriType(KategoriType kategoriTypeSomEkskluderes);
//
//    @NativeQuery("SELECT k.uuid, count(p.uuid), sum(p.inn_paa_konto_integer), sum(p.ut_fra_konto_integer) " +
//        "FROM kategori k RIGHT JOIN post p ON p.kategori_uuid = k.uuid " +
//        "WHERE p.dato_local_date >= ?1 AND p.dato_local_date <= ?2 " +
//        "AND p.postklasse_enum = 1 AND p.budsjettpoststatus_enum = ?3 " +
//        "GROUP BY k.uuid " +
//        "HAVING count(p.uuid)>0 " +
//        "ORDER BY sum(p.inn_paa_konto_integer) desc,sum(p.ut_fra_konto_integer) desc;")
//    List<Tuple> byggKategoriMedBudsjettpostList(LocalDate datoFra, LocalDate datoTil, Integer budsjettpoststatusEnumInteger);

    @NativeQuery(
            "SELECT " +
                    "k.* FROM post p LEFT JOIN kategori k ON p.kategori_uuid  = k.uuid " +
                    "WHERE " +
                    "p.dato_local_date >= ?1 AND p.dato_local_date <= ?2 " +
                    "GROUP BY " +
                    "k.uuid ")
    List<Kategori> finnKategorierDetFinnesPosterForFraDatoTilDato(LocalDate fraLocalDate, LocalDate tilLocalDate);

    @NativeQuery(
            "SELECT " +
                    "k2.uuid, k2.tittel " +
                    "FROM " +
                    "(" +
                    "SELECT " +
                    "k.tittel, count(p.uuid) " +
                    "FROM " +
                    "post p LEFT JOIN kategori k ON p.kategori_uuid  = k.uuid " +
                    "WHERE " +
                    "p.dato_local_date >=?1 AND p.dato_local_date <= ?2 AND k.kategori_type != 3 AND p.normalposttype_enum != 2 " +
                    "GROUP BY " +
                    "k.tittel" +
                    ") as kjerne " +
                    "LEFT JOIN kategori k2 ON kjerne.tittel = k2.tittel " +
                    "WHERE " +
                    "k2.nivaa =0;"
    )
    List<Tuple> finnHovedKategorierDetFinnesPosterForFraDatoTilDato(LocalDate fraLocalDate, LocalDate tilLocalDate);
    // kategori_type = 3: SKAL_IKKE_KATEGORISERES

}
