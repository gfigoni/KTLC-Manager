package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import play.data.validation.Required;
import play.db.jpa.Model;


@Entity
public class KTLCPlayerResult extends Model {

    @Required
    @ManyToOne
    public Player player;

    @Required
    @ManyToOne
    public KTLCEdition ktlc;

    @Required
    public Integer rank;

    @Required
    public Double rankAvg;

    @Required
    public Integer score;
    
    @Required
    public Integer nbRaces;
    
    public KTLCPlayerResult(KTLCEdition ktlc, Player player) {
        super();
        this.ktlc = ktlc;
        this.player = player;
    }
}
