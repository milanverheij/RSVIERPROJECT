package connection_pools;

import exceptions.RSVIERException;
import interfaces.VerkrijgConnectie;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Milan_Verheij on 19-06-16.
 *
 * Adapter voor de C3PO Connection Pool voor diverse Databases.
 *
 */
public class C3POAdapter implements VerkrijgConnectie {
    c3poDataSource c3PODataSource;
    private int DBKeuze;

    public C3POAdapter(int DBKeuze) throws RSVIERException {
        this.DBKeuze = DBKeuze;
    }

    @Override
    public Connection verkrijgConnectie() throws RSVIERException {
        try {
        return c3PODataSource.getInstance(DBKeuze).getConnection();
        } catch (SQLException ex) {
            throw new RSVIERException("C3POAdapter SQL Exception" + ex.getMessage());
        }
    }
}
