package database.connection_pools;

import exceptions.GeneriekeFoutmelding;
import database.factories.interfaces.VerkrijgConnectie;

import java.sql.Connection;

import database.daos.mysql.MySQLConnectieLeverancier;

/**
 * Created by Milan_Verheij on 19-06-16.
 *
 * Adapter voor de 'oude' MySQLConnectieLeverancier
 * Implementeert de VerkrijgConnectie interface welke gebruikt
 * waardoor deze een verkrijgConnectie methode diente te implementeren.
 *
 */
@Deprecated
public class OudeMySQLConnectorAdapter implements VerkrijgConnectie {

    /**
     * Retourneert een Connection object aangeleverd door een van de
     * connection pools welke deze interface implementeren.
     *
     * @return Een connection object van een van de connection pools.
     * @throws GeneriekeFoutmelding Gooit een fout terug met de bijbehorende message.
     */
    @Override
    public Connection verkrijgConnectie() throws GeneriekeFoutmelding {
        return MySQLConnectieLeverancier.getConnection();
    }
}
