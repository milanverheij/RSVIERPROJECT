package JUnit_tests;

import interfaces.AdresDAO;
import model.Adres;
import model.Bestelling;
import model.Klant;
import mysql.AdresDAOMySQL;
import mysql.KlantDAOMySQL;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.awt.datatransfer.DataFlavor;
import java.util.ListIterator;

import static org.junit.Assert.*;

/**
 * Created by Milan_Verheij on 10-06-16.
 *
 * J-Unit testClass van KlantDAOMySQL
 *
 * Het betreffen hoofdzakelijk basis CRUD-methoden.
 *
 * Bijzonderheden zijn dat de get-methoden ListIterators van LinkedLists teruggeven, het Klant-specifieke deel
 * is opgenomen in een specifieke methode om een ResultSet naar een ArrayList van Klanten te zetten.
 *
 * Derhalve wordt er niet getest of de getmethoden op een juiste manier een ArrayList naar een Iterator zetten
 * omdat dit een standaard librarymethode is. ArrayList.listIterator. Als er een List-Iterator terugkomt is
 * de validiteit hiervan afhankelijk van eerdergenoemde methode en de uitgevoerde query.
 *
 */
public class KlantDAOMySQLTest {
    private KlantDAOMySQL klantDAO;
    private long nieuweKlantID;
    private Klant tijdelijkeKlant;
    private boolean nieuweKlantAangemaakt = false;
    private final String VOORNAAM = "TestKlantVoornaam4101AR1225"; // Uniek ID  TODO: Random generator
    private final String ACHTERNAAM = "TestKlantAchternaam4101AR1225"; // Uniek ID TODO: Random generator
    private final String TUSSENVOEGSEL = "TUSS";
    private final String EMAIL = "TestEmail@EmailLand.rsvier";
    private final String STRAATNAAM = "TestStraatnaam9548";
    private final String POSTCODE = "9548ZZ" ;
    private final String TOEVOEGING = "ABC";
    private final int HUISNUMMER = 9548;
    private final String WOONPLAATS = "TestWoonplaats9548";

    @Before
    public void setUp() throws Exception {
        klantDAO = new KlantDAOMySQL();
    }

    @After
    public void tearDown() throws Exception {
        // Als er ergens in een test een nieuwe klant is aangemaakt, wordt deze verwijderd. Helaas wel
        // AFHANKELIJK van de verwijdermethode.
        if (nieuweKlantAangemaakt)
            klantDAO.verwijderKlant(VOORNAAM, ACHTERNAAM);

        AdresDAOMySQL.klantWordtGetest = false; // Mocht er iets fout zijn gegaan in de tests..
    }

    // De nieuwe klant-methoden zijn helaas afhankelijk van de get-methoden om te kunnen vaststellen
    // dat de DATA inderdaad juiste in de MYSQL-database is opgenomen.
    // Tijdens de nieuwe klant methoden wordt ook direct getest of er wel een ID wordt aangemaakt en teruggeven.
    // Het is niet zinvol dit los te testen.

