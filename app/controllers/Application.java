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
import models.stats.StatisticPlayer;
import play.i18n.Lang;
import play.modules.paginate.ValuePaginator;
import play.mvc.Controller;
import controllers.stats.StatisticsGenerator;

public class Application extends Controller {
	
	// avoid the regeneration of the player stats at every refresh of the page.
	private static StatisticPlayer currentPlayerStats = null;

    public static void index() {
        KTLCEdition ktlc = KTLCEdition.find("order by date desc").first();
        render(ktlc);
    }

    public static void player(String loginName) {
        // Récupération du joueur à partir du login
        Player player = Player.findByLogin(loginName);
        
        // résultats du joueur
        List<KTLCResult> resultsList = null;
        
        // liste des ktlc, pour le graphe
        List<KTLCEdition> ktlcs = null;
        
        ValuePaginator<KTLCRace> races = null;
        ValuePaginator<KTLCResult> results = null;
        
        StatisticPlayer stats = null;
        
        if (player != null) {	        
	        resultsList = KTLCResult.findByPlayer(player);
	
	        // tri par date de KTLC
	        Collections.sort(resultsList, new Comparator<KTLCResult>() {
	            public int compare(KTLCResult o1, KTLCResult o2) {
	                return o2.ktlc.date.compareTo(o1.ktlc.date);
	            }
	        });
	        
	        // liste des ktlc, pour le graphe
	        ktlcs = KTLCEdition.find("order by date asc").fetch();
	        
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
	        
	        // create the paginators for the ktlcs and the maps
	        results = new ValuePaginator<KTLCResult>(resultsList);
	        results.setPagesDisplayed(3);
	        results.setPageSize(25);
	        results.setParameterName("resultsPage");
	        
	        races = new ValuePaginator<KTLCRace>(racesList);
	        races.setPagesDisplayed(3);
	        races.setPageSize(25);
	        races.setParameterName("racesPage");
	        
	        // get the stats
	        if (currentPlayerStats == null || !player.equals(currentPlayerStats.player)) {
	        	currentPlayerStats = StatisticsGenerator.generateStatisticsPlayer(player);
	        }
	        stats = currentPlayerStats;	        
        }
        
        render(player, results, ktlcs, races, stats);
    }

    public static void players() {
        List<Player> playersList = Player.find("order by name asc").fetch();
        
        ValuePaginator<Player> players = new ValuePaginator<Player>(playersList);
        players.setPageSize(40);
        
        render(players);
    }

    public static void ktlc(Integer number) {
        KTLCEdition ktlc = KTLCEdition.findByNumber(number);
        render(ktlc);
    }

    public static void ktlcs() {
        List<KTLCEdition> ktlcsList = KTLCEdition.find("order by date desc").fetch();
        
        ValuePaginator<KTLCEdition> ktlcs = new ValuePaginator<KTLCEdition>(ktlcsList);
        ktlcs.setPageSize(40);
        
        render(ktlcs);
    }
    
    public static void statistics() {
    	StatisticGeneral statistics = StatisticGeneral.getUniqueInstance();
    	if(!statistics.isInitialized()) {
    		StatisticsGenerator.updateAllGeneralStatistics();
    	}
    	render(statistics);
    }
    
    public static void changeLocale(String lang, String requestUrl) {
    	String currentLang = Lang.get();
    	
    	// remove the first '/'
    	requestUrl = requestUrl.substring(1, requestUrl.length());
    	
    	// redirect
    	if (!lang.equals(currentLang) && (lang.equals("fr") || lang.equals("en"))) {
    		Lang.change(lang);
    		redirect("/" + requestUrl);
    	} else {
    		redirect("/" + requestUrl);
    	}
    }
}