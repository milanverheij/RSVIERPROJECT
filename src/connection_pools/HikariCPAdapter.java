package connection_pools;

import exceptions.RSVIERException;
import interfaces.VerkrijgConnectie;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Milan_Verheij on 19-06-16.
 *
 */
public class HikariCPAdapter implements VerkrijgConnectie {
    private int DBKeuze;

    public HikariCPAdapter(int DBKeuze) {
        this.DBKeuze = DBKeuze;
    }

    @Override
    public Connection verkrijgConnectie() throws RSVIERException {
        try {
            return HikariCPConnectionPool.getInstance(DBKeuze).getConnection();
        } catch (SQLException ex) {
            throw new RSVIERException("C3POAdapter SQL Exception" + ex.getMessage());
        }
    }
}
