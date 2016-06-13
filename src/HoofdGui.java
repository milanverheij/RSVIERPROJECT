import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import exceptions.RSVIERException;
import interfaces.ArtikelDAO;
import interfaces.BestellingDAO;
import interfaces.KlantDAO;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Artikel;
import model.Bestelling;
import model.Klant;
import mysql.MySQLConnectie;
import mysql.MySQLHelper;

public class HoofdGui extends Application{
	private final Insets INSET = new Insets(4);

	DAOFactory factory = DAOFactory.getDAOFactory("MySQL");
	BestellingDAO bestelDAO = factory.getBestellingDAO();
	KlantDAO klantDAO = factory.getKlantDAO();
	ArtikelDAO artikelDAO = factory.getArtikelDAO();

	TextField klantIdField;
	TextField voorNaamField;
	TextField achterNaamField;
	TextField tussenVoegselField;
	TextField emailField;
	LinkedHashMap<Long, Klant> klantenLijst = new LinkedHashMap<Long, Klant>();
	ListView<String> klantListView;

	TextField bestellingIdField;
	LinkedHashMap<Long, Bestelling> bestellingLijst;
	ListView<Long> bestellingListView;

	TextField artikelIdField;
	TextField artikelNaamField;
	TextField artikelPrijsField;
	TextField artikelAantalField;
	LinkedHashMap<Artikel, Integer> artikelLijst;
	ListView<String> artikelListView;

	public static void main(String[] args){
		launch();
	}

	@Override
	public void start(Stage stage) throws Exception {
		klantListView = new ListView<String>();
		klantListView.setOnMouseClicked(e -> getItemVanKlantenLijst());

		bestellingListView = new ListView<Long>();
		bestellingListView.setOnMouseClicked(e -> getItemVanBestellingLijst());

		artikelListView = new ListView<String>();
		artikelListView.setOnMouseClicked(e -> getItemVanArtikelLijst());

		klantIdField = new TextField();
		voorNaamField = new TextField();
		achterNaamField = new TextField();
		tussenVoegselField = new TextField();
		emailField = new TextField();

		artikelIdField = new TextField();
		artikelNaamField = new TextField();
		artikelPrijsField = new TextField();
		artikelAantalField = new TextField();
		artikelIdField.setEditable(false);

		bestellingIdField = new TextField();

		Button zoekKlantButton = new Button("Zoeken");
		Button leegButton = new Button("Wissen");
		Button updateButton = new Button("Update");

		zoekKlantButton.setOnAction(e -> klantKnopKlik());
		leegButton.setOnAction(E -> leegAlles());
		updateButton.setOnAction(e -> updateAlles());

		GridPane zoekGrid = new GridPane();
		zoekGrid.add(new Label("Zoeken op"), 0, 0);
		zoekGrid.add(new Label("Klant ID"), 0, 1);
		zoekGrid.add(new Label("Voornaam"), 0, 2);
		zoekGrid.add(new Label("Tussenvoegsel"), 0, 3);
		zoekGrid.add(new Label("Achternaam"), 0, 4);
		zoekGrid.add(new Label("E-mail"), 0, 5);
		zoekGrid.add(new Label("Bestelling Id"), 0, 6);
		zoekGrid.add(new Label(" "), 0, 7);
		zoekGrid.add(new Label("Artikel details"), 0, 8);
		zoekGrid.add(new Label("Naam"), 0, 9);
		zoekGrid.add(new Label("Artikel id"), 0, 12);
		zoekGrid.add(new Label("Aantal"), 0, 11);
		zoekGrid.add(new Label("Prijs"), 0, 10);

		zoekGrid.add(klantIdField, 1, 1);
		zoekGrid.add(voorNaamField, 1, 2);
		zoekGrid.add(tussenVoegselField, 1, 3);
		zoekGrid.add(achterNaamField, 1, 4);
		zoekGrid.add(emailField, 1, 5);
		zoekGrid.add(bestellingIdField, 1, 6);
		zoekGrid.add(artikelNaamField, 1, 9);
		zoekGrid.add(artikelPrijsField, 1, 10);
		zoekGrid.add(artikelAantalField, 1, 11);
		zoekGrid.add(artikelIdField, 1, 12);

		zoekGrid.setHgap(5);
		zoekGrid.setVgap(2);	

		GridPane displayGrid = new GridPane();
		displayGrid.add(new Label("Klanten lijst"), 0, 0);
		displayGrid.add(new Label("Bestellingen lijst"), 1, 0);
		displayGrid.add(new Label("Artikel lijst"), 2, 0);
		displayGrid.add(klantListView, 0, 1);
		displayGrid.add(bestellingListView, 1, 1);
		displayGrid.add(artikelListView, 2, 1);

		displayGrid.setHgap(5);
		displayGrid.setVgap(2);

		HBox gridBox = new HBox();
		gridBox.getChildren().add(zoekGrid);
		gridBox.getChildren().add(displayGrid);
		gridBox.setSpacing(5);

		HBox knoppenBox = new HBox();
		knoppenBox.getChildren().addAll(zoekKlantButton, leegButton, updateButton);

		VBox verticalBox = new VBox();
		verticalBox.getChildren().addAll(gridBox, knoppenBox);
		verticalBox.setSpacing(2);
		verticalBox.setPadding(INSET);

		Scene scene = new Scene(verticalBox);
		stage.setScene(scene);
		stage.show();
	}

