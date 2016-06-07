package interfaces;

import model.Adres;
import model.Bestelling;
import model.Klant;

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

    void nieuweKlant(String voornaam,
                    String achternaam,
                    String tussenvoegsel,
                    String email,
                    Adres adresgegevens,
                    Bestelling bestelGegevens);

    /** READ */
    ListIterator<Klant> getAlleKlanten();

    // TODO: Tijdelijk om naar console te printen, aangezien later naar GUI gaat
    public void printKlantenInConsole(ListIterator<Klant> klantenIterator);

    ListIterator<Klant> getKlantOpKlant(long klantId);

    ListIterator<Klant> getKlantOpKlant(String voornaam);

    ListIterator<Klant> getKlantOpKlant(String voornaam,
                        String achternaam);

    ListIterator<Klant> getKlantOpAdres(Adres adresgegevens);

    ListIterator<Klant> getKlantOpAdres(String straatnaam);

    ListIterator<Klant> getKlantOpAdres(String postcode,
                         int huisnummer);

    ListIterator<Klant> getKlantOpBestelling(long bestellingId);

    /** UPDATE */
    void updateKlant(Long klantId,
                     String voornaam,
                     String achternaam,
                     String tussenvoegsel,
                     String email);

    void updateKlant(Long KlantId,
                     String voornaam,
                     String achternaam,
                     String tussenvoegsel,
                     String email,
                     Adres adresgegevens);

    /** DELETE */
    void verwijderKlant(long klantId);
    void verwijderKlant(String voornaam,
                        String achternaam,
                        String tussenvoegsel);
    void verwijderKlantOpBestellingId(long bestellingId);
}
