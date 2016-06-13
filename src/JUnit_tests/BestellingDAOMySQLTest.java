package JUnit_tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import model.Artikel;
import model.Bestelling;
import mysql.BestellingDAOMySQL;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BestellingDAOMySQLTest {

	Artikel a1 = new Artikel();
	Artikel a2 = new Artikel();
	Artikel a3 = new Artikel();
	Bestelling bestelling1 = new Bestelling();
	Bestelling bestelling2 = new Bestelling();
	Bestelling bestelling3 = new Bestelling();

	long id1;
	long id2;
	long id3;

	BestellingDAOMySQL dao = new BestellingDAOMySQL();

	@Before
	public void setUp() throws Exception{
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


		id1 = dao.nieuweBestelling(bestelling1);
		id2 = dao.nieuweBestelling(bestelling2);
		id3 = dao.nieuweBestelling(bestelling3);
	}

	@After
	public void breakDown() throws Exception{

		dao.verwijderEnkeleBestelling(id1);
		dao.verwijderEnkeleBestelling(id2);
		dao.verwijderEnkeleBestelling(id3);

	}

	@Test
	public void aanmakenVanEenBestelingInDeDatabase() throws Exception{
		Long id = dao.nieuweBestelling(171, a1);

		Bestelling bestelling = dao.getBestellingOpKlantGegevens(171).next();
		Artikel artikel = bestelling.getArtikelLijst().keySet().iterator().next();

		assertEquals(171, bestelling.getKlant_id());
		assertEquals(a1.getArtikel_naam(), artikel.getArtikel_naam());
		assertTrue(a1.getArtikel_prijs() == artikel.getArtikel_prijs());
		assertTrue(a1.getArtikel_id() == artikel.getArtikel_id());

		dao.verwijderEnkeleBestelling(id);
	}


	@Test
	public void testAangemaakteTuplesLezenOpBestellingId() throws Exception{

		//Eerste bestelling bevat a1, a2, a3
		Bestelling b1 = dao.getBestellingOpBestelling(id1).next();
		ArrayList<Artikel> b1List = getArtikelArray(b1);

		assertEquals(a1.getArtikel_id(), b1List.get(0).getArtikel_id());
		assertEquals(a1.getArtikel_naam(), b1List.get(0).getArtikel_naam());
		assertTrue(a1.getArtikel_prijs() == b1List.get(0).getArtikel_prijs());

		assertEquals(a2.getArtikel_id(), b1List.get(1).getArtikel_id());
		assertEquals(a2.getArtikel_naam(), b1List.get(1).getArtikel_naam());
		assertTrue(a2.getArtikel_prijs() == b1List.get(1).getArtikel_prijs());

		assertEquals(a3.getArtikel_id(), b1List.get(2).getArtikel_id());
		assertEquals(a3.getArtikel_naam(), b1List.get(2).getArtikel_naam());
		assertTrue(a3.getArtikel_prijs() == b1List.get(2).getArtikel_prijs());


	}
	@Test
	public void testAangemaakteTuplesLezenOpKlantId() throws Exception{

		//Eerste bestelling bevat a1, a2, a3
		Iterator<Bestelling> b1 = dao.getBestellingOpKlantGegevens(1);
		ArrayList<Artikel> b1List = getArtikelArray(b1.next());

		assertEquals(a1.getArtikel_id(), b1List.get(0).getArtikel_id());
		assertEquals(a1.getArtikel_naam(), b1List.get(0).getArtikel_naam());
		assertTrue(a1.getArtikel_prijs() == b1List.get(0).getArtikel_prijs());

		assertEquals(a2.getArtikel_id(), b1List.get(1).getArtikel_id());
		assertEquals(a2.getArtikel_naam(), b1List.get(1).getArtikel_naam());
		assertTrue(a2.getArtikel_prijs() == b1List.get(1).getArtikel_prijs());

		assertEquals(a3.getArtikel_id(), b1List.get(2).getArtikel_id());
		assertEquals(a3.getArtikel_naam(), b1List.get(2).getArtikel_naam());
		assertTrue(a3.getArtikel_prijs() == b1List.get(2).getArtikel_prijs());

		//Tweede bestelling bevat a1, a2, a2
		Bestelling b2 = b1.next();
		ArrayList<Artikel> b2List = getArtikelArray(b2);

		assertEquals(a1.getArtikel_id(), b2List.get(0).getArtikel_id());
		assertEquals(a1.getArtikel_naam(), b2List.get(0).getArtikel_naam());
		assertTrue(a1.getArtikel_prijs() == b2List.get(0).getArtikel_prijs());

		assertEquals(a2.getArtikel_id(), b2List.get(1).getArtikel_id());
		assertEquals(a2.getArtikel_naam(), b2List.get(1).getArtikel_naam());
		assertTrue(a2.getArtikel_prijs() == b2List.get(1).getArtikel_prijs());

		assertEquals(a2.getArtikel_id(), b2List.get(1).getArtikel_id());
		assertEquals(a2.getArtikel_naam(), b2List.get(1).getArtikel_naam());
		assertTrue(a2.getArtikel_prijs() == b2List.get(1).getArtikel_prijs());

		//Derde bestelling bevat a3, a3, a3
		Bestelling b3 = b1.next();
		ArrayList<Artikel> b3List = getArtikelArray(b3);

		assertEquals(a3.getArtikel_id(), b3List.get(0).getArtikel_id());
		assertEquals(a3.getArtikel_naam(), b3List.get(0).getArtikel_naam());
		assertTrue(a3.getArtikel_prijs() == b3List.get(0).getArtikel_prijs());

		assertEquals(a3.getArtikel_id(), b3List.get(1).getArtikel_id());
		assertEquals(a3.getArtikel_naam(), b3List.get(1).getArtikel_naam());
		assertTrue(a3.getArtikel_prijs() == b3List.get(1).getArtikel_prijs());

		assertEquals(a3.getArtikel_id(), b3List.get(2).getArtikel_id());
		assertEquals(a3.getArtikel_naam(), b3List.get(2).getArtikel_naam());
		assertTrue(a3.getArtikel_prijs() == b3List.get(2).getArtikel_prijs());


	}

	@Test
	public void updateBestellingInDeDatabaseTest() throws Exception{

		LinkedHashMap<Artikel, Integer> artikelLijst = new LinkedHashMap<Artikel, Integer>();
		artikelLijst.put(a1, 3);
		bestelling2.setArtikelLijst(artikelLijst);
		bestelling2.setBestelling_id(id2);
		dao.updateBestelling(bestelling2);

		ArrayList<Artikel> bList = getArtikelArray(dao.getBestellingOpBestelling(id2).next());
		assertEquals(a1.getArtikel_id(), bList.get(0).getArtikel_id());
		assertEquals(a1.getArtikel_naam(), bList.get(0).getArtikel_naam());
		assertTrue(a1.getArtikel_prijs() == bList.get(0).getArtikel_prijs());

		assertEquals(a1.getArtikel_id(), bList.get(1).getArtikel_id());
		assertEquals(a1.getArtikel_naam(), bList.get(1).getArtikel_naam());
		assertTrue(a1.getArtikel_prijs() == bList.get(1).getArtikel_prijs());

		assertEquals(a1.getArtikel_id(), bList.get(2).getArtikel_id());
		assertEquals(a1.getArtikel_naam(), bList.get(2).getArtikel_naam());
		assertTrue(a1.getArtikel_prijs() == bList.get(2).getArtikel_prijs());		

	}

	@Test
	public void removeOneBestellingUitDeDatabase() throws Exception{

		dao.verwijderEnkeleBestelling(id1);
		dao.verwijderEnkeleBestelling(id2);
		dao.verwijderEnkeleBestelling(id3);

		assertNull(dao.getBestellingOpBestelling(id1));
		assertNull(dao.getBestellingOpBestelling(id2));
		assertNull(dao.getBestellingOpBestelling(id3));

	}

	@Test
	public void removeAllBestellingenVanEenKlantUitDeDataBase() throws Exception{

		dao.verwijderAlleBestellingenKlant(1);
		assertNull(dao.getBestellingOpKlantGegevens(1));

	}

	public ArrayList<Artikel> getArtikelArray(Bestelling b){
		ArrayList<Artikel> list = new ArrayList<Artikel>();
		LinkedHashMap<Artikel, Integer> artikelLijst = b.getArtikelLijst();
		Iterator<Artikel> iterator = artikelLijst.keySet().iterator();

		while(iterator.hasNext()){
			Artikel artikel = iterator.next();
			for(int x = 0; x < artikelLijst.get(artikel);x++){
				list.add(artikel);
			}
		}
		return list;
	}
}
