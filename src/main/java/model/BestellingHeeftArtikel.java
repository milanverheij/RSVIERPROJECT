package model;

import annotations.Column;
import annotations.Entity;

@Entity("bestellingHeeftArtikel")
public class BestellingHeeftArtikel {

    @Column(values = "bestellingId_best")
    long bestellingId_best;

    @Column(values = "artikelIdArt")
    long artikelIdArt;

    @Column(values = "prijsIdPrijs")
    long prijsIdPrijs;

    @Column(values = "aantal")
    long aantal;

    public long getBestellingId_best() {
        return bestellingId_best;
    }
    public void setBestellingId_best(long bestellingId_best) {
        this.bestellingId_best = bestellingId_best;
    }
    public long getArtikelIdArt() {
        return artikelIdArt;
    }
    public void setArtikelIdArt(long artikelIdArt) {
        this.artikelIdArt = artikelIdArt;
    }
    public long getPrijsIdPrijs() {
        return prijsIdPrijs;
    }
    public void setPrijsIdPrijs(long prijsIdPrijs) {
        this.prijsIdPrijs = prijsIdPrijs;
    }
    public long getAantal() {
        return aantal;
    }
    public void setAantal(long aantal) {
        this.aantal = aantal;
    }
}