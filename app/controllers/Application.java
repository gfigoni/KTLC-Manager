package controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import models.KTLCEdition;
import models.KTLCResult;
import models.Login;
import models.Player;
import play.mvc.Controller;

public class Application extends Controller {

    public static void index() {
        KTLCEdition ktlc = KTLCEdition.find("order by date desc").first();
        render(ktlc);
    }

    public static void player(String loginName) {
        // Récupération du joueur à partir du login
        Player player = Player.findByLogin(loginName);

        // On itère sur tous les logins du joueur pour récupérer les résultats
        List<KTLCResult> results = new ArrayList<KTLCResult>();
        for (Login login : player.logins) {
            List<KTLCResult> r = KTLCResult.findByLogin(login);
            results.addAll(r);
        }

        // tri par date de KTLC
        Collections.sort(results, new Comparator<KTLCResult>() {

            public int compare(KTLCResult o1, KTLCResult o2) {
                return o2.ktlc.date.compareTo(o1.ktlc.date);
            }
        });
        render(player, results);
    }

    public static void players() {
        List<Player> players = Player.find("order by name asc").fetch();
        render(players);
    }

    public static void ktlc(Integer number) {
        KTLCEdition ktlc = KTLCEdition.findByNumber(number);
        render(ktlc);
    }

    public static void ktlcs() {
        List<KTLCEdition> ktlcs = KTLCEdition.find("order by date desc").fetch();
        render(ktlcs);
    }
}