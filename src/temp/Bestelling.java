package model;

import java.util.LinkedHashMap;

public class Bestelling {
	long bestelling_id;
	long klant_id;
	LinkedHashMap<Artikel, Integer> artikel;

	public void setBestelling_id(long bestelling_id) {
		this.bestelling_id = bestelling_id;
	}
	public void setKlant_id(long klant_id) {
		this.klant_id = klant_id;
	}
	public void setArtikel(LinkedHashMap<Artikel, Integer> artikel) {
		this.artikel = artikel;
	}
	public long getBestelling_id() {
		return bestelling_id;
	}
	public long getKlant_id() {
		return klant_id;
	}
	public LinkedHashMap<Artikel, Integer> getArtikel() {
		return artikel;
	}
}
