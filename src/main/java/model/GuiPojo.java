package model;

import gui.gui.ErrorBox;
import interfaces.AdresDAO;
import interfaces.ArtikelDAO;
import interfaces.BestellingDAO;
import interfaces.KlantDAO;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class GuiPojo {
	public static BestellingDAO bestelDAO;
	public static ArtikelDAO artikelDAO;
	public static KlantDAO klantDAO;
	public static AdresDAO adresDAO;
	
	public static ErrorBox errorBox = new ErrorBox();
	
	public static long klantId;

	public static Bestelling bestelling = new Bestelling();
	public static Artikel artikel = new Artikel();
	public static Klant klant = new Klant();

	public static ArrayList<Artikel> artikelLijst = new ArrayList<Artikel>();
	public static LinkedHashMap<Long, Klant> klantenLijst = new LinkedHashMap<Long, Klant>();
	public static LinkedHashMap<Long, Bestelling> bestellingLijst = new LinkedHashMap<Long, Bestelling>();
}
