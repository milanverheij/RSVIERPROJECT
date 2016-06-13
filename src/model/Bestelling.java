package model;

import java.util.LinkedHashMap;

public class Bestelling {
	private long bestelling_id;
	private long klant_id;
	private LinkedHashMap<Artikel, Integer> artikelLijst;

	public Bestelling(){}
	public Bestelling(long klantId, Artikel a1){
		this.klant_id = klantId;
		voegArtikelToe(a1);
	}
	
	public Bestelling(long klantId, Artikel a1, Artikel a2){
		this.klant_id = klantId;
		voegArtikelToe(a1);
		voegArtikelToe(a2);
	}
	
	public Bestelling(long klantId, Artikel a1, Artikel a2, Artikel a3){
		this.klant_id = klantId;
		voegArtikelToe(a1);
		voegArtikelToe(a2);
		voegArtikelToe(a3);
	}
	
	
	public void setBestelling_id(long bestelling_id) {
		this.bestelling_id = bestelling_id;
	}
	public void setKlant_id(long klant_id) {
		this.klant_id = klant_id;
	}
	public void setArtikelLijst(LinkedHashMap<Artikel, Integer> artikelLijst) {
		this.artikelLijst = artikelLijst;
	}
	public long getBestelling_id() {
		return bestelling_id;
	}
	public long getKlant_id() {
		return klant_id;
	}
	public LinkedHashMap<Artikel, Integer> getArtikelLijst() {
		return artikelLijst;
	}
	public String voegArtikelToe(Artikel artikel){
		if(artikelLijst == null)
			artikelLijst = new LinkedHashMap<Artikel, Integer>();
		int size = artikelLijst.keySet().size();
		if(size < 3)
			if(artikelLijst.containsKey(artikel))
				artikelLijst.put(artikel, artikelLijst.get(artikel) + 1);
			else
				artikelLijst.put(artikel, 1);	
		else
			return "Te veel artikelen:" + size;
		return "Artikelen:" + size;
	}
	
	public String verwijderArtikel(Artikel artikel){
		if(artikelLijst.containsKey(artikel)){
			int waarde = artikelLijst.get(artikel) - 1;
			if(waarde > 0) artikelLijst.put(artikel, waarde - 1);
			else artikelLijst.remove(artikel);
			return "Artikel verwijderd:" + artikel.getArtikel_naam();
		}else
			return "Artikel niet gevonden:" + artikel.getArtikel_naam();
	}
}
