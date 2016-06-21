package interfaces;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import exceptions.RSVIERException;
import model.Artikel;
import model.Bestelling;

public interface BestellingDAO {
	//Create
	public long nieuweBestelling(Bestelling bestelling) throws SQLException, RSVIERException;
	public long nieuweBestelling(long klantId, List<Artikel> artikelLijst) throws SQLException, RSVIERException;

	//Read
	public Iterator<Bestelling> getBestellingOpKlantId(long klantId) throws SQLException, RSVIERException;
	public Iterator<Bestelling> getBestellingOpBestellingId(long bestellingId) throws SQLException, RSVIERException;
	
	//Update
	public void updateBestelling(Bestelling bestelling) throws SQLException, RSVIERException;
	
	//Delete
	public void verwijderAlleBestellingenKlant(long klantId) throws SQLException, RSVIERException;
	public void verwijderEnkeleBestelling(long bestellingId) throws SQLException, RSVIERException;

}
