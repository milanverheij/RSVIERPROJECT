package xml;

import exceptions.GeneriekeFoutmelding;
import logger.DeLogger;
import model.Klant;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.Vector;

/**
 * Created by Milan_Verheij on 11-07-16.
 */
public class XMLUtility {

    private File file;

    /**
     * Lees alle klanten in het XML bestand en stop dit in een Vector<Klant>.
     *
     * @return De klantenDatabase in Vector<Klant> formaat
     * @throws GeneriekeFoutmelding Gooit een foutmelding met gegevens als er iets mis gaat.
     */
    public Vector<Klant> leesXMLFile() throws GeneriekeFoutmelding {
        file = new File("KlantXML.xml");

        Vector<Klant> klantenDatabase = new Vector<>();

        // Check of het bestand bestaat, anders return de lijst zoals hij was.
        if (file.exists()) {
            try (
                    XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(file)))
            ) {
                Object result = decoder.readObject();
                klantenDatabase = (Vector<Klant>)result;

                DeLogger.getLogger().info("Klantenbestand in XML ingelezen");

            } catch (Exception e) {
                DeLogger.getLogger().error("Fout tijdens decoden XML-bestand: " + e.getMessage());
                throw new GeneriekeFoutmelding("Fout tijdens decoden XML-bestand: " + e.getMessage());
            }

        }
        return klantenDatabase;
    }

    /**
     * Schrijf een klantenDatabase naar het XML-bestand
     *
     * @param klantenDatabase De te schrijven klantenDatabase in Vector<Klant> formaat
     * @throws GeneriekeFoutmelding Gooit een foutmelding met gegevens als er iets mis gaat.
     */
    public void schrijfXMLFile(Vector<Klant> klantenDatabase) throws GeneriekeFoutmelding {

        file = new File("KlantXML.xml");
        verwijderXMLFile(file);

        try (
            XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(file)))
        ){
            encoder.writeObject(klantenDatabase);
            DeLogger.getLogger().info("Klantenbestand in XML weggeschreven.");
        } catch (Exception ex) {
            DeLogger.getLogger().error("Fout tijdens encoden XML-bestand: " + ex.getMessage());
            throw new GeneriekeFoutmelding("Fout tijdens encoden XML-bestand: " + ex.getMessage());
        }
    }

    /**
     * Verwijderd het huidige XML bestand
     *
     * @throws GeneriekeFoutmelding Gooit een foutmelding met gegevens als er iets mis gaat.
     */
    public void verwijderXMLFile(File file) throws GeneriekeFoutmelding {
        try {
            file.delete();
            DeLogger.getLogger().info("Klantenbestand in XML verwijderd.");
        } catch (Exception ex) {
            DeLogger.getLogger().error("Fout tijdens verwijderen XML-bestand: " + ex.getMessage());
            throw new GeneriekeFoutmelding("Fout tijdens verwijderen XML-bestand: " + ex.getMessage());
        }
    }
}
