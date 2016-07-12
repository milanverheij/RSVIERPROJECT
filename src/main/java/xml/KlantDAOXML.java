package xml;

import exceptions.GeneriekeFoutmelding;
import interfaces.KlantDAO;
import logger.DeLogger;
import model.Adres;
import model.Bestelling;
import model.Klant;

import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

/**
 * Created by Milan_Verheij on 11-07-16. // TODO
 */
public class KlantDAOXML implements KlantDAO {

    private Vector<Klant> klantenDatabase = null;
    private XMLUtility xmlUtility = new XMLUtility();

    @Override
    public long nieuweKlant(Klant nieuweKlant, long adres_id, Adres adresgegevens, Bestelling bestelGegevens) throws GeneriekeFoutmelding {

        // Check of de database reeds is ingelezen, zo niet wordt deze ingelezen.
        controleerDatabaseIngelezen();

        // Als er geen klant wordt meegegeven wordt een fout gegooid.
        if (nieuweKlant == null) {
            DeLogger.getLogger().warn("KAN GEEN KLANT AANMAKEN MET NULL OBJECT");
            throw new GeneriekeFoutmelding("KlantDAOXML: KAN GEEN KLANT AANMAKEN MET NULL OBJECT");
        }

        // Een klant aanmaken kan enkel met een voornaam en en achternaam, niet minder
        if (nieuweKlant.getVoornaam() == null || nieuweKlant.getAchternaam() == null) {
            DeLogger.getLogger().warn(("KAN GEEN KLANT MAKEN ZONDER VOOR EN ACHTERNAAM" + nieuweKlant));
            throw new GeneriekeFoutmelding("KlantDAOXML: KAN GEEN KLANT MAKEN ZONDER VOOR EN ACHTERNAAM: " + nieuweKlant);
        }

        // Adresgegevens op null, geen onderdeel van deze opdracht.
        nieuweKlant.setAdresGegevens(null);

        // Klant altijd op actief als dit niet is ingevuld
        nieuweKlant.setKlantActief("1");

        // Klant een datum gemaakt meemegen
        nieuweKlant.setDatumAanmaak(new Date().toString());

        // KlantID wordt laatste item in de lijst + 1
        if (klantenDatabase.size() > 0)
            nieuweKlant.setKlantId(klantenDatabase.lastElement().getKlantId() + 1);

        klantenDatabase.add(nieuweKlant);

        xmlUtility.schrijfXMLFile(klantenDatabase);

        // Adres gedeelte niet meegenomen in deze DAO gezien dit geen onderdeel van de opdracht is.
        return klantenDatabase.lastElement().getKlantId();
    }

    @Override
    public long nieuweKlant(Klant nieuweKlant, long adres_id) throws GeneriekeFoutmelding {
        // Check of de database reeds is ingelezen, zo niet wordt deze ingelezen.
        return nieuweKlant(nieuweKlant, adres_id, null, null);
    }

    @Override
    public long getKlantID(String voornaam, String achternaam, String email) throws GeneriekeFoutmelding {
        // Check of de database reeds is ingelezen, zo niet wordt deze ingelezen.
        controleerDatabaseIngelezen();

        ListIterator<Klant> klantenLijst = getKlantOpKlant(null);

        while (klantenLijst.hasNext()) {
            Klant tijdelijkeKlant = klantenLijst.next();

            if (tijdelijkeKlant.getVoornaam().contains(voornaam) &&
                    tijdelijkeKlant.getAchternaam().contains(achternaam) &&
                    tijdelijkeKlant.getEmail().contains(email))
                return tijdelijkeKlant.getKlantId();
        }

        return -1;
    }

    @Override
    public ListIterator<Klant> getAlleKlanten() throws GeneriekeFoutmelding {
        return getKlantOpKlant(null);
    }

    @Override
    public ListIterator<Klant> getKlantOpKlant(Klant klant) throws GeneriekeFoutmelding {
        // Check of de database reeds is ingelezen, zo niet wordt deze ingelezen.
        controleerDatabaseIngelezen();

        return klantenDatabase.listIterator();
    }

    @Override
    public ListIterator<Klant> getKlantOpAdres(Adres adresgegevens) throws GeneriekeFoutmelding {
        throw new GeneriekeFoutmelding("KlantDAOXML: Zoeken klant op Adres is niet geintegreerd in de XML - functie");
    }

