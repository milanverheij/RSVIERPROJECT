package connection_pools;

import exceptions.RSVIERException;
import interfaces.VerkrijgConnectie;
import mysql.MySQLConnectieLeverancier;

import java.sql.Connection;

/**
 * Created by Milan_Verheij on 19-06-16.
 *
 * Adapter voor de 'oude' MySQLConnectieLeverancier
 * Implementeert de VerkrijgConnectie interface welke gebruikt
 * waardoor deze een verkrijgConnectie methode diente te implementeren.
 *
 */
public class OudeMySQLConnectorAdapter implements VerkrijgConnectie {

    /**
     * Retourneert een Connection object aangeleverd door een van de
     * connection pools welke deze interface implementeren.
     *
     * @return Een connection object van een van de connection pools.
     * @throws RSVIERException Gooit een fout terug met de bijbehorende message.
     */
    @Override
    public Connection verkrijgConnectie() throws RSVIERException {
        return MySQLConnectieLeverancier.getConnection();
    }
}
