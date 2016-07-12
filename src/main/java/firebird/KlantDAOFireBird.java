package firebird;

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
 * Het verzorgt de database-operaties tussen FireBird en de objecten. <p>
 *
 * De DAO is opgezet in CRUD volgorde (Create, Read, Update, Delete)<p>
 *
 * Zie de afzonderlijke methods en constructor voor commentaar.
 */

public class KlantDAOFireBird extends AbstractDAOFireBird implements KlantDAO {

    // GLOBALE VARIABELEN
    String query = "";
    ArrayList<Klant> klantenLijst;
    BestellingDAOFireBird bestellingDAO;
    AdresDAOFireBird adresDAO;

    /** CREATE METHODS */

    /**
     * [HOOFD NIEUWEKLANTMETHODE]
     * Maakt een nieuwe klant aan in de database met alle naamgegevens.
     * Als er adres en/of bestelgegevens aanwezig zijn worden deze tevens ook toegevoegd.
     * Er wordt in de database automatisch een uniek ID gegenereerd welke automatisch verhoogd wordt.
     * Het is mogelijk door middel van een adresId mee te geven geen nieuw adres aan te maken maar
     * deze te koppelen aan de klant.
     *
     //     * @param voornaam De voornaam van de klant (max 50 karakters).
     //     * @param achternaam De achternaam van de klant (max 51 karakters).
     //     * @param tussenvoegsel Tussenvoegsel van de klant (max 10 karakters).
     //     * @param email Emailadres van de klant (max 80 karakters).
     * @param adresgegevens Adresgegevens van de klant in een Klant object (zie Klant).
     * @param bestelGegevens Bestelgegevens van de klant in een Bestel object (zie Bestelling).
     * @throws GeneriekeFoutmelding Foutmelding bij SQLException, info wordt meegegeven.
     */

    @Override
    public long nieuweKlant(Klant nieuweKlant,
                            long adresId,
                            Adres adresgegevens,
                            Bestelling bestelGegevens) throws GeneriekeFoutmelding {

        query = queryGenerator.buildInsertStatement(nieuweKlant) + " RETURNING klantId;";

        try (
                Connection connection = connPool.verkrijgConnectie();
                PreparedStatement statement = connection.prepareStatement(query);
        ) {
//            statement.execute();

            // Ophalen van de laatste genegeneerde sleutel (de nieuwe klantId)
            long nieuwId = 0;
            try (
                    ResultSet resultSet = statement.executeQuery();
            ) {
                while (resultSet.next()) {
                    nieuwId = resultSet.getInt("klantId");
                }

                // Als er een adresId wordt meegegeven betekent dit dat er een bestaand adres gekoppeled wordt
                // aan een nieuwe klant
                if (adresId > 0 && adresgegevens == null) {
                    adresDAO = new AdresDAOFireBird();
                    adresDAO.koppelAdresAanKlant(nieuwId, adresId);
                }

                // Als er adresgegeven worden meegegeven wordt er een adres aangemaakt op basis van het nieuwe klantId
                else if (adresgegevens != null && adresId == 0) {
                    adresDAO = new AdresDAOFireBird();
                    adresDAO.nieuwAdres(nieuwId, adresgegevens);
                }

                // Als er adresgegeven worden meegegeven en een adresId wordt er zowel een nieuw adres aangemaakt
                // en tevens het bestaande adres gekoppeld.
                else if (adresgegevens != null && adresId > 0) {
                    adresDAO = new AdresDAOFireBird();
                    adresDAO.nieuwAdres(nieuwId, adresgegevens);
                    adresDAO.koppelAdresAanKlant(nieuwId, adresId);
                }

                // Als er bestegegevens zijn meegegeven worden deze bijgevoegd
                if (bestelGegevens != null) {
                    bestellingDAO = new BestellingDAOFireBird();
                    bestelGegevens.setKlantId(nieuwId);
                    bestellingDAO.nieuweBestelling(bestelGegevens);
                }

            }
            return nieuwId;

        } catch (SQLException ex) {
            if (ex.getMessage().contains("Duplicate entry")) { // TODO: Is anders in FB
                DeLogger.getLogger().warn("KlantDAOFireBird: DEZE KLANT BESTAAT AL IN DE DATABASE MET ID: " +
//                        getKlantID(voornaam, achternaam, email));
                        "");
                throw new GeneriekeFoutmelding("KlantDAOFireBird: DEZE KLANT BESTAAT AL IN DE DATABASE MET ID: " +
//                        getKlantID(voornaam, achternaam, email));
                        "");
            }
            else {
                DeLogger.getLogger().error("SQL FOUT TIJDENS AANMAKEN KLANT: " + ex.getMessage());
                throw new GeneriekeFoutmelding("KlantDAOFireBird: SQL FOUT TIJDENS AANMAKEN KLANT: " + ex.getMessage());
            }
        }
    }

