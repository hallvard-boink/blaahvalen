package com.hallvardlaerum.post.budsjettpost;

import com.hallvardlaerum.grunndata.kategori.Kategori;
import com.hallvardlaerum.grunndata.kategori.KategoriService;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeAktig;
import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.periode.Periode;
import com.hallvardlaerum.post.Post;
import com.hallvardlaerum.post.PostServiceMal;
import com.hallvardlaerum.post.PostklasseEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BudsjettpostService extends PostServiceMal implements InitieringsEgnet {
    private boolean erInitiert = false;
    private KategoriService kategoriService;



    public List<Post> finnBudsjettposterFraPeriodeOgKategoriOgBudsjettstatus(Periode periode, Kategori kategori, BudsjettpoststatusEnum budsjettpoststatusEnum){
        return super.hentBudsjettPosterFradatoTilDatoKategoriBudsjettpoststatus(
            periode.getDatoFraLocalDate(),
            periode.getDatoTilLocalDate(),
            kategori,
            budsjettpoststatusEnum
        );
    }


    public BudsjettpostService() {
    }


    @Override
    public void init() {
        if (!erInitiert) {
            super.initPostServiceMal(PostklasseEnum.BUDSJETTPOST);

            this.kategoriService = Allvitekyklop.hent().getKategoriService();
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

    @Override
    public RedigeringsomraadeAktig<Post> hentRedigeringsomraadeAktig() {
        return null;
    }
}
