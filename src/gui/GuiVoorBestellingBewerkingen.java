package gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import exceptions.GeneriekeFoutmelding;
import interfaces.ArtikelDAO;
import interfaces.BestellingDAO;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.Artikel;
import model.Bestelling;
import model.GuiPojo;
import mysql.ArtikelDAOMySQL;
import mysql.BestellingDAOMySQL;

public class GuiVoorBestellingBewerkingen extends Application{
	ArtikelDAO artikelDao;
	BestellingDAO bestelDao;

	Stage bestellingStage;
	ListView<String> artikelenListView;
	Button okeButton;
	Button cancelButton;
	Label issues = new Label("");

	boolean bestellingAanpassen;

	ArrayList<Artikel> artikelArrayList;
	ArrayList<TextField> textFieldArrayList;
	ListView<Long> bestellingListView;

	Bestelling bestelling;

	long klantId;

	// Voor aanpassen bestelling
	public void setAndRun(long klantId, BestellingDAO bestelDao, ArtikelDAO artikelDao) throws Exception{
		this.klantId = klantId;
		this.artikelDao = artikelDao;
		this.bestelDao = bestelDao;

		start(new Stage());
	}

	// Voor nieuwe bestelling
	public void setAndRun(long klantId, ListView<Long> bestellingListView, BestellingDAO bestelDao, ArtikelDAO artikelDao) throws Exception {
		this.klantId = klantId;
		this.artikelDao = artikelDao;
		this.bestelDao = bestelDao;

		this.bestellingListView = bestellingListView;
		start(new Stage());
	}

	@Override
	public void start(Stage bestellingStage) throws Exception {
		this.bestellingStage = bestellingStage;
		maakButtons();
		populateArrayListsEnListView();

		if(bestelling != null){
			setArtikelAantallen();
			bestellingStage.setTitle("Bestelling aanpassen");
			bestellingAanpassen = true;
		}

		if(bestelling == null){
			bestelling = new Bestelling();
			bestellingStage.setTitle("Nieuwe bestelling");
			bestellingAanpassen = false;
		}

		GridPane bestelGrid = populateBestelGrid();

		bestellingStage.setScene(new Scene(bestelGrid));
		bestellingStage.show();
	}

	private void setArtikelAantallen(){
		ArrayList<Artikel> artikelLijst = bestelling.getArtikelLijst();
		int count = 0;
		for(Artikel artikel : artikelLijst){
			textFieldArrayList.get(count).setText("" + artikel.getAantalBesteld());
			count++;
		}
	}

	private void maakButtons(){
		okeButton = new Button("Oke");
		okeButton.setOnAction(e -> maakbestelling());

		cancelButton = new Button("Cancel");
		cancelButton.setOnAction(e -> bestellingStage.close());
	}

	private void maakbestelling(){
		bestelling.setKlant_id(klantId);

		if(bestelling.getArtikelLijst() != null)
			bestelling.getArtikelLijst().clear();

		voegArtikelenAanBestellingToe();

		try {
			if(bestellingAanpassen){
				bestelDao.updateBestelling(bestelling);
			}else{
				long bestellingId = bestelDao.nieuweBestelling(bestelling);
				bestelling.setBestelling_id(bestellingId);
				bestellingListView.getItems().add(bestellingId);
				GuiPojo.bestellingLijst.put(bestellingId, bestelling);
			}
			bestellingStage.close();
		}catch(GeneriekeFoutmelding e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void voegArtikelenAanBestellingToe(){
		int size = artikelArrayList.size();

		for(int x = 0; x < size; x++){
			TextField t = textFieldArrayList.get(x);

			if(!(t.getText().equals("0") || t.getText().isEmpty())){
				int aantal = Integer.parseInt(t.getText());

				for(int aantalIt = 0; aantalIt < aantal; aantalIt++)
					bestelling.voegArtikelToe(artikelArrayList.get(x));
			}
		}
	}

	private GridPane populateBestelGrid(){
		GridPane bestelGrid = new GridPane();
		bestelGrid.setVgap(2);
		bestelGrid.setHgap(5);
		bestelGrid.setPadding(new Insets(4));

		bestelGrid.add(new Label("Artikel"), 0, 0);
		bestelGrid.add(new Label("Prijs"), 1, 0);
		bestelGrid.add(new Label("Aantal"), 2, 0);

		int x;
		for(x = 1; x <= artikelArrayList.size(); x++){
			Artikel artikel = artikelArrayList.get(x - 1);

			bestelGrid.add(new Label(artikel.getArtikelNaam()), 0, x);
			bestelGrid.add(new Label("" + artikel.getArtikelPrijs()), 1, x);

			bestelGrid.add(textFieldArrayList.get(x - 1), 2, x);
		}

		bestelGrid.add(issues, 0, x + 1);

		HBox buttonBox = new HBox();
		buttonBox.getChildren().addAll(okeButton, cancelButton);
		bestelGrid.add(buttonBox, 0, x + 2);
		return bestelGrid;
	}

	private void populateArrayListsEnListView(){
		try{
			Iterator<Entry<Artikel, Integer>> artikelIterator = artikelDao.getAlleArtikelen();
			Artikel artikel;

			artikelenListView = new ListView<String>();
			artikelArrayList = new ArrayList<Artikel>();
			textFieldArrayList = new ArrayList<TextField>();

			while(artikelIterator.hasNext()){
				artikel = artikelIterator.next().getKey();
				artikelenListView.getItems().add(artikel.getArtikelNaam());

				artikelArrayList.add(artikel);
				textFieldArrayList.add(new TextField("0"));
			}
		} catch (GeneriekeFoutmelding e) {
			e.printStackTrace();
		}
	}

	protected void setBestelling(Bestelling bestelling){
		this.bestelling = bestelling;
	}
}
