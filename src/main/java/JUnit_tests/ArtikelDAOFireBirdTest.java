package JUnit_tests;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.math.BigDecimal;
import java.util.LinkedHashSet;

import factories.DAOFactoryFireBird;
import firebird.ArtikelDAOFireBird;
import model.Artikel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ArtikelDAOFireBirdTest {

	// De klasse die getest wordt
	private ArtikelDAOFireBird artikelDao;

	// Data
	int id1;
	int id2;
	int id3;

	int idGeretouneerd;
	int prijsID;

	Artikel a1 = new Artikel();
	Artikel a2 = new Artikel();
	Artikel a3 = new Artikel();
	Artikel aGeretouneerd = new Artikel();

	@Before
	public void setUp() throws Exception {

		if(artikelDao == null)
			artikelDao = (ArtikelDAOFireBird) DAOFactoryFireBird.getDAOFactory("FireBird", "HikariCP").getArtikelDAO();

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
		artikelDao.verWijderVoorHetEchie(id1);
		artikelDao.verWijderVoorHetEchie(id2);
		artikelDao.verWijderVoorHetEchie(id3);
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

		LinkedHashSet<Artikel> alleArtikelenLijst = artikelDao.getAlleArtikelen(/*actief =*/false);
		LinkedHashSet<Artikel> actieveArtikelenLijst = artikelDao.getAlleArtikelen(/*actief =*/true);

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
		//Het artikel dat verwijdert wordt
		a1 = new Artikel();
		a1.setArtikelNaam("Aasgier");
		a1.setArtikelPrijs(new BigDecimal(500));
		a1.setVerwachteLevertijd(5);
		a1.setInAssortiment(true);

		//Verkrijg het artikel id uit de database
		idGeretouneerd = artikelDao.nieuwArtikel(a1);
		//Verwijder het artikel
		artikelDao.verwijderArtikel(idGeretouneerd);
		//Verkrijg het artikel uit de database om te kijken of het verwijdert is
		aGeretouneerd = artikelDao.getArtikel(idGeretouneerd);

		// Het artikel is verwijdert wanneer het niet meer in het assortiment is.
		assertThat(aGeretouneerd.isInAssortiment(), is(equalTo(false)));

	}

	// Utility methodes
	public void testOfBeideArtikelenGelijkZijn(Artikel aGeretouneerd, Artikel a) {

		assertThat(aGeretouneerd.getArtikelId(), is(equalTo(id1)));
		assertThat(aGeretouneerd.getArtikelNaam(),is(equalTo(a.getArtikelNaam())));
		assertThat(aGeretouneerd.getArtikelPrijs().compareTo(a.getArtikelPrijs()), is(equalTo(0)));
		assertThat(aGeretouneerd.getPrijsId(), is(equalTo(a.getPrijsId())));
		assertThat(aGeretouneerd.getVerwachteLevertijd(), is(equalTo(a.getVerwachteLevertijd())));
		assertThat(aGeretouneerd.isInAssortiment(), is(equalTo(a.isInAssortiment())));
	}

}
