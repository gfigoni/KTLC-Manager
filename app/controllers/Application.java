package controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.google.gson.JsonSerializer;

import models.KTLCEdition;
import models.KTLCRace;
import models.KTLCResult;
import models.Login;
import models.Player;
import models.TMMap;
import models.stats.StatisticConfig;
import models.stats.StatisticGeneral;
import models.stats.StatisticMapper;
import models.stats.StatisticPlayer;
import play.i18n.Lang;
import play.modules.paginate.ValuePaginator;
import play.mvc.Controller;
import controllers.stats.StatisticsComparator;
import controllers.stats.StatisticsGenerator;

public class Application extends Controller {
	
	// avoid the regeneration of the player stats at every refresh of the page.
	private static StatisticPlayer currentPlayerStats = null;
	private static StatisticMapper currentMapperStats = null;

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
        
        ValuePaginator<KTLCResult> results = null;
        
        StatisticPlayer stats = null;
        
        if (player != null && player.isPlayer()) {	        
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
	        
	        // create the paginators for the ktlcs
	        results = new ValuePaginator<KTLCResult>(resultsList);
	        results.setPagesDisplayed(3);
	        results.setPageSize(25);
	        results.setParameterName("resultsPage");
	        
	        // get the stats
	        if (currentPlayerStats == null || !player.equals(currentPlayerStats.player)) {
	        	int minPercentage = StatisticConfig.loadStatsConfig().getMinPercentageParticipations();
	        	currentPlayerStats = StatisticsGenerator.generateStatisticsPlayer(player, minPercentage);
	        }
	        stats = currentPlayerStats;	        
        }
        
        if (player != null && !player.isPlayer()) {
        	player = null;
        }
        
        render(player, results, ktlcs, stats);
    }

    public static void players() {
        List<Player> playersList = Player.find("order by name asc").fetch();
        
        Player.filterByPlayer(playersList);
        
        ValuePaginator<Player> players = new ValuePaginator<Player>(playersList);
        players.setPageSize(40);
        
        render(players);
    }
    
    public static void mapper(String loginName) {
        // Récupération du mappeur à partir du login
        Player mapper = Player.findByLogin(loginName);
        
        // maps du mappeur  
        ValuePaginator<KTLCRace> races = null;
        
        StatisticMapper stats = null;
        
        if (mapper != null && mapper.isMapper()) {
	        // maps du joueur
	        List<TMMap> maps = TMMap.findByPlayer(mapper);
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
	        
	        // create the paginators for the maps	        
	        races = new ValuePaginator<KTLCRace>(racesList);
	        races.setPagesDisplayed(3);
	        races.setPageSize(25);
	        races.setParameterName("mapsPage");
	        
	        // get the stats
	        if (currentMapperStats == null || !mapper.equals(currentMapperStats.mapper)) {
	        	currentMapperStats = StatisticsGenerator.generateStatisticsMapper(mapper, StatisticsComparator.minPercentageMapper);
	        }
	        stats = currentMapperStats;	        
        }
        
        if(mapper != null && !mapper.isMapper()) {
        	mapper = null;
        }
        
        render(mapper, races, stats);
    }
    
    public static void mappers() {
        List<Player> mappersList = Player.find("order by name asc").fetch();
        
        Player.filterByMapper(mappersList);
        
        ValuePaginator<Player> mappers = new ValuePaginator<Player>(mappersList);
        mappers.setPageSize(40);
        
        render(mappers);
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
    
    // TODO JAVADOC
    public static void getListOfPlayerLogins(final String term) {
    	List<Login> list = Login.findByNameLike(term, 10);
    	
    	List<String> listAsString = new ArrayList<String>();
    	for (Login login : list) {
    		// only add players
    		if (login.player.isPlayer()) {
    			listAsString.add(login.name);
    		}
		}    	
        renderJSON(listAsString);
    }
    
    // TODO JAVADOC
    public static void getListOfMapperLogins(final String term) {
    	List<Login> list = Login.findByNameLike(term, 10);
    	
    	List<String> listAsString = new ArrayList<String>();
    	for (Login login : list) {
    		// only add mappers
    		if (login.player.isMapper()) {
    			listAsString.add(login.name);
    		}
		}    	
        renderJSON(listAsString);
    }
}