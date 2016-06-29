package mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashSet;

import exceptions.GeneriekeFoutmelding;
import logger.DeLogger;
import model.Artikel;

/* Author @Douwe Jongeneel
 *
 * Deze klasse regelt een verbinding met de database zodat artikelen aan een bestelling kunnen worden toegevoegd,
 * uitgelezen, geupdate en verwijdert. Er is tevens een methode die alle artikelen uit de database teruggeeft in 
 * een Iterator. Er van uitgaande dat elke bestelling uit max 3 artikelen bestaat maar dat deze artikelen per 
 * bestelling kunnen verschillen.
 *
 * TODO1 - artikel_id is nog een int, moet dit ook een long worden? in database was het een varchar geloof ik.
 */
public class ArtikelDAOMySQL extends AbstractDAOMySQL implements interfaces.ArtikelDAO{
	private String artikelQuery = "";
	private String prijsQuery = "";
	private Artikel artikel = new Artikel();


	public ArtikelDAOMySQL() {
	}

	//Create

	// Onderstaande methode maakt een nieuw artikel aan in de ARTIKEL tabel, zet de prijs gegevens van het artikel in de PRIJS
	// tabel en zorgt ervoor dat de ARTIKEL tabel het juiste prijs_id heeft.
	@Override 
	public int nieuwArtikel(Artikel aNieuw) throws GeneriekeFoutmelding {

		String queryPrijs = "INSERT INTO PRIJS (artikel_id, prijs) VALUES (?, ?);"; //TODO - prijs_id autoincrement!!!!!!
		String queryArtikel = "INSERT INTO ARTIKEL (omschrijving, prijs_id, verwachteLevertijd, inAssortiment)"
				+ "VALUES (?, ?, ?, ?);";
		String queryUpdate = "UPDATE ARTIKEL SET prijs_id = ? WHERE artikel_id = ?;";

		try (Connection connection = connPool.verkrijgConnectie();

				PreparedStatement statementPrijs = connection.prepareStatement(queryPrijs, Statement.RETURN_GENERATED_KEYS);
				PreparedStatement statementArtikel = connection.prepareStatement(queryArtikel, Statement.RETURN_GENERATED_KEYS);
				PreparedStatement statementUpdate = connection.prepareStatement(queryUpdate)){

			connection.setAutoCommit(false);

			//Zet de artikel gegevens in de ARTIKEL tabel
			statementArtikel.setString(1, aNieuw.getArtikelNaam());
			statementArtikel.setInt(2, aNieuw.getPrijsId());
			statementArtikel.setInt(3, aNieuw.getVerwachteLevertijd());
			statementArtikel.setBoolean(4, aNieuw.isInAssortiment());
			statementArtikel.executeUpdate();

			try (ResultSet generatedArtikelId = statementArtikel.getGeneratedKeys()) {
				if (generatedArtikelId.next()) {
					aNieuw.setArtikelId(generatedArtikelId.getInt(1));
				}
			}
			
			//TODO - Controleer of het artikel al in de database staat!!!!!!!!!!!!!!!!!!!!!!!!!!!


			//Zet de prijs gegevens in de PRIJS tabel
			statementPrijs.setInt(1, aNieuw.getArtikelId());
			statementPrijs.setBigDecimal(2, aNieuw.getArtikelPrijs());
			statementPrijs.executeUpdate();

			try (ResultSet generatedPrijsId = statementPrijs.getGeneratedKeys()) {
				if (generatedPrijsId.next()) {
					aNieuw.setPrijsId(generatedPrijsId.getInt(1));
				}
			}

			//Zet artikel_id in PRIJS tabel
			statementUpdate.setInt(1, aNieuw.getPrijsId());
			statementUpdate.setInt(2, aNieuw.getArtikelId());
			statementUpdate.executeUpdate();

			//Execute Transaction
			connection.commit();
			connection.setAutoCommit(true);

			return aNieuw.getArtikelId();
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			DeLogger.getLogger().error("SQL fout tijdens wegschrijven nieuw artikel");
			throw new GeneriekeFoutmelding("Niew artikel aanmaken kan niet");
		}
	}


