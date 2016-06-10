package mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map.Entry;

import exceptions.RSVIERException;
import model.Artikel;

/* Author @Douwe Jongeneel on 07-06-16
 *
 * Deze klasse regelt een verbinding met de database zodat artikelen aan een bestelling kunnen worden toegevoegd,
 * uitgelezen, geupdate en verwijdert. Er is tevens een methode die alle artikelen uit de database teruggeeft in 
 * een Iterator<Entry<Artikel,Integer>> vorm. Er van uitgaande dat elke bestelling uit max 3 artikelen bestaat 
 * maar dat deze artikelen per bestelling kunnen verschillen.
 */
public class ArtikelDAOMySQL extends AbstractDAOMySQL implements interfaces.ArtikelDAO{
	private Artikel artikel;
	private Connection connection;
	//ArtikelCountMap zorgt ervoor dat er per bestelling maar drie artikelen kunnen zijn
	private LinkedHashMap<Long, Integer> ArtikelCountMap = new LinkedHashMap<>();

	//Default no-args Constructor
	public ArtikelDAOMySQL() {
	}

	//Create
	/*
	 * Deze methode voegt een artikel toe aan een bestelling, Krijg alleen de volgende error
	 * ->java.sql.SQLException: Field 'klant_id' doesn't have a default value<-
	 * Kan het oplossten door een update te doen inplaats van een nieuwe creeren.
	 * Maar wou kijken of het ook op deze manier kon.
	 */
	@Override //voeg een artikel toe aan een bestelling zolang er nog geen drie artikelen zijn
	public void nieuwArtikelOpBestelling(long bestelling_id, Artikel a) throws RSVIERException {
		connection = MySQLConnectie.getConnection();
		boolean kanArtikelToevoegen = true;

		try {
			//Hier wordt gekeken hoeveel artikelen de bestelling al heeft. Het zorgt ervoor dat 
			//een artikel wordt toegevoegd aan een bestelling zolang er nog geen 3 artikelen zijn.
			if (!ArtikelCountMap.containsKey(bestelling_id)) {
				ArtikelCountMap.put(bestelling_id, 1);
			}
			else if (ArtikelCountMap.get(bestelling_id) >= 3) {
				System.out.println("Kan geen artikel toevoegen, maximaal 3 artikelen!");
				kanArtikelToevoegen = false;
			}
			else if (ArtikelCountMap.containsKey(bestelling_id)) {
				ArtikelCountMap.put(bestelling_id, ArtikelCountMap.get(bestelling_id) + 1);
			}
			if (kanArtikelToevoegen) {//Zolang er nog geen drie artikelen zijn, voeg artikel toe
				
				//In het statement wordt bijgehouden of artikel 1, 3 of 3 wordt toegevoegd
				statement = connection.prepareStatement("INSERT INTO BESTELLING ("
						+ "bestelling_id, "
						+ "artikel" + ArtikelCountMap.get(bestelling_id) + "_id, "
						+ "artikel" + ArtikelCountMap.get(bestelling_id)  + "_naam, "
						+ "artikel" + ArtikelCountMap.get(bestelling_id)  + "_prijs) "
						+ "VALUES (?, ?, ?, ?) "
						+ "ON DUPLICATE KEY UPDATE "
						+ "artikel" + ArtikelCountMap.get(bestelling_id) + "_id = ?, "
						+ "artikel" + ArtikelCountMap.get(bestelling_id)  + "_naam = ?, "
						+ "artikel" + ArtikelCountMap.get(bestelling_id)  + "_prijs = ?;");

				statement.setInt(1, (int) bestelling_id);
				statement.setString(2, "" + a.getArtikel_id());
				statement.setString(3, a.getArtikel_naam());
				statement.setDouble(4, a.getArtikel_prijs());
				statement.setString(5, "" + a.getArtikel_id());
				statement.setString(6, a.getArtikel_naam());
				statement.setDouble(7, a.getArtikel_prijs());

				statement.executeUpdate();
			}
		}
		catch (SQLException e) {
			throw new RSVIERException("SQL exception tijdens aanmaken artikel" + ArtikelCountMap.get(bestelling_id)
			+ " voor bestelling " + bestelling_id);
		}
		finally {
			MySQLHelper.close(connection, statement);
		}

	}

	//Read
	@Override //Geeft een artikel van een bestelling terug als Artikel object
	public Artikel getArtikelOpBestelling(long bestelling_id, int artikelNummer) throws RSVIERException {
		connection = MySQLConnectie.getConnection();
		try {
			if (artikelNummer <= 0 & artikelNummer > 3) {
				System.out.println("artikelNummer is incorrect, kies 1, 2 of 3.");
			}
			statement = connection.prepareStatement("SELECT artikel" + artikelNummer + "_id, "
					+ "artikel" + artikelNummer + "_naam, artikel" + artikelNummer + "_prijs "
					+ "FROM BESTELLING "
					+ "WHERE bestelling_id = " + bestelling_id + ";");
			ResultSet rset = statement.executeQuery();
			rset.next();
			artikel = new Artikel(Integer.parseInt(rset.getString(1)), rset.getString(2), rset.getDouble(3));
		}
		catch (SQLException e) {
			throw new RSVIERException("SQLexception tijdens opvragen artikel" + artikelNummer);
		}
		finally {
			MySQLHelper.close(connection, statement);
		}
		return artikel;
	}
	
