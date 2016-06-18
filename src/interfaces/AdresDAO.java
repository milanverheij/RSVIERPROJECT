package interfaces;

import exceptions.RSVIERException;
import model.Adres;

import java.util.ListIterator;

/**
 * Created by Milan_Verheij on 08-06-16.
 * Adres DAO Interface. Gooit een RSVIERException bij fouten.
 *
 */
public interface AdresDAO {
    /** //TODO: JAVADOC INFO aanpassen
     * Update een adres bij een klant.
     *
     * @param adres_id Het adres_id om up te daten.
     * @param adresgegevens De adresgegevens om te updaten in Adres object formaat
     * @throws RSVIERException Foutmelding bij SQL exception.
     */
    void updateAdres(long adres_id, Adres adresgegevens) throws RSVIERException;
    long getAdresID(String postcode, int huisnummer, String toevoeging) throws RSVIERException;
    ListIterator<Adres> getAdresOpKlantID(long klant_id) throws RSVIERException;
    long nieuwAdres(long klant_id, Adres adresgegevens) throws RSVIERException;
    void koppelAdresAanKlant(long klant_id, long adres_id) throws RSVIERException;
    void schakelStatusAdres(long adres_id, int status) throws RSVIERException;
}
