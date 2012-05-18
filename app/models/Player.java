package models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.NoResultException;
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
    
    public boolean isPlayer() {
    	return KTLCResult.findByPlayer(this).size() > 0;
    }
    
    public boolean isMapper() {
    	return TMMap.findByPlayer(this).size() > 0;
    }
    
    public static void filterByPlayer(Collection<Player> c) {
        for (Iterator<Player> it = c.iterator(); it.hasNext(); )
            if (!(it.next().isPlayer()))
                it.remove();
    }
    
    public static void filterByMapper(Collection<Player> c) {
        for (Iterator<Player> it = c.iterator(); it.hasNext(); )
            if (!(it.next().isMapper()))
                it.remove();
    }
    
    public static void filterPlayersByPercentageParticipation(Collection<Player> c, int percentage) {
    	int numberLimit = (int)(KTLCEdition.findAll().size() * (percentage / 100.0));
    	
        for (Iterator<Player> it = c.iterator(); it.hasNext(); ) {
        	Player player = it.next();
        	int count = KTLCResult.findByPlayer(player).size();
        	if (count < numberLimit) {
                it.remove();
        	}
        }
    }
    
    public static void filterMappersByPercentageParticipation(Collection<Player> c, int percentage) {
    	int numberLimit = (int)(TMMap.findAll().size() * (percentage / 100.0));
    	
        for (Iterator<Player> it = c.iterator(); it.hasNext(); ) {
        	Player player = it.next();
        	int count = TMMap.findByPlayer(player).size();
        	if (count < numberLimit) {
                it.remove();
        	}
        }
    }
}