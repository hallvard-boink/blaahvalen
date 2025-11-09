package com.hallvardlaerum.regnskap.service;

import com.hallvardlaerum.libs.database.RepositoryTillegg;
import com.hallvardlaerum.regnskap.data.Post;
import com.hallvardlaerum.regnskap.data.PosttypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID>, JpaSpecificationExecutor<Post>, RepositoryTillegg<Post> {
    List<Post> findByDatoLocalDateAndTekstFraBankenStringAndPosttypeEnum(LocalDate datoLocalDate, String tekstFraBankenString, PosttypeEnum posttypeEnum);

    //Window<Post> findFirst100ByTekstFraBankenStringOrderByDatoLocalDate(String tekst, OffsetScrollPosition position);
}
