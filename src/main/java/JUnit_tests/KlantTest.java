package JUnit_tests;

import model.Adres;
import model.Bestelling;
import model.Klant;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Milan_Verheij on 10-06-16.
 * Updated by Milan Verheij on 20-06-16 (nieuw DB-model).
 *
 * J-Unit testklasse om de Klant POJO te testen
 *
 */
public class KlantTest {
    private final long KLANT_ID = 9999;
    private final String VOORNAAM = "TESTVOORNAAM";
    private final String TUSSENVOEGSEL = "TESTTV";
    private final String ACHTERNAAM = "TESTACHTERNAAM";
    private final String EMAIL = "TESTMAIL@TESTDOMEIN.NL";
    private final String DATUMAANMAAK = "TEST-AANMAAK";
    private final String DATUMGEWIJZIGD = "TEST-GEWIJZIGD";
    private final String KLANTACTIEF = "TEST-ACTIEF";
    private final Adres ADRES = new Adres();
    private final Bestelling BESTELLING = new Bestelling();

    private String nieuweWaarde = "";
    private Klant testKlant;

    @Before
    public void setUp() throws Exception {
        testKlant = new Klant(KLANT_ID, VOORNAAM, ACHTERNAAM, TUSSENVOEGSEL,
                                    EMAIL, DATUMAANMAAK, DATUMGEWIJZIGD, KLANTACTIEF, ADRES, BESTELLING);
    }

    @After
    public void tearDown() throws Exception {
        testKlant = null;
    }

    @Test
    public void Klant_getKlant_id() throws Exception {
        assertEquals(KLANT_ID, testKlant.getKlantId());
    }

    @Test
    public void getVoornaam() throws Exception {
        assertEquals(VOORNAAM, testKlant.getVoornaam());
    }

    @Test
    public void getAchternaam() throws Exception {
        assertEquals(ACHTERNAAM, testKlant.getAchternaam());
    }

    @Test
    public void getTussenvoegsel() throws Exception {
        assertEquals(TUSSENVOEGSEL, testKlant.getTussenvoegsel());
    }

    @Test
    public void getEmail() throws Exception {
        assertEquals(EMAIL, testKlant.getEmail());
    }

    @Test
    public void getAdresGegevens() throws Exception {
        assertTrue(ADRES == testKlant.getAdresGegevens().get(0));
    }

    @Test
    public void getBestelGegevens() throws Exception {
        assertTrue(BESTELLING == testKlant.getBestellingGegevens());
    }

    @Test
    public void getDatumAanmaak() throws Exception {
        assertTrue(DATUMAANMAAK == testKlant.getDatumAanmaak());
    }

    @Test
    public void getDatumGewijzigd() throws Exception {
        assertTrue(DATUMGEWIJZIGD == testKlant.getDatumGewijzigd());
    }

    @Test
    public void getKlantActief() throws Exception {
        assertTrue(KLANTACTIEF == testKlant.getKlantActief());
    }

    @Test
    public void setKlant_id() throws Exception {
        nieuweWaarde = "8888";
        testKlant.setKlantId(Long.parseLong(nieuweWaarde));
        assertEquals(Long.parseLong(nieuweWaarde), testKlant.getKlantId());
    }

    @Test
    public void setVoornaam() throws Exception {
        nieuweWaarde = "Nieuwe Voornaam";
        testKlant.setVoornaam(nieuweWaarde);
        assertEquals(nieuweWaarde, testKlant.getVoornaam());
    }

    @Test
    public void setAchternaam() throws Exception {
        nieuweWaarde = "Nieuwe Achternaam";
        testKlant.setAchternaam(nieuweWaarde);
        assertEquals(nieuweWaarde, testKlant.getAchternaam());
    }

    @Test
    public void setTussenvoegsel() throws Exception {
        nieuweWaarde = "ABCD";
        testKlant.setTussenvoegsel(nieuweWaarde);
        assertEquals(nieuweWaarde, testKlant.getTussenvoegsel());
    }

    @Test
    public void setEmail() throws Exception {
        nieuweWaarde = "nieuwe@email.com";
        testKlant.setEmail(nieuweWaarde);
        assertEquals(nieuweWaarde, testKlant.getEmail());
    }

    @Test
    public void setAdresGegevens() throws Exception {
        Adres testAdres = new Adres();
        testKlant.setAdresGegevens(testAdres);
        assertTrue(testAdres == testKlant.getAdresGegevens().get(0));
    }

    @Test
    public void setBestelGegevens() throws Exception {
        Bestelling testBestelling = new Bestelling();
        testKlant.setBestellingGegevens(testBestelling);
        assertTrue(testBestelling == testKlant.getBestellingGegevens());
    }

    @Test
    public void setDatumAanmaak() throws Exception {
        nieuweWaarde = "NIEUWE DATUM AANMAAK";
        testKlant.setDatumAanmaak(nieuweWaarde);
        assertTrue(nieuweWaarde == testKlant.getDatumAanmaak());
    }

    @Test
    public void setDatumGewijzigd() throws Exception {
        nieuweWaarde = "NIEUWE DATUM GEWIJZIGD";
        testKlant.setDatumGewijzigd(nieuweWaarde);
        assertTrue(nieuweWaarde == testKlant.getDatumGewijzigd());
    }

    @Test
    public void setKlantActief() throws Exception {
        nieuweWaarde = "NIEUWE KLANT ACTIEF";
        testKlant.setKlantActief(nieuweWaarde);
        assertTrue(nieuweWaarde == testKlant.getKlantActief());
    }


    @Test
    public void equalsTrue() throws Exception {
        testKlant = new Klant(KLANT_ID, VOORNAAM, ACHTERNAAM, TUSSENVOEGSEL,
                EMAIL, ADRES);
        Klant testKlant2 = new Klant(KLANT_ID, VOORNAAM, ACHTERNAAM, TUSSENVOEGSEL,
                EMAIL, ADRES);
        assertTrue(testKlant.equals(testKlant2));
    }

    @Test
    public void equalsFalse() throws Exception {
        testKlant = new Klant(KLANT_ID, VOORNAAM, ACHTERNAAM, TUSSENVOEGSEL,
                EMAIL, ADRES);
        Klant testKlant2 = new Klant(8888, VOORNAAM, ACHTERNAAM, TUSSENVOEGSEL,
                EMAIL, ADRES);
        assertFalse(testKlant.equals(testKlant2));
    }
}