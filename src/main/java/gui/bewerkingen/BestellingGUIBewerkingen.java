package gui.bewerkingen;

import java.util.ArrayList;
import java.util.Iterator;
import exceptions.GeneriekeFoutmelding;
import gui.gui.BestellingGui;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import model.Artikel;
import model.Bestelling;
import model.GuiPojo;

public class BestellingGUIBewerkingen {
    private BestellingGui bestellingGUI;
    private Bestelling bestelling;


    private boolean bestellingAanpassen = false;

    private ArrayList<Artikel> artikelArrayList;

    public void maakbestelling(long klantId, Bestelling bestelling, CheckBox bestellingActief,
                               ListView<Long> bestellingListView, Stage bestellingStage){
        bestelling.setKlantId(klantId);

        if(bestelling.getArtikelLijst() != null){ // Een bestelling moet minimaal een artikel bevatten
            try {
                bestelling.setBestellingActief(bestellingActief.isSelected());
                if(bestellingAanpassen){
                    if(bestellingActief.isSelected()){
                        GuiPojo.bestelDAO.updateBestelling(bestelling);
                    }else{
                        GuiPojo.bestelDAO.setEnkeleBestellingInactief(bestelling.getBestellingId());
                    }
                }else{
                    long bestellingId = GuiPojo.bestelDAO.nieuweBestelling(bestelling);
                    bestelling.setBestellingId(bestellingId);
                    bestellingListView.getItems().add(bestellingId);
                    GuiPojo.bestellingLijst.put(bestellingId, bestelling);
                }
                bestellingStage.close();
            }catch(GeneriekeFoutmelding e) {
                e.printStackTrace();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void populateBestellingListView(ListView<String> bestellingListView, Bestelling bestelling) {
        bestellingListView.getItems().clear();
        if (!bestelling.getArtikelLijst().isEmpty()) {
            for (Artikel artikel : bestelling.getArtikelLijst()) {
                bestellingListView.getItems().add("Naam: " + artikel.getArtikelNaam() + "\nPrijs: "
                        + artikel.getArtikelPrijs() + "\nAantal: " + artikel.getAantalBesteld());
            }
        }
    }

        public void populateArrayListsEnListView(ListView<String> artikelenListView){
        try{
            Iterator<Artikel> artikelIterator = GuiPojo.artikelDAO.getAlleArtikelen(true).iterator(); //Haal alle artikelen op die in assortiment zijn
            Artikel artikel;
            artikelArrayList = new ArrayList<Artikel>();

            while(artikelIterator.hasNext()){
                artikel = artikelIterator.next();

                artikelenListView.getItems().add("Naam: " + artikel.getArtikelNaam() + "\nPrijs: " + artikel.getArtikelPrijs());
                artikelArrayList.add(artikel);
            }
        } catch (GeneriekeFoutmelding e) {
            e.printStackTrace();
        }
    }
}