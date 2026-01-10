package com.hallvardlaerum.post.budsjettpost;

import com.hallvardlaerum.kategori.KategoriService;
import com.hallvardlaerum.libs.felter.HelTallMester;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeAktig;
import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.periode.Periode;
import com.hallvardlaerum.post.Post;
import com.hallvardlaerum.post.PostRepository;
import com.hallvardlaerum.post.PostServiceMal;
import com.hallvardlaerum.post.PostklasseEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import jakarta.persistence.Tuple;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class BudsjettpostService extends PostServiceMal implements InitieringsEgnet {
    private boolean erInitiert = false;
    private KategoriService kategoriService;
    private PostRepository postRepository;



    public List<Post> finnFraPeriodeOgBudsjettstatus(Periode periode, BudsjettpoststatusEnum budsjettpoststatusEnum) {
        return postRepository.findByDatoLocalDateBetweenAndBudsjettpoststatusEnumAndPostklasseEnumOrderByInnPaaKontoIntegerDescUtFraKontoIntegerDesc(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate(),budsjettpoststatusEnum, PostklasseEnum.BUDSJETTPOST);
    }


    public Integer sumInnEllerUtFradatoTildatoKategoritittel(LocalDate fraDato, LocalDate tilDato, String kategoritittel) {
        Tuple tuple = postRepository.sumBudsjettPosterFradatoTilDatoKategoritittel(fraDato, tilDato, kategoritittel);
        if (tuple==null) {
            return 0;
        }

        Integer innInteger = HelTallMester.konverterBigdecimalTilInteger(tuple.get(0, BigDecimal.class), true);
        Integer utInteger = HelTallMester.konverterBigdecimalTilInteger(tuple.get(1, BigDecimal.class), true);

        return innInteger + utInteger;
    }


     // === Standardmetoder ===
    public BudsjettpostService() {
    }


    @Override
    public void init() {
        if (!erInitiert) {
            super.initPostServiceMal(PostklasseEnum.BUDSJETTPOST);

            kategoriService = Allvitekyklop.hent().getKategoriService();
            postRepository = Allvitekyklop.hent().getPostRepository();

            erInitiert = true;
        }
    }

    @Override
    public boolean erInitiert() {
        return erInitiert;
    }

    @Override
    public Post opprettEntitetMedForelder() {
        return opprettEntitet();
    }

    @Override
    public Post opprettEntitet() {
        Post budsjettpost = leggTilUUID(new Post());
        budsjettpost.setPostklasseEnum(PostklasseEnum.BUDSJETTPOST);
        return budsjettpost;
    }

}
