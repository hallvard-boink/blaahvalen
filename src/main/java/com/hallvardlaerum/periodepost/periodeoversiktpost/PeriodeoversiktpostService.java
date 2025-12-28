 package com.hallvardlaerum.periodepost.periodeoversiktpost;
import com.hallvardlaerum.grunndata.kategori.Kategori;
import com.hallvardlaerum.libs.felter.Datokyklop;
import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
import com.hallvardlaerum.periodepost.Periodepost;
import com.hallvardlaerum.periodepost.PeriodepostRepository;
import com.hallvardlaerum.periodepost.PeriodepostServiceMal;
import com.hallvardlaerum.periodepost.PeriodepostTypeEnum;
import com.hallvardlaerum.post.Post;
import com.hallvardlaerum.post.normalpost.NormalpostService;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PeriodeoversiktpostService extends PeriodepostServiceMal implements InitieringsEgnet {
    private boolean erIntiert;
    private NormalpostService normalpostService;
    private PeriodepostRepository periodepostRepository;

    @Override
    public void init() {
        if (!erIntiert) {
            super.initPeriodepostServiceMal(
                Allvitekyklop.hent().getPeriodeoversiktpostRedigeringsomraade(),
                PeriodepostTypeEnum.PERIODEOVERSIKTPOST,
                Allvitekyklop.hent().getAarsoversiktService()
            );
            normalpostService = Allvitekyklop.hent().getNormalpostService();
            periodepostRepository = super.hentRepository();
            erIntiert = true;
        }
    }


    public PeriodeoversiktpostService() {
        super();
    }

    @Override
    public boolean erInitiert() {
        return erIntiert;
    }

    public void oppdaterSumUtgifterFraTilknyttedePoster(Periodepost kostnadspakke) {
        if (kostnadspakke==null) {
            return;
        }

        List<Post> postList = normalpostService.finnPosterIKostnadspakken(kostnadspakke);
        Integer sumRegnskap = postList.stream().mapToInt(p -> p.getUtFraKontoInteger()!=null? p.getUtFraKontoInteger() : 0).sum();
        kostnadspakke.setSumRegnskapInteger(sumRegnskap);

    }

    /**
     * Periodeoversiktspost og kostnadspakke er synonymer.
     * @return
     */
    public ArrayList<Periodepost> finnAlleKostnadspakker(){
        return new ArrayList<>(periodepostRepository.findByPeriodepostTypeEnumOrderByTittelStringDesc(PeriodepostTypeEnum.PERIODEOVERSIKTPOST));
    }

    public Periodepost finnEtterTittel(String kostnadspakketittelString) {
        return periodepostRepository.findByTittelString(kostnadspakketittelString);
    }

    public Periodepost finnFraPostOgKategori(Post post, Kategori kategori) {
        if (post==null) {
            return null;
        }



        LocalDate datoAaretsStart = Datokyklop.hent().finnFoersteIAaret(post.getDatoLocalDate());
        hentRepository().finnFraPeriodedatostartOgKategoritittel(datoAaretsStart, kategori.getTittel());

        return null;
    }
}
