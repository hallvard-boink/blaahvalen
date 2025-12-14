package com.hallvardlaerum.verktoy;

import com.hallvardlaerum.grunndata.kategori.KategoriRedigeringsomraade;
import com.hallvardlaerum.grunndata.kategori.KategoriRepository;
import com.hallvardlaerum.grunndata.kategori.KategoriService;
import com.hallvardlaerum.grunndata.kategori.KategoriView;
import com.hallvardlaerum.periode.PeriodeRepository;
import com.hallvardlaerum.periode.aarsoversikt.AarsoversiktRedigeringsomraade;
import com.hallvardlaerum.periode.aarsoversikt.AarsoversiktService;
import com.hallvardlaerum.periode.aarsoversikt.AarsoversiktView;
import com.hallvardlaerum.periode.maanedsoversikt.MaanedsoversiktRedigeringsomraade;
import com.hallvardlaerum.periode.maanedsoversikt.MaanedsoversiktService;
import com.hallvardlaerum.periode.maanedsoversikt.MaanedsoversiktView;
import com.hallvardlaerum.periodepost.PeriodepostRepository;
import com.hallvardlaerum.periodepost.aarsoversiktpost.AarsoversiktpostRedigeringsomraade;
import com.hallvardlaerum.periodepost.aarsoversiktpost.AarsoversiktpostService;
import com.hallvardlaerum.periodepost.aarsoversiktpost.AarsoversiktpostView;
import com.hallvardlaerum.periodepost.maanedsoversiktpost.MaanedsoversiktpostRedigeringsomraade;
import com.hallvardlaerum.periodepost.maanedsoversiktpost.MaanedsoversiktpostService;
import com.hallvardlaerum.periodepost.maanedsoversiktpost.MaanedsoversiktpostView;
import com.hallvardlaerum.post.PostRepository;
import com.hallvardlaerum.post.PostServiceMal;
import com.hallvardlaerum.post.budsjettpost.BudsjettpostRedigeringsomraade;
import com.hallvardlaerum.post.budsjettpost.BudsjettpostService;
import com.hallvardlaerum.post.budsjettpost.BudsjettpostView;
import com.hallvardlaerum.post.normalpost.NormalpostRedigeringsomraade;
import com.hallvardlaerum.post.normalpost.NormalpostService;
import com.hallvardlaerum.post.normalpost.NormalpostView;

public class Allvitekyklop {
    private static Allvitekyklop allvitekyklop;

    private KategoriRepository kategoriRepository;
    private KategoriService kategoriService;
    private KategoriRedigeringsomraade kategoriRedigeringsomraade;
    private KategoriView kategoriView;

    private PostRepository postRepository;
    private PeriodeRepository periodeRepository;
    private PeriodepostRepository periodepostRepository;

    private NormalpostService normalpostService;
    private NormalpostRedigeringsomraade normalpostRedigeringsomraade;
    private NormalpostView normalpostView;

    private BudsjettpostService budsjettpostService;
    private BudsjettpostRedigeringsomraade budsjettpostRedigeringsomraade;
    private BudsjettpostView budsjettpostView;

    private MaanedsoversiktService maanedsoversiktService;
    private MaanedsoversiktRedigeringsomraade maanedsoversiktRedigeringsomraade;
    private MaanedsoversiktView maanedsoversiktView;

    private MaanedsoversiktpostService maanedsoversiktpostService;
    private MaanedsoversiktpostRedigeringsomraade maanedsoversiktpostRedigeringsomraade;
    private MaanedsoversiktpostRedigeringsomraade maanedsoversiktpostRedigeringsomraadeTilDialog;
    private MaanedsoversiktpostView maanedsoversiktpostView;

    private AarsoversiktService aarsoversiktService;
    private AarsoversiktRedigeringsomraade aarsoversiktRedigeringsomraade;
    private AarsoversiktView aarsoversiktView;

