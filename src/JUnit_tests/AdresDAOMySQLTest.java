package JUnit_tests;

import connection_pools.HikariCPAdapter;
import exceptions.GeneriekeFoutmelding;
import interfaces.AdresDAO;
import interfaces.KlantDAO;
import model.Adres;
import model.Klant;
import mysql.AbstractDAOMySQL;
import mysql.AdresDAOMySQL;
import mysql.KlantDAOMySQL;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

/**
 * Created by Milan_Verheij on 09-06-16.
 * Updated by Milan Verheij on 26-06-16 (nieuw DB-model).

 *
 * TestClass om AdresDAOMySQL te Testen met JUnit
 */
public class AdresDAOMySQLTest {
    private final String STRAATNAAM = "TESTSTRAATNAAM"; // Max 26 karakters
    private final String POSTCODE = "1234ZZ"; // Max 6 karakters
    private final String TOEVOEGING = "TEST"; // max 6 karakters
    private final int HUISNUMMER = 9999; // meer is onmogelijk in Nederland
    private final String WOONPLAATS = "TESTWOONPLAATS"; // Max 26 karakters
    private final String VOORNAAM = "TESTKLANTVOORNAAM4101AR1225"; // Uniek ID
    private final String ACHTERNAAM = "TESTKLANTACHTERNAAM4101AR1225"; // Uniek ID
    private final String TUSSENVOEGSEL = "TUSS";
    private final String EMAIL = "TESTEMAIL@BEESTJES.TEST";
    long klant_id_te_testen = 0; // Id is nodig voor diverse acties
    long adres_id_te_testen = 0; // Id is nodig voor diverse acties

    Adres testAdres = new Adres(STRAATNAAM, POSTCODE, TOEVOEGING, HUISNUMMER, WOONPLAATS);
    Klant testKlant = new Klant(0, VOORNAAM, ACHTERNAAM, TUSSENVOEGSEL, EMAIL, null);

    KlantDAO klantDAO = new KlantDAOMySQL();
    AdresDAO adresDAO = new AdresDAOMySQL();

    @Before
    public void setUp() throws Exception {
        // Er dient een connectionPool gebruikt te worden om een verbinding te krijgen aangezien
        // alle DAO's gebruik maken van een gedeelde link naar een connection pool in de abstract DAO.
        AbstractDAOMySQL.setConnPool(new HikariCPAdapter("MySQL"));

        // Ik moet eerste een test-klant aanmaken voordat ik een adres kan koppelen en deze methode kan testen.
        // Derhalve eerste een nieuwe klant toevoegen en het klant_id achterhalen van deze klant
        // door hierop te zoeken.
        try {
            klant_id_te_testen = klantDAO.nieuweKlant(testKlant, 0); // Adres_id = 0, er wordt nog geen adres gekoppeld.
        } catch (GeneriekeFoutmelding ex) {
            if (!ex.getMessage().contains("BESTAAT AL")) {
                throw new GeneriekeFoutmelding("FOUT TIJDENS SETUP");
            }
            klant_id_te_testen = klantDAO.getKlantOpKlant(VOORNAAM, ACHTERNAAM).next().getKlant_id();
        }
        System.out.println("KLANT_ID OM TE WORDEN GETEST = " + klant_id_te_testen);
    }

    @After
    public void tearDown() throws Exception {
        // Test-klant en adres op inactief zetten
        if (klant_id_te_testen > 0)
            klantDAO.schakelStatusKlant(klant_id_te_testen, 0);

        if (adres_id_te_testen > 0)
            adresDAO.schakelStatusAdres(adres_id_te_testen, 0);

        // Terug naar initialisatie van test-schakelaars
        AdresDAOMySQL.aangeroepenAdresInTest = new Adres("XXXXXX", "XXXX", "XX", 0000, "XXXX");
        AdresDAOMySQL.klantWordtGetest = false;
    }

    @Test
    public void TestUpdateAdresDAOnullAdres_geenException() throws Exception {
        // Als geen foutmelding is een nieuw adres gemaakt met lege waarden.
        adresDAO.updateAdres(adres_id_te_testen, null);
    }


    @Test
    public void TestUpdateAdresDAOJuisteGegevenDoorgegevent() throws Exception {
        getAdresId(); // Zet adres_id_te_testen op juiste adres_id.

        adresDAO.updateAdres(adres_id_te_testen, testAdres);
        Adres assertAdres = adresDAO.getAdresOpAdresID(adres_id_te_testen);

        assertEquals(STRAATNAAM, assertAdres.getStraatnaam());
        assertEquals(POSTCODE, assertAdres.getPostcode());
        assertEquals(TOEVOEGING, assertAdres.getToevoeging());
        assertEquals(WOONPLAATS, assertAdres.getWoonplaats());
        assertEquals(HUISNUMMER, assertAdres.getHuisnummer());
    }


    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void TestUpdateAdresDAOEenFoutieveInvoergRSVIERException() throws Exception {
        // Een te lange postcode kan niet en ik verwacht dan ook dat er een GeneriekeFoutmelding wordt gegooid
        // en wel een RSVierException
        // Dit geld voor elke foutieve invoer, het gedrag zal hetzelfde zijn.

        getAdresId();
        exception.expect(GeneriekeFoutmelding.class);

        adresDAO.updateAdres(adres_id_te_testen, new Adres(STRAATNAAM, "TELANGEPOSTCODE", TOEVOEGING, HUISNUMMER, WOONPLAATS));

        // Terug naar goede adres als faal
        adresDAO.updateAdres(adres_id_te_testen, new Adres(STRAATNAAM, POSTCODE, POSTCODE, HUISNUMMER, WOONPLAATS));
    }

    @Test
    public void TestUpdateAdresDAOConsistentGedragFoutieveInvoer() throws Exception {
        // Test of dat ook bij een andere foutieve invoer eenzelfde fout wordt gegooid, bij twee zal
        // de rest hetzelfde functioneren is de aanname.

        getAdresId();
        exception.expect(GeneriekeFoutmelding.class);

        adresDAO.updateAdres(adres_id_te_testen, new Adres(STRAATNAAM, POSTCODE, "ABCDEFG", HUISNUMMER, WOONPLAATS));

        // Terug naar goede adres als faal
        adresDAO.updateAdres(adres_id_te_testen, new Adres(STRAATNAAM, POSTCODE, POSTCODE, HUISNUMMER, WOONPLAATS));
    }


    @Test
    public void NieuwAdresEnKoppelen() throws Exception {

    }



    // Methode om adres_id te zoeken / adres te maken als deze er nog niet is (test_adres)
    // Aangezien we niet verwijderen.. bestaat er 1 test_adres in de DB at a certain point.
    private void getAdresId() throws GeneriekeFoutmelding{
        try {
            adres_id_te_testen = adresDAO.nieuwAdres(klant_id_te_testen, testAdres);
            System.out.println("ADRES_ID OM TE WORDEN GETEST(NEW) = " + adres_id_te_testen);
        } catch (GeneriekeFoutmelding ex) {
            if (!ex.getMessage().contains("BESTAAT AL"))
                throw new GeneriekeFoutmelding("FOUT BIJ MAKEN ADRES IN TEST");
            else {
                adres_id_te_testen = adresDAO.getAdresID(POSTCODE, HUISNUMMER, TOEVOEGING);
                System.out.println("ADRES_ID OM TE WORDEN GETEST (FIND) = " + adres_id_te_testen);
            }
        }
    }
}