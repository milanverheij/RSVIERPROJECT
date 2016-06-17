package gui;
import exceptions.RSVIERException;

import factories.DAOFactory;
import interfaces.AdresDAO;
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
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import model.Artikel;
import model.GuiPojo;
import mysql.GuiMySQLBewerkingen;

public class HoofdGui extends Application{
	private final Insets INSET = new Insets(4);

	ErrorBox errorBox = new ErrorBox();

	GuiMySQLBewerkingen guiMySQL = new GuiMySQLBewerkingen();
	DAOFactory factory = DAOFactory.getDAOFactory("MySQL");

	BestellingDAO bestelDAO = factory.getBestellingDAO();
	KlantDAO klantDAO = factory.getKlantDAO();
	AdresDAO adresDAO = factory.getAdresDAO();
	ArtikelDAO artikelDAO = factory.getArtikelDAO();

	TextField klantIdField;
	TextField voorNaamField;
	TextField achterNaamField;
	TextField tussenVoegselField;
	TextField emailField;
	ListView<String> klantListView;

	TextField bestellingIdField;
	ListView<Long> bestellingListView;

	TextField artikelIdField;
	TextField artikelNaamField;
	TextField artikelPrijsField;
	ListView<String> artikelListView;

	Label errorLabel = new Label();

	GridPane zoekGrid;
	GridPane displayGrid;

	Button zoekKlantButton;
	Button leegButton;

	Button updateBestellingButton;
	Button nieuweBestellingButton;
	Button verwijderBestelling;

	Button updateArtikelButton;

	Button nieuweKlantButton;
	Button updateKlantButton;

	public static void main(String[] args){
		launch();
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		maakListViewsAan();
		maakTextFieldsAan();
		maakButtonsEnSetOnAction();
		populateZoekGrid();
		populateDisplayGrid();

		HBox gridBox = new HBox();
		gridBox.getChildren().add(zoekGrid);
		gridBox.getChildren().add(displayGrid);
		gridBox.setSpacing(5);

		HBox knoppenBox = new HBox();
		knoppenBox.getChildren().addAll(zoekKlantButton, 
				new Label("  "), leegButton, 
				new Label("  "), updateArtikelButton, 
				new Label("  "), nieuweBestellingButton, updateBestellingButton, verwijderBestelling,
				new Label("  "), nieuweKlantButton, updateKlantButton);
		knoppenBox.setSpacing(5);

		VBox verticalBox = new VBox();
		verticalBox.getChildren().addAll(gridBox, knoppenBox);
		verticalBox.setSpacing(2);
		verticalBox.setPadding(INSET);

		Scene scene = new Scene(verticalBox);
		stage.setScene(scene);
		stage.setTitle("Exotische Dieren Emporium");
		stage.getIcons().add(new Image("/images/icon.jpg"));
		stage.setTitle("Harrie's Tweedehands Beessies");
		stage.show();
	}

	private void maakListViewsAan(){
		klantListView = new ListView<String>();
		klantListView.setOnMouseClicked(e -> getItemVanKlantenLijst());

		bestellingListView = new ListView<Long>();
		bestellingListView.setOnMouseClicked(e -> getItemVanBestellingLijst());

		artikelListView = new ListView<String>();
		artikelListView.setOnMouseClicked(e -> getItemVanArtikelLijst());
	}

	private void maakTextFieldsAan(){
		klantIdField = new TextField();
		voorNaamField = new TextField();
		achterNaamField = new TextField();
		tussenVoegselField = new TextField();
		emailField = new TextField();

		artikelIdField = new TextField();
		artikelNaamField = new TextField();
		artikelPrijsField = new TextField();
		artikelIdField.setEditable(false);

		bestellingIdField = new TextField();
	}

	private void maakButtonsEnSetOnAction() throws RSVIERException, Exception{
		zoekKlantButton = new Button("Zoeken");
		leegButton = new Button("Leeg velden");

		updateBestellingButton = new Button("Update bestelling");
		nieuweBestellingButton = new Button("Nieuwe bestelling");
		verwijderBestelling = new Button("Verwijder bestelling");
		updateArtikelButton = new Button("Update Artikel");

		nieuweKlantButton = new Button("Nieuwe klant");
		updateKlantButton = new Button("Update klant");

		zoekKlantButton.setOnAction(e -> zoekKlantKnopKlik());
		leegButton.setOnAction(E -> leegAlles());

		updateBestellingButton.setOnAction(e -> updateBestelling());
		nieuweBestellingButton.setOnAction(e -> nieuweBestelling());
		verwijderBestelling.setOnAction(e -> verwijderBestelling());
		updateArtikelButton.setOnAction(e -> updateArtikel());

		nieuweKlantButton.setOnAction(e -> nieuweKlant());
		updateKlantButton.setOnAction(e -> updateKlant());		
	}

	/* Verwijdert een enkele bestelling uit de database */
	private void verwijderBestelling(){
		try {
			GuiPojo.bestellingLijst.remove(GuiPojo.bestelling.getBestelling_id());
			guiMySQL.verwijderEnkeleBestelling();
			bestellingListView.getItems().clear();
			guiMySQL.populateBestellingListView(bestellingListView);
		} catch (RSVIERException e) {
			errorBox.setMessageAndStart(e.getMessage());
		}
	}

