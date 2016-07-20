package database.daos.firebird;

import exceptions.GeneriekeFoutmelding;
import logger.DeLogger;
import model.Adres;

import java.sql.*;
import java.util.ArrayList;
import java.util.ListIterator;

import database.interfaces.AdresDAO;

/**
 * Created by Milan_Verheij on 08-06-16.
 *
 * Dit is de AdresDAO die de connectie regelt tussen de FireBird database
 * en de CRUD acties verzorgt.
 *
 */

public class AdresDAOFireBird extends AbstractDAOFireBird implements AdresDAO {
    // public om test toegang te laten hebben, heeft verder geen impact op functionaliteit derhalve geen
    // veiligheidsrisico. Standaard wordt een sowieso fout adres meegegeven. Enkel als deze gewijzigd wordt
    // door middel van de klantWordtGetet 'schakelaar' kan deze het juiste adres of null aannemen.
    public static boolean klantWordtGetest = false;
    public static Adres aangeroepenAdresInTest = new Adres("XXXXXX", "XXXX", "XX", 0000, "XXXX"); // Geeft altijd FOUT
    private ArrayList<Adres> adresLijst;


    /**
     * Update een adres bij een klant op basis van een Adres-object en adresId.
     *
     * @param adresId Het adresId om up te daten.
     * @param adresgegevens De adresgegevens om te updaten in Adres object formaat
     * @throws GeneriekeFoutmelding Als er een fout is wordt deze doorgestuurd naar de GeneriekeFoutmelding met de message van
     * de exception.
     */
    @Override
    public void updateAdres(long adresId, Adres adresgegevens) throws GeneriekeFoutmelding {

        // Als er null wordt meegegeven als Adres wordt er een standaard leeg-adres geschrevne.
        if (adresgegevens == null) {
            adresgegevens = new Adres();
        }

        if (klantWordtGetest)
            aangeroepenAdresInTest = adresgegevens;

        String query = "UPDATE ADRES " +
                "SET " +
                "straatnaam = ?, " +
                "postcode = ?, " +
                "toevoeging = ?, " +
                "huisnummer = ?, " +
                "woonplaats = ?, " +
                "datumGewijzigd = CURRENT_TIMESTAMP " +
                "WHERE adres_id = ?;";

        try (
                Connection connection = connPool.verkrijgConnectie();
                PreparedStatement statement= connection.prepareStatement(query)
        ) {
            statement.setString(1, adresgegevens.getStraatnaam());
            statement.setString(2, adresgegevens.getPostcode());
            statement.setString(3, adresgegevens.getToevoeging());
            statement.setInt(4, adresgegevens.getHuisnummer());
            statement.setString(5, adresgegevens.getWoonplaats());
            statement.setLong(6, adresId);
            statement.execute();

        }  catch (SQLException ex) {
            DeLogger.getLogger().error("FOUT TIJDENS UPDATEN VAN EEN ADRES: " + ex.getMessage());
            throw new GeneriekeFoutmelding("AdresDAOFireBird: FOUT TIJDENS UPDATEN VAN EEN ADRES: " + ex.getMessage());
        }
    }

    /**
     * Koppelt een bestaand adres aan een klant.
     *
     * @param klantId Het klantId waaraan een adres gekoppeld dient te worden
     * @param adresId Het adresId van het te koppelen adres.
     * @throws GeneriekeFoutmelding Als er een fout is wordt deze doorgestuurd naar de GeneriekeFoutmelding met de message van
     * de exception.
     */
    @Override
    public void koppelAdresAanKlant(long klantId, long adresId) throws GeneriekeFoutmelding {
        String query = "INSERT INTO " +
                "KLANT_HEEFT_ADRES " +
                "(klant_id_klant, adres_id_adres) " +
                "VALUES " +
                "(?,              ?);";

        try (
                Connection connection = connPool.verkrijgConnectie();
                PreparedStatement statement = connection.prepareStatement(query);
        )
        {
            statement.setLong(1, klantId);
            statement.setLong(2, adresId);
            statement.execute();

        } catch (SQLException ex) {
            if (ex.getMessage().contains("Duplicate entry")) {
                DeLogger.getLogger().warn("ADRES IS REEDS GEKOPPELD AAN DEZE KLANT, klantid: " + klantId +
                        "adresid: " + adresId);
                throw new GeneriekeFoutmelding("AdresDAOFireBird: DIT ADRES IS REEDS GEKOPPELD AAN DEZE KLANT");
            }
            else {
                DeLogger.getLogger().error("SQL FOUT TIJDENS KOPPELEN PERSOON AAN BESTAAND ADRES: " +
                        ex.getMessage());
                throw new GeneriekeFoutmelding("AdresDAOFireBird: SQL FOUT TIJDENS KOPPELEN PERSOON AAN BESTAAND ADRES: " +
                        ex.getMessage());
            }
        }
    }

