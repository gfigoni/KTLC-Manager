package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class KTLCRaceResult extends Model implements Comparable<KTLCRaceResult>  {

    @Required
    @ManyToOne
    public Login login;
    
    @Required
    @ManyToOne
    public KTLCRace race;

    @Required
    public Integer rank;
    
    @Required
    public Integer score;
    
    @Required
    public Integer roundsCount;

    public KTLCRaceResult(KTLCRace race, Login login, Integer rank, Integer score, Integer roundsCount) {
        super();
        this.race = race;
        this.login = login;
        this.rank = rank;
        this.score = score;
        this.roundsCount = roundsCount;
    }
    
    @Override
    public int compareTo(KTLCRaceResult r) {
        return r.rank - rank;
    }
    
    public static List<KTLCRaceResult> findByPlayer(Player player) {
    	List<KTLCRaceResult> raceResults = new ArrayList<KTLCRaceResult>();
        // On itère sur tous les logins du joueur pour récupérer les résultats
        for (Login login : player.logins) {
            List<KTLCRaceResult> r = KTLCRaceResult.find("byLogin", login).fetch();
            raceResults.addAll(r);
        }
        return raceResults;
    }
}
