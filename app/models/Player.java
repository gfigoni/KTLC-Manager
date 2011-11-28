package models;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * Trackmania Player
 * @author gehef
 */
@Entity
public class Player extends Model {

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
    public List<Login> logins = new ArrayList<Login>();

    @Required
    public String name;

    public Player(String name) {
        super();
        this.name = name;                
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    public Player addLogin(String login) {
        Login l = new Login(login, this).save();
        this.logins.add(l);
        this.save();
        return this;
    }
    
    public static Player findByLogin(String login) {
        return Player.find("select p from Player p, Login l where l.name = ? and l.player = p", login).first();
    }
}