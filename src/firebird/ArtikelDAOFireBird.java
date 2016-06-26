package firebird;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
public class ArtikelDAOFireBird extends AbstractDAOFireBird implements interfaces.ArtikelDAO{
	private String artikelQuery = "";
	private String prijsQuery = "";
	private Artikel artikel = new Artikel();


	public ArtikelDAOFireBird() {
	}

	//Create

	// Onderstaande methode maakt een nieuw artikel aan in de ARTIKEL tabel, zet de prijs gegevens van het artikel in de PRIJS
	// tabel en zorgt ervoor dat de ARTIKEL tabel het juiste prijs_id heeft.
	@Override 
	public int nieuwArtikel(Artikel aNieuw) throws GeneriekeFoutmelding {

		String queryPrijs = "INSERT INTO PRIJS (artikel_id, prijs) VALUES (?, ?) RETURNING prijs_id;"; //TODO - prijs_id autoincrement!!!!!!
		String queryArtikel = "INSERT INTO ARTIKEL (omschrijving, prijs_id, verwachteLevertijd, inAssortiment)"
				+ "VALUES (?, ?, ?, ?) RETURNING ARTIKEL_ID;";
		String queryUpdate = "UPDATE ARTIKEL SET prijs_id = ? WHERE artikel_id = ?;";

		try (Connection connection = connPool.verkrijgConnectie();

				PreparedStatement statementPrijs = connection.prepareStatement(queryPrijs);
				PreparedStatement statementArtikel = connection.prepareStatement(queryArtikel);
				PreparedStatement statementUpdate = connection.prepareStatement(queryUpdate)){

			connection.setAutoCommit(false);

			//Zet de artikel gegevens in de ARTIKEL tabel
			statementArtikel.setString(1, aNieuw.getArtikelNaam());
			statementArtikel.setInt(2, aNieuw.getPrijsId());
			statementArtikel.setInt(3, aNieuw.getVerwachteLevertijd());
			statementArtikel.setBoolean(4, aNieuw.isInAssortiment());


			try (ResultSet generatedArtikelId = statementArtikel.executeQuery()) {
				if (generatedArtikelId.next()) {
					aNieuw.setArtikelId(generatedArtikelId.getInt(1));
				}
			}

			//Zet de prijs gegevens in de PRIJS tabel
			statementPrijs.setLong(1, aNieuw.getArtikelId());
			statementPrijs.setBigDecimal(2, aNieuw.getArtikelPrijs());

			try (ResultSet generatedprijs_id = statementPrijs.executeQuery()) {
				if (generatedprijs_id.next()) {
					aNieuw.setPrijsId(generatedprijs_id.getInt(1));

				}
			}
			//Zet artikel_id in PRIJS tabel
			statementUpdate.setInt(2, aNieuw.getArtikelId());
			statementUpdate.setInt(1, aNieuw.getPrijsId());
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
		artikelQuery = "SELECT * FROM ARTIKEL WHERE artikel_id = ?;";
		prijsQuery	= "SELECT prijs FROM PRIJS WHERE prijs_id = ? ;";

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
			ex.printStackTrace();
			DeLogger.getLogger().error("TODO");
			throw new GeneriekeFoutmelding("TODO");
		}
	}