	//Read
	@Override
	public Artikel getArtikel(int artikelId) throws GeneriekeFoutmelding {
		artikelQuery = "SELECT * FROM ARTIKEL WHERE artikel_id = " + artikelId + ";";
		prijsQuery	= "SELECT prijs FROM PRIJS WHERE prijs_id = ? ;";

		try (Connection connection = connPool.verkrijgConnectie();

				PreparedStatement artikelStatement = connection.prepareStatement(artikelQuery);
				PreparedStatement prijsStatement = connection.prepareStatement(prijsQuery)){

			connection.setAutoCommit(false);

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
			ex.printStackTrace();
			DeLogger.getLogger().error("TODO");
			throw new GeneriekeFoutmelding("TODO");
		}
	}

	
	
	
	// Onderstaande methode haalt alle artiekelen uit de database en geeft ze terug in een iterator.
	// Er kan gekozen worden om alleen de actieve artikelen of alle artikelen uit de database te halen.
	// artikelActief = false vraagt zowel de actieve als inactieve artikelen op.
	// artikelActief = true vraagt alleen de actieve artikelen op.
	public LinkedHashSet<Artikel> getAlleArtikelen(boolean artikelActief) throws GeneriekeFoutmelding {

		// Alle artikelen worden in een Set opgeslagen
		LinkedHashSet<Artikel> artikelSet = new LinkedHashSet<>()	;

		// Onderstaande query combineert de ARTIKEL en PRIJS tabel zodat alle artikel gegevens in een
		// keer uitgelezen kunnen worden, tevens kan er geselecteerd worden op alle of alleen actieve artikelen.
		artikelQuery = "SELECT ARTIKEL.artikel_id, ARTIKEL.omschrijving, ARTIKEL.prijs_id, PRIJS.prijs, "
				+ "ARTIKEL.datumAanmaak, ARTIKEL.verwachteLevertijd, ARTIKEL.inAssortiment FROM ARTIKEL "
				+ "LEFT JOIN PRIJS ON ARTIKEL.prijs_id = PRIJS.prijs_id WHERE inAssortiment LIKE ?;";

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
	 *  TODO - Wat zijn de implecaties wanneer er een aNieuw wordt toegevoegd aan de update methode waarbij
	 *  de prijs van het artikel niet verandert is, maar het prijs id wel. dan wordt het nieuwe prijs id naar 
	 *  de Database geschreven en dan kan het zijn dat de prijs waar de database naar verwijst niet meer klopt?!!!!!
	 */

	//Update
	@Override
	public void updateArtikel(int artikelId, Artikel aNieuw) throws GeneriekeFoutmelding {

		boolean dePrijsIsVerandert = false;
		int prijsIdUitDataBase = 0;

		artikelQuery = "UPDATE ARTIKEL SET "
				+ "omschrijving = ?, "
				+ "prijs_id = ?, "
				+ "verwachteLevertijd = ?, "
				+ "inAssortiment = ? "
				+ "WHERE artikel_id = " + artikelId + ";";

		// Met onderstaande join tables wordt het artikel_id en daarbij behorende prijs verkregen
		prijsQuery = "SELECT ARTIKEL.prijs_id, PRIJS.prijs "
				+ "FROM ARTIKEL "
				+ "LEFT JOIN PRIJS "
				+ "ON ARTIKEL.prijs_id = PRIJS.prijs_id "
				+ "WHERE ARTIKEL.artikel_id = " + artikelId + ";";

		String nieuwePrijsQuery = "INSERT INTO PRIJS (prijs, artikel_id) "
				+ "VALUES (?, " + artikelId + ");";

		try (Connection connection = connPool.verkrijgConnectie();

				PreparedStatement artikelStatement = connection.prepareStatement(artikelQuery);
				PreparedStatement prijsStatement = connection.prepareStatement(prijsQuery)) {

			connection.setAutoCommit(false);
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
						String dataInconsistentie = "Data inconsistentie: Het prijs_id van aNieuw(" + aNieuw.toString() + ")\n "
								+ "verschilt van het prijs_id uit de DB " + prijsIdUitDataBase + " terwijl "
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
			ex.printStackTrace();
			DeLogger.getLogger().error("SQL fout tijdens updaten van artikel met artikel_id " + artikelId);
			throw new GeneriekeFoutmelding("SQL fout tijdens updaten van artikel met artikel_id " + artikelId);
		}
	}

	//Delete
	@Override
	public void verwijderArtikel(long a) throws GeneriekeFoutmelding {

		artikelQuery = "UPDATE ARTIKEL SET inAssortiment = 0 WHERE artikel_id = ?;";

		try(Connection connection = connPool.verkrijgConnectie();

				PreparedStatement artikelStatement = connection.prepareStatement(artikelQuery)) {

			connection.setAutoCommit(false);
			artikelStatement.setLong(1, a);
			artikelStatement.executeUpdate();
			connection.commit();

		}
		catch (SQLException ex) {
			ex.printStackTrace();
			DeLogger.getLogger().error("SQL fout tijdens het verwijderen van artikel met id " + a);
			throw new GeneriekeFoutmelding("SQL fout tijdens het verwijderen van artikel met id " + a);
		}


		/* Onderstaande methode is er voor display purposes en toont aan dat artikelen en prijzen 
		 * verwijdert kunnen worden

		artikelQuery = "DELETE FROM ARTIKEL WHERE artikel_id = ?;";
		prijsQuery = "DELETE FROM PRIJS WHERE prijs_id = ?;";

		try (Connection connection = connPool.verkrijgConnectie();
				PreparedStatement verwijderArtikelStatement = connection.prepareStatement(artikelQuery);
				PreparedStatement verwijderPrijsStatement = connection.prepareStatement(prijsQuery)) {

			connection.setAutoCommit(false);
			verwijderArtikelStatement.setInt(1, a.getArtikelId());
			verwijderArtikelStatement.executeUpdate();
			verwijderPrijsStatement.setInt(1, a.getPrijsId());
			verwijderPrijsStatement.executeUpdate();
			connection.commit();
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			DeLogger.getLogger().error("SQL fout tijdens het verwijderen van artikel met id " + a.getArtikelId());
			throw new GeneriekeFoutmelding("SQL fout tijdens het verwijderen van artikel met id " + a.getArtikelId());
		}
		 */
	}
	
	public void verWijderVoorHetEchie(long artikelId) throws GeneriekeFoutmelding{
		artikelQuery = "DELETE FROM ARTIKEL WHERE artikel_id = ?;";
		prijsQuery = "DELETE FROM PRIJS WHERE artikel_id = ?;";

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
			DeLogger.getLogger().error("SQL fout tijdens het verwijderen van artikel met id " + artikelId);
			throw new GeneriekeFoutmelding("SQL fout tijdens het verwijderen van artikel met id " + artikelId);
		}
	}
}



