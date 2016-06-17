package mysql;

import exceptions.RSVIERException;
import interfaces.AdresDAO;
import model.Adres;

import java.sql.*;

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

    /**
     * @param adresgegevens De opgegeven adresgegevens van de klant
     * @throws RSVIERException Foutmelding bij SQLException, info wordt meegegeven.
     */
    @Override
    public void updateAdres(long klant_id, Adres adresgegevens) throws RSVIERException {
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
                "woonplaats = ? " +
                "WHERE klant_id = ?;";

        try (
                Connection connection = MySQLConnectieLeverancier.getConnection();
                PreparedStatement statement= connection.prepareStatement(query)
        ) {
            statement.setString(1, adresgegevens.getStraatnaam());
            statement.setString(2, adresgegevens.getPostcode());
            statement.setString(3, adresgegevens.getToevoeging().equals("") ? null : adresgegevens.getToevoeging());
            statement.setInt(4, adresgegevens.getHuisnummer());
            statement.setString(5, adresgegevens.getWoonplaats());
            statement.setLong(6, klant_id);
            statement.execute();

        }  catch (SQLException ex) {
            throw new RSVIERException("AdresDAOMySQL: FOUT TIJDENS UPDATEN VAN EEN ADRES");
        }
    }

    @Override
    public long nieuwAdres(long klant_id, Adres adresgegevens) throws RSVIERException {
        ResultSet generatedKeys;
        String queryNieuwAdres = "INSERT INTO ADRES " +
                "(straatnaam, postcode, toevoeging, huisnummer, woonplaats) " +
                "VALUES " +
                "(?,        ?,          ?,          ?,          ?);";

        String queryAdresKlantKoppeling = "INSERT INTO PERSOON_HEEFT_ADRES " +
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
            throw new RSVIERException("AdresDAOMySQL: SQL FOUT TIJDENS AANMAKEN ADRES");
        }
    }

    // TODO n-to-n
    @Override
    public Adres getAdres(long klant_id) throws RSVIERException {
        String query = "SELECT ADRES.* " +
                "FROM ADRES, PERSOON_HEEFT_ADRES " +
                "WHERE " +
                "PERSOON_HEEFT_ADRES.adres_id_adres = ADRES.adres_id " +
                "AND " +
                "PERSOON_HEEFT_ADRES.klant_id_klant = ?;";

        try (
                Connection connection = MySQLConnectieLeverancier.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
                ) {
                statement.setLong(1, klant_id);
                ResultSet rs = statement.executeQuery();

            Adres tijdelijkAdres = new Adres();
            while (rs.next()) {
                // Adres aanmaken met lege waarden, default constructor.
                tijdelijkAdres = new Adres();

                tijdelijkAdres.setAdres_id(rs.getLong(1));
                tijdelijkAdres.setStraatnaam(rs.getString(2));
                tijdelijkAdres.setPostcode(rs.getString(3));
                tijdelijkAdres.setToevoeging(rs.getString(4));
                tijdelijkAdres.setHuisnummer(rs.getInt(5));
                tijdelijkAdres.setWoonplaats(rs.getString(6));
                tijdelijkAdres.setDatumAanmaak(rs.getString(7));
            }

            return tijdelijkAdres;

        } catch (SQLException ex) {
            throw new RSVIERException("FOUT TIJDENS GETADRES");
        }
    }
}

