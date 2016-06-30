package mysql;

import com.mysql.jdbc.Statement;
import exceptions.GeneriekeFoutmelding;
import interfaces.KlantDAO;
import logger.DeLogger;
import model.Adres;
import model.Bestelling;
import model.Klant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Created by Milan_Verheij on 06-06-16.
 *
 * KlantDAOMySQL is de DAO van de Klant POJO. <p>
 * Het verzorgt de database-operaties tussen MySQL en de objecten. <p>
 *
 * De DAO is opgezet in CRUD volgorde (Create, Read, Update, Delete)<p>
 *
 * Zie de afzonderlijke methods en constructor voor commentaar.
 */

public class KlantDAOMySQL extends AbstractDAOMySQL implements KlantDAO {

    // GLOBALE VARIABELEN
    String query = "";
    ArrayList<Klant> klantenLijst;
    BestellingDAOMySQL bestellingDAO;
    AdresDAOMySQL adresDAO;


    /** CREATE METHODS */

    /**
     * [HOOFD NIEUWEKLANTMETHODE]
     * Maakt een nieuwe klant aan in de database met alle naamgegevens.
     * Als er adres en/of bestelgegevens aanwezig zijn worden deze tevens ook toegevoegd.
     * Er wordt in de database automatisch een uniek ID gegenereerd welke automatisch verhoogd wordt.
     * Het is mogelijk door middel van een adres_id mee te geven geen nieuw adres aan te maken maar
     * deze te koppelen aan de klant.
     *
     * @param voornaam De voornaam van de klant (max 50 karakters).
     * @param achternaam De achternaam van de klant (max 51 karakters).
     * @param tussenvoegsel Tussenvoegsel van de klant (max 10 karakters).
     * @param email Emailadres van de klant (max 80 karakters).
     * @param adresgegevens Adresgegevens van de klant in een Klant object (zie Klant).
     * @param bestelGegevens Bestelgegevens van de klant in een Bestel object (zie Bestelling).
     * @throws GeneriekeFoutmelding Foutmelding bij SQLException, info wordt meegegeven.
     */

    @Override
    public long nieuweKlant(String voornaam,
                            String achternaam,
                            String tussenvoegsel,
                            String email,
                            long adres_id,
                            Adres adresgegevens,
                            Bestelling bestelGegevens) throws GeneriekeFoutmelding {

        query = "INSERT INTO KLANT " +
                "(voornaam, achternaam, tussenvoegsel, email) " +
                "VALUES " +
                "(?,        ?,          ?,              ?);";
        try (
                Connection connection = connPool.verkrijgConnectie();
                PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        ) {
            // Voer query uit en haal de gegenereerde sleutels op bij deze query
            statement.setString(1, voornaam);
            statement.setString(2, achternaam);
            statement.setString(3, tussenvoegsel);
            statement.setString(4, email);
            statement.execute();

            // Ophalen van de laatste genegeneerde sleutel uit de generatedkeys (de nieuwe klant_id)
            long nieuwId = 0;
            try (
                    ResultSet generatedKeys = statement.getGeneratedKeys();
            ) {
                if (generatedKeys.next()) {
                    nieuwId = generatedKeys.getInt(1);
                }

                // Als er een adres_id wordt meegegeven betekent dit dat er een bestaand adres gekoppeled wordt
                // aan een nieuwe klant
                if (adres_id > 0 && adresgegevens == null) {
                    adresDAO = new AdresDAOMySQL();
                    adresDAO.koppelAdresAanKlant(nieuwId, adres_id);
                }

                // Als er adresgegeven worden meegegeven wordt er een adres aangemaakt op basis van het nieuwe klantId
                else if (adresgegevens != null && adres_id == 0) {
                    adresDAO = new AdresDAOMySQL();
                    adresDAO.nieuwAdres(nieuwId, adresgegevens);
                }

                // Als er adresgegeven worden meegegeven en een adres_id wordt er zowel een nieuw adres aangemaakt
                // en tevens het bestaande adres gekoppeld.
                else if (adresgegevens != null && adres_id > 0) {
                    adresDAO = new AdresDAOMySQL();
                    adresDAO.nieuwAdres(nieuwId, adresgegevens);
                    adresDAO.koppelAdresAanKlant(nieuwId, adres_id);
                }

                // Als er bestegegevens zijn meegegeven worden deze bijgevoegd
                if (bestelGegevens != null) {
                    bestellingDAO = new BestellingDAOMySQL();
                    bestelGegevens.setKlant_id(nieuwId);
                    bestellingDAO.nieuweBestelling(bestelGegevens);
                }

            }
            return nieuwId;

        } catch (SQLException ex) {
            if (ex.getMessage().contains("Duplicate entry")) {
                DeLogger.getLogger().warn("KlantDAOMySQL: DEZE KLANT BESTAAT AL IN DE DATABASE MET ID: " +
                        getKlantID(voornaam, achternaam, email));
                throw new GeneriekeFoutmelding("KlantDAOMySQL: DEZE KLANT BESTAAT AL IN DE DATABASE MET ID: " +
                        getKlantID(voornaam, achternaam, email));
            }
            else {
                DeLogger.getLogger().error("SQL FOUT TIJDENS AANMAKEN KLANT: " + ex.getMessage());
                throw new GeneriekeFoutmelding("KlantDAOMySQL: SQL FOUT TIJDENS AANMAKEN KLANT: " + ex.getMessage());
            }
        }
    }

