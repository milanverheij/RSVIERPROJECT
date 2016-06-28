package interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import exceptions.GeneriekeFoutmelding;
import javafx.scene.control.ListView;
import model.Artikel;
import model.Bestelling;

public interface GuiBewerkingen {
	void leegKlantBestellingArtikel();

	void resetArtikelVariabelen();

	void zoekKlant(ListView<String> klantListView, String klantId, String voorNaam, String achterNaam, String tussenVoegsel, String email);

	void zoekBestelling(String bron, ListView<Long> bestellingListView, String klantIdField, String bestellingIdField) throws SQLException;

	void populateBestellingListView(ListView<Long> bestellingListView, Iterator<Bestelling> it);

	void populateBestellingListView(ListView<Long> bestellingListView);

	void verwerkKlantResultSet(ResultSet rs, ListView<String> klantListView) throws SQLException, GeneriekeFoutmelding;

	void getItemVanKlantenLijst(ListView<String> klantListView);

	void getItemVanBestellingLijst(long selectedItem);

	void setArtikelLijstInNieuweBestelling();

	void updateArtikel(Artikel nieuwArtikel);

	void getItemVanArtikelLijst(int index);

	void updateBestelling() throws SQLException, GeneriekeFoutmelding;

	void verwijderEnkeleBestelling() throws GeneriekeFoutmelding, SQLException;
}
