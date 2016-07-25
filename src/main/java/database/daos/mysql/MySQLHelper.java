package database.daos.mysql;

import logger.DeLogger;

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
@Deprecated
public class MySQLHelper {

    /**
     * Overloaded method van de onderstaande methodes. Om gemakkelijk zowel de connectie,
     * als PreparedStatement als ResultSet te sluiten.
     * Sluit eerst resultset, dan preparedstatement en dan connection.
     *
     * @param connection Meegegeven connectie om te sluiten
     */
    @Deprecated
    public static void close(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) {
        close(resultSet);
        close(preparedStatement);
        close(connection);
    }

    /**
     * Overloaded method van de onderstaande methodes. Om gemakkelijk zowel de connectie,
     * als PreparedStatement te sluiten.
     * Sluit eerst preparedstatement en dan connection.
     *
     * @param preparedStatement Meegegeven statement om te sluiten
     * @param connection Meegegeven connectie om te sluiten
     */
    @Deprecated
    public static void close(Connection connection, PreparedStatement preparedStatement) {
        close(preparedStatement);
        close(connection);
    }

    /**
     * Overloaded method van de onderstaande methodes. Om gemakkelijk zowel de resultset,
     * als PreparedStatement te sluiten.
     * Sluit eerst resultset en dan preparedstatement.
     *
     * @param preparedStatement Meegegeven statement om te sluiten
     * @param resultSet Meegegeven resultset om te sluiten
     */
    @Deprecated
    public static void close(PreparedStatement preparedStatement, ResultSet resultSet) {
        close(resultSet);
        close(preparedStatement);
    }

    /**
     * Als er een connectie is wordt deze gesloten.
     * @param connection Meegegeven connectie om te sluiten
     */
    @Deprecated
    public static void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                DeLogger.getLogger().error("FOUT TIJDENS SLUITEN CONNECTIE: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Als er een PreparedStatement is wordt deze gesloten
     * @param statement Meegegeven statement om te sluiten
     */
    @Deprecated
    public static void close(PreparedStatement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                DeLogger.getLogger().error("FOUT TIJDENS SLUITEN STATEMENT");
            }
        }
    }

    /**
     * Als er een resultSet is wordt deze gesloten
     * @param resultSet Meegegeven resultset om te sluiten
     */
    @Deprecated
    public static void close(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                DeLogger.getLogger().error("FOUT TIJDENS SLUITEN RESULTSET");
            }
        }
    }
}
