package com.hallvardlaerum.periodepost;

import com.hallvardlaerum.kategori.Kategori;
import com.hallvardlaerum.kategori.KategoriRetning;
import com.hallvardlaerum.libs.database.EntitetserviceMal;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeAktig;
import com.hallvardlaerum.periode.Periode;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import com.hallvardlaerum.post.budsjettpost.BudsjettpostService;
import com.hallvardlaerum.post.normalpost.NormalpostService;
import com.hallvardlaerum.verktoy.Allvitekyklop;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PeriodepostServiceMal extends EntitetserviceMal<Periodepost, PeriodepostRepository> {
    private PeriodepostRepository periodepostRepository;
    private RedigeringsomraadeAktig<Periodepost> periodepostRedigeringsomraade;
    private PeriodepostTypeEnum periodepostTypeEnum;
    private NormalpostService normalpostService;
    private BudsjettpostService budsjettpostService;

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
        budsjettpostService = Allvitekyklop.hent().getBudsjettpostService();
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
        LocalDate fraLocalDate = periodepost.getPeriode().getDatoFraLocalDate();
        LocalDate tilLocalDate = periodepost.getForelder().getDatoTilLocalDate();
        String kategoriTittel = periodepost.getKategori().getTittel();

        periodepost.setSumRegnskapInteger(normalpostService.sumInnEllerUtFradatoTildatoKategoritittel(fraLocalDate, tilLocalDate, kategoriTittel));
        periodepost.setSumBudsjettInteger(budsjettpostService.sumInnEllerUtFradatoTildatoKategoritittel(fraLocalDate, tilLocalDate, kategoriTittel));

        lagre(periodepost);
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
            return periodepostRepository.findByPeriodepostTypeEnumAndPeriode(PeriodepostTypeEnum.AARSOVERSIKTPOST, periode);

        } else if (periode.getPeriodetypeEnum()== PeriodetypeEnum.MAANEDSOVERSIKT) {
            return periodepostRepository.findByPeriodepostTypeEnumAndPeriode(PeriodepostTypeEnum.AARSOVERSIKTPOST, periode);

        } else {
            Loggekyklop.bruk().loggFEIL("Hverken Årsoversikt eller Månedsoversikt");
            return new ArrayList<>();
        }
    }


    public Periodepost finnEllerOpprettPeriodepostUkategorisert(Periode periode, Kategori kategoriUkategorisert) {
        if (periode == null || kategoriUkategorisert==null) {
            Loggekyklop.bruk().loggFEIL("Periode eller kategoriUkategorisert er null, avbryter");
            return null;
        }

        List<Periodepost> periodepostList = periodepostRepository.findByPeriodeAndKategori(periode, kategoriUkategorisert);
        Periodepost periodepostUkategorisert;
        if (periodepostList.isEmpty()) {
            periodepostUkategorisert = opprettEntitet();
            periodepostUkategorisert.setKategori(kategoriUkategorisert);
            periodepostUkategorisert.setPeriode(periode);
            lagre(periodepostUkategorisert);

        } else {
            if (periodepostList.size()>1) {
                Loggekyklop.bruk().loggINFO("Fant for mange periodeposter for periode " + periode.hentBeskrivendeNavn() + " og kategori " + kategoriUkategorisert.hentBeskrivendeNavn());
            }
            periodepostUkategorisert = periodepostList.getFirst();
        }

        if (periodepostUkategorisert.getKategori().getKategoriRetning()== KategoriRetning.INN) {
            periodepostUkategorisert.setSumRegnskapInteger(normalpostService.sumInnPeriodeNormalposterUtenkategori(periode));
        } else if (periodepostUkategorisert.getKategori().getKategoriRetning()==KategoriRetning.UT) {
            periodepostUkategorisert.setSumRegnskapInteger(normalpostService.sumUtPeriodeNormalposterUtenkategori(periode));
        } else {
            Loggekyklop.bruk().loggFEIL("UkategorisertKategori har ikke riktig retning");
        }
        return periodepostUkategorisert;

    }
}
