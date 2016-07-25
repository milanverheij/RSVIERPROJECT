package database.interfaces;

import exceptions.GeneriekeFoutmelding;
import model.Artikel;
import model.Bestelling;

import java.util.ArrayList;
import java.util.List;

public interface BestellingDAO {

	//Create
	long nieuweBestelling(Bestelling bestelling) throws GeneriekeFoutmelding;
	long nieuweBestelling(long klantId, List<Artikel> artikelLijst, boolean isActief) throws GeneriekeFoutmelding;

	//Read
	ArrayList<Bestelling> getBestellingOpKlantId(long klantId, boolean bestellingActief) throws GeneriekeFoutmelding;
	ArrayList<Bestelling> getBestellingOpBestellingId(long bestellingId, boolean bestellingActief) throws GeneriekeFoutmelding;
	
	//Update
	void updateBestelling(Bestelling bestelling) throws GeneriekeFoutmelding;
	
	//Delete
	void verwijderAlleBestellingenKlant(long klantId) throws GeneriekeFoutmelding;
	void verwijderEnkeleBestelling(long bestellingId) throws GeneriekeFoutmelding;
	void setAlsInactiefAlleBestellingenKlant(long klantId) throws GeneriekeFoutmelding;
	void setEnkeleBestellingInactief(long bestellingId) throws GeneriekeFoutmelding;
}