    private AarsoversiktpostService aarsoversiktpostService;
    private AarsoversiktpostRedigeringsomraade aarsoversiktpostRedigeringsomraade;
    private AarsoversiktpostRedigeringsomraade aarsoversiktpostRedigeringsomraadeTilDialog;
    private AarsoversiktpostView aarsoversiktpostView;



    private VerktoyView verktoyView;

    /**
     * Obs! Initiering av Views gjøres når de opprettes av Spring boot.
     */
    public void initierAlleObjekter(){
        // Initierer de som andre er avhengig av først, dvs. de uten forelder.


        kategoriService.init();
        kategoriRedigeringsomraade.init();

        aarsoversiktService.init();
        aarsoversiktRedigeringsomraade.init();

        maanedsoversiktService.init();
        maanedsoversiktRedigeringsomraade.init();

        aarsoversiktpostService.initier();
        aarsoversiktpostRedigeringsomraade.init();
        aarsoversiktpostRedigeringsomraadeTilDialog.init();

        maanedsoversiktpostService.init();
        maanedsoversiktpostRedigeringsomraade.init();
        maanedsoversiktpostRedigeringsomraadeTilDialog.init();

        normalpostService.init();
        normalpostRedigeringsomraade.init();

        budsjettpostService.init();
        budsjettpostRedigeringsomraade.init();


    }


    public KategoriRepository getKategoriRepository() {
        return kategoriRepository;
    }

    public void setKategoriRepository(KategoriRepository kategoriRepository) {
        this.kategoriRepository = kategoriRepository;
    }

    public KategoriService getKategoriService() {
        return kategoriService;
    }

    public void setKategoriService(KategoriService kategoriService) {
        this.kategoriService = kategoriService;
    }

    public KategoriRedigeringsomraade getKategoriRedigeringsomraade() {
        return kategoriRedigeringsomraade;
    }

    public void setKategoriRedigeringsomraade(KategoriRedigeringsomraade kategoriRedigeringsomraade) {
        this.kategoriRedigeringsomraade = kategoriRedigeringsomraade;
    }

    public KategoriView getKategoriView() {
        return kategoriView;
    }

    public void setKategoriView(KategoriView kategoriView) {
        this.kategoriView = kategoriView;
    }

    public PostRepository getPostRepository() {
        return postRepository;
    }

