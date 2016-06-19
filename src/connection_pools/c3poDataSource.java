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
public class c3poDataSource {
    private static connection_pools.c3poDataSource c3poDataSource;
    private ComboPooledDataSource cpds;

    private static final String MYSQL_URL = "jdbc:mysql://milanverheij.nl/RSVIERPROJECTDEEL2";
    private static final String MYSQL_USER = "rsvierproject";
    private static final String MYSQL_PASSWORD = "slechtwachtwoord";
    private static final String MYSQL_DRIVER_CLASS = "com.mysql.jdbc.Driver";

    private c3poDataSource(int DBKeuze) throws RSVIERException {
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

    public static connection_pools.c3poDataSource getInstance(int DBKeuze) throws RSVIERException {
        if (c3poDataSource == null) {
            c3poDataSource = new c3poDataSource(DBKeuze);
            return c3poDataSource;
        } else {
            return c3poDataSource;
        }
    }

    public Connection getConnection() throws SQLException {
        return this.cpds.getConnection();
    }
}