    /**
     * Deze methode kan een Klant-object ontvangen en maakt op basis daarvan een nieuwe
     * klant aan in de database. Adres-object en bestelling-object mogen null zijn.
     * Zie verder de overloaded nieuweKlant methods.
     *
     * Als een bestaand adres gekoppeld dient te worden kan er een adresId worden meegegeven.
     * Er wordt dan geen nieuw adres meer aangemaakt.
     *
     * @param nieuweKlant Klantobject van de klant die gemaakt dient te worden.
     * @param adresId Er kan een adresId worden meegegeven om een bestaand adres te koppelen.
     * @return klantId van de nieuwe klant.
     * @throws GeneriekeFoutmelding
     */
    @Override
    public long nieuweKlant(Klant nieuweKlant, long adresId) throws GeneriekeFoutmelding {

        Adres tijdelijkAdres = null;

        // Als er een adres wordt meegegeven in de klant wordt deze als los adres meegegeven
        // aan de main method en in het model weer op null gezet zodat de query goed
        // gemaakt kan worden
        if (nieuweKlant.getAdresGegevens() != null) {
            tijdelijkAdres = nieuweKlant.getAdresGegevens();
            nieuweKlant.setAdresGegevens(null);
        }

        return nieuweKlant(nieuweKlant, adresId, tijdelijkAdres, null);
    }

    /** READ METHODS */