	//Methode leest alle artikelen uit een bestelling en geeft ze terug in een Iterator<Artikel>.
	
	@Override
	public Iterator<Artikel> getAlleArtikelenOpBestelling(long bestelling_id) throws RSVIERException {
		connection = MySQLConnectie.getConnection();
		//Sla alle artikelen van de bestelling op in een LinkedHashSet
		LinkedHashSet<Artikel> artikelLijst = new LinkedHashSet<>();

		try {
			statement = connection.prepareStatement("SELECT artikel1_id, artikel1_naam, artikel1_prijs, "
					+ "artikel2_id, artikel2_naam, artikel2_prijs, "
					+ "artikel3_id, artikel3_naam, artikel3_prijs "
					+ "FROM BESTELLING "
					+ "WHERE bestelling_id = " + bestelling_id + ";");
			ResultSet rset = statement.executeQuery();
			rset.next();// move pointer to first record/row

			//Lees een artikel wanneer de bestelling het artikel bevat, !rset.wasNull() zorgt ervoor dat geen null waardes gelezen worden
			if (rset.getRow() == 1 & !rset.wasNull()) { //Bestelling bevat artikel1
				artikel = new Artikel(Integer.parseInt(rset.getString(1)), rset.getString(2), rset.getDouble(3));
				artikelLijst.add(artikel);
			}
			if (rset.getRow() == 4 & !rset.wasNull()) { //Bestelling bevat artikel2
				artikel = new Artikel(Integer.parseInt(rset.getString(4)), rset.getString(5), rset.getDouble(6));
				artikelLijst.add(artikel);
			}
			if (rset.getRow() == 7 & !rset.wasNull()) { //Bestelling bevat artikel3
				artikel = new Artikel(Integer.parseInt(rset.getString(7)), rset.getString(8), rset.getDouble(9));
				artikelLijst.add(artikel);
			}
		}
		catch (SQLException e) {
			throw new RSVIERException("SQLexception tijdens getAlleArtikelen()");
		}
		finally {
			MySQLHelper.close(connection, statement);
		}
		return artikelLijst.iterator();
	}
	/*
	 * Deze methode stopt alle unieke artikelen in een LinkedHashMap en geeft deze terug
	 * in een Iterator<Entry<Artikel, Integer>> waar alle unieke artikelen inzitten en 
	 * hoevaak het artikel besteld is.
	 * 
	 * TODO - de methode werkt, alleen krijg ik nog niet alle artikelen terug, maar goed
	 * genoeg voor nu.
	 */
	@Override
	public Iterator<Entry<Artikel, Integer>> getAlleArtikelen() throws RSVIERException {
		connection = MySQLConnectie.getConnection();
		//Sla alle unieke artikelen binnen de tabel Bestelling op in een map
		LinkedHashMap<Artikel, Integer> map = new LinkedHashMap<>();
		LinkedHashSet<Entry<Artikel, Integer>> artikelMapEntries = new LinkedHashSet<>();

		try {
			statement = connection.prepareStatement("SELECT artikel1_id, artikel1_naam, artikel1_prijs, "
					+ "artikel2_id, artikel2_naam, artikel2_prijs, "
					+ "artikel3_id, artikel3_naam, artikel3_prijs "
					+ "FROM BESTELLING ;");
			ResultSet rset = statement.executeQuery();

			while (rset.next()) { //Doorloop rset en voeg alle unieke artikelen toe en update hun aantal
				if (rset.getRow() == 1 & !rset.wasNull()) { //Bestelling bevat artikel1
					artikel = new Artikel(Integer.parseInt(rset.getString(1)), rset.getString(2), rset.getDouble(3));
					voegArtikelToeAanMap(map, artikel);
				}
				if (rset.getRow() == 4 & !rset.wasNull()) { //Bestelling bevat artikel2
					artikel = new Artikel(Integer.parseInt(rset.getString(4)), rset.getString(5), rset.getDouble(6));
					voegArtikelToeAanMap(map, artikel);
				}
				if (rset.getRow() == 7 & !rset.wasNull()) { //Bestelling bevat artikel3
					artikel = new Artikel(Integer.parseInt(rset.getString(7)), rset.getString(8), rset.getDouble(9));
					voegArtikelToeAanMap(map, artikel);
				}
				rset.next();
			}
			// De map wordt omgezet in LinkedHashSet<Entry<Artikel, Iteger>>
			artikelMapEntries.addAll(map.entrySet());
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
		}
		catch (SQLException e) {
			throw new RSVIERException("SQLexception tijdens getAlleArtikelen()");
		}
		finally {
			MySQLHelper.close(connection, statement);
		}
		// Hier wordt een Iterator<Entry<Artikel, Integer>> geretouneerd.
		return artikelMapEntries.iterator();
	}


