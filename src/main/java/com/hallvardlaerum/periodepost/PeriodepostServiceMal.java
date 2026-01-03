package com.hallvardlaerum.periodepost;

import com.hallvardlaerum.kategori.Kategori;
import com.hallvardlaerum.libs.database.EntitetserviceMal;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.felter.HelTallMester;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeAktig;
import com.hallvardlaerum.periode.Periode;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import com.hallvardlaerum.post.PostklasseEnum;
import com.hallvardlaerum.post.normalpost.NormalpostService;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import jakarta.persistence.Tuple;

import java.math.BigDecimal;
import java.util.*;

public class PeriodepostServiceMal extends EntitetserviceMal<Periodepost, PeriodepostRepository> {
    private PeriodepostRepository periodepostRepository;
    private RedigeringsomraadeAktig<Periodepost> periodepostRedigeringsomraade;
    private PeriodepostTypeEnum periodepostTypeEnum;
    private NormalpostService normalpostService;

    public PeriodepostServiceMal() {

    }


    public void initPeriodepostServiceMal(
        RedigeringsomraadeAktig<Periodepost> periodepostRedigeringsomraade, PeriodepostTypeEnum periodepostTypeEnum)
    {
        this.periodepostRepository = Allvitekyklop.hent().getPeriodepostRepository();
        super.initEntitetserviceMal(Periodepost.class, periodepostRepository);
        this.periodepostRedigeringsomraade = periodepostRedigeringsomraade;
        this.periodepostTypeEnum = periodepostTypeEnum;

        normalpostService = Allvitekyklop.hent().getNormalpostService();
    }

    public void slettAlle(List<Periodepost> periodepostList) {
        periodepostRepository.deleteAll(periodepostList);
        periodepostRepository.flush();
    }

    public List<Periodepost> finnFraPeriodeOgKategori(Periode periode, Kategori kategori) {
        return periodepostRepository.findByPeriodeAndKategori(periode, kategori);
    }

    @Override
    public Periodepost opprettEntitet() {
        Periodepost periodepost = leggTilUUID(new Periodepost());
        periodepost.setPeriodepostTypeEnum(periodepostTypeEnum);
        return periodepost;
    }

    public void oppdaterOgLagreSummerForValgteVanligePeriodepost() {
        oppdaterOgLagreSummerForVanligePeriodeposter(periodepostRedigeringsomraade.getEntitet());
        periodepostRedigeringsomraade.lesBean();
        periodepostRedigeringsomraade.instansOppdaterEkstraRedigeringsfelter();

    }

    public void oppdaterOgLagreSummerForVanligePeriodeposter(Periodepost periodepost) {
        if (periodepost.getKategori()==null) {
            return;
        }
        List<Tuple> tuples = normalpostService.sumPosterFradatoTilDatoKategoritittel(
                periodepost.getPeriode().getDatoFraLocalDate(),
                periodepost.getPeriode().getDatoTilLocalDate(),
                periodepost.getKategori().getTittel());

        if (tuples==null) {
            periodepost.setSumBudsjettInteger(0);
            periodepost.setSumRegnskapInteger(0);

        } else {
            if (tuples.size()>2) {
                Loggekyklop.bruk().loggADVARSEL("Fant mer enn 2 tuple ved oppsummering av periodepost. Dette skal ikke skje, men fortsetter likevel.");
            }

            for (Tuple tuple:tuples) {
                oppdaterOgLagreSummerForVanligePeriodeposter_oppdaterSummer_tuple(periodepost,tuple);
            }

        }
        lagre(periodepost);
    }

