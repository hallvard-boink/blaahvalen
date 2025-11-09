package com.hallvardlaerum.regnskap.data;

import com.hallvardlaerum.grunndata.data.Kategori;
import com.hallvardlaerum.libs.database.AbstraktEntitet;
import com.hallvardlaerum.libs.database.EntitetMedForelderAktig;
import com.hallvardlaerum.libs.eksportimport.SkalEksporteres;
import com.hallvardlaerum.libs.felter.Datokyklop;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

import java.time.LocalDate;

@Entity
public class Post extends AbstraktEntitet implements EntitetMedForelderAktig<Kategori> {

    @SkalEksporteres
    private LocalDate datoLocalDate;

    @SkalEksporteres
    private String tekstFraBankenString;

    @SkalEksporteres
    private String meldingKIDFaktnrString;

    @SkalEksporteres
    @ManyToOne(targetEntity = Kategori.class)
    private Kategori kategori;

    @SkalEksporteres
    private String egenbeskrivelseString;

    //private Kostnadspakke kostnadspakke;

    @SkalEksporteres
    private Integer innPaaKontoInteger;

    @SkalEksporteres
    private Integer utFraKontoInteger;

    @SkalEksporteres
    private PosttypeEnum posttypeEnum; //enum

    @SkalEksporteres
    private PoststatusEnum poststatusEnum;

    @SkalEksporteres
    private String forelderPostUUID;

    @SkalEksporteres
    private String ekstraInfoString;

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

    @Override
    public String hentBeskrivendeNavn() {
        return Datokyklop.hent().formaterDato(datoLocalDate) + " " + tekstFraBankenString;
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

    public String getMeldingKIDFaktnrString() {
        return meldingKIDFaktnrString;
    }

    public void setMeldingKIDFaktnrString(String meldingKIDFaktnrString) {
        this.meldingKIDFaktnrString = meldingKIDFaktnrString;
    }

    public String getEgenbeskrivelseString() {
        return egenbeskrivelseString;
    }

    public void setEgenbeskrivelseString(String egenbeskrivelseString) {
        this.egenbeskrivelseString = egenbeskrivelseString;
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

    public PosttypeEnum getPosttypeEnum() {
        return posttypeEnum;
    }

    public void setPosttypeEnum(PosttypeEnum posttypeEnum) {
        this.posttypeEnum = posttypeEnum;
    }

    public PoststatusEnum getPoststatusEnum() {
        return poststatusEnum;
    }

    public void setPoststatusEnum(PoststatusEnum poststatusEnum) {
        this.poststatusEnum = poststatusEnum;
    }


    public String getEkstraInfoString() {
        return ekstraInfoString;
    }

    public void setEkstraInfoString(String ekstraInfoString) {
        this.ekstraInfoString = ekstraInfoString;
    }
}
