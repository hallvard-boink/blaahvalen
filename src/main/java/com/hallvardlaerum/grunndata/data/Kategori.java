package com.hallvardlaerum.grunndata.data;

import com.hallvardlaerum.libs.database.AbstraktEntitet;
import com.hallvardlaerum.libs.database.EntitetMedBarnAktig;
import com.hallvardlaerum.libs.eksportimport.SkalEksporteres;
import com.hallvardlaerum.regnskap.data.Post;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
public class Kategori extends AbstraktEntitet implements EntitetMedBarnAktig<Post> {

    @SkalEksporteres
    private String tittel;

    @SkalEksporteres
    private String beskrivelse;

    @SkalEksporteres
    private Boolean brukesTilBudsjett;

    @SkalEksporteres
    private Boolean brukesTilFastePoster;

    @SkalEksporteres
    private Boolean brukesTilRegnskap;

    @SkalEksporteres
    private KategoriRetning kategoriRetning;

    @SkalEksporteres
    private KategoriType kategoriType;

    @SkalEksporteres
    private Integer rekkefoelge;

    @SkalEksporteres
    private Boolean erAktiv;

    @OneToMany(mappedBy = "kategori")
    private List<Post> poster;

    @Override
    public ArrayList<Post> hentBarn() {
        return new ArrayList<>(poster);
    }

    @Override
    public String hentBeskrivendeNavn() {
        return tittel + "(" + kategoriType.getTittel() + ", " + kategoriRetning.getTittel() + ")";
    }

    public Kategori() {
        super();
    }

    public String getTittel() {
        return tittel;
    }


    public List<Post> getPoster() {
        return poster;
    }

    public void setPoster(List<Post> poster) {
        this.poster = poster;
    }

    public void setTittel(String tittel) {
        this.tittel = tittel;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }

    public Boolean getBrukesTilBudsjett() {
        return brukesTilBudsjett;
    }

    public void setBrukesTilBudsjett(Boolean brukesTilBudsjett) {
        this.brukesTilBudsjett = brukesTilBudsjett;
    }

    public Boolean getBrukesTilFastePoster() {
        return brukesTilFastePoster;
    }

    public void setBrukesTilFastePoster(Boolean brukesTilFastePoster) {
        this.brukesTilFastePoster = brukesTilFastePoster;
    }

    public Boolean getBrukesTilRegnskap() {
        return brukesTilRegnskap;
    }

    public void setBrukesTilRegnskap(Boolean brukesTilRegnskap) {
        this.brukesTilRegnskap = brukesTilRegnskap;
    }

    public KategoriRetning getKategoriRetning() {
        return kategoriRetning;
    }

    public void setKategoriRetning(KategoriRetning kategoriRetning) {
        this.kategoriRetning = kategoriRetning;
    }

    public KategoriType getKategoriType() {
        return kategoriType;
    }

    public void setKategoriType(KategoriType kategoriType) {
        this.kategoriType = kategoriType;
    }

    public Integer getRekkefoelge() {
        return rekkefoelge;
    }

    public void setRekkefoelge(Integer rekkefoelge) {
        this.rekkefoelge = rekkefoelge;
    }

    public Boolean getErAktiv() {
        return erAktiv;
    }

    public void setErAktiv(Boolean erAktiv) {
        this.erAktiv = erAktiv;
    }
}