    /**
     * Maakt een nieuw adres aan en koppelt deze aan de klant.
     *
     * @param klantId KlantId behorende bij het adres.
     * @param adresgegevens De adresgegevens die nieuw in de database dienen te worden opgenomen.
     * @return Het adresId van het nieuw aangemaakte adres.
     * @throws GeneriekeFoutmelding Als er een fout is wordt deze doorgestuurd naar de GeneriekeFoutmelding met de message van
     * de exception.
     */
    @Override
    public long nieuwAdres(long klantId, Adres adresgegevens) throws GeneriekeFoutmelding {
        String queryNieuwAdres = "INSERT INTO ADRES " +
                "(straatnaam, postcode, toevoeging, huisnummer, woonplaats) " +
                "VALUES " +
                "(?,        ?,          ?,          ?,          ?) " +
                "RETURNING adres_id;";

        String queryAdresKlantKoppeling = "INSERT INTO KLANT_HEEFT_ADRES " +
                "(klant_id_klant, adres_id_adres) " +
                "VALUES " +
                "(?,        ?);";

        try (
                Connection connection = connPool.verkrijgConnectie();
                PreparedStatement statementNieuwAdres = connection.prepareStatement(queryNieuwAdres);
                PreparedStatement statementAdresKlantKoppeling = connection.prepareStatement(queryAdresKlantKoppeling);
        )
        {
            // Voer query uit en haal de gegenereerde sleutels op bij deze query
            statementNieuwAdres.setString(1, adresgegevens.getStraatnaam());
            statementNieuwAdres.setString(2, adresgegevens.getPostcode());
            statementNieuwAdres.setString(3, adresgegevens.getToevoeging());
            statementNieuwAdres.setInt(4, adresgegevens.getHuisnummer());
            statementNieuwAdres.setString(5, adresgegevens.getWoonplaats());

            // Ophalen van de laatste genegeneerde sleutel uit de generatedkeys (de nieuwe klantId)
            long nieuw_adresId = 0;
            try (
                    ResultSet resultSet = statementNieuwAdres.executeQuery();
            ) {
                while (resultSet.next()) {
                    nieuw_adresId = resultSet.getInt("adresId");
                }
            }

            // Koppelingstabel BESTELLING_HEEFT_ARTIKEL updaten met de juiste klantId en adresId
            statementAdresKlantKoppeling.setLong(1, klantId);
            statementAdresKlantKoppeling.setLong(2, nieuw_adresId);
            statementAdresKlantKoppeling.execute();

            return nieuw_adresId;
        } catch (SQLException ex) {
            if (ex.getMessage().contains("Duplicate entry")) { // TODO: Is anders in FB
                DeLogger.getLogger().warn("DIT ADRES BESTAAT AL IN DE DATABASE MET ID: " +
                        getAdresID(adresgegevens.getPostcode(), adresgegevens.getHuisnummer(), adresgegevens.getToevoeging()));
                throw new GeneriekeFoutmelding("AdresDAOFireBird: DIT ADRES BESTAAT AL IN DE DATABASE MET ID: " +
                        getAdresID(adresgegevens.getPostcode(), adresgegevens.getHuisnummer(), adresgegevens.getToevoeging()));
            }
            else {
                DeLogger.getLogger().error("SQL FOUT TIJDENS AANMAKEN ADRES " + ex.getMessage());
                throw new GeneriekeFoutmelding("AdresDAOFireBird: SQL FOUT TIJDENS AANMAKEN ADRES " + ex.getMessage());
            }
        }
    }

    /**
     * Geeft op basis van de unieke gegevens van een adres (conform de equalsmethode in Adres)
     * het corresponderende adresId terug.
     *
     * @param postcode Postcode om op te zoeken.
     * @param huisnummer Huisnummer om op te zoeken.
     * @param toevoeging Toevoeging van adres om op te zoeken.
     * @return Het adresId behorend bij dit adres.
     * @throws GeneriekeFoutmelding Als er een fout is wordt deze doorgestuurd naar de GeneriekeFoutmelding met de message van
     * de exception.
     */
    @Override
    public long getAdresID(String postcode, int huisnummer, String toevoeging) throws GeneriekeFoutmelding {
        String query = "SELECT adres_id " +
                "FROM ADRES " +
                "WHERE " +
                "postcode = ? AND " +
                "huisnummer = ? AND " +
                "toevoeging = ?;";

        try (
                Connection connection = connPool.verkrijgConnectie();
                PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setString(1, postcode);
            statement.setInt(2, huisnummer);
            statement.setString(3, toevoeging);

            try (
                    ResultSet rs = statement.executeQuery();
            ) {

                if (!rs.next())
                    throw new GeneriekeFoutmelding("AdresDAOMySQL: ADRES NIET GEVONDEN");
                else {
                    return rs.getLong(1); // Door if-statement is rs al bij next()
                }
            }
        } catch (SQLException ex) {
            DeLogger.getLogger().error("SQL FOUT TIJDENS ZOEKEN ADRES: " + ex.getMessage());
            throw new GeneriekeFoutmelding("AdresDAOFireBird: SQL FOUT TIJDENS ZOEKEN ADRES: " + ex.getMessage());
        }
    }

