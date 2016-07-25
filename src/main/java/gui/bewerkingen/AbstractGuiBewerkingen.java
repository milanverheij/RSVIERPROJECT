package gui.bewerkingen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import exceptions.GeneriekeFoutmelding;
import gui.interfaces.GuiBewerkingen;
import gui.model.GuiPojo;
import javafx.scene.control.ListView;
import model.Artikel;
import model.Bestelling;
import model.Klant;

public abstract class AbstractGuiBewerkingen implements GuiBewerkingen{

	public void leegKlantBestellingArtikel(){
		GuiPojo.bestelling = new Bestelling();
		GuiPojo.artikel = new Artikel();
		GuiPojo.klant = new Klant();

		GuiPojo.artikelLijst = new ArrayList<Artikel>();
		GuiPojo.klantenLijst = new LinkedHashMap<Long, Klant>();
		GuiPojo.bestellingLijst = new LinkedHashMap<Long, Bestelling>();
	}

	public void resetArtikelVariabelen(){
		GuiPojo.artikelLijst = new ArrayList<Artikel>();
		GuiPojo.artikel = new Artikel();
	}

	public void resetBestellingVariabelen(){
		GuiPojo.bestellingLijst.clear();
		GuiPojo.bestelling = new Bestelling();
	}

	public void setArtikelLijst(){
		GuiPojo.artikelLijst.clear();
		GuiPojo.artikelLijst = GuiPojo.bestelling.getArtikelLijst();
	}

	public void populateBestellingListView(ListView<Long> bestellingListView, ArrayList<Bestelling> list) throws GeneriekeFoutmelding{
		for(Bestelling bestelling : list)
			if(!bestellingListView.getItems().contains(bestelling.getBestellingId()) && bestelling.getBestellingId() != 0){
				bestellingListView.getItems().add(bestelling.getBestellingId());
				GuiPojo.bestellingLijst.put(bestelling.getBestellingId(), bestelling);
			}
		}

	public void populateBestellingListView(ListView<Long> bestellingListView) throws GeneriekeFoutmelding{
		Iterator<Bestelling> it = GuiPojo.bestellingLijst.values().iterator();
		while(it.hasNext()){
			Bestelling bestelling = it.next();
			if(!bestellingListView.getItems().contains(bestelling.getBestellingId()) && bestelling.getBestellingId() != 0){
				bestellingListView.getItems().add(bestelling.getBestellingId());
				GuiPojo.bestellingLijst.put(bestelling.getBestellingId(), bestelling);
			}
		}
	}

	public void getItemVanKlantenLijst(ListView<String> klantListView){
		String selectedItem = klantListView.getSelectionModel().getSelectedItem();
		if(selectedItem != null){
			resetArtikelVariabelen();
			resetBestellingVariabelen();
			long itemId = Long.parseLong(selectedItem.split(":")[0]);
			GuiPojo.klant = GuiPojo.klantenLijst.get(itemId);
		}
	}

	public void getItemVanBestellingLijst(long selectedItem){
		resetArtikelVariabelen();
		GuiPojo.bestelling = GuiPojo.bestellingLijst.get(selectedItem);
	}

	public void setArtikelLijstInNieuweBestelling(){
		long bestellingId = GuiPojo.bestelling.getBestellingId();
		long klantId = GuiPojo.bestelling.getKlantId();

		GuiPojo.bestelling = new Bestelling();

		GuiPojo.bestelling.setBestellingId(bestellingId);
		GuiPojo.bestelling.setKlantId(klantId);

		GuiPojo.bestelling.setArtikelLijst(GuiPojo.artikelLijst);;
	}

	public void getItemVanArtikelLijst(int index){
		GuiPojo.artikel = GuiPojo.artikelLijst.get(index);
	}
}