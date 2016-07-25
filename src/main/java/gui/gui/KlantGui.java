package gui.gui;

import java.util.ArrayList;

import org.apache.commons.validator.EmailValidator;
import exceptions.GeneriekeFoutmelding;
import gui.model.GuiPojo;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import logger.DeLogger;
import model.Adres;
import model.Klant;

@SuppressWarnings("deprecation")
public class KlantGui extends Application{
	TextField voorNaamField = new TextField("");
	TextField tussenVoegselField = new TextField("");
	TextField achterNaamField = new TextField("");
	TextField emailField = new TextField("");
	TextField straatNaamField = new TextField("");
	TextField postcodeField = new TextField("");
	TextField huisNummerField = new TextField("");
	TextField toevoegingField = new TextField("");
	TextField woonplaatsField = new TextField("");

	EmailValidator validator = EmailValidator.getInstance();

	Label error = new Label();

	private Klant klant;

	public void start(Stage klantStage) throws Exception {
		klant = GuiPojo.klant;

		GridPane grid = new GridPane();

		final BooleanProperty eersteKeer = new SimpleBooleanProperty(true); // Of de stage de eerste keer geladen word
		setFocus(grid, eersteKeer);

		setPromptText();
		populateTextFields();
		populateGrid(grid);
		maakButtonsEnVoegAanGridToe(grid, klantStage);

		klantStage.setScene(new Scene(grid));
		klantStage.getIcons().add(new Image("/images/icon.png"));
		klantStage.setTitle("Harrie's Tweedehands Beessies");
		klantStage.show();
	}

	private void maakButtonsEnVoegAanGridToe(GridPane grid, Stage klantStage) {
		Button maakAan = new Button("Oke");
		Button cancel = new Button("Cancel");

		Button schakelStatusAan = new Button("Zet actief");
		Button schakelStatusUit = new Button("Zet inactief");

		maakAan.setOnAction(e ->{
			if(validator.isValid(emailField.getText()))
				maakKlantAan(klantStage);	
			else
				GuiPojo.errorBox.setMessageAndStart("Ongeldig e-mail adres");
		});
		cancel.setOnAction(e -> klantStage.close());

		schakelStatusAan.setOnAction(e -> schakelKlant(klant, 1));
		schakelStatusUit.setOnAction(e -> schakelKlant(klant, 0 ));

		grid.add(maakAan, 0, 6);
		grid.add(cancel, 1, 6);
		grid.add(schakelStatusAan, 2, 6);
		grid.add(schakelStatusUit, 3, 6);
	}

	private void schakelKlant(Klant klant, int status) {
		try {
			GuiPojo.klantDAO.schakelStatusKlant(klant.getKlantId(), status);
		} catch (GeneriekeFoutmelding ex) {
			GuiPojo.errorBox.setMessageAndStart(ex.getMessage());
		}
	}

	private void setFocus(GridPane grid, BooleanProperty eersteKeer){
		voorNaamField.focusedProperty().addListener((observable,  oldValue,  newValue) -> {
			if(newValue && eersteKeer.get()){
				grid.requestFocus();
				eersteKeer.setValue(false);
			}
		});
	}

	private void populateGrid(GridPane grid){
		grid.add(new Label("Klantgegevens"), 0, 0);
		grid.add(voorNaamField, 0, 1);
		grid.add(tussenVoegselField, 1, 1);
		grid.add(achterNaamField, 2, 1);
		grid.add(straatNaamField, 0, 2);
		grid.add(huisNummerField, 1, 2);
		grid.add(toevoegingField, 2, 2);
		grid.add(postcodeField, 0, 3);
		grid.add(woonplaatsField, 1, 3);
		grid.add(emailField, 0, 4);
		grid.add(new Label(" "), 0, 5);
		grid.add(error, 0, 7);

		grid.setVgap(2);
		grid.setHgap(5);
		grid.setPadding(new Insets(5));
	}

	private void setPromptText(){
		voorNaamField.setPromptText("Voornaam");
		tussenVoegselField.setPromptText("Tussenvoegsel");
		achterNaamField.setPromptText("Achternaam");
		emailField.setPromptText("E-mail adres");

		straatNaamField.setPromptText("Straatnaam");
		postcodeField.setPromptText("1234AB");
		huisNummerField.setPromptText("123");
		toevoegingField.setPromptText("A");
		woonplaatsField.setPromptText("Woonplaats");

	}

