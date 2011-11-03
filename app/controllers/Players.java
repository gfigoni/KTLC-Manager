package controllers;

import java.util.Date;
import java.util.Map;
import models.Login;
import models.Player;
import play.mvc.With;

/**
 *
 * @author gehef
 */
@With(Secure.class)
public class Players extends CRUD {

    /**
     * 
     * @param players
     * @throws Exception 
     */
    public static void addPlayers(Map<String, String> player, String url, Date date, Integer number) throws Exception {
        for (String login : player.keySet()) {
            String name = player.get(login);
            Player p = new Player(name).save();
            new Login(login, p).save();
        }

        KTLCEditions.importKTLC(number, url, date);

    }
}
