package mysql;

import com.mysql.jdbc.Statement;
import exceptions.RSVIERException;
import interfaces.KlantDAO;
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
    // Veel gebruikte en gedeelde variabelen
    String query = "";
    ArrayList<Klant> klantenLijst;
    BestellingDAOMySQL bestellingDAO;
    AdresDAOMySQL adresDAO;
    Connection connection; //TODO weghalen

    // =
    /** CREATE METHODS */

    //TODO: JavaDOC
    @Override
    public long nieuweKlant(Klant nieuweKlant) throws RSVIERException {
        if (nieuweKlant != null) {
            long nieuwId =  nieuweKlant(nieuweKlant.getVoornaam(),
                    nieuweKlant.getAchternaam(),
                    nieuweKlant.getTussenvoegsel(),
                    nieuweKlant.getEmail(),
                    0, //TODO: ondersteuning voor meegeven bestaand adres?
                    nieuweKlant.getAdresGegevens(),
                    nieuweKlant.getBestellingGegevens());
            return nieuwId;
        }
        else {
            throw new RSVIERException("KlantDAOMySQL: KAN GEEN KLANT AANMAKEN MET NULL OBJECT");
        }
    }

    /**
     * Maakt een nieuwe klant aan in de database met voornaam, achternaam en adresgegevens.
     * Er wordt in de database automatisch een uniek ID gegenereerd welke automatisch verhoogd wordt.
     *
     * @param voornaam De voornaam van de klant (max 50 karakters).
     * @param achternaam De achternaam van de klant (max 51 karakters).
     * @param adresgegevens De adresgegevens van de klant in een Adres object (Adres).
     * @throws RSVIERException Foutmelding bij SQLException, info wordt meegegeven.
     */
    @Override
    public long nieuweKlant(String voornaam,
                            String achternaam,
                            Adres adresgegevens) throws RSVIERException {
        long nieuwID = nieuweKlant(voornaam, achternaam, "", "", 0, adresgegevens, null);

        return nieuwID;
    }

    /**
     * Maakt een nieuwe klant aan in de database met voor- en achternaam.
     * Er wordt in de database automatisch een uniek ID gegenereerd welke automatisch verhoogd wordt.
     *
     * @param voornaam De voornaam van de klant (max 50 karakters).
     * @param achternaam De achternaam van de klant (max 51 karakters).
     */
    @Override
    public long nieuweKlant(String voornaam,
                            String achternaam) throws RSVIERException {
        long nieuwID = nieuweKlant(voornaam, achternaam, null);

        return nieuwID;
    }

    /**
     * Maakt een nieuwe klant aan in de database met alle naamgegevens.
     * Als er adres en/of bestelgegevens aanwezig zijn worden deze tevens ook toegevoegd.
     * Er wordt in de database automatisch een uniek ID gegenereerd welke automatisch verhoogd wordt.
     *
     * @param voornaam De voornaam van de klant (max 50 karakters).
     * @param achternaam De achternaam van de klant (max 51 karakters).
     * @param tussenvoegsel Tussenvoegsel van de klant (max 10 karakters).
     * @param email Emailadres van de klant (max 80 karakters).
     * @param adresgegevens Adresgegevens van de klant in een Klant object (zie Klant).
     * @param bestelGegevens Bestelgegevens van de klant in een Bestel object (zie Bestelling).
     * @throws RSVIERException Foutmelding bij SQLException, info wordt meegegeven.
     */

    @Override
    public long nieuweKlant(String voornaam,
                            String achternaam,
                            String tussenvoegsel,
                            String email,
                            long adres_id,
                            Adres adresgegevens,
                            Bestelling bestelGegevens) throws RSVIERException {

        ResultSet generatedKeys = null;
        query = "INSERT INTO KLANT " +
                "(voornaam, achternaam, tussenvoegsel, email) " +
                "VALUES " +
                "(?,        ?,          ?,              ?);";

        try (
                Connection connection = MySQLConnectieLeverancier.getConnection();
                PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        )
        {

            // Voer query uit en haal de gegenereerde sleutels op bij deze query
            statement.setString(1, voornaam);
            statement.setString(2, achternaam);
            statement.setString(3, tussenvoegsel);
            statement.setString(4, email);
            statement.execute();

            // Ophalen van de laatste genegeneerde sleutel uit de generatedkeys (de nieuwe klant_id)
            long nieuwId = 0;
            generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) { nieuwId = generatedKeys.getInt(1); }

            // Als er een adres_id wordt meegegeven betekent dit dat er een bestaand adres gekoppeled wordt
            // aan een nieuwe klant
            if (adres_id != 0 && adresgegevens == null) {
                adresDAO = new AdresDAOMySQL();
                adresDAO.koppelAdresAanKlant(nieuwId, adres_id);
            }

            // Als er adresgegeven worden meegegeven wordt er een adres aangemaakt op basis van het nieuwe klantId
            if (adresgegevens != null && adres_id == 0) {
                adresDAO = new AdresDAOMySQL();
                adresDAO.nieuwAdres(nieuwId, adresgegevens);
            }

            // Als er bestegegevens zijn meegegeven worden deze bijgevoegd
            // TODO: Bestelling werkt nog na aanpassingen?
            if (bestelGegevens != null) {
                bestellingDAO = new BestellingDAOMySQL();
                bestelGegevens.setKlant_id(nieuwId);
                bestellingDAO.nieuweBestelling(bestelGegevens);
            }
//            System.out.println("\n\tKlantDAOMySQL: KLANT: " + voornaam + " SUCCESVOL GEMAAKT");

            return nieuwId;
        } catch (SQLException ex) {
            if (ex.getMessage().contains("Duplicate entry"))
                throw new RSVIERException("KlantDAOMySQL: DEZE KLANT BESTAAT AL IN DE DATABASE MET ID: " +
                        getKlantID(voornaam, achternaam, email));
            else
                throw new RSVIERException("KlantDAOMySQL: SQL FOUT TIJDENS AANMAKEN KLANT: " + ex.getMessage());
        } finally {
            MySQLHelper.close(generatedKeys);
        }
    }


    // =
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
    public long getKlantID(String voornaam, String achternaam, String email) throws RSVIERException {
        ResultSet rs = null;
        String query = "SELECT klant_id " +
                "FROM KLANT " +
                "WHERE " +
                "voornaam = ? AND " +
                "achternaam = ? AND " +
                "email = ?;";

        try (
                Connection connection = MySQLConnectieLeverancier.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.setString(1, voornaam);
            statement.setString(2, achternaam);
            statement.setString(3, email);
            rs = statement.executeQuery();

            // Als er een resultaat gevonden is bestaat de klant niet en wordt er een foutmelding gegooid.
            if (!rs.next())
                throw new RSVIERException("KlantDAOMySQL: KLANT NIET GEVONDEN");
            else {
                return rs.getLong(1); // Door if-statement is rs al bij next()
            }

        } catch (SQLException ex) {
            throw new RSVIERException("KlantDAOMySQL: SQL FOUT TIJDENS OPZOEKEN KLANT ID: " + ex.getMessage());
        } finally {
            MySQLHelper.close(rs);
        }
    }

    /**
     * Deze method haalt alle klanten op uit de database en stopt ze in een ArrayList waarna, zie @return.
     *
     * @return een ListIterator wordt teruggegeven van de ArrayList met daarin Klant-objecten.
     * @throws RSVIERException Foutmelding bij SQLException, info wordt meegegeven.
     */
    @Override
    public ListIterator<Klant> getAlleKlanten() throws RSVIERException {
        resultSet = null;
        String query = "SELECT * FROM KLANT";
        try (
                Connection connection = MySQLConnectieLeverancier.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);

        ) {
            resultSet = statement.executeQuery();
            klantenLijst = voegResultSetInLijst(resultSet);
            return klantenLijst.listIterator();

        } catch (SQLException ex) {
            throw new RSVIERException("KlantDAOMySQL: SQL FOUT TIJDEN OPHALEN KLANTEN");
        } finally {
            MySQLHelper.close(resultSet);
        }
    }

    /**
     * Deze methode haalt op basis van klantId klanten (als het goed is 1) op uit de database en geeft dit
     * terug in en ListIterator van de ArrayList.
     *
     * @param klantId Het klantId van de op te zoeken klant.
     * @return een ListIterator wordt teruggegeven van de ArrayList met daarin Klant-objecten.
     * @throws RSVIERException Foutmelding bij SQLException, info wordt meegegeven.
     */
    @Override
    public ListIterator<Klant> getKlantOpKlant(long klantId) throws RSVIERException {
        String query = "SELECT * FROM " +
                "KLANT WHERE " +
                "klant_id = ?;";
        try (
                Connection connection = MySQLConnectieLeverancier.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setLong(1, klantId);
            resultSet = statement.executeQuery();
            klantenLijst = voegResultSetInLijst(resultSet);
            return klantenLijst.listIterator();

        } catch (SQLException ex) {
            throw new RSVIERException("KlantDAOMySQL: SQL FOUT TIJDENS OPZOEKEN KLANT OP KLANTID");
        } finally {
            MySQLHelper.close(resultSet);
        }
    }

    /**
     * Deze methode haalt op basis van de voornaam van een klant informatie uit de database en geeft dit
     * terug in en ListIterator van de ArrayList.
     *
     * @param voornaam Voornaam van de te zoeken klant(en).
     * @return een ListIterator wordt teruggegeven van de ArrayList met daarin Klant-objecten.
     * @throws RSVIERException Foutmelding bij SQLException, info wordt meegegeven.
     */
    @Override
    public ListIterator<Klant> getKlantOpKlant(String voornaam) throws RSVIERException {
        String query = "SELECT * FROM " +
                "KLANT WHERE " +
                "voornaam LIKE ?;";
        try (
                Connection connection = MySQLConnectieLeverancier.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setString(1, voornaam);
            resultSet = statement.executeQuery();
            klantenLijst = voegResultSetInLijst(resultSet);
            return klantenLijst.listIterator();

        } catch (SQLException ex) {
            throw new RSVIERException("KlantDAOMySQL: SQL FOUT TIJDENS OPZOEKEN KLANT OP VOORNAAM");
        } finally {
            MySQLHelper.close(resultSet);
        }
    }

    /**
     * Deze methode haalt op basis van de voor- en achternaam an een klant informatie uit de database en geeft dit
     * terug in en ListIterator van de ArrayList.
     *
     * @param voornaam Voornaam van de te zoeken klant(en).
     * @param achternaam Achternaam van de te zoeken klant(en).
     * @return een ListIterator wordt teruggegeven van de ArrayList met daarin Klant-objecten.
     * @throws RSVIERException Foutmelding bij SQLException, info wordt meegegeven.
     */
    @Override
    public ListIterator<Klant> getKlantOpKlant(String voornaam,
                                               String achternaam) throws RSVIERException {
        String query = "SELECT * FROM " +
                "KLANT WHERE " +
                "voornaam LIKE ? AND achternaam LIKE ?;";
        try (
                Connection connection = MySQLConnectieLeverancier.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setString(1, voornaam);
            statement.setString(2, achternaam);
            resultSet = statement.executeQuery();
            klantenLijst = voegResultSetInLijst(resultSet);
            return klantenLijst.listIterator();

        } catch (SQLException ex) {
            throw new RSVIERException("KlantDAOMySQL: SQL FOUT TIJDENS OPZOEKEN KLANT OP VOOR & ACHTERNAAM");
        } finally {
            MySQLHelper.close(resultSet);
        }
    }

    /**
     * Deze methode haalt op basis van adresgegevens klanten op uit de database en geeft dit
     * terug in en ListIterator van de ArrayList.
     *
     * @param adresgegevens Een Adres-object van de te zoeken klant(en).
     * @return een ListIterator wordt teruggegeven van de ArrayList met daarin Klant-objecten.
     * @throws RSVIERException Foutmelding bij SQLException, info wordt meegegeven.
     */
    @Override
    public ListIterator<Klant> getKlantOpAdres(Adres adresgegevens) throws RSVIERException {
        // TODO: CHECKEN OF ER NIET 1 METHOD MOGELIJK IS
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
                Connection connection = MySQLConnectieLeverancier.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setString(1, adresgegevens.getStraatnaam());
            statement.setString(2, adresgegevens.getPostcode());
            statement.setString(3, adresgegevens.getToevoeging());
            statement.setInt(4, adresgegevens.getHuisnummer());
            statement.setString(5, adresgegevens.getWoonplaats());
            resultSet = statement.executeQuery();
            klantenLijst = voegResultSetInLijst(resultSet);
            return klantenLijst.listIterator();

        } catch (SQLException ex) {
            throw new RSVIERException("KlantDAOMySQL: SQL FOUT TIJDENS OPZOEKEN KLANT OP VOLLE ADRES");
        } finally {
            MySQLHelper.close(resultSet);
        }
    }

    /**
     * Deze methode haalt op basis van adresgegevens klanten op uit de database en geeft dit
     * terug in en ListIterator van de ArrayList.
     *
     * @param straatnaam Straatnaam van de te zoeken klant(en).
     * @return een ListIterator wordt teruggegeven van de ArrayList met daarin Klant-objecten.
     * @throws RSVIERException Foutmelding bij SQLException, info wordt meegegeven.
     */

    @Override
    public ListIterator<Klant> getKlantOpAdres(String straatnaam) throws RSVIERException {
        String query =
                "SELECT KLANT.* " +
                        "FROM " +
                        "KLANT_HEEFT_ADRES, ADRES, KLANT " +
                        "WHERE " +
                        "ADRES.straatnaam = ? " +
                        "AND " +
                        "KLANT_HEEFT_ADRES.adres_id_adres = ADRES.ADRES_id AND " +
                        "KLANT_HEEFT_ADRES.klant_id_klant = KLANT.KLANT_ID " +
                        "GROUP BY klant_id " +
                        "ORDER BY klant_id;";

        try (
                Connection connection = MySQLConnectieLeverancier.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setString(1, straatnaam);
            resultSet = statement.executeQuery();
            klantenLijst = voegResultSetInLijst(resultSet);
            return klantenLijst.listIterator();

        } catch (SQLException ex) {
            throw new RSVIERException("KlantDAOMySQL: SQL FOUT TIJDENS OPZOEKEN KLANT OP STRAATNAAM");
        } finally {
            MySQLHelper.close(resultSet);
        }
    }

    /**
     * Deze methode haalt op basis van een postcode en huisnummer klanten op uit de database en geeft dit
     * terug in en ListIterator van de ArrayList.
     *
     * @param postcode De postcode van de te zoeken klant(en).
     * @param huisnummer Het huisnummer van de te zoeken klant(en).
     * @return een ListIterator wordt teruggegeven van de ArrayList met daarin Klant-objecten.
     * @throws RSVIERException Foutmelding bij SQLException, info wordt meegegeven.
     */
    @Override
    public ListIterator<Klant> getKlantOpAdres(String postcode,
                                               int huisnummer) throws RSVIERException {
        String query =
                "SELECT KLANT.* " +
                        "FROM " +
                        "KLANT_HEEFT_ADRES, ADRES, KLANT " +
                        "WHERE " +
                        "ADRES.postcode = ? AND " +
                        "ADRES.huisnummer = ? " +
                        "AND " +
                        "KLANT_HEEFT_ADRES.adres_id_adres = ADRES.ADRES_id AND " +
                        "KLANT_HEEFT_ADRES.klant_id_klant = KLANT.KLANT_ID " +
                        "GROUP BY klant_id " +
                        "ORDER BY klant_id;";

        try (
                Connection connection = MySQLConnectieLeverancier.getConnection();

                PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setString(1, postcode);
            statement.setInt(2, huisnummer);
            resultSet = statement.executeQuery();
            klantenLijst = voegResultSetInLijst(resultSet);
            return klantenLijst.listIterator();

        } catch (SQLException ex) {
            throw new RSVIERException("KlantDAOMySQL: SQL FOUT TIJDENS OPZOEKEN KLANT OP POSTCODE EN HUISNUMMER");
        } finally {
            MySQLHelper.close(resultSet);
        }
    }

    /**
     * Deze methode haalt op basis van bestelId klanten op uit de database en geeft dit
     * terug in en ListIterator van de ArrayList.
     *
     * @param bestellingId Het bestelId van de te zoeken klant(en).
     * @return een ListIterator wordt teruggegeven van de ArrayList met daarin Klant-objecten.
     * @throws RSVIERException Foutmelding bij SQLException, info wordt meegegeven.
     */
    @Override
    public ListIterator<Klant> getKlantOpBestelling(long bestellingId) throws RSVIERException {
        connection = MySQLConnectieLeverancier.getConnection();
        try {
            query = "SELECT klant_id FROM " +
                    "BESTELLING WHERE " +
                    "bestelling_id = ? " +
                    "LIMIT 1;";
            statement = connection.prepareStatement(query);
            statement.setLong(1, bestellingId);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                return getKlantOpKlant((long)resultSet.getInt(1));
            }
        } catch (SQLException ex) {
            throw new RSVIERException("KlantDAOMySQL: SQL FOUT TIJDENS OPZOEKEN KLANT OP BESTELLINGID");
        } finally {
            MySQLHelper.close(connection, statement, resultSet);
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
     * @throws RSVIERException Foutmelding bij SQLException, info wordt meegegeven.
     */
    @Override
    public void updateKlant(Long KlantId,
                            String voornaam,
                            String achternaam,
                            String tussenvoegsel,
                            String email) throws RSVIERException {
        String query = "UPDATE KLANT " +
                "SET " +
                "voornaam = ?, " +
                "achternaam = ?, " +
                "tussenvoegsel = ?, " +
                "email = ? " +
                "WHERE " +
                "klant_id = ?;";
        try (
                Connection connection = MySQLConnectieLeverancier.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setString(1, voornaam);
            statement.setString(2, achternaam);
            statement.setString(3, tussenvoegsel);
            statement.setString(4, email);
            statement.setLong(5, KlantId);
            statement.execute();

        } catch (SQLException ex) {
            throw new RSVIERException("KlantDAOMySQL: SQL FOUT TIJDENS UPDATEN KLANT");
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
     * @throws RSVIERException Foutmelding bij SQLException, info wordt meegegeven.
     */
    @Override
    public void updateKlant(long KlantId, String voornaam,
                            String achternaam,
                            String tussenvoegsel,
                            String email,
                            long adres_id,
                            Adres adresgegevens) throws RSVIERException {
        updateKlant(KlantId, voornaam, achternaam, tussenvoegsel, email);
        adresDAO = new AdresDAOMySQL();
        adresDAO.updateAdres(adres_id, adresgegevens);
    }

    // =

    /** DELETE METHODS */

    /**
     * Methode om een klant te verwijderen op basis van ID. Alle bestellingen van de klant worden
     * tevens ook verwijderd.
     *
     * @param klantId Klant_id van de te verwijderen klant.
     * @throws RSVIERException Foutmelding bij SQLException, info wordt meegegeven.
     */

    @Override
    public long schakelStatusKlant(long klantId, int status) throws RSVIERException {
        String query =
                "UPDATE KLANT " +
                        "SET " +
                        "klantActief = ? " +
                        "WHERE " +
                        "klant_id = ?";

        long verwijderdID = -1;

        try (
                Connection connection = MySQLConnectieLeverancier.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);

        ) {
//            bestellingDAO = new BestellingDAOMySQL();
//            verwijderdID = bestellingDAO.verwijderAlleBestellingenKlant(klantId); //TODO weer integreren als af is
            statement.setInt(1, status);
            statement.setLong(2, klantId);
            statement.execute();

            return  verwijderdID;
        } catch (SQLException ex) {
            throw new RSVIERException("KlantDAOMySQL: SQL FOUT TIJDENS KLANT OP ID INACTIEF ZETTEN" + ex.getMessage());
        }
    }

    @Override
    public void schakelStatusKlant(String voornaam, String achternaam) throws RSVIERException {
        /**
         * Methode om een klant te verwijderen op basis van alleen voor- en achternaam;
         *
         * @param voornaam Voornaam van de te verwijderen
         * @param achternaam Achternaam van de te verwijderen klant
         * @throws RSVIERException Foutmelding bij SQLException, info wordt meegegeven.
         */

        String query = "SELECT klant_id FROM " +
                "KLANT " +
                "WHERE " +
                "voornaam LIKE ? AND " +
                "achternaam LIKE ?;";


        try (
                Connection connection = MySQLConnectieLeverancier.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
        ) {

            statement.setString(1, voornaam);
            statement.setString(2, achternaam);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                schakelStatusKlant(resultSet.getInt(1), 0);
            }

        } catch (SQLException ex) {
            throw new RSVIERException("KlantDAOMySQL: SQL FOUT TIJDENS VERWIJDEREN KLANT OP VOORNAAM & ACHTERNAAM");
        } finally {
            MySQLHelper.close(resultSet);
        }
    }

    /**
     * Methode om een klant te verwijderen op basis van naamgegevens. Alle bestellingen van de klant worden
     * tevens ook verwijderd.
     *
     * @param voornaam De voornaam van de te verwijderen klant.
     * @param achternaam De achternaam van de te verwijderen klant.
     * @param tussenvoegsel Het tussenvoegsel van de te verwijderen klant.
     * @throws RSVIERException Foutmelding bij SQLException, info wordt meegegeven.
     */
    @Override
    public void schakelStatusKlant(String voornaam,
                                   String achternaam,
                                   String tussenvoegsel) throws RSVIERException {
        String query = "SELECT klant_id FROM " +
                "KLANT " +
                "WHERE " +
                "voornaam LIKE ? AND " +
                "achternaam LIKE ? AND " +
                "tussenvoegsel LIKE ?;";

        try (
                Connection connection = MySQLConnectieLeverancier.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setString(1, voornaam);
            statement.setString(2, achternaam);
            statement.setString(3, tussenvoegsel);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                schakelStatusKlant(resultSet.getInt(1), 0);
            }

        } catch (SQLException ex) {
            throw new RSVIERException("KlantDAOMySQL: SQL FOUT TIJDENS VERWIJDEREN KLANT OP VOORNAAM, ACHTERNAAM & \" +\n" +
                    "                    \"TUSSENVOEGSEL\"");
        } finally {
            MySQLHelper.close(resultSet);
        }
    }


    /**
     * Methode om een klant te verwijderen op basis van een bestelnummer.
     *
     * @param bestellingId Bestel-ID van de te verwijderen klant.
     * @throws RSVIERException Foutmelding bij SQLException, info wordt meegegeven.
     */
    @Override
    public long verwijderKlantOpBestellingId(long bestellingId) throws RSVIERException {

        //TODO: Aanpassen als bestelling weer klaar is

        // Klant wordt opgehaald uit de database om op basis van BestelID het klantID te vinden.
        ListIterator<Klant> klantenIterator = getKlantOpBestelling(bestellingId);
        long verwijderdId = -1;

        // De klantenlijst wordt doorlopen en de klant wordt verwijderd.
        if (klantenIterator != null) {
            while (klantenIterator.hasNext()) {
                Klant tijdelijkeKlant = klantenIterator.next();
                verwijderdId = schakelStatusKlant(tijdelijkeKlant.getKlant_id(), 0);
            }
        }

        // Het verwijderde klantID wordt geretourneerd (o.a. gebruikt om te testen)
        return verwijderdId;
    }

    // =

    /** ANDERE METHODS */

    /**
     * Methode om consistent een resultset van een klant-rij in een ArrayList te kunnen vormen.
     *
     * @param resultSet De resultset met klantrijen.
     * @return Een ArrayList met Klant objecten.
     * @throws RSVIERException Foutmelding bij SQLException, info wordt meegegeven.
     */
    private ArrayList<Klant> voegResultSetInLijst(ResultSet resultSet) throws RSVIERException {
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
            throw new RSVIERException("KlantDAOMySQL: FOUT TIJDENS RESULTSET VOEGEN IN LIJST");
        }
    }

    // TODO: Tijdelijk om naar console te printen, aangezien later naar GUI gaat deze methode er weer uit
    public void printKlantenInConsole(ListIterator<Klant> klantenIterator) throws RSVIERException {

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

