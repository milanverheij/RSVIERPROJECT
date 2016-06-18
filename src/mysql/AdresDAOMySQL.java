package mysql;

import exceptions.RSVIERException;
import interfaces.AdresDAO;
import model.Adres;

import java.sql.*;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Created by Milan_Verheij on 08-06-16.
 *
 * Dit is de AdresDAO die de connectie regelt tussen de MySQL database
 * en de CRUD acties verzorgt.
 *
 */

public class AdresDAOMySQL extends AbstractDAOMySQL implements AdresDAO {
    // public om test toegang te laten hebben, heeft verder geen impact op functionaliteit derhalve geen
    // veiligheidsrisico. Standaard wordt een sowieso fout adres meegegeven. Enkel als deze gewijzigd wordt
    // door middel van de klantWordtGetet 'schakelaar' kan deze het juiste adres of null aannemen.
    public static boolean klantWordtGetest = false;
    public static Adres aangeroepenAdresInTest = new Adres("XXXXXX", "XXXX", "XX", 0000, "XXXX"); // Geeft altijd FOUT
    private ArrayList<Adres> adresLijst;

    /**
     * @param adresgegevens De opgegeven adresgegevens van de klant
     * @throws RSVIERException Foutmelding bij SQLException, info wordt meegegeven.
     */
    @Override
    public void updateAdres(long adres_id, Adres adresgegevens) throws RSVIERException {
        // TODO: Een check op juiste invoer van gegevens.

        // Als er null wordt meegegeven als Adres wordt er een standaard leeg-adres geschrevne.
        if (adresgegevens == null) {
            adresgegevens = new Adres();
        }

        if (klantWordtGetest)
            aangeroepenAdresInTest = adresgegevens;

        String query = "UPDATE ADRES " +
                "SET " +
                "straatnaam = ?, " +
                "postcode = ?, " +
                "toevoeging = ?, " +
                "huisnummer = ?, " +
                "woonplaats = ?, " +
                "datumGewijzigd = CURRENT_TIMESTAMP " +
                "WHERE adres_id = ?;";

        try (
                Connection connection = MySQLConnectieLeverancier.getConnection();
                PreparedStatement statement= connection.prepareStatement(query)
        ) {
            statement.setString(1, adresgegevens.getStraatnaam());
            statement.setString(2, adresgegevens.getPostcode());
            statement.setString(3, adresgegevens.getToevoeging());
            statement.setInt(4, adresgegevens.getHuisnummer());
            statement.setString(5, adresgegevens.getWoonplaats());
            statement.setLong(6, adres_id);
            statement.execute();

        }  catch (SQLException ex) {
            throw new RSVIERException("AdresDAOMySQL: FOUT TIJDENS UPDATEN VAN EEN ADRES: " + ex.getMessage());
        }
    }

    @Override
    public void koppelAdresAanKlant(long klant_id, long adres_id) throws RSVIERException {
        String query = "INSERT INTO " +
                "KLANT_HEEFT_ADRES " +
                "(klant_id_klant, adres_id_adres) " +
                "VALUES " +
                "(?,              ?);";

        try (
                Connection connection = MySQLConnectieLeverancier.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
        )
        {
            statement.setLong(1, klant_id);
            statement.setLong(2, adres_id);
            statement.execute();

        } catch (SQLException ex) {
            if (ex.getMessage().contains("Duplicate entry"))
                throw new RSVIERException("AdresDAOMySQL: DIT ADRES IS REEDS GEKOPPELD AAN DEZE KLANT");
            else
                throw new RSVIERException("AdresDAOMySQL: SQL FOUT TIJDENS KOPPELEN PERSOON AAN BESTAAND ADRES: " +
                        ex.getMessage());
        }
    }

