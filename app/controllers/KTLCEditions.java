package controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Map;
import models.KTLCEdition;
import models.UnknownPlayerException;
import play.Logger;
import play.data.validation.Min;
import play.data.validation.Required;
import play.mvc.With;

/**
 *
 * @author gehef
 */
@With(Secure.class)
public class KTLCEditions extends CRUD {

    public static void importKTLC(
            @Required @Min(1) Integer number,
            @Required @play.data.validation.URL String url,
            @Required Date date) throws Exception {

        if (validation.hasErrors()) {
            params.flash(); // add http parameters to the flash scope
            validation.keep(); // keep the errors for the next request
            redirect(request.controller + ".list");
        }

        try {
            URL u = new URL(url);
            URLConnection con = u.openConnection();
            con.connect();
            InputStream is = con.getInputStream();
            Reader r = null;
            Map<String, String> players;
            try {
                r = new BufferedReader(new InputStreamReader(is));
                players = KTLCEdition.checkPlayers(r);
            } finally {
                if (r != null) {
                    r.close();
                }
            }
            if (players != null && players.size() > 0) {
                // il manque des joueurs
                render("Players/unknown.html", players, number, date, url);
            } else {
                // tous les joueurs de la log sont déjà identifiés
                con = u.openConnection();
                con.connect();
                is = con.getInputStream();
                try {
                    r = new BufferedReader(new InputStreamReader(is));
                    KTLCEdition.createKTLCEdition(number, date, r);
                    Application.ktlc(number);
                } catch (UnknownPlayerException upe) {
                    // malgré le check, un joueur est inconnu...
                    // remonter un message d'erreur
                } finally {
                    if (r != null) {
                        r.close();
                    }
                }
            }

        } catch (IOException ioe) {
            Logger.error(ioe, "Problem with url: %s", url);
            params.flash(); // add http parameters to the flash scope
            flash.error("Problem with url: %s", url);
            redirect(request.controller + ".list");
        }


    }
}
