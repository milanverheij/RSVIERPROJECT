package mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import exceptions.RSVIERException;
import gui.ErrorBox;
import interfaces.ArtikelDAO;
import javafx.scene.control.ListView;
import model.Adres;
import model.Artikel;
import model.Bestelling;
import model.GuiPojo;
import model.Klant;

public class GuiMySQLBewerkingen {
	BestellingDAOMySQL bestelDAO = new BestellingDAOMySQL();
	ArtikelDAO artikelDAO = new ArtikelDAOMySQL();
	ErrorBox errorBox = new ErrorBox();



	public static void leegKlantBestellingArtikel(){
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
	}

	public void zoekKlant(ListView<String> klantListView, String klantId, String voorNaam, String achterNaam, String tussenVoegsel, String email){
		klantListView.getItems().clear();

		PreparedStatement preparedStatement = null;
		Connection con = null;
		ResultSet rs = null;

		try {

			String sql = "SELECT * FROM `KLANT` WHERE klant_id LIKE ? AND voornaam LIKE ? AND achternaam LIKE ? "
					+ "AND tussenvoegsel LIKE ? AND email LIKE ?";
			con = MySQLConnectieLeverancier.getConnection();
			preparedStatement = con.prepareStatement(sql);

			preparedStatement.setString(1, klantId.equals("") ? "%" : klantId);
			preparedStatement.setString(2, voorNaam.equals("") |  voorNaam == null? "%" : voorNaam);
			preparedStatement.setString(3, achterNaam.equals("") ? "%" : achterNaam);
			preparedStatement.setString(4, tussenVoegsel.equals("") ? "%" : tussenVoegsel);
			preparedStatement.setString(5, email.equals("") ? "%" : email);
			rs = preparedStatement.executeQuery();

			verwerkKlantResultSet(rs, klantListView);

		} catch (NumberFormatException e) {
			errorBox.setMessageAndStart(e.getMessage());
		} catch (RSVIERException e) {
			errorBox.setMessageAndStart(e.getMessage());
		} catch (SQLException e) {
			errorBox.setMessageAndStart(e.getMessage());
		}finally{
			MySQLHelper.close(con, preparedStatement, rs);
		}
	}

	public void zoekBestelling(String bron, ListView<Long> bestellingListView, String klantIdField, String bestellingIdField){
		try { 
			Iterator<Bestelling> it = bron.equals("klantId") ? bestelDAO.getBestellingOpKlantGegevens(Long.parseLong(klantIdField)) : bestelDAO.getBestellingOpBestelling(Long.parseLong(bestellingIdField));
			populateBestellingListView(bestellingListView, it);
		}catch(NumberFormatException | RSVIERException | NullPointerException e){		}
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
	
	public void verwerkKlantResultSet(ResultSet rs, ListView<String> klantListView) throws SQLException, RSVIERException{
		Klant klant;
		String gegevens;
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
	}

	public void getItemVanKlantenLijst(ListView<String> klantListView){
		String selectedItem = klantListView.getSelectionModel().getSelectedItem();
		if(selectedItem != null){
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
		LinkedHashMap<Artikel, Integer> map = GuiPojo.bestelling.getArtikelLijst();
		GuiPojo.bestelling = new Bestelling();
		GuiPojo.bestelling.setBestelling_id(bestellingId);
		GuiPojo.bestelling.setKlant_id(klantId);
		for(Artikel a : GuiPojo.artikelLijst)
			for(int x = 0; x < map.get(a); x++)
				GuiPojo.bestelling.voegArtikelToe(a);
	}

	public void setArtikelLijst(){
		GuiPojo.artikelLijst.clear();
		Set<Artikel> lijst = GuiPojo.bestelling.getArtikelLijst().keySet();
		for(Artikel artikel : lijst)
			GuiPojo.artikelLijst.add(artikel);
	}

	public void updateArtikel(Artikel nieuwArtikel){
		setArtikelLijst();
		try {
			artikelDAO.updateArtikelen(nieuwArtikel);
			GuiPojo.artikel.setArtikel_naam(nieuwArtikel.getArtikel_naam());
			GuiPojo.artikel.setArtikel_prijs(nieuwArtikel.getArtikel_prijs());
			setArtikelLijst();
		} catch (NumberFormatException e) {
			errorBox.setMessageAndStart(e.getMessage());
		} catch (RSVIERException e) {
			errorBox.setMessageAndStart(e.getMessage());
		}
	}
	
	public void getItemVanArtikelLijst(int index){
		GuiPojo.artikel = GuiPojo.artikelLijst.get(index);
	}

	public void updateBestelling() throws SQLException, RSVIERException{
		bestelDAO.updateBestelling(GuiPojo.bestelling);
	}

	public void verwijderEnkeleBestelling() throws RSVIERException{
		bestelDAO.verwijderEnkeleBestelling(GuiPojo.bestelling.getBestelling_id());
		GuiPojo.bestelling = new Bestelling();
	}
}
