package mysql;

import java.util.Iterator;
import exceptions.GeneriekeFoutmelding;
import factories.DAOFactory;
import javafx.scene.control.ListView;
import logger.DeLogger;
import model.Adres;
import model.Artikel;
import model.Bestelling;
import model.GuiPojo;
import model.Klant;

public class GuiBewerkingenMySQL extends AbstractGuiBewerkingen{

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
				gegevens = klant.getTussenvoegsel().equals("") ? klant.getKlant_id() + ": " + klant.getVoornaam() + " " + klant.getAchternaam() : klant.getKlant_id() + ": " + klant.getVoornaam() + " " + klant.getTussenvoegsel() + " " + klant.getAchternaam();
				if(!klantListView.getItems().contains(gegevens))
					klantListView.getItems().add(gegevens);
				GuiPojo.klantenLijst.put(klant.getKlant_id(), klant);
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
			GuiPojo.bestelDAO.updateBestelling(GuiPojo.bestelling);
		}catch (GeneriekeFoutmelding e){
			errorBox.setMessageAndStart(e.getMessage());
		}
	}

	public void verwijderEnkeleBestelling(){
		try{
			GuiPojo.bestelDAO.verwijderEnkeleBestelling(GuiPojo.bestelling.getBestelling_id());
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
			Iterator<Adres> adresIt = GuiPojo.adresDAO.getAdresOpKlantID(GuiPojo.klant.getKlant_id());
			while(adresIt.hasNext()){
				Adres adres = adresIt.next();

				System.out.print("["+adres.getAdresActief() + "]");
				if(adres.getAdresActief().trim().equals("1")){ //TODO ook optie voor als adres niet actief is
					System.out.println("IN ACTIEF BLOK");
					GuiPojo.klant.setAdresGegevens(adres);
					break;
				}
			}
		}catch (GeneriekeFoutmelding e){
			errorBox.setMessageAndStart(String.format("Fout bij ophalen alle adressen van klant %d", GuiPojo.klant.getKlant_id()));
			e.printStackTrace();
		}
	}
}