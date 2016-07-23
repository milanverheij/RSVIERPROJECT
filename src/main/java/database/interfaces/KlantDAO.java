package database.interfaces;


import exceptions.GeneriekeFoutmelding;
import model.Adres;
import model.Bestelling;
import model.Klant;

import java.util.ArrayList;
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
     * [HOOFD NIEUWEKLANTMETHODE]
     * Maakt een nieuwe klant aan in de database met alle naamgegevens.
     * Als er adres en/of bestelgegevens aanwezig zijn worden deze tevens ook toegevoegd.
     * Er wordt in de database automatisch een uniek ID gegenereerd welke automatisch verhoogd wordt.
     * Het is mogelijk door middel van een adresId mee te geven geen nieuw adres aan te maken maar
     * deze te koppelen aan de klant.
     *
     * @param nieuweKlant Nieuwe klantgegevens in een Klant-object.
     * @param adresgegevens Adresgegevens van de klant in een Klant object (zie Klant).
     * @param bestelGegevens Bestelgegevens van de klant in een Bestel object (zie Bestelling).
     * @throws GeneriekeFoutmelding Foutmelding bij SQLException, info wordt meegegeven.
     */
    long nieuweKlant(Klant nieuweKlant,
                     long adresId,
                     Adres adresgegevens,
                     Bestelling bestelGegevens) throws GeneriekeFoutmelding;

    /**
     * Maakt een nieuwe klant aan met enkel een Adres Object en een mogelijk adres-id. Als geen
     * adres-id wordt meegegeven wordt geen adres gekopeld.
     *
     * @param nieuweKlant De gegevens van de nieuwe klant in een Klant-object
     * @param adresId Het adres-id van een mogelijk te koppelen adres.
     * @return Het nieuwe klant-id wordt teruggegeven.
     * @throws GeneriekeFoutmelding Foutmelding bij SQLException, info wordt meegegeven.
     */
    long nieuweKlant(Klant nieuweKlant,
                     long adresId) throws GeneriekeFoutmelding;

    /** READ METHODS */

    /**
     * De uniekheid van een klant is op basis van voornaam, achternaam en email, hier kan er dus maar 1 van bestaan.
     *
     * @param voornaam De te zoeken voornaam
     * @param achternaam De te zoeken achternaam
     * @param email De te zoeken email van de klant
     * @return Het klantId van de klant
     */
    long getKlantID(String voornaam,
                    String achternaam,
                    String email) throws GeneriekeFoutmelding;

    /**
     * DOet blahblah enzo //TODO
     *
     * @return
     * @throws GeneriekeFoutmelding
     */
    ArrayList<Klant> getAlleKlanten() throws GeneriekeFoutmelding ;

    /**
     * Deze method haalt klanten op uit de database op basis van een meegegeven Klant-Object.
     *
     * @param klant Klant-object gevuld met zoek-parameters.
     * @return een ListIterator wordt teruggegeven van de ArrayList met daarin Klant-objecten.
     * @throws GeneriekeFoutmelding Foutmelding bij SQLException, info wordt meegegeven.
     */
    ArrayList<Klant> getKlantOpKlant(Klant klant) throws GeneriekeFoutmelding;

    /**
     * Deze methode haalt op basis van adresgegevens klanten op uit de database en geeft dit
     * terug in en ListIterator van de ArrayList.
     *
     * @param adresgegevens Een Adres-object van de te zoeken klant(en).
     * @return een ListIterator wordt teruggegeven van de ArrayList met daarin Klant-objecten.
     * @throws GeneriekeFoutmelding Foutmelding bij SQLException, info wordt meegegeven.
     */
    ArrayList<Klant> getKlantOpAdres(Adres adresgegevens) throws GeneriekeFoutmelding;

    /**
     * Deze methode haalt op basis van bestelId klanten op uit de database en geeft dit
     * terug in en ListIterator van de ArrayList.
     *
     * @param bestellingId Het bestelId van de te zoeken klant(en).
     * @return een ListIterator wordt teruggegeven van de ArrayList met daarin Klant-objecten.
     * @throws GeneriekeFoutmelding Foutmelding bij SQLException, info wordt meegegeven.
     */
    ArrayList<Klant> getKlantOpBestelling(long bestellingId) throws GeneriekeFoutmelding;

    /** UPDATE METHODS */

    /**
     * Methode om een klant met een bepaald klantId zijn naamgegevens up te daten.
     *
     * @param nieuweKlant De te updaten klant in Klant-object
     * @throws GeneriekeFoutmelding Foutmelding bij SQLException, info wordt meegegeven.
     */
    void updateKlant(Klant nieuweKlant) throws GeneriekeFoutmelding;

    /**
     * Methode om een klant te updaten met een mogelijk los adres-object.
     *
     * @param nieuweKlant De te updaten klant in Klant-object
     * @param adresgegevens De 'gewijzigde' adresgegevens van de klant in Klantobject.
     * @throws GeneriekeFoutmelding Foutmelding bij SQLException, info wordt meegegeven.
     */
    void updateKlant(Klant nieuweKlant,
                     Adres adresgegevens) throws GeneriekeFoutmelding;

    /** DELETE METHODS */

    /**
     * Methode om een klant te verwijderen op basis van ID. Alle bestellingen van de klant worden
     * tevens ook verwijderd.
     *
     * @param klantId KlantId van de te verwijderen klant.
     * @throws GeneriekeFoutmelding Foutmelding bij SQLException, info wordt meegegeven.
     */
    void schakelStatusKlant(long klantId, int status) throws GeneriekeFoutmelding;

    /**
     * Methode om een klant te verwijderen op basis van alleen voor- en achternaam;
     *
     * @throws GeneriekeFoutmelding Foutmelding bij SQLException, info wordt meegegeven.
     */
    void schakelStatusKlant(Klant klant) throws GeneriekeFoutmelding;

    /**
     * Methode om een klant te verwijderen op basis van een bestelnummer.
     *
     * @param bestellingId Bestel-ID van de te verwijderen klant.
     * @throws GeneriekeFoutmelding Foutmelding bij SQLException, info wordt meegegeven.
     */
    long verwijderKlantOpBestellingId(long bestellingId) throws GeneriekeFoutmelding;

    /**
     * Handzame methode voor tijdens test / develop doel-einden eenvoudig informatie naar
     * de console te printen.
     *
     * @param klantenIterator Een iterator van de klantenlijst
     * @throws GeneriekeFoutmelding Foutmelding met omschrijving.
     */
    void printKlantenInConsole(ListIterator<Klant> klantenIterator) throws GeneriekeFoutmelding;
}
