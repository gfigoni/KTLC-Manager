package models;

import javax.persistence.Entity;
import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * Trackmania Player
 * @author gehef
 */
@Entity
public class Player extends Model {

    @Required
    public String login;

    @Required
    public String name;

    public Player(String login, String name) {
        super();
        this.login = login;
        this.name = name;                
    }
    
    @Override
    public String toString() {
        return (name == null) ? login : name;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Player) {
            Player p = (Player) obj;
            return this.id.equals(p.id);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
    
    public static Player findByLogin(String login) {
        return find("byLogin", login).first();
    }
}