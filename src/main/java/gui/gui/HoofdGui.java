package gui.gui;

import exceptions.GeneriekeFoutmelding;

import gui.bewerkingen.HoofdGuiBewerkingen;
import gui.model.GuiPojo;

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

import model.Adres;
import model.Artikel;
import model.Klant;

public class HoofdGui extends Application{
	private final Insets INSET = new Insets(13);

	HoofdGuiBewerkingen guiBewerkingen = new HoofdGuiBewerkingen();

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
	Button artikelModuleButton;
	Button adresModuleButton;
	Button nieuweKlantButton;
	Button updateKlantButton;

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
		gridBox.setSpacing(10);

		HBox knoppenBox = new HBox();
		knoppenBox.getChildren().addAll(zoekButton,
				new Label("  "), leegButton,
				new Label("  "), artikelModuleButton,
				new Label("  "), adresModuleButton,
				new Label("  "), nieuweBestellingButton, updateBestellingButton, verwijderBestelling,
				new Label("  "), nieuweKlantButton, updateKlantButton);
		knoppenBox.setSpacing(5);

		VBox verticalBox = new VBox();
		verticalBox.getChildren().addAll(gridBox, knoppenBox);
		verticalBox.setSpacing(10);
		verticalBox.setPadding(INSET);

		Scene scene = new Scene(verticalBox);
		stage.setScene(scene);
		stage.getIcons().add(new Image("/images/icon.png"));
		stage.setTitle("Harrie's Tweedehands Beessies");
		stage.show();
	}

	private void maakListViewsAan(){
		klantListView = new ListView<>();
		klantListView.setOnMouseClicked(e -> getItemVanKlantenLijst());

		bestellingListView = new ListView<>();
		bestellingListView.setOnMouseClicked(e -> getItemVanBestellingLijst());

		artikelListView = new ListView<>();
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

		nieuweBestellingButton = new Button("Bestelling aanmaken");
		updateBestellingButton = new Button("Bestelling aanpassen");
		verwijderBestelling = new Button("Bestelling verwijderen");

		artikelModuleButton = new Button("Artikel module");

		adresModuleButton = new Button("Adres module");

		nieuweKlantButton = new Button("Klant aanmaken");
		updateKlantButton = new Button("Klant aanpassen");

		zoekButton.setOnAction(e -> zoekKnopKlik());
		leegButton.setOnAction(E -> leegAlles());

		artikelModuleButton.setOnAction(e -> artikelModule());

		adresModuleButton.setOnAction(e -> adresModule());

		nieuweBestellingButton.setOnAction(e -> nieuweBestelling());
		updateBestellingButton.setOnAction(e -> updateBestelling());
		verwijderBestelling.setOnAction(e -> guiBewerkingen.verwijderEnkeleBestelling(bestellingListView));

		nieuweKlantButton.setOnAction(e -> nieuweKlant());
		updateKlantButton.setOnAction(e -> updateKlant());
	}

	private void artikelModule() {
		guiBewerkingen.maakArtikelGui();
	}

	private void adresModule() {
		if(!klantIdField.getText().isEmpty())
			guiBewerkingen.maakAdresGui(Long.valueOf(klantIdField.getText()));
		else
			GuiPojo.errorBox.setMessageAndStart("Selecteer eerst een klant");
	}

	//zoekGrid bevat alle velden met info waarop gezocht kan worden
	private void populateZoekGrid(){
		zoekGrid = new GridPane();

		Text zoekOp = new Text("Zoeken:");
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
		zoekGrid.add(new Label("Bestelling ID"), 0, 7);
		zoekGrid.add(new Label(" "), 0, 8);
		zoekGrid.add(new Label("Straatnaam"), 0, 9);
		zoekGrid.add(new Label("Huisnummer"), 0, 10);
		zoekGrid.add(new Label("Toevoeging"), 0, 11);
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

		if(bestellingIdField.getText().equals("")){
			Klant klant = nieuwKlantObject();

			if(actieveItems.isSelected()) {
				klant.setKlantActief("1");
			}
			klant.setAdresGegevens(nieuwAdresObject());

			guiBewerkingen.zoekKlant(klantListView, klant);
		}
		else
			zoekBestelling();
	}

	private Adres nieuwAdresObject() {
		Adres adres = new Adres();
		adres.setHuisnummer((huisnummerField.getText() != null && huisnummerField.getText().equals("")) ? 0 : Integer.parseInt(huisnummerField.getText())); 

		adres.setToevoeging(toevoegingField.getText());
		adres.setStraatnaam(straatnaamField.getText());
		adres.setPostcode(postcodeField.getText());
		adres.setWoonplaats(woonplaatsField.getText());
		return adres;
	}

	private Klant nieuwKlantObject() {
		Klant klant = new Klant();
		klant.setKlantId((klantIdField != null && klantIdField.getText().equals("")) ? 0 : Long.parseLong(klantIdField.getText()));
		klant.setVoornaam(voorNaamField.getText());
		klant.setAchternaam(achterNaamField.getText());
		klant.setTussenvoegsel(tussenVoegselField.getText());
		klant.setEmail(emailField.getText());
		return klant;
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
		guiBewerkingen.resetBestelling();
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

			if(GuiPojo.klant.getKlantId() > 0){
				setKlantGegevens();
				zoekBestelling();
			}
		}
	}

	private void setKlantGegevens() {
		klantIdField.setText("" + GuiPojo.klant.getKlantId());
		voorNaamField.setText(GuiPojo.klant.getVoornaam());
		achterNaamField.setText(GuiPojo.klant.getAchternaam());
		tussenVoegselField.setText(GuiPojo.klant.getTussenvoegsel());
		emailField.setText(GuiPojo.klant.getEmail());

		guiBewerkingen.getAdres(actieveItems.isSelected());

		Adres adres = GuiPojo.klant.getAdresGegevens().get(0); // TODO nettere manier om adres gegevens te verwerken bedenken
		straatnaamField.setText(adres.getStraatnaam());
		if(adres.getHuisnummer() != 0)
			huisnummerField.setText("" + adres.getHuisnummer());
		toevoegingField.setText(adres.getToevoeging());
		postcodeField.setText(adres.getPostcode());
		woonplaatsField.setText(adres.getWoonplaats());
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
				if(klantIdField.getText().isEmpty()){
					guiBewerkingen.zoekViaBestellingKlant();
					setKlantGegevens();
					String gegevens = GuiPojo.klant.getTussenvoegsel().equals("") ? GuiPojo.klant.getKlantId() + ": " + GuiPojo.klant.getVoornaam() + " " + GuiPojo.klant.getAchternaam() : GuiPojo.klant.getKlantId() + ": " + GuiPojo.klant.getVoornaam() + " " + GuiPojo.klant.getTussenvoegsel() + " " + GuiPojo.klant.getAchternaam();
					if(!klantListView.getItems().contains(gegevens))
						klantListView.getItems().add(gegevens);				}
			}
			getKlantGegevens(); // Vind de klant adhv beschikbare info
			setKlantGegevens(); // Zet klant gegevens in de textfields
			setArtikelListView();
		}
	}

	private void setArtikelListView(){
		if(GuiPojo.artikel != null){
			artikelListView.getItems().clear();

			if(GuiPojo.bestelling.getKlantId() != 0)
				klantIdField.setText("" + GuiPojo.bestelling.getKlantId());
			if(GuiPojo.bestelling.getBestellingId() != 0)
				bestellingIdField.setText("" + GuiPojo.bestelling.getBestellingId());

			guiBewerkingen.setArtikelLijst();

			for(Artikel artikel : GuiPojo.artikelLijst){
				artikelListView.getItems().add("Naam: " + artikel.getArtikelNaam() +
						"\nPrijs: " + artikel.getArtikelPrijs() +
						"\nAantal: " + artikel.getAantalBesteld() +
						"\nTotaalbedrag: " + ((artikel.getArtikelPrijs().doubleValue() *
								artikel.getAantalBesteld())));
			}
		}
	}


	private void getKlantGegevens(){
		if(klantIdField.getText().isEmpty()){
			Klant klant = nieuwKlantObject();
			klant.setAdresGegevens(nieuwAdresObject());
			guiBewerkingen.zoekKlant(klantListView, klant);
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
		guiBewerkingen.updateBestelling();
	}

	/* Lanceert een nieuw Stage waar een nieuwe bestelling gemaakt kan worden
	 * klantId blijft gelijk, alleen artikelen kunnen worden toegevoegd
	 * veranderen. Een bestaande klant moet geselecteerd zijn
	 */
	private void nieuweBestelling(){
		guiBewerkingen.nieuweBestelling(bestellingListView, klantIdField.getText());
	}

	/* Lanceert een nieuw Stage waar een klant aangepast kan worden.
	 * Het klantId blijft gelijk, andere gegevens kunnen allemaal
	 * veranderen. Een bestaande klant moet geselecteerd zijn
	 */
	private void updateKlant(){
		guiBewerkingen.updateKlant();
	}

	/* Lanceert een nieuw Stage waar een nieuwe klant gemaakt kan worden.*/
	private void nieuweKlant(){
		GuiPojo.klant = new Klant();
		guiBewerkingen.nieuweKlant();
	}

	public void setConnection(String databaseSelected, String connectionSelected) {
		guiBewerkingen.setDAOs(databaseSelected, connectionSelected);
	}
}