	//zoekGrid bevat alle velden met info waarop gezocht kan worden
	private void populateZoekGrid(){
		zoekGrid = new GridPane();
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
		zoekGrid.add(new Label("Prijs"), 0, 10);
		zoekGrid.add(new Label("Artikel id"), 0, 11);

		zoekGrid.add(klantIdField, 1, 1);
		zoekGrid.add(voorNaamField, 1, 2);
		zoekGrid.add(tussenVoegselField, 1, 3);
		zoekGrid.add(achterNaamField, 1, 4);
		zoekGrid.add(emailField, 1, 5);
		zoekGrid.add(bestellingIdField, 1, 6);
		zoekGrid.add(artikelNaamField, 1, 9);
		zoekGrid.add(artikelPrijsField, 1, 10);
		zoekGrid.add(artikelIdField, 1, 11);

		zoekGrid.setHgap(5);
		zoekGrid.setVgap(2);
	}

	//displayGrid bevat de ListViews voor de klantenlijst, de bestellinglijst, en de artikellijst
	private void populateDisplayGrid(){
		displayGrid = new GridPane();
		displayGrid.add(new Label("Klanten lijst"), 0, 0);
		displayGrid.add(new Label("Bestellingen lijst"), 1, 0);
		displayGrid.add(new Label("Artikel lijst"), 2, 0);
		displayGrid.add(klantListView, 0, 1);
		displayGrid.add(bestellingListView, 1, 1);
		displayGrid.add(artikelListView, 2, 1);

		displayGrid.setHgap(5);
		displayGrid.setVgap(2);
	}

	//Zoekt alle klanten op aan de hand van de ingevulde klantgegevens
	private void zoekKlantKnopKlik(){
		leegViews();

		if(bestellingIdField.getText().equals(""))
			guiMySQL.zoekKlant(klantListView, klantIdField.getText(), voorNaamField.getText(), 
					achterNaamField.getText(), tussenVoegselField.getText(), emailField.getText());
		else
			zoekBestelling();
	}

	/* Zoekt bestellingen op de waarde van het klantIdField of op bestellingIdField
	 * en plaatst deze op de bestellingListView
	 * Zoekt op bestellingId als die ingevuld is want meest specifiek
	 */
	private void zoekBestelling(){
		String bron = bestellingIdField.getText().equals("") ? "klantId" : "bestellingId";
		guiMySQL.zoekBestelling(bron, bestellingListView, klantIdField.getText(), bestellingIdField.getText());
	}

	/* Leegt de TextField van klantgegevens */
	private void leegKlantVelden(){
		klantIdField.setText("");
		voorNaamField.setText("");
		achterNaamField.setText("");
		tussenVoegselField.setText("");
		emailField.setText("");
		bestellingIdField.setText("");
	}

	/* Leegt de TextField van artikelgegevens */
	private void leegArtikelVelden(){
		artikelIdField.setText("");
		artikelNaamField.setText("");
		artikelPrijsField.setText("");
	}

	//Leegt alle ListViews en reset de variabelen waarmee ze opgebouwd 
	//worden en selecties mee bijgehouden worden
	private void leegViews(){
		bestellingListView.getItems().clear();
		artikelListView.getItems().clear();
		klantListView.getItems().clear();
		guiMySQL.leegKlantBestellingArtikel();
	}

	//Leegt alle TextFields in de HoofdGui
	private void leegAlleVelden(){
		leegArtikelVelden();
		leegKlantVelden();
	}

	//Haalt alle TextFields en ListViews leeg
	private void leegAlles(){
		leegViews();
		leegAlleVelden();
	}

	/* Loopt wanneer er op de klantenlijst
	 * op een item geklikt is
	 */
	private void getItemVanKlantenLijst(){
		guiMySQL.getItemVanKlantenLijst(klantListView);

		if(GuiPojo.klant != null){
			leegAlleVelden();
			bestellingListView.getItems().clear();
			artikelListView.getItems().clear();

			if(GuiPojo.klant.getKlant_id() != 0){
				klantIdField.setText("" + GuiPojo.klant.getKlant_id());
				voorNaamField.setText(GuiPojo.klant.getVoornaam());
				achterNaamField.setText(GuiPojo.klant.getAchternaam());
				tussenVoegselField.setText(GuiPojo.klant.getTussenvoegsel());
				emailField.setText(GuiPojo.klant.getEmail());

				zoekBestelling();
			}
		}
	}

	/* Loopt wanneer er op de bestellinglijst op een item geklikt is
	 * Haalt de bestelling uit de database en roept dan setArtikelListView aan
	 * om de artikelen weer te geven
	 */
	private void getItemVanBestellingLijst(){
		if(bestellingListView.getSelectionModel().getSelectedItem() != null){
			long selectedItem = bestellingListView.getSelectionModel().getSelectedItem();
			if(selectedItem >= 0){
				guiMySQL.getItemVanBestellingLijst(selectedItem);
				leegArtikelVelden();
			}
			setArtikelListView();
		}
	}
	
