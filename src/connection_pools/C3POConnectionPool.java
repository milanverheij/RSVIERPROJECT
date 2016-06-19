package connection_pools;

import com.mchange.v2.c3p0.*;
import exceptions.RSVIERException;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by Milan_Verheij on 19-06-16.
 *
 * C3PO Connection Pool
 *
 * Configuratie Object en uiteindelijke leverancier van 'C3PO-Connecties'.
 * Levert enkel connecties op basis van het meegegeven DataBase Type.
 */

public class C3POConnectionPool {
    private static C3POConnectionPool C3POConnectionPool;
    private ComboPooledDataSource cpds;


    // MySQL Settings
    private static final String MYSQL_URL = "jdbc:mysql://milanverheij.nl/RSVIERPROJECTDEEL2";
    private static final String MYSQL_USER = "rsvierproject";
    private static final String MYSQL_PASSWORD = "slechtwachtwoord";
    private static final String MYSQL_DRIVER_CLASS = "com.mysql.jdbc.Driver";

    // FireBird settings
    private static final String FIREBIRD_URL =
            "jdbc:firebirdsql://milanverheij.nl:3050//var//lib//firebird//2.5//data//RSVIERPROJECTDEEL2.fdb";
    private static final String FIREBIRD_USER = "rsvierproject";
    private static final String FIREBIRD_PASSWORD = "slechtwachtwoord";
    private static final String FIREBIRD_DRIVER_CLASS = "org.firebirdsql.jdbc.FBDriver";

    /**
     * Stelt de C3PO configuratie in op het gekozen DataBase type.
     *
     * @param DBKeuze Keuze voor het type database (1 = MySQL, 2 = FireBird);
     * @throws RSVIERException Foutmelding met gegevens.
     */
    private C3POConnectionPool(int DBKeuze) throws RSVIERException {
        if (DBKeuze == 1) {
            try {
                cpds = new ComboPooledDataSource();

                cpds.setDriverClass(MYSQL_DRIVER_CLASS);
                cpds.setJdbcUrl(MYSQL_URL);
                cpds.setUser(MYSQL_USER);
                cpds.setPassword(MYSQL_PASSWORD);

            } catch (PropertyVetoException ex) {
                throw new RSVIERException("PropertyVetoExcepton in C3PO connection pool");
            }
        }
        else if (DBKeuze == 2) {
            try {
                cpds = new ComboPooledDataSource();

                Properties properties = new Properties();
                properties.setProperty("charSet", "utf-8");
                cpds.setProperties(properties);
                cpds.setDriverClass(FIREBIRD_DRIVER_CLASS);
                cpds.setJdbcUrl(FIREBIRD_URL);
                cpds.setUser(FIREBIRD_USER);
                cpds.setPassword(FIREBIRD_PASSWORD);

            } catch (PropertyVetoException ex) {
                throw new RSVIERException("PropertyVetoExcepton in C3PO connection pool");
            }
        }
    }

    /**
     * Retourneert een instance van C3POConnectionPool als deze nog niet bestond.
     *
     * @param DBKeuze Keuze voor het type database (1 = MySQL, 2 = FireBird);
     * @return C3PO Connection pool.
     * @throws RSVIERException Foutmelding met gegevens.
     */
    public static C3POConnectionPool getInstance(int DBKeuze) throws RSVIERException {
        if (C3POConnectionPool == null) {
            C3POConnectionPool = new C3POConnectionPool(DBKeuze);
            return C3POConnectionPool;
        } else {
            return C3POConnectionPool;
        }
    }

    /**
     * Retourneert een connectie behorend bij deze Connection Pool.
     *
     * @return Connection-Object.
     * @throws SQLException Foutmelding met gegevens.
     */
    public Connection getConnection() throws SQLException {

        return this.cpds.getConnection();
    }
}
