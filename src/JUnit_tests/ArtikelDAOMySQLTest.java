package JUnit_tests;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import model.Artikel;
import model.Bestelling;
import mysql.ArtikelDAOMySQL;
import mysql.BestellingDAOMySQL;

public class ArtikelDAOMySQLTest {

	//De klasse die getest wordt
	ArtikelDAOMySQL artikelDao = new ArtikelDAOMySQL();

	//BestellingDAOMySQL is nodig om Artikel te kunnen testen
	BestellingDAOMySQL bestellingDao = new BestellingDAOMySQL();
	Bestelling bestelling = new Bestelling();
	Bestelling bestelling1 = new Bestelling();
	Bestelling bestelling2 = new Bestelling();
	Bestelling bestelling3 = new Bestelling();

	//Data 
	long klant_id = 1;
	long bestelling_id;
	long id1;
	long id2;
	long id3;
	Artikel artikel;
	Artikel a1 = new Artikel();
	Artikel a2 = new Artikel();
	Artikel a3 = new Artikel();
	Artikel testArtikel = new Artikel(4, "Test", 1000);

	LinkedHashMap<Artikel, Integer> artikelLijstMap = new LinkedHashMap<>();
	Iterator<Entry<Artikel, Integer>> mIterator;
	Entry<Artikel, Integer> entry;

	ArrayList<Artikel> artikelLijst = new ArrayList<>();
	Iterator<Artikel> aIterator;

	// Maak een bestelling aan om te testen
	@Before
	public void setUp() throws Exception {
		bestelling.setKlant_id(klant_id);
		artikelLijstMap.put(a1 = new Artikel(1, "Aap", 3500), new Integer(1));
		artikelLijstMap.put(a2 = new Artikel(2, "Leeuw", 5000), new Integer(1));
		artikelLijstMap.put(a3 = new Artikel(3, "Walvis", 30000), new Integer(1));
		bestelling.setArtikelLijst(artikelLijstMap);
		bestelling_id = bestellingDao.nieuweBestelling(bestelling);
	}

	// Verwijder de bestelling na de test
	@After
	public void tearDown() throws Exception{
		bestellingDao.verwijderEnkeleBestelling(bestelling_id);
		artikelLijst = null;
	}

	@Test
	public void testVoegNieuwArtikelToeAanBestelling() throws Exception{
		artikelDao.nieuwArtikelOpBestelling(bestelling_id, testArtikel);
		Artikel a = artikelDao.getArtikelOpBestelling(bestelling_id, 1);

		assertEquals(testArtikel.getArtikel_id(), a.getArtikel_id());
		assertEquals(testArtikel.getArtikel_naam(), a.getArtikel_naam());
		assertTrue(testArtikel.getArtikel_prijs() == a.getArtikel_prijs());
	}


	@Test 
	public void testGetArtikelOpBestelling() throws Exception{
		Artikel a = artikelDao.getArtikelOpBestelling(bestelling_id, 1 /*artikelNummer*/);

		assertEquals(a1.getArtikel_id(), a.getArtikel_id());
		assertEquals(a1.getArtikel_naam(), a.getArtikel_naam());
		assertTrue(a1.getArtikel_prijs() == a.getArtikel_prijs());
	}


	@Test
	public void testGetAlleArtikelenOpBestelling() throws Exception{

		aIterator = artikelDao.getAlleArtikelenOpBestelling(bestelling_id);

		while(aIterator.hasNext()) {
			Artikel a = aIterator.next();
			artikelLijst.add(a);
		}

		assertEquals(a1.getArtikel_id(), artikelLijst.get(0).getArtikel_id());	
		assertEquals(a1.getArtikel_naam(), artikelLijst.get(0).getArtikel_naam());
		assertTrue(a1.getArtikel_prijs() == artikelLijst.get(0).getArtikel_prijs());

		assertEquals(a2.getArtikel_id(), artikelLijst.get(1).getArtikel_id());	
		assertEquals(a2.getArtikel_naam(), artikelLijst.get(1).getArtikel_naam());
		assertTrue(a2.getArtikel_prijs() == artikelLijst.get(1).getArtikel_prijs());

		assertEquals(a3.getArtikel_id(), artikelLijst.get(2).getArtikel_id());	
		assertEquals(a3.getArtikel_naam(), artikelLijst.get(2).getArtikel_naam());
		assertTrue(a3.getArtikel_prijs() == artikelLijst.get(2).getArtikel_prijs());
	}

