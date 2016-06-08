package model;

import java.util.LinkedHashMap;

public class Bestelling {
	long bestelling_id;
	long klant_id;
	LinkedHashMap<Artikel, Integer> artikelLijst;

	public Bestelling() {}

	public Bestelling(long bestelling_id,
					  long klant_id,
					  LinkedHashMap<Artikel, Integer> artikelLijst) {
		this.bestelling_id = bestelling_id;
		this.klant_id = klant_id;
		this.artikelLijst = artikelLijst;
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
}
