package model;

/*
 * Created by Douwe_Jongeneel on 06-06-16.
 * 
 * Dit is de Artikel Pojo + GS zodat er Artikel objecten
 * in een LinkedHasMap kunnen worden opgeslagen in de database.
 */

public class Artikel {
	//Data field
	private int artikel_id;
	private String artikel_naam;
	private double artikel_prijs;
	
	//Constructors
	public Artikel() {
	}
	public Artikel(int artikel_id, String artikel_naam, double artikel_prijs) {
		this.artikel_id = artikel_id;
		this.artikel_naam = artikel_naam;
		this.artikel_prijs = artikel_prijs;
	}
	
	//Getters and Setters
	public int getArtikel_id() {
		return artikel_id;
	}
	public String getArtikel_naam() {
		return artikel_naam;
	}
	public double getArtikel_prijs() {
		return artikel_prijs;
	}
	
	//Hebben we alle setters nodig of laten we setnaam en setprijs weg?
	public void setArtikel_id(int artikel_id) {
		this.artikel_id = artikel_id;
	}
	public void setArtikel_naam(String artikel_naam) {
		this.artikel_naam = artikel_naam;
	}
	public void setArtikel_prijs(double artikel_prijs) {
		this.artikel_prijs = artikel_prijs;
	}
	
	//Voor het vergelijken van artikelen
	//Ik ga er van uit dat een artikel uniek is als de naam en prijs combinatie niet eerder voorkomt
	@Override
	public int hashCode(){ 
		return artikel_naam.hashCode() + (int)(artikel_prijs * 100);
	}

	@Override
	public boolean equals(Object o) {
		return (artikel_naam.equals(((Artikel)o).getArtikel_naam()) && artikel_prijs == ((Artikel)o).getArtikel_prijs());
	}
}
