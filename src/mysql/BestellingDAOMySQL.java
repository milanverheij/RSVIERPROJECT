package mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import exceptions.GeneriekeFoutmelding;
import interfaces.BestellingDAO;
import model.Artikel;
import model.Bestelling;

public class BestellingDAOMySQL extends AbstractDAOMySQL implements BestellingDAO{
	public static boolean bestellingWordGetest = false; //Kijken of een JUnit test loopt
	public static Bestelling aangeroepenBestellingInTest; //TODO nieuwe bestelling voor test maken

	public long nieuweBestelling(Bestelling bestelling) throws GeneriekeFoutmelding{
		try (Connection con = connPool.verkrijgConnectie();
				PreparedStatement statementBestelTabel = con.prepareStatement(
						"INSERT INTO `BESTELLING` (klant_id, bestellingActief) VALUES (?, ?)",
						PreparedStatement.RETURN_GENERATED_KEYS);){

			con.setAutoCommit(false);

			statementBestelTabel.setLong(1, bestelling.getKlant_id());
			statementBestelTabel.setBoolean(2, bestelling.getBestellingActief());
			statementBestelTabel.executeUpdate();

			long bestellingId = schrijfAlleArtikelenNaarDeDatabase(con, statementBestelTabel, bestelling.getArtikelLijst());
			con.commit();

			return bestellingId;
		}catch (SQLException e){
			e.printStackTrace();
			throw new GeneriekeFoutmelding("Error in: " + this.getClass() + ": nieuweBestelling(bestelling): " + e.getMessage());
		}
	}

	public long nieuweBestelling(long klantId, List<Artikel> artikelList, boolean isActief) throws GeneriekeFoutmelding{

		// De eerste try haalt het id van de nieuwe bestelling op, in de tweede try word de lijst met artikelen verwerkt
		try (Connection con = connPool.verkrijgConnectie();
				PreparedStatement statementBestelTabel = con.prepareStatement(
						"INSERT INTO `BESTELLING` (klant_id) VALUES (?)",
						PreparedStatement.RETURN_GENERATED_KEYS);){

			// Auto-commit uit om alles tegelijk door te voeren, voorkomt fouten in de database wanneer
			// de bestelling en de artikellijst niet beide goed gaan
			con.setAutoCommit(false);

			statementBestelTabel.setLong(1, klantId);
//			statementBestelTabel.setBoolean(2, isActief);
			statementBestelTabel.executeUpdate();

			long bestellingId = schrijfAlleArtikelenNaarDeDatabase(con, statementBestelTabel, artikelList);

			// Voer al de statements definitief uit
			con.commit();
			return bestellingId;
		}catch (SQLException e){
			throw new GeneriekeFoutmelding("Error in: " + this.getClass() + ": nieuweBestelling(klantId, artikelLijst): " + e.getMessage());
		}
	}

	@Override
	public Iterator<Bestelling> getBestellingOpKlantId(long klantId, boolean bestellingActief) throws GeneriekeFoutmelding{
		try(Connection con = connPool.verkrijgConnectie();
				PreparedStatement statement = con.prepareStatement(
						"SELECT `BESTELLING`.klant_id, `BESTELLING`.bestelling_id, `BESTELLING`.bestellingActief, `ARTIKEL`.artikel_id, `ARTIKEL`.omschrijving, `BESTELLING_HEEFT_ARTIKEL`.aantal, "
								+ "`BESTELLING_HEEFT_ARTIKEL`.prijs_id_prijs, `PRIJS`.prijs, `BESTELLING`.datumAanmaak"

							+ " FROM `BESTELLING_HEEFT_ARTIKEL`, `ARTIKEL`, `BESTELLING`, `PRIJS`"

							+ " WHERE `BESTELLING`.klant_id = ? AND `BESTELLING`.bestellingActief LIKE ?"
							+ " AND `BESTELLING_HEEFT_ARTIKEL`.prijs_id_prijs = `PRIJS`.prijs_id"
							+ " AND `BESTELLING_HEEFT_ARTIKEL`.bestelling_id_best = `BESTELLING`.bestelling_id"
							+ " AND `BESTELLING_HEEFT_ARTIKEL`.artikel_id_art = `ARTIKEL`.artikel_id;"
						);){

			statement.setLong(1, klantId);

			if(bestellingActief)
				statement.setBoolean(2, bestellingActief);
			else
				statement.setString(2, "%");
			
			LinkedHashSet<Bestelling> set = verwerkResultSetGetBestelling(statement);
			
			if(set == null)
				return null;
			else
				return set.iterator();
		}catch (SQLException e){
			throw new GeneriekeFoutmelding("Error in: " + this.getClass() + ": getBestellingOpKlantId: " + e.getMessage());
		}
	}

