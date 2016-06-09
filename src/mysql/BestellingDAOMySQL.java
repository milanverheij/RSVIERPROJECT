package mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import interfaces.BestellingDAO;
import model.Artikel;
import model.Bestelling;
import exceptions.RSVIERException;

public class BestellingDAOMySQL extends AbstractDAOMySQL implements BestellingDAO{
	public BestellingDAOMySQL(){

	}

	//Create
	@Override
	public void nieuweBestelling(long klantId, Artikel a1, Artikel a2, Artikel a3) throws SQLException, RSVIERException {
		Connection connection = MySQLConnectie.getConnection();
		try {
			statement = connection.prepareStatement("INSERT INTO `BESTELLING` "
					+ "(artikel1_id, artikel1_naam, artikel1_prijs, "
					+ "artikel2_id, artikel2_naam, artikel2_prijs, "
					+ "artikel3_id, artikel3_naam, artikel3_prijs, "
					+ "klant_id)"
					+ "VALUES "
					+ "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");

			statement.setLong(10, klantId);

			//PreparedStatement uitbouwen
			buildNieuwBestellingStatement(a1, 1);
			buildNieuwBestellingStatement(a2, 2);
			buildNieuwBestellingStatement(a3, 3);
			statement.executeUpdate();
		}finally{
			MySQLHelper.close(connection, statement);	//Connection en statement niet meer nodig dus sluiten
		}
	}
	@Override
	public void nieuweBestelling(long klantId, Artikel a1, Artikel a2) throws SQLException, RSVIERException {
		Connection connection = MySQLConnectie.getConnection();
		try {
			statement = connection.prepareStatement("INSERT INTO `BESTELLING` "
					+ "(klant_id, "
					+ "artikel1_id, 	artikel2_id, "
					+ "artikel1_naam, 	artikel2_naam, "
					+ "artikel1_prijs, 	artikel2_prijs) "
					+ "VALUES "
					+ "(?, ?, ?, ?, ?, ?, ?);");

			statement.setLong(1, klantId);

			//PreparedStatement uitbouwen
			buildNieuwBestellingStatement(a1, 1);
			buildNieuwBestellingStatement(a2, 2);

			statement.executeUpdate();
		}finally{
			MySQLHelper.close(connection, statement);	//Connection en statement niet meer nodig dus sluiten
		}
	}
	@Override
	public void nieuweBestelling(long klantId, Artikel a1) throws SQLException, RSVIERException {
		Connection connection = MySQLConnectie.getConnection();
		try {
			statement = connection.prepareStatement("INSERT INTO `BESTELLING` "
					+ "(artikel1_id, artikel1_naam, artikel1_prijs, "
					+ "klant_id)"
					+ "VALUES "
					+ "(?, ?, ?, ?);");

			statement.setLong(4, klantId);

			//PreparedStatement uitbouwen
			buildNieuwBestellingStatement(a1, 1);

			statement.executeUpdate();
		}finally{
			MySQLHelper.close(connection, statement);	//Connection en statement niet meer nodig dus sluiten
		}
	}
	@Override
	public void nieuweBestelling(Bestelling bestelling) throws SQLException, RSVIERException{
		Connection connection = MySQLConnectie.getConnection();
		try {
			statement = connection.prepareStatement("INSERT INTO `BESTELLING` "
					+ "(artikel1_id, artikel1_naam, artikel1_prijs, "
					+ "artikel2_id, artikel2_naam, artikel2_prijs, "
					+ "artikel3_id, artikel3_naam, artikel3_prijs, "
					+ "klant_id)"
					+ "VALUES "
					+ "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");

			statement.setLong(10, bestelling.getKlant_id());

			//Bijhouden hoeveel artikelen er bijgevoegd zijn
			int count = 1;

			LinkedHashMap<Artikel, Integer> artikelen = bestelling.getArtikelLijst();

			//De losse artikelen in een iterable vorm krijgen
			Set<Artikel> artikelSet = artikelen.keySet();

			//Voor ieder artikel de PreparedStatement invullen
			for(Artikel artikel : artikelSet){

				//Als er een artikel 2x of vaker besteld is moet deze iedere keer toegevoegd worden
				//Dus een for-loop aan de hand van de Integer waarde
				for(int x = 0; x < artikelen.get(artikel); x++){
					buildNieuwBestellingStatement(artikel, count);
					count++;
				}
			}

			//Als er nog geen 3 artikelen toegevoegd zijn, de rest van de PreparedStatement
			//afvullen met null-waarden
			while(count < 4){
				buildNieuwBestellingStatement(null, count);
				count++;
			}
			statement.executeUpdate();
		}finally{
			MySQLHelper.close(connection, statement);	//Connection en statement niet meer nodig dus sluiten
		}
	}

