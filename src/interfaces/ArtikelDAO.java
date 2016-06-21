package interfaces;



import java.util.Iterator;
import java.util.Map.Entry;

import exceptions.RSVIERException;
import model.Artikel;

/**
 * @author DouweJongeneel
 */
public interface ArtikelDAO {

	//Create
	public int nieuwArtikel(Artikel aNieuw) throws RSVIERException;

	//Read
	public Artikel getArtikel(int artikel_id) throws RSVIERException;

	//Update
	public void updateArtikel(int artikel_id, Artikel aNieuw) throws RSVIERException;
	
	//Delete
	public void verwijderArtikel(int artikel_id) throws RSVIERException;
	
}
