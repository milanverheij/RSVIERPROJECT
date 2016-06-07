package mysql;

import com.mysql.jdbc.Statement;
import com.sun.rowset.internal.Row;
import interfaces.KlantDAO;
import model.Adres;
import model.Klant;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Created by Milan_Verheij on 06-06-16.
 *
 * KlantDAOMySQL is de DAO van de Klant POJO.
 * Doordat hij AbstractDAOMySQL extend shared hij standaard een connection en PreparedStatement
 *
 */
public class KlantDAOMySQL extends AbstractDAOMySQL implements KlantDAO {
    String query = "";
    ArrayList<Klant> klanten;

    /**
     * Public Constructor initialiseert de connectie met de database
     */
    public KlantDAOMySQL() {
        connection = MySQLConnectie.getConnection();
    }

    /** CREATE */
    @Override
    public void nieuweKlant(String voornaam,
                            String achternaam,
                            Adres adresgegevens) {
        ResultSet generatedKeys = null;
        try {
            query = "INSERT INTO KLANT " +
                    "(voornaam, achternaam, straatnaam, postcode, huisnummer, woonplaats) " +
                    "VALUES " +
                    "(?, ?, ?, ?, ?, ?);";
            statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, voornaam);
            statement.setString(2, achternaam);
            // TODO, vanaf hier AdresDAO gaan gebruiken?
            statement.setString(3, adresgegevens.getStraatnaam());
            statement.setString(4, adresgegevens.getPostcode());
            statement.setInt(5, adresgegevens.getHuisnummer());
            statement.setString(6, adresgegevens.getWoonplaats());

            System.out.println("\n\tKlantDAOMySQL: KLANT: " + voornaam + " SUCCESVOL GEMAAKT");
            statement.execute();

            generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                System.out.println(generatedKeys.getInt(1));
            }

        } catch (SQLException ex) {
            System.out.println("\n\tKlantDAOMySQL: SQL FOUT TIJDENS AANMAKEN KLANT");
            ex.printStackTrace();
        } finally {
            MySQLHelper.close(statement, generatedKeys);
        }

    }

    @Override
    public void nieuweKlant(String voornaam, String achternaam) {
        nieuweKlant(voornaam, achternaam, new Adres("onbekend", "onbekend", "onbekend", 0, "onbekend"));
    }

    /** READ */
    @Override
    public ListIterator<Klant> getAlleKlanten() {
        ResultSet rs = null;
        int klantenTeller = 0;

        try {
            query = "SELECT * FROM KLANT";
            statement = connection.prepareStatement(query);
            rs = statement.executeQuery();

            klanten = new ArrayList<>();

            while (rs.next()) {
                Klant tijdelijkeKlant = new Klant();
                tijdelijkeKlant.setKlant_id(rs.getLong(1));
                tijdelijkeKlant.setVoornaam(rs.getString(2));
                tijdelijkeKlant.setAchternaam(rs.getString(3));
                tijdelijkeKlant.setTussenvoegsel(rs.getString(4));
                tijdelijkeKlant.setEmail(rs.getString(5));
                tijdelijkeKlant.getAdresGegevens().setStraatnaam(rs.getString(6));
                tijdelijkeKlant.getAdresGegevens().setPostcode(rs.getString(7));
                tijdelijkeKlant.getAdresGegevens().setToevoeging(rs.getString(8));
                tijdelijkeKlant.getAdresGegevens().setHuisnummer(rs.getInt(9));
                tijdelijkeKlant.getAdresGegevens().setWoonplaats(rs.getString(10));
                klanten.add(klantenTeller, tijdelijkeKlant);
                klantenTeller++;
            }
        } catch (SQLException ex) {
            System.out.println("\n\tKlantDAOMySQL: SQL FOUT TIJDEN OPHALEN KLANTEN");
            ex.printStackTrace();
        } finally {
            MySQLHelper.close(rs);
            MySQLHelper.close(statement);
        }
        return klanten.listIterator();
    }

    // TODO: Tijdelijk om naar console te printen, aangezien later naar GUI gaat
    public void printKlantenInConsole() {
        ListIterator<Klant> klantenIterator = getAlleKlanten();

        while (klantenIterator.hasNext()) {
            Klant tijdelijkeKlant = klantenIterator.next();
            Adres tijdelijkAdres = tijdelijkeKlant.getAdresGegevens();
            System.out.println("\n\t----------------------------------------------------");
            System.out.print("\n\tKLANTID:           " + tijdelijkeKlant.getKlant_id());
            System.out.print("\n\tVoornaam:          " + tijdelijkeKlant.getVoornaam());
            System.out.print("\n\tAchternaam:        " + tijdelijkeKlant.getAchternaam());
            System.out.print("\n\tTussenvoegsel:     " + tijdelijkeKlant.getTussenvoegsel());
            System.out.print("\n\tE-Mail:            " + tijdelijkeKlant.getEmail());
            System.out.print("\n\tStraatnaam:        " + tijdelijkAdres.getStraatnaam());
            System.out.print("\n\tPostcode:          " + tijdelijkAdres.getPostcode());
            System.out.print("\n\tToevoeging:        " + tijdelijkAdres.getToevoeging());
            System.out.print("\n\tHuisnummer:        " + tijdelijkAdres.getHuisnummer());
            System.out.print("\n\tWoonplaats:        " + tijdelijkAdres.getWoonplaats());
        }
        System.out.println("\n\n");
    }

    @Override
    public void getKlantOpKlant(long klantId) {
        try {
            query = "SELECT * FROM " +
                    "KLANT WHERE " +
                    "klant_id = ?;";
            statement = connection.prepareStatement(query);
            statement.setLong(1, klantId);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                System.out.println(resultSet.getString(2));
            }

        } catch (SQLException ex) {
            System.out.println("\n\tKlantDAOMySQL: SQL FOUT TIJDENS OPZOEKEN KLANT OP KLANTID");
            ex.printStackTrace();
        } finally {
            MySQLHelper.close(statement, resultSet);
        }
    }

    @Override
    public void getKlantOpKlant(String voornaam) {
        try {
            query = "SELECT * FROM " +
                    "KLANT WHERE " +
                    "voornaam LIKE ?;";
            statement = connection.prepareStatement(query);
            statement.setString(1, voornaam);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                System.out.println(resultSet.getString(2));
            }

        }catch (SQLException ex) {
            System.out.println("\n\tKlantDAOMySQL: SQL FOUT TIJDENS OPZOEKEN KLANT OP VOORNAAM");
            ex.printStackTrace();
        } finally {
            MySQLHelper.close(statement, resultSet);
        }
    }

    @Override
    public void getKlantOpKlant(String voornaam, String achternaam) {

    }

    @Override
    public void getKlantOpAdres(Adres adresgegevens) {

    }

    @Override
    public void getKlantOpAdres(String straatnaam) {

    }

    @Override
    public void getKlantOpAdres(String postcode, int huisnummer) {

    }

    @Override
    public void getKlantOpBestelling(long bestellingId) {

    }

    /** UPDATE */
    @Override
    public void updateKlant(String voornaam, String achternaam) {

    }

    @Override
    public void updateKlant(String voornaam,
                            String achternaam,
                            Adres adresgegevens) {

    }

    /** REMOVE */
    @Override
    public void verwijderKlant(long klantId) {

    }

    @Override
    public void verwijderKlant(String voornaam, String achternaam, String tussenvoegsel) {

    }

    @Override
    public void verwijderKlantOpBestellingId(long bestellingId) {

    }
}

