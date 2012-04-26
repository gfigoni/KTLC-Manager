package models;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class KTLCResult extends Model implements Comparable<KTLCResult> {

    @Required
    @ManyToOne
    public Login login;
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

    public KTLCResult(KTLCEdition ktlc, Login login) {
        super();
        this.ktlc = ktlc;
        this.login = login;
    }

    public int compareTo(KTLCResult r) {
        int comp = r.score.compareTo(this.score);
        if (comp == 0) {
            comp = this.rankAvg.compareTo(r.rankAvg);
        }
        return comp;
    }

    public static List<KTLCResult> findByLogin(Login login) {
        return find("byLogin", login).fetch();
    }

    public static List<KTLCResult> findByPlayer(Player player) {
        // On itère sur tous les logins du joueur pour récupérer les résultats
        List<KTLCResult> results = new ArrayList<KTLCResult>();
        for (Login login : player.logins) {
            List<KTLCResult> r = KTLCResult.findByLogin(login);
            results.addAll(r);
        }
        return results;
    }

    public static KTLCResult findByKTLCAndPlayer(KTLCEdition ktlc, Player player) {
        KTLCResult result = null;
        for (Login login : player.logins) {
            result = findByKTLCAndLogin(ktlc, login);
            if (result != null) {
                break;
            }
        }
        return result;
    }

    public static KTLCResult findByKTLCAndLogin(KTLCEdition ktlc, Login login) {
        return find("byKtlcAndLogin", ktlc, login).first();
    }
}
