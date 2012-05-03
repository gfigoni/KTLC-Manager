package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import play.data.validation.Required;
import play.db.jpa.GenericModel;

/**
 * Trackmania Map
 * @author gehef
 */
@Entity
public class TMMap extends GenericModel {
    
    @Id
    public String id;

    @Required
    public String name;
    
    @ManyToOne
    public Login login;
    
    @Required
    @Enumerated(EnumType.STRING)
    public TMEnvironment environment;

    public TMMap(String id, String name, Login login, TMEnvironment env) {
        super();
        this.id = id;
        this.name = name;
        this.login = login;
        this.environment = env;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    public static List<TMMap> findByPlayer(Player player) {
    	List<TMMap> maps = new ArrayList<TMMap>();
        // On itère sur tous les logins du joueur pour récupérer les maps
        for (Login login : player.logins) {
            List<TMMap> m = TMMap.find("byLogin", login).fetch();
            maps.addAll(m);
        }
        return maps;
    }
}
