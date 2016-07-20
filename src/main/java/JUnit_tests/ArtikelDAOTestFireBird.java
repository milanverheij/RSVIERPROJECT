package JUnit_tests;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.math.BigDecimal;
import java.util.LinkedHashSet;

import database.factories.DAOFactoryMySQL;
import model.Artikel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import database.daos.firebird.ArtikelDAOFireBird;

public class ArtikelDAOTestFireBird {

	//De klasse die getest wordt
	ArtikelDAOFireBird artikelDao;

	// Data
	int id1;
	int id2;
	int id3;

	int idGeretouneerd;
	int prijsID;
	//	String huidigeDatum = "2016-06-24";

	Artikel a1 = new Artikel();
	Artikel a2 = new Artikel();
	Artikel a3 = new Artikel();
	Artikel aGeretouneerd = new Artikel();

	@Before
	public void setUp() throws Exception {
		if(artikelDao == null)
			artikelDao = (ArtikelDAOFireBird) DAOFactoryMySQL.getDAOFactory("FireBird", "HikariCP").getArtikelDAO();

		//		String huidigeDatum = "2016-06-24";

		a1.setArtikelNaam("Oerang Oetang");
		a1.setArtikelPrijs(new BigDecimal(1000.00));
		a1.setVerwachteLevertijd(3);
		a1.setInAssortiment(true);

		a2.setArtikelNaam("Aasgier");
		a2.setArtikelPrijs(new BigDecimal(1100.00));
		a2.setVerwachteLevertijd(4);
		a2.setInAssortiment(true);

		a3.setArtikelNaam("Luiaard");
		a3.setArtikelPrijs(new BigDecimal(500.00));
		a3.setVerwachteLevertijd(14);
		a3.setInAssortiment(false);

		id1 = artikelDao.nieuwArtikel(a1);
		id2 = artikelDao.nieuwArtikel(a2);
		id3 = artikelDao.nieuwArtikel(a3);
	}

	@After
	public void tearDown() throws Exception{
		artikelDao.verwijderVoorHetEchie(id1);
		artikelDao.verwijderVoorHetEchie(id2);
		artikelDao.verwijderVoorHetEchie(id3);
	}

	@Test
	public void getArtikel() throws Exception {
		aGeretouneerd = artikelDao.getArtikel(id1);

		assertTrue(aGeretouneerd.getArtikelId() == id1);
		assertEquals(aGeretouneerd.getArtikelNaam(), a1.getArtikelNaam());
		assertTrue(aGeretouneerd.getArtikelPrijs().compareTo(a1.getArtikelPrijs()) == 0);
		assertTrue(aGeretouneerd.getPrijsId() == a1.getPrijsId());
		assertTrue(aGeretouneerd.getVerwachteLevertijd() == a1.getVerwachteLevertijd());
		assertTrue(aGeretouneerd.isInAssortiment() == a1.isInAssortiment());
	}

	@Test
	public void getAlleArtikelen() throws Exception {
		LinkedHashSet<Artikel> alleArtikelenLijst = artikelDao.getAlleArtikelen(true);
		LinkedHashSet<Artikel> actieveArtikelenLijst = artikelDao.getAlleArtikelen(false);

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
		artikelDao.verwijderArtikel(a1.getArtikelId());
		aGeretouneerd = artikelDao.getArtikel(a1.getArtikelId());

		// Het artikel is verwijdert wanneer het niet meer in het assortiment is.
		assertTrue(aGeretouneerd.isInAssortiment() == false);

		// Test of de gegevens bewaart zijn gebleven in de database. idGeretouneerd wordt
		// gelijk gesteld aan id1 omdat de methode die de artikelen vergelijkt anders het
		// verkeerde id test!

		testOfBeideArtikelenGelijkZijn(aGeretouneerd, a1);
	}

	// Utility methodes
	public void testOfBeideArtikelenGelijkZijn(Artikel aGeretouneerd, Artikel a) {
		assertThat(aGeretouneerd.getArtikelId(), is(equalTo(id1)));
		assertThat(aGeretouneerd.getArtikelNaam(),is(equalTo(a.getArtikelNaam())));
		assertThat(aGeretouneerd.getArtikelPrijs().compareTo(a.getArtikelPrijs()), is(equalTo(0)));
		assertThat(aGeretouneerd.getPrijsId(), is(equalTo(a.getPrijsId())));
		assertThat(aGeretouneerd.getVerwachteLevertijd(), is(equalTo(a.getVerwachteLevertijd())));
	}
}