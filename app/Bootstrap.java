
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Map;
import models.KTLCEdition;
import models.Player;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

// à décommenter pour les tests, afin d'initialiser l'application avec un jeu de données
//@OnApplicationStart
public class Bootstrap extends Job {

    @Override
    public void doJob() throws Exception {

//        System.setProperty("http.proxyHost", "localhost");
//        System.setProperty("http.proxyPort", "5865");

        Fixtures.deleteDatabase();
        if (Player.count() == 0) {
            importKTLC(1, new Date(111, 7, 25));
            importKTLC(2, new Date(111, 8, 1));
            importKTLC(3, new Date(111, 8, 15));
            importKTLC(4, new Date(111, 8, 22));
//            importKTLC(5, new Date(111, 8, 29));
//            importKTLC(6, new Date(111, 9, 13));
        }
    }

    private void importKTLC(int num, Date date) throws Exception {
        URL u = new URL("http://www.lsd-team.net/ktlc2/ktlc" + num + ".txt");

        URLConnection con = u.openConnection();
        con.connect();
        InputStream is = con.getInputStream();
        Reader r = null;
        Map<String, String> missingPlayers;
        try {
            r = new BufferedReader(new InputStreamReader(is));
            missingPlayers = KTLCEdition.checkPlayers(r);
        } finally {
            if (r != null) {
                r.close();
            }
        }


        for (String login : missingPlayers.keySet()) {
            Player p = new Player(missingPlayers.get(login)).save();
            p.addLogin(login);
        }

        con = u.openConnection();
        con.connect();
        is = con.getInputStream();
        try {
            r = new BufferedReader(new InputStreamReader(is));
            KTLCEdition.createKTLCEdition(num, date, r);
        } finally {
            if (r != null) {
                r.close();
            }
        }


    }
}