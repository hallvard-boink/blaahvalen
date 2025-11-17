package com.hallvardlaerum.regnskap.service;

import com.hallvardlaerum.libs.database.RepositoryTillegg;
import com.hallvardlaerum.regnskap.data.NormalposttypeEnum;
import com.hallvardlaerum.felles.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID>, JpaSpecificationExecutor<Post>, RepositoryTillegg<Post> {
    List<Post> findByDatoLocalDateAndTekstFraBankenStringAndNormalposttypeEnum(LocalDate datoLocalDate, String tekstFraBankenString, NormalposttypeEnum normalposttypeEnum);

    //Window<Post> findFirst100ByTekstFraBankenStringOrderByDatoLocalDate(String tekst, OffsetScrollPosition position);
}