    @Test
    public void nieuweKlantVoorAchternaamAdresNullIsLeeg() throws Exception {

        // De nieuwe klant-methode met voor, achternaam en Adres gebruikt de nieuweKlant methode met alle
        // klantgegevens en/of adres & bestelling. Derhalve wordt in deze test eigenlijk een juiste
        // koppeling getest als de nieuweKlant methode met alle gegevens werkt.
        //
        // Als er geen adresgegevens worden meegegeven (null) wordt er standaard een leeg
        // Adres aangemaakt omdat er null wordt meegegeven naar de AdresDAO. Aldaar wordt als er null
        // wordt geconstateerd een leeg adres gemaakt (dit valt buiten de scope van deze test).
        //
        // Deze methode test niet of dat de juiste gegevens naar de database worden verstuurd, dat doet
        // de AdresDAOMySQLTest. Er wordt enkel gekeken of de juiste gegevens zijn doorgegeven aan AdresDAO (null)
        // Dit kan doordat AdresDAOMySQL test of dat haar testwaarde op true staat en als dit het geval is
        // wordt het adres tijdelijk opgeslagen.

        nieuweKlantAangemaakt = true;
        AdresDAOMySQL.klantWordtGetest = true; // Testconditie in AdresDAOMySQL goed zetten.
        nieuweKlantID = klantDAO.nieuweKlant(VOORNAAM, ACHTERNAAM, null);

        assertTrue(nieuweKlantID != 0); // Zeker van zijn dat er wel een goed ID wordt gegeven.
        tijdelijkeKlant = klantDAO.getKlantOpKlant(nieuweKlantID).next(); // Methode geeft ListIterator terug.

        assertEquals(VOORNAAM, tijdelijkeKlant.getVoornaam());
        assertEquals(ACHTERNAAM, tijdelijkeKlant.getAchternaam());
        assertTrue((AdresDAOMySQL.aangeroepenAdresInTest == null));


        AdresDAOMySQL.klantWordtGetest = false;
        tijdelijkeKlant = null;
        nieuweKlantID = -1;
    }

    @Test
    public void nieuweKlantAdresMethodeJuistAangesproken() throws Exception {

        // De nieuwe klant-methode met voor, achternaam en Adres gebruikt de nieuweKlant methode met alle
        // klantgegevens en/of adres & bestelling. Derhalve wordt in deze test eigenlijk een juiste
        // koppeling getest als de nieuweKlant methode met alle gegevens werkt.
        //
        // Als er geen adresgegevens worden meegegeven (null) wordt er standaard een leeg
        // Adres aangemaakt. Het aanmaken (of eigenlijk updaten o.b.v. klantID wordt afzonderlijk getest
        //
        // Deze methode test niet of dat de juiste gegegevens worden geschreven naar de Database. Het kan echter
        // wel voorkomen dat als deze methode en fout geeft omdat er een fout is tijdens het schrijven naar de Database
        // dat de test faalt.
        //
        // Het test verder enkel of dat de AdresDAO met de juiste gegevens is aangesproken.
        //
        // Dit is het geval als het testAdres-object (static) in de AdresDAO op de juiste gegevens staan.
        // Er wordt in AdresDAO gekeken of er toevallig getest wordt, als ja dan wordt het test adres tijdelijk
        // opgeslagen in AdresDAOMySQL.aangeroepenAdresInTest.

        nieuweKlantAangemaakt = true;
        AdresDAOMySQL.klantWordtGetest = true; // Testconditie in AdresDAOMySQL goed zetten.
        nieuweKlantID = klantDAO.nieuweKlant(VOORNAAM, ACHTERNAAM,
                new Adres(STRAATNAAM, POSTCODE, TOEVOEGING, HUISNUMMER, WOONPLAATS));

        assertTrue(nieuweKlantID != 0); // Zeker van zijn dat er wel een goed ID wordt gegeven.
        tijdelijkeKlant = klantDAO.getKlantOpKlant(nieuweKlantID).next(); // Methode geeft ListIterator terug.

        assertEquals(STRAATNAAM, AdresDAOMySQL.aangeroepenAdresInTest.getStraatnaam());
        assertEquals(POSTCODE, AdresDAOMySQL.aangeroepenAdresInTest.getPostcode());
        assertEquals(TOEVOEGING, AdresDAOMySQL.aangeroepenAdresInTest.getToevoeging());
        assertEquals(HUISNUMMER, AdresDAOMySQL.aangeroepenAdresInTest.getHuisnummer());
        assertEquals(WOONPLAATS, AdresDAOMySQL.aangeroepenAdresInTest.getWoonplaats());

        AdresDAOMySQL.aangeroepenAdresInTest = null; // Resetten TestAdres
        AdresDAOMySQL.klantWordtGetest = false;
        tijdelijkeKlant = null;
        nieuweKlantID = -1;
    }

