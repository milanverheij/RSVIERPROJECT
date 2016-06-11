package interfaces;

import exceptions.RSVIERException;
import model.Adres;
import model.Bestelling;
import model.Klant;

import java.util.ListIterator;

/**
 * Created by Milan_Verheij on 06-06-16.<p>>
 * <p>
 * Interface voor KlantDAO's. Schrijft voor elke KlantDAO voor welke <p>
 * CRUD's er geimplementeerd moeten worden. <p>
 *
 * Gebruikt de Klant POJO / model.
 *
 */

public interface KlantDAO {

    /** CREATE METHODS */
    /**
     * Maakt een nieuwe klant aan in de database met voornaam, achternaam en adresgegevens.
     * Er wordt in de database automatisch een uniek ID gegenereerd welke automatisch verhoogd wordt.
     *
     * @param voornaam De voornaam van de klant (max 50 karakters).
     * @param achternaam De achternaam van de klant (max 51 karakters).
     * @param adresgegevens De adresgegevens van de klant in een Adres object (Adres).
     * @throws RSVIERException Foutmelding bij SQLException, info wordt meegegeven.
     */
    long nieuweKlant(String voornaam,
                     String achternaam,
                     Adres adresgegevens) throws RSVIERException;

    /**
     * Maakt een nieuwe klant aan in de database met voor- en achternaam.
     * Er wordt in de database automatisch een uniek ID gegenereerd welke automatisch verhoogd wordt.
     *
     * @param voornaam De voornaam van de klant (max 50 karakters).
     * @param achternaam De achternaam van de klant (max 51 karakters).
     * @throws RSVIERException Foutmelding bij SQLException, info wordt meegegeven.
     */
    long nieuweKlant(String voornaam,
                     String achternaam) throws RSVIERException;

    /**
     * Maakt een nieuwe klant aan in de database met alle naamgegevens.
     * Als er adres en/of bestelgegevens aanwezig zijn worden deze tevens ook toegevoegd.
     * Er wordt in de database automatisch een uniek ID gegenereerd welke automatisch verhoogd wordt.
     *
     * @param voornaam De voornaam van de klant (max 50 karakters).
     * @param achternaam De achternaam van de klant (max 51 karakters).
     * @param tussenvoegsel Tussenvoegsel van de klant (max 10 karakters).
     * @param email Emailadres van de klant (max 80 karakters).
     * @param adresgegevens Adresgegevens van de klant in een Klant object (zie Klant).
     * @param bestelGegevens Bestelgegevens van de klant in een Bestel object (zie Bestelling).
     * @throws RSVIERException Foutmelding bij SQLException, info wordt meegegeven.
     */
    long nieuweKlant(String voornaam,
                    String achternaam,
                    String tussenvoegsel,
                    String email,
                    Adres adresgegevens,
                    Bestelling bestelGegevens) throws RSVIERException;

    /** READ METHODS */

    /**
     * Deze method haalt alle klanten op uit de database en stopt ze in een ArrayList waarna, zie @return.
     *
     * @return een ListIterator wordt teruggegeven van de ArrayList met daarin Klant-objecten.
     * @throws RSVIERException Foutmelding bij SQLException, info wordt meegegeven.
     */
    ListIterator<Klant> getAlleKlanten() throws RSVIERException;

    /**
     * Deze methode haalt op basis van klantId klanten (als het goed is 1) op uit de database en geeft dit
     * terug in en ListIterator van de ArrayList.
     *
     * @param klantId Het klantId van de op te zoeken klant.
     * @return een ListIterator wordt teruggegeven van de ArrayList met daarin Klant-objecten.
     * @throws RSVIERException Foutmelding bij SQLException, info wordt meegegeven.
     */
    ListIterator<Klant> getKlantOpKlant(long klantId) throws RSVIERException;

    /**
     * Deze methode haalt op basis van de voornaam van een klant informatie uit de database en geeft dit
     * terug in en ListIterator van de ArrayList.
     *
     * @param voornaam Voornaam van de te zoeken klant(en).
     * @return een ListIterator wordt teruggegeven van de ArrayList met daarin Klant-objecten.
     * @throws RSVIERException Foutmelding bij SQLException, info wordt meegegeven.
     */
    ListIterator<Klant> getKlantOpKlant(String voornaam) throws RSVIERException;

    /**
     * Deze methode haalt op basis van de voor- en achternaam an een klant informatie uit de database en geeft dit
     * terug in en ListIterator van de ArrayList.
     *
     * @param voornaam Voornaam van de te zoeken klant(en).
     * @param achternaam Achternaam van de te zoeken klant(en).
     * @return een ListIterator wordt teruggegeven van de ArrayList met daarin Klant-objecten.
     * @throws RSVIERException Foutmelding bij SQLException, info wordt meegegeven.
     */
    ListIterator<Klant> getKlantOpKlant(String voornaam,
                        String achternaam) throws RSVIERException;

    /**
     * Deze methode haalt op basis van adresgegevens klanten op uit de database en geeft dit
     * terug in en ListIterator van de ArrayList.
     *
     * @param adresgegevens Een Adres-object van de te zoeken klant(en).
     * @return een ListIterator wordt teruggegeven van de ArrayList met daarin Klant-objecten.
     * @throws RSVIERException Foutmelding bij SQLException, info wordt meegegeven.
     */
    ListIterator<Klant> getKlantOpAdres(Adres adresgegevens) throws RSVIERException;

