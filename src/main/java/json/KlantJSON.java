//package json;
//
//import java.io.File;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.sql.Date;
//import java.util.ArrayList;
//import java.util.ListIterator;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;
//
//import exceptions.GeneriekeFoutmelding;
//import interfaces.KlantDAO;
//import logger.DeLogger;
//import model.Adres;
//import model.Bestelling;
//import model.Klant;
//
//public class KlantJSON implements KlantDAO {
//
//    final File file = new File("klantJason.txt");
//
//    @Override
//    @SuppressWarnings("unchecked")
//    public long nieuweKlant(String voornaam, String achternaam, String tussenvoegsel, String email, long adres_id, Adres adresgegevens, Bestelling bestelGegevens) throws GeneriekeFoutmelding {
//        if(!file.exists())
//            maakBestandAan(); // Zet de klantId teller in het bestand als het bestand nog niet bestaat
//
//        JSONObject klantIdObject = getKlantId();
//        long klantId = (long) klantIdObject.get("klantId") + 1;
//
//        JSONObject klantObject = new JSONObject();
//
//        klantObject.put("klantId", klantId);
//        klantObject.put("voornaam", voornaam);
//        klantObject.put("achternaam", achternaam);
//        klantObject.put("tussenvoegsel", tussenvoegsel);
//        klantObject.put("email", email);
//        klantObject.put("datumAanmaak", new Date(System.currentTimeMillis()).toString());
//        klantObject.put("datumGewijzigd", null);
//        klantObject.put("klantActief", "1");
//
//        createJSONArray(klantObject, klantId);
//
//        return klantId;
//    }
//
//    @Override
//    public long nieuweKlant(String voornaam, String achternaam, Adres adresgegevens) throws GeneriekeFoutmelding {
//        return nieuweKlant(voornaam, achternaam, "", "", 0, adresgegevens, null);
//    }
//
//    @Override
//    public long nieuweKlant(String voornaam, String achternaam) throws GeneriekeFoutmelding {
//        return nieuweKlant(voornaam, achternaam, "", "", 0, null, null);
//    }
//
//    @Override
//    public ListIterator<Klant> getAlleKlanten() throws GeneriekeFoutmelding {
//        ArrayList<Klant> list = new ArrayList<Klant>();
//
//        ListIterator<JSONObject> it = krijgIteratorEnSkipEersteENtry();
//
//        while(it.hasNext()){
//            Klant k = maakKlantObject(it.next());
//            list.add(k);
//        }
//        return list.listIterator();
//    }
//
//    @Override
//    public ListIterator<Klant> getKlantOpKlant(long klantId) throws GeneriekeFoutmelding {
//        ArrayList<Klant> list = new ArrayList<Klant>();
//        JSONObject o;
//
//        ListIterator<JSONObject> it = krijgIteratorEnSkipEersteENtry();
//
//        while(it.hasNext()){
//            o = it.next();
//            if((long) o.get("klantId") == klantId){
//                list.add(maakKlantObject(o));
//            }
//        }
//        return list.listIterator();
//    }
//
//    @Override
//    public ListIterator<Klant> getKlantOpKlant(String voornaam) throws GeneriekeFoutmelding {
//        ArrayList<Klant> list = new ArrayList<Klant>();
//        JSONObject o;
//
//        ListIterator<JSONObject> it = krijgIteratorEnSkipEersteENtry();
//
//        while(it.hasNext()){
//            o = it.next();
//            if(((String) o.get("voornaam")).equals(voornaam)){
//                list.add(maakKlantObject(o));
//            }
//        }
//        return list.listIterator();
//    }
//
//    @Override
//    public ListIterator<Klant> getKlantOpKlant(String voornaam, String achternaam) throws GeneriekeFoutmelding {
//        ArrayList<Klant> list = new ArrayList<Klant>();
//        JSONObject o;
//
//        ListIterator<JSONObject> it = krijgIteratorEnSkipEersteENtry();
//
//        while(it.hasNext()){
//            o = it.next();
//            if(((String) o.get("voornaam")).equals(voornaam) && ((String) o.get("achternaam")).equals(achternaam)){
//                list.add(maakKlantObject(o));
//            }
//        }
//        return list.listIterator();
//    }
//
//    @Override
//    @SuppressWarnings("unchecked")
//    public void updateKlant(Long klantId, String voornaam, String achternaam, String tussenvoegsel, String email) throws GeneriekeFoutmelding {
//        JSONArray array = leesArrayUitBestand();
//
//        for(Object o : array){
//            if((long) ((JSONObject) o).get("klantId") == klantId){
//                JSONObject klant = (JSONObject) o;
//                klant.put("voornaam", voornaam);
//                klant.put("achternaam", achternaam);
//                klant.put("tussenvoegsel", tussenvoegsel);
//                klant.put("email", email);
//                klant.put("datumGewijzigd", new Date(System.currentTimeMillis()).toString());
//            }
//        }
//        schrijfNaarFile(array);
//    }
//
//    @Override
//    @SuppressWarnings("unchecked")
//    public void schakelStatusKlant(long klantId, int status) throws GeneriekeFoutmelding {
//        JSONArray array = leesArrayUitBestand();
//
//        for(Object o : array){
//            if((long) ((JSONObject) o).get("klantId") == klantId){
//                JSONObject klant = (JSONObject) o;
//                klant.put("klantActief", status);
//                klant.put("datumGewijzigd", new Date(System.currentTimeMillis()).toString());
//            }
//        }
//        schrijfNaarFile(array);
//    }
//
//    @Override
//    public void printKlantenInConsole(ListIterator<Klant> klantenIterator) throws GeneriekeFoutmelding {
//        JSONArray array = leesArrayUitBestand();
//
//        for(Object o : array)
//            System.out.println((JSONObject) o);
//    }
//
//    @SuppressWarnings("unchecked")
//    private void createJSONArray(JSONObject klantObject, long klantId) {
//        JSONArray array = leesArrayUitBestand();
//
//        array.add(klantObject);
//        ((JSONObject)array.get(0)).put("klantId", klantId); //Update het klantId in de array
//
//        schrijfNaarFile(array);
//    }
//
//    private JSONArray leesArrayUitBestand() {
//        JSONParser parser = new JSONParser();
//        JSONArray array = new JSONArray();
//
//        try{	//Probeer een array uit het doelbestand te lezen zodat we er aan kunnen appenden
//            Object object = parser.parse(new FileReader(file));
//            array = (JSONArray) object;
//        }catch(ParseException e){
//            e.printStackTrace();
//            DeLogger.getLogger().warn("Fout bij het uitlezen van het doelbestand {}: ", file.toString(), e.getStackTrace());
//        } catch (IOException e) {
//            DeLogger.getLogger().error("SQL error op {}", e.getMessage(), e.getStackTrace());
//        }
//        return array;
//    }
//
//    @SuppressWarnings("unchecked")
//    private void maakBestandAan() {
//        try(FileWriter bestand = new FileWriter(file)){
//
//            JSONObject klantIdObject = new JSONObject();
//            klantIdObject.put("klantId", 0);
//
//            JSONArray array = new JSONArray();
//            array.add(klantIdObject);
//
//            bestand.write(array.toJSONString());
//
//        } catch (IOException e) {
//            DeLogger.getLogger().error("Kon niet naar bestand {} schrijven.", file, e.getStackTrace());
//            e.printStackTrace();
//        }
//    }
//
//    private void schrijfNaarFile(JSONArray array) {
//        try(FileWriter bestand = new FileWriter(file)){
//            bestand.write(array.toJSONString());
//        } catch (IOException e) {
//            DeLogger.getLogger().error("Kon niet naar bestand {} schrijven.", file, e.getStackTrace());
//            e.printStackTrace();
//        }
//    }
//
//    private JSONObject getKlantId() throws GeneriekeFoutmelding {
//        JSONArray array = leesArrayUitBestand();
//        return (JSONObject) array.get(0);
//    }
//
//    private Klant maakKlantObject(JSONObject o) {
//        Klant k = new Klant();
//
//        k.setAchternaam((String) o.get("achternaam"));
//        k.setDatumAanmaak((String) o.get("datumAanmaak"));
//        k.setDatumGewijzigd((String) o.get("datumGewijzigd"));
//        k.setEmail((String) o.get("email"));
//        k.setKlantActief((String) o.get("klantActief"));
//        k.setKlantId((long) o.get("klantId"));
//        k.setTussenvoegsel((String) o.get("tussenvoegsel"));
//        k.setVoornaam((String) o.get("voornaam"));
//
//        return k;
//    }
//
//    @SuppressWarnings("unchecked")
//    private ListIterator<JSONObject> krijgIteratorEnSkipEersteENtry() {
//        JSONArray array = leesArrayUitBestand();
//        ListIterator<JSONObject> it = array.listIterator();
//        if(it.hasNext())
//            it.next();		// Skip de klantId entry aan het begin van het bestand
//        return it;
//    }
//
//
//
//
//
//
//
//    //Irrelevant voor klant klasse dus niet uitgewerkt
//    @Override
//    public long verwijderKlantOpBestellingId(long bestellingId) throws GeneriekeFoutmelding {
//        // TODO Auto-generated method stub
//        return 0;
//    }
//
//    @Override
//    public ListIterator<Klant> getKlantOpKlant(Klant klant) throws GeneriekeFoutmelding {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public ListIterator<Klant> getKlantOpAdres(Adres adresgegevens) throws GeneriekeFoutmelding {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public ListIterator<Klant> getKlantOpAdres(String straatnaam) throws GeneriekeFoutmelding {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public ListIterator<Klant> getKlantOpAdres(String postcode, int huisnummer) throws GeneriekeFoutmelding {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public ListIterator<Klant> getKlantOpBestelling(long bestellingId) throws GeneriekeFoutmelding {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public void updateKlant(long KlantId, String voornaam, String achternaam, String tussenvoegsel, String email,
//                            long adres_id, Adres adresgegevens) throws GeneriekeFoutmelding {
//        // TODO Auto-generated method stub
//
//    }
//
//    @Override
//    public long nieuweKlant(Klant nieuweKlant, long adres_id) throws GeneriekeFoutmelding {
//        return 0;
//    }
//
//    @Override
//    public long getKlantID(String voornaam, String achternaam, String email) throws GeneriekeFoutmelding {
//        // TODO Auto-generated method stub
//        return 0;
//    }
//
//    // Deze zijn dom (dubbele namen zijn mogelijk) dus ook niet uitgewerkt
//    @Override
//    public void schakelStatusKlant(String voornaam, String achternaam) throws GeneriekeFoutmelding {
//
//    }
//    @Override
//    public void schakelStatusKlant(String voornaam, String achternaam, String tussenvoegsel) throws GeneriekeFoutmelding {
//        // TODO Auto-generated method stub
//
//    }
//
//}
