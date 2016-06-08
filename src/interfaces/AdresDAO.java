package interfaces;

import model.Adres;

/**
 * Created by Milan_Verheij on 08-06-16.
 */
public interface AdresDAO {
    void updateAdres(Long klant_id, Adres adresgegevens);
}
