package model;

import java.util.ArrayList;
import java.util.Iterator;

import annotations.Column;
import annotations.Entity;
import annotations.Id;

@Entity("Bestelling")
public class Bestelling {

	@Id
	@Column(values = "bestellingId")
	private long bestellingId;

	@Column(values = "klantId")
	private long klantId;

	@Column(values = "bestelingActief")
	private boolean bestellingActief = true;

	@Column(values = "datumAanmaak")
	private String datumAanmaak;

	private ArrayList<Artikel> artikelLijst;

	public Bestelling(){
		artikelLijst = new ArrayList<Artikel>();
	}

	public Bestelling(long bestellingId, long klantId, ArrayList<Artikel> artikelLijst, String datumAanmaak){
		this.bestellingId = bestellingId;
		this.klantId = klantId;
		this.artikelLijst = artikelLijst;
		this.datumAanmaak = datumAanmaak;
		bestellingActief = true;
	}

	public Bestelling(long bestellingId, long klantId, ArrayList<Artikel> artikelLijst){
		this.bestellingId = bestellingId;
		this.klantId = klantId;
		this.artikelLijst = artikelLijst;
		bestellingActief = true;
	}

	public void setBestellingId(long bestellingId) {
		this.bestellingId = bestellingId;
	}
	public void setKlantId(long klantId) {
		this.klantId = klantId;
	}
	public void setArtikelLijst(ArrayList<Artikel> artikelLijst) {
		this.artikelLijst = artikelLijst;
	}
	public long getBestellingId() {
		return bestellingId;
	}
	public long getKlantId() {
		return klantId;
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

	@Override
	public String toString() {
		StringBuilder string = new StringBuilder("BESTELLING: " + bestellingId + " van klant " + klantId +
				" status actief = " + bestellingActief + "\n");

		Iterator<Artikel> artikelen = getArtikelLijst().iterator();

		while (artikelen.hasNext()) {
			Artikel artikel = artikelen.next();
			string.append("\t\t" + artikel + "\n");
		}

		return string.toString();
	}
}