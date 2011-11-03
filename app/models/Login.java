package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import play.data.validation.Required;
import play.db.jpa.GenericModel;

/**
 * Trackmania player login
 * 
 * @author gehef
 */
@Entity
public class Login extends GenericModel {
    @Id
    public String id;
    
    @Required
    @ManyToOne
    public Player player;
    
    public Login(String id, Player player) {
        super();
        this.id = id;
        this.player = player;
    }   
    
    @Override
    public String toString() {
        return id;
    }
}
