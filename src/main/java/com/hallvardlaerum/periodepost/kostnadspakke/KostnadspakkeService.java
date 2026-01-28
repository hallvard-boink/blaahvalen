 package com.hallvardlaerum.periodepost.kostnadspakke;

 import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
 import com.hallvardlaerum.libs.felter.HelTallMester;
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

 import java.math.BigDecimal;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.UUID;

 @Service
public class KostnadspakkeService extends PeriodepostServiceMal implements InitieringsEgnet {
    private boolean erInitiert;
    private NormalpostService normalpostService;
    private PeriodepostRepository periodepostRepository;

    @Override
    public void init() {
        if (!erInitiert) {
            super.initPeriodepostServiceMal(
                Allvitekyklop.hent().getKostnadspakkeRedigeringsomraade(),
                PeriodepostTypeEnum.PERIODEOVERSIKTPOST
            );
            normalpostService = Allvitekyklop.hent().getNormalpostService();
            periodepostRepository = Allvitekyklop.hent().getPeriodepostRepository();
            erInitiert = true;
        }
    }


    public KostnadspakkeService() {
        super();
    }

    @Override
    public boolean erInitiert() {
        return erInitiert;
    }

     public void oppdaterAlleKostnadspakker() {
         Loggekyklop.bruk().loggINFO("Oppdaterer kostnadspakker...");
         List<Periodepost> kostnadspakkeList = periodepostRepository.findByPeriodepostTypeEnum(PeriodepostTypeEnum.PERIODEOVERSIKTPOST);
         for (Periodepost kostnadspakke:kostnadspakkeList) {
             oppdaterSumUtgifterFraTilknyttedePoster(kostnadspakke);
             lagre(kostnadspakke);
         }
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

     /**
      * Denne brukes av AarsoversiktRedigeringsomraade sin kostnadspakketab
      * @param periode periode
      * @return liste av periodepost
      */
    public List<Periodepost> hentKostnadspakkerForPerioden(Periode periode) {
        return periodepostRepository.findByPeriodepostTypeEnumAndPeriode(PeriodepostTypeEnum.PERIODEOVERSIKTPOST, periode);
    }

    /**
     * Denne brukes av MaanedsoversiktRedigeringsomraade sin KostnadspakkeTab
     */
    public ArrayList<PeriodedelAvKostnadspakkeRad> hentKostnadspakkerForPeriodenMedPeriodensSum(Periode periode) {
        List<Tuple> tupleList = finnKostnadspakkeUUIDogSummerForPeriode(periode);
        ArrayList<PeriodedelAvKostnadspakkeRad> periodedelAvKostnadspakkeRadArrayList = new ArrayList<>();

        for (Tuple tuple : tupleList) {
            String kostnadspakkeUUIDString = tuple.get(0, UUID.class).toString();
            if (kostnadspakkeUUIDString == null) {
                break;
            }
            Periodepost kostnadspakke = finnEtterUUID(kostnadspakkeUUIDString);
            if (kostnadspakke != null) {
                BigDecimal sumInnBigDecimal = tuple.get(1, BigDecimal.class);
                Integer sumInnInteger = 0;
                if (sumInnBigDecimal != null) {
                    sumInnInteger = HelTallMester.konverterBigdecimalTilInteger(sumInnBigDecimal);
                }

                BigDecimal sumUtBigDecimal = tuple.get(2, BigDecimal.class);
                Integer sumUtInteger = 0;
                if (sumUtBigDecimal != null) {
                    sumUtInteger = HelTallMester.konverterBigdecimalTilInteger(sumUtBigDecimal);
                }

                periodedelAvKostnadspakkeRadArrayList.add(new PeriodedelAvKostnadspakkeRad(kostnadspakke, sumInnInteger + sumUtInteger));
            }
        }
        return periodedelAvKostnadspakkeRadArrayList;
    }


     public void slettAlleKostnadspakker() {
         List<Periodepost> kostnadspakker = hentRepository().findByPeriodepostTypeEnum(PeriodepostTypeEnum.PERIODEOVERSIKTPOST);
         hentRepository().deleteAll(kostnadspakker);
     }


 }