    private void oppdaterOgLagreSummerForVanligePeriodeposter_oppdaterSummer_tuple(Periodepost periodepost, Tuple tuple) {
        Byte postklasseenumByte = tuple.get(0, Byte.class);
        BigDecimal sumBigDecimalInn = tuple.get(1, BigDecimal.class);
        BigDecimal sumBigDecimalUt = tuple.get(2, BigDecimal.class);
        if (postklasseenumByte==null && sumBigDecimalInn == null && sumBigDecimalUt == null) {
            return;
        }

        PostklasseEnum postklasseEnum = PostklasseEnum.konverterFraByte(postklasseenumByte);
        if (postklasseEnum==null) {
            Loggekyklop.hent().loggFEIL("Klarte ikke finne postklasseEnum fra tuple i oppdaterSummer_tuple" +
                    "Bytekode er " + postklasseenumByte + ", mens postklasseEnum er null ");
            return;
        }

        Integer sumInnInteger = HelTallMester.konverterBigdecimalTilInteger(sumBigDecimalInn);
        if (sumInnInteger==null) {
            sumInnInteger = 0;
        }
        Integer sumUtInteger = HelTallMester.konverterBigdecimalTilInteger(sumBigDecimalUt);
        if (sumUtInteger==null) {
            sumUtInteger = 0;
        }

        if (postklasseEnum==PostklasseEnum.NORMALPOST) {
            periodepost.setSumRegnskapInteger(sumInnInteger + sumUtInteger);
        } else if (postklasseEnum == PostklasseEnum.BUDSJETTPOST) {
            periodepost.setSumBudsjettInteger(sumInnInteger + sumUtInteger);
        }
    }


    public String presenterPeriodeposterMenIkkeVisPeriode(List<Periodepost> periodepostList) {
        if (periodepostList==null || periodepostList.isEmpty()) {
            return "(tom liste)";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("\n");
            for (int i = 0; i<periodepostList.size(); i++) {
                Periodepost periodepost = periodepostList.get(i);
                String kategori = "(kategori ikke satt)";
                if (periodepost.getKategori()!=null) {
                    kategori = periodepost.getKategori().getTittel();
                }
                sb.append(kategori).append(" ");
                if (periodepost.getSumBudsjettInteger()==null) {
                    sb.append("budsjett: null ");
                } else {
                    sb.append("budsjett: ").append(periodepost.getSumBudsjettInteger()).append(" ");
                }
                if (periodepost.getSumRegnskapInteger()==null) {
                    sb.append("regnskap: null ");
                } else {
                    sb.append("regnskap: ").append(periodepost.getSumRegnskapInteger()).append(" ");
                }
                if (i>0){sb.append("\n");}
            }
            return sb.toString();
        }
    }


    public List<Periodepost> finnHovedperiodeposter(Periode entitet) {
        return periodepostRepository.finnEtterPeriodeOgKategorinivaa(entitet.getUuid(), 0);
    }

    public List<Periodepost> finnEtterPeriode(Periode periode) {
        if (periode==null) {
            return new ArrayList<>();
        }

        if (periode.getPeriodetypeEnum()== PeriodetypeEnum.AARSOVERSIKT) {
            return periodepostRepository.findByPeriodepostTypeEnumAndPeriode(PeriodepostTypeEnum.PERIODEOVERSIKTPOST, periode);

        } else if (periode.getPeriodetypeEnum()==PeriodetypeEnum.MAANEDSOVERSIKT) {
            List<Tuple> tuples = periodepostRepository.finnOgOppsummerKostnadspakkerForDatospenn(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate());
            return finnKostnadspakker_konverterFraTuples(tuples);

        } else {
            List<Tuple> tuples = periodepostRepository.finnOgOppsummerKostnadspakkerForDatospenn(periode.getDatoFraLocalDate(), periode.getDatoTilLocalDate());
            return finnKostnadspakker_konverterFraTuples(tuples);
        }
    }

    /**
     * Her regner jeg med at første tuple inneholder uuid for kostnadspakke
     * @param tuples rådata fra native query
     * @return Liste med periodeposter
     */
    private ArrayList<Periodepost>finnKostnadspakker_konverterFraTuples(List<Tuple> tuples) {
        ArrayList<Periodepost> kostnadspakker = new ArrayList<>();
        if (tuples==null) {
            return kostnadspakker;
        }

        for (Tuple tuple:tuples) {
            UUID uuid = tuple.get(0, UUID.class);
            if (uuid!=null) {
                Optional<Periodepost> kostnadspakkeOptional = periodepostRepository.findById(uuid);
                kostnadspakkeOptional.ifPresent(kostnadspakker::add);
            }
        }
        return kostnadspakker;
    }
}
