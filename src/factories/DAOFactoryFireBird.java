package factories;

import exceptions.RSVIERException;
import firebird.AbstractDAOFireBird;
import firebird.AdresDAOFireBird;
import firebird.ArtikelDAOFireBird;
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
 * Stelt daarnaast de AbstractDAOFireBird in op de gekozen Connection Pool.
 */

public class DAOFactoryFireBird extends DAOFactory{

	/**
	 * Methode om de een KlantDAO te maken.
	 * Stelt daarnaast de AbstractDAOFireBird in op de gekozen Connection Pool.
	 *
	 * @param connPoolKeuze Keuze voor het type connectionPool, zie ConnectionPoolFactory.
	 * @return een KlantDAO van het FireBird-type(DBKeuze 2).
     */
	@Override
	public KlantDAO getKlantDAO(String connPoolKeuze) throws RSVIERException {
		AbstractDAOFireBird.setConnPool(ConnectionPoolFactory.getConnectionPool(connPoolKeuze, 2));
		return new KlantDAOFireBird();
	}

	/**
	 * Methode om de een AdresDAO te maken.
	 * Stelt daarnaast de AbstractDAOFireBird in op de gekozen Connection Pool.
	 *
	 * @param connPoolKeuze Keuze voor het type connectionPool, zie ConnectionPoolFactory.
	 * @return een AdresDAO van het FireBird-type(DBKeuze 2).
     */
	@Override
	public AdresDAO getAdresDAO(String connPoolKeuze) throws RSVIERException {
		AbstractDAOFireBird.setConnPool(ConnectionPoolFactory.getConnectionPool(connPoolKeuze, 2));
		return new AdresDAOFireBird();
	}

	/**
	 * Methode om de een BestellingDAO te maken.
	 * Stelt daarnaast de AbstractDAOFireBird in op de gekozen Connection Pool.
	 *
	 * @param connPoolKeuze Keuze voor het type connectionPool, zie ConnectionPoolFactory.
	 * @return een BestellingDAO van het FireBird-type(DBKeuze 2).
	 */
	@Override
	public BestellingDAO getBestellingDAO(String connPoolKeuze) throws RSVIERException {
		AbstractDAOFireBird.setConnPool(ConnectionPoolFactory.getConnectionPool(connPoolKeuze, 2));
		return null;
	}

	/**
	 * Methode om de een ArtikelDAO te maken.
	 * Stelt daarnaast de AbstractDAOFireBird in op de gekozen Connection Pool.
	 *
	 * @param connPoolKeuze Keuze voor het type connectionPool, zie ConnectionPoolFactory.
	 * @return een ArtikelDAO van het FireBird-type(DBKeuze 2).
	 */
	@Override
	public ArtikelDAO getArtikelDAO(String connPoolKeuze) throws RSVIERException {
		AbstractDAOFireBird.setConnPool(ConnectionPoolFactory.getConnectionPool(connPoolKeuze, 2));
		return new ArtikelDAOFireBird();
	}

}