    /**
     * Geeft een specifiek adres terug in een Adres_Object
     *
     * @param adresId AdresId van het adres dat opgezocht dient te worden.
     * @return Een Adres_object van het adres.
     * @throws GeneriekeFoutmelding Als er een fout is wordt deze doorgestuurd naar de GeneriekeFoutmelding met de message van
     * de exception.
     */
    @Override
    public Adres getAdresOpAdresID(long adresId) throws GeneriekeFoutmelding {
        return null; // TODO
    }

    /**
     * Geeft de adressen terug van een bepaalde klant.
     *
     * @param klantId KlantId van de klant waarvan de adressen opgezocht dienen te worden.
     * @return Een ListIterator van de ArrayList met daarin Klant objecten.
     * @throws GeneriekeFoutmelding Als er een fout is wordt deze doorgestuurd naar de GeneriekeFoutmelding met de message van
     * de exception.
     */
    @Override
    public ListIterator<Adres> getAdresOpKlantID(long klantId) throws GeneriekeFoutmelding {
        String query = "SELECT ADRES.* " +
                "FROM ADRES, KLANT_HEEFT_ADRES " +
                "WHERE " +
                "KLANT_HEEFT_ADRES.adres_id_adres = ADRES.adres_id " +
                "AND " +
                "KLANT_HEEFT_ADRES.klant_id_klant = ?;";

        try (
                Connection connection = connPool.verkrijgConnectie();
                PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setLong(1, klantId);

            try (
                    ResultSet rs = statement.executeQuery();
            ) {

                adresLijst = new ArrayList<>();

                // Ga door alle adressen heen die bij deze klant horen en stop het in een ArrayList<Adres>
                while (rs.next()) {
                    Adres tijdelijkAdres = new Adres();
                    // Adres aanmaken met lege waarden, default constructor.

                    tijdelijkAdres.setAdresId(rs.getLong(1));
                    tijdelijkAdres.setStraatnaam(rs.getString(2));
                    tijdelijkAdres.setPostcode(rs.getString(3));
                    tijdelijkAdres.setToevoeging(rs.getString(4));
                    tijdelijkAdres.setHuisnummer(rs.getInt(5));
                    tijdelijkAdres.setWoonplaats(rs.getString(6));
                    tijdelijkAdres.setDatumAanmaak(rs.getString(7));
                    tijdelijkAdres.setDatumGewijzigd(rs.getString(8));
                    tijdelijkAdres.setAdresActief(rs.getString(9));

                    adresLijst.add(tijdelijkAdres);
                }

                // Return een list-iterator
                return adresLijst.listIterator();
            }
        } catch (SQLException ex) {
            DeLogger.getLogger().error("FOUT TIJDENS GETADRES: " + ex.getMessage());
            throw new GeneriekeFoutmelding("AdresDAOFireBird: FOUT TIJDENS GETADRES: " + ex.getMessage());
        }
    }

    /**
     * Stelt de status is van een adres (0 = inactief, 1 = actief)
     *
     * @param adresId Het adresId van het adres dat geschakeld dient te worden.
     * @param status De nieuwe gewenste status van het adres.
     * @throws GeneriekeFoutmelding Als er een fout is wordt deze doorgestuurd naar de GeneriekeFoutmelding met de message van
     * de exception.
     */
    @Override
    public void schakelStatusAdres(long adresId, int status) throws GeneriekeFoutmelding {
        String query =
                "UPDATE ADRES " +
                        "SET " +
                        "adresActief = ? " +
                        "WHERE " +
                        "adres_id = ?";

        try (
                Connection connection = connPool.verkrijgConnectie();
                PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.setInt(1, status);
            statement.setLong(2, adresId);
            statement.execute();

        } catch (SQLException ex) {
            DeLogger.getLogger().error("SQL FOUT TIJDENS ADRES OP ID INACTIEF ZETTEN" + ex.getMessage());
            throw new GeneriekeFoutmelding("AdresDAOFireBird: SQL FOUT TIJDENS ADRES OP ID INACTIEF ZETTEN" + ex.getMessage());
        }
    }
}

