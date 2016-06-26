package interfaces;

import java.util.Iterator;
import java.util.List;

import exceptions.GeneriekeFoutmelding;
import model.Artikel;
import model.Bestelling;

public interface BestellingDAO {
	//Create
	public long nieuweBestelling(Bestelling bestelling) throws GeneriekeFoutmelding;
	public long nieuweBestelling(long klantId, List<Artikel> artikelLijst, boolean isActief) throws GeneriekeFoutmelding;

	//Read
	public Iterator<Bestelling> getBestellingOpKlantId(long klantId, boolean bestellingActief) throws GeneriekeFoutmelding;
	public Iterator<Bestelling> getBestellingOpBestellingId(long bestellingId, boolean bestellingActief) throws GeneriekeFoutmelding;
	
	//Update
	public void updateBestelling(Bestelling bestelling) throws GeneriekeFoutmelding;
	
	//Delete
	public void verwijderAlleBestellingenKlant(long klantId) throws GeneriekeFoutmelding;
	public void verwijderEnkeleBestelling(long bestellingId) throws GeneriekeFoutmelding;
	public void setAlsInactiefAlleBestellingenKlant(long klantId) throws GeneriekeFoutmelding;
	public void setEnkeleBestellingInactief(long bestellingId) throws GeneriekeFoutmelding;

}
