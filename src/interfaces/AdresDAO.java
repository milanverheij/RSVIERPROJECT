package interfaces;

import exceptions.RSVIERException;
import model.Adres;

/**
 * Created by Milan_Verheij on 08-06-16.
 * Adres DAO Interface. Gooit een RSVIERException bij fouten.
 *
 */
public interface AdresDAO {
    /**
     * Update een adres bij een klant.
     *
     * @param klant_id Het klant_id om up te daten.
     * @param adresgegevens De adresgegevens om te updaten in Adres object formaat
     * @throws RSVIERException Foutmelding bij SQL exception.
     */
    void updateAdres(long klant_id, Adres adresgegevens) throws RSVIERException;
    long nieuwAdres(long klant_id, Adres adresgegevens) throws RSVIERException;
    Adres getAdres(long klant_id) throws RSVIERException;
}
