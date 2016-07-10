package connection_pools;

/**
 * Created by Milan_Verheij on 10-07-16.
 *
 * Connectieconfiguratieklasse waarbij gebruik wordt gemaakt van een builder
 * patroon om de benodigde configuratie op te zetten en de klasse immutable te houden.
 *
 */
public class ConnectieConfiguratie {

    // Variabelen
    private final String SERVER_URL; // Verplicht
    private String SERVER_PORT; // Verplicht
    private final String DATABASE_NAAM; // Verplicht
    private final String USER; // Verplicht
    private final String PASSWORD; // Verplicht
    private final String CLASSDRIVER;

    private ConnectieConfiguratie (ConnectieConfiguratieBuilder builder) {
        this.SERVER_URL = builder.SERVER_URL;
        this.SERVER_PORT = builder.SERVER_PORT;
        this.DATABASE_NAAM = builder.DATABASE_NAAM;
        this.USER = builder.USER;
        this.PASSWORD = builder.PASSWORD;
        this.CLASSDRIVER = builder.CLASSDRIVER;
    }

    // Get methodes
    public String getSERVER_URL() {
        return SERVER_URL;
    }
    public String getSERVER_PORT() {
        return SERVER_PORT;
    }
    public String getDATABASE_NAAM() {
        return DATABASE_NAAM;
    }
    public String getUSER() {
        return USER;
    }
    public String getPASSWORD() {
        return PASSWORD;
    }
    public String getCLASSDRIVER() {
        return CLASSDRIVER;
    }

    public static class ConnectieConfiguratieBuilder {
        private final String SERVER_URL; // Verplicht
        private String SERVER_PORT; // Verplicht
        private final String DATABASE_NAAM; // Verplicht
        private final String USER; // Verplicht
        private final String PASSWORD; // Verplicht
        private final String CLASSDRIVER; // Verplicht

        public ConnectieConfiguratieBuilder(String SERVER_URL, String SERVER_PORT, String DATABASE_NAAM,
                                     String USER, String PASSWORD, String CLASSDRIVER) {
            this.SERVER_URL = SERVER_URL;
            this.SERVER_PORT = SERVER_PORT;
            this.DATABASE_NAAM = DATABASE_NAAM;
            this.USER = USER;
            this.PASSWORD = PASSWORD;
            this.CLASSDRIVER = CLASSDRIVER;
        }

        public ConnectieConfiguratie build() {
            return new ConnectieConfiguratie(this);
        }

    }
}
