package mysql;

import com.mysql.jdbc.Statement;
import interfaces.BestellingDAO;
//import interfaces.AdresDAO;
import interfaces.KlantDAO;
import model.Adres;
import model.Bestelling;
import model.Klant;

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
    String query = "";
    ArrayList<Klant> klantenLijst;
    BestellingDAOMySQL bestellingDAO;
//    AdresDAOMySQL adresDAO;

    /**
     * Public Constructor initialiseert de connectie met de database.
     */
    public KlantDAOMySQL() {
        connection = MySQLConnectie.getConnection();
    }

    // =================================================================================================================
    /** CREATE METHODS */

    /**
     * Maakt een nieuwe klant aan in de database met voornaam, achternaam en adresgegevens.
     * Er wordt in de database automatisch een uniek ID gegenereerd welke automatisch verhoogd wordt.
     *
     * @param voornaam De voornaam van de klant (max 50 karakters).
     * @param achternaam De achternaam van de klant (max 51 karakters).
     * @param adresgegevens De adresgegevens van de klant in een Adres object (Adres).
     */
    @Override
    public void nieuweKlant(String voornaam,
                            String achternaam,
                            Adres adresgegevens) {
        nieuweKlant(voornaam, achternaam, "", "", adresgegevens, null);
    }

    /**
     * Maakt een nieuwe klant aan in de database met voor- en achternaam.
     * Er wordt in de database automatisch een uniek ID gegenereerd welke automatisch verhoogd wordt.
     *
     * @param voornaam De voornaam van de klant (max 50 karakters).
     * @param achternaam De achternaam van de klant (max 51 karakters).
     */
    @Override
    public void nieuweKlant(String voornaam,
                            String achternaam) {
        nieuweKlant(voornaam, achternaam, new Adres("", "", "", 0, ""));
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
     */

    @Override
    public void nieuweKlant(String voornaam,
                            String achternaam,
                            String tussenvoegsel,
                            String email,
                            Adres adresgegevens,
                            Bestelling bestelGegevens) {

        ResultSet generatedKeys = null;
        try {
            query = "INSERT INTO KLANT " +
                    "(voornaam, achternaam, tussenvoegsel, email) " +
                    "VALUES " +
                    "(?,        ?,          ?,              ?);";
            statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, voornaam);
            statement.setString(2, achternaam);
            statement.setString(3, tussenvoegsel);
            statement.setString(4, email);
            statement.execute();
            generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                System.out.println(generatedKeys.getInt(1));
            }

            if (adresgegevens != null) {
//                adresDAO = new AdresDAOMySQL(); // TODO: Wachten op Douwe met AdresDAO
//                adresDAO.nieuwAdres();
            }

            if (bestelGegevens != null) {
                bestellingDAO = new BestellingDAOMySQL();
                bestellingDAO.nieuweBestelling(bestelGegevens);
            }

            System.out.println("\n\tKlantDAOMySQL: KLANT: " + voornaam + " SUCCESVOL GEMAAKT");

        } catch (SQLException ex) {
            System.out.println("\n\tKlantDAOMySQL: SQL FOUT TIJDENS AANMAKEN KLANT");
            ex.printStackTrace();
        } finally {
            MySQLHelper.close(statement, generatedKeys);
        }
    }


    // =================================================================================================================
    /** READ METHODS */

    /**
     * Deze method haalt alle klanten op uit de database en stopt ze in een ArrayList waarna, zie @return.
     *
     * @return een ListIterator wordt teruggegeven van de ArrayList met daarin Klant-objecten.
     */
    @Override
    public ListIterator<Klant> getAlleKlanten() {
        resultSet = null;

        try {
            query = "SELECT * FROM KLANT";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();

            klantenLijst = voegResultSetInLijst(resultSet);
            return klantenLijst.listIterator();

        } catch (SQLException ex) {
            System.out.println("\n\tKlantDAOMySQL: SQL FOUT TIJDEN OPHALEN KLANTEN");
            ex.printStackTrace();
        } finally {
            MySQLHelper.close(statement, resultSet);
        }
        return null;
    }

    /**
     * Deze methode haalt op basis van klantId klanten (als het goed is 1) op uit de database en geeft dit
     * terug in en ListIterator van de ArrayList.
     *
     * @param klantId Het klantId van de op te zoeken klant.
     * @return een ListIterator wordt teruggegeven van de ArrayList met daarin Klant-objecten.
     */
    @Override
    public ListIterator<Klant> getKlantOpKlant(long klantId) {
        try {
            query = "SELECT * FROM " +
                    "KLANT WHERE " +
                    "klant_id = ?;";
            statement = connection.prepareStatement(query);
            statement.setLong(1, klantId);
            resultSet = statement.executeQuery();
            klantenLijst = voegResultSetInLijst(resultSet);
            return klantenLijst.listIterator();

        } catch (SQLException ex) {
            System.out.println("\n\tKlantDAOMySQL: SQL FOUT TIJDENS OPZOEKEN KLANT OP KLANTID");
            ex.printStackTrace();
        } finally {
            MySQLHelper.close(statement, resultSet);
        }
        return null;
    }

    /**
     * Deze methode haalt op basis van de voornaam van een klant informatie uit de database en geeft dit
     * terug in en ListIterator van de ArrayList.
     *
     * @param voornaam Voornaam van de te zoeken klant(en).
     * @return een ListIterator wordt teruggegeven van de ArrayList met daarin Klant-objecten.
     */
    @Override
    public ListIterator<Klant> getKlantOpKlant(String voornaam) {
        try {
            query = "SELECT * FROM " +
                    "KLANT WHERE " +
                    "voornaam LIKE ?;";
            statement = connection.prepareStatement(query);
            statement.setString(1, voornaam);
            resultSet = statement.executeQuery();
            klantenLijst = voegResultSetInLijst(resultSet);
            return klantenLijst.listIterator();

        } catch (SQLException ex) {
            System.out.println("\n\tKlantDAOMySQL: SQL FOUT TIJDENS OPZOEKEN KLANT OP VOORNAAM");
            ex.printStackTrace();
        } finally {
            MySQLHelper.close(statement, resultSet);
        }
        return null;
    }

    /**
     * Deze methode haalt op basis van de voor- en achternaam an een klant informatie uit de database en geeft dit
     * terug in en ListIterator van de ArrayList.
     *
     * @param voornaam Voornaam van de te zoeken klant(en).
     * @param achternaam Achternaam van de te zoeken klant(en).
     * @return een ListIterator wordt teruggegeven van de ArrayList met daarin Klant-objecten.
     */
    @Override
    public ListIterator<Klant> getKlantOpKlant(String voornaam,
                                               String achternaam) {
        try {
            query = "SELECT * FROM " +
                    "KLANT WHERE " +
                    "voornaam LIKE ? AND achternaam LIKE ?;";
            statement = connection.prepareStatement(query);
            statement.setString(1, voornaam);
            statement.setString(2, achternaam);
            resultSet = statement.executeQuery();
            klantenLijst = voegResultSetInLijst(resultSet);
            return klantenLijst.listIterator();

        } catch (SQLException ex) {
            System.out.println("\n\tKlantDAOMySQL: SQL FOUT TIJDENS OPZOEKEN KLANT OP VOOR & ACHTERNAAM");
            ex.printStackTrace();
        } finally {
            MySQLHelper.close(statement, resultSet);
        }
        return null;
    }

    /**
     * Deze methode haalt op basis van adresgegevens klanten op uit de database en geeft dit
     * terug in en ListIterator van de ArrayList.
     *
     * @param adresgegevens Een Adres-object van de te zoeken klant(en).
     * @return een ListIterator wordt teruggegeven van de ArrayList met daarin Klant-objecten.
     */
    @Override
    public ListIterator<Klant> getKlantOpAdres(Adres adresgegevens) {
        try {
            query = "SELECT * FROM " +
                    "KLANT WHERE " +
                    "straatnaam LIKE ? AND " +
                    "postcode LIKE ? AND " +
                    "toevoeging LIKE ? AND " +
                    "huisnummer LIKE ? AND " +
                    "woonplaats LIKE ?;";
            statement = connection.prepareStatement(query);
            statement.setString(1, adresgegevens.getStraatnaam());
            statement.setString(2, adresgegevens.getPostcode());
            statement.setString(3, adresgegevens.getToevoeging()); //TODO Probleem met NULL oplossen
            statement.setInt(4, adresgegevens.getHuisnummer());
            statement.setString(5, adresgegevens.getWoonplaats());
            resultSet = statement.executeQuery();
            klantenLijst = voegResultSetInLijst(resultSet);
            return klantenLijst.listIterator();

        } catch (SQLException ex) {
            System.out.println("\n\tKlantDAOMySQL: SQL FOUT TIJDENS OPZOEKEN KLANT OP VOLLE ADRES");
            ex.printStackTrace();
        } finally {
            MySQLHelper.close(statement, resultSet);
        }
        return null;
    }

    /**
     * Deze methode haalt op basis van adresgegevens klanten op uit de database en geeft dit
     * terug in en ListIterator van de ArrayList.
     *
     * @param straatnaam Straatnaam van de te zoeken klant(en).
     * @return een ListIterator wordt teruggegeven van de ArrayList met daarin Klant-objecten.
     */
    @Override
    public ListIterator<Klant> getKlantOpAdres(String straatnaam) {
        try {
            query = "SELECT * FROM " +
                    "KLANT WHERE " +
                    "straatnaam LIKE ?;";
            statement = connection.prepareStatement(query);
            statement.setString(1, straatnaam);
            resultSet = statement.executeQuery();
            klantenLijst = voegResultSetInLijst(resultSet);
            return klantenLijst.listIterator();

        } catch (SQLException ex) {
            System.out.println("\n\tKlantDAOMySQL: SQL FOUT TIJDENS OPZOEKEN KLANT OP STRAATNAAM");
            ex.printStackTrace();
        } finally {
            MySQLHelper.close(statement, resultSet);
        }
        return null;
    }

    /**
     * Deze methode haalt op basis van een postcode en huisnummer klanten op uit de database en geeft dit
     * terug in en ListIterator van de ArrayList.
     *
     * @param postcode De postcode van de te zoeken klant(en).
     * @param huisnummer Het huisnummer van de te zoeken klant(en).
     * @return een ListIterator wordt teruggegeven van de ArrayList met daarin Klant-objecten.
     */
    @Override
    public ListIterator<Klant> getKlantOpAdres(String postcode,
                                               int huisnummer) {
        try {
            query = "SELECT * FROM " +
                    "KLANT WHERE " +
                    "postcode LIKE ? AND " +
                    "huisnummer LIKE ?;";
            statement = connection.prepareStatement(query);
            statement.setString(1, postcode);
            statement.setInt(2, huisnummer);
            resultSet = statement.executeQuery();
            klantenLijst = voegResultSetInLijst(resultSet);
            return klantenLijst.listIterator();

        } catch (SQLException ex) {
            System.out.println("\n\tKlantDAOMySQL: SQL FOUT TIJDENS OPZOEKEN KLANT OP POSTCODE EN HUISNUMMER");
            ex.printStackTrace();
        } finally {
            MySQLHelper.close(statement, resultSet);
        }
        return null;
    }

    /**
     * Deze methode haalt op basis van bestelId klanten op uit de database en geeft dit
     * terug in en ListIterator van de ArrayList.
     *
     * @param bestellingId Het bestelId van de te zoeken klant(en).
     * @return een ListIterator wordt teruggegeven van de ArrayList met daarin Klant-objecten.
     */
    @Override
    public ListIterator<Klant> getKlantOpBestelling(long bestellingId) {
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
            System.out.println("\n\tKlantDAOMySQL: SQL FOUT TIJDENS OPZOEKEN KLANT OP BESTELLINGID");
            ex.printStackTrace();
        } finally {
            MySQLHelper.close(statement, resultSet);
        }
        return null;
    }

    // =================================================================================================================

    /** UPDATE METHODS */

    /**
     * Methode om een klant met een bepaald klant_id zijn naamgegevens up te daten.
     *
     * @param KlantId Het klantId van de klant wiens gegevens gewijzigd dienen te worden.
     * @param voornaam De 'gewijzigde' voornaam van de klant.
     * @param achternaam De 'gewijzigde' achternaam van de klant.
     * @param tussenvoegsel Het 'gewijzigde' tussenvoegsel van de klant.
     * @param email Het gewijzigde emailadres van de klant.
     */
    @Override
    public void updateKlant(Long KlantId,
                            String voornaam,
                            String achternaam,
                            String tussenvoegsel,
                            String email) {
        try {
            query = "UPDATE KLANT " +
                    "SET " +
                    "voornaam = ?, " +
                    "achternaam = ?, " +
                    "tussenvoegsel = ?, " +
                    "email = ? " +
                    "WHERE " +
                    "klant_id = ?;";
            statement = connection.prepareStatement(query);
            statement.setString(1, voornaam);
            statement.setString(2, achternaam);
            statement.setString(3, tussenvoegsel);
            statement.setString(4, email);
            statement.setLong(5, KlantId);
            statement.execute();

        } catch (SQLException ex) {
            System.out.println("\n\tKlantDAOMySQL: SQL FOUT TIJDENS UPDATEN KLANT");
            ex.printStackTrace();
        } finally {
            MySQLHelper.close(statement);
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
     */
    @Override
    public void updateKlant(Long KlantId, String voornaam,
                            String achternaam,
                            String tussenvoegsel,
                            String email,
                            Adres adresgegevens) {
        updateKlant(KlantId, voornaam, achternaam, tussenvoegsel, email);
        // TODO, UPDATE METHOD VAN ADRESGEGEVENS TOEVOEGEN VAN DOUWE

    }

    // =================================================================================================================

    /** DELETE METHODS */

    /**
     * Methode om een klant te verwijderen op basis van ID. Alle bestellingen van de klant worden
     * tevens ook verwijderd.
     *
     * @param klantId Klant_id van de te verwijderen klant.
     */
    @Override
    public void verwijderKlant(long klantId) {
        try {
            bestellingDAO = new BestellingDAOMySQL();
            bestellingDAO.verwijderAlleBestellingenKlant(klantId);

            query = "DELETE FROM " +
                    "KLANT " +
                    "WHERE " +
                    "klant_id = ?;";
            statement = connection.prepareStatement(query);
            statement.setLong(1, klantId);
            statement.execute();

        } catch (SQLException ex) {
            System.out.println("\n\tKlantDAOMySQL: SQL FOUT TIJDENS VERWIJDEREN KLANT OP ID");
            ex.printStackTrace();
        } finally {
            MySQLHelper.close(statement);
        }
    }

    /**
     * Methode om een klant te verwijderen op basis van naamgegevens. Alle bestellingen van de klant worden
     * tevens ook verwijderd.
     *
     * @param voornaam De voornaam van de te verwijderen klant.
     * @param achternaam De achternaam van de te verwijderen klant.
     * @param tussenvoegsel Het tussenvoegsel van de te verwijderen klant.
     */
    @Override
    public void verwijderKlant(String voornaam,
                               String achternaam,
                               String tussenvoegsel) {
        try {
            query = "SELECT klant_id FROM " +
                    "KLANT " +
                    "WHERE " +
                    "voornaam LIKE ? AND " +
                    "achternaam LIKE ? AND " +
                    "tussenvoegsel LIKE ?;";
            statement = connection.prepareStatement(query);
            statement.setString(1, voornaam);
            statement.setString(2, achternaam);
            statement.setString(3, tussenvoegsel);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                verwijderKlant(resultSet.getInt(1));
            }

        } catch (SQLException ex) {
            System.out.println("\n\tKlantDAOMySQL: SQL FOUT TIJDENS VERWIJDEREN KLANT OP VOORNAAM, ACHTERNAAM & " +
                    "TUSSENVOEGSEL");
            ex.printStackTrace();
        } finally {
            MySQLHelper.close(statement, resultSet);
        }
    }


    /**
     * Methode om een klant te verwijderen op basis van een bestelnummer.
     *
     * @param bestellingId Bestel-ID van de te verwijderen klant.
     */
    @Override
    public void verwijderKlantOpBestellingId(long bestellingId) {
        ListIterator<Klant> klantenIterator = getKlantOpBestelling(bestellingId);

        if (klantenIterator != null) {
            while (klantenIterator.hasNext()) {
                Klant tijdelijkeKlant = klantenIterator.next();
                verwijderKlant(tijdelijkeKlant.getKlant_id());
            }
        }
    }

    // =================================================================================================================

    /** ANDERE METHODS */

    /**
     * Methode om consistent een resultset van een klant-rij in een ArrayList te kunnen vormen.
     *
     * @param resultSet De resultset met klantrijen.
     * @return Een ArrayList met Klant objecten.
     */
    private ArrayList<Klant> voegResultSetInLijst(ResultSet resultSet) {
        try {
            klantenLijst = new ArrayList<>();
            int klantenTeller = 0;
            while (resultSet.next()) {
                Klant tijdelijkeKlant = new Klant();
                tijdelijkeKlant.setKlant_id(resultSet.getLong(1));
                tijdelijkeKlant.setVoornaam(resultSet.getString(2));
                tijdelijkeKlant.setAchternaam(resultSet.getString(3));
                tijdelijkeKlant.setTussenvoegsel(resultSet.getString(4));
                tijdelijkeKlant.setEmail(resultSet.getString(5));
                tijdelijkeKlant.getAdresGegevens().setStraatnaam(resultSet.getString(6));
                tijdelijkeKlant.getAdresGegevens().setPostcode(resultSet.getString(7));
                tijdelijkeKlant.getAdresGegevens().setToevoeging(resultSet.getString(8));
                tijdelijkeKlant.getAdresGegevens().setHuisnummer(resultSet.getInt(9));
                tijdelijkeKlant.getAdresGegevens().setWoonplaats(resultSet.getString(10));
                klantenLijst.add(klantenTeller, tijdelijkeKlant);
                klantenTeller++; }

            return klantenLijst;
        } catch (SQLException ex) {
            System.out.println("\n\tKlantDAOMySQL: FOUT TIJDENS RESULTSET VOEGEN IN LIJST");
        }
        return null;
    }

    // TODO: Tijdelijk om naar console te printen, aangezien later naar GUI gaat
    public void printKlantenInConsole(ListIterator<Klant> klantenIterator) {

        while (klantenIterator.hasNext()) {
            Klant tijdelijkeKlant = klantenIterator.next();
            Adres tijdelijkAdres = tijdelijkeKlant.getAdresGegevens();
            System.out.println("\n\t----------------------------------------------------");
            System.out.print("\n\tKLANTID:           " + tijdelijkeKlant.getKlant_id());
            System.out.print("\n\tVoornaam:          " + tijdelijkeKlant.getVoornaam());
            System.out.print("\n\tAchternaam:        " + tijdelijkeKlant.getAchternaam());
            System.out.print("\n\tTussenvoegsel:     " + tijdelijkeKlant.getTussenvoegsel());
            System.out.print("\n\tE-Mail:            " + tijdelijkeKlant.getEmail());
            System.out.print("\n\tStraatnaam:        " + tijdelijkAdres.getStraatnaam());
            System.out.print("\n\tPostcode:          " + tijdelijkAdres.getPostcode());
            System.out.print("\n\tToevoeging:        " + tijdelijkAdres.getToevoeging());
            System.out.print("\n\tHuisnummer:        " + tijdelijkAdres.getHuisnummer());
            System.out.print("\n\tWoonplaats:        " + tijdelijkAdres.getWoonplaats());
        }
        System.out.println("\n\n");
    }
}

