package connection_pools;

import exceptions.RSVIERException;
import interfaces.VerkrijgConnectie;
import mysql.MySQLConnectieLeverancier;

import java.sql.Connection;

/**
 * Created by Milan_Verheij on 19-06-16.
 *
 * Adapter voor onze oude MYSQLConnectie
 *
 */
public class OudeMySQLConnectorAdapter implements VerkrijgConnectie {

    @Override
    public Connection verkrijgConnectie() throws RSVIERException {
        return MySQLConnectieLeverancier.getConnection();
    }
}
