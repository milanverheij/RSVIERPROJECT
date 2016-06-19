package factories;

import com.mysql.jdbc.StringUtils;
import connection_pools.C3POAdapter;
import connection_pools.OudeMySQLConnectorAdapter;
import exceptions.RSVIERException;
import interfaces.VerkrijgConnectie;

/**
 * Created by Milan_Verheij on 19-06-16.
 *
 * Dit is de factory voor de juiste connection Pool. Er wordt door middel
 * van een unieke waarde per type connectionpool de juiste factory gemaakt.
 *
 */

public class ConnectionPoolFactory {
    public static VerkrijgConnectie getConnectionPool(String connectionPoolKeuze, int DBKeuze) throws RSVIERException {
        if (connectionPoolKeuze.length() > 1 || connectionPoolKeuze.length() == 0) {
            throw new RSVIERException("ConnectionPoolFactory: U moet 1 karakter invullen als keuze");
        } else
        if (!StringUtils.isStrictlyNumeric(connectionPoolKeuze)) {
            throw new RSVIERException("ConnectionPoolFactory: U moet een getal als keuze in vullen");
        }

        switch (connectionPoolKeuze.charAt(0)) {
            case '1':
                    return new C3POAdapter(DBKeuze);
            case '2':
                    throw new RSVIERException("ConnectionPoolFactory: JIKARICP NOG NIET GEIMPLEMENTEERD");
            case '3':
                    return new OudeMySQLConnectorAdapter();
            default:
                    throw new RSVIERException("ConnectionPoolFactory: VERKEERDE KEUZE VOOR CONNECTION POOL: " + connectionPoolKeuze);
        }
    }
}
