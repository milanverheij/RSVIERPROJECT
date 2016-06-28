package mysql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import exceptions.GeneriekeFoutmelding;
import gui.ErrorBox;
import interfaces.GuiBewerkingen;
import javafx.scene.control.ListView;
import model.Adres;
import model.Artikel;
import model.Bestelling;
import model.GuiPojo;
import model.Klant;

public abstract class AbstractGuiBewerkingen implements GuiBewerkingen{
	ErrorBox errorBox = new ErrorBox();

	public void leegKlantBestellingArtikel(){
		GuiPojo.bestelling = new Bestelling();
		GuiPojo.artikel = new Artikel();
		GuiPojo.klant = new Klant();
		GuiPojo.adres  = new Adres();

		GuiPojo.artikelLijst = new ArrayList<Artikel>();
		GuiPojo.klantenLijst = new LinkedHashMap<Long, Klant>();
		GuiPojo.bestellingLijst = new LinkedHashMap<Long, Bestelling>();
	}

	public void resetArtikelVariabelen(){
		GuiPojo.artikelLijst.clear();
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

	public void populateBestellingListView(ListView<Long> bestellingListView, Iterator<Bestelling> it){
		if(it == null)
			it = GuiPojo.bestellingLijst.values().iterator();
		while(it.hasNext()){
			Bestelling bestelling = it.next();
			if(!bestellingListView.getItems().contains(bestelling.getBestelling_id())){
				bestellingListView.getItems().add(bestelling.getBestelling_id());
				GuiPojo.bestellingLijst.put(bestelling.getBestelling_id(), bestelling);
			}
		}
	}

	public void populateBestellingListView(ListView<Long> bestellingListView){
		Iterator<Bestelling> it = GuiPojo.bestellingLijst.values().iterator();
		while(it.hasNext()){
			Bestelling bestelling = it.next();
			if(!bestellingListView.getItems().contains(bestelling.getBestelling_id())){
				bestellingListView.getItems().add(bestelling.getBestelling_id());
				GuiPojo.bestellingLijst.put(bestelling.getBestelling_id(), bestelling);
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
		long bestellingId = GuiPojo.bestelling.getBestelling_id();
		long klantId = GuiPojo.bestelling.getKlant_id();

		GuiPojo.bestelling = new Bestelling();

		GuiPojo.bestelling.setBestelling_id(bestellingId);
		GuiPojo.bestelling.setKlant_id(klantId);

		GuiPojo.bestelling.setArtikelLijst(GuiPojo.artikelLijst);;
	}

	public void getItemVanArtikelLijst(int index){
		GuiPojo.artikel = GuiPojo.artikelLijst.get(index);
	}

	public abstract void updateArtikel(Artikel nieuwArtikel);

	public abstract void zoekKlant(ListView<String> klantListView, String klantId, String voorNaam, String achterNaam, String tussenVoegsel, String email);

	public abstract void zoekBestelling(String bron, ListView<Long> bestellingListView, String klantIdField, String bestellingIdField) throws SQLException;

	public abstract void updateBestelling() throws SQLException, GeneriekeFoutmelding;

	public abstract void verwijderEnkeleBestelling() throws GeneriekeFoutmelding, SQLException;
}