	// Onderstaande methode haalt alle artiekelen uit de database en geeft ze terug in een iterator.
	// Er kan gekozen worden om alleen de actieve artikelen of alle artikelen uit de database te halen.
	// artikelActief = 0 vraagt zowel de actieve als inactieve artikelen op.
	// artikelActief = 1 vraagt alleen de actieve artikelen op.
	public LinkedHashSet<Artikel> getAlleArtikelen(boolean artikelActief) throws GeneriekeFoutmelding {

		// Alle artikelen worden in een Set opgeslagen
		LinkedHashSet<Artikel> artikelSet = new LinkedHashSet<>()	;

		// Onderstaande query combineert de ARTIKEL en PRIJS tabel zodat alle artikel gegevens in een
		// keer uitgelezen kunnen worden, tevens kan er geselecteerd worden op alle of alleen actieve artikelen.
		artikelQuery = "SELECT ARTIKEL.artikel_id, ARTIKEL.omschrijving, Artikel.prijs_id, PRIJS.prijs, "
				+ "ARTIKEL.datumAanmaak, ARTIKEL.verwachteLevertijd, ARTIKEL.inAssortiment FROM ARTIKEL "
				+ "LEFT JOIN PRIJS ON Artikel.prijs_id = PRIJS.prijs_id WHERE inAssortiment LIKE ?;";

		try (Connection connection = connPool.verkrijgConnectie();

				PreparedStatement artikelStatement = connection.prepareStatement(artikelQuery)) {

			if(artikelActief)
				artikelStatement.setString(1, "%");
			else
				artikelStatement.setBoolean(1, true);

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
		int prijs_idUitDataBase = 0;

		artikelQuery = "UPDATE ARTIKEL SET "
				+ "omschrijving = ?, "
				+ "prijs_id = ?, "
				+ "verwachteLevertijd = ?, "
				+ "inAssortiment = ? "
				+ "WHERE artikel_id = ?;";

		// Met onderstaande join tables wordt het artikel_id en daarbij behorende prijs verkregen
		prijsQuery = "SELECT Artikel.prijs_id, PRIJS.prijs "
				+ "FROM ARTIKEL "
				+ "LEFT JOIN PRIJS "
				+ "ON Artikel.prijs_id = PRIJS.prijs_id "
				+ "WHERE ARTIKEL.artikel_id = ?;";

		String nieuwePrijsQuery = "INSERT INTO PRIJS (prijs, artikel_id) "
				+ "VALUES (?, ?) RETURNING prijs_id;";

		try (Connection connection = connPool.verkrijgConnectie();

				PreparedStatement artikelStatement = connection.prepareStatement(artikelQuery);
				PreparedStatement prijsStatement = connection.prepareStatement(prijsQuery)) {


			connection.setAutoCommit(false);
			aNieuw.setArtikelId(artikelId);

			prijsStatement.setLong(1, artikelId);

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
					// Wanneer de prijs niet verandert, mag het prijs_id ook niet veranderen.
					// Hier wordt vastgesteld dat het prijs_id alleen mag veranderen wanneer
					// de prijs verandert is. Als de prijs niet verandert wordt het prijs_id
					// van aNieuw geupdate zodat het weer overeenkomt met de gegevens in de 
					// database. Op deze manier kunnen artikelen geen onrechtmatige prijzen
					// worden toegekend.
					if (prijsRset.getInt(1) != aNieuw.getPrijsId() && !dePrijsIsVerandert) {

						aNieuw.setPrijsId(prijsRset.getInt(1));
						String dataInconsistentie = "Data inconsistentie: Het prijs_id van aNieuw(" + aNieuw.toString() + ")\n "
								+ "verschilt van het prijs_id uit de DB " + prijs_idUitDataBase + " terwijl "
								+ "de prijs niet verandert is!!!";
						DeLogger.getLogger().info(dataInconsistentie);
					}
				}
			}

			if (dePrijsIsVerandert) {

				// Wanneer de prijs van het artikel verandert is dan wordt hier de nieuwe 
				// prijs in de database gezet.
				try (PreparedStatement nieuwePrijsStatement = connection.prepareStatement(
						nieuwePrijsQuery)) {

					nieuwePrijsStatement.setBigDecimal(1, aNieuw.getArtikelPrijs());
					nieuwePrijsStatement.setLong(2, artikelId);


					try (ResultSet prijs_idRset = nieuwePrijsStatement.executeQuery()) {
						if (prijs_idRset.next()) {
							aNieuw.setPrijsId(prijs_idRset.getInt(1));
						}
					}
				}
			}

			// Hier worden de artikel gegevens in de database geupdate.
			artikelStatement.setString(1, aNieuw.getArtikelNaam());
			artikelStatement.setInt(2, aNieuw.getPrijsId());
			artikelStatement.setInt(3, aNieuw.getVerwachteLevertijd());
			artikelStatement.setBoolean(4, aNieuw.isInAssortiment());
			artikelStatement.setLong(5, artikelId);
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
	public void verwijderArtikel(long artikelId) throws GeneriekeFoutmelding {

		artikelQuery = "UPDATE ARTIKEL SET inAssortiment = 0 WHERE artikel_id = ?;";

		try(Connection connection = connPool.verkrijgConnectie();

				PreparedStatement artikelStatement = connection.prepareStatement(artikelQuery)) {

			artikelStatement.setLong(1, artikelId);
			artikelStatement.executeUpdate();

		}
		catch (SQLException ex) {
			ex.printStackTrace();
			DeLogger.getLogger().error("SQL fout tijdens het verwijderen van artikel met id " + artikelId);
			throw new GeneriekeFoutmelding("SQL fout tijdens het verwijderen van artikel met id " + artikelId);
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
	
	public void verwijderVoorHetEchie(long artikelId) throws GeneriekeFoutmelding{
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
	
	public void verwijderAllesVoorHetEchie() throws GeneriekeFoutmelding{
		artikelQuery = "DELETE FROM ARTIKEL;";
		String bestelHeeftQuery = "DELETE FROM BESTELLING_HEEFT_ARTIKEL;";
		String bestelQuery = "DELETE FROM BESTELLING;";
		prijsQuery = "DELETE FROM PRIJS;";

		try (Connection connection = connPool.verkrijgConnectie();
				PreparedStatement verwijderArtikelStatement = connection.prepareStatement(artikelQuery);
				PreparedStatement verwijderBestellingStatement = connection.prepareStatement(bestelQuery);
				PreparedStatement verwijderBestellingHeeftStatement = connection.prepareStatement(bestelHeeftQuery);
				PreparedStatement verwijderPrijsStatement = connection.prepareStatement(prijsQuery)) {

			connection.setAutoCommit(false);
			verwijderBestellingHeeftStatement.executeUpdate();
			
			verwijderBestellingStatement.executeUpdate();
			
//			verwijderPrijsStatement.setLong(1, artikelId);
			verwijderPrijsStatement.executeUpdate();

//			verwijderArtikelStatement.setLong(1, artikelId);
			verwijderArtikelStatement.executeUpdate();
			
			

			connection.commit();
		}
		catch (SQLException  ex) {
			ex.printStackTrace();
			DeLogger.getLogger().error("SQL fout tijdens het verwijderen van artikel met id ");
			throw new GeneriekeFoutmelding("SQL fout tijdens het verwijderen van artikel met id ");
		}
	}
}