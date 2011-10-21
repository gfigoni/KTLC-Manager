package models;

/**
 * Exception levée en cas de requête d'un joueur inexistant
 * 
 * @author gehef
 */
public class UnknownPlayerException extends Exception {
    private String login;

    public UnknownPlayerException(String login) {
        super("Joueur inconnu : " + login);
        this.login = login;
    }

    public String getLogin() {
        return login;
    }
    
}
