package interfaces;

import java.util.LinkedHashSet;
import exceptions.GeneriekeFoutmelding;
import model.Artikel;

/**
 * @author DouweJongeneel
 */
public interface ArtikelDAO {

	//Create
	int nieuwArtikel(Artikel aNieuw) throws GeneriekeFoutmelding;

	//Read
	Artikel getArtikel(int artikelId) throws GeneriekeFoutmelding;
	LinkedHashSet<Artikel> getAlleArtikelen(boolean artikelActief) throws GeneriekeFoutmelding;

	//Update
	void updateArtikel(int artikelId, Artikel aNieuw) throws GeneriekeFoutmelding;
	
	//Delete
	void verwijderArtikel(int artikelId) throws GeneriekeFoutmelding;
	
}
