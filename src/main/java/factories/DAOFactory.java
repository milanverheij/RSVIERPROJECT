package factories;

import exceptions.GeneriekeFoutmelding;
import firebird.AbstractDAOFireBird;
import interfaces.AdresDAO;
import interfaces.ArtikelDAO;
import interfaces.BestellingDAO;
import interfaces.KlantDAO;
import mysql.AbstractDAOMySQL;

/**
 * @author Milan_Verheij
 * <p>
 * Deze abstract factory verzorgt de creatie van de juiste type concrete
 * DAO factory op basis van de database keuze.
 */

public abstract class DAOFactory {

	/**
	 * Op basis van een keuze qua database maakt de factory de juiste fabriek aan.
	 *
	 * @param s De keuze van het databasetype in String formaat.
	 * @return Geeft een concrete DAO fabriek terug.
	 */
	public static DAOFactory getDAOFactory(String s, String connPoolKeuze) throws GeneriekeFoutmelding {
		if(s.equals("MySQL")) {
			AbstractDAOMySQL.setConnPool(ConnectionPoolFactory.getConnectionPool(connPoolKeuze, "MySQL"));
			return new DAOFactoryMySQL();
		}
		else if(s.equals("FireBird")) {
			AbstractDAOFireBird.setConnPool(ConnectionPoolFactory.getConnectionPool(connPoolKeuze, "FireBird"));
			return new DAOFactoryFireBird();
		}
		else
			return null;
	}

	/**
	 * De methode die geimplementeerd dient te worden door de concrete fabriek
	 * om een KlantDAO te maken.
	 *
	 * @return Een KlantDAO van het eerder gekozen database-type.
	 */
	public abstract KlantDAO getKlantDAO() throws GeneriekeFoutmelding;

	/**
	 * De methode die geimplementeerd dient te worden door de concrete fabriek
	 * om een AdresDAO te maken.
	 *
	 * @return Een AdresDAO van het eerder gekozen database-type.
	 */
	public abstract AdresDAO getAdresDAO() throws GeneriekeFoutmelding;

	/**
	 * De methode die geimplementeerd dient te worden door de concrete fabriek
	 * om een BestellingDAO te maken.
	 *
	 * @return Een BestellingDAO van het eerder gekozen database-type.
	 */
	public abstract BestellingDAO getBestellingDAO() throws GeneriekeFoutmelding;

	/**
	 * De methode die geimplementeerd dient te worden door de concrete fabriek
	 * om een ArtikelDAO te maken.
	 *
	 * @return Een ArtikelDAO van het eerder gekozen database-type.
	 */
	public abstract ArtikelDAO getArtikelDAO() throws GeneriekeFoutmelding;
}
