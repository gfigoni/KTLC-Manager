
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
import play.Logger;
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
            importKTLC(22, 2006, 12, 14, urlFormat);
            importKTLC(23, 2007, 1, 4, urlFormat);
            importKTLC(24, 2007, 1, 11, urlFormat);
            importKTLC(25, 2007, 1, 18, urlFormat);
            importKTLC(26, 2007, 1, 25, urlFormat);
            importKTLC(27, 2007, 2, 1, urlFormat);
            importKTLC(28, 2007, 2, 8, urlFormat);
            importKTLC(29, 2007, 2, 16, urlFormat);
            importKTLC(30, 2007, 2, 22, urlFormat);
            importKTLC(31, 2007, 3, 1, urlFormat);
            importKTLC(32, 2007, 3, 8, urlFormat);
            importKTLC(33, 2007, 3, 15, urlFormat);
            importKTLC(34, 2007, 3, 22, urlFormat);
            importKTLC(35, 2007, 3, 29, urlFormat);
            importKTLC(36, 2007, 4, 5, urlFormat);
            importKTLC(37, 2007, 4, 12, urlFormat);
            importKTLC(38, 2007, 4, 19, urlFormat);
            importKTLC(39, 2007, 4, 26, urlFormat);
            importKTLC(40, 2007, 5, 3, urlFormat);
            importKTLC(41, 2007, 5, 10, urlFormat);
            // pas de log pour la KTLC 42
            //importKTLC(42, 2007, 5, 17, urlFormat);
            importKTLC(43, 2007, 5, 24, urlFormat);
            importKTLC(44, 2007, 5, 31, urlFormat);
            importKTLC(45, 2007, 6, 7, urlFormat);
            importKTLC(46, 2007, 6, 14, urlFormat);
            importKTLC(47, 2007, 6, 20, urlFormat);
            importKTLC(48, 2007, 6, 28, urlFormat);
            importKTLC(49, 2007, 7, 5, urlFormat);
            importKTLC(50, 2007, 7, 12, urlFormat);
            importKTLC(51, 2007, 7, 19, urlFormat);
            importKTLC(52, 2007, 7, 26, urlFormat);
            importKTLC(53, 2007, 8, 2, urlFormat);
            importKTLC(54, 2007, 8, 9, urlFormat);
            importKTLC(55, 2007, 8, 16, urlFormat);
            importKTLC(56, 2007, 8, 23, urlFormat);
            importKTLC(57, 2007, 8, 30, urlFormat);
            importKTLC(58, 2007, 9, 13, urlFormat);
            importKTLC(59, 2007, 9, 20, urlFormat);
            importKTLC(60, 2007, 9, 27, urlFormat);
            importKTLC(61, 2007, 10, 4, urlFormat);
            importKTLC(62, 2007, 10, 11, urlFormat);
            importKTLC(63, 2007, 10, 18, urlFormat);
            importKTLC(64, 2007, 10, 25, urlFormat);
            importKTLC(65, 2007, 11, 1, urlFormat);
            importKTLC(66, 2007, 11, 8, urlFormat);
            importKTLC(67, 2007, 11, 15, urlFormat);
            importKTLC(68, 2007, 11, 22, urlFormat);
            importKTLC(69, 2007, 11, 29, urlFormat);
            importKTLC(70, 2007, 12, 6, urlFormat);
            importKTLC(71, 2007, 12, 13, urlFormat);
            importKTLC(72, 2007, 12, 20, urlFormat);
            importKTLC(73, 2008, 1, 3, urlFormat);
            importKTLC(74, 2008, 1, 10, urlFormat);
            importKTLC(75, 2008, 1, 17, urlFormat);
            importKTLC(76, 2008, 1, 24, urlFormat);
            importKTLC(77, 2008, 1, 31, urlFormat);
            importKTLC(78, 2008, 2, 7, urlFormat);
            importKTLC(79, 2008, 2, 14, urlFormat);
            importKTLC(80, 2008, 2, 21, urlFormat);
            importKTLC(81, 2008, 2, 28, urlFormat);
            importKTLC(82, 2008, 3, 6, urlFormat);
            importKTLC(83, 2008, 3, 13, urlFormat);
            importKTLC(84, 2008, 3, 20, urlFormat);
            importKTLC(85, 2008, 4, 3, urlFormat);
            importKTLC(86, 2008, 4, 10, urlFormat);
            importKTLC(87, 2008, 4, 24, urlFormat);
            importKTLC(88, 2008, 5, 1, urlFormat);
            importKTLC(89, 2008, 5, 15, urlFormat);
            importKTLC(90, 2008, 5, 29, urlFormat);
            importKTLC(91, 2008, 6, 12, urlFormat);
            importKTLC(92, 2008, 6, 19, urlFormat);
            importKTLC(93, 2008, 7, 3, urlFormat);
            importKTLC(94, 2008, 7, 10, urlFormat);
            importKTLC(95, 2008, 7, 17, urlFormat);
            importKTLC(96, 2008, 7, 24, urlFormat);
            importKTLC(97, 2008, 7, 31, urlFormat);
            importKTLC(98, 2008, 8, 7, urlFormat);
            importKTLC(99, 2008, 8, 14, urlFormat);
            importKTLC(100, 2008, 8, 21, urlFormat);
            importKTLC(101, 2008, 8, 28, urlFormat);
            importKTLC(102, 2008, 9, 4, urlFormat);
            importKTLC(103, 2008, 9, 11, urlFormat);
            importKTLC(104, 2008, 9, 18, urlFormat);
            importKTLC(105, 2008, 9, 25, urlFormat);
            importKTLC(106, 2008, 10, 2, urlFormat);
            importKTLC(107, 2008, 10, 9, urlFormat);
            importKTLC(108, 2008, 10, 16, urlFormat);
            importKTLC(109, 2008, 10, 23, urlFormat);
            importKTLC(110, 2008, 10, 30, urlFormat);
            importKTLC(111, 2008, 11, 6, urlFormat);
            importKTLC(112, 2008, 11, 13, urlFormat);
            importKTLC(113, 2008, 11, 20, urlFormat);
            importKTLC(114, 2008, 11, 27, urlFormat);
            importKTLC(115, 2008, 12, 4, urlFormat);
            importKTLC(116, 2008, 12, 11, urlFormat);
            importKTLC(117, 2008, 12, 18, urlFormat);
            importKTLC(118, 2009, 1, 8, urlFormat);
            importKTLC(119, 2009, 1, 15, urlFormat);
            importKTLC(120, 2009, 1, 22, urlFormat);
            importKTLC(121, 2009, 1, 29, urlFormat);
            importKTLC(122, 2009, 2, 12, urlFormat);
            importKTLC(123, 2009, 2, 19, urlFormat);
            importKTLC(124, 2009, 2, 26, urlFormat);
            // pas de KTLC 125
            importKTLC(126, 2009, 3, 12, urlFormat);
            importKTLC(127, 2009, 3, 19, urlFormat);
            importKTLC(128, 2009, 3, 26, urlFormat);
            importKTLC(129, 2009, 4, 2, urlFormat);
            importKTLC(130, 2009, 4, 9, urlFormat);
            importKTLC(131, 2009, 4, 16, urlFormat);
            importKTLC(132, 2009, 4, 23, urlFormat);
            importKTLC(133, 2009, 5, 7, urlFormat);
            importKTLC(134, 2009, 6, 11, urlFormat);
            importKTLC(135, 2009, 6, 18, urlFormat);
            importKTLC(136, 2009, 6, 25, urlFormat);
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

        Logger.info("import KTLC %s [%s]", num, url);

    }
}