    @Override
    public ListIterator<Klant> getKlantOpBestelling(long bestellingId) throws GeneriekeFoutmelding {
        throw new GeneriekeFoutmelding("KlantDAOXML: Zoeken klant op Bestelling is niet geintegreerd in de XML - functie");
    }

    @Override
    public void updateKlant(Klant nieuweKlant) throws GeneriekeFoutmelding {
        // Check of de database reeds is ingelezen, zo niet wordt deze ingelezen.
        controleerDatabaseIngelezen();

        klantenDatabase.get((int)nieuweKlant.getKlantId()).setVoornaam(nieuweKlant.getVoornaam());
        klantenDatabase.get((int)nieuweKlant.getKlantId()).setAchternaam(nieuweKlant.getAchternaam());
        klantenDatabase.get((int)nieuweKlant.getKlantId()).setTussenvoegsel(nieuweKlant.getTussenvoegsel());
        klantenDatabase.get((int)nieuweKlant.getKlantId()).setEmail(nieuweKlant.getEmail());
        klantenDatabase.get((int)nieuweKlant.getKlantId()).setDatumGewijzigd(new Date().toString());

        xmlUtility.schrijfXMLFile(klantenDatabase);
    }

    @Override
    public void updateKlant(Klant nieuweKlant, Adres adresgegevens) throws GeneriekeFoutmelding {
        throw new GeneriekeFoutmelding("KlantDAOXML: Updaten klant met Adres is niet geintegreerd in de XML - functie");
    }

    @Override
    public void schakelStatusKlant(long klantId, int status) throws GeneriekeFoutmelding {
        // Check of de database reeds is ingelezen, zo niet wordt deze ingelezen.
        controleerDatabaseIngelezen();

        Klant tijdelijkeKlant = klantenDatabase.get((int)klantId);
        tijdelijkeKlant.setKlantActief(String.valueOf(status));

        updateKlant(tijdelijkeKlant);

        xmlUtility.schrijfXMLFile(klantenDatabase);
    }

    @Override
    public void schakelStatusKlant(Klant klant) throws GeneriekeFoutmelding {
        // Check of de database reeds is ingelezen, zo niet wordt deze ingelezen.
        controleerDatabaseIngelezen();

        klant.setKlantActief((klant.getKlantActief().charAt(0) == '0' ? "1" : "0"));
        updateKlant(klant);

        xmlUtility.schrijfXMLFile(klantenDatabase);
    }

    @Override
    public long verwijderKlantOpBestellingId(long bestellingId) throws GeneriekeFoutmelding {
        throw new GeneriekeFoutmelding("KlantDAOXML: Verwijderen klant op bestelling ID is niet geintegreerd in de XML - functie");
    }

    @Override
    public void printKlantenInConsole(ListIterator<Klant> klantenIterator) throws GeneriekeFoutmelding {
        while (klantenIterator.hasNext()) {
            Klant tijdelijkeKlant = klantenIterator.next();
            if (tijdelijkeKlant.getKlantActief().charAt(0) == '1') {
                System.out.println("\n\n\t------------------KLANT " + (tijdelijkeKlant.getKlantId() + 1)+ " BEGIN---------------------------");
                System.out.print("\n\tKLANTID:           " + (tijdelijkeKlant.getKlantId() + 1));
                System.out.print("\n\tVoornaam:          " + tijdelijkeKlant.getVoornaam());
                System.out.print("\n\tAchternaam:        " + tijdelijkeKlant.getAchternaam());
                System.out.print("\n\tTussenvoegsel:     " + tijdelijkeKlant.getTussenvoegsel());
                System.out.print("\n\tE-Mail:            " + tijdelijkeKlant.getEmail());
                System.out.print("\n\tDatum Aangemaakt:  " + tijdelijkeKlant.getDatumAanmaak());
                System.out.print("\n\tDatum Gewijzigd:   " + tijdelijkeKlant.getDatumGewijzigd());
            } else {
                System.out.println("\n\t------------------KLANT " + (tijdelijkeKlant.getKlantId() + 1 )+ " INACTIEF------------------------");
            }
        }
        System.out.println("\n");
    }

    /**
     * Controleert of de database reeds is ingelezen, zo niet dan wordt deze ingelezen.
     */
    private void controleerDatabaseIngelezen() throws GeneriekeFoutmelding {
        if (klantenDatabase == null) {
            klantenDatabase = xmlUtility.leesXMLFile();
        }
    }
}
