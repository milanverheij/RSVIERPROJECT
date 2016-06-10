package JUnit_tests;

import exceptions.RSVIERException;
import interfaces.AdresDAO;
import interfaces.KlantDAO;
import model.Adres;
import mysql.AdresDAOMySQL;
import mysql.KlantDAOMySQL;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

/**
 * Created by Milan_Verheij on 09-06-16.
 *
 * TestClass om AdresDAOMySQL te Testen met JUnit
 */
public class AdresDAOMySQLTest {
    // Klant id is nodig om een adres te kunnen toevoegen
    long klant_id_te_testen= 0;
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
        // Ik moet eerste een klant aanmaken voordat ik een adres kan toevoegen en deze kan testen.
        // Derhalve eerste een nieuwe klant toevoegen en het klant_id achterhalen van deze klant
        // door hierop te zoeken.
        // TODO: Beter om direct bij die method ook maar de klant_id te returnen aangezien dat beste praktisch is.
        klantDAO.nieuweKlant("JUnit1337", "Men1337eer");
        klant_id_te_testen = klantDAO.getKlantOpKlant("JUnit1337", "Men1337eer").next().getKlant_id();
        klantDAO.getKlantOpKlant(klant_id_te_testen).next().getAdresGegevens();
    }

    @After
    public void tearDown() throws Exception {
        klantDAO.verwijderKlant(klant_id_te_testen);
    }

    @Test
    public void TestUpdateAdresDAOnullAdres_geenException() throws Exception {
        adresDAO.updateAdres(klant_id_te_testen, null);
    }

    @Test
    public void TestUpdateAdresDAOBijNullAlleVeldenLeeg() throws Exception {
        adresDAO.updateAdres(klant_id_te_testen, null);
        testAdres = klantDAO.getKlantOpKlant(klant_id_te_testen).next().getAdresGegevens();
        assertEquals("", testAdres.getStraatnaam());
        assertEquals("", testAdres.getPostcode());
        assertEquals(null, testAdres.getToevoeging());
        assertEquals("", testAdres.getWoonplaats());
        assertEquals(0, testAdres.getHuisnummer());
    }

    @Test
    public void TestUpdateAdresDAOStraatNaamKlopt() throws Exception {
        adresDAO.updateAdres(klant_id_te_testen,
                new Adres(juisteTestStraatnaam, "", "", 0 , ""));
        testAdres = klantDAO.getKlantOpKlant(klant_id_te_testen).next().getAdresGegevens();

    }

    @Test
    public void TestUpdateAdresDAOPostcodeKlopt() throws Exception {
        adresDAO.updateAdres(klant_id_te_testen,
                new Adres("", juisteTestPostcode, "", 0 , ""));
        testAdres = klantDAO.getKlantOpKlant(klant_id_te_testen).next().getAdresGegevens();

        assertEquals(juisteTestPostcode, testAdres.getPostcode());
    }

    @Test
    public void TestUpdateAdresDAOToevoegingKlopt() throws Exception {
        adresDAO.updateAdres(klant_id_te_testen,
                new Adres("", "", juisteTestToevoeging, 0 , ""));
        testAdres = klantDAO.getKlantOpKlant(klant_id_te_testen).next().getAdresGegevens();

        assertEquals(juisteTestToevoeging, testAdres.getToevoeging());
    }

    @Test
    public void TestUpdateAdresDAOHuisnummerKlopt() throws Exception {
        adresDAO.updateAdres(klant_id_te_testen,
                new Adres("", "", "", juisteTestHuisnummer , ""));
        testAdres = klantDAO.getKlantOpKlant(klant_id_te_testen).next().getAdresGegevens();

        assertEquals(juisteTestHuisnummer, testAdres.getHuisnummer());
    }

    @Test
    public void TestUpdateAdresDAOWoonplaatsKlopt() throws Exception {
        adresDAO.updateAdres(klant_id_te_testen,
                new Adres("", "", "", 0 , juisteTestWoonplaats));
        testAdres = klantDAO.getKlantOpKlant(klant_id_te_testen).next().getAdresGegevens();

        assertEquals(juisteTestWoonplaats, testAdres.getWoonplaats());
    }


    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void TestUpdateAdresDAOEenFoutieveInvoergRSVIERException() throws Exception {
        // Een te lange postcode kan niet en ik verwacht dan ook dat er een RSVIERException wordt gegooid
        // en wel een RSVierException
        // Dit geld voor elke foutieve invoer, het gedrag zal hetzelfde zijn.
        exception.expect(RSVIERException.class);
        adresDAO.updateAdres(klant_id_te_testen,
                new Adres("", "TELANGEPOSTCODE", "", 0, ""));
    }

    @Test
    public void TestUpdateAdresDAOConsistentGedragFoutieveInvoer() throws Exception {
        // Test of dat ook bij een andere foutieve invoer eenzelfde fout wordt gegooid.
        exception.expect(RSVIERException.class);
        adresDAO.updateAdres(klant_id_te_testen,
                new Adres("", "", "ABCDEFG", 0, ""));
    }


}