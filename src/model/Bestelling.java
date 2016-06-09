package model;

import java.util.LinkedHashMap;

public class Bestelling {
	private long bestelling_id;
	private long klant_id;
	private LinkedHashMap<Artikel, Integer> artikelLijst;

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
}