    /**
     * Zoekt het klantId op van de klant.
     * De uniekheid van een klant is op basis van voornaam, achternaam en email, hier kan er dus maar 1 van bestaan.
     *
     * @param voornaam De te zoeken voornaam
     * @param achternaam De te zoeken achternaam
     * @param email De te zoeken email van de klant
     * @return Het klantId van de klant
     */
    @Override
    public long getKlantID(String voornaam, String achternaam, String email) throws GeneriekeFoutmelding {
        String query = "SELECT klantId " +
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
                    throw new GeneriekeFoutmelding("KlantDAOFireBird: KLANT NIET GEVONDEN");
                }
                else {
                    return rs.getLong(1); // Door if-statement is rs al bij next()
                }
            }
        } catch (SQLException ex) {
            DeLogger.getLogger().error("SQL FOUT TIJDENS OPZOEKEN KLANT ID: " + ex.getMessage());
            throw new GeneriekeFoutmelding("KlantDAOFireBird: SQL FOUT TIJDENS OPZOEKEN KLANT ID: " + ex.getMessage());
        }
    }

    @Override
    public ListIterator<Klant> getAlleKlanten() throws GeneriekeFoutmelding {
        return getKlantOpKlant(new Klant());
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
            throw new GeneriekeFoutmelding("KlantDAOFireBird: SQL FOUT TIJDENS OPZOEKEN KLANT " + ex.getMessage());
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
                    ResultSet resultSet = statement.executeQuery(); )
            {
                klantenLijst = voegResultSetInLijst(resultSet);
                return klantenLijst.listIterator();
            }

        } catch (SQLException ex) {
            DeLogger.getLogger().error("SQL FOUT TIJDENS OPZOEKEN KLANT OP VOLLE ADRES: " + ex.getMessage());
            throw new GeneriekeFoutmelding("KlantDAOFireBird: SQL FOUT TIJDENS OPZOEKEN KLANT OP VOLLE ADRES: " + ex.getMessage());
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
        String query = "SELECT klantId FROM " +
                "BESTELLING WHERE " +
                "bestellingId = ? " +
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
                    tijdelijkeKlant.setKlantId((long)resultSet.getInt(1));
                    return getKlantOpKlant(tijdelijkeKlant);
                }
            }
        } catch (SQLException ex) {
            DeLogger.getLogger().error("SQL FOUT TIJDENS OPZOEKEN KLANT OP BESTELLINGID: " + ex.getMessage());
            throw new GeneriekeFoutmelding("KlantDAOFireBird: SQL FOUT TIJDENS OPZOEKEN KLANT OP BESTELLINGID: " +
                    ex.getMessage());
        }
        return null;
    }

    /** UPDATE METHODS */

    /**
     * Methode om een klant met een bepaald klantId zijn naamgegevens up te daten.
     *
     * @param nieuweKlant De te updaten klant in Klant-object
     * @throws GeneriekeFoutmelding Foutmelding bij SQLException, info wordt meegegeven.
     */
    @Override
    public void updateKlant(Klant nieuweKlant) throws GeneriekeFoutmelding {
        // Check of er een adres is meegegeven, deze wordt afzonderlijk door de adresDAO
        // behandeld en daarna op null gezet anders neemt de querygenerator deze foutief mee.
        if (nieuweKlant.getAdresGegevens() != null) {
            adresDAO = new AdresDAOFireBird();
            adresDAO.updateAdres(nieuweKlant.getAdresGegevens().getAdresId(), nieuweKlant.getAdresGegevens());
            nieuweKlant.setAdresGegevens(null);
        }

        query = queryGenerator.buildUpdateStatement(nieuweKlant) + " klantId = " + nieuweKlant.getKlantId() + ";";

        try (
                Connection connection = connPool.verkrijgConnectie();
                PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.execute();

        } catch (SQLException ex) {
            DeLogger.getLogger().error("SQL FOUT TIJDENS UPDATEN KLANT: " + ex.getMessage());
            throw new GeneriekeFoutmelding("KlantDAOFireBird: SQL FOUT TIJDENS UPDATEN KLANT: " + ex.getMessage());
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

    /** DELETE METHODS */

    /**
     * Methode om een klant te verwijderen op basis van ID. Alle bestellingen van de klant worden
     * tevens ook op non-actief gezet.
     *
     * @param klantId KlantId van de te verwijderen klant.
     * @throws GeneriekeFoutmelding Foutmelding bij SQLException, info wordt meegegeven.
     */

    @Override
    public void schakelStatusKlant(long klantId, int status) throws GeneriekeFoutmelding {
        String query =
                "UPDATE KLANT " +
                        "SET " +
                        "klantActief = ? " +
                        "WHERE " +
                        "klantId = ?";

        long verwijderdID = -1;

        try (
                Connection connection = connPool.verkrijgConnectie();
                PreparedStatement statement = connection.prepareStatement(query);

        ) {
            bestellingDAO = new BestellingDAOFireBird();
            bestellingDAO.verwijderAlleBestellingenKlant(klantId);
            statement.setInt(1, status);
            statement.setLong(2, klantId);
            statement.execute();

        } catch (SQLException ex) {
            DeLogger.getLogger().error("SQL FOUT TIJDENS KLANT OP ID INACTIEF ZETTEN: " + ex.getMessage());
            throw new GeneriekeFoutmelding("KlantDAOFireBird: SQL FOUT TIJDENS KLANT OP ID INACTIEF ZETTEN: " + ex.getMessage());
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
            schakelStatusKlant(tijdelijkeKlant.getKlantId(),
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
                schakelStatusKlant(tijdelijkeKlant.getKlantId(), 0);
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

                tijdelijkeKlant.setKlantId(resultSet.getLong(1));
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
            throw new GeneriekeFoutmelding("KlantDAOFireBird: FOUT TIJDENS RESULTSET VOEGEN IN LIJST: " + ex.getMessage());
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
                System.out.println("\n\n\t------------------KLANT " + tijdelijkeKlant.getKlantId() + " BEGIN---------------------------");
                System.out.print("\n\tKLANTID:           " + tijdelijkeKlant.getKlantId());
                System.out.print("\n\tVoornaam:          " + tijdelijkeKlant.getVoornaam());
                System.out.print("\n\tAchternaam:        " + tijdelijkeKlant.getAchternaam());
                System.out.print("\n\tTussenvoegsel:     " + tijdelijkeKlant.getTussenvoegsel());
                System.out.print("\n\tE-Mail:            " + tijdelijkeKlant.getEmail());
                System.out.print("\n\tDatum Aangemaakt:  " + tijdelijkeKlant.getDatumAanmaak());
                System.out.print("\n\tDatum Gewijzigd:   " + tijdelijkeKlant.getDatumGewijzigd());

                System.out.print("\n");
                System.out.print("\n\tADRES(SEN)");
                System.out.print("\n\t----------");

                // DAO voor adres-acties. Lijst verkrijgen van alle adressen bijbehorend bij klantId
                adresDAO = new AdresDAOFireBird();
                ListIterator<Adres> adresListIterator = adresDAO.getAdresOpKlantID(tijdelijkeKlant.getKlantId());
                while (adresListIterator.hasNext()) {

                    Adres tijdelijkAdres = adresListIterator.next();

                    if (tijdelijkAdres.getAdresActief().charAt(0) == '1') {
                        System.out.print("\n\t\t                   ");
                        System.out.print("\n\t\tADRESID:           " + tijdelijkAdres.getAdresId());
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
                        System.out.println("\n\t\tADRESID: " + tijdelijkAdres.getAdresId() + " INACTIEF");
                    }
                }
                System.out.println("\n\n\t------------------KLANT " + tijdelijkeKlant.getKlantId() + " EIND----------------------------");

            }
            else {
                System.out.println("\n\t------------------KLANT " + tijdelijkeKlant.getKlantId() + " INACTIEF------------------------");
            }
        }
        System.out.println("\n");
    }
}

