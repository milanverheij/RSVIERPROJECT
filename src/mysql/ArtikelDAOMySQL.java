package mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
    Connection connection;
	private Artikel artikel;
	private LinkedHashMap<Long, Integer> ArtikelCountMap;

	//Get connection - TODO - moet nog aangepast worden feedback gerbrich meerdere connecties
    // TODO, MV: TIJDELIJK BESTAND TOEGEVOEGD IN KLANTDAO BRANCH OM GEEN FOUTMELDING IN FACTORY TE KRIJGEN
	public ArtikelDAOMySQL() {
//		connection = MySQLConnectie.getConnection();
	}

	//Create
	@Override //voeg een artikel toe aan een bestelling zolang er nog geen drie artikelen zijn
	public void nieuwArtikelOpBestelling(long bestelling_id, Artikel a) {
		boolean artikelKanWordenToegevoegd = true;
		try {
			if (ArtikelCountMap.get(bestelling_id) >= 3) {
				System.out.println("Kan geen artikel toevoegen, maximaal 3 artikelen!");
				artikelKanWordenToegevoegd = false;
			}
			else if (ArtikelCountMap.containsKey(bestelling_id)) {
				ArtikelCountMap.put(bestelling_id, ArtikelCountMap.get(bestelling_id) + 1);
			}
			else if (!ArtikelCountMap.containsKey(bestelling_id)) {
				ArtikelCountMap.put(bestelling_id, 1);
			}
			if (artikelKanWordenToegevoegd) {
				statement = connection.prepareStatement("INSERT INTO 'BESTELLING' ("
						+ "artikel" + ArtikelCountMap.get(bestelling_id) + "_id, "
						+ "artikel" + ArtikelCountMap.get(bestelling_id)  + "_naam, "
						+ "artikel" + ArtikelCountMap.get(bestelling_id)  + "_prijs) "
						+ "VALUES (?, ?, ?) "
						+ "WHERE bestelling_id = " + bestelling_id + ";");

				statement.setString(1, "" + a.getArtikel_id());
				statement.setString(2, a.getArtikel_naam());
				statement.setDouble(3, a.getArtikel_prijs());
			}
		}
		catch (SQLException e) {
			System.out.println("SQL exception tijdens aanmaken artikel" + ArtikelCountMap.get(bestelling_id)
					+ " voor bestelling " + bestelling_id);
			e.printStackTrace();
		}
		finally {
			MySQLHelper.close(statement);
		}

	}

	//Read
	@Override //Vraag een artikel van een bestelling op
	public Artikel getArtikelOpBestelling(long bestelling_id, int artikelNummer) {

		try {
			if (artikelNummer <= 0 & artikelNummer > 3) {
				System.out.println("artikelNummer is incorrect, kies 1, 2 of 3.");
			}
			statement = connection.prepareStatement("SELECT 'artikel" + artikelNummer + "_id' "
					+ "'artikel" + artikelNummer + "_naam' 'artikel" + artikelNummer + "_prijs' "
					+ "FROM 'BESTELLING' "
					+ "WHERE bestelling_id = " + bestelling_id + ";");
			ResultSet rset = statement.executeQuery();

			artikel = new Artikel(Integer.parseInt(rset.getString(1)), rset.getString(2), rset.getDouble(3));
		}
		catch (SQLException e) {
			System.out.println("SQLexception tijdens opvragen artikel" + artikelNummer);
			e.printStackTrace();
		}
		finally {
			MySQLHelper.close(statement);
		}
		return artikel;
	}

	@Override
	public Iterator<Artikel> getAlleArtikelenOpBestelling(long bestelling_id) {
		//Sla alle artikelen van de bestelling op in een LinkedHashSet
		LinkedHashSet<Artikel> artikelLijst = new LinkedHashSet<>();

		try {
			statement = connection.prepareStatement("SELECT 'artikel1_id' 'artikel1_naam' 'artikel1_prijs' "
					+ "'artikel2_id' 'artikel2_naam' 'artikel2_prijs' "
					+ "'artikel3_id' 'artikel3_naam' 'artikel3_prijs' "
					+ "FROM 'BESTELLING' "
					+ "WHERE bestelling_id = " + bestelling_id + ";");
			ResultSet rset = statement.executeQuery();

			if (!rset.getString(1).isEmpty()) { //Bestelling bevat artikel1
				artikel = new Artikel(Integer.parseInt(rset.getString(1)), rset.getString(2), rset.getDouble(3));
				artikelLijst.add(artikel);
			}
			if (!rset.getString(4).isEmpty()) { //Bestelling bevat artikel2
				artikel = new Artikel(Integer.parseInt(rset.getString(4)), rset.getString(5), rset.getDouble(6));
				artikelLijst.add(artikel);
			}
			if (!rset.getString(7).isEmpty()) { //Bestelling bevat artikel3
				artikel = new Artikel(Integer.parseInt(rset.getString(7)), rset.getString(8), rset.getDouble(9));
				artikelLijst.add(artikel);
			}
		}
		catch (SQLException e) {
			System.out.println("SQLexception tijdens getAlleArtikelen()");
			e.printStackTrace();
		}
		finally {
			MySQLHelper.close(statement);
		}
		return artikelLijst.iterator();
	}

	@Override
	public LinkedHashMap<Artikel, Integer> getAlleArtikelen() {
		//Sla alle unieke artikelen binnen de tabel Bestelling op in een map
		LinkedHashMap<Artikel, Integer> map = new LinkedHashMap<>();

		try {
			statement = connection.prepareStatement("SELECT 'artikel1_id' 'artikel1_naam' 'artikel1_prijs' "
					+ "'artikel2_id' 'artikel2_naam' 'artikel2_prijs' "
					+ "'artikel3_id' 'artikel3_naam' 'artikel3_prijs' "
					+ "FROM 'BESTELLING' ;");
			ResultSet rset = statement.executeQuery();

			while (rset.next()) { //Doorloop rset en voeg alle unieke artikelen toe en update hun aantal
				if (!rset.getString(1).isEmpty()) { //Bestelling bevat artikel1
					artikel/*1*/ = new Artikel(Integer.parseInt(rset.getString(1)), rset.getString(2), rset.getDouble(3));
					voegArtikelToeAanMap(map, artikel);
				}
				if (!rset.getString(4).isEmpty()) { //Bestelling bevat artikel2
					artikel/*2*/ = new Artikel(Integer.parseInt(rset.getString(4)), rset.getString(5), rset.getDouble(6));
					voegArtikelToeAanMap(map, artikel);
				}
				if (!rset.getString(7).isEmpty()) { //Bestelling bevat artikel3
					artikel/*3*/ = new Artikel(Integer.parseInt(rset.getString(7)), rset.getString(8), rset.getDouble(9));
					voegArtikelToeAanMap(map, artikel);
				}

				rset.next();
			}
		}
		catch (SQLException e) {
			System.out.println("SQLexception tijdens getAlleArtikelen()");
			e.printStackTrace();
		}
		finally {
			MySQLHelper.close(statement);
		}
		return map;
	}


	//Update
	@Override //pas een artikel van een bestelling aan
	public void updateArtikelOpBestelling(long bestelling_id, int artikelNummer, Artikel a1) {
		try {
			statement = connection.prepareStatement("UPDATE 'BESTELLING' SET "
					+ "artikel" + artikelNummer + "_id = ?,"
					+ "artikel" + artikelNummer + "_naam = ?,"
					+ "artikel" + artikelNummer + "_prijs = ? "
					+ "WHERE bestelling_id = " + bestelling_id + ";");

			statement.setString(1, "" + a1.getArtikel_id());
			statement.setString(2, a1.getArtikel_naam());
			statement.setDouble(3, a1.getArtikel_prijs());

		}
		catch (SQLException e) {
			System.out.println("SQLexception update artikel " + artikelNummer + " ging verkeerd");
			e.printStackTrace();
		}
		finally {
			MySQLHelper.close(statement);
		}
	}

	//Delete
	@Override
	public void verwijderArtikelVanBestelling(long bestelling_id, int artikelNummer, Artikel a1) {
		try {
			statement = connection.prepareStatement("DELETE FROM 'artikel" + artikelNummer + "_id' "
					+ "'artikel" + artikelNummer + "_naam' 'artikel" + artikelNummer + "_prijs' "
					+ "WHERE bestelling_id = " + bestelling_id + " "
					+ "AND artikel" + artikelNummer + "_id = ? "
					+ "AND artikel" + artikelNummer + "_naam = ? "
					+ "AND artikel" + artikelNummer + "_prijs = ?;");

			statement.setInt(1, a1.getArtikel_id());
			statement.setString(2, a1.getArtikel_naam());
			statement.setDouble(3, a1.getArtikel_prijs());

		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			MySQLHelper.close(statement);
		}
	}

	@Override
	public void verwijderAlleArtikelenVanBestelling(long bestelling_id) {
		try {
			statement = connection.prepareStatement("DELETE FROM 'artikel1_id' 'artikel1_naam' 'artikel1_prijs' "
					+ "'artikel2_id' 'artikel2_naam' 'artikel2_prijs' 'artikel3_id' 'artikel3_naam' 'artikel3_prijs'"
					+ "WHERE bestelling_id = " + bestelling_id + "");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			MySQLHelper.close(statement);
		}
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
