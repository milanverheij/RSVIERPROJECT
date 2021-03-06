package model;

import annotations.Column;
import annotations.Entity;
import annotations.Id;

/**
 * Created by Douwe_Jongeneel on 06-06-16.
 * Updated by Milan Verheij on 20-06-16 (nieuw DB-model).
 *
 * Dit is de Adres POJO + GS
 */

@Entity("adres")
public class Adres {
    //Datafield

    @Id
    @Column(values = "adresId")
    private long adresId = 0;

    @Column(values = "straatnaam")
    private String straatnaam;

    @Column(values = "postcode")
    private String postcode;

    @Column(values = "toevoeging")
    private String toevoeging;

    @Column(values = "huisnummer")
    private int huisnummer;

    @Column(values = "woonplaats")
    private String woonplaats;

    @Column(values = "datumAanmaak")
    private String datumAanmaak;

    @Column(values = "datumGewijzigd")
    private String datumGewijzigd;

    @Column(values = "adresActief")
    private String adresActief;

    //Consturctors
    public Adres() {
        //standaard een no-args constructor met lege gegevens. Komt enkel voor als er 'null' als Adres wordt
        // meegegeven in de updateMethode van de AdresDAO
    }

    // Constructor met basis gegevens
    public Adres(String straatnaam, String postcode, String toevoeging,
                 int huisnummer, String woonplaats) {
        this.straatnaam = straatnaam;
        this.postcode = postcode;
        this.toevoeging = toevoeging;
        this.huisnummer = huisnummer;
        this.woonplaats = woonplaats;
    }

    // Constructor met basis gegevens en gegevens welke enkel bij tests worden gewijzigd maar wel van
    // belang zijn voor het opvragen van gegevens etc. in de DAO's
    public Adres(String straatnaam, String postcode, String toevoeging,
                 int huisnummer, String woonplaats, String datumAanmaak,
                 String datumGewijzigd, String adresActief) {
        this.straatnaam = straatnaam;
        this.postcode = postcode;
        this.toevoeging = toevoeging;
        this.huisnummer = huisnummer;
        this.woonplaats = woonplaats;
        this.datumAanmaak = datumAanmaak;
        this.datumGewijzigd = datumGewijzigd;
        this.adresActief = adresActief;
    }

    //Getters and Setters
    public String getStraatnaam() {
        return straatnaam;
    }
    public String getPostcode() {
        return postcode;
    }
    public String getToevoeging() {
        return toevoeging;
    }
    public int getHuisnummer() {
        return huisnummer;
    }
    public String getWoonplaats() {
        return woonplaats;
    }
    public long getAdresId() {
        return adresId;
    }
    public String getDatumAanmaak() {
        return datumAanmaak;
    }
    public String getDatumGewijzigd() {
        return datumGewijzigd;
    }
    public String getAdresActief() {
        return adresActief;
    }

    public void setStraatnaam(String straatnaam) {
        this.straatnaam = straatnaam;
    }
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }
    public void setToevoeging(String toevoeging) {
        this.toevoeging = toevoeging;
    }
    public void setHuisnummer(int huisnummer) {
        this.huisnummer = huisnummer;
    }
    public void setWoonplaats(String woonplaats) {
        this.woonplaats = woonplaats;
    }
    public void setAdresId(long adresId) {
        this.adresId = adresId;
    }
    public void setDatumAanmaak(String datumAanmaak) {
        this.datumAanmaak = datumAanmaak;
    }
    public void setDatumGewijzigd(String datumGewijzigd) {
        this.datumGewijzigd = datumGewijzigd;
    }
    public void setAdresActief(String adresActief) {
        this.adresActief = adresActief;
    }

    @Override
    public String toString() {
        return "[" +
                straatnaam + ", " +
                postcode + ", " +
                toevoeging + ", " +
                huisnummer + ", " +
                woonplaats + "]";
    }

    /**
     * Een adres-object wordt geacht gelijk te zijn als zowel de postcode, huisnummer
     * en de toevoeging overeen komen.
     *
     * @param obj Een adres-object om mee te vergelijken.
     * @return Een waarde true of false.
     */
    @Override
    public boolean equals(Object obj) {
        if (postcode.equals(((Adres)obj).getPostcode()) &&
                huisnummer == ((Adres)obj).getHuisnummer() &&
                toevoeging.equals(((Adres)obj).getToevoeging()))
            return true;
        return false;
    }
}