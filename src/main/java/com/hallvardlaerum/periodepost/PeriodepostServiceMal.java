package com.hallvardlaerum.periodepost;

import com.hallvardlaerum.kategori.Kategori;
import com.hallvardlaerum.kategori.KategoriRetning;
import com.hallvardlaerum.kategori.KategoriType;
import com.hallvardlaerum.libs.database.EntitetserviceMal;
import com.hallvardlaerum.libs.feiloglogging.Loggekyklop;
import com.hallvardlaerum.libs.ui.RedigeringsomraadeAktig;
import com.hallvardlaerum.periode.Periode;
import com.hallvardlaerum.periode.PeriodetypeEnum;
import com.hallvardlaerum.post.budsjettpost.BudsjettpostService;
import com.hallvardlaerum.post.normalpost.NormalpostService;
import com.hallvardlaerum.verktoy.Allvitekyklop;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PeriodepostServiceMal extends EntitetserviceMal<Periodepost, PeriodepostRepository> {
    private PeriodepostRepository periodepostRepository;
    private RedigeringsomraadeAktig<Periodepost> periodepostRedigeringsomraade;
    private PeriodepostTypeEnum periodepostTypeEnum;
    private NormalpostService normalpostService;
    private BudsjettpostService budsjettpostService;


// ===========================
// === Constructor og Init ===
//region Constructor og Init

    public PeriodepostServiceMal() {

    }


    public void initPeriodepostServiceMal(
            RedigeringsomraadeAktig<Periodepost> periodepostRedigeringsomraade, PeriodepostTypeEnum periodepostTypeEnum) {
        this.periodepostRepository = Allvitekyklop.hent().getPeriodepostRepository();
        super.initEntitetserviceMal(Periodepost.class, periodepostRepository);
        this.periodepostRedigeringsomraade = periodepostRedigeringsomraade;
        this.periodepostTypeEnum = periodepostTypeEnum;

        normalpostService = Allvitekyklop.hent().getNormalpostService();
        budsjettpostService = Allvitekyklop.hent().getBudsjettpostService();
    }
//endregion


// ============================
// === Slette periodeposter ===
// region Slette flere

    public void slettAlle(List<Periodepost> periodepostList) {
        periodepostRepository.deleteAll(periodepostList);
        periodepostRepository.flush();
    }


    public void slettUkategorisertForPeriode(Periode periode, KategoriRetning kategoriRetning) {
        if (periode == null || kategoriRetning == null) {
            return;
        }

        //Kategori ukategorisertKategori = Allvitekyklop.hent().getKategoriService().finnEllerOpprettKategoriUKATEGORISERT(kategoriRetning);
        Kategori ukategorisertKategori = Allvitekyklop.hent().getKategoriService()
                .finnEllerOpprettFraKategoriTypeOgKategoriretningOgNivaa(KategoriType.UKATEGORISERT, kategoriRetning, 0);

        Periodepost ukategorisertPeriodepost = finnEllerOpprettOgOppdaterPeriodepostUkategorisert(periode, ukategorisertKategori);
        if (ukategorisertPeriodepost != null) {
            slett(ukategorisertPeriodepost);
        }
    }

//endregion


// ==========================
// === Finne periodeposter ===
// region Finne periodeposter

    public List<Periodepost> finnPeriodeposterEtterKategori(Kategori fjernesKategori) {
        return periodepostRepository.findByKategori(fjernesKategori);
    }


    public Periodepost finnStandardFraPeriodeOgKategori(Periode periode, Kategori kategori) {
        if (kategori.getNivaa() != 0) {
            kategori = Allvitekyklop.hent().getKategoriService().finnHovedKategoriEtterTittel(kategori.getTittel());
        }

        List<Periodepost> periodeposter = periodepostRepository.findByPeriodeAndKategori(periode, kategori);
        if (periodeposter.isEmpty()) {
            return null;
        } else {
            if (periodeposter.size() > 1) {
                Loggekyklop.bruk().loggADVARSEL("Fant mer enn en standard periodepost i perioden " + periode.hentBeskrivendeNavn() + " med kategorien " + kategori.hentBeskrivendeNavn());
            }
            return periodeposter.getFirst();
        }
    }


    public List<Periodepost> finnHovedperiodeposter(Periode entitet) {
        return periodepostRepository.finnEtterPeriodeOgKategorinivaa(entitet.getUuid(), 0);
    }

    public List<Periodepost> finnAlleTyperEtterPeriode(Periode periode) {
        if (periode == null) {
            return new ArrayList<>();
        }

        if (periode.getPeriodetypeEnum() == PeriodetypeEnum.AARSOVERSIKT) {
            return periodepostRepository.findByPeriodepostTypeEnumAndPeriode(PeriodepostTypeEnum.AARSOVERSIKTPOST, periode);

        } else if (periode.getPeriodetypeEnum() == PeriodetypeEnum.MAANEDSOVERSIKT) {
            return periodepostRepository.findByPeriodepostTypeEnumAndPeriode(PeriodepostTypeEnum.AARSOVERSIKTPOST, periode);

        } else {
            Loggekyklop.bruk().loggFEIL("Hverken Årsoversikt eller Månedsoversikt");
            return new ArrayList<>();
        }
    }

//endregion


// ==========================================
// === Finne og evt. opprette periodepost ===
// region Finne og evt. opprette periodepost


    public Periodepost finnEllerOpprettOgOppdaterPeriodepostUkategorisert(Periode periode, Kategori kategoriUkategorisert) {

        Periodepost periodepostUkategorisert = finnPeriodepostUkategorisert(periode, kategoriUkategorisert);
        if (periodepostUkategorisert == null) {
            periodepostUkategorisert = opprettEntitet();
            periodepostUkategorisert.setKategori(kategoriUkategorisert);
            periodepostUkategorisert.setPeriode(periode);
            lagre(periodepostUkategorisert);
        }

        if (periodepostUkategorisert.getKategori().getKategoriRetning() == KategoriRetning.INN) {
            periodepostUkategorisert.setSumRegnskapInteger(normalpostService.sumInnPeriodeNormalposterUtenkategori(periode));
        } else if (periodepostUkategorisert.getKategori().getKategoriRetning() == KategoriRetning.UT) {
            periodepostUkategorisert.setSumRegnskapInteger(normalpostService.sumUtPeriodeNormalposterUtenkategori(periode));
        } else {
            Loggekyklop.bruk().loggFEIL("UkategorisertKategori har ikke riktig retning");
        }
        return periodepostUkategorisert;

    }

    public Periodepost finnPeriodepostUkategorisert(Periode periode, Kategori kategoriUkategorisert) {
        if (periode == null || kategoriUkategorisert == null) {
            Loggekyklop.bruk().loggFEIL("Periode eller kategoriUkategorisert er null, avbryter");
            return null;
        }

        List<Periodepost> periodepostList = periodepostRepository.findByPeriodeAndKategori(periode, kategoriUkategorisert);
        if (periodepostList.isEmpty()) {
            return null;
        } else {
            if (periodepostList.size() > 1) {
                Loggekyklop.bruk().loggINFO("Fant for mange periodeposter for periode " + periode.hentBeskrivendeNavn() + " og kategori " + kategoriUkategorisert.hentBeskrivendeNavn());
            }
            return periodepostList.getFirst();
        }
    }

//endregion


// ==========================
// === CRUD ===
// region CRUD og import

    @Override
    public Periodepost opprettEntitet() {
        Periodepost periodepost = leggTilUUID(new Periodepost());
        periodepost.setPeriodepostTypeEnum(periodepostTypeEnum);
        return periodepost;
    }

    @Override
    public boolean behandleSpesialfeltVedImport(Object entitet, Field field, String nyVerdi, String importradString) {
        if (field.getName().equals("kategori")) {
            behandleSpesialfeltVedImport_importerKategori(entitet,field,nyVerdi, importradString);
            return true;
        } else if (field.getName().equals("periode")) {
            behandleSpesialfeltVedImport_importerPeriode(entitet,field,nyVerdi,importradString);
            return true;
        } else {
            return false;
        }
    }


    private void behandleSpesialfeltVedImport_importerKategori(Object entitet, Field field, String nyVerdi, String importradString) {
        Kategori kategori = Allvitekyklop.hent().getKategoriService().finnEtterUUID(nyVerdi);
        if (kategori!=null) {
            try {
                field.set(entitet,kategori);
            } catch (IllegalAccessException e) {
                Loggekyklop.bruk().loggADVARSEL("Fant kategori, men klarte ikke å sette den: " + kategori.hentBeskrivendeNavn() + ". Hele raden:" + importradString);
            }
        } else {
            Loggekyklop.bruk().loggADVARSEL("Fant ikke kategori med UUID " + nyVerdi + ". Hele raden:" + importradString);
        }
    }

    private void behandleSpesialfeltVedImport_importerPeriode(Object entitet, Field field, String nyVerdi, String importradString) {
        Periode periode = Allvitekyklop.hent().getMaanedsoversiktService().finnEtterUUID(nyVerdi); //klarer å håndtere årsoversikter og
        if (periode!=null) {
            try {
                field.set(entitet,periode);
            } catch (IllegalAccessException e) {
                Loggekyklop.bruk().loggADVARSEL("Fant periode, men klarte ikke å sette den: " + periode.hentBeskrivendeNavn() + ". Hele raden:" + importradString);
            }
        } else {
            Loggekyklop.bruk().loggADVARSEL("Fant ikke periode med UUID " + nyVerdi + ". Hele raden:" + importradString);
        }
    }

//endregion


// =======================
// === Oppdatere summer ===
// region Oppdater summer

    public void oppdaterOgLagreSummerForValgteVanligePeriodepost() {
        oppdaterOgLagreSummerForVanligePeriodeposter(periodepostRedigeringsomraade.getEntitet());
        periodepostRedigeringsomraade.lesBean();
        periodepostRedigeringsomraade.instansOppdaterEkstraRedigeringsfelter();

    }

    public void oppdaterOgLagreSummerForVanligePeriodeposter(Periodepost periodepost) {
        if (periodepost.getKategori() == null) {
            return;
        }
        LocalDate fraLocalDate = periodepost.getPeriode().getDatoFraLocalDate();
        LocalDate tilLocalDate = periodepost.getPeriode().getDatoTilLocalDate();
        String kategoriTittel = periodepost.getKategori().getTittel();

        periodepost.setSumRegnskapInteger(normalpostService.sumInnEllerUtFradatoTildatoKategoritittel(fraLocalDate, tilLocalDate, kategoriTittel));
        periodepost.setSumBudsjettInteger(budsjettpostService.sumInnEllerUtFradatoTildatoKategoritittel(fraLocalDate, tilLocalDate, kategoriTittel));

        lagre(periodepost);
    }

//endregion


// ====================================
// === Lage tekst fra periodeposter ===
// region Lage tekst fra periodeposter

    public String presenterPeriodeposterMenIkkeVisPeriode(List<Periodepost> periodepostList) {
        if (periodepostList == null || periodepostList.isEmpty()) {
            return "(tom liste)";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("\n");
            for (int i = 0; i < periodepostList.size(); i++) {
                Periodepost periodepost = periodepostList.get(i);
                String kategoriString = "(kategori ikke satt)";
                if (periodepost.getKategori() != null) {
                    kategoriString = periodepost.getKategori().getTittel();
                }
                sb.append(kategoriString).append(" ");

                if (periodepost.getSumBudsjettInteger() == null) {
                    sb.append("budsjett: null ");
                } else {
                    sb.append("budsjett: ").append(periodepost.getSumBudsjettInteger()).append(" ");
                }
                if (periodepost.getSumRegnskapInteger() == null) {
                    sb.append("regnskap: null ");
                } else {
                    sb.append("regnskap: ").append(periodepost.getSumRegnskapInteger()).append(" ");
                }
                if (i > 0) {
                    sb.append("\n");
                }
            }
            return sb.toString();
        }
    }

//endregion


}