    @Test
    public void nieuweKlantVoorAchternaam() throws Exception {
        // De nieuwe klant-methode met voor- en achternaam gebruikt de nieuweKlant methode met alle
        // klantgegevens en/of adres & bestelling. Derhalve wordt in deze test eigenlijk een juiste
        // koppeling getest als de nieuweKlant methode met alle gegevens werkt.
        // TODO: IVM koppeling tussen methoden, allicht een Klant laten returnen en die checken?

        nieuweKlantAangemaakt = true;
        nieuweKlantID = klantDAO.nieuweKlant(VOORNAAM, ACHTERNAAM);

        assertTrue(nieuweKlantID != 0); // Zeker van zijn dat er wel een goed ID wordt gegeven.
        tijdelijkeKlant = klantDAO.getKlantOpKlant(nieuweKlantID).next(); // Methode geeft ListIterator terug.

        assertEquals(VOORNAAM, tijdelijkeKlant.getVoornaam());
        assertEquals(ACHTERNAAM, tijdelijkeKlant.getAchternaam());

        tijdelijkeKlant = null;
        nieuweKlantID = -1;
    }

    @Test
    public void nieuweKlant() throws Exception {
        // In deze methode wordt getest of dat alle gegevens juist naar de database worden weggeschreven
        // als er zowel een Adres als Bestelling wordt meegegeven.
        //
        // Er wordt niet getest of de juiste Adres en / of Bestelgegevens worden meegegeven naar de Adres
        // en / of BestelDAO. Dat wordt in de hieropvolgende tests getest.

        nieuweKlantAangemaakt = true;
        nieuweKlantID = klantDAO.nieuweKlant(VOORNAAM, ACHTERNAAM, TUSSENVOEGSEL, EMAIL,
                null, null); // TODO: Geeft een fout bij lege Bestelling omdat er geen constructor aanwezig is in Bestelling

        tijdelijkeKlant = klantDAO.getKlantOpKlant(nieuweKlantID).next(); // Methode geeft ListIterator terug.

        assertEquals(VOORNAAM, tijdelijkeKlant.getVoornaam());
        assertEquals(ACHTERNAAM, tijdelijkeKlant.getAchternaam());
        assertEquals(TUSSENVOEGSEL, tijdelijkeKlant.getTussenvoegsel());
        assertEquals(EMAIL, tijdelijkeKlant.getEmail());

        tijdelijkeKlant = null;
        nieuweKlantID = -1;
    }

    @Test
    public void nieuweKlantEnBestelling() throws Exception {
        // TODO: Overleg met Albert ivm boolean en testBestelObject
    }

    @Test
    public void getAlleKlanten() throws Exception {

    }

    // TODO: Dit is eigenlijk precies hetzelfde als het aanmaken van een klant?
    @Test
    public void getKlantOpKlant() throws Exception {
        // Deze methode test of dat de juiste klant wordt gevonden op basis van klantID

        nieuweKlantAangemaakt = true;
        nieuweKlantID = klantDAO.nieuweKlant(VOORNAAM, ACHTERNAAM, TUSSENVOEGSEL, EMAIL, null, null);

        tijdelijkeKlant = klantDAO.getKlantOpKlant(nieuweKlantID).next();

        assertEquals(VOORNAAM, tijdelijkeKlant.getVoornaam());
        assertEquals(ACHTERNAAM, tijdelijkeKlant.getAchternaam());
        assertEquals(TUSSENVOEGSEL, tijdelijkeKlant.getTussenvoegsel());
        assertEquals(EMAIL, tijdelijkeKlant.getEmail());

        tijdelijkeKlant = null;
        nieuweKlantID = -1;
    }

