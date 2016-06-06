public abstract class DAOFactory {

	public static DAOFactory getDAOFactory(String s){
		if(s.equals("MySQL")) 
			return new DAOFactoryMySQL();
//		else if(s.equals("FireBird"))
//			return new FireBirdDAOFactory();
		else
			return null;
	}
	
	public abstract interfaces.KlantDAO getKlantDAO();
	public abstract interfaces.AdresDAO getAdresDAO();
	public abstract interfaces.BestellingDAO getBestellingDAO();
	public abstract interfaces.ArtikelDAO getArtikelDAO();
	
	
}
