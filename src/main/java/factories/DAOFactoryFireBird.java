package factories;

import exceptions.GeneriekeFoutmelding;
import firebird.AdresDAOFireBird;
import firebird.ArtikelDAOFireBird;
import firebird.BestellingDAOFireBird;
import firebird.KlantDAOFireBird;
import interfaces.AdresDAO;
import interfaces.ArtikelDAO;
import interfaces.BestellingDAO;
import interfaces.KlantDAO;

/**
 * @author Milan_Verheij
 * <p>
 * Deze concrete factory van het type FireBird maakt DAO's
 * aan voor de database FireBird(DBKeuze 2) en geeft deze terug aan de gebruiker.
 *
 */

public class DAOFactoryFireBird extends DAOFactory{

	/**
	 * Methode om de een KlantDAO te maken.
	 *
	 * @return een KlantDAO van het FireBird-type(DBKeuze 2).
     */
	@Override
	public KlantDAO getKlantDAO() throws GeneriekeFoutmelding {
		return new KlantDAOFireBird();
	}

	/**
	 * Methode om de een AdresDAO te maken.
	 *
	 * @return een AdresDAO van het FireBird-type(DBKeuze 2).
     */
	@Override
	public AdresDAO getAdresDAO() throws GeneriekeFoutmelding {
		return new AdresDAOFireBird();
	}

	/**
	 * Methode om de een BestellingDAO te maken.
	 *
	 * @return een BestellingDAO van het FireBird-type(DBKeuze 2).
	 */
	@Override
	public BestellingDAO getBestellingDAO() throws GeneriekeFoutmelding {
		return new BestellingDAOFireBird();
	}

	/**
	 * Methode om de een ArtikelDAO te maken.
	 *
	 * @return een ArtikelDAO van het FireBird-type(DBKeuze 2).
	 */
	@Override
	public ArtikelDAO getArtikelDAO() throws GeneriekeFoutmelding {
		return new ArtikelDAOFireBird();
	}
}