	private void klantKnopKlik(){
		klantListView.getItems().clear();
		bestellingListView.getItems().clear();
		artikelListView.getItems().clear();
		if(bestellingIdField.getText().equals(""))
			zoekKlant();
		else
			zoekBestelling();
	}

	private void zoekKlant(){
		PreparedStatement preparedStatement = null;
		Connection con = null;
		ResultSet rs = null;
		try {

			String sql = "SELECT * FROM `KLANT` WHERE klant_id LIKE ? AND voornaam LIKE ? AND achternaam LIKE ? "
					+ "AND tussenvoegsel LIKE ? AND email LIKE ?";
			con = MySQLConnectie.getConnection();
			preparedStatement = con.prepareStatement(sql);

			preparedStatement.setString(1, klantIdField.getText().equals("") ? "%" : klantIdField.getText());
			preparedStatement.setString(2, voorNaamField.getText().equals("") ? "%" : voorNaamField.getText());
			preparedStatement.setString(3, achterNaamField.getText().equals("") ? "%" : achterNaamField.getText());
			preparedStatement.setString(4, tussenVoegselField.getText().equals("") ? "%" : tussenVoegselField.getText());
			preparedStatement.setString(5, emailField.getText().equals("") ? "%" : emailField.getText());
			rs = preparedStatement.executeQuery();

			verwerkKlantResultSet(rs);

		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (RSVIERException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			MySQLHelper.close(con, preparedStatement, rs);
		}
	}

	private void zoekBestelling(){
		String bron = bestellingIdField.getText().equals("") ? "klantId" : "bestellingId";
		try {
			Iterator<Bestelling> it = bron.equals("klantId") ? bestelDAO.getBestellingOpKlantGegevens(Long.parseLong(klantIdField.getText())) : bestelDAO.getBestellingOpBestelling(Long.parseLong(bestellingIdField.getText()));
			bestellingLijst = new LinkedHashMap<Long, Bestelling>();
			while(it.hasNext()){
				Bestelling bestelling = it.next();
				if(!bestellingListView.getItems().contains(bestelling.getBestelling_id())){
					bestellingListView.getItems().add(bestelling.getBestelling_id());
					bestellingLijst.put(bestelling.getBestelling_id(), bestelling);
				}
			}			
		}catch(NumberFormatException | RSVIERException | NullPointerException e){
			//e.printStackTrace();
		}
	}

	private void verwerkKlantResultSet(ResultSet rs) throws SQLException, RSVIERException{
		Klant klant;
		String gegevens;
		while(rs.next()){
			klant = new Klant(rs.getLong("klant_id"), rs.getString("voornaam"), rs.getString("achternaam"),
					rs.getString("tussenvoegsel"), rs.getString("email"), null);
			gegevens = klant.getTussenvoegsel().equals("") ? klant.getKlant_id() + ": " + klant.getVoornaam() + " " + klant.getAchternaam() : klant.getKlant_id() + ": " + klant.getVoornaam() + " " + klant.getTussenvoegsel() + " " + klant.getAchternaam();
			if(!klantListView.getItems().contains(gegevens))
				klantListView.getItems().add(gegevens);
			klantenLijst.put(klant.getKlant_id(), klant);
		}
	}

	private void leegVelden(){
		klantIdField.setText("");
		voorNaamField.setText("");
		achterNaamField.setText("");
		tussenVoegselField.setText("");
		emailField.setText("");
		bestellingIdField.setText("");

		artikelIdField.setText("");
		artikelNaamField.setText("");
		artikelPrijsField.setText("");
		artikelAantalField.setText("");
	}

	private void leegViews(){
		bestellingListView.getItems().clear();
		artikelListView.getItems().clear();;
		klantListView.getItems().clear();
	}

	private void leegAlles(){
		leegViews();
		leegVelden();
	}

	private void getItemVanKlantenLijst(){
		leegVelden();
		bestellingListView.getItems().clear();
		artikelListView.getItems().clear();
		try{
			Klant geselecteerd = klantenLijst.get(Long.parseLong(klantListView.getSelectionModel().getSelectedItem().split(":")[0]));
			klantIdField.setText("" + geselecteerd.getKlant_id());
			voorNaamField.setText(geselecteerd.getVoornaam());
			achterNaamField.setText(geselecteerd.getAchternaam());
			tussenVoegselField.setText(geselecteerd.getTussenvoegsel());
			emailField.setText(geselecteerd.getEmail());
		}catch(NullPointerException e){}
		zoekBestelling();
	}

	private void getItemVanBestellingLijst(){
		artikelListView.getItems().clear();
		try{
			Bestelling geselecteerd = bestellingLijst.get(bestellingListView.getSelectionModel().getSelectedItem());
			klantIdField.setText("" + geselecteerd.getKlant_id());
			bestellingIdField.setText("" + geselecteerd.getBestelling_id());
			Set<Artikel> set = geselecteerd.getArtikelLijst().keySet();
			for(Artikel artikel : set){
				artikelListView.getItems().add("Naam: " + artikel.getArtikel_naam() + "\nPrijs: " + artikel.getArtikel_prijs() + 
						"\nAantal: " + geselecteerd.getArtikelLijst().get(artikel));
			}
			artikelLijst = geselecteerd.getArtikelLijst();
			zoekKlant();
		}catch(NullPointerException e){}
	}

	private void getItemVanArtikelLijst(){
		Artikel artikel;
		try{
			Iterator<Artikel> it = artikelLijst.keySet().iterator();
			String geselecteerdArtikel = artikelListView.getSelectionModel().getSelectedItem().split("[\\r?\\n]")[0];
			String[] cleaned = geselecteerdArtikel.split(":", 2);
			if(cleaned.length > 1)  geselecteerdArtikel = cleaned[1].trim();
			else 					geselecteerdArtikel = cleaned[0].trim();
			while(it.hasNext())
				if((artikel = it.next()).getArtikel_naam().equals(geselecteerdArtikel)){
					setArtikelFields(artikel);
					break;
				}
		}catch(Exception e){ 
			e.printStackTrace();
		}
	}

	private void setArtikelFields(Artikel artikel){
		artikelNaamField.setText(artikel.getArtikel_naam());
		artikelPrijsField.setText("" + artikel.getArtikel_prijs());
		artikelIdField.setText("" + artikel.getArtikel_id());
		artikelAantalField.setText("" + artikelLijst.get(artikel));
	}

	private void updateAlles(){
		Bestelling bestelling = new Bestelling();
		Artikel artikel = new Artikel();
		try {
			if(!artikelIdField.getText().isEmpty()){
				artikelDAO.updateArtikelOpBestelling(Integer.parseInt(bestellingIdField.getText()), 
						Integer.parseInt(artikelIdField.getText()), artikel);
			}

			if(!bestellingIdField.getText().isEmpty()){
				bestelling.setBestelling_id(Long.parseLong(bestellingIdField.getText()));
				bestelling.setKlant_id(Long.parseLong(klantIdField.getText()));
				bestelling.voegArtikelToe(artikel);
			}

			if(!klantIdField.getText().isEmpty()){
				klantDAO.updateKlant(Long.parseLong(klantIdField.getText()), 
						voorNaamField.getText(), 
						achterNaamField.getText(), 
						tussenVoegselField.getText(), 
						emailField.getText());
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (RSVIERException e) {
			e.printStackTrace();
		}
	}
}
