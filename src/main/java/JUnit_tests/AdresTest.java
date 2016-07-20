package JUnit_tests;

import model.Adres;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Milan_Verheij on 10-06-16.
 * Updated by Milan Verheij on 20-06-16 (nieuw DB-model).
 *
 * J-Unit testklasse om de Adres_POJO te testen
 *
 */
public class AdresTest {
    // Er wordt een standaard adres aangemaakt om de getters te testen
    private final String STRAATNAAM = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; // Max 26 karakters
    private final String POSTCODE = "9999AB"; // Max 6 karakters
    private final String TOEVOEGING = "ABCDEF"; // max 6 karakters
    private final int HUISNUMMER = 9999; // meer is onmogelijk in Nederland
    private final String WOONPLAATS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; // Max 26 karakters
    private final String DATUMAANMAAK = "TEST DATUM AANMAAK";
    private final String DATUMGEWIJZIGD = "TEST DATUM GEWIJZIGD";
    private final String ADRESACTIEF = "TEST ADRES ACTIEF";
    private Adres testAdres;
    private String nieuweWaarde; // Wordt in de set tests gebruikt

    @Before
    public void setUp() throws Exception {
        testAdres = new Adres(STRAATNAAM, POSTCODE, TOEVOEGING,
                HUISNUMMER, WOONPLAATS, DATUMAANMAAK, DATUMGEWIJZIGD,
                ADRESACTIEF);
    }

    @After
    public void tearDown() throws Exception {
        testAdres = null;
    }

    @Test
    public void getStraatnaam() throws Exception {
        assertEquals(STRAATNAAM, testAdres.getStraatnaam());
    }

    @Test
    public void getPostcode() throws Exception {
        assertEquals(POSTCODE, testAdres.getPostcode());
    }

    @Test
    public void getToevoeging() throws Exception {
        assertEquals(TOEVOEGING, testAdres.getToevoeging());
    }

    @Test
    public void getHuisnummer() throws Exception {
        assertEquals(HUISNUMMER, testAdres.getHuisnummer());
    }

    @Test
    public void getWoonplaats() throws Exception {
        assertEquals(WOONPLAATS, testAdres.getWoonplaats());
    }

    @Test
    public void getDatumAanmaak() throws Exception {
        assertTrue(DATUMAANMAAK == testAdres.getDatumAanmaak());
    }

    @Test
    public void getDatumGewijzigd() throws Exception {
        assertTrue(DATUMGEWIJZIGD == testAdres.getDatumGewijzigd());
    }

    @Test
    public void getKlantActief() throws Exception {
        assertTrue(ADRESACTIEF == testAdres.getAdresActief());
    }

    @Test
    public void setStraatnaam() throws Exception {
        nieuweWaarde = "Nieuwe Straatnaam";
        testAdres.setStraatnaam(nieuweWaarde);
        assertEquals(nieuweWaarde, testAdres.getStraatnaam());
    }

    @Test
    public void setPostcode() throws Exception {
        nieuweWaarde = "1024MB";
        testAdres.setPostcode(nieuweWaarde);
        assertEquals(nieuweWaarde, testAdres.getPostcode());
    }

    @Test
    public void setToevoeging() throws Exception {
        nieuweWaarde = "AB";
        testAdres.setToevoeging(nieuweWaarde);
        assertEquals(nieuweWaarde, testAdres.getToevoeging());
    }

    @Test
    public void setHuisnummer() throws Exception {
        nieuweWaarde = "1234";
        testAdres.setHuisnummer(Integer.parseInt(nieuweWaarde));
        assertEquals(Integer.parseInt(nieuweWaarde), testAdres.getHuisnummer());
    }

    @Test
    public void setWoonplaats() throws Exception {
        nieuweWaarde = "Nieuwe Woonplaats";
        testAdres.setWoonplaats(nieuweWaarde);
        assertEquals(nieuweWaarde, testAdres.getWoonplaats());
    }

    @Test
    public void setDatumAanmaak() throws Exception {
        nieuweWaarde = "NIEUWE DATUM AANMAAK";
        testAdres.setDatumAanmaak(nieuweWaarde);
        assertTrue(nieuweWaarde == testAdres.getDatumAanmaak());
    }

    @Test
    public void setDatumGewijzigd() throws Exception {
        nieuweWaarde = "NIEUWE DATUM GEWIJZIGD";
        testAdres.setDatumGewijzigd(nieuweWaarde);
        assertTrue(nieuweWaarde == testAdres.getDatumGewijzigd());
    }

    @Test
    public void setKlantActief() throws Exception {
        nieuweWaarde = "NIEUWE KLANT ACTIEF";
        testAdres.setAdresActief(nieuweWaarde);
        assertTrue(nieuweWaarde == testAdres.getAdresActief());
    }

    @Test
    public void equalsTrue() throws Exception {
        testAdres = new Adres(STRAATNAAM, POSTCODE, TOEVOEGING, HUISNUMMER, WOONPLAATS);
        Adres testAdres2 = new Adres(STRAATNAAM, POSTCODE, TOEVOEGING, HUISNUMMER, WOONPLAATS);
        assertTrue(testAdres.equals(testAdres2));
    }

    @Test
    public void equalsFalse() throws Exception {
        testAdres = new Adres(STRAATNAAM, POSTCODE, TOEVOEGING, HUISNUMMER, WOONPLAATS);
        Adres testAdres2 = new Adres(STRAATNAAM, POSTCODE, TOEVOEGING, 4321, WOONPLAATS);
        assertFalse(testAdres.equals(testAdres2));
    }
}