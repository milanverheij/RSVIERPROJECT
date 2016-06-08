package mysql;

/*
 *  Deze klasse regelt de verbinding tussen het programma en de database
 *  voor de bestellingen. De no-args constructor maakt verbinding en vervolgens
 *  kunnen de methoden lezen, schrijven, updaten, en verwijderen.
 *  Lezen op klant_id geeft alle bestellingen van de klant terug in Iterator<Bestelling> formaat
 *  Lezen op bestelling_id geeft een enkel Bestelling object terug
 *
 * */

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import interfaces.BestellingDAO;
import model.Artikel;
import model.Bestelling;

public class BestellingDAOMySQL extends AbstractDAOMySQL implements BestellingDAO{
	Connection connection;

	public BestellingDAOMySQL(){
		connection = MySQLConnectie.getConnection();
	}

	//Create
	@Override
	public void nieuweBestelling(long klantId, Artikel a1, Artikel a2, Artikel a3) {
		try {
			statement = connection.prepareStatement("INSERT INTO `BESTELLING` "
					+ "(klant_id, "
					+ "artikel1_id, 	artikel2_id, 	artikel3_id, "
					+ "artikel1_naam, 	artikel2_naam, 	artikel3_naam, "
					+ "artikel1_prijs, 	artikel2_prijs, artikel3_prijs) "
					+ "VALUES "
					+ "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");

			statement.setLong(1, klantId);

			statement.setLong(2, a1.getArtikel_id());
			statement.setLong(3, a2.getArtikel_id());
			statement.setLong(4, a3.getArtikel_id());

			statement.setString(5, a1.getArtikel_naam());
			statement.setString(6, a2.getArtikel_naam());
			statement.setString(7, a3.getArtikel_naam());

			statement.setString(8, "" + a1.getArtikel_prijs());
			statement.setString(9, "" + a2.getArtikel_prijs());
			statement.setString(10, "" + a3.getArtikel_prijs());

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			MySQLHelper.close(statement);
		}
	}
	@Override
	public void nieuweBestelling(long klantId, Artikel a1, Artikel a2) {
		try {
			statement = connection.prepareStatement("INSERT INTO `BESTELLING` "
					+ "(klant_id, "
					+ "artikel1_id, 	artikel2_id, "
					+ "artikel1_naam, 	artikel2_naam, "
					+ "artikel1_prijs, 	artikel2_prijs) "
					+ "VALUES "
					+ "(?, ?, ?, ?, ?, ?, ?);");

			statement.setLong(1, klantId);
			statement.setLong(2, a1.getArtikel_id());
			statement.setLong(3, a2.getArtikel_id());

			statement.setString(4, a1.getArtikel_naam());
			statement.setString(5, a2.getArtikel_naam());

			statement.setString(6, "" + a1.getArtikel_prijs());
			statement.setString(7, "" + a2.getArtikel_prijs());

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			MySQLHelper.close(statement);
		}
	}
	@Override
	public void nieuweBestelling(long klantId, Artikel a1) {
		try {
			statement = connection.prepareStatement("INSERT INTO `BESTELLING` "
					+ "(klant_id, artikel1_id, "
					+ "artikel1_naam, "
					+ "artikel1_prijs) "
					+ "VALUES "
					+ "(?, ?, ?, ?);");

			statement.setLong(1, klantId);
			statement.setLong(2, a1.getArtikel_id());
			statement.setString(3, a1.getArtikel_naam());
			statement.setString(4, "" + a1.getArtikel_prijs());

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			MySQLHelper.close(statement);
		}
	}
	@Override
	public void nieuweBestelling(Bestelling bestelling) {
		try {
			statement = connection.prepareStatement("INSERT INTO `BESTELLING` "
					+ "(klant_id, "
					+ "artikel1_id, 	artikel2_id, 	artikel3_id, " 					//2, 3, 4
					+ "artikel1_naam, 	artikel2_naam, 	artikel3_naam, " 				//5, 6, 7
					+ "artikel1_prijs, 	artikel2_prijs, artikel3_prijs) "				//8, 9, 10
					+ "VALUES "
					+ "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"); //TODO 10 stuks

			statement.setLong(1, bestelling.getKlant_id());

			int count = 1;
			LinkedHashMap<Artikel, Integer> artikelen = bestelling.getArtikelLijst();
			Set<Artikel> artikelSet = artikelen.keySet();

			for(Artikel artikel : artikelSet){
				for(int x = 0; x < artikelen.get(artikel); x++){
					statement.setLong(count + 1, artikel.getArtikel_id());				//2, 3, 4
					statement.setString(count + 4, artikel.getArtikel_naam());			//5, 6, 7
					statement.setString(count + 7, "" + artikel.getArtikel_prijs());	//8, 9, 10
					count++;
				}
			}

			while(count < 4){
				statement.setString(count + 1, null);									//2, 3, 4
				statement.setString(count + 4, null);									//5, 6, 7
				statement.setString(count + 7, null);									//8, 9, 10
				count++;
			}
			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			MySQLHelper.close(statement);
		}
	}
	//Read
	@Override
	public Iterator<Bestelling> getBestellingOpKlantGegevens(long klantId) {
		//Alle bestellingen voor de klant
		LinkedHashSet<Bestelling> bestellijst = new LinkedHashSet<Bestelling>();

		//Individuele bestelling
		Bestelling bestelling;

		//De artikelen binnen een bestelling
		LinkedHashMap<Artikel, Integer> map;

		ResultSet rs;

		try {
			//Alle bestellingen van de klant in de ResultSet laden
			statement = connection.prepareStatement("SELECT * FROM `BESTELLING` WHERE klant_id =  ?;");
			statement.setLong(1, klantId);
			rs = statement.executeQuery();
			while(rs.next()){ 			//Zolang er meer entries zijn
				map = new LinkedHashMap<Artikel, Integer>();
				bestelling = new Bestelling();

				bestelling.setBestelling_id(rs.getLong(1));
				bestelling.setKlant_id(rs.getLong(2));
				voegArtikelToe(rs, map);
				bestelling.setArtikelLijst(map);
				bestellijst.add(bestelling);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			MySQLHelper.close(statement);
		}
		Iterator<Bestelling> it = bestellijst.iterator();
		return it;
	}
	@Override
	public Bestelling getBestellingOpBestelling(long bestellingId) {
		try {
			statement = connection.prepareStatement("SELECT * FROM `BESTELLING` WHERE bestelling_id =  ? ;");
			statement.setLong(1, bestellingId);
			ResultSet rs = statement.executeQuery();

			Bestelling bestelling = new Bestelling();
			LinkedHashMap<Artikel, Integer> map = new LinkedHashMap<Artikel, Integer>();

			rs.next();
			bestelling.setBestelling_id(rs.getLong(1));
			bestelling.setKlant_id(rs.getLong(2));
			voegArtikelToe(rs, map);
			bestelling.setArtikelLijst(map);
			return bestelling;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			MySQLHelper.close(statement);
		}
		return null;
	}

	//Update
	@Override
	public void updateBestelling(long bestellingId, Artikel a1) {

		try {
			statement = connection.prepareStatement("UPDATE `BESTELLING` "
					+ "SET artikel1_id = ?, artikel1_naam = ?, artikel1_prijs = ?"
					+ "WHERE bestelling_id = ?;");

			statement.setLong(1, a1.getArtikel_id());
			statement.setString(2, a1.getArtikel_naam());
			statement.setString(3, "" + a1.getArtikel_prijs());

			statement.setLong(4, bestellingId);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			MySQLHelper.close(statement);
		}
	}
	@Override
	public void updateBestelling(long bestellingId, Artikel a1, Artikel a2) {

		try {
			statement = connection.prepareStatement("UPDATE `BESTELLING` "
					+ "SET artikel1_id = ?, artikel1_naam = ?, artikel1_prijs = ?, "
					+ "artikel2_id = ?, artikel2_naam = ?, artikel2_prijs = ?"
					+ "WHERE bestelling_id = ?;");

			statement.setLong(1, a1.getArtikel_id());
			statement.setString(2, a1.getArtikel_naam());
			statement.setString(3, "" + a1.getArtikel_prijs());

			statement.setLong(4, a2.getArtikel_id());
			statement.setString(5, a2.getArtikel_naam());
			statement.setString(6, "" + a2.getArtikel_prijs());

			statement.setLong(7, bestellingId);

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			MySQLHelper.close(statement);
		}
	}
	@Override
	public void updateBestelling(long bestellingId, Artikel a1, Artikel a2, Artikel a3) {
		try {
			statement = connection.prepareStatement("UPDATE `BESTELLING` "
					+ "SET artikel1_id = ?, artikel1_naam = ?, artikel1_prijs = ?, "
					+ "artikel2_id = ?, artikel2_naam = ?, artikel2_prijs = ?, "
					+ "artikel3_id = ?, artikel3_naam = ?, artikel3_prijs = ? "
					+ "WHERE bestelling_id = ?;");

			statement.setLong(1, a1.getArtikel_id());
			statement.setString(2, a1.getArtikel_naam());
			statement.setString(3, "" + a1.getArtikel_prijs());

			statement.setLong(4, a2.getArtikel_id());
			statement.setString(5, a2.getArtikel_naam());
			statement.setString(6, "" + a2.getArtikel_prijs());

			statement.setLong(7, a3.getArtikel_id());
			statement.setString(8, a3.getArtikel_naam());
			statement.setString(9, "" + a3.getArtikel_prijs());
			statement.setLong(10, bestellingId);

			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			MySQLHelper.close(statement);
		}
	}

	//Delete
	@Override
	public void verwijderAlleBestellingenKlant(long klantId) {
		try {
			statement = connection.prepareStatement("DELETE FROM `BESTELLING` WHERE klant_id = ?;");
			statement.setLong(1, klantId);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			MySQLHelper.close(statement);
		}
	}
	@Override
	public void verwijderEnkeleBestelling(long bestellingId) {
		try {
			statement = connection.prepareStatement("DELETE FROM `BESTELLING` WHERE bestelling_id = ?;");
			statement.setLong(1, bestellingId);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			MySQLHelper.close(statement);
		}
	}

	//Utility
	private void voegArtikelToe(ResultSet rs, LinkedHashMap<Artikel, Integer> map){
		for(int x = 0; x < 3; x++){
			try {
				if(!(rs.getString(x + 9) == null)){
					Artikel artikel = new Artikel();
					artikel.setArtikel_id(rs.getInt(x + 3));
					artikel.setArtikel_naam(rs.getString(x + 6));
					artikel.setArtikel_prijs(Double.parseDouble(rs.getString(x + 9)));

					if(map.containsKey(artikel)){
						map.put(artikel, map.get(artikel) + 1);
					}else{
						map.put(artikel, 1);
					}
				}
			} catch (NumberFormatException | SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
