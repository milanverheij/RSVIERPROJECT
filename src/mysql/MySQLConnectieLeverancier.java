package mysql;

import exceptions.GeneriekeFoutmelding;
import logger.DeLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Milan_Verheij on 06-06-16.
 *
 * Dit is de MySQLConnectie klasse in het RSVIERPROJECT.
 *
 * Het is een singleton klasse welke een mysql-connectie aanmaakt en geeft aan de gebruiker.
 * Het heeft een private methode om een connectie te maken en een publieke methode
 * om een instance van de connectie aan de caller te geven.
 *
 * Er wordt door de klasse zelf een private instance aangemaakt. De constructor is tevens ook private,
 * dit is dus de enige instance die er is en kan zijn.
 *
 */

@Deprecated
public class MySQLConnectieLeverancier {
    // Instant van deze klasse. De enige instance die er zal zijn.
    @SuppressWarnings("unused")
    private static MySQLConnectieLeverancier instance = new MySQLConnectieLeverancier();
    private static Connection connection;
    private static final String URL = "jdbc:mysql://milanverheij.nl/RSVIERPROJECTDEEL2";
    private static final String USER = "rsvierproject";
    private static final String PASSWORD = "slechtwachtwoord";
    private static final String DRIVER_CLASS = "com.mysql.jdbc.Driver";


    /** Private constructor zodat alleen de klasse zelf mag instantiaten (wat hierboven reeds is gebeurd).
     *  Probeert de bijbehorende driver te laden (mysql). Geeft een consolemelding van het resultaat.
     *
     *  Geen parameters
     */
    @Deprecated
    private MySQLConnectieLeverancier() {
        try {
            // Laden van de mysql Driver en log in console als dit gelukt is
            Class.forName(DRIVER_CLASS);
            DeLogger.getLogger().info("MysQL JDBC DRIVER SUCCESVOL GELADEN");

        } catch (ClassNotFoundException e) {
            DeLogger.getLogger().error("DRIVER LADEN MISLUKT: " + e.getMessage());
        }
    }


    /** Private method om een connectie aan te maken en
     * connectie proberen te maken en naar console loggen als dit gelukt is.
     *
     * @return De gemaakte connectie met de database.
     */
    @Deprecated
    private synchronized static Connection connectToDatabase() throws GeneriekeFoutmelding {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            DeLogger.getLogger().info("DATABASE SUCCESVOL VERBONDEN" );
            return connection;
        } catch (SQLException e) {
            DeLogger.getLogger().warn("MISLUKT MET DATABASE TE VERBINDEN: " + e.getMessage());
            throw new GeneriekeFoutmelding("MySQLConnectie: MISLUKT MET DATABASE TE VERBINDEN");
        }
    }

    /** Publieke methode om de connectie mee te verkrijgen
     *
     * @return de connectie gemaakt in de methode connecToDatabase
     */
    @Deprecated
    public static Connection getConnection() throws GeneriekeFoutmelding {
        return connectToDatabase();
    }
}
