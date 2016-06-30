package model;

import java.util.ArrayList;

public class Bestelling {
	private long bestelling_id;
	private long klant_id;
	private String datumAanmaak;
	private boolean bestellingActief = true;
	private ArrayList<Artikel> artikelLijst;

	public Bestelling(){
		artikelLijst = new ArrayList<Artikel>();
	}
	
	public Bestelling(long bestelling_id, long klant_id, ArrayList<Artikel> artikelLijst, String datumAanmaak){
		this.bestelling_id = bestelling_id;
		this.klant_id = klant_id;
		this.artikelLijst = artikelLijst;
		this.datumAanmaak = datumAanmaak;
		bestellingActief = true;
	}

	public Bestelling(long bestelling_id, long klant_id, ArrayList<Artikel> artikelLijst){
		this.bestelling_id = bestelling_id;
		this.klant_id = klant_id;
		this.artikelLijst = artikelLijst;
		bestellingActief = true;
	}

	public void setBestelling_id(long bestelling_id) {
		this.bestelling_id = bestelling_id;
	}
	public void setKlant_id(long klant_id) {
		this.klant_id = klant_id;
	}
	public void setArtikelLijst(ArrayList<Artikel> artikelLijst) {
		this.artikelLijst = artikelLijst;
	}
	public long getBestelling_id() {
		return bestelling_id;
	}
	public long getKlant_id() {
		return klant_id;
	}
	public ArrayList<Artikel> getArtikelLijst() {
		return artikelLijst;
	}
	
	public void voegArtikelToe(Artikel artikel){
		if(artikelLijst == null)
			artikelLijst = new ArrayList<Artikel>();
		artikelLijst.add(artikel);
	}
	
	public void verwijderArtikel(Artikel artikel){
		if(artikelLijst.contains(artikel))
			artikelLijst.remove(artikel);
	}

	public String getDatumAanmaak() {
		return datumAanmaak;
	}

	public void setDatumAanmaak(String datumAanmaak) {
		this.datumAanmaak = datumAanmaak;
	}

	public boolean getBestellingActief() {
		return bestellingActief;
	}

	public void setBestellingActief(boolean bestellingActief) {
		this.bestellingActief = bestellingActief;
	}
}
