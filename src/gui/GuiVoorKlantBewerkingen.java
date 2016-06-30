package gui;
import exceptions.GeneriekeFoutmelding;
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
import model.Adres;
import model.GuiPojo;
import model.Klant;


public class GuiVoorKlantBewerkingen extends Application{
	TextField voorNaam = new TextField("");
	TextField tussenVoegsel = new TextField("");
	TextField achterNaam = new TextField("");
	TextField email = new TextField("");
	TextField straatNaam = new TextField("");
	TextField postcode = new TextField("");
	TextField huisNummer = new TextField("");
	TextField toevoeging = new TextField("");
	TextField woonplaats = new TextField("");

	Label error = new Label();

	private Klant klant;

	public void start(Stage klantStage) throws Exception {
		klant = GuiPojo.klant;

		GridPane grid = new GridPane();

		final BooleanProperty eersteKeer = new SimpleBooleanProperty(true); // Of de stage de eerste keer geladen word
		setFocus(grid, eersteKeer);

		populateTextFields();
		populateGrid(grid);
		maakButtonsEnVoegAanGridToe(grid, klantStage);

		klantStage.setScene(new Scene(grid));
		klantStage.getIcons().add(new Image("/images/icon.jpg"));
		klantStage.setTitle("Harrie's Tweedehands Beessies");
		klantStage.show();

	}

	private void maakButtonsEnVoegAanGridToe(GridPane grid, Stage klantStage) {
		Button maakAan = new Button("Oke");
		Button cancel = new Button("Cancel");

		maakAan.setOnAction(e -> maakKlantAan(klantStage));
		cancel.setOnAction(e -> klantStage.close());

		grid.add(maakAan, 0, 6);
		grid.add(cancel, 1, 6);
	}

	private void setFocus(GridPane grid, BooleanProperty eersteKeer){
		voorNaam.focusedProperty().addListener((observable,  oldValue,  newValue) -> {
			if(newValue && eersteKeer.get()){
				grid.requestFocus();
				eersteKeer.setValue(false);
			}
		});
	}

	private void populateGrid(GridPane grid){
		grid.add(new Label("Klantgegevens"), 0, 0);
		grid.add(voorNaam, 0, 1);
		grid.add(tussenVoegsel, 1, 1);
		grid.add(achterNaam, 2, 1);
		grid.add(straatNaam, 0, 2);
		grid.add(huisNummer, 1, 2);
		grid.add(toevoeging, 2, 2);
		grid.add(postcode, 0, 3);
		grid.add(woonplaats, 1, 3);
		grid.add(email, 0, 4);
		grid.add(new Label(" "), 0, 5);
		grid.add(error, 0, 7);

		grid.setVgap(2);
		grid.setHgap(5);
		grid.setPadding(new Insets(5));
	}

	private void populateTextFields(){
		voorNaam.setPromptText("Voornaam");
		tussenVoegsel.setPromptText("Tussenvoegsel");
		achterNaam.setPromptText("Achternaam");
		email.setPromptText("E-mail adres");

		straatNaam.setPromptText("Straatnaam");
		postcode.setPromptText("1234AB");
		huisNummer.setPromptText("123");
		toevoeging.setPromptText("A");
		woonplaats.setPromptText("Woonplaats");

		if(!(klant == null)){
			if(klant.getVoornaam() != null)
				voorNaam.setText(klant.getVoornaam());
			if(klant.getAchternaam() != null)
				achterNaam.setText(klant.getAchternaam());
			if(klant.getTussenvoegsel() != null)
				tussenVoegsel.setText(klant.getTussenvoegsel());
			if(klant.getEmail() != null)
				email.setText(klant.getEmail());

			if(GuiPojo.klant.getAdresGegevens() != null) {
				Adres adres = GuiPojo.klant.getAdresGegevens();

				straatNaam.setText(adres.getStraatnaam());
				postcode.setText(adres.getPostcode());
				huisNummer.setText("" + adres.getHuisnummer());
				toevoeging.setText(adres.getToevoeging());
				woonplaats.setText(adres.getWoonplaats());
			}
		}
	}

	private void maakKlantAan(Stage klantStage){
		Adres adres = null;

		alleVeldenInvullen();

		if(!huisNummer.getText().equals("")){
			adres = new Adres(straatNaam.getText(), postcode.getText(), toevoeging.getText(),
					Integer.parseInt(huisNummer.getText()), woonplaats.getText());
		}
		Klant nieuweKlant = new Klant(0, voorNaam.getText(), achterNaam.getText(),
				tussenVoegsel.getText(), email.getText(), adres);
		adres = new Adres();
		adres.setWoonplaats(woonplaats.getText());
		adres.setPostcode(postcode.getText());
		adres.setHuisnummer(Integer.parseInt(huisNummer.getText()));
		adres.setStraatnaam(straatNaam.getText());
		adres.setToevoeging(toevoeging.getText());
		GuiPojo.klant.setAdresGegevens(adres);



		try {
			if(klant.getVoornaam() == null){
				GuiPojo.klant.setKlant_id(GuiPojo.klantDAO.nieuweKlant(nieuweKlant, 0));
			} else {
				GuiPojo.klantDAO.updateKlant(klant.getKlant_id(), nieuweKlant.getVoornaam(),
						nieuweKlant.getAchternaam(), nieuweKlant.getTussenvoegsel(), nieuweKlant.getEmail());
				if(GuiPojo.klant.getAdresGegevens().getAdres_id() != 0)
					GuiPojo.adresDAO.updateAdres(GuiPojo.klant.getAdresGegevens().getAdres_id(), adres);
				else
					GuiPojo.adresDAO.nieuwAdres(klant.getKlant_id(), GuiPojo.klant.getAdresGegevens());
			}
			klantStage.close();

		} catch (GeneriekeFoutmelding e) {
			new ErrorBox().setMessageAndStart(e.getMessage());
		}
	}

	private void alleVeldenInvullen(){
		if(voorNaam.getText().isEmpty())
			voorNaam.setText("");
		if(achterNaam.getText().isEmpty())
			achterNaam.setText("");
		if(tussenVoegsel.getText().isEmpty())
			tussenVoegsel.setText("");
		if(email.getText().isEmpty())
			email.setText("");

		if(straatNaam.getText().isEmpty())
			straatNaam.setText("");
		if(postcode.getText().isEmpty())
			postcode.setText("");
		if(toevoeging.getText().isEmpty())
			toevoeging.setText("");
		if(huisNummer.getText().isEmpty())
			huisNummer.setText("");
		if(woonplaats.getText().isEmpty())
			woonplaats.setText("");

	}


}