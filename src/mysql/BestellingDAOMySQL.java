package mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
	public BestellingDAOMySQL(){}
	public static boolean bestellingWordGetest = false; //Kijken of een JUnit test loopt
	public static Bestelling aangeroepenBestellingInTest =	//Standaardwaarden voor de JUnit test instellen 
			new Bestelling(1, new Artikel(666, "Necronomicon", 6.66), new Artikel(123, "Voynich Manuscript", 1.23), new Artikel(999, "Munich Manual of Demonic Magic", 9.99));

	//Create
	@Override
	public long nieuweBestelling(long klantId, Artikel a1, Artikel a2, Artikel a3) throws SQLException, RSVIERException {
		Bestelling bestelling = new Bestelling();
		bestelling.setKlant_id(klantId);
		bestelling.voegArtikelToe(a1);
		bestelling.voegArtikelToe(a2);
		bestelling.voegArtikelToe(a3);
		return nieuweBestelling(bestelling);
	}
	@Override
	public long nieuweBestelling(long klantId, Artikel a1, Artikel a2) throws SQLException, RSVIERException {
		return nieuweBestelling(klantId, a1, a2, null);
	}
	@Override
	public long nieuweBestelling(long klantId, Artikel a1) throws SQLException, RSVIERException {
		return nieuweBestelling(klantId, a1, null, null);
	}
	@Override
	public long nieuweBestelling(Bestelling bestelling) throws SQLException, RSVIERException{
		if(bestellingWordGetest)
			aangeroepenBestellingInTest = bestelling;

		Connection connection = MySQLConnectie.getConnection();
		ResultSet rs = null;

		try{
			statement = connection.prepareStatement("INSERT INTO `BESTELLING` "
					+ "(artikel1_id, artikel1_naam, artikel1_prijs, "
					+ "artikel2_id, artikel2_naam, artikel2_prijs, "
					+ "artikel3_id, artikel3_naam, artikel3_prijs, "
					+ "klant_id)"
					+ "VALUES "
					+ "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);", PreparedStatement.RETURN_GENERATED_KEYS);

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
			rs = statement.getGeneratedKeys();
			rs.next();
			return rs.getLong(1);
		}finally{
			MySQLHelper.close(connection, statement, rs);	//Connection en statement niet meer nodig dus sluiten
		}

	}

	//Read
	@Override
	public Iterator<Bestelling> getBestellingOpKlantGegevens(long klantId) throws RSVIERException {
		Connection connection = MySQLConnectie.getConnection();
		//Alle bestellingen voor de klant
		LinkedHashSet<Bestelling> bestellijst = new LinkedHashSet<Bestelling>();
		//De artikelen binnen een bestelling
		LinkedHashMap<Artikel, Integer> map;
		ResultSet rs = null;

		try {
			//Alle bestellingen van de klant in de ResultSet laden
			statement = connection.prepareStatement("SELECT * FROM `BESTELLING` WHERE klant_id =  ?;");
			statement.setLong(1, klantId);
			rs = statement.executeQuery();

			if(rs.next()){
				do{ 	//Alle 'rs' entries verwerken naar een Bestelling object
					map = new LinkedHashMap<Artikel, Integer>();

					//Nieuwe Bestelling aanmaken en invullen
					bestellijst.add(maakBestelling(rs, map));
				}while(rs.next());
				return bestellijst.iterator(); //Iterator van de bestellijst terug sturen
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{ // Alles connections sluiten
			MySQLHelper.close(connection, statement, rs);
		}
		return null;
	}
	@Override
	public Iterator<Bestelling> getBestellingOpBestelling(long bestellingId) throws RSVIERException {
		Connection connection = MySQLConnectie.getConnection();
		ResultSet rs = null;
		try {
			statement = connection.prepareStatement("SELECT * FROM `BESTELLING` WHERE bestelling_id =  ? ;");
			statement.setLong(1, bestellingId);

			rs = statement.executeQuery();
			if(rs.next()){
				LinkedHashSet<Bestelling> bestellijst = new LinkedHashSet<Bestelling>();
				LinkedHashMap<Artikel, Integer> map = new LinkedHashMap<Artikel, Integer>();

				bestellijst.add(maakBestelling(rs, map));

				//Geeft een Iterator terug om op dezelfde manier als de andere getBetselling
				//doorlopen te kunnen worden, anders kon het net zo goed Bestelling object geven

				return bestellijst.iterator();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{	//Sluit alle conntections
			MySQLHelper.close(connection, statement, rs);
		}
		return null;
	}

	//Update
	@Override
	public void updateBestelling(long bestellingId, Artikel a1) throws SQLException, RSVIERException {
		updateBestelling(bestellingId, a1, null, null);
	}
	@Override
	public void updateBestelling(long bestellingId, Artikel a1, Artikel a2) throws SQLException, RSVIERException {
		updateBestelling(bestellingId, a1, a2, null);
	}
	@Override
	public void updateBestelling(long bestellingId, Artikel a1, Artikel a2, Artikel a3) throws SQLException, RSVIERException {
		updateBestelling(new Bestelling(bestellingId, a1, a2, a3));
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
	@Override
	//Verwijder alle bestellingen van een klant
	public long verwijderAlleBestellingenKlant(long klantId) throws RSVIERException {
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
		return klantId;

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
