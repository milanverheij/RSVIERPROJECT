package mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import exceptions.RSVIERException;
import model.Artikel;

/* Author @Douwe Jongeneel
 *
 * Deze klasse regelt een verbinding met de database zodat artikelen aan een bestelling kunnen worden toegevoegd,
 * uitgelezen, geupdate en verwijdert. Er is tevens een methode die alle artikelen uit de database teruggeeft.
 * Er van uitgaande dat elke bestelling uit max 3 artikelen bestaat maar dat deze artikelen per bestelling kunnen
 * verschillen.
 *
 * TODO1 - artikel_id is nog een int, moet dit ook een long worden? in database was het een varchar geloof ik.
 */
public class ArtikelDAOMySQL extends AbstractDAOMySQL implements interfaces.ArtikelDAO{
	private Artikel artikel;
	private Connection connection;
	private LinkedHashMap<Long, Integer> artikelCountMap = new LinkedHashMap<>();

	public ArtikelDAOMySQL() {
	}

	//Create

	// Onderstaande methode voegt een artikel toe aan een bestelling zolang er nog geen drie artikelen 
	// in de bestelling aanwezig zijn.
	
	@Override 
	public void nieuwArtikelOpBestelling(long bestelling_id, Artikel aNieuw) throws RSVIERException {
		connection = MySQLConnectie.getConnection();
		boolean kanArtikelToevoegen = true;

		try {
			// Hier wordt gecontroleerd hoeveel artikelen zich in de bestelling bevinden, zolang er
			// nog geen drie artikelen zijn kan het nieuwe artikel worden toegevoegd.
			if (!artikelCountMap.containsKey(bestelling_id)) {
				artikelCountMap.put(bestelling_id, 1);
			}
			else if (artikelCountMap.get(bestelling_id) >= 3) {
				System.out.println("Kan geen artikel toevoegen, maximaal 3 artikelen!");
				kanArtikelToevoegen = false;
			}
			else if (artikelCountMap.containsKey(bestelling_id)) {
				artikelCountMap.put(bestelling_id, artikelCountMap.get(bestelling_id) + 1);
			}
			if (kanArtikelToevoegen) {
				// Het nieuwe artikel wordt toegevoegd aan artikel1, 2 of 3 op basis van het aantal artikelen dat zich
				// in de bestelling bevind, wat gecontroleerd wordt middels de Integer value uit artikelCountMap.
				updateArtikelOpBestelling(bestelling_id,artikelCountMap.get(bestelling_id), aNieuw);
			}
		}
		catch(RSVIERException e) {
			e.printStackTrace();
		}
		finally {
			MySQLHelper.close(connection, statement);
		}

	}

	//Read
	
	// Onderstaande methode vraagt een artikel op van de bestelling op basis van het artikelNummer(artikel1, artikel2 of artikel3).
	@Override
	public Artikel getArtikelOpBestelling(long bestelling_id, int artikelNummer) throws RSVIERException {
		connection = MySQLConnectie.getConnection();
		try {
			if (artikelNummer <= 0 & artikelNummer > 3) {
				throw new RSVIERException("artikelNummer is incorrect, kies 1, 2 of 3.");
			}
			statement = connection.prepareStatement("SELECT artikel" + artikelNummer + "_id, "
					+ "artikel" + artikelNummer + "_naam, artikel" + artikelNummer + "_prijs "
					+ "FROM BESTELLING "
					+ "WHERE bestelling_id = " + bestelling_id + ";");
			ResultSet rset = statement.executeQuery();
			rset.next();
			artikel = new Artikel(Integer.parseInt(rset.getString(1)), rset.getString(2), rset.getDouble(3));
		}
		catch (RSVIERException e) {
		}
		catch (SQLException e) {
			throw new RSVIERException("SQLexception tijdens opvragen artikel" + artikelNummer);
		}
		finally {
			MySQLHelper.close(connection, statement);
		}
		return artikel;
	}
	