	@Override
	public Iterator<Bestelling> getBestellingOpBestellingId(long bestellingId, boolean bestellingActief) throws GeneriekeFoutmelding{
		try(Connection con = connPool.verkrijgConnectie();
				PreparedStatement statement = con.prepareStatement(
						"SELECT `BESTELLING`.klant_id, `BESTELLING`.bestelling_id, `BESTELLING`.bestellingActief, `ARTIKEL`.artikel_id, "
								+ "`ARTIKEL`.omschrijving, `BESTELLING_HEEFT_ARTIKEL`.aantal, "
								+ "`BESTELLING_HEEFT_ARTIKEL`.prijs_id_prijs, `PRIJS`.prijs, "
								+ "`BESTELLING`.datumAanmaak"

						+ " FROM `BESTELLING_HEEFT_ARTIKEL`, `ARTIKEL`, `BESTELLING`, `PRIJS`"

						+ " WHERE `BESTELLING`.bestelling_id = ? AND `BESTELLING`.bestellingActief LIKE ?"
						+ " AND `BESTELLING_HEEFT_ARTIKEL`.prijs_id_prijs = `PRIJS`.prijs_id"
						+ " AND `BESTELLING_HEEFT_ARTIKEL`.bestelling_id_best = `BESTELLING`.bestelling_id"
						+ " AND `BESTELLING_HEEFT_ARTIKEL`.artikel_id_art = `ARTIKEL`.artikel_id;"
						);){

			statement.setLong(1, bestellingId);

			if(bestellingActief)
				statement.setBoolean(2, bestellingActief);
			else
				statement.setString(2, "%");
			LinkedHashSet<Bestelling> set = verwerkResultSetGetBestelling(statement);
			if(set == null)
				return null;
			else
				return set.iterator();
		}catch (SQLException e){
			throw new GeneriekeFoutmelding("Error in: " + this.getClass() + ": getBestellingOpBestellingId: " + e.getMessage());
		}
	}

	@Override
	public void updateBestelling(Bestelling bestelling) throws GeneriekeFoutmelding{
		try(Connection con = connPool.verkrijgConnectie();
				PreparedStatement deleteStatement = con.prepareStatement("DELETE FROM `BESTELLING_HEEFT_ARTIKEL` WHERE bestelling_id_best = ?;");
				PreparedStatement setActiefStatement = con.prepareStatement("UPDATE `BESTELLING` SET `bestellingActief` = true WHERE bestelling_id = ?;")
				){

			con.setAutoCommit(false);

			// Verwijder alle oude artikelen van de bestelling uit BESTELLING_HEEFT_ARTIKEL
			deleteStatement.setLong(1, bestelling.getBestelling_id());
			deleteStatement.executeUpdate();

			setActiefStatement.setLong(1, bestelling.getBestelling_id());
			setActiefStatement.executeUpdate();
			
			// Schrijf alle nieuwe artikelen naar BESTELLING_HEEFT_ARTIKEL
			schrijfAlleArtikelenNaarDeDatabase(con, bestelling.getBestelling_id(), bestelling.getArtikelLijst());
			con.commit();
		}catch (SQLException e){
			e.printStackTrace();
			throw new GeneriekeFoutmelding("Error in: " + this.getClass() + ": updateBestelling: " + e.getMessage());
		}
	}

