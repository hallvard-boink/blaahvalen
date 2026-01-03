package com.hallvardlaerum.periode;

import com.hallvardlaerum.kategori.Kategori;
import com.hallvardlaerum.post.budsjettpost.BudsjettpoststatusEnum;
import com.hallvardlaerum.verktoy.Allvitekyklop;

/**
 * Denne klassen brukes for å lage tabeller til bruk for grupper av faste poster
 */
public class KategoriBudsjettAntallposterSumInnUt {
    private Kategori kategori;
    private BudsjettpoststatusEnum budsjettpoststatusEnum;
    private Integer antallBudsjettposter;
    private Integer sumBudsjettUtFraKonto;
    private Integer sumBudsjettInnPaaKonto;


    public KategoriBudsjettAntallposterSumInnUt(String kategoriUUIDString, Integer antallBudsjettposter, Integer sumBudsjettInnPaaKonto, Integer sumBudsjettUtFraKonto) {
        this.kategori = Allvitekyklop.hent().getKategoriService().finnEtterUUID(kategoriUUIDString);
        this.antallBudsjettposter = antallBudsjettposter;
        this.sumBudsjettUtFraKonto = sumBudsjettUtFraKonto;
        this.sumBudsjettInnPaaKonto = sumBudsjettInnPaaKonto;
    }

    public Kategori getKategori() {
        return kategori;
    }

    public void setKategori(Kategori kategori) {
        this.kategori = kategori;
    }

    public Integer getSum(){
        if (sumBudsjettInnPaaKonto>0){
            return sumBudsjettInnPaaKonto;
        } else {
            return sumBudsjettUtFraKonto;
        }
    }

    public BudsjettpoststatusEnum getBudsjettpoststatusEnum() {
        return budsjettpoststatusEnum;
    }

    public void setBudsjettpoststatusEnum(BudsjettpoststatusEnum budsjettpoststatusEnum) {
        this.budsjettpoststatusEnum = budsjettpoststatusEnum;
    }

    public Integer getAntallBudsjettposter() {
        return antallBudsjettposter;
    }

    public void setAntallBudsjettposter(Integer antallBudsjettposter) {
        this.antallBudsjettposter = antallBudsjettposter;
    }

    public Integer getSumBudsjettUtFraKonto() {
        return sumBudsjettUtFraKonto;
    }

    public void setSumBudsjettUtFraKonto(Integer sumBudsjettUtFraKonto) {
        this.sumBudsjettUtFraKonto = sumBudsjettUtFraKonto;
    }

    public Integer getSumBudsjettInnPaaKonto() {
        return sumBudsjettInnPaaKonto;
    }

    public void setSumBudsjettInnPaaKonto(Integer sumBudsjettInnPaaKonto) {
        this.sumBudsjettInnPaaKonto = sumBudsjettInnPaaKonto;
    }
}
