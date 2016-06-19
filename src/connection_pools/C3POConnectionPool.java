package connection_pools;

import com.mchange.v2.c3p0.*;
import exceptions.RSVIERException;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Milan_Verheij on 19-06-16.
 *
 * AANPASSEN
 *
 */
public class C3POConnectionPool {
    private static C3POConnectionPool C3POConnectionPool;
    private ComboPooledDataSource cpds;

    private static final String MYSQL_URL = "jdbc:mysql://milanverheij.nl/RSVIERPROJECTDEEL2";
    private static final String MYSQL_USER = "rsvierproject";
    private static final String MYSQL_PASSWORD = "slechtwachtwoord";
    private static final String MYSQL_DRIVER_CLASS = "com.mysql.jdbc.Driver";

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
    }

    public static C3POConnectionPool getInstance(int DBKeuze) throws RSVIERException {
        if (C3POConnectionPool == null) {
            C3POConnectionPool = new C3POConnectionPool(DBKeuze);
            return C3POConnectionPool;
        } else {
            return C3POConnectionPool;
        }
    }

    public Connection getConnection() throws SQLException {
        return this.cpds.getConnection();
    }
}
