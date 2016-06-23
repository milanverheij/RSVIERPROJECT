package JUnit_tests;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;

import java.math.BigDecimal;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import model.Artikel;
import mysql.ArtikelDAOMySQL;

public class ArtikelDAOMySQLTest {
	
	//De klasse die getest wordt
	ArtikelDAOMySQL artikelDao = new ArtikelDAOMySQL();
	
	// Data
	int artikelId, prijsId;
	Artikel a1, a2, aVerwacht;
	

	
	@Before
	public void setUp() throws Exception {
		a1 = new Artikel("Oerang Oetang", new BigDecimal(1000), "2016-06-21", 3, true);
		a2 = new Artikel("Oerang Oetang", new BigDecimal(1000), "2016-06-22", 4, true);
		a1.setArtikelId(1);
	}


	@After
	public void tearDown() throws Exception{
		
	}
	
	@Test
	public void nieuwArtikel() throws Exception {
		artikelDao.nieuwArtikel(a1);
		
	}
	
	@Test
	public void getArtikel() throws Exception {
		artikelId = 1;
		a2 = artikelDao.getArtikel(artikelId);
		
		assertThat(a2.getArtikelId(), is(equalTo(a1.getArtikelId())));
		assertThat(a2.getArtikelNaam(),is(equalTo(a1.getArtikelNaam())));
		//assertThat(a1.getArtikelPrijs(), is(BigDecimalCloseTo(a2.getArtikelPrijs())))
		
		assertThat(a2.getArtikelPrijs().compareTo(a1.getArtikelPrijs()), is(equalTo(0)));
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