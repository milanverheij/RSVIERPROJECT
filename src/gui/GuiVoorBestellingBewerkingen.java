package gui;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import exceptions.RSVIERException;
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
	ArtikelDAOMySQL artikelDao = new ArtikelDAOMySQL();

	Stage bestellingStage;
	ListView<String> artikelenListView;
	Button okeButton;
	Button cancelButton;
	Label issues = new Label("");

	boolean aanpassen;

	ArrayList<Artikel> artikelArrayList;
	ArrayList<TextField> textFieldArrayList;
	ListView<Long> bestellingListView;

	Bestelling bestelling;

	long klantId;

	public void setKlantIdAndRun(long klantId) throws Exception{
		this.klantId = klantId;
		start(new Stage());
	}

	public void setKlantIdAndRun(long klantId, ListView<Long> bestellingListView) throws Exception {
		this.klantId = klantId;
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
			aanpassen = true;
		}

		if(bestelling == null){
			bestelling = new Bestelling();
			bestellingStage.setTitle("Nieuwe bestelling");
			aanpassen = false;
		}

		GridPane bestelGrid = populateBestelGrid();

		bestellingStage.setScene(new Scene(bestelGrid));
		bestellingStage.show();
	}

	private void setArtikelAantallen(){
		LinkedHashMap<Artikel, Integer> map = bestelling.getArtikelLijst();
		int size = artikelArrayList.size();
		for(int it = 0; it < size; it++){
			if(map.get(artikelArrayList.get(it)) != null)
				textFieldArrayList.get(it).setText("" + map.get(artikelArrayList.get(it)));
		}
	}

	private void maakButtons(){
		okeButton = new Button("Oke");
		okeButton.setOnAction(e -> maakbestelling());

		cancelButton = new Button("Cancel");
		cancelButton.setOnAction(e -> bestellingStage.close());
	}

	private void maakbestelling(){
		if(checkAantal()){
			bestelling.setKlant_id(1);
			
			if(bestelling.getArtikelLijst() != null)
				bestelling.getArtikelLijst().clear();
			
			voegArtikelenAanBestellingToe();
			BestellingDAOMySQL bestelDAO = new BestellingDAOMySQL();

			try {
				if(aanpassen){
					bestelDAO.updateBestelling(bestelling);
				}else{
					long bestellingId = bestelDAO.nieuweBestelling(bestelling);
					bestelling.setBestelling_id(bestellingId);
					bestellingListView.getItems().add(bestellingId);
					GuiPojo.bestellingLijst.put(bestellingId, bestelling);
				}
				bestellingStage.close();
			}catch(SQLException | RSVIERException e) {
				e.printStackTrace();
			}catch(Exception e){
				e.printStackTrace();
			}
		}else{
			new ErrorBox().setMessageAndStart("Incorrect aantal artikelen");
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

	private boolean checkAantal(){
		long count = 0;
		for(TextField t : textFieldArrayList){
			count += Long.parseLong(t.getText());
		}
		if(count < 4 && count > 0)
			return true;
		else
			return false;
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

			bestelGrid.add(new Label(artikel.getArtikel_naam()), 0, x);
			bestelGrid.add(new Label("" + artikel.getArtikel_prijs()), 1, x);

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
				artikelenListView.getItems().add(artikel.getArtikel_naam());

				artikelArrayList.add(artikel);
				textFieldArrayList.add(new TextField("0"));
			}
		} catch (RSVIERException e) {
			e.printStackTrace();
		}
	}

	protected void setBestelling(Bestelling bestelling){
		this.bestelling = bestelling;
	}


}
