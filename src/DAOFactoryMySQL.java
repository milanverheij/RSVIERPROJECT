//import interfaces.AdresDAO;
//import interfaces.ArtikelDAO;
import interfaces.BestellingDAO;
import interfaces.KlantDAO;

/**
 * Deze concrete factory van het type MySQL maakt DAO's
 * aan voor de database MySQL en geeft deze terug aan de gebruiker.
 */

public class DAOFactoryMySQL extends DAOFactory{


	/**
	 * Methode om de een KlantDAO te maken.
	 *
	 * @return een KlantDAO van het MySQL-type
     */
	@Override
	public KlantDAO getKlantDAO() {
		return new mysql.KlantDAOMySQL();
	}

//	@Override
//	public AdresDAO getAdresDAO() {
//		return new mysql.AdresDAOMySQL();
//	}

	/**
	 * Methode om een BestellingDAO te maken.
	 *
	 * @return een BestellingDAO van het MySQL-type.
     */
	@Override
	public BestellingDAO getBestellingDAO() {
		return new mysql.BestellingDAOMySQL();
	}

//	@Override
//	public ArtikelDAO getArtikelDAO() {
//		return new mysql.ArtikelDAOMySQL();
//	}

}
