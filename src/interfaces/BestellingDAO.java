package interfaces;

import java.sql.SQLException;
import java.util.Iterator;

import exceptions.RSVIERException;
import model.Artikel;
import model.Bestelling;

public interface BestellingDAO {
	//Create
	public long nieuweBestelling(long klantId, Artikel a1, Artikel a2, Artikel a3) throws SQLException, RSVIERException;
	public long nieuweBestelling(long klantId, Artikel a1, Artikel a2) throws SQLException, RSVIERException;
	public long nieuweBestelling(long klantId, Artikel a1) throws SQLException, RSVIERException;
	public long nieuweBestelling(Bestelling bestelling) throws SQLException, RSVIERException;

	//Read
	public Iterator<Bestelling> getBestellingOpKlantGegevens(long klantId) throws RSVIERException;
	public Iterator<Bestelling> getBestellingOpBestelling(long bestellingId) throws RSVIERException;
	
	//Update
	public void updateBestelling(long bestellingId, Artikel a1) throws SQLException, RSVIERException;
	public void updateBestelling(long bestellingId, Artikel a1, Artikel a2) throws SQLException, RSVIERException;
	public void updateBestelling(long bestellingId, Artikel a1, Artikel a2, Artikel a3) throws SQLException, RSVIERException;
	public void updateBestelling(Bestelling bestelling) throws SQLException, RSVIERException;
	
	//Delete
	public long verwijderAlleBestellingenKlant(long klantId) throws RSVIERException;
	public void verwijderEnkeleBestelling(long bestellingId) throws RSVIERException;

}