	@Test 
	public void testGetAlleArtikelen() throws Exception{
		mIterator = artikelDao.getAlleArtikelen();
		int artikelCount = 0;
		ArrayList<Integer> artikelAantal = new ArrayList<>();

		while(mIterator.hasNext()) {
			entry = mIterator.next();
			artikelLijst.add(entry.getKey());
			artikelAantal.add(entry.getValue());
		}

		int indexA1 = artikelLijst.indexOf(a1);
		int indexA2 = artikelLijst.indexOf(a2);
		int indexA3 = artikelLijst.indexOf(a3);

		if (artikelLijst.contains(a1)) {
			assertEquals(a1.getArtikel_id(), artikelLijst.get(indexA1).getArtikel_id());	
			assertEquals(a1.getArtikel_naam(), artikelLijst.get(indexA1).getArtikel_naam());
			assertTrue(a1.getArtikel_prijs() == artikelLijst.get(indexA1).getArtikel_prijs());
			assertTrue(1 == artikelAantal.get(indexA1));
			artikelCount++;
		}
		if (artikelLijst.contains(a2)) {
			assertEquals(a2.getArtikel_id(), artikelLijst.get(indexA2).getArtikel_id());	
			assertEquals(a2.getArtikel_naam(), artikelLijst.get(indexA2).getArtikel_naam());
			assertTrue(a2.getArtikel_prijs() == artikelLijst.get(indexA2).getArtikel_prijs());
			assertTrue(1 == artikelAantal.get(indexA1));
			artikelCount++;
		}
		if (artikelLijst.contains(a3)) {
			assertEquals(a3.getArtikel_id(), artikelLijst.get(indexA3).getArtikel_id());	
			assertEquals(a3.getArtikel_naam(), artikelLijst.get(indexA3).getArtikel_naam());
			assertTrue(a3.getArtikel_prijs() == artikelLijst.get(indexA3).getArtikel_prijs());
			assertTrue(1 == artikelAantal.get(indexA1));
			artikelCount++;
		}

		assertTrue(artikelCount == 3);
	}

	@Test 
	public void testUpdateArtikelOpBestelling() throws Exception{
		// Test de eerste overloaded methode
		int artikelNummer = 3;
		artikelDao.updateArtikelOpBestelling(bestelling_id, artikelNummer, testArtikel);
		Artikel a = artikelDao.getArtikelOpBestelling(bestelling_id, artikelNummer);
		assertUpdateArtikelOpBestelling(testArtikel, a);

		// Test de tweede overloaded methode
		artikelNummer = 1;
		artikelDao.updateArtikelOpBestelling(bestelling_id, a1, testArtikel);
		a = artikelDao.getArtikelOpBestelling(bestelling_id, artikelNummer);
		assertUpdateArtikelOpBestelling(testArtikel, a);


	}

	@Test
	public void testUpdateAlleArtikelenOpBestelling() throws Exception{
		artikelDao.updateAlleArtikelenOpBestelling(bestelling_id, testArtikel, testArtikel, testArtikel);
		aIterator = artikelDao.getAlleArtikelenOpBestelling(bestelling_id);

		while(aIterator.hasNext()) {
			Artikel a = aIterator.next();
			artikelLijst.add(a);
		}
		for (int i = 0; i < 3; i++) {
			assertEquals(testArtikel.getArtikel_id(), artikelLijst.get(i).getArtikel_id());	
			assertEquals(testArtikel.getArtikel_naam(), artikelLijst.get(i).getArtikel_naam());
			assertTrue(testArtikel.getArtikel_prijs() == artikelLijst.get(i).getArtikel_prijs());
		}
	}
	// Albert

