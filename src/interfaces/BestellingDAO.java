package interfaces;

import java.sql.ResultSet;

public interface BestellingDAO {
	//Create
	public void nieuweBestelling(int klantId, model.Artikel a1, model.Artikel a2, model.Artikel a3);
	
	//Read
	public ResultSet getBestellingOpKlantGegevens(int klantId);
	public ResultSet getBestellingOpBestelling(int bestellingId);
	
	//Update
	public void updateBestelling(int bestellingId, model.Artikel a1);
	public void updateBestelling(int bestellingId, model.Artikel a1, model.Artikel a2);
	public void updateBestelling(int bestellingId, model.Artikel a1, model.Artikel a2, model.Artikel a3);
	
	//Delete
	public void verwijderAlleBestellingenKlant(int klantId);
	public void verwijderEnkeleBestelling(int bestellingId);
}
