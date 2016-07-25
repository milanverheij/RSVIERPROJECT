package gui.gui;

import java.math.BigDecimal;
import gui.bewerkingen.BestellingGuiBewerkingen;
import gui.model.GuiPojo;
import gui.model.SubGuiPojo;
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
import javafx.stage.Stage;
import model.Bestelling;

public class BestellingGui extends Application{

	BestellingGuiBewerkingen guiBewerkingen = new BestellingGuiBewerkingen();

	Insets inset = new Insets(6);
	
	private Stage bestellingStage;

	private Button voegToeButton;
	private Button haalWegButton;
	private Button okeButton;
	private Button cancelButton;

	private CheckBox bestellingActief;

	private ListView<String> huidigeBestellingListView;
	private ListView<String> artikelenListView;

	private TextField totaalPrijs;
	private TextField aantal;

	@Override
	public void start(Stage bestellingStage) throws Exception {
		this.bestellingStage = bestellingStage;

		if(SubGuiPojo.bestellingBewerken){
			bestellingStage.setTitle("Bestelling aanpassen");
		}else{
			SubGuiPojo.bestelling = new Bestelling();
			bestellingStage.setTitle("Nieuwe bestelling");
		}
		bestellingStage.getIcons().add(new Image("/images/icon.png"));
		maakButtons();
		maakListViews();
		populateArrayList();
		
		guiBewerkingen.populateListView(artikelenListView, "CompleteArtikelLijst");
		
		if(SubGuiPojo.bestellingBewerken)
			guiBewerkingen.populateListView(huidigeBestellingListView, "BestellingArtikelLijst");
		maakTextFields();

		GridPane bestelGrid = populateBestelGrid();

		HBox winkelwagenBox = new HBox(); //Bevat knoppen om dingen in/uit de winkelwagen te doen
		winkelwagenBox.getChildren().addAll(voegToeButton, haalWegButton);
		winkelwagenBox.setSpacing(3);

		HBox okeCancelBox = new HBox();
		okeCancelBox.getChildren().addAll(okeButton, cancelButton);
		okeCancelBox.setSpacing(3);

		VBox vbox = new VBox();
		vbox.setPadding(inset);
		vbox.setSpacing(3);
		vbox.getChildren().addAll(bestelGrid, winkelwagenBox, okeCancelBox);

		HBox box = new HBox();
		box.setPadding(inset);
		box.getChildren().addAll(artikelenListView, vbox, huidigeBestellingListView);

		bestellingStage.setScene(new Scene(box));
		bestellingStage.show();
	}

	private GridPane populateBestelGrid(){
		GridPane bestelGrid = new GridPane();
		bestelGrid.setVgap(2);
		bestelGrid.setHgap(5);
		bestelGrid.setPadding(new Insets(4));

		bestelGrid.add(new Label("Aantal"), 0, 0);
		bestelGrid.add(new Label("Prijs"), 0, 1);

		bestelGrid.add(aantal, 1, 0);
		bestelGrid.add(totaalPrijs, 1, 1);

		bestelGrid.add(bestellingActief, 0, 2);

		return bestelGrid;
	}

	private void maakListViews(){
		artikelenListView = new ListView<String>();
		artikelenListView.setOnMouseClicked(e -> getItemVanListView(artikelenListView, "CompleteArtikelLijst"));

		huidigeBestellingListView = new ListView<String>();
		huidigeBestellingListView.setOnMouseClicked(e ->  getItemVanListView(huidigeBestellingListView, "BestellingArtikelLijst"));
	}

	private void populateArrayList(){
		guiBewerkingen.populateArrayList();
	}
	
	private void maakTextFields() {
		totaalPrijs = new TextField();
		totaalPrijs.setEditable(false);

		aantal = new TextField();

		aantal.textProperty().addListener((observable, oldValue, newValue) -> {
			if(newValue.isEmpty() || Long.parseLong(newValue) < 1) //Wanneer er geen aantal of 0 is opgegeven
				totaalPrijs.setText("0");
			else if(aantal.getText().length() < 10)
				totaalPrijs.setText(SubGuiPojo.huidigArtikel.getArtikelPrijs().multiply(new BigDecimal(Long.parseLong(newValue))).toString());
			else
				GuiPojo.errorBox.setMessageAndStart("We kunnen niet meer dan 999.999.999 van een artikel leveren");
		});
	}

	private void maakButtons(){
		bestellingActief = new CheckBox("Bestelling actief");

		okeButton = new Button("Oke");
		okeButton.setOnAction(e -> maakbestelling());

		cancelButton = new Button("Cancel");
		cancelButton.setOnAction(e -> bestellingStage.close());

		if(SubGuiPojo.bestellingBewerken){
			voegToeButton = new Button("Voeg toe / Update");
			bestellingActief.setSelected(SubGuiPojo.bestelling.getBestellingActief());
		}else{
			voegToeButton = new Button("Voeg toe");
			bestellingActief.setSelected(true);
			bestellingActief.setDisable(true);
		}
		voegToeButton.setOnAction(e -> voegArtikelenAanBestellingToe());

		haalWegButton = new Button("Haal uit winkelwagen");
		haalWegButton.setOnAction(e -> haalUitBestelling());
	}

	private void haalUitBestelling() {
		guiBewerkingen.verwijderArtikel(SubGuiPojo.huidigArtikel);
		guiBewerkingen.populateListView(huidigeBestellingListView, "BestellingArtikelLijst");
	}
	
	private void voegArtikelenAanBestellingToe(){
		if(!aantal.getText().isEmpty()){
			guiBewerkingen.voegArtikelenAanBestellingToe(Integer.parseInt(aantal.getText()));
		}

		guiBewerkingen.populateListView(huidigeBestellingListView, "BestellingArtikelLijst");
	}

	private void maakbestelling(){
		if(guiBewerkingen.maakBestelling(SubGuiPojo.bestellingBewerken, bestellingActief.isSelected())){
			bestellingStage.close();
		}
	}	

	private void getItemVanListView(ListView<String> listView, String welkeArtikelLijst) {
		guiBewerkingen.getItemVanListView(listView.getSelectionModel().getSelectedIndex(), welkeArtikelLijst);
		aantal.setText("" + guiBewerkingen.getAantal());
	}
}