	// Onderstaande methode retourneert alle artikelen van de bestelling in een Iterator
	@Override
	public Iterator<Artikel> getAlleArtikelenOpBestelling(long bestelling_id) throws RSVIERException {
		connection = MySQLConnectie.getConnection();
		//Sla alle artikelen van de bestelling op in een ArrayList
		ArrayList<Artikel> artikelLijst = new ArrayList<>();

		try {
			statement = connection.prepareStatement("SELECT artikel1_id, artikel1_naam, artikel1_prijs, "
					+ "artikel2_id, artikel2_naam, artikel2_prijs, "
					+ "artikel3_id, artikel3_naam, artikel3_prijs "
					+ "FROM BESTELLING "
					+ "WHERE bestelling_id = " + bestelling_id + ";");
			ResultSet rset = statement.executeQuery();

			rset.next(); //Doorloop rset en voeg alle unieke artikelen toe en update hun aantal
			for(int x = 1; x <=7 ; x+=3) {
				if (rset.getString(x) != null) { //Bestelling bevat artikel
					artikel = new Artikel(Integer.parseInt(rset.getString(x)), rset.getString(x+1), rset.getDouble(x+2));
					artikelLijst.add(artikel);
				}
			}
			return artikelLijst.iterator();
		}

		catch (SQLException e) {
			System.out.println("SQLexception tijdens getAlleArtikelen()");
			e.printStackTrace();
		}
		finally {
			MySQLHelper.close(connection, statement);
		}
		return null;
	}
	
	// Onderstaande methode retouneerd alle unieke artikelen + hoevaak ze besteld zijn in een Iterator
	@Override
	public Iterator<Entry<Artikel, Integer>> getAlleArtikelen() throws RSVIERException {
		connection = MySQLConnectie.getConnection();
		// Sla alle unieke artikelen binnen de tabel BESTELLING op in een map
		LinkedHashMap<Artikel, Integer> map = new LinkedHashMap<>();
		// Alle unieke atikelen worden in een iterator geretouneerd
		Iterator<Entry<Artikel,Integer>> iterator = null;

		try {
			statement = connection.prepareStatement("SELECT artikel1_id, artikel1_naam, artikel1_prijs, "
					+ "artikel2_id, artikel2_naam, artikel2_prijs, "
					+ "artikel3_id, artikel3_naam, artikel3_prijs "
					+ "FROM BESTELLING ;");
			ResultSet rset = statement.executeQuery();
			
			while (rset.next()) { //Doorloop rset en voeg alle unieke artikelen toe en update hun aantal
				for(int x = 1; x <=7 ; x+=3) {
					if (rset.getString(x) != null) { //Zolang de bestelling artikelen bevat voeg ze toe aan de map
						artikel = new Artikel(Integer.parseInt(rset.getString(x)), rset.getString(x+1), rset.getDouble(x+2));
						voegArtikelToeAanMap(map, artikel);
					}
				}
			}
			return iterator = map.entrySet().iterator();
		}
		catch (SQLException e) {
			throw new RSVIERException("SQLexception tijdens getAlleArtikelen()");
		}
		finally {
			MySQLHelper.close(connection, statement);
		}
	}


	//Update
	
	// Onderstaande methode vervangt het artikel dat zich op het doorgegeven artikel(1, 2, 3) nummer bevind met het nieuwe artikel aNieuw
	@Override
	public void updateArtikelOpBestelling(long bestelling_id, int artikelNummer, Artikel aNieuw) throws RSVIERException {
		connection = MySQLConnectie.getConnection();
		
		try {
			statement = connection.prepareStatement("UPDATE BESTELLING SET "
					+ "artikel" + artikelNummer + "_id = ?,"
					+ "artikel" + artikelNummer + "_naam = ?,"
					+ "artikel" + artikelNummer + "_prijs = ? "
					+ "WHERE bestelling_id = " + bestelling_id + ";");

			statement.setString(1, "" + aNieuw.getArtikel_id());
			statement.setString(2, aNieuw.getArtikel_naam());
			statement.setDouble(3, aNieuw.getArtikel_prijs());

			statement.executeUpdate();

		}
		catch (SQLException e) {
			System.out.println("SQLexception update artikel " + artikelNummer + " ging verkeerd");
			e.printStackTrace();
		}
		finally {
			MySQLHelper.close(connection, statement);
		}
	}
	
