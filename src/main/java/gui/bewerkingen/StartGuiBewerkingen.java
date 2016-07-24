package gui.bewerkingen;

public class StartGuiBewerkingen {

	public boolean controleerGegevens(String inlognaam, String wachtwoord){
		if(inlognaam.equals("Harrie") && wachtwoord.equals("1234")){
			return true;
		}else{
			return false;
		}
	}
}
