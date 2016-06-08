package model;

/*
 * Created by Douwe_Jongeneel on 06-06-16.
 *
 * Dit is de Adres POJO + GS
 */

public class Adres {
    //Datafield
    private String straatnaam;
    private String postcode;
    private String toevoeging;
    private int huisnummer;
    private String woonplaats;

    //Consturctors
    public Adres() { //standaard een no-args constructor
    }
    public Adres(String straatnaam, String postcode, String toevoeging,
                 int huisnummer, String woonplaats) {
        this.straatnaam = straatnaam;
        this.postcode = postcode;
        this.toevoeging = toevoeging;
        this.huisnummer = huisnummer;
        this.woonplaats = woonplaats;
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
}