package database.daos.mysql;

import exceptions.GeneriekeFoutmelding;
import logger.DeLogger;
import model.Artikel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashSet;

import database.interfaces.ArtikelDAO;

/* Author @Douwe Jongeneel
 *
 * Deze klasse regelt een verbinding met de database zodat artikelen aan een bestelling kunnen worden toegevoegd,
 * uitgelezen, geupdate en verwijdert. Er is tevens een methode die alle artikelen uit de database teruggeeft in 
 * een Iterator. Er van uitgaande dat elke bestelling uit max 3 artikelen bestaat maar dat deze artikelen per 
 * bestelling kunnen verschillen.
 *
 */
public class ArtikelDAOMySQL extends AbstractDAOMySQL implements ArtikelDAO {
	private String artikelQuery = "";
	private String prijsQuery = "";
	private Artikel artikel = new Artikel();


	public ArtikelDAOMySQL() {
	}

	//Create

	// Onderstaande methode maakt een nieuw artikel aan in de artikel tabel, zet de prijs gegevens van het artikel in de prijs
	// tabel en zorgt ervoor dat de artikel tabel het juiste prijsId heeft.
	@Override 
	public int nieuwArtikel(Artikel aNieuw) throws GeneriekeFoutmelding {
		
		if (aNieuw == null) {
			DeLogger.getLogger().error("Fout: Null waarde voor aNieuw in de methode nieuwArtikel");
		}
		
		prijsQuery = "INSERT INTO prijs (prijs) VALUES (?);";
		artikelQuery = "INSERT INTO artikel (omschrijving, prijsId, verwachteLevertijd, inAssortiment)"
				+ "VALUES (?, ?, ?, ?);";
		String queryUpdate = "UPDATE prijs SET artikelId = ? WHERE prijsId = ?;";

		try (Connection connection = connPool.verkrijgConnectie();
				PreparedStatement prijsStatement = connection.prepareStatement(prijsQuery, Statement.RETURN_GENERATED_KEYS);
				PreparedStatement artikelStatement = connection.prepareStatement(artikelQuery, Statement.RETURN_GENERATED_KEYS);
				PreparedStatement updateStatement = connection.prepareStatement(queryUpdate)){

			connection.setAutoCommit(false);

			//Zet de prijs gegevens in de prijs tabel
			prijsStatement.setBigDecimal(1, aNieuw.getArtikelPrijs());
			prijsStatement.executeUpdate();

			try (ResultSet generatedPrijsId = prijsStatement.getGeneratedKeys()) {
				if (generatedPrijsId.next()) {
					aNieuw.setPrijsId(generatedPrijsId.getInt(1));
				}
			}

			//Zet de artikel gegevens in de artikel tabel
			artikelStatement.setString(1, aNieuw.getArtikelNaam());
			artikelStatement.setInt(2, aNieuw.getPrijsId());
			artikelStatement.setInt(3, aNieuw.getVerwachteLevertijd());
			artikelStatement.setBoolean(4, aNieuw.isInAssortiment());
			artikelStatement.executeUpdate();

			try (ResultSet generatedArtikelId = artikelStatement.getGeneratedKeys()) {
				if (generatedArtikelId.next()) {
					aNieuw.setArtikelId(generatedArtikelId.getInt(1));
				}
			}

			//Zet artikelId in prijs tabel
			updateStatement.setInt(1, aNieuw.getArtikelId());
			updateStatement.setInt(2, aNieuw.getPrijsId());
			updateStatement.executeUpdate();

			//Execute Transaction
			connection.commit();
			connection.setAutoCommit(true);

			return aNieuw.getArtikelId();
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			DeLogger.getLogger().error("SQL fout tijdens invoeren nieuw artikel");
			throw new GeneriekeFoutmelding("Niew artikel aanmaken kan niet");
		}
	}


	//Read
	@Override
	public Artikel getArtikel(int artikelId) throws GeneriekeFoutmelding {

		artikelQuery = "SELECT * FROM artikel WHERE artikelId = ? ;";
		prijsQuery	= "SELECT prijs FROM prijs WHERE prijsId = ? ;";

		try (Connection connection = connPool.verkrijgConnectie();
				PreparedStatement artikelStatement = connection.prepareStatement(artikelQuery);
				PreparedStatement prijsStatement = connection.prepareStatement(prijsQuery)){

			connection.setAutoCommit(false);
			artikelStatement.setInt(1, artikelId);

			// Vraag de artikel gegevens op
			try (ResultSet artikelRset = artikelStatement.executeQuery()) {

				if (artikelRset.next()) {
					artikel.setArtikelId(artikelRset.getInt(1));
					artikel.setArtikelNaam(artikelRset.getString(2));
					artikel.setPrijsId(artikelRset.getInt(3));
					artikel.setDatumAanmaak(artikelRset.getString(4));
					artikel.setVerwachteLevertijd(artikelRset.getInt(5));
					artikel.setInAssortiment(artikelRset.getBoolean(6));

					// Vraag de prijs gegevens op
					prijsStatement.setInt(1, artikel.getPrijsId());

					try (ResultSet prijsRset = prijsStatement.executeQuery()) {

						if (prijsRset.next()) {
							artikel.setArtikelPrijs(prijsRset.getBigDecimal(1));
						}
					}
				}
			}

			//Execute transaction
			connection.commit();
			connection.setAutoCommit(true);

			return artikel;
		}
		catch (SQLException ex) {
			DeLogger.getLogger().error("SQL fout tijdens het verkrijgen van het artikel met artikelId {}", artikelId);
			throw new GeneriekeFoutmelding("SQL fout tijdens het verkrijgen van het artikel met artikelId " + artikelId);
		}
	}

