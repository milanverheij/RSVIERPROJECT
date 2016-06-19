package factories;

import exceptions.RSVIERException;
import interfaces.*;
import mysql.AbstractDAOMySQL;
import mysql.AdresDAOMySQL;
import mysql.KlantDAOMySQL;

/**
 * @author Milan_Verheij
 * <p>
 * Deze concrete factory van het type MySQL maakt DAO's
 * aan voor de database MySQL en geeft deze terug aan de gebruiker.
 *
 * Stelt daarnaast de AbstractDAOMySQL in op de gekozen Connection Pool.
 */

public class DAOFactoryMySQL extends DAOFactory{

	/**
	 * Methode om de een KlantDAO te maken.
	 * Stelt daarnaast de AbstractDAOMySQL in op de gekozen Connection Pool.
	 *
	 * @param connPoolKeuze Keuze voor het type connectionPool, zie ConnectionPoolFactory.
	 * @return een KlantDAO van het MySQL-type
     */
	@Override
	public KlantDAO getKlantDAO(String connPoolKeuze) throws RSVIERException {
		AbstractDAOMySQL.setConnPool(ConnectionPoolFactory.getConnectionPool(connPoolKeuze, 1));
		return new KlantDAOMySQL();
	}

	/**
	 * Methode om de een AdresDAO te maken.
	 * Stelt daarnaast de AbstractDAOMySQL in op de gekozen Connection Pool.
	 *
	 * @param connPoolKeuze Keuze voor het type connectionPool, zie ConnectionPoolFactory.
	 * @return een AdresDAO van het MySQL-type
     */
	@Override
	public AdresDAO getAdresDAO(String connPoolKeuze) throws RSVIERException {
		AbstractDAOMySQL.setConnPool(ConnectionPoolFactory.getConnectionPool(connPoolKeuze, 1));
		return new AdresDAOMySQL();
	}

	/**
	 * Methode om een BestellingDAO te maken.
	 *
	 * @return een BestellingDAO van het MySQL-type.
     */
	@Override
	public BestellingDAO getBestellingDAO() {
		return new mysql.BestellingDAOMySQL();
	}

	/**
	 * Methode om een ArtikelDAO te maken;
	 * @return een ArtikelDAO van het MySQL-type
     */
	@Override
	public ArtikelDAO getArtikelDAO() {
		return new mysql.ArtikelDAOMySQL();
	}

}
