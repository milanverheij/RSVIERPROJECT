package gui.bewerkingen;

import java.util.*;

import exceptions.GeneriekeFoutmelding;
import database.factories.DAOFactory;
import gui.gui.AdresGui;
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

	public void zoekKlant(ListView<String> klantListView, Klant klant){
		klantListView.getItems().clear();

		try {
			ArrayList<Klant> klantAdres = GuiPojo.klantDAO.getKlantOpAdres(klant.getAdresGegevens().get(0)); // TODO ook hier nettere manier voor adresgegevens			
			klant.setAdresGegevens(null);
			ArrayList<Klant> klantKlant = GuiPojo.klantDAO.getKlantOpKlant(klant);
			
			klantKlant.retainAll(klantAdres);

			verwerkKlantResultSet(klantKlant, klantListView);
		} catch (GeneriekeFoutmelding e) {
			e.printStackTrace();
			DeLogger.getLogger().error("Fout bij verwerken klantgegevens {}", e.getMessage(), e.getStackTrace());
			GuiPojo.errorBox.setMessageAndStart(e.getMessage());
		}catch(Exception e){
			e.printStackTrace();
			DeLogger.getLogger().error("Fout bij zoeken klant {}", e.getMessage(), e.getStackTrace());
			GuiPojo.errorBox.setMessageAndStart(String.format("Fout bij zoeken klant"));
		}
	}

	public void zoekBestelling(String bron, ListView<Long> bestellingListView, String klantIdField, String bestellingIdField, boolean actieveItems){
		try{
			ArrayList<Bestelling> list = bron.equals("klantId") ? GuiPojo.bestelDAO.getBestellingOpKlantId(Long.parseLong(klantIdField), actieveItems) : GuiPojo.bestelDAO.getBestellingOpBestellingId(Long.parseLong(bestellingIdField), actieveItems);

			populateBestellingListView(bestellingListView, list);
		}catch(NumberFormatException | GeneriekeFoutmelding | NullPointerException e){
			e.printStackTrace();
			DeLogger.getLogger().error("Fout bij zoeken bestelling {}", e.getMessage(), e.getStackTrace());
			GuiPojo.errorBox.setMessageAndStart(e.getMessage());
		}
	}

	public void verwerkKlantResultSet(ArrayList<Klant> klantIterator, ListView<String> klantListView) throws GeneriekeFoutmelding{
		try{
			String gegevens;
			for(Klant klant : klantIterator){
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
			e.printStackTrace();
			DeLogger.getLogger().error("Fout bij updaten artikel {}", e.getMessage(), e.getStackTrace());
			GuiPojo.errorBox.setMessageAndStart(e.getMessage());
		}
	}

	public void updateBestelling(){
		try{
			if(!(GuiPojo.bestelling.getBestellingId() == 0)){
				BestellingGuiBewerkingen bestellingBewerken = new BestellingGuiBewerkingen();
				bestellingBewerken.setAndRun(GuiPojo.bestelling.getKlantId(), GuiPojo.bestelling, true);
			}else{
				GuiPojo.errorBox.setMessageAndStart("Selecteer eerst een bestelling");
			}
			GuiPojo.bestelDAO.updateBestelling(GuiPojo.bestelling);
		}catch (GeneriekeFoutmelding e){
			e.printStackTrace();
			DeLogger.getLogger().error("Fout bij updaten bestelling {}", e.getMessage(), e.getStackTrace());
			GuiPojo.errorBox.setMessageAndStart(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			DeLogger.getLogger().error("Onbekende error {}", e.getMessage(), e.getStackTrace());
			GuiPojo.errorBox.setMessageAndStart("Onbekende fout");
		}
	}

	/* Verwijdert een enkele bestelling uit de database */
	public void verwijderEnkeleBestelling(ListView<Long> bestellingListView){
		try {
			GuiPojo.bestellingLijst.remove(GuiPojo.bestelling.getBestellingId());
			bestellingListView.getItems().clear();

			populateBestellingListView(bestellingListView);

			GuiPojo.bestelDAO.setEnkeleBestellingInactief(GuiPojo.bestelling.getBestellingId());
		}catch (GeneriekeFoutmelding e){
			e.printStackTrace();
			DeLogger.getLogger().error("Fout bij verwijderen bestelling {}", e.getMessage(), e.getStackTrace());
			GuiPojo.errorBox.setMessageAndStart("Fout bij verwijderen bestelling " + e.getMessage());
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
			e.printStackTrace();
			DeLogger.getLogger().error("Fout bij ophalen alle adressen van klant {}", e.getMessage(), e.getStackTrace());
			GuiPojo.errorBox.setMessageAndStart(String.format("Fout bij ophalen alle adressen van klant %d", GuiPojo.klant.getKlantId()));
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

	public void maakAdresGui(long klantId) {
		try {
			AdresGui adresGui = new AdresGui();
			LinkedHashSet<Adres> adresLinkedHashSet = new LinkedHashSet<>();
			ListIterator<Adres> adresListIterator = GuiPojo.adresDAO.getAdresOpKlantID(klantId);

			while (adresListIterator.hasNext()) { //TODO: Wat omweg van iterator naar set. Maar even snel werk
				Adres adres = adresListIterator.next();
				adresLinkedHashSet.add(adres);
			}

			adresGui.setAndRun(adresLinkedHashSet);

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
				DeLogger.getLogger().error("Fout bij maken nieuwe bestelling {}", e.getMessage(), e.getStackTrace());
				GuiPojo.errorBox.setMessageAndStart(String.format("Fout bij maken nieuwe bestelling", GuiPojo.klant.getKlantId()));
			}
		}else{
			GuiPojo.errorBox.setMessageAndStart("Selecteer eerst een klant");
		}
	}

	public void updateKlant() {
		try {
			if(GuiPojo.klant.getKlantId() == 0)
				GuiPojo.errorBox.setMessageAndStart("Selecteer eerst een klant");
			else{
				KlantGui nieuweKlant = new KlantGui();
				nieuweKlant.start(new Stage());
			}
		}catch(GeneriekeFoutmelding e){
			e.printStackTrace();
			DeLogger.getLogger().error("Fout bij updaten klant {}", e.getMessage(), e.getStackTrace());
			GuiPojo.errorBox.setMessageAndStart(String.format("Fout bij updaten klant %d", GuiPojo.klant.getKlantId()));
		}catch(NullPointerException e){
			e.printStackTrace();
			DeLogger.getLogger().error("Fout bij updaten klant {}", e.getMessage(), e.getStackTrace());
			GuiPojo.errorBox.setMessageAndStart(String.format("Fout bij updaten klant %d", GuiPojo.klant.getKlantId()));
		}catch (Exception e) {
			e.printStackTrace();
			DeLogger.getLogger().error("Fout bij updaten klant {}", e.getMessage(), e.getStackTrace());
			GuiPojo.errorBox.setMessageAndStart(String.format("Fout bij updaten klant %d", GuiPojo.klant.getKlantId()));
		}
	}

	public void nieuweKlant() {
		try {
			new KlantGui().start(new Stage());
		} catch (Exception e) {
			e.printStackTrace();
			DeLogger.getLogger().error("Fout bij nieuweKlant GUI {}", e.getMessage(), e.getStackTrace());
			GuiPojo.errorBox.setMessageAndStart(String.format("Fout bij nieuweKlant GUI"));
		}
	}

	public void zoekViaBestellingKlant() {
		Klant klant = new Klant();
		klant.setKlantId(GuiPojo.bestelling.getKlantId());
		try {
			GuiPojo.klant = GuiPojo.klantDAO.getKlantOpKlant(klant).get(0);
		} catch (GeneriekeFoutmelding e) {
			DeLogger.getLogger().error("kan de klant niet vinden aan de hand van klantId {}", GuiPojo.bestelling.getKlantId(), e.getStackTrace());
			GuiPojo.errorBox.setMessageAndStart("kan de klant niet vinden aan de hand van klantId " + GuiPojo.bestelling.getKlantId());
			e.printStackTrace();
		}
	}
}