	// Onderstaande methode haalt alle artiekelen uit de database en geeft ze terug in een iterator.
	// Er kan gekozen worden om alleen de actieve artikelen of alle artikelen uit de database te halen.
	// artikelActief = false vraagt zowel de actieve als inactieve artikelen op.
	// artikelActief = true vraagt alleen de actieve artikelen op.
	public LinkedHashSet<Artikel> getAlleArtikelen(boolean artikelActief) throws GeneriekeFoutmelding {

		// Alle artikelen worden in een Set opgeslagen
		LinkedHashSet<Artikel> artikelSet = new LinkedHashSet<>()	;

		// Onderstaande query combineert de artikel en prijs tabel zodat alle artikel gegevens in een
		// keer uitgelezen kunnen worden, tevens kan er geselecteerd worden op alle of alleen actieve artikelen.
		artikelQuery = "SELECT artikel.artikelId, artikel.omschrijving, artikel.prijsId, prijs.prijs, "
				+ "artikel.datumAanmaak, artikel.verwachteLevertijd, artikel.inAssortiment FROM artikel "
				+ "LEFT JOIN prijs ON artikel.prijsId = prijs.prijsId WHERE inAssortiment LIKE ?;";

		try (Connection connection = connPool.verkrijgConnectie();

				PreparedStatement artikelStatement = connection.prepareStatement(artikelQuery)) {

			if(artikelActief)
				artikelStatement.setBoolean(1, true);
			else
				artikelStatement.setString(1, "%");

			try (ResultSet artikelRset = artikelStatement.executeQuery()) {
				while (artikelRset.next()){

					artikel = new Artikel();
					artikel.setArtikelId(artikelRset.getInt(1));
					artikel.setArtikelNaam(artikelRset.getString(2));
					artikel.setPrijsId(artikelRset.getInt(3));
					artikel.setArtikelPrijs(artikelRset.getBigDecimal(4));
					artikel.setDatumAanmaak(artikelRset.getString(5));
					artikel.setVerwachteLevertijd(artikelRset.getInt(6));
					artikel.setInAssortiment(artikelRset.getBoolean(7));
					artikelSet.add(artikel);
				}
			}
			// Execute transaction
			return artikelSet;
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			DeLogger.getLogger().error("SQL fout tijdens opvragen van alle " + (artikelActief ? "artikelen " : "actieve artikelen "));
			throw new GeneriekeFoutmelding("SQL fout tijdens opvragen van alle " + (artikelActief ? "artikelen " : "actieve artikelen "));
		}
	}

	/*
	 *  Onderstaande methode update de artikel gegevens in de database, de prijs en of artikel gegevens kunnen
	 *  tegelijkertijd en appart geupdate worden. Tevens zorgt de methode ervoor dat er geen inconsistentie op
	 *  kan treden in de artikel prijs gegevens alswel in het artikel object.
	 */