    /**
     * Deze methode kan een Klant-object ontvangen en maakt op basis daarvan een nieuwe
     * klant aan in de database. Adres-object en bestelling-object mogen null zijn.
     * Zie verder de overloaded nieuweKlant methods.
     *
     * Als een bestaand adres gekoppeld dient te worden kan er een adres_id worden meegegeven.
     * Er wordt dan geen nieuw adres meer aangemaakt.
     *
     * @param nieuweKlant Klantobject van de klant die gemaakt dient te worden.
     * @param adres_id Er kan een adres_id worden meegegeven om een bestaand adres te koppelen.
     * @return klant_id van de nieuwe klant.
     * @throws GeneriekeFoutmelding
     */
    @Override
    public long nieuweKlant(Klant nieuweKlant, long adres_id) throws GeneriekeFoutmelding {

        // Als er geen klant wordt meegegeven wordt een fout gegooid.
        if (nieuweKlant != null) {
            long nieuwId =  nieuweKlant(nieuweKlant.getVoornaam(), nieuweKlant.getAchternaam(),
                    nieuweKlant.getTussenvoegsel(), nieuweKlant.getEmail(), adres_id,
                    nieuweKlant.getAdresGegevens(), nieuweKlant.getBestellingGegevens());
            return nieuwId;
        }
        else {
            DeLogger.getLogger().warn("KAN GEEN KLANT AANMAKEN MET NULL OBJECT");
            throw new GeneriekeFoutmelding("KlantDAOMySQL: KAN GEEN KLANT AANMAKEN MET NULL OBJECT");
        }
    }

    /**
     * Maakt een nieuwe klant aan in de database met voornaam, achternaam en adresgegevens.
     * Er wordt in de database automatisch een uniek ID gegenereerd welke automatisch verhoogd wordt.
     *
     * @param voornaam De voornaam van de klant (max 50 karakters).
     * @param achternaam De achternaam van de klant (max 51 karakters).
     * @param adresgegevens De adresgegevens van de klant in een Adres object (Adres).
     * @throws GeneriekeFoutmelding Foutmelding bij SQLException, info wordt meegegeven.
     */
    @Override
    public long nieuweKlant(String voornaam,
                            String achternaam,
                            Adres adresgegevens) throws GeneriekeFoutmelding {
        long nieuwID = nieuweKlant(voornaam, achternaam, "", "", 0, adresgegevens, null);
        return nieuwID;
    }

    /**
     * Maakt een nieuwe klant aan in de database met voor- en achternaam.
     * Er wordt in de database automatisch een uniek ID gegenereerd welke automatisch verhoogd wordt.
     * Aangezien geen adres wordt meegegeven wordt een null waarde gestuurd naar de HOOFDMETHODE van
     * nieuweKlant.
     *
     * @param voornaam De voornaam van de klant (max 50 karakters).
     * @param achternaam De achternaam van de klant (max 51 karakters).
     */
    @Override
    public long nieuweKlant(String voornaam,
                            String achternaam) throws GeneriekeFoutmelding {
        long nieuwID = nieuweKlant(voornaam, achternaam, null);
        return nieuwID;
    }

    /** READ METHODS */

