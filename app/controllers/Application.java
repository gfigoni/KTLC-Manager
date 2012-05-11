package controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import models.KTLCEdition;
import models.KTLCRace;
import models.KTLCResult;
import models.Player;
import models.TMMap;
import models.stats.StatisticGeneral;
import play.i18n.Lang;
import play.modules.paginate.ValuePaginator;
import play.mvc.Controller;
import controllers.stats.StatisticsGenerator;

public class Application extends Controller {

    public static void index() {
        KTLCEdition ktlc = KTLCEdition.find("order by date desc").first();
        render(ktlc);
    }

    public static void player(String loginName) {
        // Récupération du joueur à partir du login
        Player player = Player.findByLogin(loginName);

        // résultats du joueur
        List<KTLCResult> resultsList = KTLCResult.findByPlayer(player);

        // tri par date de KTLC
        Collections.sort(resultsList, new Comparator<KTLCResult>() {
            public int compare(KTLCResult o1, KTLCResult o2) {
                return o2.ktlc.date.compareTo(o1.ktlc.date);
            }
        });
        
        // liste des ktlc, pour le graphe
        List<KTLCEdition> ktlcs = KTLCEdition.find("order by date asc").fetch();
        
        // maps du joueur
        List<TMMap> maps = TMMap.findByPlayer(player);
        List<KTLCRace> racesList = new ArrayList<KTLCRace>(maps.size());
        for (TMMap map : maps) {
			racesList.add(KTLCRace.findByMap(map));
		}
        
        // tri par ktlc descendant
        Collections.sort(racesList, new Comparator<KTLCRace>() {
            @Override
            public int compare(final KTLCRace entry1, final KTLCRace entry2) {
                final Date dateKTLC1 = entry1.ktlc.date;
                final Date dateKTLC2 = entry2.ktlc.date;
                return -1*dateKTLC1.compareTo(dateKTLC2);
            }
        });
        
        ValuePaginator<KTLCResult> results = new ValuePaginator<KTLCResult>(resultsList);
        results.setPagesDisplayed(3);
        results.setParameterName("resultsPage");
        
        ValuePaginator<KTLCRace> races = new ValuePaginator<KTLCRace>(racesList);
        races.setPagesDisplayed(3);
        races.setParameterName("racesPage");
        
        render(player, results, ktlcs, races);
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
    
    public static void statistics() {
    	StatisticGeneral statistics = StatisticGeneral.getUniqueInstance();
    	if(!statistics.isInitialized()) {
    		StatisticsGenerator.updateAllGeneralStatistics();
    	}
    	render(statistics);
    }
    
    public static void changeLocale(String lang, String page) {
    	String currentLang = Lang.get();
    	
    	if (!lang.equals(currentLang) && (lang.equals("fr") || lang.equals("en"))) {
    		Lang.change(lang);
    		redirect("/" + page);
    	} else {
    		redirect("/" + page);
    	}
    }
}