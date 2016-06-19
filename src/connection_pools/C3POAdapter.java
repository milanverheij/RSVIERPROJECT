package connection_pools;

import exceptions.RSVIERException;
import interfaces.VerkrijgConnectie;

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
    private int DBKeuze;

    /**
     * Standaard C3PO Adapter.
     *
     * @param DBKeuze Keuze van het type database welke door de connection pool
     *                gebruikt moet worden.
     * @throws RSVIERException Geeft een foutmelding met bijhorende melding.
     */
    public C3POAdapter(int DBKeuze) throws RSVIERException {
        this.DBKeuze = DBKeuze;
    }

    /**
     * Retourneert een Connection object aangeleverd door een van de
     * connection pools welke deze interface implementeren.
     *
     * @return Een connection object van een van de connection pools.
     * @throws RSVIERException Gooit een fout terug met de bijbehorende message.
     */
    @Override
    public Connection verkrijgConnectie() throws RSVIERException {
        try {
        return C3POConnectionPool.getInstance(DBKeuze).getConnection();
        } catch (SQLException ex) {
            throw new RSVIERException("C3POAdapter SQL Exception" + ex.getMessage());
        }
    }
}
