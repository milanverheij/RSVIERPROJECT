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
    private static int logModus = 0; // 0 is standaard uit, 1 is aan

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
                if (logModus == 1)
                    System.out.println("\n\tMySQLHelper: CONNECTIE GESLOTEN" );
            } catch (SQLException e) {
                System.out.println("\n\tMySQLHelper: FOUT TIJDENS SLUITEN CONNECTIE");
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
                if (logModus == 1)
                    System.out.println("\n\tMySQLHelper: STATEMENT GESLOTEN" );
            } catch (SQLException e) {
                System.out.println("\n\tMySQLHelper: FOUT TIJDENS SLUITEN STATEMENT");
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
                if (logModus == 1)
                    System.out.println("\n\tMySQLHelper: RESULTSET GESLOTEN" );
            } catch (SQLException e) {
                System.out.println("\n\tMySQLHelper: FOUT TIJDENS SLUITEN RESULTSET");
            }
        }
    }

    /**
     * Als er een rowSet is wordt deze gesloten.
     * @param rowSet Meegegeven rowSet om te sluiten
     */
    @Deprecated
    public static void close(RowSet rowSet) {
        if (rowSet != null) {
            try {
                rowSet.close();
                if (logModus == 1)
                    System.out.println("\n\tMySQLHelper: ROWSET gesloten" );
            } catch (SQLException e) {
                System.out.println("\n\tMySQLHelper: FOUT TIJDENS SLUITEN ROWSET");
            }
        }
    }

    /** Methode om logmodus aan of uit te zetten
     *
     * @param logModus: Logmodus uit(0) of aan(1)
     */
    @Deprecated
    public static void setLogModus(int logModus) {
        MySQLHelper.logModus = logModus;
    }
}
