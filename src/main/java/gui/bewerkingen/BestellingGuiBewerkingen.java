package gui.bewerkingen;

import java.util.ArrayList;
import java.util.Iterator;

import exceptions.GeneriekeFoutmelding;
import gui.gui.BestellingGui;
import gui.gui.ErrorBox;
import gui.model.GuiPojo;
import gui.model.SubGuiPojo;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import model.Artikel;
import model.Bestelling;

public class BestellingGuiBewerkingen {

	// Voor aanpassen bestelling
	public void setAndRun(long klantId, Bestelling bestelling, boolean bestellingBewerken) throws Exception{
		setKlantId(klantId);
		SubGuiPojo.bestellingBewerken = bestellingBewerken;
		SubGuiPojo.bestelling = bestelling;
		new BestellingGui().start(new Stage());
	}

	// Voor nieuwe bestelling
	public void setAndRun(long klantId, ListView<Long> bestellingListView, boolean bestellingBewerken) throws Exception {
		setKlantId(klantId);
		SubGuiPojo.bestellingBewerken = bestellingBewerken;
		SubGuiPojo.bestelling = new Bestelling();
		SubGuiPojo.hoofdGuiBestellingListView = bestellingListView;
		new BestellingGui().start(new Stage());
	}

	public void voegArtikelenAanBestellingToe(int aantal){
		if(aantal > 0){ //Er moet minimaal 1 besteld worden
			if(!SubGuiPojo.bestelling.getArtikelLijst().contains(SubGuiPojo.huidigArtikel)){ //Als het er niet in staat, voeg het toe
				SubGuiPojo.huidigArtikel.setAantalBesteld(aantal);
				voegArtikelAanArtikelLijstToe(SubGuiPojo.huidigArtikel);
			}else{
				SubGuiPojo.huidigArtikel.setAantalBesteld(aantal);
			}
		}else{
			new ErrorBox().setMessageAndStart("Geef een geldig aantal op");
		}
	}

	public void getItemVanListView(int selectedIndex, String welkeArtikelLijst) {
		try{ 
			ArrayList<Artikel> artikelLijst;
			if(welkeArtikelLijst.equals("CompleteArtikelLijst"))
				artikelLijst = SubGuiPojo.artikelArrayList;
			else
				artikelLijst = SubGuiPojo.bestelling.getArtikelLijst();
			
			if(selectedIndex != -1){
				SubGuiPojo.huidigArtikel = artikelLijst.get(selectedIndex);
			}
		}catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
		}
	}

	public int getAantal(){
		return SubGuiPojo.huidigArtikel.getAantalBesteld();
	}

	public void voegArtikelAanArtikelLijstToe(Artikel huidigArtikel) {
		if(!SubGuiPojo.bestelling.getArtikelLijst().contains(huidigArtikel)) //Als het er niet in staat, voeg het toe
			SubGuiPojo.bestelling.voegArtikelToe(huidigArtikel);
		else
			new ErrorBox().setMessageAndStart("Geef een geldig aantal op");
	}

	public void populateListView(ListView<String> listView, String welkeArtikelLijst) {
		ArrayList<Artikel> artikelLijst;
		if(welkeArtikelLijst.equals("CompleteArtikelLijst"))
			artikelLijst = SubGuiPojo.artikelArrayList;
		else
			artikelLijst = SubGuiPojo.bestelling.getArtikelLijst();
		
		if(artikelLijst != null){
			listView.getItems().clear();
			for(Artikel artikel : artikelLijst){
				listView.getItems().add("Naam: " + artikel.getArtikelNaam() + "\nPrijs: " 
						+ artikel.getArtikelPrijs() + "\nAantal: " + artikel.getAantalBesteld());
			}
		}
	}

	public void populateArrayList(){
		try{
			SubGuiPojo.artikelArrayList = new ArrayList<Artikel>();
			Iterator<Artikel> artikelIterator = GuiPojo.artikelDAO.getAlleArtikelen(true).iterator(); //Haal alle artikelen op die in assortiment zijn
			Artikel artikel;

			while(artikelIterator.hasNext()){
				artikel = artikelIterator.next();
				SubGuiPojo.artikelArrayList.add(artikel);
			}
		} catch (GeneriekeFoutmelding e) {
			e.printStackTrace();
		}
	}

	public boolean maakBestelling(boolean bestellingAanpassen, boolean bestellingActief){
		SubGuiPojo.bestelling.setKlantId(SubGuiPojo.klantId);
		boolean sluiten = false;

		if(SubGuiPojo.bestelling.getArtikelLijst() != null){ // Een bestelling moet minimaal een artikel bevatten
			try {
				SubGuiPojo.bestelling.setBestellingActief(bestellingActief);
				if(bestellingAanpassen){
					if(bestellingActief){
						GuiPojo.bestelDAO.updateBestelling(SubGuiPojo.bestelling);
					}else{
						GuiPojo.bestelDAO.setEnkeleBestellingInactief(SubGuiPojo.bestelling.getBestellingId());
					}
				}else{
					long bestellingId = GuiPojo.bestelDAO.nieuweBestelling(SubGuiPojo.bestelling);
					SubGuiPojo.bestelling.setBestellingId(bestellingId);
					SubGuiPojo.hoofdGuiBestellingListView.getItems().add(bestellingId);
					GuiPojo.bestellingLijst.put(bestellingId, SubGuiPojo.bestelling);
				}
				sluiten = true;
			}catch(GeneriekeFoutmelding e) {
				e.printStackTrace();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return sluiten;
	}

	public void setKlantId(long klantId) {
		SubGuiPojo.klantId = klantId;
	}

	public void verwijderArtikel(Artikel huidigArtikel) {
		SubGuiPojo.bestelling.verwijderArtikel(SubGuiPojo.huidigArtikel);
	}


}
