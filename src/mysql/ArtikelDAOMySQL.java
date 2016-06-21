package mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import exceptions.RSVIERException;
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
	private String query = "";
	private Artikel artikel = null;
	

	public ArtikelDAOMySQL() {
	}

	//Create

	@Override 
	public int nieuwArtikel(Artikel aNieuw) throws RSVIERException {
		int prijsId = 0;
		int artikelId = 0;
		String queryPrijs = "INSERT INTO PRIJS (prijs_id, prijs, artikel_id) VALUES (?, ?, ?);";
		String queryArtikel = "INSERT INTO ARTIKEL (omschrijving, prijsId, datumAanmaak, verwachteLevertijd, inAssortiment)" //TODO prijs_id naam in artikel tabel klopt niet!!!!!
				+ "VALUES (?, ?, ?, ?, ?);";
		String queryUpdate = "UPDATE ARTIKEL SET prijs_id = ? WHERE artikel_id = ?;";
		
		try (//Connection connection = connPool.verkrijgConnectie();
				Connection connection = MySQLConnectieLeverancier.getConnection();
				PreparedStatement statementPrijs = connection.prepareStatement(queryPrijs, Statement.RETURN_GENERATED_KEYS);
				PreparedStatement statementArtikel = connection.prepareStatement(queryArtikel, Statement.RETURN_GENERATED_KEYS);
				PreparedStatement statementUpdate = connection.prepareStatement(queryUpdate)){
			
			connection.setAutoCommit(false);
			
			//Zet de artikel gegevens in de artikel tabel
			statementArtikel.setString(1, aNieuw.getArtikelNaam());
			statementArtikel.setInt(2, prijsId);
			statementArtikel.setString(3, aNieuw.getDatumAanmaak());
			statementArtikel.setInt(4, aNieuw.getVerwachteLevertijd());
			statementArtikel.setBoolean(5, true);
			statementArtikel.executeUpdate();
			
			try (ResultSet generatedKeysArtikel = statementArtikel.getGeneratedKeys()) {
				if (generatedKeysArtikel.next()) {
					artikelId = generatedKeysArtikel.getInt(1);
					aNieuw.setArtikelId(artikelId);
				}
			}
			
			//Zet de prijs gegevens in de prijs tabel
			statementPrijs.setInt(1, prijsId);
			statementPrijs.setBigDecimal(2, aNieuw.getArtikelPrijs());
			statementPrijs.setInt(3, aNieuw.getArtikelId());
			statementPrijs.executeUpdate();
			
			try (ResultSet generatedKeysPrijs = statementPrijs.getGeneratedKeys()) {
				if (generatedKeysPrijs.next()) {
					prijsId = generatedKeysPrijs.getInt(1);
					aNieuw.setPrijsId(prijsId);
				}
			}
			//Update prijs_id in artikel tabel
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
			throw new RSVIERException("Niew artikel aanmaken kan niet");
		}
	}
	public int nieuwePrijs(Artikel a) throws RSVIERException { // Prijs_id mag van mij wel een long worden. Stel dat er heel veel prijswijzigingen plaatsvinden?
		int prijs_id = 0;
		query = "INSERT INTO PRIJS (prijs, artikel_id) VALUES (?, ?);";
		
		try (//Connection connection = connPool.verkrijgConnectie();
				Connection connection = MySQLConnectieLeverancier.getConnection();
				PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			connection.setAutoCommit(false);
			statement.setBigDecimal(1, a.getArtikelPrijs());
			statement.setInt(2, a.getArtikelId());
			statement.executeUpdate();
			
			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					prijs_id = generatedKeys.getInt(1);
					a.setArtikelPrijsId(prijs_id);
				}
			return prijs_id;
			}
			
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			throw new RSVIERException("SQL fout tijdens instellen nieuwe prijs");
		}
	}
	
	//Read
	@Override
	public Artikel getArtikel(int artikel_id) throws RSVIERException {
		// TODO Auto-generated method stub
		return null;
	}
	//Update
	@Override
	public void updateArtikel(int artikel_id, Artikel aNieuw) throws RSVIERException {
		// TODO Auto-generated method stub
		
	}
	//Delete
	@Override
	public void verwijderArtikel(int artikel_id) throws RSVIERException {
		// TODO Auto-generated method stub
		
	}
}

	

	