package database.connection_pools;

import exceptions.GeneriekeFoutmelding;
import logger.DeLogger;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
/**
 * Created by Milan_Verheij on 10-07-16.
 *
 * Deze klasse laad het connecties properties bestand 'connecties.properties' in het geheugen en verzorgt
 * een constructor welke een specifieke keuze accepteert welke wordt gebruikt als property keyprefix voor het
 * properties bestand. Er is een property getter welke enkel de properties terug geeft met die prefix
 * 'dbKeuze.' en geeft aan of die property verplicht is of niet.
 */
public class ConnectiePropertyLader {

    private static final String PROPERTIES_LOCATIE = "connecties.properties";
    private static final Properties PROPERTIES = new Properties();

    private String dbKeuze;

    /**n
     * Construct a DAOProperties instance for the given specific key which is to be used as property
     * key prefix of the DAO properties file.
     * @param dbKeuze The specific key which is to be used as property key prefix.
     * @throws GeneriekeFoutmelding During class initialization if the DAO properties file is
     * missing in the classpath or cannot be loaded.
     */
    public ConnectiePropertyLader(String dbKeuze) throws GeneriekeFoutmelding {
        this.dbKeuze = dbKeuze;

        // Probeer het properties-bestand in te lezen als stream en in te laden als properties
        try (
                InputStream propertiesBestand = new FileInputStream(PROPERTIES_LOCATIE);
        ) {
            PROPERTIES.load(propertiesBestand);

        } catch (Exception e) {
            DeLogger.getLogger().error("Fout bij inladen properties bestand: " + e.getMessage());
            throw new GeneriekeFoutmelding("ConnectiePropertyLader: Fout bij inladen properties bestand: " + e.getMessage());
        }
    }

    // Actions ------------------------------------------------------------------------------------

    /**
     * Returns the DAOProperties instance specific property value associated with the given key with
     * the option to indicate whether the property is mandatory or not.
     * @param key The key to be associated with a DAOProperties instance specific value.
     * @param mandatory Sets whether the returned property value should not be null nor empty.
     * @return The DAOProperties instance specific property value associated with the given key.
     * @throws GeneriekeFoutmelding If the returned property value is null or empty while
     * it is mandatory.
     */
    public String getProperty(String key, boolean mandatory) throws GeneriekeFoutmelding {
        String fullKey = dbKeuze + "." + key;
        String property = PROPERTIES.getProperty(fullKey);

        if (property == null || property.trim().length() == 0) {
            if (mandatory) {
                throw new GeneriekeFoutmelding("Required property '" + fullKey + "'"
                        + " is missing in properties file '" + PROPERTIES_LOCATIE + "'.");
            } else {
                // Make empty value null. Empty Strings are evil.
                property = null;
            }
        }
        return property;
    }

}
