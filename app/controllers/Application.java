package controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import models.KTLCEdition;
import models.KTLCRace;
import models.KTLCResult;
import models.Player;
import models.TMMap;
import models.stats.StatisticConfig;
import models.stats.StatisticGeneral;
import models.stats.StatisticMapper;
import models.stats.StatisticPlayer;
import play.i18n.Lang;
import play.modules.paginate.ValuePaginator;
import play.mvc.Controller;
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
	        	currentPlayerStats = StatisticsGenerator.generateStatisticsPlayer(player);
	        }
	        stats = currentPlayerStats;	        
        }
        
        if (!player.isPlayer()) {
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
    
    public static void comparePlayers(String loginName1, String loginName2) {  	
    	Player originPlayer = null;
    	Player targetPlayer = null;    	
    	if (loginName1 == null && loginName2 == null) {
    		// TODO error no entered players at all
    		render(null, null, null, null); // redirection with null
    	} else if (loginName1 == null && loginName2 != null) {
    		// TODO error no entered player origin
    		render(null, null, null, null); // redirection with null
    	} else if (loginName1 != null && loginName2 == null) {
           comparePlayerWithRandom(loginName1);
    	} else {
    		// origin player
            originPlayer = Player.findByLogin(loginName1);
    		// target player
            targetPlayer = Player.findByLogin(loginName2);
            
            // generate stats for both players
            if (originPlayer != null && originPlayer.isPlayer() 
            		&& targetPlayer != null && targetPlayer.isPlayer()) {
            	compareTwoPlayers(originPlayer, targetPlayer);
            } else {
            	// TODO other errors...
            	render(null, null, null, null); // redirection with null
            }
    	}
    }
    
    public static void comparePlayersPost(String loginName1, String loginName2) {  	
    	Player originPlayer = null;
    	Player targetPlayer = Player.findByLogin(loginName2);    	
    	
    	if (loginName1 != null && (loginName2 == null || loginName2.isEmpty() || targetPlayer == null)) {
    		// TODO flash error exist pas
           redirect("/player/" + loginName1);
    	} else {
    		// origin player
            originPlayer = Player.findByLogin(loginName1);
            
            redirect("/comparePlayers/" + originPlayer.logins.get(0).name + "/" + targetPlayer.logins.get(0).name);
    	}
    }
    
    private static void comparePlayerWithRandom(String origin) {
        Player targetPlayer = null;

    	StatisticConfig config = StatisticConfig.loadStatsConfig();
    	
    	List<Player> playersList = Player.find("order by name asc").fetch();
    	// remove the player to avoid comparison with self
        playersList.remove(Player.findByLogin(origin));
        // remove player that did not reach the minimum percentage of participation
        Player.filterbyPercentageParticipation(playersList, config.getMinPercentageParticipations());
        
        Random generator = new Random(new Date().getTime());
        targetPlayer = playersList.get(generator.nextInt(playersList.size()));
    	
        redirect("/comparePlayers/" + origin + "/" + targetPlayer.logins.get(0).name);
    }
    
    private static void compareTwoPlayers(Player originPlayer, Player targetPlayer) {

    	// origin player
        StatisticPlayer originStats = StatisticsGenerator.generateStatisticsPlayer(originPlayer);
        // target player
        StatisticPlayer targetStats = StatisticsGenerator.generateStatisticsPlayer(targetPlayer);
        
        HashMap<String, String> originVersus = StatisticsGenerator.compareStatsPlayers(originStats, targetStats);
        HashMap<String, String> targetVersus = StatisticsGenerator.compareStatsPlayers(targetStats, originStats);
        
        // liste des ktlc, pour le graphe
        List<KTLCEdition> ktlcs = KTLCEdition.find("order by date asc").fetch();

        render(originPlayer, originStats, originVersus, targetPlayer, targetStats, targetVersus, ktlcs);
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
	        	currentMapperStats = StatisticsGenerator.generateStatisticsMapper(mapper);
	        }
	        stats = currentMapperStats;	        
        }
        
        if(!mapper.isMapper()) {
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
}