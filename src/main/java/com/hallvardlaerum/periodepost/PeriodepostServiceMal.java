package com.hallvardlaerum.periodepost;

import com.hallvardlaerum.grunndata.kategori.Kategori;
import com.hallvardlaerum.libs.database.EntitetserviceMal;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.felter.DesimalMester;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeAktig;
import com.hallvardlaerum.periode.Periode;
import com.hallvardlaerum.periode.PeriodeServiceMal;
import com.hallvardlaerum.post.PostklasseEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;
import jakarta.persistence.Tuple;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PeriodepostServiceMal extends EntitetserviceMal<Periodepost, PeriodepostRepository> {
    private PeriodepostRepository periodepostRepository;
    private RedigeringsomraadeAktig<Periodepost> periodepostRedigeringsomraade;
    private PeriodepostTypeEnum periodepostTypeEnum;
    private PeriodeServiceMal periodeService;

    public PeriodepostServiceMal() {

    }

    public void initPeriodepostServiceMal(RedigeringsomraadeAktig<Periodepost> periodepostRedigeringsomraade,
                                          PeriodepostTypeEnum periodepostTypeEnum,
                                          PeriodeServiceMal periodeService) {
        this.periodepostRepository = Allvitekyklop.hent().getPeriodepostRepository();
        super.initEntitetserviceMal(Periodepost.class, periodepostRepository);
        this.periodeService = periodeService;
        this.periodepostRedigeringsomraade = periodepostRedigeringsomraade;
        this.periodepostTypeEnum = periodepostTypeEnum;
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

    @Override
    public RedigeringsomraadeAktig<Periodepost> hentRedigeringsomraadeAktig() {
        return periodepostRedigeringsomraade;
    }

    public void oppdaterSummer() {
        oppdaterSummer(periodepostRedigeringsomraade.getEntitet());
        hentRedigeringsomraadeAktig().lesBean();
        hentRedigeringsomraadeAktig().instansOppdaterEkstraRedigeringsfelter();

    }

    public void oppdaterSummer(Periodepost periodepost) {
        if (periodepost.getKategori()==null) {
            return;
        }
        List<Tuple> tuples = periodepostRepository.sumPosterFradatoTilDatoKategoritittel(
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
                oppdaterSummer_tuple(periodepost,tuple);
            }

        }
        lagre(periodepost);
    }

    private void oppdaterSummer_tuple(Periodepost periodepost, Tuple tuple) {

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

        Integer sumInnInteger = DesimalMester.konverterBigdecimalTilInteger(sumBigDecimalInn);
        if (sumInnInteger==null) {
            sumInnInteger = 0;
        }
        Integer sumUtInteger = DesimalMester.konverterBigdecimalTilInteger(sumBigDecimalUt);
        if (sumUtInteger==null) {
            sumUtInteger = 0;
        }

        if (postklasseEnum==PostklasseEnum.NORMALPOST) {
            periodepost.setSumRegnskapInteger(sumInnInteger + sumUtInteger);
        } else if (postklasseEnum == PostklasseEnum.BUDSJETTPOST) {
            periodepost.setSumBudsjettInteger(sumInnInteger + + sumUtInteger);
        }

    }


    public String presenterPeriodeposterMenIkkeVisPeriode(List<Periodepost> periodepostList) {
        if (periodepostList==null || periodepostList.isEmpty()) {
            return "(tom liste)";
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i<periodepostList.size(); i++) {
                Periodepost periodepost = periodepostList.get(i);
                String kategori = "(kategori ikke satt)";
                if (periodepost.getKategori()!=null) {
                    kategori = periodepost.getKategori().getTittel();
                }
                sb.append(kategori).append(" ");
                if (periodepost.getSumBudsjettInteger()==null) {
                    sb.append("b:null ");
                } else {
                    sb.append("b:").append(periodepost.getSumBudsjettInteger());
                }
                if (periodepost.getSumRegnskapInteger()==null) {
                    sb.append("r:null ");
                } else {
                    sb.append("r:").append(periodepost.getSumRegnskapInteger());
                }
                if (i>0){sb.append(", ");}
            }
            return sb.toString();
        }
    }


    public List<Periodepost> finnHovedperiodeposter(Periode entitet) {
        return hentRepository().finnEtterPeriodeOgKategorinivaa(entitet.getUuid(), 0);
    }
}
