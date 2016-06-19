package connection_pools;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import exceptions.RSVIERException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Milan_Verheij on 19-06-16.
 *
 * AANPASSEN
 *
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
    }

    public static HikariCPConnectionPool getInstance(int DBKeuze) throws RSVIERException {
        if (hikariCPConnectionPool == null) {
            hikariCPConnectionPool = new HikariCPConnectionPool(DBKeuze);
            return hikariCPConnectionPool;
        } else {
            return hikariCPConnectionPool;
        }
    }

    public Connection getConnection() throws SQLException, RSVIERException {
        return this.hikariDataSource.getConnection();
    }
}
