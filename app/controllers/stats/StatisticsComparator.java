package controllers.stats;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import models.KTLCEdition;
import models.Player;
import models.stats.StatisticConfig;
import models.stats.StatisticMapper;
import models.stats.StatisticPlayer;
import play.mvc.Controller;

public class StatisticsComparator extends Controller {
	
	private static int minPercentagePlayer;
	public static final int minPercentageMapper = 1;

	public static void comparePlayers(String loginName1, String loginName2) {
		Player originPlayer = Player.findByLogin(loginName1);
		Player targetPlayer = Player.findByLogin(loginName2);

		if (loginName1 == null && loginName2 == null) {
			// compare two random if both null
			compareTwoRandomPlayers();
		} else if ((loginName1 == null && loginName2 != null)){
			// error if first player is null
			render("Application/comparePlayers.html");
		} else if (loginName1 != null && loginName2 == null) {
			// random if second is null
			comparePlayerWithRandom(loginName1);
		} else {
			if (originPlayer == null && targetPlayer == null) {
				//error if both players don't exist
				render("Application/comparePlayers.html");
			} else if (originPlayer == null && targetPlayer != null && targetPlayer.isPlayer()) {
				// statistics player if only 1 true player
				redirect("/player/" + loginName2);
			} else if (originPlayer != null && targetPlayer == null && originPlayer.isPlayer()) {
				// statistics player if only 1 true player
				redirect("/player/" + loginName1);
			} else if (originPlayer.equals(targetPlayer) && originPlayer.isPlayer()) {				
				// same player
				redirect("/player/" + loginName1);
			} else {
				if (originPlayer.isPlayer() && targetPlayer.isPlayer()) {
					// two valid players
					compareTwoPlayers(originPlayer, targetPlayer);
				} else if (originPlayer.isMapper() && targetPlayer.isMapper()){
					// two valid mappers
					compareTwoMappers(originPlayer, targetPlayer);
				} else {
					// error if two invalid players
					render("Application/comparePlayers.html");
				}
			}
		}
	}

	public static void comparePlayersPost(String loginName1, String loginName2) {		
		Player originPlayer = Player.findByLogin(loginName1);
		Player targetPlayer = Player.findByLogin(loginName2);
			
		if (originPlayer == null && targetPlayer == null) {
			// error if two invalid players
			render("Application/comparePlayers.html");
		} else if (originPlayer != null && targetPlayer == null && originPlayer.isPlayer()) {
			// compare with random
			comparePlayerWithRandom(loginName1);
		} else if (originPlayer == null && targetPlayer != null && targetPlayer.isPlayer()) {
			redirect("/player/" + loginName2);
		} else {
			if (originPlayer.isPlayer() && targetPlayer.isPlayer()) {
				// two valid players
				redirect("/comparePlayers/" + originPlayer.logins.get(0).name + "/" + targetPlayer.logins.get(0).name);
			} else if (originPlayer.isMapper() && targetPlayer.isMapper()){
				// two valid mappers
				redirect("/compareMappers/" + originPlayer.logins.get(0).name + "/" + targetPlayer.logins.get(0).name);
			} else {
				// error if two invalid players
				render("Application/comparePlayers.html");
			}
		}
	}

	private static void comparePlayerWithRandom(String origin) {
		Random generator = new Random(new Date().getTime());
		Player targetPlayer = null;
		
		minPercentagePlayer = StatisticConfig.loadStatsConfig().getMinPercentageParticipations();

		List<Player> playersList = Player.find("order by name asc").fetch();
		// remove the player to avoid comparison with self
		playersList.remove(Player.findByLogin(origin));
		// remove player that did not reach the minimum percentage of participation
		Player.filterPlayersByPercentageParticipation(playersList, minPercentagePlayer);

		targetPlayer = playersList.get(generator.nextInt(playersList.size()));

		redirect("/comparePlayers/" + origin + "/" + targetPlayer.logins.get(0).name);
	}

	private static void compareTwoRandomPlayers() {
		Random generator = new Random(new Date().getTime());
		Player originPlayer = null;
		Player targetPlayer = null;
		
		minPercentagePlayer = StatisticConfig.loadStatsConfig().getMinPercentageParticipations();

		List<Player> playersList = Player.find("order by name asc").fetch();
		// remove player that did not reach the minimum percentage of participation
		Player.filterPlayersByPercentageParticipation(playersList, minPercentagePlayer);

		// get the 1st player
		originPlayer = playersList.get(generator.nextInt(playersList.size()));

		// remove the player to avoid comparison with self
		playersList.remove(originPlayer);

		// get the 2nd player
		targetPlayer = playersList.get(generator.nextInt(playersList.size()));

		redirect("/comparePlayers/" + originPlayer.logins.get(0).name + "/" + targetPlayer.logins.get(0).name);
	}

