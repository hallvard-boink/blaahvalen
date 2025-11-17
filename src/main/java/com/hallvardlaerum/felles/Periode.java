package com.hallvardlaerum.felles;

import com.hallvardlaerum.libs.database.AbstraktEntitet;
import com.hallvardlaerum.libs.eksportimport.SkalEksporteres;
import com.hallvardlaerum.libs.felter.Datokyklop;
import jakarta.persistence.Entity;

import java.time.LocalDate;

@Entity
public class Periode extends AbstraktEntitet {

    @SkalEksporteres
    private PeriodetypeEnum periodetypeEnum;

    @SkalEksporteres
    private LocalDate datoFraLocalDate;

    @SkalEksporteres
    private LocalDate datTilLocalDate;

    @SkalEksporteres
    private String beskrivelse;

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
    private Integer sumDifferanseResultatBudsjettRegnskap;

    public Periode() {
    }


    @Override
    public String hentBeskrivendeNavn() {
        return periodetypeEnum.getTittel() + " " + Datokyklop.hent().formaterDato(datoFraLocalDate);
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

    public LocalDate getDatTilLocalDate() {
        return datTilLocalDate;
    }

    public void setDatTilLocalDate(LocalDate datTilLocalDate) {
        this.datTilLocalDate = datTilLocalDate;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }
}
