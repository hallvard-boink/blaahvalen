package com.hallvardlaerum.post;

import com.hallvardlaerum.libs.felter.DatopresisjonEnum;
import com.hallvardlaerum.periodepost.Periodepost;
import com.hallvardlaerum.post.budsjettpost.BudsjettpoststatusEnum;
import com.hallvardlaerum.post.budsjettpost.EstimatpresisjonEnum;
import com.hallvardlaerum.post.budsjettpost.PrioritetEnum;
import com.hallvardlaerum.kategori.Kategori;
import com.hallvardlaerum.libs.database.AbstraktEntitet;
import com.hallvardlaerum.libs.database.EntitetMedForelderAktig;
import com.hallvardlaerum.libs.eksportimport.SkalEksporteres;
import com.hallvardlaerum.libs.felter.Datokyklop;
import com.hallvardlaerum.post.normalpost.NormalpoststatusEnum;
import com.hallvardlaerum.post.normalpost.NormalposttypeEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

import java.time.LocalDate;

@Entity
public class Post extends AbstraktEntitet implements EntitetMedForelderAktig<Kategori> {

    // Felter som er felles for normalposter og budsjettposter
    @SkalEksporteres
    private PostklasseEnum postklasseEnum;

    @SkalEksporteres
    private LocalDate datoLocalDate;

    @SkalEksporteres
    @Column(length = 1000)
    private String beskrivelseString;

    @SkalEksporteres
    @ManyToOne(targetEntity = Kategori.class)
    private Kategori kategori;

    @SkalEksporteres
    private Integer innPaaKontoInteger;

    @SkalEksporteres
    private Integer utFraKontoInteger;

    @SkalEksporteres
    @ManyToOne(targetEntity = Periodepost.class)
    private Periodepost kostnadsPakke;


    // Felter som er unike for normalposter

    @SkalEksporteres
    @Column(length = 1000)
    private String tekstFraBankenString;  // Skal inkludere meldingKIDFaktnrString i regnearket

    @SkalEksporteres
    private NormalposttypeEnum normalposttypeEnum; //enum

    @SkalEksporteres
    private NormalpoststatusEnum normalpoststatusEnum;

    @SkalEksporteres
    private String forelderPostUUID; //Brukes for å håndtere forholdet mellom delposter og hovedpost

    @SkalEksporteres
    @Column(length = 1000)
    private String ekstraInfoString;


    // Felter som er unike for budsjettposter
    @SkalEksporteres
    private BudsjettpoststatusEnum budsjettpoststatusEnum;

    @SkalEksporteres
    private EstimatpresisjonEnum estimatpresisjonEnum;

    @SkalEksporteres
    private DatopresisjonEnum datopresisjonEnum;

    @SkalEksporteres
    private PrioritetEnum prioritetEnum;

    @SkalEksporteres
    Integer rekkefoelgeInteger;

    @SkalEksporteres
    Boolean erRegelmessigBoolean;


    // === Getters and Setters ===


    public Periodepost getKostnadsPakke() {
        return kostnadsPakke;
    }

    public void setKostnadsPakke(Periodepost kostnadsPakke) {
        this.kostnadsPakke = kostnadsPakke;
    }

    public DatopresisjonEnum getDatopresisjonEnum() {
        return datopresisjonEnum;
    }

    public void setDatopresisjonEnum(DatopresisjonEnum datopresisjonEnum) {
        this.datopresisjonEnum = datopresisjonEnum;
    }

    public String getUuidString(){
        return getUuid().toString();
    }

    public void setUuidStringFake(String uuid){
        //Do nothing
    }

    @Override
    public String hentBeskrivendeNavn() {
        if (postklasseEnum==null) {
            return "Post av ukjent klasse";
        } else if (postklasseEnum==PostklasseEnum.BUDSJETTPOST) {
            StringBuilder sb = new StringBuilder();
            sb.append(Datokyklop.hent().formaterDato(datoLocalDate)).append(" ");
            if (kategori!=null) {
                sb.append(kategori).append(": ");
            } else {
                sb.append("(ukjent kategori): ");
            }
            if (innPaaKontoInteger!=null && innPaaKontoInteger>0) {
                sb.append(innPaaKontoInteger);
            }
            if (utFraKontoInteger!=null && utFraKontoInteger>0) {
                sb.append(utFraKontoInteger);
            }

            return sb.toString();
        } else {
            StringBuilder sb = new StringBuilder();
            if (datoLocalDate!=null) {
                sb.append(Datokyklop.hent().formaterDato(datoLocalDate)).append(" ");
            }
            if (tekstFraBankenString!=null) {
                sb.append(tekstFraBankenString);
            }
            if (innPaaKontoInteger!=null && innPaaKontoInteger>0) {
                sb.append(innPaaKontoInteger);
            }

            if (utFraKontoInteger!=null && utFraKontoInteger>0) {
                sb.append(utFraKontoInteger);
            }

            return sb.toString();
        }
    }

