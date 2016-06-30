package model;

/**
 * Created by Milan_Verheij on 06-06-16.
 * Updated by Milan Verheij on 20-06-16 (nieuw DB-model).
 *
 * Model van Klant. Variabelen komen overeen met de tabel KLANT
 * in de database.
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
    private String datumAanmaak;
    private String datumGewijzigd;
    private String klantActief;
    private Adres adresGegevens;
    private Bestelling bestellingGegevens;

    // Standaard public constructor met basis parameters
    public Klant(long klant_id,
                 String voornaam,
                 String achternaam,
                 String tussenvoegsel,
                 String email,
                 Adres adresGegevens) {

        if (adresGegevens != null)
            this.adresGegevens = adresGegevens;

        this.klant_id = klant_id;
        this.voornaam = voornaam;
        this.achternaam = achternaam;
        this.tussenvoegsel = tussenvoegsel;
        this.email = email;
    }

    // Constructor voor alle variabelen, wordt over het algemeen gebruikt tijdens testwerkzaamheden
    // en bij het opvragen van gegevens via de DAO's
    public Klant(long klant_id,
                 String voornaam,
                 String achternaam,
                 String tussenvoegsel,
                 String email,
                 String datumAanmaak,
                 String datumGewijzigd,
                 String klantActief,
                 Adres adresGegevens,
                 Bestelling bestellingGegevens) {

        if (adresGegevens != null)
            this.adresGegevens = adresGegevens;

        this.klant_id = klant_id;
        this.voornaam = voornaam;
        this.achternaam = achternaam;
        this.tussenvoegsel = tussenvoegsel;
        this.email = email;
        this.datumAanmaak = datumAanmaak;
        this.datumGewijzigd = datumGewijzigd;
        this.klantActief = klantActief;
        this.bestellingGegevens = bestellingGegevens;
    }

    // Als er een klant aangemaakt wordt, wordt er een Adres-object aan gekoppeld
    public Klant() {
        klant_id = -1;
        adresGegevens = null;
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
    public String getDatumAanmaak() {
        return datumAanmaak;
    }
    public void setDatumAanmaak(String datumAanmaak) {
        this.datumAanmaak = datumAanmaak;
    }
    public String getDatumGewijzigd() {
        return datumGewijzigd;
    }
    public void setDatumGewijzigd(String datumGewijzigd) {
        this.datumGewijzigd = datumGewijzigd;
    }
    public String getKlantActief() {
        return klantActief;
    }
    public void setKlantActief(String klantActief) {
        this.klantActief = klantActief;
    }
    public void setAdresGegevens(Adres adresGegevens) {
        this.adresGegevens = adresGegevens;
    } // Enkel bij testen gebruikt
    public Bestelling getBestellingGegevens() {
        return bestellingGegevens;
    }
    public void setBestellingGegevens(Bestelling bestellingGegevens) {
        this.bestellingGegevens = bestellingGegevens;
    } // Enkel bij testen gebruikt

    // Overrided methoden van Object etc.

    @Override
    public String toString() {
        return "[" + klant_id + ", " +
                     voornaam + ", " +
                     achternaam + ", " +
                     tussenvoegsel + ", " +
                     email + ", " +
                     "Adresgegevens aanwezig:" +
                     (adresGegevens != null ? " ja" : " nee") +
                    "]";
    }

    /**
     * Een klant-object wordt geacht gelijk te zijn als het klant_id hetzelfde is.
     *
     * @param obj Klant-Object om mee te vergelijken.
     * @return Een waarde true of false als de klant gelijk is.
     */
    @Override
    public boolean equals(Object obj) {
        if (klant_id == ((Klant)obj).getKlant_id())
            return true;
        return false;
    }
}