    /**
     * Zoekt het klant_id op van de klant.
     * De uniekheid van een klant is op basis van voornaam, achternaam en email, hier kan er dus maar 1 van bestaan.
     *
     * @param voornaam De te zoeken voornaam
     * @param achternaam De te zoeken achternaam
     * @param email De te zoeken email van de klant
     * @return Het klant_id van de klant
     */
    @Override
    public long getKlantID(String voornaam, String achternaam, String email) throws GeneriekeFoutmelding {
        String query = "SELECT klant_id " +
                "FROM KLANT " +
                "WHERE " +
                "voornaam = ? AND " +
                "achternaam = ? AND " +
                "email = ?;";

        try (
                Connection connection = connPool.verkrijgConnectie();
                PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.setString(1, voornaam);
            statement.setString(2, achternaam);
            statement.setString(3, email);

            try (
                    ResultSet rs = statement.executeQuery();
            ){
                // Als er een resultaat gevonden is bestaat de klant niet en wordt er een foutmelding gegooid.
                if (!rs.next()) {
                    DeLogger.getLogger().warn("KLANT NIET GEVONDEN: " + voornaam + "/" + achternaam + "/" + email);
                    throw new GeneriekeFoutmelding("KlantDAOMySQL: KLANT NIET GEVONDEN");
                }
                else {
                    return rs.getLong(1); // Door if-statement is rs al bij next()
                }
            }
        } catch (SQLException ex) {
            DeLogger.getLogger().error("SQL FOUT TIJDENS OPZOEKEN KLANT ID: " + ex.getMessage());
            throw new GeneriekeFoutmelding("KlantDAOMySQL: SQL FOUT TIJDENS OPZOEKEN KLANT ID: " + ex.getMessage());
        }
    }

    /**
     * Deze method haalt alle klanten op uit de database en stopt ze in een ArrayList waarna, zie @return.
     *
     * @return een ListIterator wordt teruggegeven van de ArrayList met daarin Klant-objecten.
     * @throws GeneriekeFoutmelding Foutmelding bij SQLException, info wordt meegegeven.
     */
    @Override
    public ListIterator<Klant> getAlleKlanten() throws GeneriekeFoutmelding {
        String query = "SELECT * FROM KLANT";
        try (
                Connection connection = connPool.verkrijgConnectie();
                PreparedStatement statement = connection.prepareStatement(query);

        ) {
            try (
                    ResultSet resultSet = statement.executeQuery();
            ) {
                klantenLijst = voegResultSetInLijst(resultSet);

                return klantenLijst.listIterator();
            }

        } catch (SQLException ex) {
            DeLogger.getLogger().error("SQL FOUT TIJDEN OPHALEN KLANTEN: " + ex.getMessage());
            throw new GeneriekeFoutmelding("KlantDAOMySQL: SQL FOUT TIJDEN OPHALEN KLANTEN:" + ex.getMessage());
        }
    }

    /**
     * HOOFD READ METHODE.
     *
     * In deze methode kan een klant-object ontvangen en op basis van de ingevulde velden de klant(en)
     * opzoeken.
     *
     * @param klant De klantgegevens in een Klant-Object dat opgezocht dient te worden.
     * @return een ListIterator wordt teruggegeven van de ArrayList met daarin Klant-objecten.
     * @throws GeneriekeFoutmelding
     */
    @Override
    public ListIterator<Klant> getKlantOpKlant(Klant klant) throws GeneriekeFoutmelding {
        if (klant != null && klant.getKlant_id() != -1 ) {
            String query = "SELECT * FROM " +
                    "KLANT WHERE " +
                    "klant_id LIKE ? AND " +
                    "voornaam LIKE ? AND " +
                    "achternaam LIKE ? AND " +
                    "tussenvoegsel LIKE ? " +
                    "AND email LIKE ?";

            try (
                    Connection connection = connPool.verkrijgConnectie();
                    PreparedStatement statement = connection.prepareStatement(query);
            ) {
                statement.setString(1, klant.getKlant_id() == 0 ? "%" : String.valueOf(klant.getKlant_id()) );
                statement.setString(2, klant.getVoornaam().equals("") |  klant.getVoornaam() == null ? "%" : klant.getVoornaam());
                statement.setString(3, klant.getAchternaam().equals("") ? "%" : klant.getAchternaam());
                statement.setString(4, klant.getTussenvoegsel().equals("") ? "%" : klant.getTussenvoegsel());
                statement.setString(5, klant.getEmail().equals("") ? "%" : klant.getEmail());

                try (
                        ResultSet resultSet = statement.executeQuery();
                ) {
                    klantenLijst = voegResultSetInLijst(resultSet);
                    return klantenLijst.listIterator();
                }
            } catch (SQLException ex) {
                DeLogger.getLogger().error("SQL FOUT TIJDENS OPZOEKEN KLANT " + ex.getMessage());
                throw new GeneriekeFoutmelding("KlantDAOMySQL: SQL FOUT TIJDENS OPZOEKEN KLANT " + ex.getMessage());
            }
        } else {
            DeLogger.getLogger().warn("ER DIENT EEN GEVULD KLANTOBJECT MEEGEGEVEN TE WORDEN OM EEN KLANT TE ZOEKEN");
            throw new GeneriekeFoutmelding("KlantDAOMySQL: ER DIENT EEN GEVULD KLANTOBJECT MEEGEGEVEN TE WORDEN OM EEN KLANT TE ZOEKEN");
        }
    }

