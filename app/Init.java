
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Map;
import models.KTLCEdition;
import models.Player;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

/**
 * Classe d'initialisation de la base de données à partir des logs FAST
 * hébergées sur le serveur LSD.
 *
 * /!\ A N'UTILISER QU'UNE SEULE FOIS
 *
 * @author gehef
 */
//@OnApplicationStart
public class Init extends Job {

    @Override
    public void doJob() throws Exception {
        if (KTLCEdition.count() == 0) {
            String urlFormat;
            
            // KTLC
            urlFormat = "http://www.lsd-team.net/ktlc/ktlc%s.txt";
            importKTLC(137, 2009, 7, 2, urlFormat);
            importKTLC(138, 2009, 7, 9, urlFormat);
            importKTLC(139, 2009, 7, 16, urlFormat);
            importKTLC(140, 2009, 7, 23, urlFormat);
            importKTLC(141, 2009, 7, 30, urlFormat);
            importKTLC(142, 2009, 8, 6, urlFormat);
            importKTLC(143, 2009, 8, 13, urlFormat);
            importKTLC(144, 2009, 8, 20, urlFormat);
            importKTLC(145, 2009, 8, 27, urlFormat);
            importKTLC(146, 2009, 9, 3, urlFormat);
            importKTLC(147, 2009, 9, 17, urlFormat);
            importKTLC(148, 2009, 9, 24, urlFormat);
            importKTLC(149, 2009, 10, 1, urlFormat);
            importKTLC(150, 2009, 10, 8, urlFormat);
            importKTLC(151, 2009, 10, 15, urlFormat);
            importKTLC(152, 2009, 10, 22, urlFormat);
            importKTLC(153, 2009, 10, 29, urlFormat);
            importKTLC(154, 2009, 11, 5, urlFormat);
            importKTLC(155, 2009, 11, 12, urlFormat);
            importKTLC(156, 2009, 11, 19, urlFormat);
            importKTLC(157, 2009, 11, 26, urlFormat);
            importKTLC(158, 2009, 12, 10, urlFormat);
            importKTLC(159, 2009, 12, 17, urlFormat);
            importKTLC(160, 2010, 1, 7, urlFormat);
            importKTLC(161, 2010, 1, 14, urlFormat);
            importKTLC(162, 2010, 1, 21, urlFormat);
            importKTLC(163, 2010, 1, 28, urlFormat);
            importKTLC(164, 2010, 2, 4, urlFormat);
            importKTLC(165, 2010, 2, 11, urlFormat);
            importKTLC(166, 2010, 2, 18, urlFormat);
            importKTLC(167, 2010, 2, 25, urlFormat);
            importKTLC(168, 2010, 3, 4, urlFormat);
            importKTLC(169, 2010, 3, 11, urlFormat);
            importKTLC(170, 2010, 3, 18, urlFormat);
            importKTLC(171, 2010, 3, 25, urlFormat);
            importKTLC(172, 2010, 4, 1, urlFormat);
            importKTLC(173, 2010, 4, 8, urlFormat);
            importKTLC(174, 2010, 4, 15, urlFormat);
            importKTLC(175, 2010, 4, 22, urlFormat);
            importKTLC(176, 2010, 4, 29, urlFormat);
            importKTLC(177, 2010, 5, 6, urlFormat);
            importKTLC(178, 2010, 5, 13, urlFormat);
            importKTLC(179, 2010, 5, 20, urlFormat);
            importKTLC(180, 2010, 5, 27, urlFormat);
            importKTLC(181, 2010, 6, 3, urlFormat);
            importKTLC(182, 2010, 6, 10, urlFormat);
            importKTLC(183, 2010, 6, 24, urlFormat);
            importKTLC(184, 2010, 7, 22, urlFormat);
            importKTLC(185, 2010, 8, 12, urlFormat);
            importKTLC(186, 2010, 9, 2, urlFormat);
            importKTLC(187, 2010, 9, 9, urlFormat);
            importKTLC(188, 2010, 9, 16, urlFormat);
            importKTLC(189, 2010, 9, 23, urlFormat);
            importKTLC(190, 2010, 9, 30, urlFormat);
            importKTLC(191, 2010, 10, 14, urlFormat);
            importKTLC(192, 2010, 10, 21, urlFormat);
            importKTLC(193, 2010, 10, 28, urlFormat);
            importKTLC(194, 2010, 11, 11, urlFormat);
            importKTLC(195, 2010, 11, 25, urlFormat);
            importKTLC(196, 2010, 12, 9, urlFormat);
            importKTLC(197, 2010, 12, 16, urlFormat);
            importKTLC(198, 2011, 1, 13, urlFormat);
            importKTLC(199, 2011, 1, 20, urlFormat);
            importKTLC(200, 2011, 3, 17, urlFormat);
            importKTLC(201, 2011, 2, 10, urlFormat);
            importKTLC(202, 2011, 2, 17, urlFormat);
            importKTLC(203, 2011, 2, 24, urlFormat);
            importKTLC(204, 2011, 3, 3, urlFormat);
            importKTLC(205, 2011, 3, 10, urlFormat);
            importKTLC(206, 2011, 3, 24, urlFormat);
            importKTLC(207, 2011, 3, 31, urlFormat);
            importKTLC(208, 2011, 4, 14, urlFormat);
            importKTLC(209, 2011, 4, 21, urlFormat);
            importKTLC(210, 2011, 5, 12, urlFormat);
            importKTLC(211, 2011, 5, 19, urlFormat);
            importKTLC(212, 2011, 6, 16, urlFormat);
            importKTLC(213, 2011, 6, 23, urlFormat);
            importKTLC(214, 2011, 6, 30, urlFormat);
            
            // KTLC²
            urlFormat = "http://www.lsd-team.net/ktlc2/ktlc%s.txt";
            importKTLC(215, 2011, 8, 25, urlFormat);
            importKTLC(216, 2011, 9, 1, urlFormat);
            importKTLC(217, 2011, 9, 15, urlFormat);
            importKTLC(218, 2011, 9, 22, urlFormat);
            importKTLC(219, 2011, 9, 29, urlFormat);
            importKTLC(220, 2011, 10, 13, urlFormat);
            importKTLC(221, 2011, 10, 20, urlFormat);
            importKTLC(222, 2011, 10, 27, urlFormat);
            importKTLC(223, 2011, 11, 10, urlFormat);
            importKTLC(224, 2011, 11, 17, urlFormat);
            importKTLC(225, 2011, 11, 24, urlFormat);
        }
    }

    private void importKTLC(int num, int year, int month, int day, String urlFormat) throws Exception {
        Formatter f = new Formatter();
        f.format(urlFormat, num);
        String url = f.out().toString();
        URL u = new URL(url);

        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day);
        Date date = cal.getTime();

        URLConnection con = u.openConnection();
        con.connect();
        InputStream is = con.getInputStream();
        Reader r = null;
        Map<String, String> missingPlayers;
        try {
            r = new BufferedReader(new InputStreamReader(is));
            missingPlayers = KTLCEdition.checkLogins(r);
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