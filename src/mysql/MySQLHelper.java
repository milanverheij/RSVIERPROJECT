package mysql;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Milan_Verheij on 06-06-16.
 *
 * Dit is de MySQLHelper class in het RSVIERPROJECT
 *
 * Deze klasse dient ertoe om te helpen met het afsluiten van de connectiestromen. Dit
 * ter voorkoming van dubbele code in het kader try / catch blocken e.d.
 *
 */
public class MySQLHelper {

    /**
     * Overloaded method van de onderstaande methodes. Om gemakkelijk zowel de connectie,
     * als PreparedStatement als ResultSet te sluiten.
     * Sluit eerst resultset, dan preparedstatement en dan connection.
     *
     * @param connection Meegegeven connectie om te sluiten
     */
    public static void close(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) {
        close(resultSet);
        close(preparedStatement);
        close(connection);
    }

    /**
     * Als er een connectie is wordt deze gesloten.
     * @param connection Meegegeven connectie om te sluiten
     */
    public static void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("\n\tCONNECTIE GESLOTEN");
            } catch (SQLException e) {
                System.out.println("\n\tFOUT TIJDENS SLUITEN CONNECTIE");
                e.printStackTrace();
            }
        }
    }

    /**
     * Als er een PreparedStatement is wordt deze gesloten
     * @param statement Meegegeven statement om te sluiten
     */
    public static void close(PreparedStatement statement) {
        if (statement != null) {
            try {
                statement.close();
                System.out.println("\n\tSTATEMENT GESLOTEN");
            } catch (SQLException e) {
                System.out.println("\n\tFOUT TIJDENS SLUITEN STATEMENT");
            }
        }
    }

    /**
     * Als er een resultSet is wordt deze gesloten
     * @param resultSet Meegegeven resultset om te sluiten
     */
    public static void close(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
                System.out.println("\n\tRESULTSET GESLOTEN");
            } catch (SQLException e) {
                System.out.println("\n\tFOUT TIJDENS SLUITEN RESULTSET");
            }
        }
    }

    /**
     * Als er een rowSet is wordt deze gesloten.
     * @param rowSet Meegegeven rowSet om te sluiten
     */
    public static void close(RowSet rowSet) {
        if (rowSet != null) {
            try {
                rowSet.close();
                System.out.println("\n\tROWSET gesloten");
            } catch (SQLException e) {
                System.out.println("\n\tFOUT TIJDENS SLUITEN ROWSET");
            }
        }
    }
}
