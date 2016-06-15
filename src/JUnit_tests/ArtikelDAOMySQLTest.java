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

	//De te testen klasse
	ArtikelDAOMySQL tester = new ArtikelDAOMySQL();

	//BestellingDAOMySQL is nodig om Artikel te kunnen testen
	BestellingDAOMySQL besteller = new BestellingDAOMySQL();
	Bestelling bestelling = new Bestelling();

	//Data 
	long klant_id = 1;
	long bestelling_id;
	Artikel artikel;
	Artikel artikel1 = new Artikel(1, "Aap", 3500);
	Artikel artikel2 = new Artikel(2, "Leeuw", 5000);
	Artikel artikel3 = new Artikel(3, "Walvis", 30000);
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
		artikelLijstMap.put(artikel1, new Integer(1));
		artikelLijstMap.put(artikel2, new Integer(1));
		artikelLijstMap.put(artikel3, new Integer(1));
		bestelling.setArtikelLijst(artikelLijstMap);
		bestelling_id = besteller.nieuweBestelling(bestelling);
	}
	
	// Verwijder de bestelling na de test
	@After
	public void tearDown() throws Exception{
		besteller.verwijderEnkeleBestelling(bestelling_id);
		artikelLijst = null;
	}

	@Test
	public void testVoegNieuwArtikelToeAanBestelling() throws Exception{
		tester.nieuwArtikelOpBestelling(bestelling_id, testArtikel);
		Artikel a = tester.getArtikelOpBestelling(bestelling_id, 1);

		assertEquals(testArtikel.getArtikel_id(), a.getArtikel_id());
		assertEquals(testArtikel.getArtikel_naam(), a.getArtikel_naam());
		assertTrue(testArtikel.getArtikel_prijs() == a.getArtikel_prijs());
	}


	@Test 
	public void testGetArtikelOpBestelling() throws Exception{
		Artikel a = tester.getArtikelOpBestelling(bestelling_id, 1 /*artikelNummer*/);

		assertEquals(artikel1.getArtikel_id(), a.getArtikel_id());
		assertEquals(artikel1.getArtikel_naam(), a.getArtikel_naam());
		assertTrue(artikel1.getArtikel_prijs() == a.getArtikel_prijs());
	}


	@Test
	public void testGetAlleArtikelenOpBestelling() throws Exception{

		aIterator = tester.getAlleArtikelenOpBestelling(bestelling_id);

		while(aIterator.hasNext()) {
			Artikel a = aIterator.next();
			artikelLijst.add(a);
		}

		System.out.println(artikelLijst);

		assertEquals(artikel1.getArtikel_id(), artikelLijst.get(0).getArtikel_id());	
		assertEquals(artikel1.getArtikel_naam(), artikelLijst.get(0).getArtikel_naam());
		assertTrue(artikel1.getArtikel_prijs() == artikelLijst.get(0).getArtikel_prijs());

		assertEquals(artikel2.getArtikel_id(), artikelLijst.get(1).getArtikel_id());	
		assertEquals(artikel2.getArtikel_naam(), artikelLijst.get(1).getArtikel_naam());
		assertTrue(artikel2.getArtikel_prijs() == artikelLijst.get(1).getArtikel_prijs());

		assertEquals(artikel3.getArtikel_id(), artikelLijst.get(2).getArtikel_id());	
		assertEquals(artikel3.getArtikel_naam(), artikelLijst.get(2).getArtikel_naam());
		assertTrue(artikel3.getArtikel_prijs() == artikelLijst.get(2).getArtikel_prijs());
	}
	
	@Test 
	public void testGetAlleArtikelen() throws Exception{
		mIterator = tester.getAlleArtikelen();
		int artikelCount = 0;
		ArrayList<Integer> artikelAantal = new ArrayList<>();

		while(mIterator.hasNext()) {
			entry = mIterator.next();
			artikelLijst.add(entry.getKey());
			artikelAantal.add(entry.getValue());
		}

		int indexA1 = artikelLijst.indexOf(artikel1);
		int indexA2 = artikelLijst.indexOf(artikel2);
		int indexA3 = artikelLijst.indexOf(artikel3);

		if (artikelLijst.contains(artikel1)) {
			assertEquals(artikel1.getArtikel_id(), artikelLijst.get(indexA1).getArtikel_id());	
			assertEquals(artikel1.getArtikel_naam(), artikelLijst.get(indexA1).getArtikel_naam());
			assertTrue(artikel1.getArtikel_prijs() == artikelLijst.get(indexA1).getArtikel_prijs());
			assertTrue(1 == artikelAantal.get(indexA1));
			artikelCount++;
		}
		if (artikelLijst.contains(artikel2)) {
			assertEquals(artikel2.getArtikel_id(), artikelLijst.get(indexA2).getArtikel_id());	
			assertEquals(artikel2.getArtikel_naam(), artikelLijst.get(indexA2).getArtikel_naam());
			assertTrue(artikel2.getArtikel_prijs() == artikelLijst.get(indexA2).getArtikel_prijs());
			assertTrue(1 == artikelAantal.get(indexA1));
			artikelCount++;
		}
		if (artikelLijst.contains(artikel3)) {
			assertEquals(artikel3.getArtikel_id(), artikelLijst.get(indexA3).getArtikel_id());	
			assertEquals(artikel3.getArtikel_naam(), artikelLijst.get(indexA3).getArtikel_naam());
			assertTrue(artikel3.getArtikel_prijs() == artikelLijst.get(indexA3).getArtikel_prijs());
			assertTrue(1 == artikelAantal.get(indexA1));
			artikelCount++;
		}

		assertTrue(artikelCount == 3);
	}
	
	@Test 
	public void testUpdateArtikelOpBestelling() throws Exception{
		// Test de eerste overloaded methode
		int artikelNummer = 3;
		tester.updateArtikelOpBestelling(bestelling_id, artikelNummer, testArtikel);
		Artikel a = tester.getArtikelOpBestelling(bestelling_id, artikelNummer);
		assertUpdateArtikelOpBestelling(testArtikel, a);
		
		// Test de tweede overloaded methode
		artikelNummer = 1;
		tester.updateArtikelOpBestelling(bestelling_id, artikel1, testArtikel);
		a = tester.getArtikelOpBestelling(bestelling_id, artikelNummer);
		assertUpdateArtikelOpBestelling(testArtikel, a);

		
	}

	@Test
	public void testUpdateAlleArtikelenOpBestelling() throws Exception{
		tester.updateAlleArtikelenOpBestelling(bestelling_id, testArtikel, testArtikel, testArtikel);
		aIterator = tester.getAlleArtikelenOpBestelling(bestelling_id);

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

	@Test
	public void testVerwijderArtikelVanBestelling() throws Exception{
		tester.verwijderArtikelVanBestelling(bestelling_id, artikel1);
		Artikel a = tester.getArtikelOpBestelling(bestelling_id, 1);

		assertTrue(0 == a.getArtikel_id());
		assertTrue("0".equals(a.getArtikel_naam()));
		assertTrue(0 == a.getArtikel_prijs());
	}

	@Test
	public void testVerwijderAlleArtikelenVanBestelling() throws Exception{
		tester.verwijderAlleArtikelenVanBestelling(bestelling_id);
		aIterator = tester.getAlleArtikelenOpBestelling(bestelling_id);

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
}