	@Override
	public void setAlsInactiefAlleBestellingenKlant(long klantId) throws GeneriekeFoutmelding{
		try(Connection con = connPool.verkrijgConnectie();
				PreparedStatement updateStatement = con.prepareStatement(
						"UPDATE `BESTELLING` "
								+ "SET bestellingActief = 0 "
								+ "WHERE klant_id = ?;");){
			updateStatement.setLong(1, klantId);
			updateStatement.executeUpdate();
		}catch (SQLException e){
			throw new GeneriekeFoutmelding("Error in: " + this.getClass() + ": verwijderAlleBestellingenKlant: " + e.getMessage());
		}
	}

	@Override
	public void verwijderAlleBestellingenKlant(long klantId) throws GeneriekeFoutmelding{
		try(Connection con = connPool.verkrijgConnectie();
				PreparedStatement statement = con.prepareStatement(
						"DELETE `BESTELLING_HEEFT_ARTIKEL` "
								+ "FROM `BESTELLING_HEEFT_ARTIKEL` "
								+ "INNER JOIN `BESTELLING` "
								+ "ON `BESTELLING_HEEFT_ARTIKEL`.bestelling_id_best = `BESTELLING`.bestelling_id "
								+ "WHERE `BESTELLING`.klant_id = ?;");){

			// Auto-commit uit want we werken op 2 tabellen
			// Dus het moet allebei goed gaan
			con.setAutoCommit(false);

			//Verwijder alle items met het klantId uit BESTELLING_HEEFT_ARTIKEL
			statement.setLong(1, klantId);
			statement.executeUpdate();

			// Verwijder bestelling
			try(PreparedStatement updateStatement = con.prepareStatement(
					"DELETE `BESTELLING` "
							+ "FROM `BESTELLING`"
							+ "WHERE klant_id = ?;");){
				updateStatement.setLong(1, klantId);
				updateStatement.executeUpdate();
			}
			con.commit();

		} catch (SQLException e) {
			throw new GeneriekeFoutmelding(e.getMessage());
		}
	}

	@Override
	public void setEnkeleBestellingInactief(long bestellingId) throws GeneriekeFoutmelding{
		try(Connection con = connPool.verkrijgConnectie();
				PreparedStatement statement = con.prepareStatement(
						"UPDATE `BESTELLING` SET bestellingActief = 0 WHERE bestelling_id = ?")){
			statement.setLong(1, bestellingId);
			statement.executeUpdate();
		}catch (SQLException e){
			throw new GeneriekeFoutmelding("Error in: " + this.getClass() + ": setEnkeleBestellingInactief: " + e.getMessage());
		}
	}