	//Read
	@Override
	public Iterator<Bestelling> getBestellingOpKlantGegevens(long klantId) throws RSVIERException {
		Connection connection = MySQLConnectie.getConnection();
		//Alle bestellingen voor de klant
		LinkedHashSet<Bestelling> bestellijst = new LinkedHashSet<Bestelling>();

		//Individuele bestelling
		Bestelling bestelling;

		//De artikelen binnen een bestelling
		LinkedHashMap<Artikel, Integer> map;

		ResultSet rs = null;

		try {
			//Alle bestellingen van de klant in de ResultSet laden
			statement = connection.prepareStatement("SELECT * FROM `BESTELLING` WHERE klant_id =  ?;");
			statement.setLong(1, klantId);
			rs = statement.executeQuery();

			while(rs.next()){ 	//Alle 'rs' entries verwerken naar een Bestelling object
				map = new LinkedHashMap<Artikel, Integer>();

				//Nieuwe Bestelling aanmaken en invullen
				bestellijst.add(maakBestelling(rs, map));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}finally{ // Alles connections sluiten
			MySQLHelper.close(connection, statement, rs);
		}
		return bestellijst.iterator(); //Iterator van de bestellijst terug sturen
	}
	@Override
	public Iterator<Bestelling> getBestellingOpBestelling(long bestellingId) throws RSVIERException {
		Connection connection = MySQLConnectie.getConnection();
		ResultSet rs = null;
		try {
			statement = connection.prepareStatement("SELECT * FROM `BESTELLING` WHERE bestelling_id =  ? ;");
			statement.setLong(1, bestellingId);

			rs = statement.executeQuery();
			rs.next();	// Gelijk naar de eerste row gaan

			LinkedHashSet<Bestelling> bestellijst = new LinkedHashSet<Bestelling>();
			LinkedHashMap<Artikel, Integer> map = new LinkedHashMap<Artikel, Integer>();

			bestellijst.add(maakBestelling(rs, map));

			//Geeft een Iterator terug om op dezelfde manier als de andere getBetselling
			//doorlopen te kunnen worden, anders kon het net zo goed Bestelling object geven
			return bestellijst.iterator();

		} catch (SQLException e) {
			e.printStackTrace();
		}finally{	//Sluit alle conntections
			MySQLHelper.close(connection, statement, rs);
		}
		return null;
	}

	//Update
	@Override
	public void updateBestelling(long bestellingId, Artikel a1) throws SQLException, RSVIERException{
		Connection connection = MySQLConnectie.getConnection();
		try {
			statement = connection.prepareStatement("UPDATE `BESTELLING` "
					+ "SET artikel1_id = ?, artikel1_naam = ?, artikel1_prijs = ?"
					+ "WHERE bestelling_id = ?;");

			//PreparedStatement invullen
			buildUpdateStatement(a1, 1);
			buildUpdateStatement(null, 2);
			buildUpdateStatement(null, 3);

			statement.setLong(4, bestellingId);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			MySQLHelper.close(connection, statement);	//Connection en statement niet meer nodig dus sluiten
		}
	}
	@Override
	public void updateBestelling(long bestellingId, Artikel a1, Artikel a2) throws SQLException, RSVIERException{
		Connection connection = MySQLConnectie.getConnection();
		try {
			statement = connection.prepareStatement("UPDATE `BESTELLING` "
					+ "SET artikel1_id = ?, artikel1_naam = ?, artikel1_prijs = ?, "
					+ "artikel2_id = ?, artikel2_naam = ?, artikel2_prijs = ?"
					+ "WHERE bestelling_id = ?;");

			//PreparedStatement invullen
			buildUpdateStatement(a1, 1);
			buildUpdateStatement(a2, 2);
			buildUpdateStatement(null, 3);

			statement.setLong(7, bestellingId);

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			MySQLHelper.close(connection, statement);	//Connection en statement niet meer nodig dus sluiten
		}
	}
	@Override
	public void updateBestelling(long bestellingId, Artikel a1, Artikel a2, Artikel a3) throws SQLException, RSVIERException{
		Connection connection = MySQLConnectie.getConnection();
		try {
			statement = connection.prepareStatement("UPDATE `BESTELLING` "
					+ "SET artikel1_id = ?, artikel1_naam = ?, artikel1_prijs = ?, "
					+ "artikel2_id = ?, artikel2_naam = ?, artikel2_prijs = ?, "
					+ "artikel3_id = ?, artikel3_naam = ?, artikel3_prijs = ? "
					+ "WHERE bestelling_id = ?;");

			//PreparedStatement invullen
			buildUpdateStatement(a1, 1);
			buildUpdateStatement(a2, 2);
			buildUpdateStatement(a3, 3);

			statement.setLong(10, bestellingId);

			statement.executeUpdate();
		}finally{
			MySQLHelper.close(connection, statement);	//Connection en statement niet meer nodig dus sluiten
		}
	}
	@Override
	public void updateBestelling(Bestelling bestelling) throws SQLException, RSVIERException{
		Connection connection = MySQLConnectie.getConnection();
		try {
			statement = connection.prepareStatement("UPDATE `BESTELLING` "
					+ "SET artikel1_id = ?, artikel1_naam = ?, artikel1_prijs = ?, "
					+ "artikel2_id = ?, artikel2_naam = ?, artikel2_prijs = ?, "
					+ "artikel3_id = ?, artikel3_naam = ?, artikel3_prijs = ? "
					+ "WHERE bestelling_id = ?;");

			statement.setLong(10, bestelling.getBestelling_id());

			//Bijhouden hoeveel artikelen er bijgevoegd zijn
			int count = 1;

			LinkedHashMap<Artikel, Integer> artikelen = bestelling.getArtikelLijst();

			//De losse artikelen in een iterable vorm krijgen
			Set<Artikel> artikelSet = artikelen.keySet();

			//Voor ieder artikel de PreparedStatement invullen
			for(Artikel artikel : artikelSet){

				//Als er een artikel 2x of vaker besteld is moet deze iedere keer toegevoegd worden
				//Dus een for-loop aan de hand van de Integer waarde
				for(int x = 0; x < artikelen.get(artikel); x++){
					buildUpdateStatement(artikel, count);
					count++;
				}
			}

			//Als er nog geen 3 artikelen toegevoegd zijn, de rest van de PreparedStatement
			//afvullen met null-waarden
			while(count < 4){
				buildUpdateStatement(null, count);
				count++;
			}
			statement.executeUpdate();
		}finally{
			MySQLHelper.close(connection, statement);	//Connection en statement niet meer nodig dus sluiten
		}
	}

	//Delete

	//Delete
	@Override
	//Verwijder alle bestellingen van een klant
	public void verwijderAlleBestellingenKlant(long klantId) throws RSVIERException {
		Connection connection = MySQLConnectie.getConnection();
		try {
			statement = connection.prepareStatement("DELETE FROM `BESTELLING` WHERE klant_id = ?;");
			statement.setLong(1, klantId);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			MySQLHelper.close(connection, statement);	//Connection en statement niet meer nodig dus sluiten
		}
	}
	@Override
	//Verwijder 1 bestelling uit een tabel
	public void verwijderEnkeleBestelling(long bestellingId) throws RSVIERException {
		Connection connection = MySQLConnectie.getConnection();
		try {
			statement = connection.prepareStatement("DELETE FROM `BESTELLING` WHERE bestelling_id = ?;");
			statement.setLong(1, bestellingId);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			MySQLHelper.close(connection, statement);	//Connection en statement niet meer nodig dus sluiten
		}
	}

	//Utility
	//Voeg artikelen uit een ResultSet toe aan de LinkedHashMap
	private void voegArtikelToe(ResultSet rs, LinkedHashMap<Artikel, Integer> map){
		for(int x = 0; x < 3; x++){
			try {
				if(!(rs.getString(x + 3) == null)){ //Controleer iedere row of er een "not null" artikel_id is
					Artikel artikel = new Artikel();
					artikel.setArtikel_id(rs.getInt(x + 3));
					artikel.setArtikel_naam(rs.getString(x + 6));
					artikel.setArtikel_prijs(Double.parseDouble(rs.getString(x + 9)));

					if(map.containsKey(artikel)){
						map.put(artikel, map.get(artikel) + 1);
					}else{
						map.put(artikel, 1);
					}
				}
			} catch (NumberFormatException | SQLException e) {
				e.printStackTrace();
			}
		}
	}

	//Vult de PreparedStatement voor nieuweBestelling(...) in
	//Gooit een exception wanneer er 4 of meer artikelen worden geupdate.
	private void buildNieuwBestellingStatement(Artikel artikel, int nr) throws SQLException, RSVIERException{
		if(nr >= 4) throw new RSVIERException("Teveel artikelen");
		if(artikel != null){
			statement.setLong(  -2 + 3 * nr, artikel.getArtikel_id());
			statement.setString(-1 + 3 * nr, artikel.getArtikel_naam());
			statement.setDouble(     3 * nr, artikel.getArtikel_prijs());
		}else{
			statement.setString(-2 + 3 * nr, null);
			statement.setString(-1 + 3 * nr, null);
			statement.setString(     3 * nr, null);
		}
	}

	//Vult de PreparedStatement voor updateBestelling(...) in
	//Gooit een exception wanneer er 4 of meer artikelen worden geupdate.
	private void buildUpdateStatement(Artikel artikel, int nr) throws SQLException, RSVIERException{
		if(nr >= 4) throw new RSVIERException("Teveel artikelen");
		if(artikel != null){
			statement.setLong(-2 + 3 * nr, artikel.getArtikel_id());
			statement.setString(-1 + 3 * nr, artikel.getArtikel_naam());
			statement.setDouble(3 * nr, artikel.getArtikel_prijs());
		}else{
			statement.setString(-2 + 3 * nr, null);
			statement.setString(-1 + 3 * nr, null);
			statement.setString(3 * nr, null);
		}

	}

	private Bestelling maakBestelling(ResultSet rs, LinkedHashMap<Artikel, Integer> map){
		Bestelling bestelling = new Bestelling();
		try {
		bestelling.setBestelling_id(rs.getLong(1));
		bestelling.setKlant_id(rs.getLong(2));
		voegArtikelToe(rs, map);					//Artikelen uit rs toevoegen aan de LinkedHashMap
		bestelling.setArtikelLijst(map);
		}catch (SQLException e){
			e.printStackTrace();
		}
		return bestelling;
	}
}
