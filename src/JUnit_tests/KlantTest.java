package JUnit_tests;

import model.Adres;
import model.Klant;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Milan_Verheij on 10-06-16.
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
    private final Adres ADRES = new Adres();
    private String nieuweWaarde = "";
    private Klant testKlant;

    @Before
    public void setUp() throws Exception {
        testKlant = new Klant(KLANT_ID, VOORNAAM, ACHTERNAAM, TUSSENVOEGSEL,
                                    EMAIL, ADRES);
    }

    @After
    public void tearDown() throws Exception {
        testKlant = null;
    }

    @Test
    public void Klant_getKlant_id() throws Exception {
        assertEquals(KLANT_ID, testKlant.getKlant_id());
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
        assertTrue(ADRES == testKlant.getAdresGegevens());
    }

    // TODO: Kan double maken ivm gebruik getters in set-testers, maar onhandig qua versies
    @Test
    public void setKlant_id() throws Exception {
        nieuweWaarde = "8888";
        testKlant.setKlant_id(Long.parseLong(nieuweWaarde));
        assertEquals(Long.parseLong(nieuweWaarde), testKlant.getKlant_id());
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

    //TODO: ToString method testen
}