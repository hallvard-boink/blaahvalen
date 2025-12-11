package com.hallvardlaerum.periodepost;

import com.hallvardlaerum.grunndata.kategori.Kategori;
import com.hallvardlaerum.libs.database.AbstraktEntitet;
import com.hallvardlaerum.libs.database.EntitetMedForelderAktig;
import com.hallvardlaerum.libs.eksportimport.SkalEksporteres;
import com.hallvardlaerum.periode.Periode;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class Periodepost extends AbstraktEntitet implements EntitetMedForelderAktig<Periode> {

    @SkalEksporteres
    private PeriodepostTypeEnum periodepostTypeEnum;

    @SkalEksporteres
    @ManyToOne(targetEntity =  Kategori.class)
    private Kategori kategori;

    @SkalEksporteres
    private Integer sumRegnskapInteger;

    @SkalEksporteres
    private Integer sumBudsjettInteger;

    @SkalEksporteres
    @ManyToOne(targetEntity = Periode.class)
    private Periode periode;

    @SkalEksporteres
    private String beskrivelseString;


    public String getBeskrivelseString() {
        return beskrivelseString;
    }

    public void setBeskrivelseString(String beskrivelseString) {
        this.beskrivelseString = beskrivelseString;
    }

    @Override
    public void setForelder(Periode forelder) {
        this.periode = forelder;
    }

    @Override
    public Periode getForelder() {
        return periode;
    }

    @Override
    public String hentBeskrivendeNavn() {
        if (periodepostTypeEnum ==null || kategori==null) {
            return "";
        } else {
            return periodepostTypeEnum.getTittel() + " " +
                kategori.getTittel() + " " +
                (sumBudsjettInteger!=null? "Budsjett:" + sumBudsjettInteger : "" ) + " " +
                (sumRegnskapInteger!=null? "Regnskap:" + sumRegnskapInteger : "");
        }
    }

    public PeriodepostTypeEnum getPeriodepostTypeEnum() {
        return periodepostTypeEnum;
    }

    public void setPeriodepostTypeEnum(PeriodepostTypeEnum periodepostTypeEnum) {
        this.periodepostTypeEnum = periodepostTypeEnum;
    }

    public Kategori getKategori() {
        return kategori;
    }

    public void setKategori(Kategori kategori) {
        this.kategori = kategori;
    }

    public Integer getSumRegnskapInteger() {
        return sumRegnskapInteger;
    }

    public void setSumRegnskapInteger(Integer sumRegnskapInteger) {
        this.sumRegnskapInteger = sumRegnskapInteger;
    }

    public Integer getSumBudsjettInteger() {
        return sumBudsjettInteger;
    }

    public void setSumBudsjettInteger(Integer sumBudsjettInteger) {
        this.sumBudsjettInteger = sumBudsjettInteger;
    }

    public Periode getPeriode() {
        return periode;
    }

    public void setPeriode(Periode periode) {
        this.periode = periode;
    }
}
