package database;

import database.factories.DAOFactory;
import database.interfaces.AdresDAO;
import database.interfaces.ArtikelDAO;
import database.interfaces.BestellingDAO;
import database.interfaces.KlantDAO;
import model.Adres;
import model.Artikel;
import model.Bestelling;
import model.Klant;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.ListIterator;

/**
 * Created by Milan_Verheij on 24-07-16.
 *
 * Script om de gegevens van de MySQL database -> FireBird database te duwen.
 * Werkt alleen met een lege FireBird database.
 *
 * Let op:
 *
 * Prijshistoriek gaat verloren.
 *
 */
public class MySQLFireBirdMigratie {
    public static void main(String[] args) throws Exception {

        boolean klantEnAdressenImport = true;
        boolean artikelImport = true;
        boolean bestellingImport = true;

        /** Klanten en adressen import MySQL -> FireBird */
        if (klantEnAdressenImport)
        {
            System.out.println("KLANTEN EN ADRESSEN IMPORT");
            System.out.println("--------------------------");
            Thread.sleep(1000);

            KlantDAO klantDAOMySQL = DAOFactory.getDAOFactory("MySQL", "c3po").getKlantDAO();
            AdresDAO adresDAOMySQL = DAOFactory.getDAOFactory("MySQL", "c3po").getAdresDAO();
            ArrayList<Klant> klantenLijst = klantDAOMySQL.getAlleKlanten();
            Iterator<Klant> klantIterator = klantenLijst.iterator();

            KlantDAO klantDAOFireBird = DAOFactory.getDAOFactory("FireBird", "HikariCP").getKlantDAO();
            AdresDAO adresDAOFirebird = DAOFactory.getDAOFactory("FireBird", "HikariCP").getAdresDAO();

            while (klantIterator.hasNext()) {
                Klant klant = klantIterator.next();
                Long klantIdOorspronkelijk = klant.getKlantId();

                // Nieuwe klant aanmaken
                klant.setKlantId(0);
                klant.setDatumAanmaak(null);
                klant.setAdresGegevens(null);
                klant.setKlantActief(null);
                System.out.println("KLANT: " + klant);
                long nieuwKlantId = klantDAOFireBird.nieuweKlant(klant, 0);

                // Adressen opzoeken die horen bij deze klant en koppelen aan de klant
                ListIterator<Adres> adresListIterator = adresDAOMySQL.getAdresOpKlantID(klantIdOorspronkelijk);
                while (adresListIterator.hasNext()) {
                    Adres adres = adresListIterator.next();
                    System.out.println("ADRES: " + adres);
                    adresDAOFirebird.nieuwAdres(nieuwKlantId, adres);
                }
                System.out.println("");
            }
        }

        /** Artikelen import MySQL -> FireBird */
        if (artikelImport)
        {
            System.out.println("ARTIKELEN IMPORT");
            System.out.println("----------------");
            Thread.sleep(1000);

            ArtikelDAO artikelDAOMySQL = DAOFactory.getDAOFactory("MySQL", "c3po").getArtikelDAO();
            LinkedHashSet<Artikel> artikelenMySQL = artikelDAOMySQL.getAlleArtikelen(true);
            Iterator<Artikel> artikelIterator = artikelenMySQL.iterator();

            ArtikelDAO artikelDAOFireBIrd = DAOFactory.getDAOFactory("FireBird", "HikariCP").getArtikelDAO();

            while (artikelIterator.hasNext()) {
                Artikel artikel = artikelIterator.next();
                System.out.println(artikel);
                artikelDAOFireBIrd.nieuwArtikel(artikel);
            }

            System.out.println("");
        }

        if (bestellingImport) {
            System.out.println("BESTELLINGEN IMPORT");
            System.out.println("-------------------");
            Thread.sleep(1000);

            BestellingDAO bestellingDAOMySQL = DAOFactory.getDAOFactory("MySQL", "c3po").getBestellingDAO();
            BestellingDAO bestellingDAOFireBird = DAOFactory.getDAOFactory("FireBird", "HikariCP").getBestellingDAO();

            KlantDAO klantDAOMySQL = DAOFactory.getDAOFactory("MySQL", "c3po").getKlantDAO();
            Iterator<Klant> klantIterator = klantDAOMySQL.getAlleKlanten().iterator();

            ArtikelDAO artikelDAOFireBIrd = DAOFactory.getDAOFactory("FireBird", "HikariCP").getArtikelDAO();

            while (klantIterator.hasNext()) {
                Klant klant = klantIterator.next();
                System.out.println("\n\tKLANT ID: " + klant.getKlantId());
                System.out.println("\t--------");
                Iterator<Bestelling> bestellingIterator = bestellingDAOMySQL.getBestellingOpKlantId(klant.getKlantId(), true).iterator();

                while (bestellingIterator.hasNext()) {
                    Bestelling bestelling = bestellingIterator.next();

                    // Rechtzetten prijsID's aangezien hij enkel de laatste prijs pakt nu
                    Iterator<Artikel> artikelen = bestelling.getArtikelLijst().iterator();
                    while (artikelen.hasNext()) {
                        Artikel artikelInBestellingMySQL = artikelen.next();

                        Artikel artikelInFireBird = artikelDAOFireBIrd.getArtikel(artikelInBestellingMySQL.getArtikelId());

                        artikelInBestellingMySQL.setPrijsId(artikelInFireBird.getPrijsId());
                    }

                    System.out.println("\t" + bestelling);
                    bestellingDAOFireBird.nieuweBestelling(bestelling);
                }
            }

        }

    }
}
