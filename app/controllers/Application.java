package controllers;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import models.KTLCEdition;
import models.KTLCPlayerResult;
import models.Player;
import play.mvc.Controller;

public class Application extends Controller {

    public static void index() {
        List<KTLCEdition> ktlcs = KTLCEdition.find("order by date desc").fetch();
        render(ktlcs);
    }

    public static void player(String login) {
        Player player = Player.findByLogin(login);
        List<KTLCPlayerResult> results = KTLCPlayerResult.find("byPlayer", player).fetch();
        // tri par date de KTLC
        Collections.sort(results, new Comparator<KTLCPlayerResult>() {

            public int compare(KTLCPlayerResult o1, KTLCPlayerResult o2) {
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
        List<KTLCEdition> ktlcs = KTLCEdition.find("order by date asc").fetch();
        render(ktlcs);
    }
}