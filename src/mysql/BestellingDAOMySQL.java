package mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import model.Artikel;


public class BestellingDAOMySQL extends AbstractDAOMySQL implements interfaces.BestellingDAO{

	public BestellingDAOMySQL(){
		connection = MySQLConnectie.getConnection();
	}

	//Create
	@Override
	public void nieuweBestelling(int klantId, Artikel a1, Artikel a2, Artikel a3) {
		try {
			statement = connection.prepareStatement("INSERT INTO `BESTELLING` "
					+ "(klant_id, "
					+ "artikel1_id, 	artikel2_id, 	artikel3_id, "
					+ "artikel1_naam, 	artikel2_naam, 	artikel3_naam, "
					+ "artikel1_prijs, 	artikel2_prijs, artikel3_prijs) "
					+ "VALUES "
					+ "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");

			statement.setInt(1, klantId);
			
			statement.setInt(2, a1.getArtikel_id());
			statement.setInt(3, a2.getArtikel_id());
			statement.setInt(4, a3.getArtikel_id());
			
			statement.setString(5, a1.getArtikel_naam());
			statement.setString(6, a2.getArtikel_naam());
			statement.setString(7, a3.getArtikel_naam());
			
			statement.setString(8, "" + a1.getArtikel_prijs());
			statement.setString(9, "" + a2.getArtikel_prijs());
			statement.setString(10, "" + a3.getArtikel_prijs());

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void nieuweBestelling(int klantId, Artikel a1, Artikel a2) {
		try {
			statement = connection.prepareStatement("INSERT INTO `BESTELLING` "
					+ "(klant_id, "
					+ "artikel1_id, 	artikel2_id, "
					+ "artikel1_naam, 	artikel2_naam, "
					+ "artikel1_prijs, 	artikel2_prijs) "
					+ "VALUES "
					+ "(?, ?, ?, ?, ?, ?, ?);");

			statement.setInt(1, klantId);
			statement.setInt(2, a1.getArtikel_id());
			statement.setInt(3, a2.getArtikel_id());
			
			statement.setString(4, a1.getArtikel_naam());
			statement.setString(5, a2.getArtikel_naam());

			statement.setString(6, "" + a1.getArtikel_prijs());
			statement.setString(7, "" + a2.getArtikel_prijs());

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void nieuweBestelling(int klantId, Artikel a1) {
		try {
			statement = connection.prepareStatement("INSERT INTO `BESTELLING` "
					+ "(klant_id, artikel1_id, "
					+ "artikel1_naam, "
					+ "artikel1_prijs) "
					+ "VALUES "
					+ "(?, ?, ?, ?);");

			statement.setInt(1, klantId);
			statement.setInt(2, a1.getArtikel_id());
			statement.setString(3, a1.getArtikel_naam());
			statement.setString(4, "" + a1.getArtikel_prijs());

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//Read
	@Override
	public ResultSet getBestellingOpKlantGegevens(int klantId) {
		try {
			statement = connection.prepareStatement("SELECT * FROM `BESTELLING` WHERE klant_id =  ? ;");
			statement.setInt(1, klantId);
			ResultSet rs = statement.executeQuery();
			return rs;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public ResultSet getBestellingOpBestelling(int bestellingId) {
		try {
			statement = connection.prepareStatement("SELECT * FROM `BESTELLING` WHERE bestelling_id =  ? ;");
			statement.setInt(1, bestellingId);
			ResultSet rs = statement.executeQuery();
			return rs;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	//Update
	@Override
	public void updateBestelling(int bestellingId, Artikel a1) {
		
		try {
			statement = connection.prepareStatement("UPDATE `BESTELLING` "
					+ "SET artikel1_id = ?, artikel1_naam = ?, artikel1_prijs = ?"
					+ "WHERE bestelling_id = ?;");
			
			statement.setInt(1, a1.getArtikel_id());
			statement.setString(2, a1.getArtikel_naam());
			statement.setString(3, "" + a1.getArtikel_prijs());
			
			statement.setInt(4, bestellingId);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void updateBestelling(int bestellingId, Artikel a1, Artikel a2) {
		
		try {
			statement = connection.prepareStatement("UPDATE `BESTELLING` "
					+ "SET artikel1_id = ?, artikel1_naam = ?, artikel1_prijs = ?, "
					+ "artikel2_id = ?, artikel2_naam = ?, artikel2_prijs = ?"
					+ "WHERE bestelling_id = ?;");
			
			statement.setInt(1, a1.getArtikel_id());
			statement.setString(2, a1.getArtikel_naam());
			statement.setString(3, "" + a1.getArtikel_prijs());
			
			statement.setInt(4, a2.getArtikel_id());
			statement.setString(5, a2.getArtikel_naam());
			statement.setString(6, "" + a2.getArtikel_prijs());
			
			statement.setInt(7, bestellingId);
			
			statement.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void updateBestelling(int bestellingId, Artikel a1, Artikel a2, Artikel a3) {
		try {
			statement = connection.prepareStatement("UPDATE `BESTELLING` "
					+ "SET artikel1_id = ?, artikel1_naam = ?, artikel1_prijs = ?, "
					+ "artikel2_id = ?, artikel2_naam = ?, artikel2_prijs = ?, "
					+ "artikel3_id = ?, artikel3_naam = ?, artikel3_prijs = ? "
					+ "WHERE bestelling_id = ?;");
			
			statement.setInt(1, a1.getArtikel_id());
			statement.setString(2, a1.getArtikel_naam());
			statement.setString(3, "" + a1.getArtikel_prijs());
			
			statement.setInt(4, a2.getArtikel_id());
			statement.setString(5, a2.getArtikel_naam());
			statement.setString(6, "" + a2.getArtikel_prijs());
			
			statement.setInt(7, a3.getArtikel_id());
			statement.setString(8, a3.getArtikel_naam());
			statement.setString(9, "" + a3.getArtikel_prijs());
			statement.setInt(10, bestellingId);
			
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//Delete
	@Override
	public void verwijderAlleBestellingenKlant(int klantId) {
		try {
			statement = connection.prepareStatement("DELETE FROM `BESTELLING` WHERE klant_id = ?;");
			statement.setInt(1, klantId);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void verwijderEnkeleBestelling(int bestellingId) {
		try {
			statement = connection.prepareStatement("DELETE FROM `BESTELLING` WHERE bestelling_id = ?;");
			statement.setInt(1, bestellingId);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
