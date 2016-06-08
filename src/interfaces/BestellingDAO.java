package interfaces;

import java.util.Iterator;
import model.Artikel;
import model.Bestelling;

public interface BestellingDAO {
	//Create
	public void nieuweBestelling(long klantId, Artikel a1, Artikel a2, Artikel a3);
	public void nieuweBestelling(long klantId, Artikel a1, Artikel a2);
	public void nieuweBestelling(long klantId, Artikel a1);
	public void nieuweBestelling(Bestelling bestelling);
	//Read
	public Iterator<Bestelling> getBestellingOpKlantGegevens(long klantId);
	public model.Bestelling getBestellingOpBestelling(long bestellingId);
	
	//Update
	public void updateBestelling(long bestellingId, Artikel a1);
	public void updateBestelling(long bestellingId, Artikel a1, Artikel a2);
	public void updateBestelling(long bestellingId, Artikel a1, Artikel a2, Artikel a3);
	
	//Delete
	public void verwijderAlleBestellingenKlant(long klantId);
	public void verwijderEnkeleBestelling(long bestellingId);
}
