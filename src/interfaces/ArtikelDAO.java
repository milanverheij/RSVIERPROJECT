package interfaces;



import exceptions.GeneriekeFoutmelding;
import model.Artikel;

/**
 * @author DouweJongeneel
 */
public interface ArtikelDAO {

	//Create
	public int nieuwArtikel(Artikel aNieuw) throws GeneriekeFoutmelding;

	//Read
	public Artikel getArtikel(int artikel_id) throws GeneriekeFoutmelding;

	//Update
	public void updateArtikel(int artikel_id, Artikel aNieuw) throws GeneriekeFoutmelding;
	
	//Delete
	public void verwijderArtikel(int artikel_id) throws GeneriekeFoutmelding;
	
}