	//Update
	@Override //pas een artikel van een bestelling aan door het nummer van het artikel mee te geven.
	public void updateArtikelOpBestelling(long bestelling_id, int artikelNummer, Artikel a1) throws RSVIERException {
		connection = MySQLConnectie.getConnection();
		try {
			statement = connection.prepareStatement("UPDATE BESTELLING SET "
					+ "artikel" + artikelNummer + "_id = ?,"
					+ "artikel" + artikelNummer + "_naam = ?,"
					+ "artikel" + artikelNummer + "_prijs = ? "
					+ "WHERE bestelling_id = " + bestelling_id + ";");

			statement.setString(1, "" + a1.getArtikel_id());
			statement.setString(2, a1.getArtikel_naam());
			statement.setDouble(3, a1.getArtikel_prijs());

			statement.executeUpdate();

		}
		catch (SQLException e) {
			throw new RSVIERException("SQLexception update artikel " + artikelNummer + " ging verkeerd");
		}
		finally {
			MySQLHelper.close(connection, statement);
		}
	}

	@Override //Pas alle artikelen in een keer aan door alle artikelen mee te geven
	public void updateAlleArtikelenOpBestelling(long bestelling_id, Artikel a1, Artikel a2, Artikel a3)
			throws RSVIERException {
		connection = MySQLConnectie.getConnection();
		try {
			statement = connection.prepareStatement("UPDATE BESTELLING SET "
					+ "artikel1_id = ?, artikel1_naam = ?, artikel1_prijs = ?, "
					+ "artikel2_id = ?, artikel2_naam = ?, artikel2_prijs = ?, "
					+ "artikel3_id = ?, artikel3_naam = ?, artikel3_prijs = ? "
					+ "WHERE bestelling_id = " + bestelling_id + ";");

			statement.setString(1, "" + a1.getArtikel_id());
			statement.setString(2, a1.getArtikel_naam());
			statement.setDouble(3, a1.getArtikel_prijs());
			statement.setString(4, "" + a2.getArtikel_id());
			statement.setString(5, a2.getArtikel_naam());
			statement.setDouble(6, a2.getArtikel_prijs());
			statement.setString(7, "" + a3.getArtikel_id());
			statement.setString(8, a3.getArtikel_naam());
			statement.setDouble(9, a3.getArtikel_prijs());

			statement.executeUpdate();

		}
		catch (SQLException e) {
			throw new RSVIERException("SQLexception update artikelen van bestelling met"
					+ bestelling_id + " ging verkeerd");
		}
		finally {
			MySQLHelper.close(connection, statement);
		}
	}

	//Delete
	/*
	 * Deze methode zet de gegevens op -1 van het artikel met artikelnummer in bestelling met bestelling_id.
	 * -1 geeft aan dat het artikel onrealistisch is, oftewel niet bestaat.
	 * Het is niet mogelijk om een artikel te verwijderen zonder de gehele bestelling te verwijderen
	 * maar op deze manier kunnen we wel een artikel in een bestelling resetten/verwijderen.
	 */
	@Override
	public void verwijderArtikelVanBestelling(long bestelling_id, int artikelNummer) throws RSVIERException {
		//Maak een artikel dat alle waardes op -1 zet
		Artikel artikelWisser = new Artikel(-1, "-1", -1);

		//Onderstaande methode maakt een connectie, vangt exception en sluit de connectie + statement.
		updateArtikelOpBestelling(bestelling_id, artikelNummer, artikelWisser);
	}

	// Deze methode zet de waardes van alle artikelen op -1 van bestelling met bestelling_id.
	@Override
	public void verwijderAlleArtikelenVanBestelling(long bestelling_id) throws RSVIERException {
		//artikel met waardes -1
		Artikel artikelWisser = new Artikel(-1, "-1", -1) ;

		//De aangeroepen methode maakt verbinding, vangt exceptie en sluit verbinding + statement
		updateAlleArtikelenOpBestelling(bestelling_id, artikelWisser, artikelWisser, artikelWisser);
	}

	//Utility
	//Methodes
	public void voegArtikelToeAanMap(LinkedHashMap<Artikel, Integer> map, Artikel artikel) {
		if (map.containsKey(artikel)) {
			map.put(artikel, map.get(artikel) + 1); //Aantal + 1
		}
		else {
			map.put(artikel, 1);
		}
	}
}
