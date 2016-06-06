package mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Milan_Verheij on 06-06-16.
 *
 * Dit is de MySQLConnectie klasse in het RSVIERPROJECT.
 *
 * Het is een singleton klasse welke een mysql-connectie aanmaakt als deze er nog niet is.
 * Het heeft een private methode om een connectie te maken en een publieke methode
 * om een instance van de connectie aan de caller te geven.
 */

public class MySQLConnectie {
    // Instant van deze klasse. De enige instance die er zal zijn.
    private static MySQLConnectie instance = new MySQLConnectie();
    private static Connection connection;
    private static final String URL = "jdbc:mysql://milanverheij.nl/RSVIERPROJECT";
    private static final String USER = "rsvierproject";
    private static final String PASSWORD = "slechtwachtwoord";
    private static final String DRIVER_CLASS = "com.mysql.jdbc.Driver";


    /** Private constructor zodat alleen de klasse zelf mag instantiaten (wat hierboven reeds is gebeurd).
     *  Probeert de bijbehorende driver te laden (mysql). Geeft een consolemelding van het resultaat.
     *
     *  Geen parameters
     */
    private MySQLConnectie() {
        try {
            // Laden van de mysql Driver en log in console als dit gelukt is
            Class.forName(DRIVER_CLASS);
            System.out.println("\n\tDRIVER SUCCESVOL GELADEN");
        } catch (ClassNotFoundException e) {
            System.out.println("\n\tDRIVER LADEN MISLUKT");
            e.printStackTrace();
        }
    }


    /** Private method om een connectie aan te maken als deze er niet is (null) en
     * connectie proberen te maken en naar console loggen als dit gelukt is.
     *
     * Zal altijd een connectie teruggeven.
     *
     * @return De gemaakte connectie met de database.
     */

    private static Connection connectToDatabase() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("\n\tDATABASE SUCCESVOL VERBONDEN");
            } catch (SQLException e) {
                System.out.println("\n\tMISLUKT MET DATABASE TE VERBINDEN");
                e.printStackTrace();
            }
        }
        return connection;
    }

    /** Publieke methode om de connectie mee te verkrijgen
     *
     * @return de connectie gemaakt in de methode connecToDatabase
     */
    public static Connection getConnection() {
        return connectToDatabase();
    }

}
