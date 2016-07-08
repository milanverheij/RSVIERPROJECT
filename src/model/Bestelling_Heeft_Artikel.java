package model;

import annotations.Column;
import annotations.Entity;

@Entity("bestellingHeeftArtikel")
public class Bestelling_Heeft_Artikel {

	@Column(values = "bestelling_id_best")
	long bestelling_id_best;

	@Column(values = "artikel_id_art")
	long artikel_id_art;

	@Column(values = "prijs_id_prijs")
	long prijs_id_prijs;

	@Column(values = "aantal")
	long aantal;

	public long getBestelling_id_best() {
		return bestelling_id_best;
	}
	public void setBestelling_id_best(long bestelling_id_best) {
		this.bestelling_id_best = bestelling_id_best;
	}
	public long getArtikel_id_art() {
		return artikel_id_art;
	}
	public void setArtikel_id_art(long artikel_id_art) {
		this.artikel_id_art = artikel_id_art;
	}
	public long getPrijs_id_prijs() {
		return prijs_id_prijs;
	}
	public void setPrijs_id_prijs(long prijs_id_prijs) {
		this.prijs_id_prijs = prijs_id_prijs;
	}
	public long getAantal() {
		return aantal;
	}
	public void setAantal(long aantal) {
		this.aantal = aantal;
	}
}
