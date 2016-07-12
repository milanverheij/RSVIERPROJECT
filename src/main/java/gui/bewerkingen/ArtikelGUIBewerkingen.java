package gui.bewerkingen;

import java.math.BigDecimal;

import exceptions.GeneriekeFoutmelding;
import gui.gui.ErrorBox;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import logger.DeLogger;
import model.Artikel;
import model.GuiPojo;

public class ArtikelGUIBewerkingen {
    ErrorBox errorBox = new ErrorBox();

    public void nieuwArtikel(TextField naamField, TextField artikelPrijsField, TextField verwachteLevertijdField, CheckBox inAssortiment) {
        Artikel artikel = new Artikel();
        artikel.setArtikelNaam(naamField.getText());
        artikel.setPrijsId(0);
        artikel.setArtikelPrijs(new BigDecimal(artikelPrijsField.getText()));
        artikel.setVerwachteLevertijd(Integer.parseInt(verwachteLevertijdField.getText()));
        artikel.setInAssortiment(inAssortiment.isSelected());

        try {
            GuiPojo.artikelDAO.nieuwArtikel(artikel);
        } catch (GeneriekeFoutmelding e) {
            e.printStackTrace();
            DeLogger.getLogger().error("Fout bij aanmaken nieuw artikel", e.getCause());
            errorBox.setMessageAndStart("Fout bij aanmaken nieuw artikel: " + e.getMessage());
        }
    }

    public void updateArtikel(Artikel artikel, TextField naamField, TextField artikelPrijsField, TextField verwachteLevertijdField, CheckBox inAssortiment) {
        boolean prijsAangepast = false;
        boolean ietsAangepast = false;
        BigDecimal prijs = new BigDecimal(artikelPrijsField.getText());

        if(!artikel.getArtikelNaam().equals(naamField.getText())){
            ietsAangepast = true;
            artikel.setArtikelNaam(naamField.getText());
        }
        if((artikel.getArtikelPrijs()).compareTo(prijs) != 0){
            prijsAangepast = true;
            artikel.setArtikelPrijs(new BigDecimal(artikelPrijsField.getText()));
        }
        if(artikel.isInAssortiment() != inAssortiment.isSelected()){
            ietsAangepast = true;
            artikel.setInAssortiment(inAssortiment.isSelected());
        }
        if(artikel.getVerwachteLevertijd() != Integer.parseInt(verwachteLevertijdField.getText())){
            ietsAangepast = true;
            artikel.setVerwachteLevertijd(Integer.parseInt(verwachteLevertijdField.getText()));
        }

        if(ietsAangepast || prijsAangepast){
            try {
                GuiPojo.artikelDAO.updateArtikel(artikel.getArtikelId(), artikel);
            } catch (GeneriekeFoutmelding e) {
                e.printStackTrace();
                DeLogger.getLogger().error("Fout bij update artikel", e.getCause());
                errorBox.setMessageAndStart("Fout bij update artikel: " + e.getMessage());
            }
        }
    }
}