package controllers;

import java.util.Date;
import models.Player;
import play.mvc.With;

/**
 *
 * @author gehef
 */
@With(Secure.class)
public class Logins extends CRUD {

    /**
     * Crée tous les joueurs déclarés dans le formulaire, ou rattache les logins
     * à des joueurs existants selon le cas.
     */
    public static void addLogins(String url, Date date, Integer number) throws Exception {
        // Liste des logins à traiter
        String[] logins = params.getAll("logins");

        // Pour chaque login, on détermine s'il faut créer un nouveau joueur ou
        // le rattacher à un joueur existant
        for (String login : logins) {
            String existingPlayerId = params.get("existingPlayer." + login);
            String newPlayerName = params.get("newPlayer." + login);
            Player p;
            if (existingPlayerId == null || ("".equals(existingPlayerId.trim()))) {
                // Si aucun joueur existant n'est déclaré, on en crée un nouveau
                p = new Player(newPlayerName).save();
            } else {
                // Sinon on récupère le joueur existant
                p = Player.findById(Long.parseLong(existingPlayerId));
            }
            // Dans tous les cas, on rattache le login au joueur
            p.addLogin(login);
        }

        KTLCEditions.importKTLC(number, url, date);
    }
}
