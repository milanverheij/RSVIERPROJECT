package JUnit_tests;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;

import factories.DAOFactory;
import interfaces.ArtikelDAO;
import interfaces.BestellingDAO;
import interfaces.KlantDAO;
import model.Artikel;
import model.Bestelling;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BestellingDAOTestFireBird {

	Artikel a1 = new Artikel();
	Artikel a2 = new Artikel();
	Artikel a3 = new Artikel();

	Bestelling bestelling1 = new Bestelling();
	Bestelling bestelling2 = new Bestelling();

	long id1;
	long id2;

	BestellingDAO dao;
	ArtikelDAO artikelDao;
	KlantDAO klantDao;
	
	long klantId = 1;
	
	@Before
	public void setUp() throws Exception{
		
		DAOFactory birdFactory = DAOFactory.getDAOFactory("FireBird", "HikariCP");
		if(dao == null)
			dao = birdFactory.getBestellingDAO();

		if(artikelDao == null)
		artikelDao = birdFactory.getArtikelDAO();

		if(klantDao == null){
			klantDao = birdFactory.getKlantDAO();
//			klantId = klantDao.nieuweKlant("Albert" + (int) (Math.random() * 9999), "Lovers" + (int) (Math.random() * 9999));
		}

		a1.setArtikelNaam("Oerang Oetang");
		a1.setArtikelPrijs(new BigDecimal(1000.00));
		a1.setVerwachteLevertijd(3);
		a1.setInAssortiment(true);
		a1.setAantalBesteld(4);

		a2.setArtikelNaam("Aasgier");
		a2.setArtikelPrijs(new BigDecimal(1100.00));
		a2.setVerwachteLevertijd(4);
		a2.setInAssortiment(true);
		a2.setAantalBesteld(2);

		a3.setArtikelNaam("Luiaard");
		a3.setArtikelPrijs(new BigDecimal(500.00));
		a3.setVerwachteLevertijd(14);
		a3.setInAssortiment(false);
		a3.setAantalBesteld(3);

		a1.setArtikelId(artikelDao.nieuwArtikel(a1));
		a2.setArtikelId(artikelDao.nieuwArtikel(a2));
		a3.setArtikelId(artikelDao.nieuwArtikel(a3));
		
		ArrayList<Artikel> artikelLijst1 = new ArrayList<Artikel>();
		artikelLijst1.add(a1);
		artikelLijst1.add(a2);
		artikelLijst1.add(a3);
		bestelling1.setKlantId(klantId);
		bestelling1.setArtikelLijst(artikelLijst1);
		bestelling1.setBestellingActief(true);

		ArrayList<Artikel> artikelLijst2 = new ArrayList<Artikel>();
		artikelLijst2.add(a1);
		artikelLijst2.add(a2);
		bestelling2.setKlantId(klantId);
		bestelling2.setArtikelLijst(artikelLijst2);
		bestelling2.setBestellingActief(false);

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
		Bestelling b1 = dao.getBestellingOpBestellingId(id1, true).next();
		ArrayList<Artikel> b1List = b1.getArtikelLijst();

		assertEquals(a1.getArtikelId(), b1List.get(0).getArtikelId());
		assertEquals(a1.getArtikelNaam(), b1List.get(0).getArtikelNaam());
		assertTrue(a1.getArtikelPrijs().compareTo(b1List.get(0).getArtikelPrijs()) == 0);

		assertEquals(a2.getArtikelId(), b1List.get(1).getArtikelId());
		assertEquals(a2.getArtikelNaam(), b1List.get(1).getArtikelNaam());
		assertTrue(a2.getArtikelPrijs().compareTo(b1List.get(1).getArtikelPrijs()) == 0);

		assertEquals(a3.getArtikelId(), b1List.get(2).getArtikelId());
		assertEquals(a3.getArtikelNaam(), b1List.get(2).getArtikelNaam());
		assertTrue(a3.getArtikelPrijs().compareTo(b1List.get(2).getArtikelPrijs()) == 0);
	}

	@Test
	public void actiefInactiefGetBestellingTesten() throws Exception{
		assertNull(dao.getBestellingOpBestellingId(id2, true));

		Bestelling b1 = dao.getBestellingOpBestellingId(id1, true).next();
		ArrayList<Artikel> b1List = b1.getArtikelLijst();

		assertEquals(a1.getArtikelId(), b1List.get(0).getArtikelId());
		assertEquals(a1.getArtikelNaam(), b1List.get(0).getArtikelNaam());
		assertTrue(a1.getArtikelPrijs().compareTo(b1List.get(0).getArtikelPrijs()) == 0);

		assertEquals(a2.getArtikelId(), b1List.get(1).getArtikelId());
		assertEquals(a2.getArtikelNaam(), b1List.get(1).getArtikelNaam());
		assertTrue(a2.getArtikelPrijs().compareTo(b1List.get(1).getArtikelPrijs()) == 0);

		assertEquals(a3.getArtikelId(), b1List.get(2).getArtikelId());
		assertEquals(a3.getArtikelNaam(), b1List.get(2).getArtikelNaam());
		assertTrue(a3.getArtikelPrijs().compareTo(b1List.get(2).getArtikelPrijs()) == 0);

	}

	//Werkt alleen met een lege bestelling_heeft_artikel database wegens het lezen van alle tuples en het tegenkomen van onverwachte items
	@Test
	public void testAangemaakteTuplesLezenOpKlantId() throws Exception{

		//Eerste bestelling bevat a1, a2, a3
		Iterator<Bestelling> b1 = dao.getBestellingOpKlantId(klantId, false);
		ArrayList<Artikel> b1List = b1.next().getArtikelLijst();

		assertEquals(a1.getArtikelId(), b1List.get(0).getArtikelId());
		assertEquals(a1.getArtikelNaam(), b1List.get(0).getArtikelNaam());
		assertTrue(a1.getArtikelPrijs().compareTo(b1List.get(0).getArtikelPrijs()) == 0);
		assertTrue(a1.getAantalBesteld() == b1List.get(0).getAantalBesteld());

		assertEquals(a2.getArtikelId(), b1List.get(1).getArtikelId());
		assertEquals(a2.getArtikelNaam(), b1List.get(1).getArtikelNaam());
		assertTrue(a2.getArtikelPrijs().compareTo(b1List.get(1).getArtikelPrijs()) == 0);
		assertTrue(a2.getAantalBesteld() == b1List.get(1).getAantalBesteld());

		assertEquals(a3.getArtikelId(), b1List.get(2).getArtikelId());
		assertEquals(a3.getArtikelNaam(), b1List.get(2).getArtikelNaam());
		assertTrue(a3.getArtikelPrijs().compareTo(b1List.get(2).getArtikelPrijs()) == 0);
		assertTrue(a3.getAantalBesteld() == b1List.get(2).getAantalBesteld());

		//Tweede bestelling bevat a1, a2
		ArrayList<Artikel> b2List = b1.next().getArtikelLijst();

		assertEquals(a1.getArtikelId(), b2List.get(0).getArtikelId());
		assertEquals(a1.getArtikelNaam(), b2List.get(0).getArtikelNaam());
		assertTrue(a1.getArtikelPrijs().compareTo(b2List.get(0).getArtikelPrijs()) == 0);
		assertTrue(a1.getAantalBesteld() == b2List.get(0).getAantalBesteld());

		assertEquals(a2.getArtikelId(), b2List.get(1).getArtikelId());
		assertEquals(a2.getArtikelNaam(), b2List.get(1).getArtikelNaam());
		assertTrue(a2.getArtikelPrijs().compareTo(b2List.get(1).getArtikelPrijs()) == 0);
		assertTrue(a2.getAantalBesteld() == b1List.get(1).getAantalBesteld());
	}

	@Test
	public void updateBestellingInDeDatabaseTest() throws Exception{
		ArrayList<Artikel> artikelLijst = new ArrayList<Artikel>(); // gaat van a1 en a2 naar a2 en a3
		artikelLijst.add(a2);
		artikelLijst.add(a3);
		bestelling2.setArtikelLijst(artikelLijst);
		bestelling2.setBestellingId(id2);
		dao.updateBestelling(bestelling2);

		ArrayList<Artikel> bList = dao.getBestellingOpBestellingId(id2, false).next().getArtikelLijst();
		assertEquals(a2.getArtikelId(), bList.get(0).getArtikelId());
		assertEquals(a2.getArtikelNaam(), bList.get(0).getArtikelNaam());
		assertTrue(a2.getArtikelPrijs().compareTo(bList.get(0).getArtikelPrijs()) == 0);

		assertEquals(a3.getArtikelId(), bList.get(1).getArtikelId());
		assertEquals(a3.getArtikelNaam(), bList.get(1).getArtikelNaam());
		assertTrue(a3.getArtikelPrijs().compareTo(bList.get(1).getArtikelPrijs()) == 0);
	}

	@Test
	public void setAlsInactiefEenBestellingUitDeDatabase() throws Exception{
		assertTrue(dao.getBestellingOpBestellingId(id1, false).next().getBestellingActief());
		dao.setEnkeleBestellingInactief(id1);
		assertEquals(dao.getBestellingOpBestellingId(id1, false).next().getBestellingActief(), false);

		assertFalse(dao.getBestellingOpBestellingId(id2, false).next().getBestellingActief());
		dao.setEnkeleBestellingInactief(id2);
		assertFalse(dao.getBestellingOpBestellingId(id2, false).next().getBestellingActief());
	}

	@Test
	public void setAlsInactiefAlleBestellingenKlant() throws Exception{
		dao.setAlsInactiefAlleBestellingenKlant(klantId);
		assertNull(dao.getBestellingOpKlantId(klantId, true));
	}

	@Test
	public void verwijderenAlleBestellingenKlant() throws Exception{
		dao.verwijderAlleBestellingenKlant(klantId);
		assertNull(dao.getBestellingOpKlantId(klantId, false));	
	}

	@Test
	public void verwijderenEnkeleBestelling() throws Exception{
		dao.verwijderEnkeleBestelling(id1);
		assertNull(dao.getBestellingOpBestellingId(id1, false));

		dao.verwijderEnkeleBestelling(id2);
		assertNull(dao.getBestellingOpBestellingId(id2, false));
	}

}
