package JUnit_tests;

import static org.junit.Assert.*;

import java.math.BigDecimal;
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
	
	Artikel artikel;
	

	
	@Before
	public void setUp() throws Exception {
		artikel = new Artikel("Oerang Oetang", new BigDecimal(10000), "20-06-16", 3, true);
	}


	@After
	public void tearDown() throws Exception{
		
	}

	@Test
	public void nieuwArtikel() throws Exception {
		artikelDao.nieuwArtikel(artikel);
		
	}
	
}