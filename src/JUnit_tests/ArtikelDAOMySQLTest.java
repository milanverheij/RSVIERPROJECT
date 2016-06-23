package JUnit_tests;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import model.Artikel;
import mysql.ArtikelDAOMySQL;

public class ArtikelDAOMySQLTest {
	
	//De klasse die getest wordt
	ArtikelDAOMySQL artikelDao = new ArtikelDAOMySQL();
	
	// Data
	int artikelId, prijsId;
	Artikel a1, a2, a3, aVerwacht;
	

	
	@Before
	public void setUp() throws Exception {
		a1 = new Artikel("Oerang Oetang", new BigDecimal(1000), "2016-06-21", 3, true);
		a2 = new Artikel("Oerang Oetang", new BigDecimal(1100), "2016-06-22", 4, true);
		a3 = new Artikel("Luiaard", new BigDecimal(500), "", 14, false);
	}


	@After
	public void tearDown() throws Exception{
		
	}
	
	@Test
	public void nieuwArtikel() throws Exception {
		artikelId = artikelDao.nieuwArtikel(a1);
		
	}
	
	@Test
	public void getArtikel() throws Exception {
		a2 = artikelDao.getArtikel(artikelId);
		
		assertThat(a2.getArtikelId(), is(equalTo(artikelId)));
		assertThat(a2.getArtikelNaam(),is(equalTo(a1.getArtikelNaam())));
		assertThat(a2.getArtikelPrijs().compareTo(a1.getArtikelPrijs()), is(equalTo(0)));
		assertThat(a2.getPrijsId(), is(equalTo(a1.getPrijsId())));
		assertThat(a2.getDatumAanmaak(), is(equalTo(a1.getDatumAanmaak())));
		assertThat(a2.getVerwachteLevertijd(), is(equalTo(a1.getVerwachteLevertijd())));
		assertThat(a2.isInAssortiment(), is(equalTo(a1.isInAssortiment())));
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
		artikelDao.updateArtikel(1, a2);
		aVerwacht = artikelDao.getArtikel(1);
		System.out.println(a2.toString() + "\n" + aVerwacht.toString());
		assertThat(aVerwacht.getArtikelId(), is(equalTo(a2.getArtikelId())));
		assertThat(aVerwacht.getVerwachteLevertijd(), is(equalTo(a2.getVerwachteLevertijd())));
	}
	
	@Test
	public void verwijderArtikel() throws Exception {
		a1 = new Artikel("Aasgier", new BigDecimal(500), "2016-06-23", 5, true);
		artikelId = artikelDao.nieuwArtikel(a1);
		artikelDao.verwijderArtikel(a1);
		
		// Test of het artikel verwijdert is
		aVerwacht = artikelDao.getArtikel(artikelId);
		
		assertThat(aVerwacht.isInAssortiment(), is(equalTo(false)));
	}
	
}