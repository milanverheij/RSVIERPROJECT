package mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map.Entry;

import exceptions.RSVIERException;
import model.Artikel;

/* Author @Douwe Jongeneel on 07-06-16
 *
 * Deze klasse regelt een verbinding met de database zodat artikelen aan een bestelling kunnen worden toegevoegd,
 * uitgelezen, geupdate en verwijdert. Er is tevens een methode die alle artikelen uit de database teruggeeft.
 * Er van uitgaande dat elke bestelling uit max 3 artikelen bestaat maar dat deze artikelen per bestelling kunnen
 * verschillen.
 *
 * TODO1 - ik heb onderstaande methodes nog niet uitvoerig getest, dus ik weet nog niet of alles correct werkt.
 * TODO2 - artikel_id is nog een int, moet dit ook een long worden? in database was het een varchar geloof ik.
 */
public class ArtikelDAOMySQL extends AbstractDAOMySQL implements interfaces.ArtikelDAO{
	private Artikel artikel;
	private Connection connection;
	private LinkedHashMap<Long, Integer> artikelCountMap = new LinkedHashMap<>();

	//Get connection - TODO - moet nog aangepast worden feedback gerbrich meerdere connecties
	public ArtikelDAOMySQL() {
	}

	//Create

	@Override //voeg een artikel toe aan een bestelling zolang er nog geen drie artikelen zijn
	public void nieuwArtikelOpBestelling(long bestelling_id, Artikel aNieuw) throws RSVIERException {
		connection = MySQLConnectie.getConnection();
		boolean kanArtikelToevoegen = true;

		try {
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
	@Override //Vraag een artikel van een bestelling op
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
			System.out.println("SQLexception tijdens opvragen artikel" + artikelNummer);
			e.printStackTrace();
		}
		finally {
			MySQLHelper.close(connection, statement);
		}
		return artikel;
	}
	/*
	 * Methode leest alle artikelen uit een bestelling en geeft ze terug in een Iterator<Artikel>.
	 */
	@Override
	public Iterator<Artikel> getAlleArtikelenOpBestelling(long bestelling_id) throws RSVIERException {
		connection = MySQLConnectie.getConnection();
		//Sla alle artikelen van de bestelling op in een LinkedHashSet
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
	/*
	 * Deze methode stopt alle unieke artikelen in een LinkedHashMap, houd hun aantal bij en geeft deze terug
	 * in een Iterator<Entry<Artikel, Integer>>.
	 */
	@Override
	public Iterator<Entry<Artikel, Integer>> getAlleArtikelen() throws RSVIERException {
		connection = MySQLConnectie.getConnection();
		//Sla alle unieke artikelen binnen de tabel Bestelling op in een map
		LinkedHashMap<Artikel, Integer> map = new LinkedHashMap<>();
		Iterator<Entry<Artikel,Integer>> iterator = null;

		try {
			statement = connection.prepareStatement("SELECT artikel1_id, artikel1_naam, artikel1_prijs, "
					+ "artikel2_id, artikel2_naam, artikel2_prijs, "
					+ "artikel3_id, artikel3_naam, artikel3_prijs "
					+ "FROM BESTELLING ;");
			ResultSet rset = statement.executeQuery();
			
			while (rset.next()) { //Doorloop rset en voeg alle unieke artikelen toe en update hun aantal
				for(int x = 1; x <=7 ; x+=3) {
					if (rset.getString(x) != null) { //Bestelling bevat artikel
						artikel = new Artikel(Integer.parseInt(rset.getString(x)), rset.getString(x+1), rset.getDouble(x+2));
						voegArtikelToeAanMap(map, artikel);
					}
				}
			}
			return iterator = map.entrySet().iterator();
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


	//Update
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
	@Override //Methode om een artikel van een bestelling aan te passen.
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
			System.out.println("SQLexception update artikelen van bestelling met"
					+ bestelling_id + " ging verkeerd");
			e.printStackTrace();
		}
		finally {
			MySQLHelper.close(connection, statement);
		}
	}

	//Delete
	/*
	 * Deze methode zet de gegevens van het artikel met artikelnummer in bestelling met bestelling_id
	 * allemaal op -1, -1 geeft dan ook aan dat het artikel onrealistisch is, oftewel niet bestaat.
	 * Het is niet mogelijk om een artikel te verwijderen atm zonder de gehele bestelling te verwijderen
	 * maar op deze manier hebben we dit probleem omzeild.
	 */
	@Override
	public void verwijderArtikelVanBestelling(long bestelling_id, Artikel aOud) throws RSVIERException {
		//Maak een artikel dat alle waardes op -1 zet
		Artikel artikelWisser = new Artikel(0, "0", 0);

		//Onderstaande methode maakt een connectie, vangt exception en sluit de connectie + statement.
		updateArtikelOpBestelling(bestelling_id, aOud, artikelWisser);
	}

	// Deze methode zet de waardes van alle artikelen van bestelling met bestelling_id op -1.
	@Override
	public void verwijderAlleArtikelenVanBestelling(long bestelling_id) throws RSVIERException {
		//artikel met waardes -1
		Artikel artikelWisser = new Artikel(0, "0", 0) ;

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
