package interfaces;

import exceptions.RSVIERException;

import java.sql.Connection;

/**
 * Created by Milan_Verheij on 19-06-16.
 *
 * Interface voor generieke verkrijging van een connectie afhankelijk van
 * connectie pool.
 */
public interface VerkrijgConnectie {
   Connection verkrijgConnectie() throws RSVIERException ;
}
