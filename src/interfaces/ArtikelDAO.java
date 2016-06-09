package interfaces;



import java.util.Iterator;
import java.util.LinkedHashMap;
import model.Artikel;

/**
 * @author DouweJongeneel
 */
public interface ArtikelDAO {

	//Create
	public void nieuwArtikelOpBestelling(long bestelling_id, Artikel a);

	//Read
	public Artikel getArtikelOpBestelling(long bestelling_id, int artikelNummer);
	public Iterator<Artikel> getAlleArtikelenOpBestelling(long bestelling_id);
	public LinkedHashMap<Artikel, Integer> getAlleArtikelen(); //returned een map met alle unieke artikelen + aantal

	//Update
	public void updateArtikelOpBestelling(long bestelling_id, int artikelNummer, Artikel a1);

	//Delete
	public void verwijderArtikelVanBestelling(long bestelling_id, int artikelNummer, Artikel a1);
	public void verwijderAlleArtikelenVanBestelling(long bestelling_id);
}
