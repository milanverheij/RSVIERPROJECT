package JUnit_tests;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import model.Artikel;
import mysql.ArtikelDAOMySQL;

public class ArtikelDAOMySQLTest {
		
	//De klasse die getest wordt
	private ArtikelDAOMySQL artikelDao = new ArtikelDAOMySQL();

	// Data
	int id1;
	int idGeretouneerd;
	int prijsID;
	String huidigeDatum = "2016-06-24";

	Artikel a1 = new Artikel();
	Artikel a2 = new Artikel();
	Artikel a3 = new Artikel();
	Artikel aGeretouneerd = new Artikel();

	@Before
	public void setUp() throws Exception {
		String huidigeDatum = "2016-06-24";
		
		a1.setArtikelNaam("Oerang Oetang");
		a1.setArtikelPrijs(new BigDecimal(1000));
		a1.setDatumAanmaak(huidigeDatum);
		a1.setVerwachteLevertijd(3);
		a1.setInAssortiment(true);

		a2.setArtikelNaam("Oerang Oetang");
		a2.setArtikelPrijs(new BigDecimal(1100));
		a2.setDatumAanmaak(huidigeDatum);
		a2.setVerwachteLevertijd(4);
		a2.setInAssortiment(true);

		a3.setArtikelNaam("Luiaard");
		a3.setArtikelPrijs(new BigDecimal(500));
		a3.setDatumAanmaak(huidigeDatum);
		a3.setVerwachteLevertijd(14);
		a3.setInAssortiment(false);

		id1 = artikelDao.nieuwArtikel(a1);
	}


	@After
	public void tearDown() throws Exception{
		
		artikelDao.verwijderArtikel(a1);
	}
	
	@Test
	public void nieuwArtikel() throws Exception {
		idGeretouneerd = artikelDao.nieuwArtikel(a1);
	}

	@Test
	public void getArtikel() throws Exception {
		aGeretouneerd = artikelDao.getArtikel(id1);

		testOfBeideArtikelenGelijkZijn(aGeretouneerd, a1);

	}

	@Test
	public void getAlleArtikelen() throws Exception {
		// Schrijf een nieuw artikel naar de database en sla zijn artikel id op in het testObject
		a1.setArtikelId(artikelDao.nieuwArtikel(a1));//in assortiment
		a3.setArtikelId(artikelDao.nieuwArtikel(a3));//niet in assortiment

		LinkedHashSet<Artikel> alleArtikelenLijst = artikelDao.getAlleArtikelen(0);
		LinkedHashSet<Artikel> actieveArtikelenLijst = artikelDao.getAlleArtikelen(1);

		assertThat(actieveArtikelenLijst.size() <= alleArtikelenLijst.size(), is(true));

		// Hier wordt gecontroleerd dat alleArtikelenLijst zowel artikelen bevat die in assortiment
		// als uit assortiment zijn.
		assertThat(alleArtikelenLijst.contains(a1), is (true));
		assertThat(alleArtikelenLijst.contains(a3), is (true));
		// Hier wordt gecontroleerd dat actieveArtikelenLijst alleen actieve artikelen bevat
		assertThat(actieveArtikelenLijst.contains(a1), is (true));
		assertThat(actieveArtikelenLijst.contains(a3), is (false));
	}

	@Test 
	public void updateArtikel() throws Exception {
		artikelDao.updateArtikel(id1, a2);
		aGeretouneerd = artikelDao.getArtikel(id1);

		testOfBeideArtikelenGelijkZijn(aGeretouneerd, a2);
	}

	@Test
	public void verwijderArtikel() throws Exception {
		
		a1 = new Artikel("Aasgier", new BigDecimal(500), huidigeDatum, 5, true);
		idGeretouneerd = artikelDao.nieuwArtikel(a1);
		artikelDao.verwijderArtikel(a1);
		aGeretouneerd = artikelDao.getArtikel(idGeretouneerd);

		// Het artikel is verwijdert wanneer het niet meer in het assortiment is.
		assertThat(aGeretouneerd.isInAssortiment(), is(equalTo(false)));
		
		// Test of de gegevens bewaart zijn gebleven in de database. idGeretouneerd wordt
		// gelijk gesteld aan id1 omdat de methode die de artikelen vergelijkt anders het
		// verkeerde id test!
		id1 = idGeretouneerd; 
		testOfBeideArtikelenGelijkZijn(aGeretouneerd, a1);
	}

	// Utility methodes
	public void testOfBeideArtikelenGelijkZijn(Artikel aGeretouneerd, Artikel a) {
		
		assertThat(aGeretouneerd.getArtikelId(), is(equalTo(id1)));
		assertThat(aGeretouneerd.getArtikelNaam(),is(equalTo(a.getArtikelNaam())));
		assertThat(aGeretouneerd.getArtikelPrijs().compareTo(a.getArtikelPrijs()), is(equalTo(0)));
		assertThat(aGeretouneerd.getPrijsId(), is(equalTo(a.getPrijsId())));
		assertThat(aGeretouneerd.getDatumAanmaak(), containsString(a.getDatumAanmaak()));
		assertThat(aGeretouneerd.getVerwachteLevertijd(), is(equalTo(a.getVerwachteLevertijd())));
		assertThat(aGeretouneerd.isInAssortiment(), is(equalTo(a.isInAssortiment())));
	}
}