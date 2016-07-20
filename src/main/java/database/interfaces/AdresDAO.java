package database.interfaces;

import exceptions.GeneriekeFoutmelding;
import model.Adres;

import java.util.ListIterator;

/**
 * Created by Milan_Verheij on 08-06-16.
 * Adres DAO Interface. Gooit een GeneriekeFoutmelding bij fouten.
 *
 */
public interface AdresDAO {
    /**
     * Update een adres bij een klant op basis van een Adres-object en adresId.
     *
     * @param adresId Het adresId om up te daten.
     * @param adresgegevens De adresgegevens om te updaten in Adres object formaat
     * @throws GeneriekeFoutmelding Als er een fout is wordt deze doorgestuurd naar de GeneriekeFoutmelding met de message van
     * de exception.
     */
    void updateAdres(long adresId, Adres adresgegevens) throws GeneriekeFoutmelding;

    /**
     * Geeft op basis van de unieke gegevens van een adres (conform de equalsmethode in Adres)
     * het corresponderende adresId terug.
     *
     * @param postcode Postcode om op te zoeken.
     * @param huisnummer Huisnummer om op te zoeken.
     * @param toevoeging Toevoeging van adres om op te zoeken.
     * @return Het adresId behorend bij dit adres.
     * @throws GeneriekeFoutmelding Als er een fout is wordt deze doorgestuurd naar de GeneriekeFoutmelding met de message van
     * de exception.
     */
    long getAdresID(String postcode, int huisnummer, String toevoeging) throws GeneriekeFoutmelding;

    /**
     * Geeft de adressen terug van een bepaalde klant.
     *
     * @param klantId KlantId van de klant waarvan de adressen opgezocht dienen te worden.
     * @return Een ListIterator van de ArrayList met daarin Klant objecten.
     * @throws GeneriekeFoutmelding Als er een fout is wordt deze doorgestuurd naar de GeneriekeFoutmelding met de message van
     * de exception.
     */
    ListIterator<Adres> getAdresOpKlantID(long klantId) throws GeneriekeFoutmelding;

    /**
     * Geeft een specifiek adres terug in een Adres_Object
     *
     * @param adresId AdresId van het adres dat opgezocht dient te worden.
     * @return Een Adres_object van het adres.
     * @throws GeneriekeFoutmelding Als er een fout is wordt deze doorgestuurd naar de GeneriekeFoutmelding met de message van
     * de exception.
     */
    Adres getAdresOpAdresID(long adresId) throws GeneriekeFoutmelding;

    /**
     * Maakt een nieuw adres aan en koppelt deze aan de klant.
     *
     * @param klantId KlantId behorende bij het adres.
     * @param adresgegevens De adresgegevens die nieuw in de database dienen te worden opgenomen.
     * @return Het adresId van het nieuw aangemaakte adres.
     * @throws GeneriekeFoutmelding Als er een fout is wordt deze doorgestuurd naar de GeneriekeFoutmelding met de message van
     * de exception.
     */
    long nieuwAdres(long klantId, Adres adresgegevens) throws GeneriekeFoutmelding;

    /**
     * Koppelt een bestaand adres aan een klant.
     *
     * @param klantId Het klantId waaraan een adres gekoppeld dient te worden
     * @param adresId Het adresId van het te koppelen adres.
     * @throws GeneriekeFoutmelding Als er een fout is wordt deze doorgestuurd naar de GeneriekeFoutmelding met de message van
     * de exception.
     */
    void koppelAdresAanKlant(long klantId, long adresId) throws GeneriekeFoutmelding;

    /**
     * Stelt de status is van een adres (0 = inactief, 1 = actief)
     *
     * @param adresId Het adresId van het adres dat geschakeld dient te worden.
     * @param status De nieuwe gewenste status van het adres.
     * @throws GeneriekeFoutmelding Als er een fout is wordt deze doorgestuurd naar de GeneriekeFoutmelding met de message van
     * de exception.
     */
    void schakelStatusAdres(long adresId, int status) throws GeneriekeFoutmelding;
}
