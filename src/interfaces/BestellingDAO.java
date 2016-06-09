package interfaces;

import java.sql.SQLException;
import java.util.Iterator;
import model.Artikel;
import model.Bestelling;

public interface BestellingDAO {
	//Create
	public void nieuweBestelling(long klantId, Artikel a1, Artikel a2, Artikel a3) throws SQLException;
	public void nieuweBestelling(long klantId, Artikel a1, Artikel a2) throws SQLException;
	public void nieuweBestelling(long klantId, Artikel a1) throws SQLException;
	public void nieuweBestelling(Bestelling bestelling) throws SQLException;

	//Read
	public Iterator<Bestelling> getBestellingOpKlantGegevens(long klantId);
	public Iterator<Bestelling> getBestellingOpBestelling(long bestellingId);
	
	//Update
	public void updateBestelling(long bestellingId, Artikel a1) throws SQLException;
	public void updateBestelling(long bestellingId, Artikel a1, Artikel a2) throws SQLException;
	public void updateBestelling(long bestellingId, Artikel a1, Artikel a2, Artikel a3) throws SQLException;
	public void updateBestelling(Bestelling bestelling) throws SQLException;
	
	//Delete
	public void verwijderAlleBestellingenKlant(long klantId);
	public void verwijderEnkeleBestelling(long bestellingId);

}
