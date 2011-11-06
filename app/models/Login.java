package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import play.data.validation.Required;
import play.db.jpa.Model;


/**
 * Trackmania player login
 * 
 * @author gehef
 */
@Entity
public class Login extends Model {
    @Required
    public String name;
    
    @Required
    @ManyToOne
    public Player player;
    
    public Login(String name) {
        super();
        this.name = name;
    }   

    public Login(String name, Player player) {
        super();
        this.name = name;
        this.player = player;
    }   
    
    @Override
    public String toString() {
        return name;
    }
    
    public static Login findByName(String name) {
        return find("byName", name).first();
    }
}
