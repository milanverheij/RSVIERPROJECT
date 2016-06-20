package JUnit_tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
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

	long id1;
	long id2;

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

		ArrayList<Artikel> artikelLijst1 = new ArrayList<Artikel>();
		artikelLijst1.add(a1);
		artikelLijst1.add(a2);
		artikelLijst1.add(a3);
		bestelling1.setKlant_id(1);
		bestelling1.setArtikelLijst(artikelLijst1);

		ArrayList<Artikel> artikelLijst2 = new ArrayList<Artikel>();
		artikelLijst2.add(a1);
		artikelLijst2.add(a2);
		artikelLijst2.add(a3);
		bestelling2.setKlant_id(1);
		bestelling2.setArtikelLijst(artikelLijst2);

		id1 = dao.nieuweBestelling(bestelling1);
		id2 = dao.nieuweBestelling(bestelling2);
	}

	@After
	public void breakDown() throws Exception{
		dao.verwijderEnkeleBestelling(id1);
		dao.verwijderEnkeleBestelling(id2);
	}


	@Test
	public void testAangemaakteTuplesLezenOpBestellingId() throws Exception{

		//Eerste bestelling bevat a1, a2, a3
		Bestelling b1 = dao.getBestellingOpBestellingId(id1).next();
		ArrayList<Artikel> b1List = b1.getArtikelLijst();

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
		Iterator<Bestelling> b1 = dao.getBestellingOpKlantId(1);
		ArrayList<Artikel> b1List = b1.next().getArtikelLijst();

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
		ArrayList<Artikel> b2List = b1.next().getArtikelLijst();

		assertEquals(a1.getArtikel_id(), b2List.get(0).getArtikel_id());
		assertEquals(a1.getArtikel_naam(), b2List.get(0).getArtikel_naam());
		assertTrue(a1.getArtikel_prijs() == b2List.get(0).getArtikel_prijs());

		assertEquals(a2.getArtikel_id(), b2List.get(1).getArtikel_id());
		assertEquals(a2.getArtikel_naam(), b2List.get(1).getArtikel_naam());
		assertTrue(a2.getArtikel_prijs() == b2List.get(1).getArtikel_prijs());

		assertEquals(a2.getArtikel_id(), b2List.get(1).getArtikel_id());
		assertEquals(a2.getArtikel_naam(), b2List.get(1).getArtikel_naam());
		assertTrue(a2.getArtikel_prijs() == b2List.get(1).getArtikel_prijs());
	}

	@Test
	public void updateBestellingInDeDatabaseTest() throws Exception{

		ArrayList<Artikel> artikelLijst = new ArrayList<Artikel>();
		artikelLijst.add(a1);
		artikelLijst.add(a1);
		artikelLijst.add(a1);
		bestelling2.setArtikelLijst(artikelLijst);
		bestelling2.setBestelling_id(id2);
		dao.updateBestelling(bestelling2);

		ArrayList<Artikel> bList = dao.getBestellingOpBestellingId(id2).next().getArtikelLijst();
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

		assertNull(dao.getBestellingOpBestellingId(id1));
		assertNull(dao.getBestellingOpBestellingId(id2));
	}

	@Test
	public void removeAllBestellingenVanEenKlantUitDeDataBase() throws Exception{
		dao.verwijderAlleBestellingenKlant(1);
		assertNull(dao.getBestellingOpKlantId(1));
	}
}
