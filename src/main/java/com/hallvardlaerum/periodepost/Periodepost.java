package com.hallvardlaerum.periodepost;

import com.hallvardlaerum.kategori.Kategori;
import com.hallvardlaerum.libs.database.AbstraktEntitet;
import com.hallvardlaerum.libs.eksportimport.SkalEksporteres;
import com.hallvardlaerum.periode.Periode;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class Periodepost extends AbstraktEntitet {


// ===========================
// region Felter
// ===========================


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

// endregion



// ===========================
// region toString og beskrivendeNavn
// ===========================


    @Override
    public String toString(){
        return hentBeskrivendeNavn() + " (Periodepost)";
    }

    @Override
    public String hentBeskrivendeNavn() {
        if (periodepostTypeEnum ==null) {
            if (kategori!=null) {
                return kategori.hentBeskrivendeNavn();
            } else {
                return "";
            }
        } else {
            return switch (periodepostTypeEnum) {
                case AARSOVERSIKTPOST, MAANEDSOVERSIKTPOST ->  lagBeskrivendenavn_AarsoversiktMaanedsoversikt();
                case PERIODEOVERSIKTPOST -> lagBeskrivendenavn_Periodeoversiktpost();
                };
        }
    }

    private String lagBeskrivendenavn_Periodeoversiktpost() {
        return "Kostnadspakke " + kategori.hentKortnavn() + " " +
                (sumRegnskapInteger!=null? "Regnskap:" + sumRegnskapInteger : "");
    }

    private String lagBeskrivendenavn_AarsoversiktMaanedsoversikt(){
        return periodepostTypeEnum.getTittel() + " " +
                kategori.getTittel() + " " +
                (sumBudsjettInteger!=null? "Budsjett:" + sumBudsjettInteger : "" ) + " " +
                (sumRegnskapInteger!=null? "Regnskap:" + sumRegnskapInteger : "");
    }

    public String hentKortnavn(){
        if (periodepostTypeEnum ==null || kategori==null) {
            return "";
        } else {
            return switch (periodepostTypeEnum) {
                case AARSOVERSIKTPOST, MAANEDSOVERSIKTPOST ->  lagBeskrivendenavn_AarsoversiktMaanedsoversikt();
                case PERIODEOVERSIKTPOST -> lagKostnadspakkeKortnavn();
            };
        }
    }

    private String lagKostnadspakkeKortnavn(){
        StringBuilder sb = new StringBuilder();
        if (periode!=null) {
            sb.append(periode.getDatoFraLocalDate().getYear()).append(" ");
        }
        if (kategori!=null) {
            sb.append(kategori.hentKortnavn()).append(" ");
        }
        sb.append(tittelString);
        return sb.toString();
    }

// endregion



// ===========================
// region Getters and setters
// ===========================


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
    }// endregion


}
