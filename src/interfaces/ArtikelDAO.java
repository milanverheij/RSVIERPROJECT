package interfaces;



import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import exceptions.RSVIERException;
import model.Artikel;

/**
 * @author DouweJongeneel
 */
public interface ArtikelDAO {

	//Create
	public void nieuwArtikelOpBestelling(long bestelling_id, Artikel aNieuw) throws RSVIERException;

	//Read
	public Artikel getArtikelOpBestelling(long bestelling_id, int artikelNummer) throws RSVIERException;
	public Iterator<Artikel> getAlleArtikelenOpBestelling(long bestelling_id) throws RSVIERException;
	public Iterator<Entry<Artikel, Integer>> getAlleArtikelen() throws RSVIERException;

	//Update
	public void updateArtikelOpBestelling(long bestelling_id, int artikelNummer, Artikel aNieuw) throws RSVIERException;
	public void updateArtikelOpBestelling(long bestelling_id, Artikel aOud, Artikel aNieuw) throws RSVIERException;
	public void updateAlleArtikelenOpBestelling(long bestelling_id, Artikel a1, Artikel a2, Artikel a3) throws RSVIERException;
	public void updateArtikelen(Artikel aNieuw) throws RSVIERException;
	
	//Delete
	public void verwijderArtikelVanBestelling(long bestelling_id, Artikel a) throws RSVIERException;
	public void verwijderAlleArtikelenVanBestelling(long bestelling_id) throws RSVIERException;
	
}
