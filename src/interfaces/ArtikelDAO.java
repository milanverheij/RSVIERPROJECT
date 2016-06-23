package interfaces;

import java.util.LinkedHashSet;
import exceptions.RSVIERException;
import model.Artikel;

/**
 * @author DouweJongeneel
 */
public interface ArtikelDAO {

	//Create
	public int nieuwArtikel(Artikel aNieuw) throws RSVIERException;

	//Read
	public Artikel getArtikel(int artikelId) throws RSVIERException;
	public LinkedHashSet<Artikel> getAlleArtikelen(int artikelActief) throws RSVIERException;

	//Update
	public void updateArtikel(int artikelId, Artikel aNieuw) throws RSVIERException;
	
	//Delete
	public void verwijderArtikel(Artikel artikel) throws RSVIERException;
	
}