    @Test
    public void getKlantOpKlant1() throws Exception {
        // Deze methode test of dat de juiste klant wordt gevonden op basis van voornaam

        nieuweKlantAangemaakt = true;
        klantDAO.nieuweKlant(VOORNAAM, ACHTERNAAM, TUSSENVOEGSEL, EMAIL, null, null);
        klantDAO.nieuweKlant(VOORNAAM, ACHTERNAAM + "2", TUSSENVOEGSEL + "2", EMAIL + "2", null, null);

        tijdelijkeKlant = klantDAO.getKlantOpKlant(VOORNAAM).next();
        Klant tijdelijkeKlant2 = klantDAO.getKlantOpKlant(VOORNAAM).next();

        assertEquals(VOORNAAM, tijdelijkeKlant.getVoornaam());
        assertEquals(ACHTERNAAM, tijdelijkeKlant.getAchternaam());
        assertEquals(TUSSENVOEGSEL, tijdelijkeKlant.getTussenvoegsel());
        assertEquals(EMAIL, tijdelijkeKlant.getEmail());

        assertEquals(VOORNAAM, tijdelijkeKlant2.getVoornaam());
        assertEquals(ACHTERNAAM + "2", tijdelijkeKlant2.getAchternaam());
        assertEquals(TUSSENVOEGSEL + "2", tijdelijkeKlant2.getTussenvoegsel());
        assertEquals(EMAIL + "2", tijdelijkeKlant2.getEmail());

        tijdelijkeKlant = null;
    }

    @Test
    public void getKlantOpKlant2() throws Exception {

    }

    @Test
    public void getKlantOpAdres() throws Exception {

    }

    @Test
    public void getKlantOpAdres1() throws Exception {

    }

    @Test
    public void getKlantOpAdres2() throws Exception {

    }

    @Test
    public void getKlantOpBestelling() throws Exception {

    }

    @Test
    public void updateKlantGegeven() throws Exception {
        // Deze methode test of de juiste klantGegevens worden geUpdate als een klantID wordt meegegeven
        nieuweKlantAangemaakt = true;
        nieuweKlantID = klantDAO.nieuweKlant("", "", "", "", null, null); // Leeg

        klantDAO.updateKlant(nieuweKlantID, VOORNAAM, ACHTERNAAM, TUSSENVOEGSEL, EMAIL);

        // Vraag klant op en controleer de inhoud
        tijdelijkeKlant = klantDAO.getKlantOpKlant(nieuweKlantID).next();
        assertEquals(VOORNAAM, tijdelijkeKlant.getVoornaam());
        assertEquals(ACHTERNAAM, tijdelijkeKlant.getAchternaam());
        assertEquals(TUSSENVOEGSEL, tijdelijkeKlant.getTussenvoegsel());
        assertEquals(EMAIL, tijdelijkeKlant.getEmail());

        tijdelijkeKlant = null;
    }

    @Test
    public void updateKlantJuisteAdresGegevensMeegegeven() throws Exception {
        // Deze methode test niet of dat de juiste gegevens in de database worden geschreven, dat wordt
        // in de methode hierboven gedaan. Die methode wordt ook simpelweg aangeroepen in deze methode.
        // Deze methode test of de juiste klantGegevens worden geUpdate als een klantID wordt meegegeven
        nieuweKlantAangemaakt = true;
        nieuweKlantID = klantDAO.nieuweKlant("", "", "", "", null, null); // Leeg

        AdresDAOMySQL.klantWordtGetest = true;
        klantDAO.updateKlant(nieuweKlantID, VOORNAAM, ACHTERNAAM, TUSSENVOEGSEL, EMAIL,
                new Adres(STRAATNAAM, POSTCODE, TOEVOEGING, HUISNUMMER, WOONPLAATS));

        // Controleer of de juiste adresgegevens zijn meegegeven aan Adres
        assertEquals(STRAATNAAM, AdresDAOMySQL.aangeroepenAdresInTest.getStraatnaam());
        assertEquals(POSTCODE, AdresDAOMySQL.aangeroepenAdresInTest.getPostcode());
        assertEquals(TOEVOEGING, AdresDAOMySQL.aangeroepenAdresInTest.getToevoeging());
        assertEquals(HUISNUMMER, AdresDAOMySQL.aangeroepenAdresInTest.getHuisnummer());
        assertEquals(WOONPLAATS, AdresDAOMySQL.aangeroepenAdresInTest.getWoonplaats());

        tijdelijkeKlant = null;
        AdresDAOMySQL.aangeroepenAdresInTest = null; // Resetten ADRES
        AdresDAOMySQL.klantWordtGetest = false;
    }

