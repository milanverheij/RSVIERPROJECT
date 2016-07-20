package database.daos.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import database.interfaces.BestellingDAO;
import exceptions.GeneriekeFoutmelding;
import model.Artikel;
import model.Bestelling;

public class BestellingDAOMySQL extends AbstractDAOMySQL implements BestellingDAO{
	public static boolean bestellingWordGetest = false; //Kijken of een JUnit test loopt
	public static Bestelling aangeroepenBestellingInTest; //TODO nieuwe bestelling voor test maken

	public long nieuweBestelling(Bestelling bestelling) throws GeneriekeFoutmelding{
		try (Connection con = connPool.verkrijgConnectie();
			 PreparedStatement statementBestelTabel = con.prepareStatement(
					 "INSERT INTO `bestelling` (klantId, bestellingActief) VALUES (?, ?)",
					 PreparedStatement.RETURN_GENERATED_KEYS);){

			con.setAutoCommit(false);

			statementBestelTabel.setLong(1, bestelling.getKlantId());
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
					 "INSERT INTO `bestelling` (klantId, isActief) VALUES (?, ?)",
					 PreparedStatement.RETURN_GENERATED_KEYS);){

			// Auto-commit uit om alles tegelijk door te voeren, voorkomt fouten in de database wanneer
			// de bestelling en de artikellijst niet beide goed gaan
			con.setAutoCommit(false);

			statementBestelTabel.setLong(1, klantId);
			statementBestelTabel.setBoolean(2, isActief);
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
					"SELECT `bestelling`.klantId, `bestelling`.bestellingId, `bestelling`.bestellingActief, `artikel`.artikelId, `artikel`.omschrijving, `bestellingHeeftArtikel`.aantal, "
							+ "`bestellingHeeftArtikel`.prijsIdPrijs, `prijs`.prijs, `bestelling`.datumAanmaak"

							+ " FROM `bestellingHeeftArtikel`, `artikel`, `bestelling`, `prijs`"

							+ " WHERE `bestelling`.klantId = ? AND `bestelling`.bestellingActief LIKE ?"
							+ " AND `bestellingHeeftArtikel`.prijsIdPrijs = `prijs`.prijsId"
							+ " AND `bestellingHeeftArtikel`.bestellingIdBest = `bestelling`.bestellingId"
							+ " AND `bestellingHeeftArtikel`.artikelIdArt = `artikel`.artikelId;"
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
					"SELECT `bestelling`.klantId, `bestelling`.bestellingId, `bestelling`.bestellingActief, `artikel`.artikelId, "
							+ "`artikel`.omschrijving, `bestellingHeeftArtikel`.aantal, "
							+ "`bestellingHeeftArtikel`.prijsIdPrijs, `prijs`.prijs, "
							+ "`bestelling`.datumAanmaak"

							+ " FROM `bestellingHeeftArtikel`, `artikel`, `bestelling`, `prijs`"

							+ " WHERE `bestelling`.bestellingId = ? AND `bestelling`.bestellingActief LIKE ?"
							+ " AND `bestellingHeeftArtikel`.prijsIdPrijs = `prijs`.prijsId"
							+ " AND `bestellingHeeftArtikel`.bestellingIdBest = `bestelling`.bestellingId"
							+ " AND `bestellingHeeftArtikel`.artikelIdArt = `artikel`.artikelId;"
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
			PreparedStatement deleteStatement = con.prepareStatement("DELETE FROM `bestellingHeeftArtikel` WHERE bestellingIdBest = ?;");
			PreparedStatement setActiefStatement = con.prepareStatement("UPDATE `bestelling` SET `bestellingActief` = true WHERE bestellingId = ?;")
		){

			con.setAutoCommit(false);

			// Verwijder alle oude artikelen van de bestelling uit bestellingHeeftArtikel
			deleteStatement.setLong(1, bestelling.getBestellingId());
			deleteStatement.executeUpdate();

			setActiefStatement.setLong(1, bestelling.getBestellingId());
			setActiefStatement.executeUpdate();

			// Schrijf alle nieuwe artikelen naar bestellingHeeftArtikel
			schrijfAlleArtikelenNaarDeDatabase(con, bestelling.getBestellingId(), bestelling.getArtikelLijst());
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
					"UPDATE `bestelling` "
							+ "SET bestellingActief = 0 "
							+ "WHERE klantId = ?;");){
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
					"DELETE `bestellingHeeftArtikel` "
							+ "FROM `bestellingHeeftArtikel` "
							+ "INNER JOIN `bestelling` "
							+ "ON `bestellingHeeftArtikel`.bestellingIdBest = `bestelling`.bestellingId "
							+ "WHERE `bestelling`.klantId = ?;");){

			// Auto-commit uit want we werken op 2 tabellen
			// Dus het moet allebei goed gaan
			con.setAutoCommit(false);

			//Verwijder alle items met het klantId uit bestellingHeeftArtikel
			statement.setLong(1, klantId);
			statement.executeUpdate();

			// Verwijder bestelling
			try(PreparedStatement updateStatement = con.prepareStatement(
					"DELETE `bestelling` "
							+ "FROM `bestelling`"
							+ "WHERE klantId = ?;");){
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
					"UPDATE `bestelling` SET bestellingActief = 0 WHERE bestellingId = ?")){
			statement.setLong(1, bestellingId);
			statement.executeUpdate();
		}catch (SQLException e){
			throw new GeneriekeFoutmelding("Error in: " + this.getClass() + ": setEnkeleBestellingInactief: " + e.getMessage());
		}
	}

	public void verwijderEnkeleBestelling(long bestellingId) throws GeneriekeFoutmelding{
		try(Connection con = connPool.verkrijgConnectie();
			PreparedStatement statement = con.prepareStatement(
					"DELETE FROM `bestelling` WHERE bestellingId = ?");
			PreparedStatement statement2 = con.prepareStatement(
					"DELETE FROM `bestellingHeeftArtikel` WHERE bestellingIdBest = ?")){

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
				"INSERT INTO `bestellingHeeftArtikel` (bestellingIdBest, artikelIdArt, prijsIdPrijs, aantal)"
						+ " VALUES (?, ?, ?, ?)");){
//			System.out.println(bestellingId);
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
					"INSERT INTO `bestellingHeeftArtikel` (bestellingIdBest, artikelIdArt, prijsIdPrijs, aantal)"
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
				if(best.getBestellingId() == 0){
					setBestellingGegevens(rs, best);
				}

				// Wanneer er een volgende bestelling is, schrijf de vorige naar de ArrayList
				// en maak een nieuwe bestelling aan
				if(best.getBestellingId() != (rs.getLong("bestellingId"))){
					bestellingSet.add(best);
					best = new Bestelling();
					setBestellingGegevens(rs, best);
				}

				// Maak een nieuw Artikel object aan en voeg deze toe aan de ArrayList
				// met Artikel objecten in Bestelling
				art = new Artikel();

				art.setArtikelId(rs.getInt("artikelId"));
				art.setArtikelNaam(rs.getString("omschrijving"));
				art.setArtikelPrijs(rs.getBigDecimal("prijs"));
				art.setPrijsId(rs.getInt("prijsIdPrijs"));
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
			best.setBestellingId(rs.getLong("bestellingId"));
			best.setKlantId(rs.getLong("klantId"));
			best.setDatumAanmaak(rs.getString("datumAanmaak"));
			best.setBestellingActief(rs.getBoolean("bestellingActief"));
		}catch (SQLException e){
			e.printStackTrace();
			throw new GeneriekeFoutmelding("Error in: " + this.getClass() + ": setBestellingGegevens: " + e.getMessage());
		}

	}

}