package JUnit_tests;

import model.Adres;
import model.Artikel;
import model.Bestelling;
import model.Klant;
import mysql.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
 * is opgenomen in een specifieke methode om een ResultSet naar een ArrayList van Klanten te zetten. De ResultSet
 * naar ArrayList methode wordt impliciet getest in de meeste methoden omdat bijna elke methode hiervan afhankelijk
 * is.
 *
 * Derhalve wordt er niet getest of de getmethoden op een juiste manier een ArrayList naar een Iterator zetten
 * omdat dit een standaard librarymethode is. ArrayList.listIterator. Als er een List-Iterator terugkomt is
 * de validiteit hiervan afhankelijk van eerdergenoemde methode en de uitgevoerde query.
 *
 */
public class KlantDAOMySQLTest {
    private KlantDAOMySQL klantDAO;
    private BestellingDAOMySQL bestellingDAO;
    private long nieuweKlantID;
    private Klant tijdelijkeKlant;
    @SuppressWarnings("unused")
	private boolean nieuweKlantAangemaakt = false;
    private final String VOORNAAM = "TestKlantVoornaam4101AR1225"; // Uniek ID  TODO: Random generator kan nog (nice to have)
    private final String ACHTERNAAM = "TestKlantAchternaam4101AR1225"; // Uniek ID TODO: Random generator kan nog (nice to have)
    private final String TUSSENVOEGSEL = "TUSS";
    private final String EMAIL = "TestEmail@EmailLand.rsvier";
    private final String STRAATNAAM = "TestStraatnaam9548";
    private final String POSTCODE = "9548ZZ" ;
    private final String TOEVOEGING = "ABC";
    private final int HUISNUMMER = 9548;
    private final String WOONPLAATS = "TestWoonplaats9548";
    private Connection connection;
    private PreparedStatement statement;
    private ResultSet rs;

    @Before
    public void setUp() throws Exception {
        klantDAO = new KlantDAOMySQL();
        bestellingDAO = new BestellingDAOMySQL();
    }

    @After
    public void tearDown() throws Exception {
        // Vangnet
        klantDAO.verwijderKlant(VOORNAAM, ACHTERNAAM);
        klantDAO.verwijderKlant(VOORNAAM, ACHTERNAAM + "2");
        MySQLHelper.close(connection, statement, rs);

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

        tijdelijkeKlant = null; // TODO: OVeral fixen met wat het wel meot zijn
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
                null, null);

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
        // De nieuwe klant-methode met voor, achternaam en Beslling gebruikt de nieuweKlant methode met alle
        // klantgegevens en/of adres & bestelling. Derhalve wordt in deze test eigenlijk een juiste
        // koppeling getest als de nieuweKlant methode met alle gegevens werkt.
        //
        // Als er geen Bestelgegevens worden meegegeven (null) wordt er standaard een lege
        // Bestelling aangemaakt. Het aanmaken (of eigenlijk updaten o.b.v. klantID wordt afzonderlijk getest
        //
        // Deze methode test niet of dat de juiste gegegevens worden geschreven naar de Database. Het kan echter
        // wel voorkomen dat als deze methode en fout geeft omdat er een fout is tijdens het schrijven naar de Database
        // dat de test faalt.
        //
        // Het test verder enkel of dat de BestellingDAO met de juiste gegevens is aangesproken.
        //
        // Dit is het geval als het testAdres-object (static) in de BestellingDAO op de juiste gegevens staan.
        // Er wordt in Bestelling gekeken of er toevallig getest wordt, als ja dan wordt het test adres tijdelijk
        // opgeslagen in BestellingDAOMySQL.aangeroepenAdresInTest.

        nieuweKlantAangemaakt = true;
        BestellingDAOMySQL.bestellingWordGetest = true; // Testconditie in BestellingDAOMySQL goed zetten.
        Artikel a1 = new Artikel(333, "Mecronomicon", 3.33);
        Artikel a2 = new Artikel(321, "Woynich Manuscript", 3.21);
        Artikel a3 = new Artikel(888, "Nunich Manual of Demonic Magic", 8.88);