    @Override
    public long nieuwAdres(long klant_id, Adres adresgegevens) throws RSVIERException {
        ResultSet generatedKeys = null;
        String queryNieuwAdres = "INSERT INTO ADRES " +
                "(straatnaam, postcode, toevoeging, huisnummer, woonplaats) " +
                "VALUES " +
                "(?,        ?,          ?,          ?,          ?);";

        String queryAdresKlantKoppeling = "INSERT INTO KLANT_HEEFT_ADRES " +
                "(klant_id_klant, adres_id_adres) " +
                "VALUES " +
                "(?,        ?);";

        try (
                Connection connection = MySQLConnectieLeverancier.getConnection();
                PreparedStatement statementNieuwAdres = connection.prepareStatement(queryNieuwAdres, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement statementAdresKlantKoppeling = connection.prepareStatement(queryAdresKlantKoppeling);
        )
        {
            // Voer query uit en haal de gegenereerde sleutels op bij deze query
            statementNieuwAdres.setString(1, adresgegevens.getStraatnaam());
            statementNieuwAdres.setString(2, adresgegevens.getPostcode());
            statementNieuwAdres.setString(3, adresgegevens.getToevoeging());
            statementNieuwAdres.setInt(4, adresgegevens.getHuisnummer()); // TODO Naar string in model
            statementNieuwAdres.setString(5, adresgegevens.getWoonplaats());
            statementNieuwAdres.execute();

            // Ophalen van de laatste genegeneerde sleutel uit de generatedkeys (de nieuwe klant_id)
            long nieuw_adres_id = 0;
            generatedKeys = statementNieuwAdres.getGeneratedKeys();
            if (generatedKeys.next()) { nieuw_adres_id = generatedKeys.getInt(1); }

            // Koppelingstabel BESTELLING_HEEFT_ARTIKEL updaten met de juiste klant_id en adres_id
            statementAdresKlantKoppeling.setLong(1, klant_id);
            statementAdresKlantKoppeling.setLong(2, nieuw_adres_id);
            statementAdresKlantKoppeling.execute();

            return nieuw_adres_id;
        } catch (SQLException ex) {
            if (ex.getMessage().contains("Duplicate entry"))
                throw new RSVIERException("AdresDAOMySQL: DIT ADRES BESTAAT AL IN DE DATABASE MET ID: " +
                        getAdresID(adresgegevens.getPostcode(), adresgegevens.getHuisnummer(), adresgegevens.getToevoeging()));
            else
                throw new RSVIERException("AdresDAOMySQL: SQL FOUT TIJDENS AANMAKEN ADRES");
        } finally {
            MySQLHelper.close(generatedKeys);
        }
    }

    @Override
    public long getAdresID(String postcode, int huisnummer, String toevoeging) throws RSVIERException {
        ResultSet rs = null;
        String query = "SELECT adres_id " +
                "FROM ADRES " +
                "WHERE " +
                "postcode = ? AND " +
                "huisnummer = ? AND " +
                "toevoeging = ?;";

        try (
                Connection connection = MySQLConnectieLeverancier.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setString(1, postcode);
            statement.setInt(2, huisnummer);
            statement.setString(3, toevoeging);

            rs = statement.executeQuery();

            if (!rs.next())
                throw new RSVIERException("AdresDAOMySQL: ADRES NIET GEVONDEN");
            else {
                return rs.getLong(1); // Door if-statement is rs al bij next()
            }

        } catch (SQLException ex) {
            throw new RSVIERException("AdresDAOMySQL: SQL FOUT TIJDENS ZOEKEN ADRES: " + ex.getMessage());
        } finally {
            MySQLHelper.close(rs);
        }
    }

    @Override
    public ListIterator<Adres> getAdresOpKlantID(long klant_id) throws RSVIERException {
        ResultSet rs = null;
        String query = "SELECT ADRES.* " +
                "FROM ADRES, KLANT_HEEFT_ADRES " +
                "WHERE " +
                "KLANT_HEEFT_ADRES.adres_id_adres = ADRES.adres_id " +
                "AND " +
                "KLANT_HEEFT_ADRES.klant_id_klant = ?;";

        try (
                Connection connection = MySQLConnectieLeverancier.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setLong(1, klant_id);
            rs = statement.executeQuery();
            adresLijst = new ArrayList<>();

            // Ga door alle adressen heen die bij deze klant horen en stop het in een ArrayList<Adres>
            while (rs.next()) {
                Adres tijdelijkAdres = new Adres();
                // Adres aanmaken met lege waarden, default constructor.

                tijdelijkAdres.setAdres_id(rs.getLong(1));
                tijdelijkAdres.setStraatnaam(rs.getString(2));
                tijdelijkAdres.setPostcode(rs.getString(3));
                tijdelijkAdres.setToevoeging(rs.getString(4));
                tijdelijkAdres.setHuisnummer(rs.getInt(5));
                tijdelijkAdres.setWoonplaats(rs.getString(6));
                tijdelijkAdres.setDatumAanmaak(rs.getString(7)); // TODO: Checken of aparte methode nodig is voor RS ->it
                tijdelijkAdres.setDatumGewijzigd(rs.getString(8));
                tijdelijkAdres.setAdresActief(rs.getString(9));

                adresLijst.add(tijdelijkAdres);
            }

            // Return een list-iterator
            return adresLijst.listIterator();

        } catch (SQLException ex) {
            throw new RSVIERException("FOUT TIJDENS GETADRES: " + ex.getMessage());
        } finally {
            MySQLHelper.close(rs);
        }
    }

    @Override
    public void schakelStatusAdres(long adres_id, int status) throws RSVIERException {
        String query =
                        "UPDATE ADRES " +
                        "SET " +
                        "adresActief = ? " +
                        "WHERE " +
                        "adres_id = ?";

        try (
                Connection connection = MySQLConnectieLeverancier.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);

        ) {
            statement.setInt(1, status);
            statement.setLong(2, adres_id);
            statement.execute();

        } catch (SQLException ex) {
            throw new RSVIERException("AdresDAOMySQL: SQL FOUT TIJDENS ADRES OP ID INACTIEF ZETTEN" + ex.getMessage());
        }
    }
}

