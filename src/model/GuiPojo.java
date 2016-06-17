package model;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class GuiPojo {
	public static long klantId;

	public static Bestelling bestelling = new Bestelling();
	public static Artikel artikel = new Artikel();
	public static Klant klant = new Klant();
	public static Adres adres  = new Adres();

	public static ArrayList<Artikel> artikelLijst = new ArrayList<Artikel>();
	public static LinkedHashMap<Long, Klant> klantenLijst = new LinkedHashMap<Long, Klant>();
	public static LinkedHashMap<Long, Bestelling> bestellingLijst = new LinkedHashMap<Long, Bestelling>();
}
