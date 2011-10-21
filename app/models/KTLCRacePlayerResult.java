package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class KTLCRacePlayerResult extends Model implements Comparable<KTLCRacePlayerResult>  {

    @Required
    @ManyToOne
    public Player player;
    
    @Required
    @ManyToOne
    public KTLCRace race;

    @Required
    public Integer rank;
    
    @Required
    public Integer score;
    
    @Required
    public Integer roundsCount;

    public KTLCRacePlayerResult(KTLCRace race, Player player, Integer rank, Integer score, Integer roundsCount) {
        super();
        this.race = race;
        this.player = player;
        this.rank = rank;
        this.score = score;
        this.roundsCount = roundsCount;
    }
    
    @Override
    public int compareTo(KTLCRacePlayerResult r) {
        return r.rank - rank;
    }
}
