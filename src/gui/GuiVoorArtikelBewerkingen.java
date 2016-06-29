package gui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;

import exceptions.GeneriekeFoutmelding;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import logger.DeLogger;
import model.Artikel;
import model.GuiPojo;

public class GuiVoorArtikelBewerkingen extends Application{
	Artikel artikel;

	ArrayList<Artikel> artikelArrayLijst = new ArrayList<Artikel>();
	LinkedHashSet<Artikel> artikelSet = new LinkedHashSet<Artikel>(); 
	
	Stage artikelStage;
	ListView<String> artikelListView = new ListView<String>();

	Button updateButton;
	Button nieuwButton;
	Button cancelButton;
	CheckBox alleenActief;

	TextField naamField;
	TextField artikelPrijsField;
	TextField verwachteLevertijdField;
	CheckBox inAssortiment;

	ErrorBox errorBox = new ErrorBox();

	public void setAndRun(LinkedHashSet<Artikel> artikelSet) throws Exception{
		this.artikelSet = artikelSet;
		start(new Stage());
	}

	@Override
	public void start(Stage artikelStage) throws Exception {
		this.artikelStage = artikelStage;

		maakButtons();
		maakTextFields();
		populateListViewEnArrayList();

		GridPane artikelGrid = populateArtikelGrid();

		HBox buttonBox = new HBox();
		buttonBox.getChildren().addAll(nieuwButton, updateButton, cancelButton);

		VBox leftBox = new VBox();
		
		leftBox.getChildren().addAll(artikelGrid, buttonBox);
		
		HBox box = new HBox();
		box.getChildren().addAll(artikelListView, leftBox);
		
		artikelStage.setScene(new Scene(box));
		artikelStage.show();
	}

	private void maakTextFields() {
		naamField = new TextField();
		artikelPrijsField = new TextField();
		verwachteLevertijdField = new TextField(); 

		naamField.setPromptText("Artikelnaam");
		artikelPrijsField.setPromptText("Artikelprijs");
		verwachteLevertijdField.setPromptText("Levertijd");
	}

	private GridPane populateArtikelGrid() {
		GridPane artikelGrid = new GridPane();
		artikelGrid.setVgap(2);
		artikelGrid.setHgap(5);
		artikelGrid.setPadding(new Insets(4));

		artikelGrid.add(new Label("Artikel"), 0, 0);
		artikelGrid.add(new Label("Prijs"), 0, 1);
		artikelGrid.add(new Label("Levertijd"), 0, 2);
		artikelGrid.add(inAssortiment, 0, 3);

		artikelGrid.add(naamField, 1, 0);
		artikelGrid.add(artikelPrijsField, 1, 1);
		artikelGrid.add(verwachteLevertijdField, 1, 2);

		return artikelGrid;
	}

	private void populateListViewEnArrayList() {
		Iterator<Artikel> artikelIterator = artikelSet.iterator();
		Artikel artikel;

		artikelListView = new ListView<String>();
		artikelListView.setOnMouseClicked(e -> getItemVanArtikelLijst());

		artikelArrayLijst = new ArrayList<Artikel>();

		while(artikelIterator.hasNext()){
			artikel = artikelIterator.next();
			artikelListView.getItems().add("Naam: " + artikel.getArtikelNaam() 
			+ "\nPrijs: " + artikel.getArtikelPrijs());

			artikelArrayLijst.add(artikel);
		}		
	}

	private void getItemVanArtikelLijst() {
		artikel = artikelArrayLijst.get(artikelListView.getSelectionModel().getSelectedIndex());

		naamField.setText(artikel.getArtikelNaam());
		artikelPrijsField.setText("" + artikel.getArtikelPrijs());
		verwachteLevertijdField.setText("" + artikel.getVerwachteLevertijd());
		inAssortiment.setSelected(artikel.isInAssortiment());
	}

	private void maakButtons() {
		updateButton = new Button("Update artikel");
		nieuwButton = new Button("Nieuw artikel");
		cancelButton = new Button("Cancel");

		alleenActief = new CheckBox("Alleen actieve items");
		inAssortiment = new CheckBox("Artikel in assortiment");

		updateButton.setOnAction(e -> updateArtikel());
		nieuwButton.setOnAction(e -> nieuwArtikel());
		cancelButton.setOnAction(e ->  artikelStage.close());
	}

	private void nieuwArtikel() {
		artikel.setArtikelNaam(naamField.getText());
		artikel.setPrijsId(0);
		artikel.setArtikelPrijs(new BigDecimal(artikelPrijsField.getText()));
		artikel.setVerwachteLevertijd(Integer.parseInt(verwachteLevertijdField.getText()));
		artikel.setInAssortiment(inAssortiment.isSelected());
		
		try {
			GuiPojo.artikelDAO.nieuwArtikel(artikel);
		} catch (GeneriekeFoutmelding e) {
			e.printStackTrace();
			DeLogger.getLogger().error("Fout bij aanmaken nieuw artikel", e.getCause());
			errorBox.setMessageAndStart("Fout bij aanmaken nieuw artikel: " + e.getMessage());
		}
	}

	private void updateArtikel() {
		boolean prijsAangepast = false;
		boolean ietsAangepast = false;
		BigDecimal prijs = new BigDecimal(artikelPrijsField.getText());

		if(!artikel.getArtikelNaam().equals(naamField.getText())){
			ietsAangepast = true;
			artikel.setArtikelNaam(naamField.getText());
		}
		if((artikel.getArtikelPrijs()).compareTo(prijs) != 0){
			prijsAangepast = true;
			artikel.setArtikelPrijs(new BigDecimal(artikelPrijsField.getText()));
		}
		if(artikel.isInAssortiment() != inAssortiment.isSelected()){
			ietsAangepast = true;
			artikel.setInAssortiment(inAssortiment.isSelected());
		}
		if(artikel.getVerwachteLevertijd() != Integer.parseInt(verwachteLevertijdField.getText())){
			ietsAangepast = true;
			artikel.setVerwachteLevertijd(Integer.parseInt(verwachteLevertijdField.getText()));
		}

		if(ietsAangepast || prijsAangepast){
			try {
				GuiPojo.artikelDAO.updateArtikel(artikel.getArtikelId(), artikel);
			} catch (GeneriekeFoutmelding e) {
				e.printStackTrace();
				DeLogger.getLogger().error("Fout bij update artikel", e.getCause());
				errorBox.setMessageAndStart("Fout bij update artikel: " + e.getMessage());
			}
		}
	}

}