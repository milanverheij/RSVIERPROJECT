package factories;

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
	public static DAOFactory getDAOFactory(String s){
		if(s.equals("MySQL")) 
			return new DAOFactoryMySQL();
//		else if(s.equals("FireBird"))
//			return new FireBirdDAOFactory();
		else
			return null;
	}

	/**
	 * De methode die geimplementeerd dient te worden door de concrete fabriek
	 * om een KlantDAO te maken.
	 *
	 * @return Een KlantDAO van het eerder gekozen database-type.
     */
	public abstract interfaces.KlantDAO getKlantDAO();

	public abstract interfaces.AdresDAO getAdresDAO();

	/**
	 * De methode die geimplementeerd dient te worden door de concrete fabriek
	 * om een BestellingDAO te maken.
	 *
	 * @return Een BestellingDAO van het eerder gekozen database-type.
     */
	public abstract interfaces.BestellingDAO getBestellingDAO();
	public abstract interfaces.ArtikelDAO getArtikelDAO();
	
	
}
