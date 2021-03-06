package database.factories;

import database.interfaces.AdresDAO;
import database.interfaces.ArtikelDAO;
import database.interfaces.BestellingDAO;
import database.interfaces.KlantDAO;
import database.daos.mysql.AdresDAOMySQL;
import database.daos.mysql.ArtikelDAOMySQL;
import database.daos.mysql.BestellingDAOMySQL;
import database.daos.mysql.KlantDAOMySQL;
import exceptions.GeneriekeFoutmelding;

/**
 * @author Milan_Verheij
 * <p>
 * Deze concrete factory van het type MySQL maakt DAO's
 * aan voor de database MySQL en geeft deze terug aan de gebruiker.
 *
 */

public class DAOFactoryMySQL extends DAOFactory{

	/**
	 * Methode om de een KlantDAO te maken.
	 *
	 * @return een KlantDAO van het MySQL-type(DBKeuze 1).
     */
	@Override
	public KlantDAO getKlantDAO() throws GeneriekeFoutmelding {
		return new KlantDAOMySQL();
	}

	/**
	 * Methode om de een AdresDAO te maken.
	 *
	 * @return een AdresDAO van het MySQL-type(DBKeuze 1).
     */
	@Override
	public AdresDAO getAdresDAO() throws GeneriekeFoutmelding {
		return new AdresDAOMySQL();
	}

    /**
     * Methode om de een BestellingDAO te maken.
     *
     * @return een BestellingDAO van het MySQL-type(DBKeuze 1).
     */
	@Override
	public BestellingDAO getBestellingDAO() throws GeneriekeFoutmelding {
		return new BestellingDAOMySQL();
	}

    /**
     * Methode om de een ArtikelDAO te maken.
	 *
     * @return een ArtikelDAO van het MySQL-type(DBKeuze 1).
     */
	@Override
	public ArtikelDAO getArtikelDAO() throws GeneriekeFoutmelding {
		return new ArtikelDAOMySQL();
	}

}
