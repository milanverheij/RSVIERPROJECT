package gui.bewerkingen;

import exceptions.GeneriekeFoutmelding;
import gui.gui.ErrorBox;
import gui.gui.KlantGui;
import javafx.stage.Stage;
import model.Adres;
import model.GuiPojo;
import model.Klant;

public class KlantGUIBewerkingen {
    KlantGui klantGUI;

    public KlantGUIBewerkingen() {
        if (klantGUI == null)
            klantGUI = new KlantGui();
    }

    public void populateTextFields(){
        Klant klant = GuiPojo.klant;
        klantGUI.voorNaam.setPromptText("Voornaam");
        klantGUI.tussenVoegsel.setPromptText("Tussenvoegsel");
        klantGUI.achterNaam.setPromptText("Achternaam");
        klantGUI.email.setPromptText("E-mail adres");

        klantGUI.straatNaam.setPromptText("Straatnaam");
        klantGUI.postcode.setPromptText("1234AB");
        klantGUI.huisNummer.setPromptText("123");
        klantGUI.toevoeging.setPromptText("A");
        klantGUI.woonplaats.setPromptText("Woonplaats");

        if(!(klant == null)){
            if(klant.getVoornaam() != null)
                klantGUI.voorNaam.setText(klant.getVoornaam());
            if(klant.getAchternaam() != null)
                klantGUI.achterNaam.setText(klant.getAchternaam());
            if(klant.getTussenvoegsel() != null)
                klantGUI.tussenVoegsel.setText(klant.getTussenvoegsel());
            if(klant.getEmail() != null)
                klantGUI.email.setText(klant.getEmail());

            if(GuiPojo.klant.getAdresGegevens() != null) {
                Adres adres = GuiPojo.klant.getAdresGegevens();

                klantGUI.straatNaam.setText(adres.getStraatnaam());
                klantGUI.postcode.setText(adres.getPostcode());
                klantGUI.huisNummer.setText("" + adres.getHuisnummer());
                klantGUI.toevoeging.setText(adres.getToevoeging());
                klantGUI.woonplaats.setText(adres.getWoonplaats());
            }
        }
    }

    public void maakKlantAan(Stage klantStage){
        Klant klant = GuiPojo.klant;
        Adres adres = null;

        alleVeldenInvullen();

        if(!klantGUI.huisNummer.getText().equals("")){
            adres = new Adres(klantGUI.straatNaam.getText(), klantGUI.postcode.getText(), klantGUI.toevoeging.getText(),
                    Integer.parseInt(klantGUI.huisNummer.getText()), klantGUI.woonplaats.getText());
        }
        Klant nieuweKlant = new Klant(0, klantGUI.voorNaam.getText(), klantGUI.achterNaam.getText(),
                klantGUI.tussenVoegsel.getText(), klantGUI.email.getText(), adres);
        adres = new Adres();
        adres.setWoonplaats(klantGUI.woonplaats.getText());
        adres.setPostcode(klantGUI.postcode.getText());
        adres.setHuisnummer(Integer.parseInt(klantGUI.huisNummer.getText()));
        adres.setStraatnaam(klantGUI.straatNaam.getText());
        adres.setToevoeging(klantGUI.toevoeging.getText());
        GuiPojo.klant.setAdresGegevens(adres);



        try {
            if(klant.getVoornaam() == null){
                GuiPojo.klant.setKlantId(GuiPojo.klantDAO.nieuweKlant(nieuweKlant, 0));
            } else {
                GuiPojo.klantDAO.updateKlant(new Klant(klant.getKlantId(), nieuweKlant.getVoornaam(),
                        nieuweKlant.getAchternaam(), nieuweKlant.getTussenvoegsel(), nieuweKlant.getEmail(), null));
                if(GuiPojo.klant.getAdresGegevens().getAdresId() != 0)
                    GuiPojo.adresDAO.updateAdres(GuiPojo.klant.getAdresGegevens().getAdresId(), adres);
                else
                    GuiPojo.adresDAO.nieuwAdres(klant.getKlantId(), GuiPojo.klant.getAdresGegevens());
            }
            klantStage.close();

        } catch (GeneriekeFoutmelding e) {
            new ErrorBox().setMessageAndStart(e.getMessage());
        }
    }

    private void alleVeldenInvullen(){
        if(klantGUI.voorNaam.getText().isEmpty())
            klantGUI.voorNaam.setText("");
        if(klantGUI.achterNaam.getText().isEmpty())
            klantGUI.achterNaam.setText("");
        if(klantGUI.tussenVoegsel.getText().isEmpty())
            klantGUI.tussenVoegsel.setText("");
        if(klantGUI.email.getText().isEmpty())
            klantGUI.email.setText("");

        if(klantGUI.straatNaam.getText().isEmpty())
            klantGUI.straatNaam.setText("");
        if(klantGUI.postcode.getText().isEmpty())
            klantGUI.postcode.setText("");
        if(klantGUI.toevoeging.getText().isEmpty())
            klantGUI.toevoeging.setText("");
        if(klantGUI.huisNummer.getText().isEmpty())
            klantGUI.huisNummer.setText("");
        if(klantGUI.woonplaats.getText().isEmpty())
            klantGUI.woonplaats.setText("");

    }
}