    @Test
    public void updateKlantJuisteBestellingGegevensMeegegeven() throws Exception {
        // TODO: Zie verwijdermethode, Albert
    }

    @Test
    public void verwijderKlantOpID() throws Exception {
        // Deze methode test of dat een aangemaakte klant daadwerkelijk wordt verwijderd als het juiste klantID
        // wordt meegeven.
        nieuweKlantAangemaakt = true;
        nieuweKlantID = klantDAO.nieuweKlant(VOORNAAM, ACHTERNAAM);

        klantDAO.verwijderKlant(nieuweKlantID);

        // Als het klant_ID niet meer in de lijst voorkomt wanneer er gezocht wordt op klantID klopt dit
        ListIterator<Klant> klantenLijst = klantDAO.getKlantOpKlant(nieuweKlantID);
        while (klantenLijst.hasNext()) {
            tijdelijkeKlant = klantenLijst.next();
            assertTrue(tijdelijkeKlant.getKlant_id() != nieuweKlantID);
        }

        tijdelijkeKlant = null;
    }

    @Test
    public void verwijderKlantOpVoorAchternaam() throws Exception {
        // Deze methode test of dat een aangemaakte klant daadwerkelijk wordt verwijderd als de juiste
        // voor- en achternaam worden meegegeven.
        nieuweKlantAangemaakt = true;
        nieuweKlantID = klantDAO.nieuweKlant(VOORNAAM, ACHTERNAAM);

        klantDAO.verwijderKlant(VOORNAAM, ACHTERNAAM);

        // Als het klant_ID niet meer in de lijst voorkomt wanneer er gezocht wordt op klantID klopt dit
        ListIterator<Klant> klantenLijst = klantDAO.getKlantOpKlant(nieuweKlantID);
        while (klantenLijst.hasNext()) {
            tijdelijkeKlant = klantenLijst.next();
            assertTrue(tijdelijkeKlant.getKlant_id() != nieuweKlantID);
        }

        tijdelijkeKlant = null;
    }

    @Test
    public void verwijderKlantOpVoorAchterNaamTussenv() throws Exception {
        // Deze methode test of dat een aangemaakte klant daadwerkelijk wordt verwijderd als de juiste
        // voor, achternaam en tussenvoegsel worden meegegeven.
        nieuweKlantAangemaakt = true;
        nieuweKlantID = klantDAO.nieuweKlant(VOORNAAM, ACHTERNAAM, TUSSENVOEGSEL, "", null, null);

        klantDAO.verwijderKlant(VOORNAAM, ACHTERNAAM, TUSSENVOEGSEL);

        // Als het klant_ID niet meer in de lijst voorkomt wanneer er gezocht wordt op klantID klopt dit
        ListIterator<Klant> klantenLijst = klantDAO.getKlantOpKlant(nieuweKlantID);
        while (klantenLijst.hasNext()) {
            tijdelijkeKlant = klantenLijst.next();
            assertTrue(tijdelijkeKlant.getKlant_id() != nieuweKlantID);
        }

        tijdelijkeKlant = null;
    }

    @Test
    public void verwijderKlantOpBestellingId() throws Exception {
        // TODO: Vragen aan Albert of hij standaard als je een bestelling aanmaakt het BestelID retourneert.
        //TODO: Test of juiste klantID wordt meegegeven aan BestellingVerwijderen
    }

}