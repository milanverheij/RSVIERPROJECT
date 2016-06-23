package JUnit_tests;

import connection_pools.HikariCPAdapter;
import exceptions.GeneriekeFoutmelding;
import interfaces.AdresDAO;
import interfaces.KlantDAO;
import model.Adres;
import mysql.AbstractDAOMySQL;
import mysql.AdresDAOMySQL;
import mysql.KlantDAOMySQL;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Milan_Verheij on 09-06-16.
 * Updated by Milan Verheij on 20-06-16 (nieuw DB-model).

 *
 * TestClass om AdresDAOMySQL te Testen met JUnit
 */
public class AdresDAOMySQLTest {
    // Klant id is nodig om een adres te kunnen toevoegen
    long klant_id_te_testen = 0;
    long adres_id_te_testen = 0;
    KlantDAO klantDAO = new KlantDAOMySQL();
    AdresDAO adresDAO = new AdresDAOMySQL();

    String juisteTestStraatnaam = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; // Max 26 karakters
    String juisteTestPostcode = "9999AB"; // Max 6 karakters
    String juisteTestToevoeging = "ABCDEF"; // max 6 karakters
    int juisteTestHuisnummer = 9999; // meer is onmogelijk in Nederland
    String juisteTestWoonplaats = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; // Max 26 karakters
    Adres testAdres;

    @Before
    public void setUp() throws Exception {
        // Er dient een connectionPool gebruikt te worden om een verbinding te krijgen aangezien
        // alle DAO's gebruik maken van een gedeelde link naar een connection pool in de abstract DAO.
        AbstractDAOMySQL.setConnPool(new HikariCPAdapter("MySQL"));

        // Ik moet eerste een klant aanmaken voordat ik een adres kan toevoegen en deze kan testen.
        // Derhalve eerste een nieuwe klant toevoegen en het klant_id achterhalen van deze klant
        // door hierop te zoeken.
        try {
            klantDAO.nieuweKlant("GEBRUIKT", "IN TEST");

        } catch (GeneriekeFoutmelding ex) {
            if (!ex.getMessage().contains("BESTAAT AL")) {
                throw new GeneriekeFoutmelding("FOUT TIJDENS SETUP");
            }
            klant_id_te_testen = klantDAO.getKlantOpKlant("GEBRUIKT", "IN TEST").next().getKlant_id();
        }

        try {
            adresDAO.nieuwAdres(klant_id_te_testen, new Adres(juisteTestStraatnaam, juisteTestPostcode,
                    juisteTestToevoeging, juisteTestHuisnummer, juisteTestWoonplaats));
            adres_id_te_testen = adresDAO.getAdresID(juisteTestPostcode, juisteTestHuisnummer,
                    juisteTestToevoeging);
        } catch (GeneriekeFoutmelding ex) {
            if (!ex.getMessage().contains("BESTAAT AL")) {
                throw new GeneriekeFoutmelding("FOUT TIJDENS SETUP" + ex.getMessage());
            }
            adres_id_te_testen = adresDAO.getAdresID(juisteTestPostcode, juisteTestHuisnummer,
                    juisteTestToevoeging);
        }
    }

    @After
    public void tearDown() throws Exception {
        klantDAO.updateKlant(klant_id_te_testen, "GEBRUIKT", "IN TEST", "", "TEARDOWN");
        klantDAO.schakelStatusKlant(klant_id_te_testen, 0);
        adresDAO.schakelStatusAdres(adres_id_te_testen, 0);

        // Terug naar initialisatie van test-schakelaars
        AdresDAOMySQL.aangeroepenAdresInTest = new Adres("XXXXXX", "XXXX", "XX", 0000, "XXXX");
        AdresDAOMySQL.klantWordtGetest = false;
    }

