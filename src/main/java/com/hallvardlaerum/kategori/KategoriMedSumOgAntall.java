package com.hallvardlaerum.kategori;

public class KategoriMedSumOgAntall {
    Kategori kategori;
    Integer sumInteger;
    Integer antallInteger;

    public KategoriMedSumOgAntall(Kategori kategori, Integer sumInteger, Integer antallInteger) {
        this.kategori = kategori;
        this.sumInteger = sumInteger;
        this.antallInteger = antallInteger;
    }

    public KategoriMedSumOgAntall(Kategori kategori) {
        this.kategori = kategori;
        this.sumInteger = null;
        this.antallInteger = null;
    }

    public String getTittel() {
        if (kategori == null) {
            return "<tom>";
        } else {
            return kategori.getTittel();
        }
    }

    public String getUndertittel() {
        if (kategori == null) {
            return "<tom>";
        } else {
            return kategori.getUndertittel();
        }
    }

    public Kategori getKategori() {
        return kategori;
    }

    public void setKategori(Kategori kategori) {
        this.kategori = kategori;
    }

    public Integer getSumInteger() {
        return sumInteger;
    }

    public void setSumInteger(Integer sumInteger) {
        this.sumInteger = sumInteger;
    }

    public Integer getAntallInteger() {
        return antallInteger;
    }

    public void setAntallInteger(Integer antallInteger) {
        this.antallInteger = antallInteger;
    }
}