    @Override
    public String toString(){
        if (getUuid()!=null) {
            return getUuidString() + ": " + hentBeskrivendeNavn();
        } else {
            return "(ingen uuid ennå): " + hentBeskrivendeNavn();
        }
    }

    public Kategori getKategori() {
        return kategori;
    }

    public void setKategori(Kategori kategori) {
        this.kategori = kategori;
    }

    public String getForelderPostUUID() {
        return forelderPostUUID;
    }

    public void setForelderPostUUID(String forelderPostUUID) {
        this.forelderPostUUID = forelderPostUUID;
    }

    @Override
    public void setForelder(Kategori forelder) {
        kategori = forelder;
    }

    @Override
    public Kategori getForelder() {
        return kategori;
    }

    public PostklasseEnum getPostklasseEnum() {
        return postklasseEnum;
    }

    public void setPostklasseEnum(PostklasseEnum postklasseEnum) {
        this.postklasseEnum = postklasseEnum;
    }

    public LocalDate getDatoLocalDate() {
        return datoLocalDate;
    }

    public void setDatoLocalDate(LocalDate datoLocalDate) {
        this.datoLocalDate = datoLocalDate;
    }

    public String getTekstFraBankenString() {
        return tekstFraBankenString;
    }

    public void setTekstFraBankenString(String tekstFraBankenString) {
        this.tekstFraBankenString = tekstFraBankenString;
    }


    public String getBeskrivelseString() {
        return beskrivelseString;
    }

    public void setBeskrivelseString(String beskrivelseString) {
        this.beskrivelseString = beskrivelseString;
    }

    public Integer getInnPaaKontoInteger() {
        return innPaaKontoInteger;
    }

    public void setInnPaaKontoInteger(Integer innPaaKontoInteger) {
        this.innPaaKontoInteger = innPaaKontoInteger;
    }

    public Integer getUtFraKontoInteger() {
        return utFraKontoInteger;
    }

    public void setUtFraKontoInteger(Integer utFraKontoInteger) {
        this.utFraKontoInteger = utFraKontoInteger;
    }

    public NormalposttypeEnum getNormalPosttypeEnum() {
        return normalposttypeEnum;
    }

    public void setNormalPosttypeEnum(NormalposttypeEnum normalposttypeEnum) {
        this.normalposttypeEnum = normalposttypeEnum;
    }

    public NormalpoststatusEnum getNormalPoststatusEnum() {
        return normalpoststatusEnum;
    }

    public void setNormalPoststatusEnum(NormalpoststatusEnum normalpoststatusEnum) {
        this.normalpoststatusEnum = normalpoststatusEnum;
    }

    public String getEkstraInfoString() {
        return ekstraInfoString;
    }

    public void setEkstraInfoString(String ekstraInfoString) {
        this.ekstraInfoString = ekstraInfoString;
    }

    public NormalposttypeEnum getNormalposttypeEnum() {
        return normalposttypeEnum;
    }

    public void setNormalposttypeEnum(NormalposttypeEnum normalposttypeEnum) {
        this.normalposttypeEnum = normalposttypeEnum;
    }

    public NormalpoststatusEnum getNormalpoststatusEnum() {
        return normalpoststatusEnum;
    }

    public void setNormalpoststatusEnum(NormalpoststatusEnum normalpoststatusEnum) {
        this.normalpoststatusEnum = normalpoststatusEnum;
    }

    public BudsjettpoststatusEnum getBudsjettpoststatusEnum() {
        return budsjettpoststatusEnum;
    }

    public void setBudsjettpoststatusEnum(BudsjettpoststatusEnum budsjettpoststatusEnum) {
        this.budsjettpoststatusEnum = budsjettpoststatusEnum;
    }

    public EstimatpresisjonEnum getEstimatpresisjonEnum() {
        return estimatpresisjonEnum;
    }

    public void setEstimatpresisjonEnum(EstimatpresisjonEnum estimatpresisjonEnum) {
        this.estimatpresisjonEnum = estimatpresisjonEnum;
    }

    public PrioritetEnum getPrioritetEnum() {
        return prioritetEnum;
    }

    public void setPrioritetEnum(PrioritetEnum prioritetEnum) {
        this.prioritetEnum = prioritetEnum;
    }

    public Integer getRekkefoelgeInteger() {
        return rekkefoelgeInteger;
    }

    public void setRekkefoelgeInteger(Integer rekkefoelgeInteger) {
        this.rekkefoelgeInteger = rekkefoelgeInteger;
    }

    public Boolean getErRegelmessigBoolean() {
        return erRegelmessigBoolean;
    }

    public void setErRegelmessigBoolean(Boolean erRegelmessigBoolean) {
        this.erRegelmessigBoolean = erRegelmessigBoolean;
    }

}
