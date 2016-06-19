package connection_pools;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import exceptions.RSVIERException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Milan_Verheij on 19-06-16.
 *
 * HikariCP Connection Pool
 *
 * Configuratie Object en uiteindelijke leverancier van 'HikariCP-Connecties'.
 * Levert enkel connecties op basis van het meegegeven DataBase Type.
 */

public class HikariCPConnectionPool {
    @SuppressWarnings("unused")
    private static HikariCPConnectionPool hikariCPConnectionPool;
    private HikariConfig hikariConfig;
    private HikariDataSource hikariDataSource;

    private static final String MYSQL_SERVERURL = "milanverheij.nl";
    private static final String MYSQL_SERVERPORT = "3306";
    private static final String MYSQL_DATABASE = "RSVIERPROJECTDEEL2";
    private static final String MYSQL_USER = "rsvierproject";
    private static final String MYSQL_PASSWORD = "slechtwachtwoord";
    private static final String MYSQL_DRIVER_CLASS = "com.mysql.jdbc.jdbc2.optional.MysqlDataSource";

    private static final String FIREBIRD_DATABASE =
            "//milanverheij.nl:3050//var//lib//firebird//2.5//data//RSVIERPROJECTDEEL2.fdb";
    private static final String FIREBIRD_USER = "rsvierproject";
    private static final String FIREBIRD_PASSWORD = "slechtwachtwoord";
    private static final String FIREBIRD_DRIVER_CLASS = "org.firebirdsql.pool.FBSimpleDataSource";

    /**
     * Stelt de HikariCP configuratie in op het gekozen DataBase type.
     *
     * @param DBKeuze Keuze voor het type database (1 = MySQL, 2 = FireBird);
     * @throws RSVIERException Foutmelding met gegevens.
     */
    private HikariCPConnectionPool(int DBKeuze) throws RSVIERException {
        if (DBKeuze == 1) {
            hikariConfig = new HikariConfig();
            hikariConfig.setMinimumIdle(1);
            hikariConfig.setMaximumPoolSize(2);
            hikariConfig.setInitializationFailFast(true);

            hikariConfig.setDataSourceClassName(MYSQL_DRIVER_CLASS);
            hikariConfig.addDataSourceProperty("serverName", MYSQL_SERVERURL);
            hikariConfig.addDataSourceProperty("port", MYSQL_SERVERPORT);
            hikariConfig.addDataSourceProperty("databaseName", MYSQL_DATABASE);
            hikariConfig.addDataSourceProperty("user", MYSQL_USER);
            hikariConfig.addDataSourceProperty("password", MYSQL_PASSWORD);

            hikariDataSource = new HikariDataSource(hikariConfig);
        }

        if (DBKeuze == 2) {
            hikariConfig = new HikariConfig();
            hikariConfig.setMinimumIdle(1);
            hikariConfig.setMaximumPoolSize(2);
            hikariConfig.setInitializationFailFast(true);

            hikariConfig.setDataSourceClassName(FIREBIRD_DRIVER_CLASS);
            hikariConfig.addDataSourceProperty("database", FIREBIRD_DATABASE);
            hikariConfig.addDataSourceProperty("userName", FIREBIRD_USER);
            hikariConfig.addDataSourceProperty("password", FIREBIRD_PASSWORD);

            hikariDataSource = new HikariDataSource(hikariConfig);
        }
    }

    /**
     * Retourneert een instance van HikariCPConnectionPool als deze nog niet bestond.
     *
     * @param DBKeuze Keuze voor het type database (1 = MySQL, 2 = FireBird);
     * @return HikariCP Connection pool.
     * @throws RSVIERException Foutmelding met gegevens.
     */
    public static HikariCPConnectionPool getInstance(int DBKeuze) throws RSVIERException {
        if (hikariCPConnectionPool == null) {
            hikariCPConnectionPool = new HikariCPConnectionPool(DBKeuze);
            return hikariCPConnectionPool;
        } else {
            return hikariCPConnectionPool;
        }
    }

    /**
     * Retourneert een connectie behorend bij deze Connection Pool.
     *
     * @return Connection-Object.
     * @throws SQLException Foutmelding met gegevens.
     */
    public Connection getConnection() throws SQLException, RSVIERException {
        return this.hikariDataSource.getConnection();
    }
}