	@Test
	public void testHetAanpassenVanAlleArtikelenInDeDataBase() throws Exception{
		// @Before van alberts TestToevoeging in een methode gezet
		setUpTestHetAanpassenVanAlleArtikelenInDeDataBase();

		//Update artikel 1
		a1.setArtikel_naam("nieuwe naam 1");
		a1.setArtikel_prijs(11.11);
		artikelDao.updateArtikelen(a1);

		//Update artikel 2
		a2.setArtikel_naam("nieuwe naam 2");
		a2.setArtikel_prijs(22.22);
		artikelDao.updateArtikelen(a2);

		//Update artikel 3
		a3.setArtikel_naam("nieuwe naam 3");
		a3.setArtikel_prijs(33.33);
		artikelDao.updateArtikelen(a3);

		//Bestellingen weer uit de database halen
		Bestelling teTesten1 = bestellingDao.getBestellingOpBestellingId(id1).next();
		Bestelling teTesten2 = bestellingDao.getBestellingOpBestellingId(id2).next();
		Bestelling teTesten3 = bestellingDao.getBestellingOpBestellingId(id3).next();

		//Eerste bestelling testen
		Iterator<Artikel> artikelIterator = getIteratorMetDubbelen(teTesten1);
		Artikel artikel = artikelIterator.next();
		assertTrue(artikel.getArtikel_id() == 1);
		assertEquals(artikel.getArtikel_naam(), "nieuwe naam 1");
		assertTrue(artikel.getArtikel_prijs() == 11.11);

		artikel = artikelIterator.next();
		assertTrue(artikel.getArtikel_id() == 2);
		assertTrue(artikel.getArtikel_prijs() == 22.22);
		assertEquals(artikel.getArtikel_naam(), "nieuwe naam 2");


		artikel = artikelIterator.next();
		assertTrue(artikel.getArtikel_id() == 3);
		assertEquals(artikel.getArtikel_naam(), "nieuwe naam 3");
		assertTrue(artikel.getArtikel_prijs() == 33.33);


		//Tweede bestelling testen
		artikelIterator = getIteratorMetDubbelen(teTesten2);
		artikel = artikelIterator.next();
		assertTrue(artikel.getArtikel_id() == 1);
		assertEquals(artikel.getArtikel_naam(), "nieuwe naam 1");
		assertTrue(artikel.getArtikel_prijs() == 11.11);

		artikel = artikelIterator.next();
		assertTrue(artikel.getArtikel_id() == 2);
		assertEquals(artikel.getArtikel_naam(), "nieuwe naam 2");
		assertTrue(artikel.getArtikel_prijs() == 22.22);

		artikel = artikelIterator.next();
		assertTrue(artikel.getArtikel_id() == 2);
		assertEquals(artikel.getArtikel_naam(), "nieuwe naam 2");
		assertTrue(artikel.getArtikel_prijs() == 22.22);

		//Derde bestelling testen

		artikelIterator = getIteratorMetDubbelen(teTesten3);
		artikel = artikelIterator.next();
		assertTrue(artikel.getArtikel_id() == 3);
		assertEquals(artikel.getArtikel_naam(), "nieuwe naam 3");
		assertTrue(artikel.getArtikel_prijs() == 33.33);

		artikel = artikelIterator.next();
		assertTrue(artikel.getArtikel_id() == 3);
		assertEquals(artikel.getArtikel_naam(), "nieuwe naam 3");
		assertTrue(artikel.getArtikel_prijs() == 33.33);

		artikel = artikelIterator.next();
		assertTrue(artikel.getArtikel_id() == 3);
		assertEquals(artikel.getArtikel_naam(), "nieuwe naam 3");
		assertTrue(artikel.getArtikel_prijs() == 33.33);

		// @After Alberts testToevoeging in een methode gezet
		breakDownTestHetAanpassenVanAlleArtikelenInDeDataBase();
	}


	@Test
	public void testVerwijderArtikelVanBestelling() throws Exception{
		artikelDao.verwijderArtikelVanBestelling(bestelling_id, a1);
		Artikel a = artikelDao.getArtikelOpBestelling(bestelling_id, 1);

		assertTrue(0 == a.getArtikel_id());
		assertTrue("0".equals(a.getArtikel_naam()));
		assertTrue(0 == a.getArtikel_prijs());
	}