    /**
     * Deze methode haalt op basis van klantId klanten (als het goed is 1) op uit de database en geeft dit
     * terug in en ListIterator van de ArrayList.
     *
     * @param klantId Het klantId van de op te zoeken klant.
     * @return een ListIterator wordt teruggegeven van de ArrayList met daarin Klant-objecten.
     * @throws GeneriekeFoutmelding Foutmelding bij SQLException, info wordt meegegeven.
     */
    @Override
    public ListIterator<Klant> getKlantOpKlant(long klantId) throws GeneriekeFoutmelding {
        return getKlantOpKlant(new Klant(klantId, "", "", "", "", null));
    }

    /**
     * Deze methode haalt op basis van de voornaam van een klant informatie uit de database en geeft dit
     * terug in en ListIterator van de ArrayList.
     *
     * @param voornaam Voornaam van de te zoeken klant(en).
     * @return een ListIterator wordt teruggegeven van de ArrayList met daarin Klant-objecten.
     * @throws GeneriekeFoutmelding Foutmelding bij SQLException, info wordt meegegeven.
     */
    @Override
    public ListIterator<Klant> getKlantOpKlant(String voornaam) throws GeneriekeFoutmelding {
        return getKlantOpKlant(new Klant(0, voornaam, "", "", "", null));
    }

    /**
     * Deze methode haalt op basis van de voor- en achternaam an een klant informatie uit de database en geeft dit
     * terug in en ListIterator van de ArrayList.
     *
     * @param voornaam Voornaam van de te zoeken klant(en).
     * @param achternaam Achternaam van de te zoeken klant(en).
     * @return een ListIterator wordt teruggegeven van de ArrayList met daarin Klant-objecten.
     * @throws GeneriekeFoutmelding Foutmelding bij SQLException, info wordt meegegeven.
     */
    @Override
    public ListIterator<Klant> getKlantOpKlant(String voornaam,
                                               String achternaam) throws GeneriekeFoutmelding {
        return getKlantOpKlant(new Klant(0, voornaam, achternaam, "", "", null));
    }

