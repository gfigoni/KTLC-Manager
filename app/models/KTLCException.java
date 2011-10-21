package models;

/**
 *
 * @author gehef
 */
public class KTLCException extends Exception {

    public KTLCException(Throwable cause) {
        super(cause);
    }

    public KTLCException(String message, Throwable cause) {
        super(message, cause);
    }

    public KTLCException(String message) {
        super(message);
    }

    public KTLCException() {
    }
    
}
