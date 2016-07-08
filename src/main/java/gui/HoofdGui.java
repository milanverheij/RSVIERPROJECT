package gui;

import exceptions.GeneriekeFoutmelding;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Artikel;
import model.Bestelling;
import model.GuiPojo;
import mysql.GuiBewerkingenMySQL;

import java.util.LinkedHashMap;

public class HoofdGui extends Application{
	private final Insets INSET = new Insets(6);

	ErrorBox errorBox = new ErrorBox();

	GuiBewerkingenMySQL guiBewerkingen = new GuiBewerkingenMySQL();

	TextField klantIdField;
	TextField voorNaamField;
	TextField achterNaamField;
	TextField tussenVoegselField;
	TextField emailField;
	ListView<String> klantListView;

	TextField straatnaamField;
	TextField huisnummerField;
	TextField toevoegingField;
	TextField postcodeField;
	TextField woonplaatsField;

	TextField bestellingIdField;
	ListView<Long> bestellingListView;

	ListView<String> artikelListView;

	CheckBox actieveItems;

	Label errorLabel = new Label();

	GridPane zoekGrid;
	GridPane displayGrid;

	Button zoekButton;
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
		knoppenBox.getChildren().addAll(zoekButton,
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

		bestellingIdField = new TextField();

		straatnaamField = new TextField();
		huisnummerField = new TextField();
		toevoegingField = new TextField();
		straatnaamField = new TextField();
		postcodeField = new TextField();
		woonplaatsField = new TextField();
	}

	private void maakButtonsEnSetOnAction() throws GeneriekeFoutmelding, Exception{
		zoekButton = new Button("Zoeken");
		leegButton = new Button("Leeg velden");

		updateBestellingButton = new Button("Update bestelling");
		nieuweBestellingButton = new Button("Nieuwe bestelling");
		verwijderBestelling = new Button("Verwijder bestelling");

		updateArtikelButton = new Button("Update Artikel");

		nieuweKlantButton = new Button("Nieuwe klant");
		updateKlantButton = new Button("Update klant");

		zoekButton.setOnAction(e -> zoekKnopKlik());
		leegButton.setOnAction(E -> leegAlles());

		updateArtikelButton.setOnAction(e -> nieuwArtikel());

		updateBestellingButton.setOnAction(e -> updateBestelling());
		nieuweBestellingButton.setOnAction(e -> nieuweBestelling());
		verwijderBestelling.setOnAction(e -> verwijderBestelling());

		nieuweKlantButton.setOnAction(e -> nieuweKlant());
		updateKlantButton.setOnAction(e -> updateKlant());
	}