    /**
     * HOOFD-READMETHODE VAN getKlantOpAdres
     *
     * Deze methode haalt op basis van adresgegevens klanten op uit de database en geeft dit
     * terug in en ListIterator van de ArrayList.
     *
     * @param adresgegevens Een Adres-object van de te zoeken klant(en).
     * @return een ListIterator wordt teruggegeven van de ArrayList met daarin Klant-objecten.
     * @throws GeneriekeFoutmelding Foutmelding bij SQLException, info wordt meegegeven.
     */
    @Override
    public ListIterator<Klant> getKlantOpAdres(Adres adresgegevens) throws GeneriekeFoutmelding {
        String query = "SELECT KLANT.* " +
                "FROM " +
                "KLANT_HEEFT_ADRES, ADRES, KLANT " +
                "WHERE " +
                "straatnaam LIKE ? AND " +
                "postcode LIKE ? AND " +
                "toevoeging LIKE ? AND " +
                "huisnummer LIKE ? AND " +
                "woonplaats LIKE ? " +
                "AND " +
                "KLANT_HEEFT_ADRES.adres_id_adres = ADRES.ADRES_id AND " +
                "KLANT_HEEFT_ADRES.klant_id_klant = KLANT.KLANT_ID " +
                "GROUP BY klant_id " +
                "ORDER BY klant_id;";
        try (
                Connection connection = connPool.verkrijgConnectie();
                PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setString(1, adresgegevens.getStraatnaam().equals("") ? "%" : adresgegevens.getStraatnaam());
            statement.setString(2, adresgegevens.getPostcode().equals("") ? "%" : adresgegevens.getPostcode());
            statement.setString(3, adresgegevens.getToevoeging().equals("") ? "%" : adresgegevens.getToevoeging());
            statement.setString(4, adresgegevens.getHuisnummer() == 0 ? "%" : String.valueOf(adresgegevens.getHuisnummer()));
            statement.setString(5, adresgegevens.getWoonplaats().equals("") ? "%" : adresgegevens.getWoonplaats());

            try (
                    ResultSet resultSet = statement.executeQuery(); )
            {
                klantenLijst = voegResultSetInLijst(resultSet);
                return klantenLijst.listIterator();
            }

        } catch (SQLException ex) {
            DeLogger.getLogger().error("SQL FOUT TIJDENS OPZOEKEN KLANT OP VOLLE ADRES: " + ex.getMessage());
            throw new GeneriekeFoutmelding("KlantDAOMySQL: SQL FOUT TIJDENS OPZOEKEN KLANT OP VOLLE ADRES: " + ex.getMessage());
        }
    }

    /**
     * Deze methode haalt op basis van adresgegevens klanten op uit de database en geeft dit
     * terug in en ListIterator van de ArrayList.
     *
     * @param straatnaam Straatnaam van de te zoeken klant(en).
     * @return een ListIterator wordt teruggegeven van de ArrayList met daarin Klant-objecten.
     * @throws GeneriekeFoutmelding Foutmelding bij SQLException, info wordt meegegeven.
     */

    @Override
    public ListIterator<Klant> getKlantOpAdres(String straatnaam) throws GeneriekeFoutmelding {
        return getKlantOpAdres(new Adres(straatnaam, "", "", 0, ""));
    }

    /**
     * Deze methode haalt op basis van een postcode en huisnummer klanten op uit de database en geeft dit
     * terug in en ListIterator van de ArrayList.
     *
     * @param postcode De postcode van de te zoeken klant(en).
     * @param huisnummer Het huisnummer van de te zoeken klant(en).
     * @return een ListIterator wordt teruggegeven van de ArrayList met daarin Klant-objecten.
     * @throws GeneriekeFoutmelding Foutmelding bij SQLException, info wordt meegegeven.
     */
    @Override
    public ListIterator<Klant> getKlantOpAdres(String postcode,
                                               int huisnummer) throws GeneriekeFoutmelding {
        return getKlantOpAdres(new Adres("", postcode, "", huisnummer, ""));
    }

    /**
     * Deze methode haalt op basis van bestelId klanten op uit de database en geeft dit
     * terug in en ListIterator van de ArrayList.
     *
     * @param bestellingId Het bestelId van de te zoeken klant(en).
     * @return een ListIterator wordt teruggegeven van de ArrayList met daarin Klant-objecten.
     * @throws GeneriekeFoutmelding Foutmelding bij SQLException, info wordt meegegeven.
     */
    @Override
    public ListIterator<Klant> getKlantOpBestelling(long bestellingId) throws GeneriekeFoutmelding {
        String query = "SELECT klant_id FROM " +
                "BESTELLING WHERE " +
                "bestelling_id = ? " +
                "LIMIT 1;";
        try (
                Connection connection = connPool.verkrijgConnectie();
                PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setLong(1, bestellingId);

            try (
                    ResultSet resultSet = statement.executeQuery();
            ) {
                while (resultSet.next()) {
                    return getKlantOpKlant((long) resultSet.getInt(1));
                }
            }
        } catch (SQLException ex) {
            DeLogger.getLogger().error("SQL FOUT TIJDENS OPZOEKEN KLANT OP BESTELLINGID: " + ex.getMessage());
            throw new GeneriekeFoutmelding("KlantDAOMySQL: SQL FOUT TIJDENS OPZOEKEN KLANT OP BESTELLINGID: " +
                    ex.getMessage());
        }
        return null;
    }

