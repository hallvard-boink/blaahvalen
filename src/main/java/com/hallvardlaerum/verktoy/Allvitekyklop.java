package com.hallvardlaerum.verktoy;

import com.hallvardlaerum.kategori.KategoriRedigeringsomraade;
import com.hallvardlaerum.kategori.KategoriRepository;
import com.hallvardlaerum.kategori.KategoriService;
import com.hallvardlaerum.kategori.KategoriView;
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
import com.hallvardlaerum.periodepost.kostnadspakke.KostnadspakkeView;
import com.hallvardlaerum.periodepost.kostnadspakke.KostnadspakkeRedigeringsomraade;
import com.hallvardlaerum.periodepost.maanedsoversiktpost.MaanedsoversiktpostRedigeringsomraade;
import com.hallvardlaerum.periodepost.maanedsoversiktpost.MaanedsoversiktpostService;
import com.hallvardlaerum.periodepost.maanedsoversiktpost.MaanedsoversiktpostView;
import com.hallvardlaerum.periodepost.kostnadspakke.KostnadspakkeService;
import com.hallvardlaerum.post.PostRepository;
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
    private MaanedsoversiktpostView maanedsoversiktpostView;

    private AarsoversiktService aarsoversiktService;
    private AarsoversiktRedigeringsomraade aarsoversiktRedigeringsomraade;
    private AarsoversiktView aarsoversiktView;

    private AarsoversiktpostService aarsoversiktpostService;
    private AarsoversiktpostRedigeringsomraade aarsoversiktpostRedigeringsomraade;
    private AarsoversiktpostView aarsoversiktpostView;

    private KostnadspakkeService kostnadspakkeService;
    private KostnadspakkeRedigeringsomraade kostnadspakkeRedigeringsomraade;
    private KostnadspakkeView kostnadspakkeView;

    private VerktoyView verktoyView;


    /**
     * Obs! Initiering av Views gjøres når de opprettes av Spring boot.
     */
    public void initierAlleObjekter(){
        // Initierer de som andre er avhengig av først, dvs. de uten forelder.


        kategoriService.init();
        aarsoversiktService.init();
        maanedsoversiktService.init();
        aarsoversiktpostService.initier();
        maanedsoversiktpostService.init();
        kostnadspakkeService.init();  //periodeoversiktpost
        budsjettpostService.init();
        normalpostService.init();

        maanedsoversiktpostRedigeringsomraade.init();

        kategoriRedigeringsomraade.init();
        aarsoversiktRedigeringsomraade.init();
        maanedsoversiktRedigeringsomraade.init();

        aarsoversiktpostRedigeringsomraade.init();
        kostnadspakkeRedigeringsomraade.init();  //periodeoversiktpost
        budsjettpostRedigeringsomraade.init();
        normalpostRedigeringsomraade.init();


        //aarsoversiktpostRedigeringsomraadeTilDialogFraAarsoversiktView.init();
        //maanedsoversiktpostRedigeringsomraadeTilDialogFraMaanedsoversiktView.init();
        //budsjettpostRedigeringsomraadeTilDialogFraMaanedsoversiktView.init();
        //kostnadspakkeRedigeringsomraadeTilDialogFraAarsoversiktView.init();
        //normalpostRedigeringsomraadeTilDialogFraMaanedsoversiktpostView.init();

    }



    public KostnadspakkeView getKostnadspakkeView() {
        return kostnadspakkeView;
    }

    public void setKostnadspakkeView(KostnadspakkeView kostnadspakkeView) {
        this.kostnadspakkeView = kostnadspakkeView;
    }

    public KostnadspakkeRedigeringsomraade getKostnadspakkeRedigeringsomraade() {
        return kostnadspakkeRedigeringsomraade;
    }

    public void setKostnadspakkeRedigeringsomraade(KostnadspakkeRedigeringsomraade kostnadspakkeRedigeringsomraade) {
        this.kostnadspakkeRedigeringsomraade = kostnadspakkeRedigeringsomraade;
    }

    public KostnadspakkeService getKostnadspakkeService() {
        return kostnadspakkeService;
    }

    public void setKostnadspakkeService(KostnadspakkeService kostnadspakkeService) {
        this.kostnadspakkeService = kostnadspakkeService;
    }

    public KostnadspakkeView getPeriodeoversiktpostView() {
        return kostnadspakkeView;
    }

    public void setPeriodeoversiktpostView(KostnadspakkeView kostnadspakkeView) {
        this.kostnadspakkeView = kostnadspakkeView;
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




    public static Allvitekyklop hent(){
        if (allvitekyklop == null) {
            allvitekyklop = new Allvitekyklop();
        }
        return allvitekyklop;
    }



    private Allvitekyklop() {
    }


    public void setBudsjettpostView(BudsjettpostView budsjettpostView) {
        this.budsjettpostView = budsjettpostView;
    }


}
