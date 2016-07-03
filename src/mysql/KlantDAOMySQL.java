package mysql;

import com.mysql.jdbc.Statement;
import exceptions.GeneriekeFoutmelding;
import interfaces.KlantDAO;
import logger.DeLogger;
import model.Adres;
import model.Bestelling;
import model.Klant;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Created by Milan_Verheij on 06-06-16.
 * <p>
 * KlantDAOMySQL is de DAO van de Klant POJO. <p>
 * Het verzorgt de database-operaties tussen MySQL en de objecten. <p>
 * <p>
 * De DAO is opgezet in CRUD volgorde (Create, Read, Update, Delete)<p>
 * <p>
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
     * @param nieuweKlant Nieuwe klantgegevens in een Klant-object.
     * @param adresgegevens Adresgegevens van de klant in een Klant object (zie Klant).
     * @param bestelGegevens Bestelgegevens van de klant in een Bestel object (zie Bestelling).
     * @throws GeneriekeFoutmelding Foutmelding bij SQLException, info wordt meegegeven.
     */
    @Override
    public long nieuweKlant(Klant nieuweKlant,
                            long adres_id,
                            Adres adresgegevens,
                            Bestelling bestelGegevens) throws GeneriekeFoutmelding {

        // Als er geen klant wordt meegegeven wordt een fout gegooid.
        if (nieuweKlant == null) {
            DeLogger.getLogger().warn("KAN GEEN KLANT AANMAKEN MET NULL OBJECT");
            throw new GeneriekeFoutmelding("KlantDAOMySQL: KAN GEEN KLANT AANMAKEN MET NULL OBJECT");
        }

        // Een klant aanmaken kan enkel met een voornaam en en achternaam, niet minder
        if (nieuweKlant.getVoornaam().trim().length() == 0 || nieuweKlant.getAchternaam().trim().length() == 0) {
            DeLogger.getLogger().warn(("KAN GEEN KLANT MAKEN ZONDER VOOR EN ACHTERNAAM"));
            throw new GeneriekeFoutmelding("KlantDAOMySQL: KAN GEEN KLANT MAKEN ZONDER VOOR EN ACHTERNAAM");
        }

        // Bouw de query
        query = queryGenerator.buildInsertStatement(nieuweKlant);

        try (
                Connection connection = connPool.verkrijgConnectie();
                PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        ) {
            // Voer query uit en haal de gegenereerde sleutels op bij deze query
            statement.execute();

            // Ophalen van de laatste genegeneerde sleutel uit de generatedkeys (de nieuwe klant_id)
            long nieuwId = 0;
            try (
                    ResultSet generatedKeys = statement.getGeneratedKeys();
            ) {
                if (generatedKeys.next())
                    nieuwId = generatedKeys.getInt(1);

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
                        getKlantID(nieuweKlant.getVoornaam(), nieuweKlant.getAchternaam(), nieuweKlant.getEmail()));
                throw new GeneriekeFoutmelding("KlantDAOMySQL: DEZE KLANT BESTAAT AL IN DE DATABASE MET ID: " +
                        getKlantID(nieuweKlant.getVoornaam(), nieuweKlant.getAchternaam(), nieuweKlant.getEmail()));
            } else {
                DeLogger.getLogger().error("SQL FOUT TIJDENS AANMAKEN KLANT: " + ex.getMessage());
                throw new GeneriekeFoutmelding("KlantDAOMySQL: SQL FOUT TIJDENS AANMAKEN KLANT: " + ex.getMessage());
            }
        }
    }

    /**
     * Maakt een nieuwe klant aan met enkel een Adres Object en een mogelijk adres-id. Als geen
     * adres-id wordt meegegeven wordt geen adres gekopeld.
     *
     * @param nieuweKlant De gegevens van de nieuwe klant in een Klant-object
     * @param adres_id Het adres-id van een mogelijk te koppelen adres.
     * @return Het nieuwe klant-id wordt teruggegeven.
     * @throws GeneriekeFoutmelding Foutmelding bij SQLException, info wordt meegegeven.
     */
    @Override
    public long nieuweKlant(Klant nieuweKlant, long adres_id) throws GeneriekeFoutmelding {
        return nieuweKlant(nieuweKlant, adres_id, null, null);
    }

    /** READ METHODS */

    /**
     * Zoekt het klant_id op van de klant.
     * De uniekheid van een klant is op basis van voornaam, achternaam en email, hier kan er dus maar 1 van bestaan.
     *
     * @param voornaam   De te zoeken voornaam
     * @param achternaam De te zoeken achternaam
     * @param email      De te zoeken email van de klant
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
            ) {
                // Als er een resultaat gevonden is bestaat de klant niet en wordt er een foutmelding gegooid.
                if (!rs.next()) {
                    DeLogger.getLogger().warn("KLANT NIET GEVONDEN: " + voornaam + "/" + achternaam + "/" + email);
                    throw new GeneriekeFoutmelding("KlantDAOMySQL: KLANT NIET GEVONDEN");
                } else {
                    return rs.getLong(1); // Door if-statement is rs al bij next()
                }
            }
        } catch (SQLException ex) {
            DeLogger.getLogger().error("SQL FOUT TIJDENS OPZOEKEN KLANT ID: " + ex.getMessage());
            throw new GeneriekeFoutmelding("KlantDAOMySQL: SQL FOUT TIJDENS OPZOEKEN KLANT ID: " + ex.getMessage());
        }
    }


    /**
     * Deze method haalt klanten op uit de database op basis van een meegegeven Klant-Object.
     *
     * @param klant Klant-object gevuld met zoek-parameters.
     * @return een ListIterator wordt teruggegeven van de ArrayList met daarin Klant-objecten.
     * @throws GeneriekeFoutmelding Foutmelding bij SQLException, info wordt meegegeven.
     */
    @Override
    public ListIterator<Klant> getKlantOpKlant(Klant klant) throws GeneriekeFoutmelding {
        query = queryGenerator.buildSelectStatement(klant);

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
            DeLogger.getLogger().error("SQL FOUT TIJDENS OPZOEKEN KLANT " + ex.getMessage());
            throw new GeneriekeFoutmelding("KlantDAOMySQL: SQL FOUT TIJDENS OPZOEKEN KLANT " + ex.getMessage());
        }
    }

    /**
     * Deze methode haalt op basis van adresgegevens klanten op uit de database en geeft dit
     * terug in en ListIterator van de ArrayList.
     *
     * @param adresgegevens Een Adres-object van de te zoeken klant(en).
     * @return een ListIterator wordt teruggegeven van de ArrayList met daarin Klant-objecten.
     * @throws GeneriekeFoutmelding Foutmelding bij SQLException, info wordt meegegeven.
     */
    @Override
    public ListIterator<Klant> getKlantOpAdres(Adres adresgegevens) throws GeneriekeFoutmelding {

        // Om een query te laten maken door de querygenerator die naar klanten zoekt op basis van een adres
        // dient er een klant object gegeven te worden met een _notnull_ adresgegevens property.
        Klant klantAdres = new Klant();
        klantAdres.setAdresGegevens(adresgegevens);
        query = queryGenerator.buildSelectStatement(klantAdres);

        try (
                Connection connection = connPool.verkrijgConnectie();
                PreparedStatement statement = connection.prepareStatement(query);
        ) {
            try (
                    ResultSet resultSet = statement.executeQuery();) {
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
                    Klant tijdelijkeKlant = new Klant();
                    tijdelijkeKlant.setKlant_id((long)resultSet.getInt(1));
                    return getKlantOpKlant(tijdelijkeKlant);
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
     * @param nieuweKlant De te updaten klant in Klant-object
     * @throws GeneriekeFoutmelding Foutmelding bij SQLException, info wordt meegegeven.
     */
    @Override
    public void updateKlant(Klant nieuweKlant) throws GeneriekeFoutmelding {
        // Check of er een adres is meegegeven, deze wordt afzonderlijk door de adresDAO
        // behandeld en daarna op null gezet anders neemt de querygenerator deze foutief mee.
        if (nieuweKlant.getAdresGegevens() != null) {
            adresDAO = new AdresDAOMySQL();
            adresDAO.updateAdres(nieuweKlant.getAdresGegevens().getAdres_id(), nieuweKlant.getAdresGegevens());
            nieuweKlant.setAdresGegevens(null);
        }

        query = queryGenerator.buildUpdateStatement(nieuweKlant) + " klant_id = " + nieuweKlant.getKlant_id() + ";";

        try (
                Connection connection = connPool.verkrijgConnectie();
                PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.execute();

        } catch (SQLException ex) {
            DeLogger.getLogger().error("SQL FOUT TIJDENS UPDATEN KLANT: " + ex.getMessage());
            throw new GeneriekeFoutmelding("KlantDAOMySQL: SQL FOUT TIJDENS UPDATEN KLANT: " + ex.getMessage());
        }
    }

    /**
     * Methode om een klant te updaten met een mogelijk los adres-object.
     *
     * @param nieuweKlant De te updaten klant in Klant-object
     * @param adresgegevens De 'gewijzigde' adresgegevens van de klant in Klantobject.
     * @throws GeneriekeFoutmelding Foutmelding bij SQLException, info wordt meegegeven.
     */
    @Override
    public void updateKlant(Klant nieuweKlant,
                            Adres adresgegevens) throws GeneriekeFoutmelding {
        if (adresgegevens != null)
            nieuweKlant.setAdresGegevens(adresgegevens);

        updateKlant(nieuweKlant);
    }

    /** 'DELETE' METHODS */
    /** In feite worden klanten niet verwijderd maar op non-actief gezet */

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

    /**
     * Methode om een klant zijn/haar status te switchen op basis van klant-object.
     *
     * @param klant Klant-gegevens in klant-object
     * @throws GeneriekeFoutmelding Foutmelding bij SQLException, info wordt meegegeven.
     */
    @Override
    public void schakelStatusKlant(Klant klant) throws GeneriekeFoutmelding {

        ListIterator<Klant> klantListIterator = getKlantOpKlant(klant);

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
                Klant tijdelijkeKlant = new Klant(0, "", "", "", "", new Adres());

                tijdelijkeKlant.setKlant_id(resultSet.getLong(1));
                tijdelijkeKlant.setVoornaam(resultSet.getString(2));
                tijdelijkeKlant.setAchternaam(resultSet.getString(3));
                tijdelijkeKlant.setTussenvoegsel(resultSet.getString(4));
                tijdelijkeKlant.setEmail(resultSet.getString(5));
                tijdelijkeKlant.setDatumAanmaak(resultSet.getString(6));
                tijdelijkeKlant.setDatumGewijzigd(resultSet.getString(7));
                tijdelijkeKlant.setKlantActief(resultSet.getString(8));
                klantenLijst.add(klantenTeller, tijdelijkeKlant);
                klantenTeller++;
            }

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
                    } else {
                        System.out.println("\n\t\tADRESID: " + tijdelijkAdres.getAdres_id() + " INACTIEF");
                    }
                }
                System.out.println("\n\n\t------------------KLANT " + tijdelijkeKlant.getKlant_id() + " EIND----------------------------");

            } else {
                System.out.println("\n\t------------------KLANT " + tijdelijkeKlant.getKlant_id() + " INACTIEF------------------------");
            }
        }
        System.out.println("\n");
    }



}