    /** UPDATE METHODS */

    /**
     * Methode om een klant met een bepaald klant_id zijn naamgegevens up te daten.
     *
     * @param KlantId Het klantId van de klant wiens gegevens gewijzigd dienen te worden.
     * @param voornaam De 'gewijzigde' voornaam van de klant.
     * @param achternaam De 'gewijzigde' achternaam van de klant.
     * @param tussenvoegsel Het 'gewijzigde' tussenvoegsel van de klant.
     * @param email Het gewijzigde emailadres van de klant.
     * @throws GeneriekeFoutmelding Foutmelding bij SQLException, info wordt meegegeven.
     */
    @Override
    public void updateKlant(Long KlantId,
                            String voornaam,
                            String achternaam,
                            String tussenvoegsel,
                            String email) throws GeneriekeFoutmelding {
        String query = "UPDATE KLANT " +
                "SET " +
                "voornaam = ?, " +
                "achternaam = ?, " +
                "tussenvoegsel = ?, " +
                "email = ? " +
                "WHERE " +
                "klant_id = ?;";
        try (
                Connection connection = connPool.verkrijgConnectie();
                PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setString(1, voornaam);
            statement.setString(2, achternaam);
            statement.setString(3, tussenvoegsel);
            statement.setString(4, email);
            statement.setLong(5, KlantId);
            statement.execute();

        } catch (SQLException ex) {
            DeLogger.getLogger().error("SQL FOUT TIJDENS UPDATEN KLANT: " + ex.getMessage());
            throw new GeneriekeFoutmelding("KlantDAOMySQL: SQL FOUT TIJDENS UPDATEN KLANT: " + ex.getMessage());
        }
    }

    /**
     * Methode om een klant met een bepaald klant_id zijn naam en tevens
     * adres gegevens up te daten.
     *
     * @param KlantId Het klantId van de klant wiens gegevens gewijzigd dienen te worden.
     * @param voornaam De 'gewijzigde' voornaam van de klant.
     * @param achternaam De 'gewijzigde' achternaam van de klant.
     * @param tussenvoegsel Het 'gewijzigde' tussenvoegsel van de klant.
     * @param email Het 'gewijzigde' emailadres van de klant.
     * @param adresgegevens De 'gewijzigde' adresgegevens van de klant in Klantobject.
     * @throws GeneriekeFoutmelding Foutmelding bij SQLException, info wordt meegegeven.
     */
    @Override
    public void updateKlant(long KlantId, String voornaam,
                            String achternaam,
                            String tussenvoegsel,
                            String email,
                            long adres_id,
                            Adres adresgegevens) throws GeneriekeFoutmelding {
        updateKlant(KlantId, voornaam, achternaam, tussenvoegsel, email);
        adresDAO = new AdresDAOMySQL();
        adresDAO.updateAdres(adres_id, adresgegevens);
    }

    /** DELETE METHODS */

    /**
     * Methode om een klant te verwijderen op basis van ID. Alle bestellingen van de klant worden
     * tevens ook op non-actief gezet.
     *
     * @param klantId Klant_id van de te verwijderen klant.
     * @throws GeneriekeFoutmelding Foutmelding bij SQLException, info wordt meegegeven.
     */

    @Override
    public void schakelStatusKlant(long klantId, int status) throws GeneriekeFoutmelding {
        String query =
                "UPDATE KLANT " +
                        "SET " +
                        "klantActief = ? " +
                        "WHERE " +
                        "klant_id = ?";

        try (
                Connection connection = connPool.verkrijgConnectie();
                PreparedStatement statement = connection.prepareStatement(query);

        ) {
            bestellingDAO = new BestellingDAOMySQL();
            bestellingDAO.verwijderAlleBestellingenKlant(klantId); //TODO weer integreren als af is
            statement.setInt(1, status);
            statement.setLong(2, klantId);
            statement.execute();

        } catch (SQLException ex) {
            DeLogger.getLogger().error("SQL FOUT TIJDENS KLANT OP ID INACTIEF ZETTEN: " + ex.getMessage());
            throw new GeneriekeFoutmelding("KlantDAOMySQL: SQL FOUT TIJDENS KLANT OP ID INACTIEF ZETTEN: " + ex.getMessage());
        }
    }

