package gui.gui;

import gui.bewerkingen.KlantGUIBewerkingen;
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

public class KlantGui extends Application{
    public TextField voorNaam = new TextField("");
    public TextField tussenVoegsel = new TextField("");
    public TextField achterNaam = new TextField("");
    public TextField email = new TextField("");
    public TextField straatNaam = new TextField("");
    public TextField postcode = new TextField("");
    public TextField huisNummer = new TextField("");
    public TextField toevoeging = new TextField("");
    public TextField woonplaats = new TextField("");

    Label error = new Label();

    KlantGUIBewerkingen klantGUIBewerkingen = new KlantGUIBewerkingen();

    public void start(Stage klantStage) throws Exception {

        GridPane grid = new GridPane();

        final BooleanProperty eersteKeer = new SimpleBooleanProperty(true); // Of de stage de eerste keer geladen word
        setFocus(grid, eersteKeer);

        klantGUIBewerkingen.populateTextFields();
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

        maakAan.setOnAction(e -> klantGUIBewerkingen.maakKlantAan(klantStage));
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
}