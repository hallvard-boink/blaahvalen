package com.hallvardlaerum.verktoy;

import com.hallvardlaerum.post.Post;
import com.hallvardlaerum.post.PostRepository;
import com.hallvardlaerum.post.normalpost.NormalposttypeEnum;

import java.util.List;

public class DataDoktor {

    public static void reparerNormalposterSomSkulleVaertUtelatt(){
        PostRepository postRepository = Allvitekyklop.hent().getPostRepository();
        List<Post> poster =  postRepository.finnPosterSomSkalKorrigeres_FeilNormalposttypeSelvOmKategoriErType4SkalIkkekategoriseres();
        for (Post post:poster) {
            post.setNormalposttypeEnum(NormalposttypeEnum.UTELATES); // 2
        }
        postRepository.saveAll(poster);
        postRepository.flush();
    }

}