    @Test
    public void TestUpdateAdresDAOnullAdres_geenException() throws Exception {
        adresDAO.updateAdres(adres_id_te_testen, null);

        // Daarna naar goede adres omdat de teardown anders een fout maakt en de volgende keer
        // een nieuw null adres wordt gemaakt in de DB.
        adresDAO.updateAdres(adres_id_te_testen, new Adres(juisteTestStraatnaam, juisteTestPostcode,
                juisteTestToevoeging, juisteTestHuisnummer, juisteTestWoonplaats));
    }
//
//    @Test
//    public void TestUpdateAdresDAOBijNullAlleVeldenLeeg() throws Exception {
//        AdresDAOMySQL.klantWordtGetest = true;
//        adresDAO.updateAdres(adres_id_te_testen, null);
//
//        assertEquals("", AdresDAOMySQL.aangeroepenAdresInTest.getStraatnaam());
//        assertEquals("", AdresDAOMySQL.aangeroepenAdresInTest.getPostcode());
//        assertEquals("", AdresDAOMySQL.aangeroepenAdresInTest.getToevoeging());
//        assertEquals("", AdresDAOMySQL.aangeroepenAdresInTest.getWoonplaats());
//        assertEquals(0, AdresDAOMySQL.aangeroepenAdresInTest.getHuisnummer());
//
//        AdresDAOMySQL.klantWordtGetest = false;
//        AdresDAOMySQL.aangeroepenAdresInTest = new Adres("XXXXXX", "XXXX", "XX", 0000, "XXXX");
//    }
//
//    @Test
//    public void TestUpdateAdresDAOStraatNaamKlopt() throws Exception {
//        AdresDAOMySQL.klantWordtGetest = true;
//
//        adresDAO.updateAdres(adres_id_te_testen, new Adres(juisteTestStraatnaam, "", "", 0 , ""));
//
//        assertEquals(juisteTestStraatnaam, AdresDAOMySQL.aangeroepenAdresInTest.getStraatnaam());
//
//        AdresDAOMySQL.klantWordtGetest = false;
//        AdresDAOMySQL.aangeroepenAdresInTest = new Adres("XXXXXX", "XXXX", "XX", 0000, "XXXX");
//    }
//
//    @Test
//    public void TestUpdateAdresDAOPostcodeKlopt() throws Exception {
//        AdresDAOMySQL.klantWordtGetest = true;
//
//        adresDAO.updateAdres(adres_id_te_testen, new Adres("", juisteTestPostcode, "", 0 , ""));
//
//        assertEquals(juisteTestPostcode, AdresDAOMySQL.aangeroepenAdresInTest.getPostcode());
//
//        AdresDAOMySQL.klantWordtGetest = false;
//        AdresDAOMySQL.aangeroepenAdresInTest = new Adres("XXXXXX", "XXXX", "XX", 0000, "XXXX");
//    }
//
//    @Test
//    public void TestUpdateAdresDAOToevoegingKlopt() throws Exception {
//        AdresDAOMySQL.klantWordtGetest = true;
//
//        adresDAO.updateAdres(adres_id_te_testen, new Adres("", "", juisteTestToevoeging, 0 , ""));
//
//        assertEquals(juisteTestToevoeging, AdresDAOMySQL.aangeroepenAdresInTest.getToevoeging());
//
//        AdresDAOMySQL.klantWordtGetest = false;
//        AdresDAOMySQL.aangeroepenAdresInTest = new Adres("XXXXXX", "XXXX", "XX", 0000, "XXXX");
//    }
//
//    @Test
//    public void TestUpdateAdresDAOHuisnummerKlopt() throws Exception {
//        AdresDAOMySQL.klantWordtGetest = true;
//
//        adresDAO.updateAdres(adres_id_te_testen, new Adres("", "", "", juisteTestHuisnummer , ""));
//
//        assertEquals(juisteTestHuisnummer, AdresDAOMySQL.aangeroepenAdresInTest.getHuisnummer());
//
//        AdresDAOMySQL.klantWordtGetest = false;
//        AdresDAOMySQL.aangeroepenAdresInTest = new Adres("XXXXXX", "XXXX", "XX", 0000, "XXXX");
//    }
//
//    @Test
//    public void TestUpdateAdresDAOWoonplaatsKlopt() throws Exception {
//        AdresDAOMySQL.klantWordtGetest = true;
//
//        adresDAO.updateAdres(adres_id_te_testen, new Adres("", "", "", 0 , juisteTestWoonplaats));
//
//        assertEquals(juisteTestWoonplaats, AdresDAOMySQL.aangeroepenAdresInTest.getWoonplaats());
//
//        AdresDAOMySQL.klantWordtGetest = false;
//        AdresDAOMySQL.aangeroepenAdresInTest = new Adres("XXXXXX", "XXXX", "XX", 0000, "XXXX");
//    }
//
//
//    @Rule
//    public final ExpectedException exception = ExpectedException.none();
//
//    @Test
//    public void TestUpdateAdresDAOEenFoutieveInvoergRSVIERException() throws Exception {
//        // Een te lange postcode kan niet en ik verwacht dan ook dat er een GeneriekeFoutmelding wordt gegooid
//        // en wel een RSVierException
//        // Dit geld voor elke foutieve invoer, het gedrag zal hetzelfde zijn.
//        exception.expect(GeneriekeFoutmelding.class);
//        adresDAO.updateAdres(adres_id_te_testen, new Adres("", "TELANGEPOSTCODE", "", 0, ""));
//    }
//
//    @Test
//    public void TestUpdateAdresDAOConsistentGedragFoutieveInvoer() throws Exception {
//        // Test of dat ook bij een andere foutieve invoer eenzelfde fout wordt gegooid, bij twee zal
//        // de rest hetzelfde functioneren is de aanname.
//        exception.expect(GeneriekeFoutmelding.class);
//        adresDAO.updateAdres(adres_id_te_testen, new Adres("", "", "ABCDEFG", 0, ""));
//    }


}