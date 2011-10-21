package models;

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
    public Player author;
    
    @Required
    @Enumerated(EnumType.STRING)
    public TMEnvironment environment;

    public TMMap(String id, String name, Player author, TMEnvironment env) {
        super();
        this.id = id;
        this.name = name;
        this.author = author;
        this.environment = env;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
}
