package gui.interfaces;

import java.sql.SQLException;
import java.util.ArrayList;
import exceptions.GeneriekeFoutmelding;
import javafx.scene.control.ListView;
import model.Artikel;
import model.Bestelling;
import model.Klant;

public interface GuiBewerkingen {
	void leegKlantBestellingArtikel();

	void resetArtikelVariabelen();

	void zoekKlant(ListView<String> klantListView, Klant klant);

	void zoekBestelling(String bron, ListView<Long> bestellingListView, String klantIdField, String bestellingIdField, boolean actieveItems) throws SQLException;

	void populateBestellingListView(ListView<Long> bestellingListView, ArrayList<Bestelling> list) throws GeneriekeFoutmelding;

	void populateBestellingListView(ListView<Long> bestellingListView) throws GeneriekeFoutmelding;

	void getItemVanKlantenLijst(ListView<String> klantListView);

	void getItemVanBestellingLijst(long selectedItem);

	void setArtikelLijstInNieuweBestelling();

	void updateArtikel(Artikel nieuwArtikel);

	void getItemVanArtikelLijst(int index);

	void updateBestelling() throws SQLException, GeneriekeFoutmelding;

	void verwijderEnkeleBestelling(ListView<Long> bestellingListView) throws GeneriekeFoutmelding, SQLException;
}