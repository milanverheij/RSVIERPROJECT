package database.factories;

import database.connection_pools.*;
import exceptions.GeneriekeFoutmelding;
import database.factories.interfaces.VerkrijgConnectie;

/**
 * Created by Milan_Verheij on 19-06-16.
 *
 * Dit is de factory voor de juiste connection Pool. Er wordt door middel
 * van een unieke waarde per type connectionpool de juiste factory gemaakt.
 *
 */

public class ConnectionPoolFactory {

    private static String SERVER_URL;
    private static String SERVER_PORT;
    private static String DATABASE_NAAM;
    private static String USER;
    private static String PASSWORD;
    private static String CLASSDRIVER;

    private static ConnectieConfiguratie connectieConfiguratie;

    /**
     * Geeft op basis van de gekozen Database Soort (1 = MySQL, 2 = FireBird) en
     * gekozen Connection Pool (1 = C3PO, 2 = HikariCP, 3 = MySQlConnectieLeverancier)
     * de juiste Connection Pool Adapter terug.
     *
     * @param connectionPoolKeuze Keuze voor de connection pool (zie keuzes hierboven).
     * @param DBKeuze Keuze voor type database (zie keuzes hierboven).
     * @return Adapter behorend bij bovenstaande keuzes.
     * @throws GeneriekeFoutmelding Foutmelding met omschrijving.
     */
    @SuppressWarnings("deprecation")
	public static VerkrijgConnectie getConnectionPool(String connectionPoolKeuze, String DBKeuze) throws GeneriekeFoutmelding {

        if (connectionPoolKeuze.equals("c3po")) {

            ConnectiePropertyLader properties = null;

            // Connectieconfiguratie maken op basis van connecties.properties en databasekeuze
            if (DBKeuze.equals("MySQL")) {
                properties = new ConnectiePropertyLader("mysql");
                CLASSDRIVER = "com.mysql.cj.jdbc.Driver";
            }
            else if (DBKeuze.equals("FireBird")) {
                properties = new ConnectiePropertyLader("firebird");
                CLASSDRIVER = "org.firebirdsql.jdbc.FBDriver";
            }

            maakConnectieConfiguratie(properties);
            return new C3POAdapter(connectieConfiguratie);
        }


        else if (connectionPoolKeuze.equals("HikariCP")) {
            ConnectiePropertyLader properties = null;

            // Connectieconfiguratie maken op basis van connecties.properties en databasekeuze
            if (DBKeuze.equals("MySQL")) {
                properties = new ConnectiePropertyLader("mysqlhik");
                CLASSDRIVER = "com.mysql.jdbc.jdbc2.optional.MysqlDataSource";
            }
            else if (DBKeuze.equals("FireBird")) {
                properties = new ConnectiePropertyLader("firebirdhik");
                CLASSDRIVER = "org.firebirdsql.pool.FBSimpleDataSource";
            }

            maakConnectieConfiguratie(properties);

            return new HikariCPAdapter(connectieConfiguratie);
        }

        else if (connectionPoolKeuze.equals("MySQlConnectieLeverancier"))
            return new OudeMySQLConnectorAdapter();
        else
            return new HikariCPAdapter(connectieConfiguratie); // Default
    }

    /**
     * Leest vanuit de ConnectiePropertyLader de properties uit en stopt deze in een immutable
     * configuratiebestand.
     *
     * @param properties Ingelezen properties
     * @throws GeneriekeFoutmelding Foutmelding met gegevens
     */
    private static void maakConnectieConfiguratie(ConnectiePropertyLader properties) throws GeneriekeFoutmelding {
        SERVER_URL = properties.getProperty("jdbc.url", true).toString();
        SERVER_PORT = properties.getProperty("jdbc.port", true).toString();
        DATABASE_NAAM = properties.getProperty("jdbc.databasenaam", true).toString();
        USER = properties.getProperty("jdbc.user", true).toString();
        PASSWORD = properties.getProperty("jdbc.password", true).toString();

        connectieConfiguratie = new ConnectieConfiguratie.ConnectieConfiguratieBuilder(
                SERVER_URL, SERVER_PORT, DATABASE_NAAM, USER, PASSWORD, CLASSDRIVER).build();
    }
}