    @Override
    public void schakelStatusKlant(String voornaam, String achternaam) throws GeneriekeFoutmelding {
        /**
         * Methode om een klant zijn/haar status te switchen op basis van alleen voor- en achternaam;
         *
         * @param voornaam Voornaam van de te verwijderen
         * @param achternaam Achternaam van de te verwijderen klant
         * @throws GeneriekeFoutmelding Foutmelding bij SQLException, info wordt meegegeven.
         */

        ListIterator<Klant> klantListIterator = getKlantOpKlant(voornaam, achternaam);

        while (klantListIterator.hasNext()) {
            Klant tijdelijkeKlant = klantListIterator.next();
            schakelStatusKlant(tijdelijkeKlant.getKlant_id(),
                    (tijdelijkeKlant.getKlantActief().charAt(0) == '0' ? 1 : 0));
        }
    }

    /**
     * Methode om een klant zijn/haar status te switchen op basis van naamgegevens. Alle bestellingen van de klant worden
     * tevens ook op non-actief gezet.
     *
     * @param voornaam De voornaam van de te verwijderen klant.
     * @param achternaam De achternaam van de te verwijderen klant.
     * @param tussenvoegsel Het tussenvoegsel van de te verwijderen klant.
     * @throws GeneriekeFoutmelding Foutmelding bij SQLException, info wordt meegegeven.
     */
    @Override
    public void schakelStatusKlant(String voornaam,
                                   String achternaam,
                                   String tussenvoegsel) throws GeneriekeFoutmelding {
        ListIterator<Klant> klantListIterator =
                getKlantOpKlant(new Klant(0, voornaam, achternaam, tussenvoegsel, "", null));

        while (klantListIterator.hasNext()) {
            Klant tijdelijkeKlant = klantListIterator.next();
            schakelStatusKlant(tijdelijkeKlant.getKlant_id(),
                    (tijdelijkeKlant.getKlantActief().charAt(0) == '0' ? 1 : 0));
        }
    }


    /**
     * Methode om een klant te verwijderen op basis van een bestelnummer.
     *
     * @param bestellingId Bestel-ID van de te verwijderen klant.
     * @throws GeneriekeFoutmelding Foutmelding bij SQLException, info wordt meegegeven.
     */
    @Override
    public long verwijderKlantOpBestellingId(long bestellingId) throws GeneriekeFoutmelding {

        //TODO: Aanpassen als bestelling weer klaar is
        // Klant wordt opgehaald uit de database om op basis van BestelID het klantID te vinden.
        ListIterator<Klant> klantenIterator = getKlantOpBestelling(bestellingId);
        long verwijderdId = -1;

        // De klantenlijst wordt doorlopen en de klant wordt verwijderd.
        if (klantenIterator != null) {
            while (klantenIterator.hasNext()) {
                Klant tijdelijkeKlant = klantenIterator.next();
                schakelStatusKlant(tijdelijkeKlant.getKlant_id(), 0);
            }
        }

        // Het verwijderde klantID wordt geretourneerd (o.a. gebruikt om te testen)
        return verwijderdId;
    }

    /** ANDERE METHODS */

    /**
     * Methode om consistent een resultset van een klant-rij in een ArrayList te kunnen vormen.
     *
     * @param resultSet De resultset met klantrijen.
     * @return Een ArrayList met Klant objecten.
     * @throws GeneriekeFoutmelding Foutmelding bij SQLException, info wordt meegegeven.
     */
    private ArrayList<Klant> voegResultSetInLijst(ResultSet resultSet) throws GeneriekeFoutmelding {
        try {
            klantenLijst = new ArrayList<>();
            int klantenTeller = 0;
            while (resultSet.next()) {
                // Klant aanmaken met lege waarden, default constructor zet adres
                // op null (ivm equals methode) derhalve dient er een waarde aanwezig
                // te zijn om een null pointer exception te voorkomen.
                Klant tijdelijkeKlant = new Klant(0, "", "","","", new Adres());

                tijdelijkeKlant.setKlant_id(resultSet.getLong(1));
                tijdelijkeKlant.setVoornaam(resultSet.getString(2));
                tijdelijkeKlant.setAchternaam(resultSet.getString(3));
                tijdelijkeKlant.setTussenvoegsel(resultSet.getString(4));
                tijdelijkeKlant.setEmail(resultSet.getString(5));
                tijdelijkeKlant.setDatumAanmaak(resultSet.getString(6));
                tijdelijkeKlant.setDatumGewijzigd(resultSet.getString(7));
                tijdelijkeKlant.setKlantActief(resultSet.getString(8));
                klantenLijst.add(klantenTeller, tijdelijkeKlant);
                klantenTeller++; }

            return klantenLijst;
        } catch (SQLException ex) {
            DeLogger.getLogger().error("FOUT TIJDENS RESULTSET VOEGEN IN LIJST: " + ex.getMessage());
            throw new GeneriekeFoutmelding("KlantDAOMySQL: FOUT TIJDENS RESULTSET VOEGEN IN LIJST: " + ex.getMessage());
        }
    }

