package database.connection_pools;

import exceptions.GeneriekeFoutmelding;
import database.factories.interfaces.VerkrijgConnectie;
import logger.DeLogger;

import java.sql.Connection;

/**
 * Created by Milan_Verheij on 19-06-16.
 *
 * Adapter voor de C3PO Connection Pool.
 * Implementeert de VerkrijgConnectie interface welke gebruikt
 * waardoor deze een verkrijgConnectie methode diente te implementeren.
 *
 */
public class C3POAdapter implements VerkrijgConnectie {
    private ConnectieConfiguratie configuratie;

    /**
     * Standaard C3PO Adapter.
     *
     * @param configuratie Keuze van het type database welke door de connection pool
     *                gebruikt moet worden.
     * @throws GeneriekeFoutmelding Geeft een foutmelding met bijhorende melding.
     */
    public C3POAdapter(ConnectieConfiguratie configuratie) throws GeneriekeFoutmelding {
        this.configuratie = configuratie;
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
            return C3POConnectionPool.getInstance(configuratie).getConnection();
        } catch (Exception ex) {
            DeLogger.getLogger().error("Fout bij getInstance (DBKEUZE: " + configuratie + "): " + ex.getMessage());
            throw new GeneriekeFoutmelding("C3POAdapter: " + ex.getMessage());
        }
    }
}