    public void setPostRepository(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public NormalpostService getNormalpostService() {
        return normalpostService;
    }

    public void setNormalpostService(NormalpostService normalpostService) {
        this.normalpostService = normalpostService;
    }

    public NormalpostRedigeringsomraade getNormalpostRedigeringsomraade() {
        return normalpostRedigeringsomraade;
    }

    public void setNormalpostRedigeringsomraade(NormalpostRedigeringsomraade normalpostRedigeringsomraade) {
        this.normalpostRedigeringsomraade = normalpostRedigeringsomraade;
    }

    public NormalpostView getNormalpostView() {
        return normalpostView;
    }

    public void setNormalpostView(NormalpostView normalpostView) {
        this.normalpostView = normalpostView;
    }

    public PeriodeRepository getPeriodeRepository() {
        return periodeRepository;
    }

    public void setPeriodeRepository(PeriodeRepository periodeRepository) {
        this.periodeRepository = periodeRepository;
    }

    public PeriodepostRepository getPeriodepostRepository() {
        return periodepostRepository;
    }

    public void setPeriodepostRepository(PeriodepostRepository periodepostRepository) {
        this.periodepostRepository = periodepostRepository;
    }

    public MaanedsoversiktService getMaanedsoversiktService() {
        return maanedsoversiktService;
    }

    public void setMaanedsoversiktService(MaanedsoversiktService maanedsoversiktService) {
        this.maanedsoversiktService = maanedsoversiktService;
    }

    public MaanedsoversiktRedigeringsomraade getMaanedsoversiktRedigeringsomraade() {
        return maanedsoversiktRedigeringsomraade;
    }

    public void setMaanedsoversiktRedigeringsomraade(MaanedsoversiktRedigeringsomraade maanedsoversiktRedigeringsomraade) {
        this.maanedsoversiktRedigeringsomraade = maanedsoversiktRedigeringsomraade;
    }

    public MaanedsoversiktView getMaanedsoversiktView() {
        return maanedsoversiktView;
    }

    public void setMaanedsoversiktView(MaanedsoversiktView maanedsoversiktView) {
        this.maanedsoversiktView = maanedsoversiktView;
    }

    public MaanedsoversiktpostService getMaanedsoversiktpostService() {
        return maanedsoversiktpostService;
    }

    public void setMaanedsoversiktpostService(MaanedsoversiktpostService maanedsoversiktpostService) {
        this.maanedsoversiktpostService = maanedsoversiktpostService;
    }

    public MaanedsoversiktpostRedigeringsomraade getMaanedsoversiktpostRedigeringsomraade() {
        return maanedsoversiktpostRedigeringsomraade;
    }

    public void setMaanedsoversiktpostRedigeringsomraade(MaanedsoversiktpostRedigeringsomraade maanedsoversiktpostRedigeringsomraade) {
        this.maanedsoversiktpostRedigeringsomraade = maanedsoversiktpostRedigeringsomraade;
    }

    public MaanedsoversiktpostRedigeringsomraade getMaanedsoversiktpostRedigeringsomraadeTilDialog() {
        return maanedsoversiktpostRedigeringsomraadeTilDialog;
    }

    public void setMaanedsoversiktpostRedigeringsomraadeTilDialog(MaanedsoversiktpostRedigeringsomraade maanedsoversiktpostRedigeringsomraadeTilDialog) {
        this.maanedsoversiktpostRedigeringsomraadeTilDialog = maanedsoversiktpostRedigeringsomraadeTilDialog;
    }

    public MaanedsoversiktpostView getMaanedsoversiktpostView() {
        return maanedsoversiktpostView;
    }

    public void setMaanedsoversiktpostView(MaanedsoversiktpostView maanedsoversiktpostView) {
        this.maanedsoversiktpostView = maanedsoversiktpostView;
    }

    public AarsoversiktService getAarsoversiktService() {
        return aarsoversiktService;
    }

    public void setAarsoversiktService(AarsoversiktService aarsoversiktService) {
        this.aarsoversiktService = aarsoversiktService;
    }

    public AarsoversiktRedigeringsomraade getAarsoversiktRedigeringsomraade() {
        return aarsoversiktRedigeringsomraade;
    }

    public void setAarsoversiktRedigeringsomraade(AarsoversiktRedigeringsomraade aarsoversiktRedigeringsomraade) {
        this.aarsoversiktRedigeringsomraade = aarsoversiktRedigeringsomraade;
    }

    public AarsoversiktView getAarsoversiktView() {
        return aarsoversiktView;
    }

    public void setAarsoversiktView(AarsoversiktView aarsoversiktView) {
        this.aarsoversiktView = aarsoversiktView;
    }

    public AarsoversiktpostService getAarsoversiktpostService() {
        return aarsoversiktpostService;
    }

    public void setAarsoversiktpostService(AarsoversiktpostService aarsoversiktpostService) {
        this.aarsoversiktpostService = aarsoversiktpostService;
    }

    public BudsjettpostService getBudsjettpostService() {
        return budsjettpostService;
    }

    public void setBudsjettpostService(BudsjettpostService budsjettpostService) {
        this.budsjettpostService = budsjettpostService;
    }

    public BudsjettpostRedigeringsomraade getBudsjettpostRedigeringsomraade() {
        return budsjettpostRedigeringsomraade;
    }

    public void setBudsjettpostRedigeringsomraade(BudsjettpostRedigeringsomraade budsjettpostRedigeringsomraade) {
        this.budsjettpostRedigeringsomraade = budsjettpostRedigeringsomraade;
    }

    public BudsjettpostView getBudsjettpostView() {
        return budsjettpostView;
    }

    public AarsoversiktpostRedigeringsomraade getAarsoversiktpostRedigeringsomraade() {
        return aarsoversiktpostRedigeringsomraade;
    }

    public void setAarsoversiktpostRedigeringsomraade(AarsoversiktpostRedigeringsomraade aarsoversiktpostRedigeringsomraade) {
        this.aarsoversiktpostRedigeringsomraade = aarsoversiktpostRedigeringsomraade;
    }

    public AarsoversiktpostRedigeringsomraade getAarsoversiktpostRedigeringsomraadeTilDialog() {
        return aarsoversiktpostRedigeringsomraadeTilDialog;
    }

    public void setAarsoversiktpostRedigeringsomraadeTilDialog(AarsoversiktpostRedigeringsomraade aarsoversiktpostRedigeringsomraadeTilDialog) {
        this.aarsoversiktpostRedigeringsomraadeTilDialog = aarsoversiktpostRedigeringsomraadeTilDialog;
    }

    public AarsoversiktpostView getAarsoversiktpostView() {
        return aarsoversiktpostView;
    }

    public void setAarsoversiktpostView(AarsoversiktpostView aarsoversiktpostView) {
        this.aarsoversiktpostView = aarsoversiktpostView;
    }

    public VerktoyView getVerktoyView() {
        return verktoyView;
    }

    public void setVerktoyView(VerktoyView verktoyView) {
        this.verktoyView = verktoyView;
    }


    /*
    public VerktoyView getVerktoyView() {
        if (!verktoyView.erInitiert()) {
            verktoyView.init();
        }
        return verktoyView;
    }


    public KategoriRepository getKategoriRepository() {
        return kategoriRepository;
    }

    public void setKategoriRepository(KategoriRepository kategoriRepository) {
        this.kategoriRepository = kategoriRepository;
    }

    public KategoriService getKategoriService() {
        if (!kategoriService.erInitiert()) {
            kategoriService.init();
        }
        return kategoriService;
    }

    public void setKategoriService(KategoriService kategoriService) {
        this.kategoriService = kategoriService;
    }

    public KategoriRedigeringsomraade getKategoriRedigeringsomraade() {
        if (!kategoriRedigeringsomraade.erInitiert()) {
            kategoriRedigeringsomraade.init();
        }
        return kategoriRedigeringsomraade;
    }

    public void setKategoriRedigeringsomraade(KategoriRedigeringsomraade kategoriRedigeringsomraade) {
        this.kategoriRedigeringsomraade = kategoriRedigeringsomraade;
    }

    public KategoriView getKategoriView() {
        if (!kategoriView.erInitiert()) {
            kategoriView.init();
        }
        return kategoriView;
    }

    public void setKategoriView(KategoriView kategoriView) {
        this.kategoriView = kategoriView;
    }

    public PostRepository getPostRepository() {
        return postRepository;
    }

    public void setPostRepository(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public PostService getPostService() {
        if (!postService.erInitiert()) {
            postService.init();
        }

        return postService;
    }

    public void setPostService(PostService postService) {
        this.postService = postService;
    }

    public NormalpostRedigeringsomraade getNormalpostRedigeringsomraade() {
        if (!normalpostRedigeringsomraade.erInitiert()) {
            normalpostRedigeringsomraade.init();
        }
        return normalpostRedigeringsomraade;
    }

    public void setNormalpostRedigeringsomraade(NormalpostRedigeringsomraade normalpostRedigeringsomraade) {
        this.normalpostRedigeringsomraade = normalpostRedigeringsomraade;
    }

    public NormalpostView getNormalpostView() {
        if (!normalpostView.erInitiert()) {
            normalpostView.init();
        }
        return normalpostView;
    }

    public void setNormalpostView(NormalpostView normalpostView) {
        this.normalpostView = normalpostView;
    }

    public PeriodeRepository getPeriodeRepository() {
        return periodeRepository;
    }

    public void setPeriodeRepository(PeriodeRepository periodeRepository) {
        this.periodeRepository = periodeRepository;
    }

    public PeriodepostRepository getPeriodepostRepository() {
        return periodepostRepository;
    }

    public void setPeriodepostRepository(PeriodepostRepository periodepostRepository) {
        this.periodepostRepository = periodepostRepository;
    }

    public MaanedsoversiktService getMaanedsoversiktService() {
        if (!maanedsoversiktService.erInitiert()) {
            maanedsoversiktService.init();
        }
        return maanedsoversiktService;
    }

    public void setMaanedsoversiktService(MaanedsoversiktService maanedsoversiktService) {
        this.maanedsoversiktService = maanedsoversiktService;
    }

    public MaanedsoversiktRedigeringsomraade getMaanedsoversiktRedigeringsomraade() {
        if (!maanedsoversiktRedigeringsomraade.erInitiert()) {
            maanedsoversiktRedigeringsomraade.init();
        }
        return maanedsoversiktRedigeringsomraade;
    }

    public void setMaanedsoversiktRedigeringsomraade(MaanedsoversiktRedigeringsomraade maanedsoversiktRedigeringsomraade) {
        this.maanedsoversiktRedigeringsomraade = maanedsoversiktRedigeringsomraade;
    }

    public MaanedsoversiktView getMaanedsoversiktView() {
        if (!maanedsoversiktView.erInitiert()) {
            maanedsoversiktView.init();
        }
        return maanedsoversiktView;
    }

    public void setMaanedsoversiktView(MaanedsoversiktView maanedsoversiktView) {
        this.maanedsoversiktView = maanedsoversiktView;
    }

    public MaanedsoversiktpostService getMaanedsoversiktpostService() {
        if (!maanedsoversiktpostService.erInitiert()) {
            maanedsoversiktpostService.init();
        }
        return maanedsoversiktpostService;
    }

    public void setMaanedsoversiktpostService(MaanedsoversiktpostService maanedsoversiktpostService) {
        this.maanedsoversiktpostService = maanedsoversiktpostService;
    }

    public MaanedsoversiktpostRedigeringsomraade getMaanedsoversiktpostRedigeringsomraade() {
        if (!maanedsoversiktpostRedigeringsomraade.erInitiert()) {
            maanedsoversiktRedigeringsomraade.init();
        }
        return maanedsoversiktpostRedigeringsomraade;
    }

    public void setMaanedsoversiktpostRedigeringsomraade(MaanedsoversiktpostRedigeringsomraade maanedsoversiktpostRedigeringsomraade) {
        this.maanedsoversiktpostRedigeringsomraade = maanedsoversiktpostRedigeringsomraade;
    }

    public MaanedsoversiktpostRedigeringsomraade getMaanedsoversiktpostRedigeringsomraadeTilDialog() {
        if (!maanedsoversiktpostRedigeringsomraadeTilDialog.erInitiert()) {
            maanedsoversiktpostRedigeringsomraadeTilDialog.init();
        }
        return maanedsoversiktpostRedigeringsomraadeTilDialog;
    }

    public void setMaanedsoversiktpostRedigeringsomraadeTilDialog(MaanedsoversiktpostRedigeringsomraade maanedsoversiktpostRedigeringsomraadeTilDialog) {
        this.maanedsoversiktpostRedigeringsomraadeTilDialog = maanedsoversiktpostRedigeringsomraadeTilDialog;
    }

    public MaanedsoversiktpostView getMaanedsoversiktpostView() {
        if (!maanedsoversiktpostView.erInitiert()) {
            maanedsoversiktpostView.init();
        }
        return maanedsoversiktpostView;
    }

    public void setMaanedsoversiktpostView(MaanedsoversiktpostView maanedsoversiktpostView) {
        this.maanedsoversiktpostView = maanedsoversiktpostView;
    }

    public AarsoversiktService getAarsoversiktService() {
        if (!aarsoversiktService.erInitiert()) {
            aarsoversiktService.init();
        }

        return aarsoversiktService;
    }

    public void setAarsoversiktService(AarsoversiktService aarsoversiktService) {
        this.aarsoversiktService = aarsoversiktService;
    }

    public AarsoversiktRedigeringsomraade getAarsoversiktRedigeringsomraade() {
        if (!aarsoversiktRedigeringsomraade.erInitiert()) {
            aarsoversiktRedigeringsomraade.init();
        }
        return aarsoversiktRedigeringsomraade;
    }

    public void setAarsoversiktRedigeringsomraade(AarsoversiktRedigeringsomraade aarsoversiktRedigeringsomraade) {
        this.aarsoversiktRedigeringsomraade = aarsoversiktRedigeringsomraade;
    }

    public AarsoversiktView getAarsoversiktView() {

        if (!aarsoversiktView.erInitiert()) {
            aarsoversiktView.init();
        }
        return aarsoversiktView;
    }

    public void setAarsoversiktView(AarsoversiktView aarsoversiktView) {
        this.aarsoversiktView = aarsoversiktView;
    }

    public AarsoversiktpostService getAarsoversiktpostService() {
        if (!aarsoversiktpostService.erInitiert()) {
            aarsoversiktpostService.init();
        }
        return aarsoversiktpostService;
    }

    public void setAarsoversiktpostService(AarsoversiktpostService aarsoversiktpostService) {
        this.aarsoversiktpostService = aarsoversiktpostService;
    }

    public AarsoversiktpostRedigeringsomraade getAarsoversiktpostRedigeringsomraade() {
        if (!aarsoversiktpostRedigeringsomraade.erInitiert()) {
            aarsoversiktpostRedigeringsomraade.init();
        }
        return aarsoversiktpostRedigeringsomraade;
    }

    public void setAarsoversiktpostRedigeringsomraade(AarsoversiktpostRedigeringsomraade aarsoversiktpostRedigeringsomraade) {
        this.aarsoversiktpostRedigeringsomraade = aarsoversiktpostRedigeringsomraade;
    }

    public AarsoversiktpostRedigeringsomraade getAarsoversiktpostRedigeringsomraadeTilDialog() {
        if (!aarsoversiktpostRedigeringsomraadeTilDialog.erInitiert()) {
            aarsoversiktpostRedigeringsomraadeTilDialog.init();
        }
        return aarsoversiktpostRedigeringsomraadeTilDialog;
    }

    public void setAarsoversiktpostRedigeringsomraadeTilDialog(AarsoversiktpostRedigeringsomraade aarsoversiktpostRedigeringsomraadeTilDialog) {
        this.aarsoversiktpostRedigeringsomraadeTilDialog = aarsoversiktpostRedigeringsomraadeTilDialog;
    }

    public AarsoversiktpostView getAarsoversiktpostView() {
        if (!aarsoversiktpostView.erInitiert()) {
            aarsoversiktpostView.init();
        }
        return aarsoversiktpostView;
    }

    public void setAarsoversiktpostView(AarsoversiktpostView aarsoversiktpostView) {
        this.aarsoversiktpostView = aarsoversiktpostView;
    }

    public void setVerktoyView(VerktoyView verktoyView) {
        this.verktoyView = verktoyView;
    }

*/

    public static Allvitekyklop hent(){
        if (allvitekyklop == null) {
            allvitekyklop = new Allvitekyklop();
        }
        return allvitekyklop;
    }



    private Allvitekyklop() {
    }


    public void setBudsjettpostView(BudsjettpostView budsjettpostView) {

    }
}
