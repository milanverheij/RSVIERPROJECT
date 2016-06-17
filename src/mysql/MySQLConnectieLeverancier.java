package mysql;

import exceptions.RSVIERException;

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

public class MySQLConnectieLeverancier {
    // Instant van deze klasse. De enige instance die er zal zijn.
    @SuppressWarnings("unused")
    private static MySQLConnectieLeverancier instance = new MySQLConnectieLeverancier();
    private static Connection connection;
    private static final String URL = "jdbc:mysql://milanverheij.nl/RSVIERPROJECTDEEL2";
    private static final String USER = "rsvierproject";
    private static final String PASSWORD = "slechtwachtwoord";
    private static final String DRIVER_CLASS = "com.mysql.jdbc.Driver";
    private static int logModus = 0; // Standaard 0(uit), 1(aan)


    /** Private constructor zodat alleen de klasse zelf mag instantiaten (wat hierboven reeds is gebeurd).
     *  Probeert de bijbehorende driver te laden (mysql). Geeft een consolemelding van het resultaat.
     *
     *  Geen parameters
     */
    private MySQLConnectieLeverancier() {
        try {
            // Laden van de mysql Driver en log in console als dit gelukt is
            Class.forName(DRIVER_CLASS);
            System.out.println("\n\tMySQLConnectie: DRIVER SUCCESVOL GELADEN" );

        } catch (ClassNotFoundException e) {
            System.out.println("\n\tMySQLConnectie: DRIVER LADEN MISLUKT" );
            e.printStackTrace();
        }
    }


    /** Private method om een connectie aan te maken en
     * connectie proberen te maken en naar console loggen als dit gelukt is.
     *
     * @return De gemaakte connectie met de database.
     */
    private synchronized static Connection connectToDatabase() throws RSVIERException {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            if(logModus == 1)
                System.out.println("\n\tMySQLConnectie: DATABASE SUCCESVOL VERBONDEN" );
            return connection;
        } catch (SQLException e) {
            throw new RSVIERException("MySQLConnectie: MISLUKT MET DATABASE TE VERBINDEN");
        }
    }

    /** Publieke methode om de connectie mee te verkrijgen
     *
     * @return de connectie gemaakt in de methode connecToDatabase
     */
    public static Connection getConnection() throws RSVIERException {
        return connectToDatabase();
    }

    /**
     * Methode om de logModus van de connector aan en uit te zetten
     * @param logModus Logmodus uit (0) of logModus aan (1).
     */
    public static void setLogModus(int logModus) {
        MySQLConnectieLeverancier.logModus = logModus;
    }
}
