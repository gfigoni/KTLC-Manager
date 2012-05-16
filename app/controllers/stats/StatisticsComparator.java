package controllers.stats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import models.KTLCEdition;
import models.KTLCRace;
import models.Player;
import models.TMMap;
import models.stats.StatisticConfig;
import models.stats.StatisticMapper;
import models.stats.StatisticPlayer;
import play.modules.paginate.ValuePaginator;
import play.mvc.Controller;

public class StatisticsComparator extends Controller {

	public static void comparePlayers(String loginName1, String loginName2) {  	
    	Player originPlayer = null;
    	Player targetPlayer = null;
    	
    	if (loginName1 == null && loginName2 == null) {
    		// compare two random if both null
    		compareTwoRandomPlayers();
    	} else if (loginName1 == null && loginName2 != null) {
    		// TODO error no entered player origin
    		render("Application/comparePlayers.html", null, null, null, null); // redirection with null
    	} else if (loginName1 != null && loginName2 == null) {
            comparePlayerWithRandom(loginName1);
    	} else {
    		// origin player
            originPlayer = Player.findByLogin(loginName1);
    		// target player
            targetPlayer = Player.findByLogin(loginName2);
            
            if (originPlayer.equals(targetPlayer)) {
            	// TODO error same player
            	render("Application/comparePlayers.html", null, null, null, null); // redirection with null
            } else if (originPlayer != null && originPlayer.isPlayer() 
            		&& targetPlayer != null && targetPlayer.isPlayer()) {
            	compareTwoPlayers(originPlayer, targetPlayer);
            } else {
            	// TODO other errors...
            	render("Application/comparePlayers.html", null, null, null, null); // redirection with null
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
    	Random generator = new Random(new Date().getTime());
        Player targetPlayer = null;

    	StatisticConfig config = StatisticConfig.loadStatsConfig();
    	
    	List<Player> playersList = Player.find("order by name asc").fetch();
    	// remove the player to avoid comparison with self
        playersList.remove(Player.findByLogin(origin));
        // remove player that did not reach the minimum percentage of participation
        Player.filterPlayersByPercentageParticipation(playersList, config.getMinPercentageParticipations());
        
        targetPlayer = playersList.get(generator.nextInt(playersList.size()));
    	
        redirect("/comparePlayers/" + origin + "/" + targetPlayer.logins.get(0).name);
    }
    
    private static void compareTwoRandomPlayers() {
    	Random generator = new Random(new Date().getTime());
    	Player originPlayer = null;
        Player targetPlayer = null;

    	StatisticConfig config = StatisticConfig.loadStatsConfig();
    	
    	List<Player> playersList = Player.find("order by name asc").fetch();
        // remove player that did not reach the minimum percentage of participation
        Player.filterPlayersByPercentageParticipation(playersList, config.getMinPercentageParticipations());
        
        // get the 1st player
        originPlayer = playersList.get(generator.nextInt(playersList.size()));
        
        // remove the player to avoid comparison with self
        playersList.remove(originPlayer);
        
        // get the 2nd player
        targetPlayer = playersList.get(generator.nextInt(playersList.size()));
        
        redirect("/comparePlayers/" + originPlayer.logins.get(0).name + "/" + targetPlayer.logins.get(0).name);
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

        render("Application/comparePlayers.html", originPlayer, originStats, originVersus, targetPlayer, targetStats, targetVersus, ktlcs);
    }
    
    public static void compareMappers(String loginName1, String loginName2) {  	
    	Player originMapper = null;
    	Player targetMapper = null;    	
    	if (loginName1 == null && loginName2 == null) {
    		// compare two random if both null
    		compareTwoRandomMappers();
    	} else if (loginName1 == null && loginName2 != null) {
    		// TODO error no entered player origin
    		render(null, null, null, null); // redirection with null
    	} else if (loginName1 != null && loginName2 == null) {
           compareMapperWithRandom(loginName1);
    	} else {
    		// origin player
            originMapper = Player.findByLogin(loginName1);
    		// target player
            targetMapper = Player.findByLogin(loginName2);
            
            if (originMapper.equals(targetMapper)) {
            	// TODO error same player
            	render("Application/compareMappers.html", null, null, null, null); // redirection with null
            } else if  (originMapper != null && originMapper.isMapper() 
            		&& targetMapper != null && targetMapper.isMapper()) {
            	compareTwoMappers(originMapper, targetMapper);
            } else {
            	// TODO other errors...
            	render("Application/compareMappers.html", null, null, null, null); // redirection with null
            }
    	}
    }
    
    public static void compareMappersPost(String loginName1, String loginName2) {  	
    	Player originMapper = null;
    	Player targetMapper = Player.findByLogin(loginName2);    	
    	
    	if (loginName1 != null && (loginName2 == null || loginName2.isEmpty() || targetMapper == null)) {
    		// TODO flash error exist pas
           redirect("/mapper/" + loginName1);
    	} else {
    		// origin player
            originMapper = Player.findByLogin(loginName1);
            
            redirect("/compareMappers/" + originMapper.logins.get(0).name + "/" + targetMapper.logins.get(0).name);
    	}
    }
    
    private static void compareMapperWithRandom(String origin) {
    	Random generator = new Random(new Date().getTime());
    	Player targetMapper = null;
    	
    	List<Player> mappersList = Player.find("order by name asc").fetch();
    	// remove the player to avoid comparison with self
        mappersList.remove(Player.findByLogin(origin));
        // remove player that did not reach the minimum percentage of participation (1%)
        Player.filterMappersByPercentageParticipation(mappersList, 1);
        
        targetMapper = mappersList.get(generator.nextInt(mappersList.size()));
    	
        redirect("/compareMappers/" + origin + "/" + targetMapper.logins.get(0).name);
    }
    
    private static void compareTwoRandomMappers() {
    	Random generator = new Random(new Date().getTime());
    	Player originMapper = null;
        Player targetMapper = null;
    	
    	List<Player> mappersList = Player.find("order by name asc").fetch();
        // remove mapper that did not build enough map (1%)
        Player.filterMappersByPercentageParticipation(mappersList, 1);
        
        // get the 1st player
        originMapper = mappersList.get(generator.nextInt(mappersList.size()));
        
        // remove the player to avoid comparison with self
        mappersList.remove(originMapper);
        
        // get the 2nd player
        targetMapper = mappersList.get(generator.nextInt(mappersList.size()));
        
    	
        redirect("/compareMappers/" + originMapper.logins.get(0).name + "/" + targetMapper.logins.get(0).name);
    }
    
    private static void compareTwoMappers(Player originMapper, Player targetMapper) {

    	// origin player
        StatisticMapper originStats = StatisticsGenerator.generateStatisticsMapper(originMapper);
        // target player
        StatisticMapper targetStats = StatisticsGenerator.generateStatisticsMapper(targetMapper);
        
        HashMap<String, String> originVersus = StatisticsGenerator.compareStatsMappers(originStats, targetStats);
        HashMap<String, String> targetVersus = StatisticsGenerator.compareStatsMappers(targetStats, originStats);

        render("Application/compareMappers.html", originMapper, originStats, originVersus, targetMapper, targetStats, targetVersus);
    }
}
