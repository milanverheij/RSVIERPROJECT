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
        GridPane artikelGrid = new GridPane();
        artikelGrid.setVgap(2);
        artikelGrid.setHgap(5);
        artikelGrid.setPadding(new Insets(4));

        artikelGrid.add(new Label("Straatnaam"), 0, 0);
        artikelGrid.add(new Label("Huisnummer"), 0, 1);
        artikelGrid.add(new Label("Toevoeging"), 0, 2);
        artikelGrid.add(new Label("Postcode"), 0, 3);
        artikelGrid.add(new Label("Woonplaats"), 0, 4);
        artikelGrid.add(adresActief, 0, 5);

        artikelGrid.add(straatNaamField, 1, 0);
        artikelGrid.add(huisnummerField, 1, 1);
        artikelGrid.add(toevoegingField, 1, 2);
        artikelGrid.add(postcodeField, 1, 3);
        artikelGrid.add(woonplaatsField, 1, 4);

        return artikelGrid;
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
        adres = adresArrayLijst.get(adresListView.getSelectionModel().getSelectedIndex());

        straatNaamField.setText(adres.getStraatnaam());
        huisnummerField.setText("" + adres.getHuisnummer());
        toevoegingField.setText("" + adres.getToevoeging());
        postcodeField.setText("" + adres.getPostcode());
        woonplaatsField.setText("" + adres.getWoonplaats());
        adresActief.setSelected((adres.getAdresActief().charAt(0) == '1') ? true : false);
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
        adres.setAdresId(0);
        adres.setStraatnaam(straatNaamField.getText());
        adres.setToevoeging(toevoegingField.getText());
        adres.setPostcode(postcodeField.getText());
        adres.setWoonplaats(woonplaatsField.getText());
        adres.setAdresActief((adresActief.isSelected() == true) ? "1" : "0");

        try {
            GuiPojo.adresDAO.nieuwAdres(0, adres); //TODO: KlantID moet hier nog naar toe gegooid worden.
        } catch (GeneriekeFoutmelding e) {
            e.printStackTrace();
            DeLogger.getLogger().error("Fout bij aanmaken nieuw adres", e.getCause());
            GuiPojo.errorBox.setMessageAndStart("Fout bij aanmaken nieuw adres: " + e.getMessage());
        }
    }

    private void updateAdres() {
        boolean prijsAangepast = false;
        boolean ietsAangepast = false;
//        BigDecimal prijs = new BigDecimal(adres.getText());

//        if(!artikel.getArtikelNaam().equals(naamField.getText())){
//            ietsAangepast = true;
//            artikel.setArtikelNaam(naamField.getText());
//        }
//        if((artikel.getArtikelPrijs()).compareTo(prijs) != 0){
//            prijsAangepast = true;
//            artikel.setArtikelPrijs(new BigDecimal(artikelPrijsField.getText()));
//        }
//        if(artikel.isInAssortiment() != inAssortiment.isSelected()){
//            ietsAangepast = true;
//            artikel.setInAssortiment(inAssortiment.isSelected());
//        }
//        if(artikel.getVerwachteLevertijd() != Integer.parseInt(verwachteLevertijdField.getText())){
//            ietsAangepast = true;
//            artikel.setVerwachteLevertijd(Integer.parseInt(verwachteLevertijdField.getText()));
//        }

        if(ietsAangepast || prijsAangepast){
            try {
                GuiPojo.adresDAO.updateAdres(adres.getAdresId(), adres);
            } catch (GeneriekeFoutmelding e) {
                e.printStackTrace();
                DeLogger.getLogger().error("Fout bij update adres", e.getCause());
                GuiPojo.errorBox.setMessageAndStart("Fout bij update adres: " + e.getMessage());
            }
        }
    }

}