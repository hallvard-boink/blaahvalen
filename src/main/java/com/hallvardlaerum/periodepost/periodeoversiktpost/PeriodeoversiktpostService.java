 package com.hallvardlaerum.periodepost.periodeoversiktpost;

 import com.hallvardlaerum.libs.verktoy.InitieringsEgnet;
 import com.hallvardlaerum.periode.Periode;
 import com.hallvardlaerum.periodepost.Periodepost;
 import com.hallvardlaerum.periodepost.PeriodepostRepository;
 import com.hallvardlaerum.periodepost.PeriodepostServiceMal;
 import com.hallvardlaerum.periodepost.PeriodepostTypeEnum;
 import com.hallvardlaerum.post.Post;
 import com.hallvardlaerum.post.normalpost.NormalpostService;
 import com.hallvardlaerum.verktoy.Allvitekyklop;

 import jakarta.persistence.Tuple;
 import org.springframework.stereotype.Service;
 import java.util.ArrayList;
 import java.util.List;

@Service
public class PeriodeoversiktpostService extends PeriodepostServiceMal implements InitieringsEgnet {
    private boolean erInitiert;
    private NormalpostService normalpostService;
    private PeriodepostRepository periodepostRepository;

    @Override
    public void init() {
        if (!erInitiert) {
            super.initPeriodepostServiceMal(
                Allvitekyklop.hent().getPeriodeoversiktpostRedigeringsomraade(),
                PeriodepostTypeEnum.PERIODEOVERSIKTPOST,
                Allvitekyklop.hent().getAarsoversiktService()
            );
            normalpostService = Allvitekyklop.hent().getNormalpostService();
            periodepostRepository = super.hentRepository();
            erInitiert = true;
        }
    }


    public PeriodeoversiktpostService() {
        super();
    }

    @Override
    public boolean erInitiert() {
        return erInitiert;
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
     * @return liste av kostnadspakker
     */
    public ArrayList<Periodepost> finnAlleKostnadspakker(){
        return new ArrayList<>(periodepostRepository.findByPeriodepostTypeEnumOrderByTittelStringDesc(PeriodepostTypeEnum.PERIODEOVERSIKTPOST));
    }

    public Periodepost finnEtterTittel(String kostnadspakketittelString) {
        return periodepostRepository.findByTittelString(kostnadspakketittelString);
    }


    public List<Tuple> finnKostnadspakkeUUIDogSummerForPeriode(Periode periode) {
        if (periode==null) {
            return new ArrayList<>();
        }

        return periodepostRepository.finnOgOppsummerKostnadspakkerForDatospenn(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate());

    }
}
