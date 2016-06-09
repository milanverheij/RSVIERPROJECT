package exceptions;

/**
 * Created by Milan_Verheij on 09-06-16.
 *
 * Algemene exception class om alle foutmeldingen in het programma
 * naar toe te kunnen gooien. Worden in de GUI opgevangen en verwerkt.
 *
 */
public class RSVIERException extends Exception {

    /** Standaard constructor om message mee te geven */
    public RSVIERException(String message) {
        super(message);
    }
}
