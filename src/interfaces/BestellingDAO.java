package interfaces;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import exceptions.GeneriekeFoutmelding;
import model.Artikel;
import model.Bestelling;

public interface BestellingDAO {
	//Create
	public long nieuweBestelling(Bestelling bestelling) throws SQLException, GeneriekeFoutmelding;
	public long nieuweBestelling(long klantId, List<Artikel> artikelLijst) throws SQLException, GeneriekeFoutmelding;

	//Read
	public Iterator<Bestelling> getBestellingOpKlantId(long klantId) throws SQLException, GeneriekeFoutmelding;
	public Iterator<Bestelling> getBestellingOpBestellingId(long bestellingId) throws SQLException, GeneriekeFoutmelding;
	
	//Update
	public void updateBestelling(Bestelling bestelling) throws SQLException, GeneriekeFoutmelding;
	
	//Delete
	public void verwijderAlleBestellingenKlant(long klantId) throws SQLException, GeneriekeFoutmelding;
	public void verwijderEnkeleBestelling(long bestellingId) throws SQLException, GeneriekeFoutmelding;

}
