import interfaces.AdresDAO;
import interfaces.ArtikelDAO;
import interfaces.BestellingDAO;
import interfaces.KlantDAO;

public class DAOFactoryMySQL extends DAOFactory{

	@Override
	public KlantDAO getKlantDAO() {
		return new mysql.KlantDAOMySQL();
	}

	@Override
	public AdresDAO getAdresDAO() {
		return new mysql.AdresDAOMySQL();
	}

	@Override
	public BestellingDAO getBestellingDAO() {
		return new mysql.BestellingDAOMySQL();
	}

	@Override
	public ArtikelDAO getArtikelDAO() {
		return new mysql.ArtikelDAOMySQL();
	}

}
