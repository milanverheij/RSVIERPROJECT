package model;

/**
 * Created by Milan_Verheij on 06-06-16.
 *
 * Model van Klant. Variabelen komen overeen met de tabel KLANT
 * in de database. Standaard POJO.
 *
 */
public class Klant {
    // Private variabelen zodat er controle wordt uitgeoefend over het verkrijgen en
    // muteren in de methods.
    private long klant_id;
    private String voornaam;
    private String achternaam;
    private String tussenvoegsel;
    private String email;
    private Adres adresGegevens;

    // Standaard public constructor met alle paramaters
    public Klant(long klant_id,
                 String voornaam,
                 String achternaam,
                 String tussenvoegsel,
                 String email,
                 Adres adresGegevens) {
        super();
        this.klant_id = klant_id;
        this.voornaam = voornaam;
        this.achternaam = achternaam;
        this.tussenvoegsel = tussenvoegsel;
        this.email = email;
    }

    // Als er een klant aangemaakt wordt, wordt er een Adres-object aan gekoppeld
    public Klant() {
        adresGegevens = new Adres();
    }

    // Getters & setters
    public long getKlant_id() {
        return klant_id;
    }
    public void setKlant_id(long klant_id) {
        this.klant_id = klant_id;
    }
    public String getVoornaam() {
        return voornaam;
    }
    public void setVoornaam(String voornaam) {
        this.voornaam = voornaam;
    }
    public String getAchternaam() {
        return achternaam;
    }
    public void setAchternaam(String achternaam) {
        this.achternaam = achternaam;
    }
    public String getTussenvoegsel() {
        return tussenvoegsel;
    }
    public void setTussenvoegsel(String tussenvoegsel) {
        this.tussenvoegsel = tussenvoegsel;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public Adres getAdresGegevens() {
        return adresGegevens;
    }
}