	private static void compareTwoPlayers(Player originPlayer, Player targetPlayer) {
		
		minPercentagePlayer = StatisticConfig.loadStatsConfig().getMinPercentageParticipations();

		// origin player
		StatisticPlayer originStats = StatisticsGenerator.generateStatisticsPlayer(originPlayer, minPercentagePlayer);
		// target player
		StatisticPlayer targetStats = StatisticsGenerator.generateStatisticsPlayer(targetPlayer, minPercentagePlayer);

		HashMap<String, String> originVersus = StatisticsGenerator.compareStatsPlayers(originStats, targetStats);
		HashMap<String, String> targetVersus = StatisticsGenerator.compareStatsPlayers(targetStats, originStats);

		// liste des ktlc, pour le graphe
		List<KTLCEdition> ktlcs = KTLCEdition.find("order by date asc").fetch();

		render("Application/comparePlayers.html", originPlayer, originStats, originVersus, targetPlayer, targetStats, targetVersus, ktlcs);
	}

	public static void compareMappers(String loginName1, String loginName2) {
		Player originMapper = Player.findByLogin(loginName1);
		Player targetMapper = Player.findByLogin(loginName2);

		if (loginName1 == null && loginName2 == null) {
			// compare two random if both null
			compareTwoRandomMappers();
		} else if ((loginName1 == null && loginName2 != null)){
			// error if first player is null
			render("Application/compareMappers.html");
		} else if (loginName1 != null && loginName2 == null) {
			// random if second is null
			compareMapperWithRandom(loginName1);
		} else {
			if (originMapper == null && targetMapper == null) {
				//error if both players don't exist
				render("Application/compareMappers.html");
			} else if (originMapper == null && targetMapper != null && targetMapper.isMapper()) {
				// statistics player if only 1 true player
				redirect("/mapper/" + loginName2);
			} else if (originMapper != null && targetMapper == null && originMapper.isMapper()) {
				// statistics player if only 1 true player
				redirect("/mapper/" + loginName1);
			} else if (originMapper.equals(targetMapper)  && originMapper.isMapper()) {				
				// same player
				redirect("/mapper/" + loginName1);
			} else {
				if (originMapper.isMapper() && targetMapper.isMapper()) {
					// two valid mappers
					compareTwoMappers(originMapper, targetMapper);
				} else if (originMapper.isPlayer() && targetMapper.isPlayer()){
					// two valid players
					compareTwoPlayers(originMapper, targetMapper);
				} else {
					// error if two invalid mappers
					render("Application/compareMappers.html");
				}
			}
		}
	}

	public static void compareMappersPost(String loginName1, String loginName2) {		
		Player originMapper = Player.findByLogin(loginName1);
		Player targetMapper = Player.findByLogin(loginName2);
			
		if (originMapper == null && targetMapper == null) {
			// error if two invalid players
			render("Application/compareMappers.html");
		} else if (originMapper != null && targetMapper == null && originMapper.isMapper()) {
			redirect("/mapper/" + loginName1);
		} else if (originMapper == null && targetMapper != null && targetMapper.isMapper()) {
			redirect("/mapper/" + loginName2);
		} else {
			if (originMapper.isMapper() && targetMapper.isMapper()){
				// two valid mappers
				redirect("/compareMappers/" + originMapper.logins.get(0).name + "/" + targetMapper.logins.get(0).name);
			} else if (originMapper.isPlayer() && targetMapper.isPlayer()) {
				// two valid players
				redirect("/comparePlayers/" + originMapper.logins.get(0).name + "/" + targetMapper.logins.get(0).name);
			} else {
				// error if two invalid players
				render("Application/compareMappers.html");
			}
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

		redirect("/compareMappers/" + origin + "/"  + targetMapper.logins.get(0).name);
	}

	private static void compareTwoRandomMappers() {
		Random generator = new Random(new Date().getTime());
		Player originMapper = null;
		Player targetMapper = null;

		List<Player> mappersList = Player.find("order by name asc").fetch();
		// remove mapper that did not build enough map (1%)
		Player.filterMappersByPercentageParticipation(mappersList, minPercentageMapper);

		// get the 1st player
		originMapper = mappersList.get(generator.nextInt(mappersList.size()));

		// remove the player to avoid comparison with self
		mappersList.remove(originMapper);

		// get the 2nd player
		targetMapper = mappersList.get(generator.nextInt(mappersList.size()));

		redirect("/compareMappers/" + originMapper.logins.get(0).name + "/" + targetMapper.logins.get(0).name);
	}

	private static void compareTwoMappers(Player originMapper,
			Player targetMapper) {

		// origin player
		StatisticMapper originStats = StatisticsGenerator.generateStatisticsMapper(originMapper, minPercentageMapper);
		// target player
		StatisticMapper targetStats = StatisticsGenerator.generateStatisticsMapper(targetMapper, minPercentageMapper);

		HashMap<String, String> originVersus = StatisticsGenerator.compareStatsMappers(originStats, targetStats);
		HashMap<String, String> targetVersus = StatisticsGenerator.compareStatsMappers(targetStats, originStats);

		render("Application/compareMappers.html", originMapper, originStats, originVersus, targetMapper, targetStats, targetVersus);
	}
}
