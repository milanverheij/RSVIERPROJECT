package interfaces;

import model.Adres;
import model.Klant;

import javax.xml.transform.Result;
import java.sql.ResultSet;
import java.util.ListIterator;

/**
 * Created by Milan_Verheij on 06-06-16.
 *
 * Interface voor KlantDAO's. Schrijft voor elke KlantDAO voor welke
 * CRUD's er geimplementeerd moeten worden.
 *
 * Gebruikt de Klant POJO / model.
 *
 */


public interface KlantDAO {

    /** CREATE */
    void nieuweKlant(String voornaam,
                     String achternaam,
                     Adres adresgegevens);

    void nieuweKlant(String voornaam,
                     String achternaam);

    /** READ */
    ListIterator<Klant> getAlleKlanten();

    // TODO: Tijdelijk om naar console te printen, aangezien later naar GUI gaat
    public void printKlantenInConsole();

    void getKlantOpKlant(long klantId);

    void getKlantOpKlant(String voornaam);

    void getKlantOpKlant(String voornaam,
                        String achternaam);

    void getKlantOpAdres(Adres adresgegevens);

    void getKlantOpAdres(String straatnaam);

    void getKlantOpAdres(String postcode,
                         int huisnummer);

    void getKlantOpBestelling(long bestellingId);

    /** UPDATE */
    void updateKlant(String voornaam,
                     String achternaam);

    void updateKlant(String voornaam,
                     String achternaam,
                     Adres adresgegevens);

    /** DELETE */
    void verwijderKlant(long klantId);
    void verwijderKlant(String voornaam,
                        String achternaam,
                        String tussenvoegsel);
    void verwijderKlantOpBestellingId(long bestellingId);
}
