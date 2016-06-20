package mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import exceptions.RSVIERException;
import gui.ErrorBox;
import interfaces.ArtikelDAO;
import javafx.scene.control.ListView;
import model.Adres;
import model.Artikel;
import model.Bestelling;
import model.GuiPojo;
import model.Klant;

public class GuiBewerkingenMySQL extends AbstractGuiBewerkingen{
	BestellingDAOMySQL bestelDAO = new BestellingDAOMySQL();
	ArtikelDAO artikelDAO = new ArtikelDAOMySQL();
	ErrorBox errorBox = new ErrorBox();

	public void resetArtikelVariabelen(){
		GuiPojo.artikelLijst.clear();
	}

	public void zoekKlant(ListView<String> klantListView, String klantId, String voorNaam, String achterNaam, String tussenVoegsel, String email){
		klantListView.getItems().clear();

		try (Connection con = MySQLConnectieLeverancier.getConnection();
			PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM `KLANT` WHERE klant_id LIKE ? AND voornaam LIKE ? AND achternaam LIKE ? "
				+ "AND tussenvoegsel LIKE ? AND email LIKE ?");){

			preparedStatement.setString(1, klantId.equals("") ? "%" : klantId);
			preparedStatement.setString(2, voorNaam.equals("") |  voorNaam == null? "%" : voorNaam);
			preparedStatement.setString(3, achterNaam.equals("") ? "%" : achterNaam);
			preparedStatement.setString(4, tussenVoegsel.equals("") ? "%" : tussenVoegsel);
			preparedStatement.setString(5, email.equals("") ? "%" : email);

			try(ResultSet rs = preparedStatement.executeQuery();){
				verwerkKlantResultSet(rs, klantListView);
			}
		} catch (NumberFormatException | SQLException | RSVIERException e) {
			errorBox.setMessageAndStart(e.getMessage());
		}
	}

	public void zoekBestelling(String bron, ListView<Long> bestellingListView, String klantIdField, String bestellingIdField){
		try {
			Iterator<Bestelling> it = bron.equals("klantId") ? bestelDAO.getBestellingOpKlantId(Long.parseLong(klantIdField)) : bestelDAO.getBestellingOpBestellingId(Long.parseLong(bestellingIdField));
			populateBestellingListView(bestellingListView, it);
		}catch(NumberFormatException | RSVIERException | NullPointerException | SQLException e){
			errorBox.setMessageAndStart(e.getMessage());
		}
	}

	public void verwerkKlantResultSet(ResultSet rs, ListView<String> klantListView) throws RSVIERException{
		Klant klant;
		String gegevens;
		try {
			while(rs.next()){
				Adres adres = new Adres();
				adres.setStraatnaam(rs.getString("straatnaam"));
				adres.setPostcode(rs.getString("postcode"));
				adres.setToevoeging(rs.getString("toevoeging"));
				adres.setHuisnummer(rs.getInt("huisnummer"));
				adres.setWoonplaats(rs.getString("woonplaats"));
				klant = new Klant(rs.getLong("klant_id"), rs.getString("voornaam"), rs.getString("achternaam"),
						rs.getString("tussenvoegsel"), rs.getString("email"), adres);
				gegevens = klant.getTussenvoegsel().equals("") ? klant.getKlant_id() + ": " + klant.getVoornaam() + " " + klant.getAchternaam() : klant.getKlant_id() + ": " + klant.getVoornaam() + " " + klant.getTussenvoegsel() + " " + klant.getAchternaam();
				if(!klantListView.getItems().contains(gegevens))
					klantListView.getItems().add(gegevens);
				GuiPojo.klantenLijst.put(klant.getKlant_id(), klant);
			}
		} catch (SQLException e) {
			throw new RSVIERException("Fout in verwerkKlantresultSet" + e.getMessage());
		}
	}

	public void updateArtikel(Artikel nieuwArtikel){
		setArtikelLijst();
		try {
			artikelDAO.updateArtikelen(nieuwArtikel);
			GuiPojo.artikel.setArtikel_naam(nieuwArtikel.getArtikel_naam());
			GuiPojo.artikel.setArtikel_prijs(nieuwArtikel.getArtikel_prijs());
			setArtikelLijst();
		} catch (NumberFormatException | RSVIERException e) {
			errorBox.setMessageAndStart(e.getMessage());
		}
	}

	public void updateBestelling(){
		try {
			bestelDAO.updateBestelling(GuiPojo.bestelling);
		} catch (SQLException | RSVIERException e) {
			errorBox.setMessageAndStart(e.getMessage());
		}
	}

	public void verwijderEnkeleBestelling(){
		try {
			bestelDAO.verwijderEnkeleBestelling(GuiPojo.bestelling.getBestelling_id());
		} catch (RSVIERException | SQLException e) {
			errorBox.setMessageAndStart(e.getMessage());
		}
		GuiPojo.bestelling = new Bestelling();
	}
}
