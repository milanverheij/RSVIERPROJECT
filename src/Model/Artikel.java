package model;

public class Artikel {
	int artikel_id;
	String artikel_naam;
	double artikel_prijs;
	public int getArtikel_id() {
		return artikel_id;
	}
	public void setArtikel_id(int artikel_id) {
		this.artikel_id = artikel_id;
	}
	public String getArtikel_naam() {
		return artikel_naam;
	}
	public void setArtikel_naam(String artikel_naam) {
		this.artikel_naam = artikel_naam;
	}
	public double getArtikel_prijs() {
		return artikel_prijs;
	}
	public void setArtikel_prijs(double artikel_prijs) {
		this.artikel_prijs = artikel_prijs;
	}
}
