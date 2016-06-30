package connection_pools;

import exceptions.GeneriekeFoutmelding;
import interfaces.VerkrijgConnectie;
import logger.DeLogger;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Milan_Verheij on 19-06-16.
 *
 * Adapter voor de C3PO Connection Pool.
 * Implementeert de VerkrijgConnectie interface welke gebruikt
 * waardoor deze een verkrijgConnectie methode diente te implementeren.
 *
 */
public class C3POAdapter implements VerkrijgConnectie {
    private String DBKeuze;

    /**
     * Standaard C3PO Adapter.
     *
     * @param DBKeuze Keuze van het type database welke door de connection pool
     *                gebruikt moet worden.
     * @throws GeneriekeFoutmelding Geeft een foutmelding met bijhorende melding.
     */
    public C3POAdapter(String DBKeuze) throws GeneriekeFoutmelding {
        this.DBKeuze = DBKeuze;
    }

    /**
     * Retourneert een Connection object aangeleverd door een van de
     * connection pools welke deze interface implementeren.
     *
     * @return Een connection object van een van de connection pools.
     * @throws GeneriekeFoutmelding Gooit een fout terug met de bijbehorende message.
     */
    @Override
    public Connection verkrijgConnectie() throws GeneriekeFoutmelding {
        try {
        return C3POConnectionPool.getInstance(DBKeuze).getConnection();
        } catch (SQLException ex) {
            DeLogger.getLogger().error("Fout bij getInstance (DBKEUZE: " + DBKeuze + "): " + ex.getMessage());
            throw new GeneriekeFoutmelding("C3POAdapter SQL Exception" + ex.getMessage());
        }
    }
}