    /**
     * Deze methode haalt op basis van adresgegevens klanten op uit de database en geeft dit
     * terug in en ListIterator van de ArrayList.
     *
     * @param straatnaam Straatnaam van de te zoeken klant(en).
     * @return een ListIterator wordt teruggegeven van de ArrayList met daarin Klant-objecten.
     * @throws RSVIERException Foutmelding bij SQLException, info wordt meegegeven.
     */
    ListIterator<Klant> getKlantOpAdres(String straatnaam) throws RSVIERException;

    /**
     * Deze methode haalt op basis van een postcode en huisnummer klanten op uit de database en geeft dit
     * terug in en ListIterator van de ArrayList.
     *
     * @param postcode De postcode van de te zoeken klant(en).
     * @param huisnummer Het huisnummer van de te zoeken klant(en).
     * @return een ListIterator wordt teruggegeven van de ArrayList met daarin Klant-objecten.
     * @throws RSVIERException Foutmelding bij SQLException, info wordt meegegeven.
     */
    ListIterator<Klant> getKlantOpAdres(String postcode,
                         int huisnummer) throws RSVIERException;

    /**
     * Deze methode haalt op basis van bestelId klanten op uit de database en geeft dit
     * terug in en ListIterator van de ArrayList.
     *
     * @param bestellingId Het bestelId van de te zoeken klant(en).
     * @return een ListIterator wordt teruggegeven van de ArrayList met daarin Klant-objecten.
     * @throws RSVIERException Foutmelding bij SQLException, info wordt meegegeven.
     */
    ListIterator<Klant> getKlantOpBestelling(long bestellingId) throws RSVIERException;

    /** UPDATE METHODS */

    /**
     * Methode om een klant met een bepaald klant_id zijn naamgegevens up te daten.
     *
     * @param klantId Het klantId van de klant wiens gegevens gewijzigd dienen te worden.
     * @param voornaam De 'gewijzigde' voornaam van de klant.
     * @param achternaam De 'gewijzigde' achternaam van de klant.
     * @param tussenvoegsel Het 'gewijzigde' tussenvoegsel van de klant.
     * @param email Het gewijzigde emailadres van de klant.
     * @throws RSVIERException Foutmelding bij SQLException, info wordt meegegeven.
     */
    void updateKlant(Long klantId,
                     String voornaam,
                     String achternaam,
                     String tussenvoegsel,
                     String email) throws RSVIERException;

    /**
     * Methode om een klant met een bepaald klant_id zijn naam en tevens
     * adres gegevens up te daten.
     *
     * @param KlantId Het klantId van de klant wiens gegevens gewijzigd dienen te worden.
     * @param voornaam De 'gewijzigde' voornaam van de klant.
     * @param achternaam De 'gewijzigde' achternaam van de klant.
     * @param tussenvoegsel Het 'gewijzigde' tussenvoegsel van de klant.
     * @param email Het 'gewijzigde' emailadres van de klant.
     * @param adresgegevens De 'gewijzigde' adresgegevens van de klant in Klantobject.
     * @throws RSVIERException Foutmelding bij SQLException, info wordt meegegeven.
     */
    void updateKlant(long KlantId,
                     String voornaam,
                     String achternaam,
                     String tussenvoegsel,
                     String email,
                     Adres adresgegevens) throws RSVIERException;

    /** DELETE METHODS */

    /**
     * Methode om een klant te verwijderen op basis van ID. Alle bestellingen van de klant worden
     * tevens ook verwijderd.
     *
     * @param klantId Klant_id van de te verwijderen klant.
     * @throws RSVIERException Foutmelding bij SQLException, info wordt meegegeven.
     */
    void verwijderKlant(long klantId) throws RSVIERException;

    /**
     * Methode om een klant te verwijderen op basis van alleen voor- en achternaam;
     *
     * @param voornaam Voornaam van de te verwijderen
     * @param achternaam Achternaam van de te verwijderen klant
     * @throws RSVIERException Foutmelding bij SQLException, info wordt meegegeven.
     */
    void verwijderKlant(String voornaam,
                        String achternaam) throws RSVIERException;

    /**
     * Methode om een klant te verwijderen op basis van naamgegevens. Alle bestellingen van de klant worden
     * tevens ook verwijderd.
     *
     * @param voornaam De voornaam van de te verwijderen klant.
     * @param achternaam De achternaam van de te verwijderen klant.
     * @param tussenvoegsel Het tussenvoegsel van de te verwijderen klant.
     * @throws RSVIERException Foutmelding bij SQLException, info wordt meegegeven.
     */
    void verwijderKlant(String voornaam,
                        String achternaam,
                        String tussenvoegsel) throws RSVIERException;

    /**
     * Methode om een klant te verwijderen op basis van een bestelnummer.
     *
     * @param bestellingId Bestel-ID van de te verwijderen klant.
     * @throws RSVIERException Foutmelding bij SQLException, info wordt meegegeven.
     */
    void verwijderKlantOpBestellingId(long bestellingId) throws RSVIERException;

    // TODO: Tijdelijk om naar console te printen, aangezien later naar GUI gaat
    public void printKlantenInConsole(ListIterator<Klant> klantenIterator);
}
