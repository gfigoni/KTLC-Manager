package controllers;

import java.util.Date;
import java.util.Map;
import models.Player;

/**
 *
 * @author gehef
 */
public class Players extends CRUD {

    /**
     * 
     * @param players
     * @throws Exception 
     */
    public static void addPlayers(Map<String, String> player, String url, Date date, Integer number) throws Exception {
        for (String login : player.keySet()) {
            String name = player.get(login);
            new Player(login, name).save();
        }

        KTLCEditions.importKTLC(number, url, date);

    }
}