    /**
     * Handzame methode voor tijdens test / develop doel-einden eenvoudig informatie naar
     * de console te printen.
     *
     * @param klantenIterator Een iterator van de klantenlijst
     * @throws GeneriekeFoutmelding Foutmelding met omschrijving.
     */
    public void printKlantenInConsole(ListIterator<Klant> klantenIterator) throws GeneriekeFoutmelding {

        // Per klant een print, per klant alle adressen
        while (klantenIterator.hasNext()) {
            Klant tijdelijkeKlant = klantenIterator.next();
            if (tijdelijkeKlant.getKlantActief().charAt(0) == '1') {
                System.out.println("\n\n\t------------------KLANT " + tijdelijkeKlant.getKlant_id() + " BEGIN---------------------------");
                System.out.print("\n\tKLANTID:           " + tijdelijkeKlant.getKlant_id());
                System.out.print("\n\tVoornaam:          " + tijdelijkeKlant.getVoornaam());
                System.out.print("\n\tAchternaam:        " + tijdelijkeKlant.getAchternaam());
                System.out.print("\n\tTussenvoegsel:     " + tijdelijkeKlant.getTussenvoegsel());
                System.out.print("\n\tE-Mail:            " + tijdelijkeKlant.getEmail());
                System.out.print("\n\tDatum Aangemaakt:  " + tijdelijkeKlant.getDatumAanmaak());
                System.out.print("\n\tDatum Gewijzigd:   " + tijdelijkeKlant.getDatumGewijzigd());

                System.out.print("\n");
                System.out.print("\n\tADRES(SEN)");
                System.out.print("\n\t----------");

                // DAO voor adres-acties. Lijst verkrijgen van alle adressen bijbehorend bij klant_id
                adresDAO = new AdresDAOMySQL();
                ListIterator<Adres> adresListIterator = adresDAO.getAdresOpKlantID(tijdelijkeKlant.getKlant_id());
                while (adresListIterator.hasNext()) {

                    Adres tijdelijkAdres = adresListIterator.next();

                    if (tijdelijkAdres.getAdresActief().charAt(0) == '1') {
                        System.out.print("\n\t\t                   ");
                        System.out.print("\n\t\tADRESID:           " + tijdelijkAdres.getAdres_id());
                        System.out.print("\n\t\tStraatnaam:        " + tijdelijkAdres.getStraatnaam());
                        System.out.print("\n\t\tPostcode:          " + tijdelijkAdres.getPostcode());
                        System.out.print("\n\t\tToevoeging:        " + tijdelijkAdres.getToevoeging());
                        System.out.print("\n\t\tHuisnummer:        " + tijdelijkAdres.getHuisnummer());
                        System.out.print("\n\t\tWoonplaats:        " + tijdelijkAdres.getWoonplaats());
                        System.out.print("\n\t\tDatum Aangemaakt:  " + tijdelijkAdres.getDatumAanmaak());
                        System.out.print("\n\t\tDatum Gewijzigd:   " + tijdelijkAdres.getDatumGewijzigd());
                    }
                    else
                    {
                        System.out.println("\n\t\tADRESID: " + tijdelijkAdres.getAdres_id() + " INACTIEF");
                    }
                }
                System.out.println("\n\n\t------------------KLANT " + tijdelijkeKlant.getKlant_id() + " EIND----------------------------");

            }
            else {
                System.out.println("\n\t------------------KLANT " + tijdelijkeKlant.getKlant_id() + " INACTIEF------------------------");
            }
        }
        System.out.println("\n");
    }
}

