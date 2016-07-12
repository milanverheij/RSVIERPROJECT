package interfaces;

import exceptions.GeneriekeFoutmelding;

import java.sql.Connection;

/**
 * Created by Milan_Verheij on 19-06-16.
 *
 * Interface voor generieke verkrijging van een connectie afhankelijk van
 * connectie pool. Wordt geimplementeerd door de diverse ConnectionPool
 * Adapters. Zie verder deze Adapters in de connection_pools package.
 *
 */
public interface VerkrijgConnectie {

    /**
     * Retourneert een Connection object aangeleverd door een van de
     * connection pools welke deze interface implementeren.
     *
     * @return Een connection object van een van de connection pools.
     * @throws GeneriekeFoutmelding Gooit een fout terug met de bijbehorende message.
     */
   Connection verkrijgConnectie() throws GeneriekeFoutmelding;
}
