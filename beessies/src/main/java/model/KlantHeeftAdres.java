package model;

import annotations.Column;
import annotations.Entity;

/**
 * Created by Milan_Verheij on 08-07-16.
 *
 */
@Entity("klantHeeftAdres")
public class KlantHeeftAdres {

    @Column(values = "adresIdAdres")
    private long adresIdAdres;

    @Column(values = "klantIdKlant")
    private long klantIdKlant;

    public long getAdresIdAdres() {
        return adresIdAdres;
    }

    public void setAdresIdAdres(long adresIdAdres) {
        this.adresIdAdres = adresIdAdres;
    }

    public long getKlantIdKlant() {
        return klantIdKlant;
    }

    public void setKlantIdKlant(long klantIdKlant) {
        this.klantIdKlant = klantIdKlant;
    }
}
