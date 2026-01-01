package com.hallvardlaerum.periodepost;

import com.hallvardlaerum.kategori.Kategori;
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

    @SkalEksporteres
    private String tittelString;



    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        if (getUuid()!=null) {
            sb.append(getUuid()).append(": ");
        }
        if (kategori!=null) {
            sb.append(kategori.hentBeskrivendeNavn());
        }

        return sb.toString();
    }

    @Override
    public String hentBeskrivendeNavn() {
        if (periodepostTypeEnum ==null || kategori==null) {
            return "";
        } else {
            switch (periodepostTypeEnum) {
                case AARSOVERSIKTPOST, MAANEDSOVERSIKTPOST ->  lagBeskrivendenavnAarsoversiktMaanedsoversikt();
                case PERIODEOVERSIKTPOST -> lagBeskrivendenavnPeriodeoversiktpost();
                default -> {return super.toString();}
                }
        }
        return "";
    }

    private String lagBeskrivendenavnPeriodeoversiktpost() {
        return "Kostnadspakke " + kategori.hentKortnavn() + " " +
                (sumRegnskapInteger!=null? "Regnskap:" + sumRegnskapInteger : "");
    }

    private String lagBeskrivendenavnAarsoversiktMaanedsoversikt(){
        return periodepostTypeEnum.getTittel() + " " +
                kategori.getTittel() + " " +
                (sumBudsjettInteger!=null? "Budsjett:" + sumBudsjettInteger : "" ) + " " +
                (sumRegnskapInteger!=null? "Regnskap:" + sumRegnskapInteger : "");
    }

    @Override
    public void setForelder(Periode forelder) {
        this.periode = forelder;
    }

    @Override
    public Periode getForelder() {
        return periode;
    }


    // === Getters and setters ===


    public String getTittelString() {
        return tittelString;
    }

    public void setTittelString(String tittelString) {
        this.tittelString = tittelString;
    }

    public PeriodepostTypeEnum getPeriodepostTypeEnum() {
        return periodepostTypeEnum;
    }

    public void setPeriodepostTypeEnum(PeriodepostTypeEnum periodepostTypeEnum) {
        this.periodepostTypeEnum = periodepostTypeEnum;
    }

    public String getBeskrivelseString() {
        return beskrivelseString;
    }

    public void setBeskrivelseString(String beskrivelseString) {
        this.beskrivelseString = beskrivelseString;
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
