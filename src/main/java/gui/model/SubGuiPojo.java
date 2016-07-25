package gui.model;

import java.util.ArrayList;

import javafx.scene.control.ListView;
import model.Artikel;
import model.Bestelling;

public class SubGuiPojo {

	public static ListView<Long> hoofdGuiBestellingListView;

	public static Bestelling bestelling;
	public static Artikel huidigArtikel;
	public static ArrayList<Artikel> artikelArrayList;

	public static long klantId;

	public static boolean bestellingBewerken;
}