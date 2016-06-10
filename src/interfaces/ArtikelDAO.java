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
	public void nieuwArtikelOpBestelling(long bestelling_id, Artikel a) throws RSVIERException;

	//Read
	public Artikel getArtikelOpBestelling(long bestelling_id, int artikelNummer) throws RSVIERException;
	public Iterator<Artikel> getAlleArtikelenOpBestelling(long bestelling_id) throws RSVIERException;
	public Iterator<Entry<Artikel, Integer>> getAlleArtikelen() throws RSVIERException;
	//returned een map met alle unieke artikelen + aantal

	//Update
	public void updateArtikelOpBestelling(long bestelling_id, int artikelNummer, Artikel a1) throws RSVIERException;
	public void updateAlleArtikelenOpBestelling(long bestelling_id, Artikel a1, Artikel a2, Artikel a3) throws RSVIERException;

	//Delete
	public void verwijderArtikelVanBestelling(long bestelling_id, int artikelNummer) throws RSVIERException;
	public void verwijderAlleArtikelenVanBestelling(long bestelling_id) throws RSVIERException;
}
