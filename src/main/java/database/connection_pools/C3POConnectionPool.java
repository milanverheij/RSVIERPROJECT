package database.connection_pools;

import com.mchange.v2.c3p0.*;
import exceptions.GeneriekeFoutmelding;
import logger.DeLogger;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by Milan_Verheij on 19-06-16.
 *
 * C3PO Connection Pool
 *
 * Configuratie Object en uiteindelijke leverancier van 'C3PO-Connecties'.
 * Levert enkel connecties op basis van het meegegeven DataBase Type.
 */

public class C3POConnectionPool {
    private static C3POConnectionPool C3POConnectionPool;
    private ComboPooledDataSource cpds;

    /**
     * Stelt de C3PO configuratie in op het gekozen DataBase type.
     *
     * @param configuratie Configuratiegegevens voor de verbinding.
     * @throws GeneriekeFoutmelding Foutmelding met gegevens.
     */
    private C3POConnectionPool(ConnectieConfiguratie configuratie) throws GeneriekeFoutmelding {
        try {
//            System.out.println("IN C3PO");
            cpds = new ComboPooledDataSource();

            Properties properties = new Properties();
            properties.setProperty("charSet", "utf-8");
            cpds.setProperties(properties);

            cpds.setDriverClass(configuratie.getCLASSDRIVER());
            cpds.setJdbcUrl(configuratie.getSERVER_URL() + configuratie.getSERVER_PORT() + configuratie.getDATABASE_NAAM());
            cpds.setUser(configuratie.getUSER());
            cpds.setPassword(configuratie.getPASSWORD());

            DeLogger.getLogger().info("C3PO Geconfigureerd");
        } catch (PropertyVetoException ex) {
            DeLogger.getLogger().error("PropertyVetoExcepton in C3PO(MYSQL) connection pool: " + ex.getMessage());
            throw new GeneriekeFoutmelding("PropertyVetoExcepton in C3PO connection pool");
        }
    }


    /**
     * Retourneert een instance van C3POConnectionPool als deze nog niet bestond.
     *
     * @param configuratie Configuratiegegevens voor de verbinding.
     * @return C3PO Connection pool.
     * @throws GeneriekeFoutmelding Foutmelding met gegevens.
     */
    public static C3POConnectionPool getInstance(ConnectieConfiguratie configuratie) throws GeneriekeFoutmelding {
        if (C3POConnectionPool == null) {
            C3POConnectionPool = new C3POConnectionPool(configuratie);
            return C3POConnectionPool;
        } else {
            return C3POConnectionPool;
        }
    }

    /**
     * Retourneert een connectie behorend bij deze Connection Pool.
     *
     * @return Connection-Object.
     * @throws SQLException Foutmelding met gegevens.
     */
    public Connection getConnection() throws SQLException {
        return this.cpds.getConnection();
    }
}