	//Update
	@Override
	public void updateArtikel(int artikelId, Artikel aNieuw) throws GeneriekeFoutmelding {

		boolean dePrijsIsVerandert = false;
		int prijsIdUitDataBase = 0;

		artikelQuery = "UPDATE artikel SET "
				+ "omschrijving = ?, "
				+ "prijsId = ?, "
				+ "verwachteLevertijd = ?, "
				+ "inAssortiment = ? "
				+ "WHERE artikelId = ? ;";

		// Met onderstaande join tables wordt het artikelId en daarbij behorende prijs verkregen
		prijsQuery = "SELECT artikel.prijsId, prijs.prijs "
				+ "FROM artikel "
				+ "LEFT JOIN prijs "
				+ "ON artikel.prijsId = prijs.prijsId "
				+ "WHERE artikel.artikelId = ? ;";

		String nieuwePrijsQuery = "INSERT INTO prijs (prijs, artikelId) "
				+ "VALUES (?, ?);";

		try (Connection connection = connPool.verkrijgConnectie();
				PreparedStatement artikelStatement = connection.prepareStatement(artikelQuery);
				PreparedStatement prijsStatement = connection.prepareStatement(prijsQuery)) {

			connection.setAutoCommit(false);
			artikelStatement.setInt(5, artikelId);
			prijsStatement.setInt(1, artikelId);
			aNieuw.setArtikelId(artikelId);

			// Voordat de artikel gegevens geupdate worden moet gecontroleerd worden of de prijs 
			// van het artikel gewijzigd is. Hiervoor wordt de prijs van het artikel uit de 
			// database gehaald.
			try (ResultSet prijsRset = prijsStatement.executeQuery()) {
				if (prijsRset.next()) {

					// Hier wordt gecontroleerd of de prijs van het artikel in de database en dat van de 
					// artikelgegevens die geupdate worden van elkaar verschillen.
					if (	(prijsRset.getBigDecimal(2)).compareTo(aNieuw.getArtikelPrijs()) != 0) {
						dePrijsIsVerandert = true;
					}
					// Wanneer de prijs niet verandert, mag het prijsId ook niet veranderen.
					// Hier wordt vastgesteld dat het prijsId alleen mag veranderen wanneer
					// de prijs verandert is. Als de prijs niet verandert wordt het prijsId
					// van aNieuw geupdate zodat het weer overeenkomt met de gegevens in de 
					// database. Op deze manier kunnen artikelen geen onrechtmatige prijzen
					// worden toegekend.
					if (prijsRset.getInt(1) != aNieuw.getPrijsId() && !dePrijsIsVerandert) {

						aNieuw.setPrijsId(prijsRset.getInt(1));
						String dataInconsistentie = "Data inconsistentie: Het prijsId van aNieuw(" + aNieuw.toString() + ")\n "
								+ "verschilt van het prijsId uit de DB " + prijsIdUitDataBase + " terwijl "
								+ "de prijs niet verandert is!!!";
						DeLogger.getLogger().info(dataInconsistentie);
					}
				}
			}

			if (dePrijsIsVerandert) {

				// Wanneer de prijs van het artikel verandert is dan wordt hier de nieuwe 
				// prijs in de database gezet.
				try (PreparedStatement nieuwePrijsStatement = connection.prepareStatement(
						nieuwePrijsQuery, Statement.RETURN_GENERATED_KEYS)) {

					nieuwePrijsStatement.setBigDecimal(1, aNieuw.getArtikelPrijs());
					nieuwePrijsStatement.setInt(2, artikelId);
					nieuwePrijsStatement.executeUpdate();

					try (ResultSet prijsIdRset = nieuwePrijsStatement.getGeneratedKeys()) {
						if (prijsIdRset.next()) {
							aNieuw.setPrijsId(prijsIdRset.getInt(1));
						}
					}
				}
			}

			// Hier worden de artikel gegevens in de database geupdate.
			artikelStatement.setString(1, aNieuw.getArtikelNaam());
			artikelStatement.setInt(2, aNieuw.getPrijsId());
			artikelStatement.setInt(3, aNieuw.getVerwachteLevertijd());
			artikelStatement.setBoolean(4, aNieuw.isInAssortiment());
			artikelStatement.executeUpdate();

			//Execute transaction
			connection.commit();
			connection.setAutoCommit(true);


		}
		catch (SQLException ex) {
			DeLogger.getLogger().error("SQL fout tijdens updaten van artikel met artikelId " + artikelId);
			throw new GeneriekeFoutmelding("SQL fout tijdens updaten van artikel met artikelId " + artikelId);
		}

	}

	//Delete
	@Override
	public void verwijderArtikel(int artikelId) throws GeneriekeFoutmelding {

		artikelQuery = "UPDATE artikel SET inAssortiment = 0 WHERE artikelId = ?;";

		try(Connection connection = connPool.verkrijgConnectie();

				PreparedStatement artikelStatement = connection.prepareStatement(artikelQuery)) {

			connection.setAutoCommit(false);
			artikelStatement.setInt(1, artikelId);
			artikelStatement.executeUpdate();
			connection.commit();

		}
		catch (SQLException ex) {
			ex.printStackTrace();
			DeLogger.getLogger().error("SQL fout tijdens het verwijderen van artikel met id {}", artikelId);
			throw new GeneriekeFoutmelding("SQL fout tijdens het verwijderen van artikel met id " + artikelId);
		}
	}
	
	public void verWijderVoorHetEchie(long artikelId) throws GeneriekeFoutmelding{
		artikelQuery = "DELETE FROM artikel WHERE artikelId = ?;";
		prijsQuery = "DELETE FROM prijs WHERE artikelId = ?;";

		try (Connection connection = connPool.verkrijgConnectie();
				PreparedStatement verwijderArtikelStatement = connection.prepareStatement(artikelQuery);
				PreparedStatement verwijderPrijsStatement = connection.prepareStatement(prijsQuery)) {

			connection.setAutoCommit(false);
			
			verwijderPrijsStatement.setLong(1, artikelId);
			verwijderPrijsStatement.executeUpdate();
			
			verwijderArtikelStatement.setLong(1, artikelId);
			verwijderArtikelStatement.executeUpdate();
			
			
			connection.commit();
		}
		catch (SQLException  ex) {
			ex.printStackTrace();
			DeLogger.getLogger().error("SQL fout tijdens het verwijderen van artikel met id {}", artikelId);
			throw new GeneriekeFoutmelding("SQL fout tijdens het verwijderen van artikel met id " + artikelId);
		}
	}
}



