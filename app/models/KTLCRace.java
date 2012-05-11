package models;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class KTLCRace extends Model {

    @Required
    @ManyToOne
    public KTLCEdition ktlc;

    @Required
    @ManyToOne
    public TMMap map;
    
    @OneToMany(mappedBy = "race", cascade = CascadeType.ALL)
    public List<KTLCRaceResult> results;

    public KTLCRace(KTLCEdition ktlc, TMMap map) {
        super();
        this.ktlc = ktlc;
        this.map = map;
    }
    
    public List<KTLCRaceResult> findResults() {
        return KTLCRaceResult.find("byRace", this).fetch();
    }
    
    public KTLCRaceResult findResult(Login login) {
        return KTLCRaceResult.find("race = ? and login = ?", this, login).first();
    }
    
    public static KTLCRace findByMap(TMMap map) {
    	return KTLCRace.find("map = ?", map).first();
    }
}
