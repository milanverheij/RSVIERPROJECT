package model;

import java.math.BigDecimal;

/*
 * Created by Douwe_Jongeneel on 06-06-16.
 * 
 * Dit is de Artikel Pojo + GS zodat er Artikel objecten
 * in een LinkedHasMap kunnen worden opgeslagen in de database.
 */

public class Artikel implements Comparable{
	//Data field
	private int artikelId = 0;
	private String artikelNaam;
	private BigDecimal artikelPrijs;//TODO
	private int prijsId;
	private String datumAanmaak;
	private int verwachteLevertijd;
	private boolean inAssortiment;
	
	//Constructors
	public Artikel() {
	}
	public Artikel(String artikelNaam, BigDecimal artikelPrijs, 
			String datumAanmaak, int verwachteLevertijd, boolean inAssortiment) {
		this.artikelNaam = artikelNaam;
		this.artikelPrijs = artikelPrijs;
		this.datumAanmaak = datumAanmaak;
		this.verwachteLevertijd = verwachteLevertijd;
		this.inAssortiment = inAssortiment;
	}
	
	//Getters and Setters
	public int getArtikelId() {
		return artikelId;
	}
	public String getArtikelNaam() {
		return artikelNaam;
	}
	public BigDecimal getArtikelPrijs() {
		return artikelPrijs;
	}
	public int getPrijsId() {
		return prijsId;
	}
	public String getDatumAanmaak() {
		return datumAanmaak;
	}
	public int getVerwachteLevertijd() {
		return verwachteLevertijd;
	}
	public boolean isInAssortiment() {
		return inAssortiment;
	}

	public void setArtikelId(int artikelId) {
		this.artikelId = artikelId;
	}
	public void setArtikelNaam(String artikelNaam) {
		this.artikelNaam = artikelNaam;
	}
	public void setArtikelPrijs(BigDecimal artikelPrijs) {
		this.artikelPrijs = artikelPrijs;
	}
	public void setPrijsId(int prijs_id) {
		this.prijsId = prijs_id;
	}
	public void setDatumAanmaak(String datumAanmaak) {
		this.datumAanmaak = datumAanmaak;
	}
	public void setVerwachteLevertijd(int verwachteLevertijd) {
		this.verwachteLevertijd = verwachteLevertijd;
	}
	public void setInAssortiment(boolean inAssortiment) {
		this.inAssortiment = inAssortiment;
	}
	
	// Methodes die overschreven worden
	@Override
	public int hashCode(){ 
		return artikelNaam.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return artikelId == ((Artikel)o).getArtikelId();
	}

	@Override
	public String toString(){
		return "" + artikelId + " " + artikelNaam + " $" + artikelPrijs.toPlainString(); //To string methode met BigDecimal nog testen!!
	}

	@Override
	public int compareTo(Object o) {
		if (this.artikelId == ((Artikel)o).getArtikelId()) {
			return 0;
		}
		else if (this.artikelId > ((Artikel)o).getArtikelId()) {
			return 1;
		}
		else {
			return -1;
		}
	}
}
