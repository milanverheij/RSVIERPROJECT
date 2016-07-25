package gui.gui;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;

import exceptions.GeneriekeFoutmelding;
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
import javafx.stage.Stage;
import logger.DeLogger;
import model.Adres;

public class AdresGui extends Application{
	Adres adres;

	ArrayList<Adres> adresArrayLijst = new ArrayList<>();
	LinkedHashSet<Adres> adresSet = new LinkedHashSet<>();

	Stage adresStage;
	ListView<String> adresListView = new ListView<>();

	Button updateButton;
	Button nieuwButton;
	Button cancelButton;
	CheckBox alleenActief;

	TextField straatNaamField;
	TextField huisnummerField;
	TextField toevoegingField;
	TextField postcodeField;
	TextField woonplaatsField;
	CheckBox adresActief;

	public void setAndRun(LinkedHashSet<Adres> adresSet) throws Exception{
		this.adresSet = adresSet;
		start(new Stage());
	}

	@Override
	public void start(Stage adresStage) throws Exception {
		this.adresStage = adresStage;

		maakButtons();
		maakTextFields();
		populateListViewEnArrayList();

		GridPane adresGrid = populateAdresGrid();

		HBox buttonBox = new HBox();
		buttonBox.getChildren().addAll(nieuwButton, updateButton, cancelButton);

		VBox leftBox = new VBox();

		leftBox.getChildren().addAll(adresGrid, buttonBox);

		HBox box = new HBox();
		box.getChildren().addAll(adresListView, leftBox);

		adresStage.getIcons().add(new Image("/images/icon.png"));
		adresStage.setScene(new Scene(box));
		adresStage.show();
	}

	private void maakTextFields() {
		straatNaamField = new TextField();
		huisnummerField = new TextField();
		toevoegingField = new TextField();
		postcodeField = new TextField();
		woonplaatsField = new TextField();

		straatNaamField.setPromptText("Straatnaam");
		huisnummerField.setPromptText("Huisnummer");
		toevoegingField.setPromptText("Toevoeging");
		postcodeField.setPromptText("Postcode");
		woonplaatsField.setPromptText("Woonplaats");
	}

	private GridPane populateAdresGrid() {
		GridPane adresGrid = new GridPane();
		adresGrid.setVgap(2);
		adresGrid.setHgap(5);
		adresGrid.setPadding(new Insets(4));

		adresGrid.add(new Label("Straatnaam"), 0, 0);
		adresGrid.add(new Label("Huisnummer"), 0, 1);
		adresGrid.add(new Label("Toevoeging"), 0, 2);
		adresGrid.add(new Label("Postcode"), 0, 3);
		adresGrid.add(new Label("Woonplaats"), 0, 4);
		adresGrid.add(adresActief, 0, 5);

		adresGrid.add(straatNaamField, 1, 0);
		adresGrid.add(huisnummerField, 1, 1);
		adresGrid.add(toevoegingField, 1, 2);
		adresGrid.add(postcodeField, 1, 3);
		adresGrid.add(woonplaatsField, 1, 4);

		return adresGrid;
	}

	private void populateListViewEnArrayList() {
		Iterator<Adres> adresIterator = adresSet.iterator();
		Adres adres;

		adresListView = new ListView<>();
		adresListView.setOnMouseClicked(e -> getItemVanAdresLijst());

		adresArrayLijst = new ArrayList<>();

		while(adresIterator.hasNext()){
			adres = adresIterator.next();
			adresListView.getItems().add("Straatnaam: " + adres.getStraatnaam()
			+ "\nHuisnummer: " + adres.getHuisnummer()
			+ "\nToevoeging: " + adres.getToevoeging()
			+ "\nPostcode: " + adres.getPostcode()
			+ "\nWoonplaats: " + adres.getWoonplaats());

			adresArrayLijst.add(adres);
		}
	}

	private void getItemVanAdresLijst() {
		if(adresListView.getSelectionModel().getSelectedIndex() >= 0){
			adres = adresArrayLijst.get(adresListView.getSelectionModel().getSelectedIndex());

			straatNaamField.setText(adres.getStraatnaam());
			huisnummerField.setText("" + adres.getHuisnummer());
			toevoegingField.setText("" + adres.getToevoeging());
			postcodeField.setText("" + adres.getPostcode());
			woonplaatsField.setText("" + adres.getWoonplaats());
			adresActief.setSelected((adres.getAdresActief().charAt(0) == '1') ? true : false);
		}
	}

	private void maakButtons() {
		updateButton = new Button("Update adres");
		nieuwButton = new Button("Nieuw adres");
		cancelButton = new Button("Sluit venster");

		alleenActief = new CheckBox("Alleen actieve items");
		adresActief = new CheckBox("Adres actief");

		updateButton.setOnAction(e -> updateAdres());
		nieuwButton.setOnAction(e -> nieuwAdres());
		cancelButton.setOnAction(e ->  adresStage.close());
	}

	private void nieuwAdres() {
		adres = new Adres();
		vulWaardenInBijAdresObject();
		adres.setAdresId(0);

		try {
			GuiPojo.adresDAO.nieuwAdres(GuiPojo.klant.getKlantId(), adres);
		} catch (GeneriekeFoutmelding e) {
			e.printStackTrace();
			DeLogger.getLogger().error("Fout bij aanmaken nieuw adres", e.getCause());
			GuiPojo.errorBox.setMessageAndStart("Fout bij aanmaken nieuw adres: " + e.getMessage());
		}
	}

	private void vulWaardenInBijAdresObject() {
		adres.setStraatnaam(straatNaamField.getText());
		adres.setHuisnummer(Integer.parseInt(huisnummerField.getText()));
		adres.setToevoeging(toevoegingField.getText());
		adres.setPostcode(postcodeField.getText());
		adres.setWoonplaats(woonplaatsField.getText());
		adres.setAdresActief((adresActief.isSelected() == true) ? "1" : "0");
	}

	private void updateAdres() {
		//        boolean prijsAangepast = false;
		//        boolean ietsAangepast = false;

		//        if(artikel.isInAssortiment() != inAssortiment.isSelected()){
		//            ietsAangepast = true;
		//            artikel.setInAssortiment(inAssortiment.isSelected());
		//        }

		//        if(ietsAangepast || prijsAangepast){
		try {
			vulWaardenInBijAdresObject();
			GuiPojo.adresDAO.updateAdres(adres.getAdresId(), adres);
		} catch (GeneriekeFoutmelding e) {
			e.printStackTrace();
			DeLogger.getLogger().error("Fout bij update adres", e.getCause(), e.getStackTrace());
			GuiPojo.errorBox.setMessageAndStart("Fout bij update adres: " + e.getMessage());
		}
		//        }
	}
}