package gui.bewerkingen;

import java.util.Iterator;
import java.util.LinkedHashMap;

import exceptions.GeneriekeFoutmelding;
import database.factories.DAOFactory;
import gui.gui.ArtikelGui;
import gui.gui.KlantGui;
import gui.model.GuiPojo;
import gui.model.SubGuiPojo;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import logger.DeLogger;
import model.Adres;
import model.Artikel;
import model.Bestelling;
import model.Klant;

public class HoofdGuiBewerkingen extends AbstractGuiBewerkingen {

	public void zoekKlant(ListView<String> klantListView, String klantId, String voorNaam, String achterNaam, String tussenVoegsel, String email){
		klantListView.getItems().clear();
		Klant klant = new Klant(!klantId.equals("") ? Long.parseLong(klantId) : 0, voorNaam, achterNaam, tussenVoegsel, email, null);

		try {
			Iterator<Klant> klantIterator = GuiPojo.klantDAO.getKlantOpKlant(klant);
			verwerkKlantResultSet(klantIterator, klantListView);
		} catch (GeneriekeFoutmelding e) {
			e.printStackTrace();
			errorBox.setMessageAndStart(e.getMessage());
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void zoekBestelling(String bron, ListView<Long> bestellingListView, String klantIdField, String bestellingIdField, boolean actieveItems){
		try{
			Iterator<Bestelling> it = bron.equals("klantId") ? GuiPojo.bestelDAO.getBestellingOpKlantId(Long.parseLong(klantIdField), actieveItems) : GuiPojo.bestelDAO.getBestellingOpBestellingId(Long.parseLong(bestellingIdField), actieveItems);

			populateBestellingListView(bestellingListView, it);
		}catch(NumberFormatException | GeneriekeFoutmelding | NullPointerException e){
			errorBox.setMessageAndStart(e.getMessage());
		}
	}

	public void verwerkKlantResultSet(Iterator<Klant> klantIterator, ListView<String> klantListView) throws GeneriekeFoutmelding{
		try{
			Klant klant;
			String gegevens;
			while(klantIterator.hasNext()){
				klant = klantIterator.next();
				if(klant.getTussenvoegsel() == null)
					klant.setTussenvoegsel("");
				gegevens = klant.getTussenvoegsel().equals("") ? klant.getKlantId() + ": " + klant.getVoornaam() + " " + klant.getAchternaam() : klant.getKlantId() + ": " + klant.getVoornaam() + " " + klant.getTussenvoegsel() + " " + klant.getAchternaam();
				if(!klantListView.getItems().contains(gegevens))
					klantListView.getItems().add(gegevens);
				GuiPojo.klantenLijst.put(klant.getKlantId(), klant);
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new GeneriekeFoutmelding("error");
		}
	}

	public void updateArtikel(Artikel nieuwArtikel){
		setArtikelLijst();
		try{
			GuiPojo.artikelDAO.updateArtikel(nieuwArtikel.getArtikelId(), nieuwArtikel);
			GuiPojo.artikel.setArtikelNaam(nieuwArtikel.getArtikelNaam());
			GuiPojo.artikel.setArtikelPrijs(nieuwArtikel.getArtikelPrijs());
			setArtikelLijst();
		}catch (NumberFormatException | GeneriekeFoutmelding e){
			errorBox.setMessageAndStart(e.getMessage());
		}
	}

	public void updateBestelling(){
		try{
			if(!(GuiPojo.bestelling.getBestellingId() == 0)){
				BestellingGuiBewerkingen bestellingBewerken = new BestellingGuiBewerkingen();
				bestellingBewerken.setAndRun(GuiPojo.bestelling.getKlantId(), GuiPojo.bestelling, true);
			}else{
				errorBox.setMessageAndStart("Selecteer eerst een bestelling");
			}
			GuiPojo.bestelDAO.updateBestelling(GuiPojo.bestelling);
		}catch (GeneriekeFoutmelding e){
			errorBox.setMessageAndStart(e.getMessage());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* Verwijdert een enkele bestelling uit de database */
	public void verwijderEnkeleBestelling(ListView<Long> bestellingListView){
		try {
			GuiPojo.bestellingLijst.remove(GuiPojo.bestelling.getBestellingId());
			bestellingListView.getItems().clear();

			populateBestellingListView(bestellingListView);

			GuiPojo.bestelDAO.verwijderEnkeleBestelling(GuiPojo.bestelling.getBestellingId());
		}catch (GeneriekeFoutmelding e){
			errorBox.setMessageAndStart(e.getMessage());
		}
		GuiPojo.bestelling = new Bestelling();
	}

	public void setDAOs(String databaseSelected, String connectionSelected) {
		try {
			DAOFactory factory = DAOFactory.getDAOFactory(databaseSelected, connectionSelected);
			GuiPojo.bestelDAO = factory.getBestellingDAO();
			GuiPojo.artikelDAO = factory.getArtikelDAO();
			GuiPojo.klantDAO = factory.getKlantDAO();
			GuiPojo.adresDAO = factory.getAdresDAO();
			DeLogger.getLogger().info("Database: " + databaseSelected + " Connection pool: " + connectionSelected);
		}catch (GeneriekeFoutmelding e){
			DeLogger.getLogger().error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void getAdres(boolean selected) {

		try {
			Iterator<Adres> adresIt = GuiPojo.adresDAO.getAdresOpKlantID(GuiPojo.klant.getKlantId());
			while(adresIt.hasNext()){
				Adres adres = adresIt.next();

				if(adres.getAdresActief().trim().equals("1")){ //TODO ook optie voor als adres niet actief is
					GuiPojo.klant.setAdresGegevens(adres);
					break;
				}
			}
		}catch (GeneriekeFoutmelding e){
			errorBox.setMessageAndStart(String.format("Fout bij ophalen alle adressen van klant %d", GuiPojo.klant.getKlantId()));
			e.printStackTrace();
		}
	}

	public void maakArtikelGui() {
		try {
			ArtikelGui artikelGui = new ArtikelGui();
			artikelGui.setAndRun(GuiPojo.artikelDAO.getAlleArtikelen(false));
		} catch (GeneriekeFoutmelding e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void resetBestelling() {
		GuiPojo.bestelling = new Bestelling();
		GuiPojo.bestellingLijst = new LinkedHashMap<Long, Bestelling>();
	}

	public void nieuweBestelling(ListView<Long> bestellingListView, String klantIdField){
		if(!klantIdField.isEmpty()){
			BestellingGuiBewerkingen bestellingBewerken = new BestellingGuiBewerkingen();
			try {
				SubGuiPojo.bestelling = null;
				bestellingBewerken.setAndRun(GuiPojo.klant.getKlantId(), bestellingListView, false);
			} catch (Exception e) {
				e.printStackTrace();
				errorBox.setMessageAndStart(e.getMessage());
			}
		}else{
			errorBox.setMessageAndStart("Selecteer eerst een klant");
		}
	}

	public void updateKlant() {
		try {
			if(GuiPojo.klant.getKlantId() == 0)
				errorBox.setMessageAndStart("Selecteer eerst een klant");
			else{
				KlantGui nieuweKlant = new KlantGui();
				nieuweKlant.start(new Stage());
			}
		}catch(GeneriekeFoutmelding e){
			errorBox.setMessageAndStart(e.getMessage());
		}catch(NullPointerException e){
			e.printStackTrace();
		}catch (Exception e) {
			errorBox.setMessageAndStart(e.getMessage());
		}
	}

	public void nieuweKlant() {
		try {
			new KlantGui().start(new Stage());
		} catch (Exception e) {
			errorBox.setMessageAndStart(e.getMessage());
		}
	}


	public void zoekViaBestellingKlant() {
		Klant klant = new Klant();
		klant.setKlantId(GuiPojo.bestelling.getKlantId());
		try {
			GuiPojo.klant = GuiPojo.klantDAO.getKlantOpKlant(klant).next();
		} catch (GeneriekeFoutmelding e) {
			DeLogger.getLogger().error("kan de klant niet vinden aan de hand van klantId {}", GuiPojo.bestelling.getKlantId(), e.getStackTrace());
			errorBox.setMessageAndStart("kan de klant niet vinden aan de hand van klantId " + GuiPojo.bestelling.getKlantId());
			e.printStackTrace();
		}
	}
}