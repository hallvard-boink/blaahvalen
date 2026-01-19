package com.hallvardlaerum.periode;

import com.hallvardlaerum.libs.database.AbstraktEntitet;
import com.hallvardlaerum.libs.eksportimport.SkalEksporteres;
import com.hallvardlaerum.libs.felter.Datokyklop;
import com.hallvardlaerum.periodepost.Periodepost;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Periode extends AbstraktEntitet {

    @SkalEksporteres
    private PeriodetypeEnum periodetypeEnum;

    @SkalEksporteres
    private LocalDate datoFraLocalDate;

    @SkalEksporteres
    private LocalDate datoTilLocalDate;

    @SkalEksporteres
    @Column(length = 2000)
    private String beskrivelseString;

    @SkalEksporteres
    private Integer sumBudsjettInntektInteger;

    @SkalEksporteres
    private Integer sumBudsjettUtgifterInteger;

    @SkalEksporteres
    private Integer sumBudsjettResultatInteger;

    @SkalEksporteres
    private Integer sumRegnskapInntektInteger;

    @SkalEksporteres
    private Integer sumRegnskapUtgifterInteger;

    @SkalEksporteres
    private Integer sumRegnskapResultatInteger;

    @SkalEksporteres
    private Integer sumUkategorisertInnInteger;

    @SkalEksporteres
    private Integer sumUkategorisertUtInteger;

    @SkalEksporteres
    private Integer sumRegnskapInntektMedOverfoeringerInteger;

    @SkalEksporteres
    private Integer sumRegnskapUtgifterMedOverfoeringerInteger;

    @SkalEksporteres
    private Integer sumRegnskapResultatMedOverfoeringerInteger;

    @OneToMany(fetch =  FetchType.LAZY, mappedBy = "periode", cascade = CascadeType.REMOVE)
    private List<Periodepost> periodeposterList;

    public List<Periodepost> getPeriodeposterList() {
        return periodeposterList;
    }

    public Periode() {
    }

    @Override
    public String toString(){
        return hentBeskrivendeNavn();
    }

    @Override
    public String hentBeskrivendeNavn() {
        if (periodetypeEnum!=null) {
            return periodetypeEnum.getTittel() + " " + Datokyklop.hent().formaterLocalDateMedPresisjon(datoFraLocalDate, periodetypeEnum.getDatopresisjonEnum());
        } else {
            return toString();
        }
    }

    public PeriodetypeEnum getPeriodetypeEnum() {
        return periodetypeEnum;
    }

    public void setPeriodetypeEnum(PeriodetypeEnum periodetypeEnum) {
        this.periodetypeEnum = periodetypeEnum;
    }

    public LocalDate getDatoFraLocalDate() {
        return datoFraLocalDate;
    }

    public void setDatoFraLocalDate(LocalDate datoFraLocalDate) {
        this.datoFraLocalDate = datoFraLocalDate;
    }

    public LocalDate getDatoTilLocalDate() {
        return datoTilLocalDate;
    }

    public void setDatoTilLocalDate(LocalDate datoTilLocalDate) {
        this.datoTilLocalDate = datoTilLocalDate;
    }

    public String getBeskrivelseString() {
        return beskrivelseString;
    }

    public void setBeskrivelseString(String beskrivelseString) {
        this.beskrivelseString = beskrivelseString;
    }

    public Integer getSumBudsjettInntektInteger() {
        return sumBudsjettInntektInteger;
    }

    public void setSumBudsjettInntektInteger(Integer sumBudsjettInntektInteger) {
        this.sumBudsjettInntektInteger = sumBudsjettInntektInteger;
    }

    public Integer getSumBudsjettUtgifterInteger() {
        return sumBudsjettUtgifterInteger;
    }

    public void setSumBudsjettUtgifterInteger(Integer sumBudsjettUtgifterInteger) {
        this.sumBudsjettUtgifterInteger = sumBudsjettUtgifterInteger;
    }

    public Integer getSumBudsjettResultatInteger() {
        return sumBudsjettResultatInteger;
    }

    public void setSumBudsjettResultatInteger(Integer sumBudsjettResultatInteger) {
        this.sumBudsjettResultatInteger = sumBudsjettResultatInteger;
    }

    public Integer getSumRegnskapInntektInteger() {
        return sumRegnskapInntektInteger;
    }

    public void setSumRegnskapInntektInteger(Integer sumRegnskapInntektInteger) {
        this.sumRegnskapInntektInteger = sumRegnskapInntektInteger;
    }

    public Integer getSumRegnskapUtgifterInteger() {
        return sumRegnskapUtgifterInteger;
    }

    public void setSumRegnskapUtgifterInteger(Integer sumRegnskapUtgifterInteger) {
        this.sumRegnskapUtgifterInteger = sumRegnskapUtgifterInteger;
    }

    public Integer getSumRegnskapResultatInteger() {
        return sumRegnskapResultatInteger;
    }

    public void setSumRegnskapResultatInteger(Integer sumRegnskapResultatInteger) {
        this.sumRegnskapResultatInteger = sumRegnskapResultatInteger;
    }

    public Integer getSumRegnskapInntektMedOverfoeringerInteger() {
        return sumRegnskapInntektMedOverfoeringerInteger;
    }

    public void setSumRegnskapInntektMedOverfoeringerInteger(Integer sumRegnskapInntektMedOverfoeringerInteger) {
        this.sumRegnskapInntektMedOverfoeringerInteger = sumRegnskapInntektMedOverfoeringerInteger;
    }

    public Integer getSumRegnskapUtgifterMedOverfoeringerInteger() {
        return sumRegnskapUtgifterMedOverfoeringerInteger;
    }

    public void setSumRegnskapUtgifterMedOverfoeringerInteger(Integer sumRegnskapUtgifterMedOverfoeringerInteger) {
        this.sumRegnskapUtgifterMedOverfoeringerInteger = sumRegnskapUtgifterMedOverfoeringerInteger;
    }

    public Integer getSumRegnskapResultatMedOverfoeringerInteger() {
        return sumRegnskapResultatMedOverfoeringerInteger;
    }

    public void setSumRegnskapResultatMedOverfoeringerInteger(Integer sumRegnskapResultatMedOverfoeringerInteger) {
        this.sumRegnskapResultatMedOverfoeringerInteger = sumRegnskapResultatMedOverfoeringerInteger;
    }

    public Integer getSumUkategorisertInnInteger() {
        return sumUkategorisertInnInteger;
    }

    public void setSumUkategorisertInnInteger(Integer sumUkategorisertInnInteger) {
        this.sumUkategorisertInnInteger = sumUkategorisertInnInteger;
    }

    public Integer getSumUkategorisertUtInteger() {
        return sumUkategorisertUtInteger;
    }

    public void setSumUkategorisertUtInteger(Integer sumUkategorisertUtInteger) {
        this.sumUkategorisertUtInteger = sumUkategorisertUtInteger;
    }

    public Integer getSumDifferanseBudsjettRegnskapInntekter(){
        if (sumBudsjettInntektInteger!=null && sumRegnskapInntektInteger!=null) {
            return sumBudsjettInntektInteger-sumRegnskapInntektInteger;
        } else {
            return null;
        }
    }

    public Integer getSumDifferanseBudsjettRegnskapUtgifter(){
        if (sumBudsjettUtgifterInteger!= null && sumRegnskapUtgifterInteger!=null) {
            return sumBudsjettUtgifterInteger-sumRegnskapUtgifterInteger;
        } else {
            return null;
        }
    }

    public Integer getSumDifferanseBudsjettRegnskapResultat(){
        if (sumBudsjettResultatInteger!=null && sumRegnskapResultatInteger!=null) {
            return sumBudsjettResultatInteger-sumRegnskapResultatInteger;
        } else {
            return null;
        }
    }

}