	public void verwijderEnkeleBestelling(long bestellingId) throws GeneriekeFoutmelding{
		try(Connection con = connPool.verkrijgConnectie();
			PreparedStatement statement = con.prepareStatement(
					"DELETE FROM `BESTELLING` WHERE bestelling_id = ?");
					PreparedStatement statement2 = con.prepareStatement(
					"DELETE FROM `BESTELLING_HEEFT_ARTIKEL` WHERE bestelling_id_best = ?")){

			con.setAutoCommit(false);
			
			statement2.setLong(1, bestellingId);
			statement2.executeUpdate();
			
			statement.setLong(1, bestellingId);
			statement.executeUpdate();
			
			con.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}




	private long schrijfAlleArtikelenNaarDeDatabase(Connection con, long bestellingId, List<Artikel> artikelList) throws GeneriekeFoutmelding{
		try(PreparedStatement statementBestelHeeftArtikelTabel = con.prepareStatement(
				"INSERT INTO `BESTELLING_HEEFT_ARTIKEL` (bestelling_id_best, artikel_id_art, prijs_id_prijs, aantal)"
						+ " VALUES (?, ?, ?, ?)");){

			// Nieuw aangemaakte bestellingId ophalen
			for(Artikel artikel : artikelList){
				statementBestelHeeftArtikelTabel.setLong(1, bestellingId);
				statementBestelHeeftArtikelTabel.setLong(2, artikel.getArtikelId());
				statementBestelHeeftArtikelTabel.setLong(3, artikel.getPrijsId());
				statementBestelHeeftArtikelTabel.setLong(4, artikel.getAantalBesteld());
				statementBestelHeeftArtikelTabel.executeUpdate();
			}
			return bestellingId;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new GeneriekeFoutmelding("Error in: " + this.getClass() + ": schrijfAlleArtikelenNaarDeDatabase(con, bestellingId: " + e.getMessage());
		}
	}

	private long schrijfAlleArtikelenNaarDeDatabase(Connection con, PreparedStatement statement, List<Artikel> artikelList) throws GeneriekeFoutmelding{
		try(ResultSet rs = statement.getGeneratedKeys();
				PreparedStatement statementBestelHeeftArtikelTabel = con.prepareStatement(
						"INSERT INTO `BESTELLING_HEEFT_ARTIKEL` (bestelling_id_best, artikel_id_art, prijs_id_prijs, aantal)"
								+ " VALUES (?, ?, ?, ?)");){


			// Nieuw aangemaakte bestellingId ophalen
			rs.next();
			long bestellingId = rs.getLong(1);

			for(Artikel artikel : artikelList){
				statementBestelHeeftArtikelTabel.setLong(1, bestellingId);
				statementBestelHeeftArtikelTabel.setLong(2, artikel.getArtikelId());
				statementBestelHeeftArtikelTabel.setLong(3, artikel.getPrijsId());
				statementBestelHeeftArtikelTabel.setLong(4, artikel.getAantalBesteld());
				statementBestelHeeftArtikelTabel.executeUpdate();
			}
			return bestellingId;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new GeneriekeFoutmelding("Error in: " + this.getClass() + ": schrijfAlleArtikelenNaarDeDatabase(con, statement, artikelList: " + e.getMessage());
		}
	}

	private LinkedHashSet<Bestelling> verwerkResultSetGetBestelling(PreparedStatement statement) throws GeneriekeFoutmelding{
		try(ResultSet rs = statement.executeQuery();){
			LinkedHashSet<Bestelling> bestellingSet = new LinkedHashSet<Bestelling>();

			// Eerste rij verwerken tot bestelling zodat ik straks een fixed bestellingId heb om
			// mee te vergelijken in de while loop
			Bestelling best = new Bestelling();
			Artikel art;

			// Voeg artikelen toe aan de bestelling zolang het bestellingId gelijk is aan
			// die op de vorige rij van de ResultSet, maak anders een nieuwe Bestelling aan
			while(rs.next()){
				// Kijk of het de eerste bestelling is
				if(best.getBestelling_id() == 0){
					setBestellingGegevens(rs, best);
				}

				// Wanneer er een volgende bestelling is, schrijf de vorige naar de ArrayList
				// en maak een nieuwe bestelling aan
				if(best.getBestelling_id() != (rs.getLong("bestelling_id"))){
					bestellingSet.add(best);
					best = new Bestelling();
					setBestellingGegevens(rs, best);
				}

				// Maak een nieuw Artikel object aan en voeg deze toe aan de ArrayList
				// met Artikel objecten in Bestelling
				art = new Artikel();

				art.setArtikelId(rs.getInt("artikel_id"));
				art.setArtikelNaam(rs.getString("omschrijving"));
				art.setArtikelPrijs(rs.getBigDecimal("prijs"));
				art.setPrijsId(rs.getInt("prijs_id_prijs"));
				art.setAantalBesteld(rs.getInt("aantal"));
				best.voegArtikelToe(art);
			
			}
			//Laatste bestelling ook toevoegen aan de ArrayList
			if(!(best.getArtikelLijst() == null)){
				bestellingSet.add(best);
				return bestellingSet;
			}
			
		} catch (SQLException e) {
			throw new GeneriekeFoutmelding("Error in: " + this.getClass() + ": verwerkResultSetGetBestelling(con, statement, artikelList: " + e.getMessage());
		} catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	private void setBestellingGegevens(ResultSet rs, Bestelling best) throws GeneriekeFoutmelding{
		try{
			best.setBestelling_id(rs.getLong("bestelling_id"));
			best.setKlant_id(rs.getLong("klant_id"));
			best.setDatumAanmaak(rs.getString("datumAanmaak"));
			best.setBestellingActief(rs.getBoolean("bestellingActief"));
		}catch (SQLException e){
			e.printStackTrace();
			throw new GeneriekeFoutmelding("Error in: " + this.getClass() + ": setBestellingGegevens: " + e.getMessage());
		}

	}

}
