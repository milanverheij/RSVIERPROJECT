package mysql;

import interfaces.AdresDAO;
import model.Adres;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Milan_Verheij on 08-06-16.
 *
 */

public class AdresDAOMySQL extends AbstractDAOMySQL implements AdresDAO {
    String query = "";
    Connection connection;
    /**
     * @param adresgegevens
     */
    @Override
    public void updateAdres(Long klant_id, Adres adresgegevens) {
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
            statement.setString(3, adresgegevens.getToevoeging());
            statement.setInt(4, adresgegevens.getHuisnummer());
            statement.setString(5, adresgegevens.getWoonplaats());
            statement.setLong(6, klant_id);
            statement.execute();

        } catch (SQLException ex) {
            System.out.println("\n\tAdresDAOMySQL: FOUT TIJDENS MAKEN NIEUW ADRES");
            ex.printStackTrace();
        } finally {
            MySQLHelper.close(connection, statement);
        }
    }
}

