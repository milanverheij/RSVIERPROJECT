package model;

import java.math.BigDecimal;

import annotations.Column;
import annotations.Id;
import annotations.Entity;

/*
 * Created by Douwe_Jongeneel on 06-06-16.
 *
 * Dit is de Artikel Pojo + GS zodat er Artikel objecten
 * in een LinkedHasMap kunnen worden opgeslagen in de database.
 */
@Entity("artikel")
public class Artikel implements Comparable<Artikel>{
	//Data field
	@Id
	@Column(values = "artikel_id")
	private int artikelId = 0;
	@Column(values = "omschrijving")
	private String artikelNaam;
	private int aantalBesteld;
	@Column(values = "prijs")
	private BigDecimal artikelPrijs;
	@Id
	@Column(values = "prijs_id")
	private int prijsId;
	@Column(values = "datumAanmaak")
	private String datumAanmaak;
	@Column(values = "verwachteLevertijd")
	private int verwachteLevertijd;
	@Column(values = "inAssortiment")
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
	public int getAantalBesteld() {
		return aantalBesteld;
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
	public void setAantalBesteld(int aantalBesteld) {
		this.aantalBesteld = aantalBesteld;
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
		return "ARTIKEL: " + artikelId + "\t " + artikelNaam + "\t $" + artikelPrijs.toPlainString()
		+ "\t prijs id " + prijsId + "\t " + datumAanmaak + "\t " + verwachteLevertijd + "\t "
		+ inAssortiment;
	}

	@Override
	public int compareTo(Artikel o) {
		if (this.artikelId == o.getArtikelId())
			return 0;
		else if (this.artikelId > o.getArtikelId())
			return 1;
		else
			return -1;
	}
}