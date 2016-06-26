package interfaces;



import java.util.LinkedHashSet;
import exceptions.GeneriekeFoutmelding;
import model.Artikel;

/**
 * @author DouweJongeneel
 */
public interface ArtikelDAO {

	//Create
	public int nieuwArtikel(Artikel aNieuw) throws GeneriekeFoutmelding;

	//Read
	public Artikel getArtikel(int artikelId) throws GeneriekeFoutmelding;
	public LinkedHashSet<Artikel> getAlleArtikelen(boolean artikelActief) throws GeneriekeFoutmelding;

	//Update
	public void updateArtikel(int artikelId, Artikel aNieuw) throws GeneriekeFoutmelding;
	
	//Delete
	public void verwijderArtikel(long artikelId) throws GeneriekeFoutmelding;
	
}