        nieuweKlantID = klantDAO.nieuweKlant(VOORNAAM, ACHTERNAAM, TUSSENVOEGSEL, EMAIL, null,
                                new Bestelling(0, a1, a2, a3)); // ID wordt in de nieuweKlant methode op het juiste ID geze,

        assertTrue(nieuweKlantID != 0); // Zeker van zijn dat er wel een goed ID wordt gegeven.
        tijdelijkeKlant = klantDAO.getKlantOpKlant(nieuweKlantID).next(); // Methode geeft ListIterator terug.

        assertEquals(nieuweKlantID, BestellingDAOMySQL.aangeroepenBestellingInTest.getKlant_id());
        LinkedHashMap<Artikel, Integer> artikelLijst = BestellingDAOMySQL.aangeroepenBestellingInTest.getArtikelLijst();

        // In de bestelling moeten de volgende artikelen 1 x aanwezig zijn
        // Er mogen niet meer dan drie artikelen aanwezig zijn.
        assertTrue(artikelLijst.size() == 3);
        assertEquals(1, (long)artikelLijst.get(a1));
        assertEquals(1, (long)artikelLijst.get(a2));
        assertEquals(1, (long)artikelLijst.get(a3));

        // Alle waarden weer naar default
        BestellingDAOMySQL.bestellingWordGetest = false;
        BestellingDAOMySQL.aangeroepenBestellingInTest = new Bestelling(1,
                new Artikel(666, "Necronomicon", 6.66),
                new Artikel(123, "Voynich Manuscript", 1.23),
                new Artikel(999, "Munich Manual of Demonic Magic", 9.99));
        tijdelijkeKlant = null;
        nieuweKlantID = -1;
    }

    @Test
    public void getAlleKlanten() throws Exception {
        // Deze test controleert of daadwerkelijk alle klanten worden opgevraagd en zijn gegeven.
        // Er wordt een query uitgevoerd om te peilen hoeveel klanten er zijn waarna deze wordt vergeleken
        // met de grootte van de lijst.

        connection = MySQLConnectieLeverancier.getConnection();
        String query = "SELECT COUNT(*) FROM KLANT";
        statement = connection.prepareStatement(query);
        rs = statement.executeQuery();
        int aantalKlantenInDB = 0;
        int aantalKlantenInList = 0;

        while (rs.next())
            aantalKlantenInDB = rs.getInt(1);

        ListIterator<Klant> klantenIterator = klantDAO.getAlleKlanten();
        while (klantenIterator.hasNext()) {
            klantenIterator.next();
            aantalKlantenInList++;
        }

        assertEquals(aantalKlantenInDB, aantalKlantenInList);

        MySQLHelper.close(connection, statement, rs);
    }

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
    public void getKlantOpKlantVoornaam() throws Exception {
        // Deze methode test of dat de juiste klant wordt gevonden op basis van voornaam
        // Om te controleren of daadwerkelijke alle klanten met dezelfde voornaam worden
        // meegegeven worden er twee klanten met dezelfde voornaam aangemaakt. Maar met
        // net andere andere gegegevens. Vervolgens wordt nagegaan of deze gegevens ook
        // daadwerkelijk zijn teruggegeven. Vervolgens worden deze klanten weer verwijderd.

        nieuweKlantAangemaakt = true;
        klantDAO.nieuweKlant(VOORNAAM, ACHTERNAAM, TUSSENVOEGSEL, EMAIL, null, null);
        klantDAO.nieuweKlant(VOORNAAM, ACHTERNAAM + "2", TUSSENVOEGSEL + "2", EMAIL + "2", null, null);

        ListIterator<Klant> klantIterator = klantDAO.getKlantOpKlant(VOORNAAM);
        ArrayList<Klant> klantenLijst = new ArrayList<>();
        while (klantIterator.hasNext()) {
            klantenLijst.add(klantIterator.next());
        }

        assertEquals(VOORNAAM, klantenLijst.get(0).getVoornaam());
        assertEquals(ACHTERNAAM, klantenLijst.get(0).getAchternaam());
        assertEquals(TUSSENVOEGSEL, klantenLijst.get(0).getTussenvoegsel());
        assertEquals(EMAIL, klantenLijst.get(0).getEmail());

        assertEquals(VOORNAAM, klantenLijst.get(1).getVoornaam());
        assertEquals(ACHTERNAAM + "2", klantenLijst.get(1).getAchternaam());
        assertEquals(TUSSENVOEGSEL + "2", klantenLijst.get(1).getTussenvoegsel());
        assertEquals(EMAIL + "2", klantenLijst.get(1).getEmail());

        klantDAO.verwijderKlant(VOORNAAM, ACHTERNAAM);
        klantDAO.verwijderKlant(VOORNAAM, ACHTERNAAM + "2");
    }

    @Test
    public void getKlantOpKlantVoornaamAchternaam() throws Exception {
        // Deze methode test of dat de juiste klant wordt gevonden op basis van voornaam en achternaam
        // Om te controleren of daadwerkelijke alle klanten met dezelfde voornaam worden
        // meegegeven worden er twee klanten met dezelfde voornaam aangemaakt. Maar met
        // net andere andere gegegevens. Vervolgens wordt nagegaan of deze gegevens ook
        // daadwerkelijk zijn teruggegeven. Vervolgens worden deze klanten weer verwijderd.

        nieuweKlantAangemaakt = true;
        klantDAO.nieuweKlant(VOORNAAM, ACHTERNAAM, TUSSENVOEGSEL, EMAIL, null, null);
        klantDAO.nieuweKlant(VOORNAAM, ACHTERNAAM, TUSSENVOEGSEL + "2", EMAIL + "2", null, null);

        ListIterator<Klant> klantIterator = klantDAO.getKlantOpKlant(VOORNAAM);
        ArrayList<Klant> klantenLijst = new ArrayList<>();
        while (klantIterator.hasNext()) {
            klantenLijst.add(klantIterator.next());
        }

        assertEquals(VOORNAAM, klantenLijst.get(0).getVoornaam());
        assertEquals(ACHTERNAAM, klantenLijst.get(0).getAchternaam());
        assertEquals(TUSSENVOEGSEL, klantenLijst.get(0).getTussenvoegsel());
        assertEquals(EMAIL, klantenLijst.get(0).getEmail());

        assertEquals(VOORNAAM, klantenLijst.get(1).getVoornaam());
        assertEquals(ACHTERNAAM, klantenLijst.get(1).getAchternaam());
        assertEquals(TUSSENVOEGSEL + "2", klantenLijst.get(1).getTussenvoegsel());
        assertEquals(EMAIL + "2", klantenLijst.get(1).getEmail());

        klantDAO.verwijderKlant(VOORNAAM, ACHTERNAAM);
    }

    @Test
    public void getKlantOpVolledigAdres() throws Exception {
        // Deze methode test of dat de juiste klant wordt gevonden op basis van de volledige adresdegevens
        // Om te controleren of daadwerkelijke alle klanten met dezelfde voornaam worden
        // meegegeven worden er twee klanten met dezelfde voornaam aangemaakt. Maar met
        // net andere andere gegegevens. Vervolgens wordt nagegaan of deze gegevens ook
        // daadwerkelijk zijn teruggegeven. Vervolgens worden deze klanten weer verwijderd.

        nieuweKlantAangemaakt = true;
        Adres tijdelijkAdres = new Adres(STRAATNAAM, POSTCODE, TOEVOEGING, HUISNUMMER, WOONPLAATS);
        klantDAO.nieuweKlant(VOORNAAM, ACHTERNAAM, TUSSENVOEGSEL, EMAIL, tijdelijkAdres, null);
        klantDAO.nieuweKlant(VOORNAAM, ACHTERNAAM + "2", TUSSENVOEGSEL, EMAIL, tijdelijkAdres, null);

        ListIterator<Klant> klantIterator = klantDAO.getKlantOpAdres(tijdelijkAdres);
        ArrayList<Klant> klantenLijst = new ArrayList<>();
        while (klantIterator.hasNext()) {
            klantenLijst.add(klantIterator.next());
        }

        // Check op klant 1 gevonden
        assertEquals(ACHTERNAAM, klantenLijst.get(0).getAchternaam());

        // Check of klant twee ook werd gevonden
        assertEquals(ACHTERNAAM + "2", klantenLijst.get(1).getAchternaam());

        klantDAO.verwijderKlant(VOORNAAM, ACHTERNAAM);
        klantDAO.verwijderKlant(VOORNAAM, ACHTERNAAM + "2");
    }

    @Test
    public void getKlantOpAdresStraatnaam() throws Exception {
        // Deze methode test of dat de juiste klant wordt gevonden op basis van de straatnaam
        // Om te controleren of daadwerkelijke alle klanten met dezelfde voornaam worden
        // meegegeven worden er twee klanten met dezelfde voornaam aangemaakt. Maar met
        // net andere andere gegegevens. Vervolgens wordt nagegaan of deze gegevens ook
        // daadwerkelijk zijn teruggegeven. Vervolgens worden deze klanten weer verwijderd.

        nieuweKlantAangemaakt = true;
        Adres tijdelijkAdres = new Adres(STRAATNAAM, POSTCODE, TOEVOEGING, HUISNUMMER, WOONPLAATS);
        klantDAO.nieuweKlant(VOORNAAM, ACHTERNAAM, TUSSENVOEGSEL, EMAIL, tijdelijkAdres, null);
        klantDAO.nieuweKlant(VOORNAAM, ACHTERNAAM + "2", TUSSENVOEGSEL, EMAIL, tijdelijkAdres, null);

        ListIterator<Klant> klantIterator = klantDAO.getKlantOpAdres(STRAATNAAM);
        ArrayList<Klant> klantenLijst = new ArrayList<>();
        while (klantIterator.hasNext()) {
            klantenLijst.add(klantIterator.next());
        }

        // Check op klant 1 gevonden
        assertEquals(ACHTERNAAM, klantenLijst.get(0).getAchternaam());

        // Check of klant twee ook werd gevonden
        assertEquals(ACHTERNAAM + "2", klantenLijst.get(1).getAchternaam());

        klantDAO.verwijderKlant(VOORNAAM, ACHTERNAAM);
        klantDAO.verwijderKlant(VOORNAAM, ACHTERNAAM + "2");
        tijdelijkeKlant = null;
    }

    @Test
    public void getKlantOpAdresPostcodeHuisNR() throws Exception {
        // Deze methode test of dat de juiste klant wordt gevonden op basis van de postcode en het huisnummer
        // Om te controleren of daadwerkelijke alle klanten met dezelfde voornaam worden
        // meegegeven worden er twee klanten met dezelfde voornaam aangemaakt. Maar met
        // net andere andere gegegevens. Vervolgens wordt nagegaan of deze gegevens ook
        // daadwerkelijk zijn teruggegeven. Vervolgens worden deze klanten weer verwijderd.

        nieuweKlantAangemaakt = true;
        Adres tijdelijkAdres = new Adres(STRAATNAAM, POSTCODE, TOEVOEGING, HUISNUMMER, WOONPLAATS);
        klantDAO.nieuweKlant(VOORNAAM, ACHTERNAAM, TUSSENVOEGSEL, EMAIL, tijdelijkAdres, null);
        klantDAO.nieuweKlant(VOORNAAM, ACHTERNAAM + "2", TUSSENVOEGSEL, EMAIL, tijdelijkAdres, null);

        ListIterator<Klant> klantIterator = klantDAO.getKlantOpAdres(POSTCODE, HUISNUMMER);
        ArrayList<Klant> klantenLijst = new ArrayList<>();
        while (klantIterator.hasNext()) {
            klantenLijst.add(klantIterator.next());
        }

        // Check op klant 1 gevonden
        assertEquals(ACHTERNAAM, klantenLijst.get(0).getAchternaam());

        // Check of klant twee ook werd gevonden
        assertEquals(ACHTERNAAM + "2", klantenLijst.get(1).getAchternaam());

        klantDAO.verwijderKlant(VOORNAAM, ACHTERNAAM);
        klantDAO.verwijderKlant(VOORNAAM, ACHTERNAAM + "2");
        tijdelijkeKlant = null;
    }

    @Test
    public void getKlantOpBestelling() throws Exception {
        // Deze methode checkt of dat de juiste klant worden opgehaald op basis van
        // het opgegeven Bestel_ID

        // Er wordt een nieuwe klant gemaakt met een bestelling waarna deze wordt verwijderd.
        BestellingDAOMySQL.bestellingWordGetest = true;

        // Nieuwe klant aanmaken en klantID achterhalen, wordt altijd in teardown weer verwijderd
        nieuweKlantID = klantDAO.nieuweKlant(VOORNAAM, ACHTERNAAM, TUSSENVOEGSEL, EMAIL, null, null);

        // Aan de hand van klantID een bestelling toevoegen aan de klant. Is getest in test hierboven.
        long nieuweBestellingID = bestellingDAO.nieuweBestelling(nieuweKlantID,
                new Artikel(333, "Mecronomicon", 3.33),
                new Artikel(321, "Woynich Manuscript", 3.21),
                new Artikel(888, "Nunich Manual of Demonic Magic", 8.88));

        tijdelijkeKlant = klantDAO.getKlantOpBestelling(nieuweBestellingID).next();

        assertEquals(VOORNAAM, tijdelijkeKlant.getVoornaam());
        assertEquals(ACHTERNAAM, tijdelijkeKlant.getAchternaam());
        assertEquals(TUSSENVOEGSEL, tijdelijkeKlant.getTussenvoegsel());
        assertEquals(EMAIL, tijdelijkeKlant.getEmail());

        // Alle waarden weer resetten naar default
        tijdelijkeKlant = null;
        BestellingDAOMySQL.bestellingWordGetest = false;
        BestellingDAOMySQL.aangeroepenBestellingInTest = new Bestelling(1,
                new Artikel(666, "Necronomicon", 6.66),
                new Artikel(123, "Voynich Manuscript", 1.23),
                new Artikel(999, "Munich Manual of Demonic Magic", 9.99));

        nieuweKlantID = -1;
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
        // In deze test wordt niet getest of dat de bestelling daadwerkelijk verwijderd is. Er wordt gelinkt
        // naar de verwijderKlantOpID methode welke hiervoor al is getest. Er wordt wel getest of de juiste
        // klant naar de BestellingDAO is gestuurd.
        //
        // Er wordt een nieuwe klant gemaakt met een bestelling waarna deze wordt verwijderd.

        BestellingDAOMySQL.bestellingWordGetest = true;

        // Nieuwe klant aanmaken en klantID achterhalen, wordt altijd in teardown weer verwijderd
        nieuweKlantID = klantDAO.nieuweKlant(VOORNAAM, ACHTERNAAM, TUSSENVOEGSEL, EMAIL, null, null);

        // Aan de hand van klantID een bestelling toevoegen aan de klant. Is getest in test hierboven.
        long nieuweBestellingID = bestellingDAO.nieuweBestelling(nieuweKlantID,
                                new Artikel(333, "Mecronomicon", 3.33),
                                new Artikel(321, "Woynich Manuscript", 3.21),
                                new Artikel(888, "Nunich Manual of Demonic Magic", 8.88));

        // Klantverwijder methode aanroepen op basis van bestelling-ID
        long verwijderdId = klantDAO.verwijderKlantOpBestellingId(nieuweBestellingID);

        // Als het goed is zou de juiste klant gevonden moeten zijn en het juiste klant_id doorgegeven
        assertEquals(nieuweKlantID, verwijderdId);

        BestellingDAOMySQL.bestellingWordGetest = false;
        BestellingDAOMySQL.aangeroepenBestellingInTest = new Bestelling(1,
                new Artikel(666, "Necronomicon", 6.66),
                new Artikel(123, "Voynich Manuscript", 1.23),
                new Artikel(999, "Munich Manual of Demonic Magic", 9.99));

        nieuweKlantID = -1;
    }
}