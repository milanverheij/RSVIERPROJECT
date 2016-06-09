package mysql;

import exceptions.RSVIERException;
import interfaces.AdresDAO;
import model.Adres;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Milan_Verheij on 08-06-16.
 *
 * Dit is de AdresDAO die de connectie regelt tussen de MySQL database
 * en de CRUD acties verzorgt.
 *
 */

public class AdresDAOMySQL extends AbstractDAOMySQL implements AdresDAO {
    String query = "";
    Connection connection;

    /**
     * @param adresgegevens De opgegeven adresgegevens van de klant
     * @throws RSVIERException Foutmelding bij SQLException, info wordt meegegeven.
     */
    @Override
    public void updateAdres(long klant_id, Adres adresgegevens) throws RSVIERException {
        connection = MySQLConnectie.getConnection();
        try {
            query = "UPDATE KLANT " +
                    "SET " +
                    "straatnaam = ?, " +
                    "postcode = ?, " +
                    "toevoeging = ?, " +
                    "huisnummer = ?, " +
                    "woonplaats = ? " +
                    "WHERE klant_id = ?;";

            statement = connection.prepareStatement(query);
            statement.setString(1, adresgegevens.getStraatnaam());
            statement.setString(2, adresgegevens.getPostcode());
            statement.setString(3, adresgegevens.getToevoeging().equals("") ? null : adresgegevens.getToevoeging());
            statement.setInt(4, adresgegevens.getHuisnummer());
            statement.setString(5, adresgegevens.getWoonplaats());
            statement.setLong(6, klant_id);
            statement.execute();

        } catch (SQLException ex) {
            throw new RSVIERException("AdresDAOMySQL: FOUT TIJDENS MAKEN NIEUW ADRES");
        } finally {
            MySQLHelper.close(connection, statement);
        }
    }
}

