package interfaces;

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
     * Update een adres bij een klant op basis van een Adres-object en adres_id.
     *
     * @param adres_id Het adres_id om up te daten.
     * @param adresgegevens De adresgegevens om te updaten in Adres object formaat
     * @throws GeneriekeFoutmelding Als er een fout is wordt deze doorgestuurd naar de GeneriekeFoutmelding met de message van
     * de exception.
     */
    void updateAdres(long adres_id, Adres adresgegevens) throws GeneriekeFoutmelding;

    /**
     * Geeft op basis van de unieke gegevens van een adres (conform de equalsmethode in Adres)
     * het corresponderende adres_id terug.
     *
     * @param postcode Postcode om op te zoeken.
     * @param huisnummer Huisnummer om op te zoeken.
     * @param toevoeging Toevoeging van adres om op te zoeken.
     * @return Het adres_id behorend bij dit adres.
     * @throws GeneriekeFoutmelding Als er een fout is wordt deze doorgestuurd naar de GeneriekeFoutmelding met de message van
     * de exception.
     */
    long getAdresID(String postcode, int huisnummer, String toevoeging) throws GeneriekeFoutmelding;

    /**
     * Geeft de adressen terug van een bepaalde klant.
     *
     * @param klant_id Klant_id van de klant waarvan de adressen opgezocht dienen te worden.
     * @return Een ListIterator van de ArrayList met daarin Klant objecten.
     * @throws GeneriekeFoutmelding Als er een fout is wordt deze doorgestuurd naar de GeneriekeFoutmelding met de message van
     * de exception.
     */
    ListIterator<Adres> getAdresOpKlantID(long klant_id) throws GeneriekeFoutmelding;

    /**
     * Maakt een nieuw adres aan en koppelt deze aan de klant.
     *
     * @param klant_id Klant_id behorende bij het adres.
     * @param adresgegevens De adresgegevens die nieuw in de database dienen te worden opgenomen.
     * @return Het adres_id van het nieuw aangemaakte adres.
     * @throws GeneriekeFoutmelding Als er een fout is wordt deze doorgestuurd naar de GeneriekeFoutmelding met de message van
     * de exception.
     */
    long nieuwAdres(long klant_id, Adres adresgegevens) throws GeneriekeFoutmelding;

    /**
     * Koppelt een bestaand adres aan een klant.
     *
     * @param klant_id Het klant_id waaraan een adres gekoppeld dient te worden
     * @param adres_id Het adres_id van het te koppelen adres.
     * @throws GeneriekeFoutmelding Als er een fout is wordt deze doorgestuurd naar de GeneriekeFoutmelding met de message van
     * de exception.
     */
    void koppelAdresAanKlant(long klant_id, long adres_id) throws GeneriekeFoutmelding;

    /**
     * Stelt de status is van een adres (0 = inactief, 1 = actief)
     *
     * @param adres_id Het adres_id van het adres dat geschakeld dient te worden.
     * @param status De nieuwe gewenste status van het adres.
     * @throws GeneriekeFoutmelding Als er een fout is wordt deze doorgestuurd naar de GeneriekeFoutmelding met de message van
     * de exception.
     */
    void schakelStatusAdres(long adres_id, int status) throws GeneriekeFoutmelding;
}
