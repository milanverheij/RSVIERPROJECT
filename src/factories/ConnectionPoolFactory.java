package factories;

import connection_pools.C3POAdapter;
import connection_pools.HikariCPAdapter;
import connection_pools.OudeMySQLConnectorAdapter;
import exceptions.GeneriekeFoutmelding;
import interfaces.VerkrijgConnectie;

/**
 * Created by Milan_Verheij on 19-06-16.
 *
 * Dit is de factory voor de juiste connection Pool. Er wordt door middel
 * van een unieke waarde per type connectionpool de juiste factory gemaakt.
 *
 */

public class ConnectionPoolFactory {

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
    public static VerkrijgConnectie getConnectionPool(String connectionPoolKeuze, String DBKeuze) throws GeneriekeFoutmelding {

        if (connectionPoolKeuze.equals("c3po"))
            return new C3POAdapter(DBKeuze);
        else if (connectionPoolKeuze.equals("HikariCP"))
            return new HikariCPAdapter(DBKeuze);
        else if (connectionPoolKeuze.equals("MySQlConnectieLeverancier"))
            return new OudeMySQLConnectorAdapter();
        else
            return new HikariCPAdapter(DBKeuze); // Default
    }
}