	private void setArtikelListView(){
		if(GuiPojo.artikel != null){
			artikelListView.getItems().clear();

			if(GuiPojo.bestelling.getKlant_id() != 0)
				klantIdField.setText("" + GuiPojo.bestelling.getKlant_id());
			if(GuiPojo.bestelling.getBestelling_id() != 0)
				bestellingIdField.setText("" + GuiPojo.bestelling.getBestelling_id());

			guiMySQL.setArtikelLijst();

			for(Artikel artikel : GuiPojo.artikelLijst){
				artikelListView.getItems().add("Naam: " + artikel.getArtikel_naam() + "\nPrijs: " + artikel.getArtikel_prijs() + 
						"\nAantal: " + GuiPojo.bestelling.getArtikelLijst().get(artikel));
			}
			if(klantIdField.getText().isEmpty())
				guiMySQL.zoekKlant(klantListView, klantIdField.getText(), voorNaamField.getText(), 
						achterNaamField.getText(), tussenVoegselField.getText(), emailField.getText());
		}
	}

	/* Loopt wanneer er op de artikellijst
	 * op een item geklikt is
	 */
	private void getItemVanArtikelLijst(){
		int index = artikelListView.getSelectionModel().getSelectedIndex();

		if(index >= 0){
			guiMySQL.getItemVanArtikelLijst(index);

			if(GuiPojo.artikel != null)	
				setArtikelFields(GuiPojo.artikel);
		}
	}

	//Set de waarden van een Artikel-object in de relevante TextField
	private void setArtikelFields(Artikel artikel){
		artikelNaamField.setText(artikel.getArtikel_naam());
		artikelPrijsField.setText("" + artikel.getArtikel_prijs());
		artikelIdField.setText("" + artikel.getArtikel_id());
	}

	//Update een artikel in de database aan de hand van de info
	//in de artikel TextFields
	private void updateArtikel(){
		if(!artikelIdField.getText().isEmpty()){
			Artikel nieuwArtikel = new Artikel(Integer.parseInt(artikelIdField.getText()), artikelNaamField.getText(), Double.parseDouble(artikelPrijsField.getText()));
			guiMySQL.updateArtikel(nieuwArtikel);
			setArtikelListView();
		}
	}

	/* Lanceert een nieuw Stage waar een bestelling aangepast kan worden
	 * bestellingId en klantId blijven gelijk, alleen de artikelen kunnen 
	 * veranderen. Een bestaande bestelling moet geselecteerd zijn
	 */
	private void updateBestelling(){
		if(!(GuiPojo.bestelling.getBestelling_id() == 0)){
			GuiVoorBestellingBewerkingen bestellingBewerken = new GuiVoorBestellingBewerkingen();
			bestellingBewerken.setBestelling(GuiPojo.bestelling);
			try {
				bestellingBewerken.start(new Stage());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			errorBox.setMessageAndStart("Selecteer eerst een bestelling");
		}
	}
	/* Lanceert een nieuw Stage waar een nieuwe bestelling gemaakt kan worden
	 * klantId blijft gelijk, alleen artikelen kunnen worden toegevoegd
	 * veranderen. Een bestaande klant moet geselecteerd zijn
	 */
	private void nieuweBestelling(){
		if(!klantIdField.getText().isEmpty()){
			GuiVoorBestellingBewerkingen bestellingBewerken = new GuiVoorBestellingBewerkingen();
			try {
				bestellingBewerken.setKlantIdAndRun(GuiPojo.bestelling.getKlant_id(), bestellingListView);
			} catch (Exception e) {
				errorBox.setMessageAndStart(e.getMessage());
			}
		}else{
			errorBox.setMessageAndStart("Selecteer eerst een klant");
		}
	}

	/* Lanceert een nieuw Stage waar een klant aangepast kan worden.
	 * Het klantId blijft gelijk, andere gegevens kunnen allemaal
	 * veranderen. Een bestaande klant moet geselecteerd zijn
	 */
	private void updateKlant(){
		try {
			if(GuiPojo.klant.getKlant_id() == 0)
				throw new RSVIERException("Selecteer eerst een klant");
			GuiVoorKlantBewerkingen nieuweKlant = new GuiVoorKlantBewerkingen();
			nieuweKlant.setKlant(GuiPojo.klant);
			nieuweKlant.start(new Stage());
		}catch(RSVIERException e){
			errorBox.setMessageAndStart(e.getMessage());
		}catch(NullPointerException e){
			e.printStackTrace();
		}catch (Exception e) {
			errorBox.setMessageAndStart(e.getMessage());
		}

	}

	/* Lanceert een nieuw Stage waar een nieuwe klant gemaakt kan worden.*/
	private void nieuweKlant(){
		try {
			new GuiVoorKlantBewerkingen().start(new Stage());
		} catch (Exception e) {
			errorBox.setMessageAndStart(e.getMessage());
		}

	}
}