	@Test
	public void testVerwijderAlleArtikelenVanBestelling() throws Exception{
		artikelDao.verwijderAlleArtikelenVanBestelling(bestelling_id);
		aIterator = artikelDao.getAlleArtikelenOpBestelling(bestelling_id);

		while (aIterator.hasNext()) {
			Artikel a = aIterator.next();
			artikelLijst.add(a);
		}
		for(int i = 0; i < 3; i++) {
			assertTrue(0 == artikelLijst.get(i).getArtikel_id());
			assertTrue("0".equals(artikelLijst.get(i).getArtikel_naam()));
			assertTrue(0 == artikelLijst.get(i).getArtikel_prijs());
		}
	}

	// Utility methods
	public void assertUpdateArtikelOpBestelling(Artikel testArtikel, Artikel a) {
		assertTrue(testArtikel.equals(a));
		assertEquals(testArtikel.getArtikel_id(), a.getArtikel_id());
		assertEquals(testArtikel.getArtikel_naam(), a.getArtikel_naam());
		assertTrue(testArtikel.getArtikel_prijs() == a.getArtikel_prijs());
	}

	// Methodes behorende bij testHetAanpassenVanAlleArtikelenInDeDataBase();
	public void setUpTestHetAanpassenVanAlleArtikelenInDeDataBase() throws Exception{
		a1.setArtikel_id(1);
		a1.setArtikel_naam("artikel 1");
		a1.setArtikel_prijs(1.01);

		a2.setArtikel_id(2);
		a2.setArtikel_naam("artikel 2");
		a2.setArtikel_prijs(2.02);

		a3.setArtikel_id(3);
		a3.setArtikel_naam("artikel 3");
		a3.setArtikel_prijs(3.03);

		LinkedHashMap<Artikel, Integer> artikelLijst1 = new LinkedHashMap<Artikel, Integer>();
		artikelLijst1.put(a1, 1);
		artikelLijst1.put(a2, 1);
		artikelLijst1.put(a3, 1);
		bestelling1.setKlant_id(1);
		bestelling1.setArtikelLijst(artikelLijst1);

		LinkedHashMap<Artikel, Integer> artikelLijst2 = new LinkedHashMap<Artikel, Integer>();
		artikelLijst2.put(a1, 1);
		artikelLijst2.put(a2, 2);
		artikelLijst2.put(a3, 0);
		bestelling2.setKlant_id(1);
		bestelling2.setArtikelLijst(artikelLijst2);

		LinkedHashMap<Artikel, Integer> artikelLijst3 = new LinkedHashMap<Artikel, Integer>();
		artikelLijst3.put(a1, 0);
		artikelLijst3.put(a2, 0);
		artikelLijst3.put(a3, 3);
		bestelling3.setKlant_id(1);
		bestelling3.setArtikelLijst(artikelLijst3);


		id1 = bestellingDao.nieuweBestelling(bestelling1);
		id2 = bestellingDao.nieuweBestelling(bestelling2);
		id3 = bestellingDao.nieuweBestelling(bestelling3);
	}

	public void breakDownTestHetAanpassenVanAlleArtikelenInDeDataBase() throws Exception{
		bestellingDao.verwijderEnkeleBestelling(id1);
		bestellingDao.verwijderEnkeleBestelling(id2);
		bestellingDao.verwijderEnkeleBestelling(id3);
	}

	private Iterator<Artikel> getIteratorMetDubbelen(Bestelling teTesten){
		Iterator<Artikel> set = teTesten.getArtikelLijst().keySet().iterator();
		ArrayList<Artikel> artikelArray = new ArrayList<Artikel>();
		while(set.hasNext()){
			Artikel artikel = set.next();
			for(int x = 0; x < teTesten.getArtikelLijst().get(artikel); x++)
				artikelArray.add(artikel);
		}
		return artikelArray.iterator();
	}
}