	// Onderstaande methode vervangt Artikel aOud met Artikel aNieuw op bestelling(bestelling_id).
	@Override
	public void updateArtikelOpBestelling(long bestelling_id, Artikel aOud, Artikel aNieuw) throws RSVIERException {
		connection = MySQLConnectie.getConnection();
		int artikelNummer = 0;
		
		try {
			// Kijk of het artikel dat geupdate dient te worden artikel1, 2 of 3 is in de DataBase.
			statement = connection.prepareStatement("SELECT artikel1_id, artikel2_id, artikel3_id "
					+ "FROM BESTELLING WHERE bestelling_id = " + bestelling_id + ";");
			ResultSet rset = statement.executeQuery();
			rset.next();
			for(int i = 1; i <= rset.getMetaData().getColumnCount(); i++) {
				if (rset.getInt(/*vanKolom*/i) == aOud.getArtikel_id()) {
					artikelNummer = i;
				}
			}
			// Wanneer de bestelling het oude artikel niet bevat t
			if (artikelNummer == 0) {
				throw new RSVIERException("Kan artikel(" + aOud.toString() + ") niet vervangen "
						+ "omdat het zich niet op bestelling" + bestelling_id + " bevind!");
			}
			// Update de gegevens op locatie aOud met aNieuw
			statement = connection.prepareStatement("UPDATE BESTELLING SET "
					+ "artikel" + artikelNummer + "_id = ?,"
					+ "artikel" + artikelNummer + "_naam = ?,"
					+ "artikel" + artikelNummer + "_prijs = ? "
					+ "WHERE bestelling_id = " + bestelling_id + ";");

			statement.setString(1, "" + aNieuw.getArtikel_id());
			statement.setString(2, aNieuw.getArtikel_naam());
			statement.setDouble(3, aNieuw.getArtikel_prijs());

			statement.executeUpdate();

		}
		catch (RSVIERException e) {
		}
		catch (SQLException e) {
			throw new RSVIERException("SQLexception update artikel " + artikelNummer + " ging verkeerd");
		}
		finally {
			MySQLHelper.close(connection, statement);
		}
	}
	
	
	// Update alle artikelen van bestelling(bestelling_id) met drie nieuwe artikelen.
	@Override 
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
			System.out.println("SQLexception update artikelen van bestelling met"
					+ bestelling_id + " ging verkeerd");
			e.printStackTrace();
		}
		finally {
			MySQLHelper.close(connection, statement);
		}
	}

	// Delete
	
	// Onderstaande methode verwijdert de artikel gegevens van aOud van de bestelling door de waardes 
	// in de database allemaal op 0 te zetten. 
	@Override
	public void verwijderArtikelVanBestelling(long bestelling_id, Artikel aOud) throws RSVIERException {
		//Maak een artikel dat alle waardes op 0 zet
		Artikel artikelWisser = new Artikel(0, "0", 0);

		// Onderstaande methode update de gegevens van aOud met aNieuw
		updateArtikelOpBestelling(bestelling_id, aOud, artikelWisser);
	}

	// Onderstaande methode verwijdert alle artikel gegevens van de bestelling door de waardes in de
	// database op 0 te zetten.
	@Override
	public void verwijderAlleArtikelenVanBestelling(long bestelling_id) throws RSVIERException {
		//artikel met waardes 0
		Artikel artikelWisser = new Artikel(0, "0", 0) ;

		//Onderstaande methode update alle artikel gegevens naar 0
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