	private void populateTextFields(){
		if(!(klant == null)){
			if(klant.getVoornaam() != null)
				voorNaamField.setText(klant.getVoornaam());
			if(klant.getAchternaam() != null)
				achterNaamField.setText(klant.getAchternaam());
			if(klant.getTussenvoegsel() != null)
				tussenVoegselField.setText(klant.getTussenvoegsel());
			if(klant.getEmail() != null)
				emailField.setText(klant.getEmail());

			if(GuiPojo.klant.getAdresGegevens() != null) {
				ArrayList<Adres> alleAdressen = GuiPojo.klant.getAdresGegevens();
				if(!alleAdressen.isEmpty()){
					Adres adres = alleAdressen.get(0);

					if(adres.getStraatnaam() != null)
						straatNaamField.setText(adres.getStraatnaam());
					if(adres.getPostcode() != null)
						postcodeField.setText(adres.getPostcode());
					if(adres.getHuisnummer() != 0)
						huisNummerField.setText("" + adres.getHuisnummer());
					if(adres.getToevoeging() != null)
						toevoegingField.setText(adres.getToevoeging());
					if(adres.getWoonplaats() != null)
						woonplaatsField.setText(adres.getWoonplaats());
				}
			}
		}
	}

	private void maakKlantAan(Stage klantStage){
		alleVeldenInvullen();

		Klant klant = new Klant();
		if(GuiPojo.klant.getKlantId() != 0)
			klant.setKlantId(GuiPojo.klant.getKlantId());

		klant.setVoornaam(voorNaamField.getText());
		klant.setAchternaam(achterNaamField.getText());
		klant.setTussenvoegsel(tussenVoegselField.getText());
		klant.setEmail(emailField.getText());

		Adres adres = new Adres();
		if(!GuiPojo.klant.getAdresGegevens().isEmpty() && GuiPojo.klant.getAdresGegevens().get(0).getAdresId() != 0)		//TODO: Nettere manier om adressen weer te geven in de klantmodule
			adres.setAdresId(GuiPojo.klant.getAdresGegevens().get(0).getAdresId());
		adres.setWoonplaats(woonplaatsField.getText());
		adres.setPostcode(postcodeField.getText());
		adres.setHuisnummer(Integer.parseInt(huisNummerField.getText()));
		adres.setStraatnaam(straatNaamField.getText());
		adres.setToevoeging(toevoegingField.getText());
		klant.setAdresGegevens(adres);

		GuiPojo.klant = klant;
		try {
			if(GuiPojo.klant.getKlantId() == 0){
				GuiPojo.klant.setKlantId(GuiPojo.klantDAO.nieuweKlant(GuiPojo.klant, 0));
			} else {
				GuiPojo.adresDAO.updateAdres(adres.getAdresId(), adres);
				GuiPojo.klantDAO.updateKlant(GuiPojo.klant);
			}

			klantStage.close();
		} catch (GeneriekeFoutmelding e) {
			e.printStackTrace();
			DeLogger.getLogger().error("Fout bij verwerken klantgegevens {}", e.getMessage(), e.getStackTrace());
			GuiPojo.errorBox.setMessageAndStart(e.getMessage());
		}
	}

	private void alleVeldenInvullen(){
		if(voorNaamField.getText().isEmpty())
			voorNaamField.setText("");
		if(achterNaamField.getText().isEmpty())
			achterNaamField.setText("");
		if(tussenVoegselField.getText().isEmpty())
			tussenVoegselField.setText("");
		if(emailField.getText().isEmpty())
			emailField.setText("");

		if(straatNaamField.getText().isEmpty())
			straatNaamField.setText("");
		if(postcodeField.getText().isEmpty())
			postcodeField.setText("");
		if(toevoegingField.getText().isEmpty())
			toevoegingField.setText("");
		if(huisNummerField.getText().isEmpty())
			huisNummerField.setText("");
		if(woonplaatsField.getText().isEmpty())
			woonplaatsField.setText("");
	}
}