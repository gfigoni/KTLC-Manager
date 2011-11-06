package models;

/**
 * Exception levée en cas de requête d'un login inexistant
 * 
 * @author gehef
 */
public class UnknownLoginException extends Exception {
    private String login;

    public UnknownLoginException(String login) {
        super("Login inconnu : " + login);
        this.login = login;
    }

    public String getLogin() {
        return login;
    }
    
}