	private void nieuwArtikel() {
		try {
			GuiVoorArtikelBewerkingen artikelGui = new GuiVoorArtikelBewerkingen();
			artikelGui.setAndRun(GuiPojo.artikelDAO.getAlleArtikelen(false));
		} catch (GeneriekeFoutmelding e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/* Verwijdert een enkele bestelling uit de database */
	private void verwijderBestelling(){
		GuiPojo.bestellingLijst.remove(GuiPojo.bestelling.getBestelling_id());
		guiBewerkingen.verwijderEnkeleBestelling();
		bestellingListView.getItems().clear();
		try {
			guiBewerkingen.populateBestellingListView(bestellingListView);
		} catch (GeneriekeFoutmelding e) {
			errorBox.setMessageAndStart(e.getMessage());
		}
	}

	//zoekGrid bevat alle velden met info waarop gezocht kan worden
	private void populateZoekGrid(){
		zoekGrid = new GridPane();

		Text zoekOp = new Text("Zoeken op");
		zoekOp.setFont(Font.font(Font.getDefault().getName(), FontWeight.BOLD, Font.getDefault().getSize()));

		actieveItems = new CheckBox("Alleen actieve items tonen");
		actieveItems.setSelected(true);

		zoekGrid.add(zoekOp, 0, 0);
		zoekGrid.add(new Label("Klant ID"), 0, 1);
		zoekGrid.add(new Label("Voornaam"), 0, 2);
		zoekGrid.add(new Label("Tussenvoegsel"), 0, 3);
		zoekGrid.add(new Label("Achternaam"), 0, 4);
		zoekGrid.add(new Label("E-mail"), 0, 5);
		zoekGrid.add(new Label(" "), 0, 6);
		zoekGrid.add(new Label("Bestelling Id"), 0, 7);
		zoekGrid.add(new Label(" "), 0, 8);
		zoekGrid.add(new Label("Straatnaam"), 0, 9);
		zoekGrid.add(new Label("Huisnummer"), 0, 10);
		zoekGrid.add(new Label("toevoeging"), 0, 11);
		zoekGrid.add(new Label("Postcode"), 0, 12);
		zoekGrid.add(new Label("Woonplaats"), 0, 13);
		zoekGrid.add(new Label(" "), 0, 14);

		zoekGrid.add(klantIdField, 1, 1);
		zoekGrid.add(voorNaamField, 1, 2);
		zoekGrid.add(tussenVoegselField, 1, 3);
		zoekGrid.add(achterNaamField, 1, 4);
		zoekGrid.add(emailField, 1, 5);

		zoekGrid.add(bestellingIdField, 1, 7);

		zoekGrid.add(straatnaamField, 1, 9);
		zoekGrid.add(huisnummerField, 1, 10);
		zoekGrid.add(toevoegingField, 1, 11);
		zoekGrid.add(postcodeField, 1, 12);
		zoekGrid.add(woonplaatsField, 1, 13);

		zoekGrid.add(actieveItems, 0, 15);

		zoekGrid.setHgap(5);
		zoekGrid.setVgap(2);
	}

	//displayGrid bevat de ListViews voor de klantenlijst, de bestellinglijst, en de artikellijst
	private void populateDisplayGrid(){
		displayGrid = new GridPane();
		displayGrid.add(new Label("Klantenlijst"), 0, 0);
		displayGrid.add(new Label("Bestellingenlijst"), 1, 0);
		displayGrid.add(new Label("Artikellijst"), 2, 0);
		displayGrid.add(klantListView, 0, 1);
		displayGrid.add(bestellingListView, 1, 1);
		displayGrid.add(artikelListView, 2, 1);

		displayGrid.setHgap(5);
		displayGrid.setVgap(2);
	}

	//Zoekt alle klanten op aan de hand van de ingevulde gegevens
	private void zoekKnopKlik(){
		leegViews();

		if(bestellingIdField.getText().equals(""))
			guiBewerkingen.zoekKlant(klantListView, klantIdField.getText(), voorNaamField.getText(),
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
		guiBewerkingen.zoekBestelling(bron, bestellingListView, klantIdField.getText(), bestellingIdField.getText(), actieveItems.isSelected());
	}

	/* Leegt de TextField van klantgegevens */
	private void leegKlantVelden(){
		klantIdField.setText("");
		voorNaamField.setText("");
		achterNaamField.setText("");
		tussenVoegselField.setText("");
		emailField.setText("");
		bestellingIdField.setText("");

		straatnaamField.setText("");
		huisnummerField.setText("");
		toevoegingField.setText("");
		postcodeField.setText("");
		woonplaatsField.setText("");
	}

	//Leegt alle ListViews en reset de variabelen waarmee ze opgebouwd
	//worden en selecties mee bijgehouden worden
	private void leegViews(){
		bestellingListView.getItems().clear();
		artikelListView.getItems().clear();
		klantListView.getItems().clear();
		guiBewerkingen.leegKlantBestellingArtikel();
	}

	//Leegt alle TextFields in de HoofdGui
	private void leegAlleVelden(){
		leegKlantVelden();
		leegAdresVelden();
		GuiPojo.bestelling = new Bestelling();
		GuiPojo.bestellingLijst = new LinkedHashMap<Long, Bestelling>();
	}

	private void leegAdresVelden() {
		straatnaamField.clear();
		huisnummerField.clear();
		toevoegingField.clear();
		straatnaamField.clear();
		postcodeField.clear();
		woonplaatsField.clear();
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
		guiBewerkingen.getItemVanKlantenLijst(klantListView);

		if(GuiPojo.klant != null){
			leegAlleVelden();
			bestellingListView.getItems().clear();
			artikelListView.getItems().clear();

			if(GuiPojo.klant.getKlantId() != 0){
				klantIdField.setText("" + GuiPojo.klant.getKlantId());
				voorNaamField.setText(GuiPojo.klant.getVoornaam());
				achterNaamField.setText(GuiPojo.klant.getAchternaam());
				tussenVoegselField.setText(GuiPojo.klant.getTussenvoegsel());
				emailField.setText(GuiPojo.klant.getEmail());

				guiBewerkingen.getAdres(actieveItems.isSelected());				

				straatnaamField.setText(GuiPojo.klant.getAdresGegevens().getStraatnaam());
				if(GuiPojo.klant.getAdresGegevens().getHuisnummer() != 0)
					huisnummerField.setText("" + GuiPojo.klant.getAdresGegevens().getHuisnummer());
				toevoegingField.setText(GuiPojo.klant.getAdresGegevens().getToevoeging());
				postcodeField.setText(GuiPojo.klant.getAdresGegevens().getPostcode());
				woonplaatsField.setText(GuiPojo.klant.getAdresGegevens().getWoonplaats());

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
			artikelListView.getItems().clear();
			long selectedItem = bestellingListView.getSelectionModel().getSelectedItem();
			if(selectedItem >= 0){
				guiBewerkingen.getItemVanBestellingLijst(selectedItem);
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

			guiBewerkingen.setArtikelLijst();

			for(Artikel artikel : GuiPojo.artikelLijst){
				artikelListView.getItems().add("Naam: " + artikel.getArtikelNaam() + "\nPrijs: " + artikel.getArtikelPrijs() +
						"\nAantal: " + artikel.getAantalBesteld());
			}
			if(klantIdField.getText().isEmpty())
				guiBewerkingen.zoekKlant(klantListView, klantIdField.getText(), voorNaamField.getText(),
						achterNaamField.getText(), tussenVoegselField.getText(), emailField.getText());
		}
	}

	/* Loopt wanneer er op de artikellijst
	 * op een item geklikt is
	 */
	private void getItemVanArtikelLijst(){
		int index = artikelListView.getSelectionModel().getSelectedIndex();

		if(index >= 0){
			guiBewerkingen.getItemVanArtikelLijst(index);
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
				bestellingBewerken.setAndRun(GuiPojo.klant.getKlantId(), bestellingListView);
			} catch (Exception e) {
				e.printStackTrace();
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
			if(GuiPojo.klant.getKlantId() == 0)
				errorBox.setMessageAndStart("Selecteer eerst een klant");
			else{
				GuiVoorKlantBewerkingen nieuweKlant = new GuiVoorKlantBewerkingen();
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

	/* Lanceert een nieuw Stage waar een nieuwe klant gemaakt kan worden.*/
	private void nieuweKlant(){
		try {
			new GuiVoorKlantBewerkingen().start(new Stage());
		} catch (Exception e) {
			errorBox.setMessageAndStart(e.getMessage());
		}
	}

	public void setConnection(String databaseSelected, String connectionSelected) {
		guiBewerkingen.setDAOs(databaseSelected, connectionSelected);
	}
}