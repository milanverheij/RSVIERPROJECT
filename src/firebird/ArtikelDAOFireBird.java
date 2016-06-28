package firebird;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;

import exceptions.RSVIERException;
import logger.DeLogger;
import model.Artikel;

/* Author @Douwe Jongeneel
 *
 * Deze klasse regelt een verbinding met de database zodat artikelen aan een bestelling kunnen worden toegevoegd,
 * uitgelezen, geupdate en verwijdert. Er is tevens een methode die alle artikelen uit de database teruggeeft in 
 * een Iterator. Er van uitgaande dat elke bestelling uit max 3 artikelen bestaat maar dat deze artikelen per 
 * bestelling kunnen verschillen.
 *
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
	public int nieuwArtikel(Artikel aNieuw) throws RSVIERException {

		prijsQuery = "INSERT INTO PRIJS (prijs) VALUES (?) RETURNING prijs_id;";
		artikelQuery = "INSERT INTO ARTIKEL (omschrijving, prijs_id, verwachteLevertijd, inAssortiment)"
				+ "VALUES (?, ?, ?, ?) RETURNING artikel_id;";
		String queryUpdate = "UPDATE PRIJS SET artikel_id = ? WHERE prijs_id = ?;";

		try (Connection connection = connPool.verkrijgConnectie();
				PreparedStatement prijsStatement = connection.prepareStatement(prijsQuery);
				PreparedStatement artikelStatement = connection.prepareStatement(artikelQuery);
				PreparedStatement updateStatement = connection.prepareStatement(queryUpdate)){

			connection.setAutoCommit(false);

			//TODO - Controleer of het artikel al in de database staat!!!!!!!!!!!!!!!!!!!!!!!!!!!


			//Zet de prijs gegevens in de PRIJS tabel
			prijsStatement.setBigDecimal(1, aNieuw.getArtikelPrijs());

			try (ResultSet generatedPrijsId = prijsStatement.executeQuery()) {
				if (generatedPrijsId.next()) {
					aNieuw.setPrijsId(generatedPrijsId.getInt("prijs_id"));
				}
			}

			//Zet de artikel gegevens in de ARTIKEL tabel
			artikelStatement.setString(1, aNieuw.getArtikelNaam());
			artikelStatement.setInt(2, aNieuw.getPrijsId());
			artikelStatement.setInt(3, aNieuw.getVerwachteLevertijd());
			// De ternery operator hieronder vertaalt een boolean in een character omdat
			// Firebird geen boolean bevat."1" = true, "0" = false.
			artikelStatement.setString(4, ((aNieuw.isInAssortiment())? "1" : "0"));

			try (ResultSet generatedArtikelId = artikelStatement.executeQuery()) {
				if (generatedArtikelId.next()) {
					aNieuw.setArtikelId(generatedArtikelId.getInt("artikel_id"));
				}
			}

			//Zet artikel_id in PRIJS tabel
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
			throw new RSVIERException("Niew artikel aanmaken kan niet");
		}
	}


	//Read
	@Override
	public Artikel getArtikel(int artikelId) throws RSVIERException {

		artikelQuery = "SELECT * FROM ARTIKEL WHERE artikel_id = ? ;";
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
					// Omdat Firebird geen booleans bevat wordt de ternary operator gebruikt om de 
					// waarde uit de firebird database om te zetten in een boolean.
					artikel.setInAssortiment(artikelRset.getString(6).contains("1") ? true : false);

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
			DeLogger.getLogger().error("TODO");
			throw new RSVIERException("TODO");
		}
	}

	// Onderstaande methode haalt alle artiekelen uit de database en geeft ze terug in een iterator.
	// Er kan gekozen worden om alleen de actieve artikelen of alle artikelen uit de database te halen.
	// artikelActief = 0 vraagt zowel de actieve als inactieve artikelen op.
	// artikelActief = 1 vraagt alleen de actieve artikelen op.
	@Override
	public LinkedHashSet<Artikel> getAlleArtikelen(int artikelActief) throws RSVIERException {

		// Alle artikelen worden in een Set opgeslagen
		LinkedHashSet<Artikel> artikelSet = new LinkedHashSet<>()	;

		// Onderstaande query combineert de ARTIKEL en PRIJS tabel zodat alle artikel gegevens in een
		// keer uitgelezen kunnen worden, tevens kan er geselecteerd worden op alle of alleen actieve artikelen.
		artikelQuery = "SELECT ARTIKEL.artikel_id, ARTIKEL.omschrijving, ARTIKEL.prijs_id, PRIJS.prijs, "
				+ "ARTIKEL.datumAanmaak, ARTIKEL.verwachteLevertijd, ARTIKEL.inAssortiment FROM ARTIKEL "
				+ "LEFT JOIN PRIJS ON ARTIKEL.prijs_id = PRIJS.prijs_id WHERE inAssortiment LIKE ?;";

		try (Connection connection = connPool.verkrijgConnectie();
				PreparedStatement artikelStatement = connection.prepareStatement(artikelQuery)) {

			connection.setAutoCommit(false);
			artikelStatement.setString(1, (artikelActief == 0) ? "%" : "1");

			try (ResultSet artikelRset = artikelStatement.executeQuery()) {
				while (artikelRset.next()){

					artikel = new Artikel();
					artikel.setArtikelId(artikelRset.getInt(1));
					artikel.setArtikelNaam(artikelRset.getString(2));
					artikel.setPrijsId(artikelRset.getInt(3));
					artikel.setArtikelPrijs(artikelRset.getBigDecimal(4));
					artikel.setDatumAanmaak(artikelRset.getString(5));
					artikel.setVerwachteLevertijd(artikelRset.getInt(6));
					// Omdat Firebird geen booleans bevat wordt de ternary operator gebruikt om de 
					// waarde uit de firebird database om te zetten in een boolean.
					artikel.setInAssortiment(artikelRset.getString(6).contains("1") ? true : false);
					artikelSet.add(artikel);
				}
			}
			// Execute transaction
			connection.commit();
			return artikelSet;
		}
		catch (SQLException ex) {
			DeLogger.getLogger().error("SQL fout tijdens opvragen van alle " + ((artikelActief == 0) ? "artikelen " : "actieve artikelen "));
			throw new RSVIERException("SQL fout tijdens opvragen van alle " + ((artikelActief == 0) ? "artikelen " : "actieve artikelen "));
		}
	}

	/*
	 *  Onderstaande methode update de artikel gegevens in de database, de prijs en of artikel gegevens kunnen
	 *  tegelijkertijd en appart geupdate worden. Tevens zorgt de methode ervoor dat er geen inconsistentie op
	 *  kan treden in de artikel prijs gegevens alswel in het artikel object.
	 */

	//Update
	@Override
	public void updateArtikel(int artikelId, Artikel aNieuw) throws RSVIERException {

		boolean dePrijsIsVerandert = false;
		int prijsIdUitDataBase = 0;

		artikelQuery = "UPDATE ARTIKEL SET "
				+ "omschrijving = ?, "
				+ "prijs_id = ?, "
				+ "verwachteLevertijd = ?, "
				+ "inAssortiment = ? "
				+ "WHERE artikel_id = ? ;";

		// Met onderstaande join tables wordt het artikel_id en daarbij behorende prijs verkregen
		prijsQuery = "SELECT ARTIKEL.prijs_id, PRIJS.prijs "
				+ "FROM ARTIKEL "
				+ "LEFT JOIN PRIJS "
				+ "ON ARTIKEL.prijs_id = PRIJS.prijs_id "
				+ "WHERE ARTIKEL.artikel_id = ? ;";

		String nieuwePrijsQuery = "INSERT INTO PRIJS (prijs, artikel_id) "
				+ "VALUES (?, ?) RETURNING prijs_id;";

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
						nieuwePrijsQuery)) {

					nieuwePrijsStatement.setBigDecimal(1, aNieuw.getArtikelPrijs());
					nieuwePrijsStatement.setInt(2, artikelId);

					try (ResultSet prijsIdRset = nieuwePrijsStatement.executeQuery()) {
						if (prijsIdRset.next()) {
							aNieuw.setPrijsId(prijsIdRset.getInt("prijs_id"));
						}
					}
				}
			}

			// Hier worden de artikel gegevens in de database geupdate.
			artikelStatement.setString(1, aNieuw.getArtikelNaam());
			artikelStatement.setInt(2, aNieuw.getPrijsId());
			artikelStatement.setInt(3, aNieuw.getVerwachteLevertijd());
			// De ternery operator hieronder vertaalt een boolean in een character omdat
			// Firebird geen boolean bevat."1" = true, "0" = false.
			artikelStatement.setString(4, ((aNieuw.isInAssortiment())? "1" : "0"));
			artikelStatement.executeUpdate();

			//Execute transaction
			connection.commit();
			connection.setAutoCommit(true);


		}
		catch (SQLException ex) {
			DeLogger.getLogger().error("SQL fout tijdens updaten van artikel met artikel_id " + artikelId);
			throw new RSVIERException("SQL fout tijdens updaten van artikel met artikel_id " + artikelId);
		}

	}

	//Delete
	@Override
	public void verwijderArtikel(Artikel a) throws RSVIERException {

		artikelQuery = "UPDATE ARTIKEL SET inAssortiment = ? WHERE artikel_id = ?;";

		try(Connection connection = connPool.verkrijgConnectie();
				PreparedStatement artikelStatement = connection.prepareStatement(artikelQuery)) {

			connection.setAutoCommit(false);
			artikelStatement.setString(1, "0"); // Omdat firebird geen boolean accepteerd
			artikelStatement.setInt(2, a.getArtikelId());
			artikelStatement.executeUpdate();
			connection.commit();

		}
		catch (SQLException ex) {
			DeLogger.getLogger().error("SQL fout tijdens het verwijderen van artikel met id " + a.getArtikelId());
			throw new RSVIERException("SQL fout tijdens het verwijderen van artikel met id " + a.getArtikelId());
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
				throw new RSVIERException("SQL fout tijdens het verwijderen van artikel met id " + a.getArtikelId());
			}
		 */
	}
}
