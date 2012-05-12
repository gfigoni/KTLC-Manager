package controllers.stats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import models.KTLCEdition;
import models.KTLCRace;
import models.KTLCRaceResult;
import models.KTLCResult;
import models.Player;
import models.TMEnvironment;
import models.TMMap;
import models.stats.Rank;
import models.stats.StatisticConfig;
import models.stats.StatisticGeneral;
import models.stats.StatisticPlayer;

/**
 * This class is used to generate general statistics regarding the whole KTLC history.
 * @author Toub
 */
public class StatisticsGenerator {
	/** The configuration for the statistics calculations */
	private static StatisticConfig config;
	
	/**
	 * Update all the statistics, should be called only using administrator board... 
	 * @return the elapsed time in milliseconds
	 */
	public static Long updateAllGeneralStatistics() {
		Long startTime = System.nanoTime();
		
		StatisticGeneral stats = StatisticGeneral.getUniqueInstance();
		
		config = StatisticConfig.loadStatsConfig();
		
		stats.LENGTH_TOP = config.getLengthTop();
		stats.RANK_INTEREST = config.getRankInterest();
		stats.MIN_PERCENTAGE_PARTICIPATIONS = config.getMinPercentageParticipations();
		stats.MIN_NUMBER_MAPS_FOR_EPIC_FAIL = config.getMinNumberMapsForEpicFail();
		
		List<KTLCEdition> ktlcs = KTLCEdition.find("order by date asc").fetch();
		List<Player> players = Player.find("order by name asc").fetch();
		List<TMMap> maps = TMMap.find("order by environment desc").fetch();
		
		// Stats		
		stats.stats_numberKTLCs = ktlcs.size();
		stats.stats_numberKTLC_TMU = calcNumberKTLCTMU(ktlcs);
		stats.stats_numberSuperKTLC_TMU = calcNumberSuperKTLCTMU(ktlcs);
		stats.stats_numberKTLC_TM2 = calcNumberKTLCTM2(ktlcs);
		stats.stats_numberPlayers = players.size();
		stats.stats_numberPlayersPercentage = calcNumberPlayerByPercentage(players, config.getMinPercentageParticipations());
		stats.stats_numberMaps = maps.size();
		stats.stats_numberRuns = calcTotalNumberRuns();
		stats.stats_averageNumberPlayersByKTLC = calcAverageNumberPlayersByKTLC(ktlcs);
		stats.stats_averageNumberMapsByKTLC = calcAverageNumberMapsByKTLC(ktlcs);
		
		// charts
		stats.chart_numberMapsByEnviro = calcMapsByEnviro(maps);
		stats.chart_numberPlayersByKTLC = calcNumberPlayersByKTLC(ktlcs);
		
		// particular KTLC
		stats.smallestKTLC = calcMinNumberPlayers(ktlcs);
		stats.biggestKTLC = calcMaxNumberPlayers(ktlcs);
		
		// rankings - hall of fame
		stats.ranking_numberParticipation = calcRankingParticipatioRatio(players, config.getLengthTop());
		stats.ranking_bestAverageRank = calcRankingBestAverageRank(players, config.getLengthTop(), config.getMinPercentageParticipations());
		stats.ranking_numberMaps = calcRankingNumberMaps(players, config.getLengthTop());
		stats.ranking_numberPodiumsKTLC = calcRankingNumberPodiumsKTLC(players, config.getLengthTop(), config.getRankInterest());
		stats.ranking_numberPodiumsRace = calcRankingNumberPodiumsRace(players, config.getLengthTop(), config.getRankInterest());
		stats.ranking_numberPerfect = calcRankingNumberPerfect();
		
		// ranking - hall of shame
		List<KTLCRace> races = KTLCRace.findAll();
		stats.ranking_violentMaps = calcRankingViolentMaps(races, config.getLengthTop());
		stats.ranking_numberLastPlaceKTLC = calcRankingNumberlastPlaceKTLC(players, config.getLengthTop(), config.getMinPercentageParticipations());
		stats.ranking_numberLastPlaceRace = calcRankingNumberlastPlaceRace(players, config.getLengthTop());
		stats.ranking_worstAverageRank = calcRankingWorstAverageRank(players, config.getLengthTop(), config.getMinPercentageParticipations());
		stats.ranking_numberEpicFail = calcRankingNumberEpicFail(config.getMinNumberMapsForEpicFail());
		
		// change the status
		stats.setInitialized(true);
		
		Long endTime = System.nanoTime();
		
		return Math.abs(endTime - startTime) / 1000000;
	}
	
	public static StatisticPlayer generateStatisticsPlayer(Player player) {		
		StatisticPlayer stats = new StatisticPlayer();
		
		// set the parameters from the config
		config = StatisticConfig.loadStatsConfig();
		stats.RANK_LIMIT = config.getRankLimit();
		
		// init the lists
		List<KTLCEdition> ktlcs = KTLCEdition.find("order by date asc").fetch();
		List<KTLCResult> ktlcResults = KTLCResult.findByPlayer(player);
		List<KTLCEdition> ktlcEditions = new ArrayList<KTLCEdition>();
		for (KTLCResult result : ktlcResults) { ktlcEditions.add(result.ktlc); }		
		List<TMMap> maps = TMMap.findByPlayer(player);
		
		// set the stats
		stats.player = player;
		
		stats.numberKTLC = ktlcResults.size();
		stats.totalKTLCs = ktlcs.size();
		stats.partRatioKTLC = stats.numberKTLC / (double)stats.totalKTLCs;
		
		stats.numberKTLC_TMU = calcNumberKTLCTMU(ktlcEditions);
		stats.totalKTLC_TMU = calcNumberKTLCTMU(ktlcs);
		stats.partRatioKTLC_TMU = stats.numberKTLC_TMU / (double)stats.totalKTLC_TMU;
		
		stats.numberSuperKTLC_TMU = calcNumberSuperKTLCTMU(ktlcEditions);
		stats.totalSuperKTLC_TMU = calcNumberSuperKTLCTMU(ktlcs);
		stats.partRatioSuperKTLC_TMU = stats.numberSuperKTLC_TMU / (double)stats.totalSuperKTLC_TMU;
		
		stats.numberKTLC_TM2 = calcNumberKTLCTM2(ktlcEditions);
		stats.totalKTLC_TM2 = calcNumberKTLCTM2(ktlcs);
		stats.partRatioKTLC_TM2 = stats.numberKTLC_TM2 / (double)stats.totalKTLC_TM2;
		
		stats.averageRank = averageRankByPlayer(player);
		
		stats.totalRaces = calcNumberMapsPlayed(ktlcResults);
		stats.totalRuns = calcTotalNumberRuns(player);
		
		stats.createdMaps = maps.size();
		stats.totalMaps = TMMap.findAll().size();
		stats.ratioMaps = stats.createdMaps / (double)stats.totalMaps;
		
		stats.chart_ranksByKTLCs = ktlcResultsByRanksByPlayer(player, config.getRankLimit());
		stats.chart_ranksByRaces = raceResultsByRanksByPlayer(player, config.getRankLimit());
		
		stats.chart_numberMapsByEnviro = calcMapsByEnviro(maps);
		
		return stats;
	}
	
	
	/**
	 * Calculate the number of players that played at least x % of the KTLCS
	 * @param players the list of players
	 * @param percentage the percentage required
	 * @return
	 */
	private static int calcNumberPlayerByPercentage(List<Player> players, int percentage) {
		int result = 0;
		int minKTLC = (int)(KTLCEdition.findAll().size() * percentage * 0.01);
		
		for (Player player : players) {
			if (KTLCResult.findByPlayer(player).size() >= minKTLC) {
				result++;
			}
		}
		
		return result;
	}
	
	/**
	 * TODO
	 * @param results
	 * @return
	 */
	private static int calcNumberMapsPlayed(List<KTLCResult> results) {
		int count = 0;
		for (KTLCResult result : results) {
			count += result.nbRaces;
		}
		return count;
	}
	
	/**
	 * Calculate the evolution of the number of players by KTLC
	 * @param ktlcs the list of KTLC Editions
	 * @return
	 */
	private static int[][] calcNumberPlayersByKTLC(List<KTLCEdition> ktlcs) {
		int[][] playersByKTLC = new int[ktlcs.size()][2];
		for (int i = 0; i < ktlcs.size(); i++) {
			playersByKTLC[i][0] = ktlcs.get(i).number;
			playersByKTLC[i][1] = ktlcs.get(i).results.size();
		}
		
		return playersByKTLC;
	}
	
	/**
	 * Count the number of maps by TMEnvironment
	 * @param maps the list of maps that have to be used
	 * @return a HashMap with the environment as keys and count as value
	 */
	private static HashMap<TMEnvironment, Integer> calcMapsByEnviro(List<TMMap> maps) {
		HashMap<TMEnvironment, Integer> count = new HashMap<TMEnvironment, Integer>();

		for (TMMap map : maps) {
			if (count.containsKey(map.environment)) {
				int currentCount = count.get(map.environment);
				count.put(map.environment, currentCount + 1);
			} else {
				count.put(map.environment, 1);
			}
		}
		
		return count;
	}
	
	/**
	 * Calculate the number of KTLC TMU that have been played
	 * @param ktlcs the list of KTLC Editions
	 * @return
	 */
	private static int calcNumberKTLCTMU(List<KTLCEdition> ktlcs) {
		int count = 0;
		for (KTLCEdition ktlc : ktlcs) {
			KTLCRace firstRace = ktlc.races.get(0);
			if (TMEnvironment.getEnvironmentsTMU().contains(firstRace.map.environment)) {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * Calculate the number of Super KTLC TMU that have been played
	 * @param ktlcs the list of KTLC Editions
	 * @return
	 */
	private static int calcNumberSuperKTLCTMU(List<KTLCEdition> ktlcs) {
		int count = 0;
		
		for (KTLCEdition ktlc : ktlcs) {
			if (ktlc.races.size() == TMEnvironment.getEnvironmentsTMU().size()) {
				int distinctEnviro = 0;
				for (TMEnvironment enviro : TMEnvironment.getEnvironmentsTMU()) {
					for (KTLCRace race : ktlc.races) {
						if (race.map.environment.equals(enviro)) {
							distinctEnviro++;
						}
					}
				}
				if (distinctEnviro == TMEnvironment.getEnvironmentsTMU().size()) {
					count++;
				}
			}
		}
		return count;
	}
	
	/**
	 * Calculate the number of KTLC TM2
	 * @param ktlcs the list of KTLC Editions
	 * @return
	 */
	private static int calcNumberKTLCTM2(List<KTLCEdition> ktlcs) {
		int count = 0;
		for (KTLCEdition ktlc : ktlcs) {
			KTLCRace firstRace = ktlc.races.get(0);
			if (TMEnvironment.getEnvironmentsTM2().contains(firstRace.map.environment)) {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * Calculate the average number of players for a list of KTLCs
	 * @param ktlcs the list of KTLC Editions
	 * @return the average number of players
	 */
	private static double calcAverageNumberPlayersByKTLC(List<KTLCEdition> ktlcs) {
		int count = 0;
		for (KTLCEdition ktlc : ktlcs) {
			count += ktlc.results.size();
		}
		return (double)count / (double)ktlcs.size();
	}
	
	/**
	 * Calculate the average number of maps for a list of KTLCs
	 * @param ktlcs the list of KTLC Editions
	 * @return the average number of maps
	 */
	private static double calcAverageNumberMapsByKTLC(List<KTLCEdition> ktlcs) {
		int count = 0;
		for (KTLCEdition ktlc : ktlcs) {
			count += ktlc.races.size();
		}
		return (double)count / (double)ktlcs.size();
	}
	
	/**
	 * Find the ktlc with the smallest number of players in a list of KTLCs
	 * @param ktlcs the list of KTLC Editions
	 * @return the smallest KTLCEdition
	 */
	private static KTLCEdition calcMinNumberPlayers(List<KTLCEdition> ktlcs) {
		KTLCEdition minNumberPlayersKTLC = null;
		int min = Integer.MAX_VALUE;
		for (KTLCEdition ktlc : ktlcs) {
			if (ktlc.results.size() < min) {
				min = ktlc.results.size();
				minNumberPlayersKTLC = ktlc;
			}
		}
		return minNumberPlayersKTLC;
	}
	
	/**
	 * Find the ktlc with the biggest number of players in a list of KTLCs
	 * @param ktlcs the list of KTLC Editions
	 * @return the biggest KTLCEdition
	 */
	private static KTLCEdition calcMaxNumberPlayers(List<KTLCEdition> ktlcs) {
		KTLCEdition maxNumberPlayersKTLC = null;
		int max = Integer.MIN_VALUE;
		for (KTLCEdition ktlc : ktlcs) {
			if (ktlc.results.size() > max) {
				max = ktlc.results.size();
				maxNumberPlayersKTLC = ktlc;
			}
		}
		return maxNumberPlayersKTLC;
	}
	
	/**
	 * Calculate the number of played runs by getting the results of the winner of each maps.
	 * The number of runs is the sum of the number of round played by each winner
	 * @return the total number of runs
	 */
	private static int calcTotalNumberRuns() {
		// get only the results from the winner of each race
		List<KTLCRaceResult> results = KTLCRaceResult.find("rank = 1").fetch();
	
		int count = 0;
		for (KTLCRaceResult result : results) {
			count += result.roundsCount;
		}		
		return count;
	}
	
	/**
	 * Calculate the number of played runs by the player
	 * The number of runs is the sum of the number of round played by each winner
	 * @param player
	 * @return the total number of runs
	 */
	private static int calcTotalNumberRuns(Player player) {
		// get only the results from the winner of each race
		List<KTLCRaceResult> results = KTLCRaceResult.findByPlayer(player);
	
		int count = 0;
		for (KTLCRaceResult result : results) {
			count += result.roundsCount;
		}		
		return count;
	}
	
	/**
	 * Calculate the ranking regarding the Participation Ratio of a list of players, 
	 * on ALL the KTLCs.
	 * @param players the list of players
	 * @param lengthTop the size of the top
	 * @return the list of size lengthTop of the best players with their values
	 */
	private static List<Rank> calcRankingParticipatioRatio(List<Player> players, int lengthTop) {
		List<Rank> ranking = new ArrayList<Rank>(lengthTop);
		int numberKTLCs = KTLCEdition.findAll().size();
		
		// calculate the participation ratio for each player
		Double[][] participationRatiosByPlayerID = new Double[players.size()][2];
		int currentIndex = 0;
		for (Player player : players) {
			int numberParticipation = KTLCResult.findByPlayer(player).size();
			participationRatiosByPlayerID[currentIndex][0] = (double)player.id;
			participationRatiosByPlayerID[currentIndex][1] = (double)numberParticipation / (double)numberKTLCs;
			currentIndex++;
		}
		
		// sort the array by ASCENDING participation rate
		Arrays.sort(participationRatiosByPlayerID, new Comparator<Double[]>() {
            @Override
            public int compare(final Double[] entry1, final Double[] entry2) {
                final Double ratio1 = entry1[1];
                final Double ratio2 = entry2[1];
                return ratio1.compareTo(ratio2);
            }
        });
		
		// Add the best results to the ranking create the structure for holding results
		for (int i = 0; i < lengthTop; i++) {
			int index = players.size() - 1 - i;
			
			Player player = Player.findById(participationRatiosByPlayerID[index][0].longValue());
			Double ratio = participationRatiosByPlayerID[index][1];
			Integer participation = KTLCResult.findByPlayer(player).size();
			
			Rank r = new Rank(player, participation, ratio);
			ranking.add(r);
		}
		
		return ranking;
	}
	
	/**
	 * Calculate the ranking regarding the Best Average Rank of a list of players, 
	 * on ALL the KTLCs.
	 * @param players the list of players
	 * @param lengthTop the size of the top
	 * @param minPercentage the minimal percentage that the players should have played to be considered
	 * @return the list of size lengthTop of the best players with their values
	 */
	private static List<Rank<Double>> calcRankingBestAverageRank(List<Player> players, int lengthTop, int MinPercentage) {
		List<Rank<Double>> ranking = new ArrayList<Rank<Double>>(lengthTop);
		int numberKTLC = KTLCEdition.findAll().size();
		
		// calculate the number of maps for each player
		double[][] averageRankByPlayerID = new double[players.size()][2];
		
		int currentIndex = 0;
		for (Player player: players) {
			averageRankByPlayerID[currentIndex][0] = player.id.doubleValue();
			averageRankByPlayerID[currentIndex][1] = averageRankByPlayer(player);
			currentIndex++;
		}
		
		// sort the array by ASCENDING participation rate
		Arrays.sort(averageRankByPlayerID, new Comparator<double[]>() {
            @Override
            public int compare(final double[] entry1, final double[] entry2) {
                final Double averageRank1 = entry1[1];
                final Double averageRank2 = entry2[1];
                return averageRank1.compareTo(averageRank2);
            }
        });
		
		// create the structure for holding results,
		// but only for players with at least MIN_PARTICIPATION
		int currentCount = 0;
		for (int i = 0; i < players.size(); i++) {
			Player player = Player.findById((long)averageRankByPlayerID[i][0]);
			double value = averageRankByPlayerID[i][1];
			int numberParticipation = KTLCResult.findByPlayer(player).size();
			
			if (numberParticipation >= (MinPercentage*0.01*numberKTLC)) {
				Rank<Double> r = new Rank<Double>(player, numberParticipation, -1, value);
				ranking.add(currentCount, r);
				
				currentCount++;
				if (currentCount == lengthTop) { break; }
			}
		}
		
		return ranking;
	}
	
	/**
	 * Calculate the ranking regarding the Worst Average Rank of a list of players, 
	 * on ALL the KTLCs.
	 * @param players the list of players
	 * @param lengthTop the size of the top
	 * @param minPercentage the minimal percentage that the players should have played to be considered
	 * @return the list of size lengthTop of the best players with their values
	 */
	private static List<Rank<Double>> calcRankingWorstAverageRank(List<Player> players, int lengthTop, int MinPercentage) {
		List<Rank<Double>> ranking = new ArrayList<Rank<Double>>(lengthTop);
		int numberKTLC = KTLCEdition.findAll().size();
		
		// calculate the number of maps for each player
		double[][] averageRankByPlayerID = new double[players.size()][2];
		
		int currentIndex = 0;
		for (Player player: players) {
			averageRankByPlayerID[currentIndex][0] = player.id.doubleValue();
			averageRankByPlayerID[currentIndex][1] = averageRankByPlayer(player);
			currentIndex++;
		}
		
		// sort the array by ASCENDING participation rate
		Arrays.sort(averageRankByPlayerID, new Comparator<double[]>() {
            @Override
            public int compare(final double[] entry1, final double[] entry2) {
                final Double averageRank1 = entry1[1];
                final Double averageRank2 = entry2[1];
                return averageRank1.compareTo(averageRank2);
            }
        });
		
		// create the structure for holding results,
		// but only for players with at least MIN_PARTICIPATION
		int currentCount = 0;
		for (int i = players.size() - 1; i >= 0; i--) {
			Player player = Player.findById((long)averageRankByPlayerID[i][0]);
			double value = averageRankByPlayerID[i][1];
			int numberParticipation = KTLCResult.findByPlayer(player).size();
			
			if (numberParticipation >= (MinPercentage*0.01*numberKTLC)) {
				Rank<Double> r = new Rank<Double>(player, numberParticipation, -1, value);
				ranking.add(currentCount, r);
				
				currentCount++;
				if (currentCount == lengthTop) { break; }
			}
		}
		
		return ranking;
	}
	
	/**
	 * Calculate the ranking regarding the Number of map created by the players from the list, 
	 * on ALL the KTLCs.
	 * @param players the list of players
	 * @param lengthTop the size of the top
	 * @return the list of size lengthTop of the best players with their values
	 */
	private static List<Rank<Integer>> calcRankingNumberMaps(List<Player> players, int lengthTop) {
		List<Rank<Integer>> ranking = new ArrayList<Rank<Integer>>(lengthTop);
		
		// calculate the number of maps for each player
		int[][] numberMapsByPlayerID = new int[players.size()][2];
		
		int currentIndex = 0;
		for (Player player : players) {
			numberMapsByPlayerID[currentIndex][0] = player.id.intValue();
			numberMapsByPlayerID[currentIndex][1] = TMMap.findByPlayer(player).size();
			currentIndex++;
		}
		
		// sort the array by ASCENDING participation rate
		Arrays.sort(numberMapsByPlayerID, new Comparator<int[]>() {
            @Override
            public int compare(final int[] entry1, final int[] entry2) {
                final Integer numberMaps1 = entry1[1];
                final Integer numberMaps2 = entry2[1];
                return numberMaps1.compareTo(numberMaps2);
            }
        });
		
		// create the structure for holding results
		for (int i = 0; i < lengthTop; i++) {
			int index = players.size() - 1 - i;
			
			Player player = Player.findById((long)numberMapsByPlayerID[index][0]);
			int numberMaps = numberMapsByPlayerID[index][1];
			
			// numberParticipation & ratio are not used nor calculated
			Rank r = new Rank<Integer>(player, -1, -1, numberMaps);
			ranking.add(i, r);
		}
		
		return ranking;
	}
	
	/**
	 * Calculate the ranking regarding the Number of podiums performed during the KTLCs by the 
	 * players from the list, on ALL the KTLCs.
	 * @param players the list of players
	 * @param lengthTop the size of the top
	 * @param rankInterest the last rank that should be considered (from 1st place to rankInterest)
	 * @return the list of size lengthTop of the best players with their values
	 */
	private static List<Rank<int[]>> calcRankingNumberPodiumsKTLC(List<Player> players, int lengthTop, int rankInterest) {
		List<Rank<int[]>> ranking = new ArrayList<Rank<int[]>>(lengthTop);
		
		// calculate the number of podiums by KTLC for each player
		int[][] numberPodiumByPlayerID = new int[players.size()][rankInterest + 1];
		
		int currentIndex = 0;
		for (Player player : players) {
			// store the player id in the first cell
			numberPodiumByPlayerID[currentIndex][0] = player.id.intValue();
			// store the results in the remaining cells
			List<KTLCResult> results = KTLCResult.findByPlayer(player);
			if (!results.isEmpty()) {
				for (KTLCResult result : results) {
					if (result.rank <= rankInterest) {
						numberPodiumByPlayerID[currentIndex][result.rank]++;
					}
				}
			}
			currentIndex++;
		}
		
		// sort the array by ASCENDING number of podium
		Arrays.sort(numberPodiumByPlayerID, new Comparator<int[]>() {
            @Override
            public int compare(final int[] entry1, final int[] entry2) {
                final Integer numPodium1 = entry1[1] + entry1[2] + entry1[3];
                final Integer numPodium2 = entry2[1] + entry2[2] + entry2[3];
                return numPodium1.compareTo(numPodium2);
            }
        });
		
		// create the structure for holding results
		for (int i = 0; i < lengthTop; i++) {			
			int index = players.size() - 1 - i;
			
			Player player = Player.findById((long)numberPodiumByPlayerID[index][0]);
			int participation = KTLCResult.findByPlayer(player).size();
			
			int[] podiums = Arrays.copyOfRange(numberPodiumByPlayerID[index], 1, rankInterest + 1);
			int totalPodium = podiums[0] + podiums[1] + podiums[2];
			double ratio = (double)totalPodium / (double)participation;
			
			Rank<int[]> r = new Rank<int[]>(player, participation, ratio, podiums);
			ranking.add(i, r);
		}
		
		return ranking;
	}
	
	/**
	 * Calculate the ranking regarding the Number of podiums performed during the races of the KTLCs
	 * by the players from the list, on ALL the KTLCs.
	 * @param players the list of players
	 * @param lengthTop the size of the top
	 * @param rankInterest the last rank that should be considered (from 1st place to rankInterest)
	 * @return the list of size lengthTop of the best players with their values
	 */
	private static List<Rank<int[]>> calcRankingNumberPodiumsRace(List<Player> players, int lengthTop, int rankInterest) {
		List<Rank<int[]>> ranking = new ArrayList<Rank<int[]>>(lengthTop);
		
		// calculate the number of podiums by race for each player
		int[][] numberPodiumByPlayerID = new int[players.size()][rankInterest + 1];
		
		int currentIndex = 0;
		for (Player player : players) {
			// store the player id in the first cell
			numberPodiumByPlayerID[currentIndex][0] = player.id.intValue();
			// store the results in the remaining cells
			List<KTLCRaceResult> results = KTLCRaceResult.findByPlayer(player);
			if (!results.isEmpty()) {
				for (KTLCRaceResult result : results) {
					if (result.rank <= rankInterest) {
						numberPodiumByPlayerID[currentIndex][result.rank]++;
					}
				}
			}
			currentIndex++;
		}
		
		// sort the array by ASCENDING number of podium
		Arrays.sort(numberPodiumByPlayerID, new Comparator<int[]>() {
            @Override
            public int compare(final int[] entry1, final int[] entry2) {
                final Integer numPodium1 = entry1[1] + entry1[2] + entry1[3];
                final Integer numPodium2 = entry2[1] + entry2[2] + entry2[3];
                return numPodium1.compareTo(numPodium2);
            }
        });
		
		// create the structure for holding results
		for (int i = 0; i < lengthTop; i++) {
			int index = players.size() - 1 - i;
			
			Player player = Player.findById((long)numberPodiumByPlayerID[index][0]);
			int participation = KTLCRaceResult.findByPlayer(player).size();
			
			int[] podiums = Arrays.copyOfRange(numberPodiumByPlayerID[index], 1, rankInterest + 1);
			int totalPodium = podiums[0] + podiums[1] + podiums[2];
			double ratio = (double)totalPodium / (double)participation;
			
			Rank<int[]> r = new Rank<int[]>(player, participation, ratio, podiums);
			ranking.add(i, r);
		}
		
		return ranking;
	}
	
	/**
	 * Calculate the ranking regarding the maps which eliminated the highest number of player, 
	 * on ALL the KTLCs.
	 * @param races the list of races
	 * @param lengthTop the size of the top
	 * @return the list of size lengthTop of the most violent races with their values
	 */
	private static List<Rank<KTLCRace>> calcRankingViolentMaps(List<KTLCRace> races, int lengthTop) {
		List<Rank<KTLCRace>> ranking = new ArrayList<Rank<KTLCRace>>(lengthTop);
		
		//calculate the number of elimination for each races
		int[][] numberEliminationFirstLapByRaceID = new int[races.size()][2];
		
		int currentIndex = 0;
		for (KTLCRace race: races) {
			numberEliminationFirstLapByRaceID[currentIndex][0] = race.id.intValue();
			// count the number of giving up or elimination
			int countElimination = 0;
			for (KTLCRaceResult result : race.results) {
				if (result.roundsCount == 1) {
					countElimination++;
				}
			}
			// retract one as 1 player at least should have finished the map if only one round has been played
			if (countElimination == race.results.size()) {
				countElimination-=1;
			}
			
			numberEliminationFirstLapByRaceID[currentIndex][1] = countElimination;
			currentIndex++;
		}
		
		// sort the array by ASCENDING elimination
		Arrays.sort(numberEliminationFirstLapByRaceID, new Comparator<int[]>() {
            @Override
            public int compare(final int[] entry1, final int[] entry2) {
                final Integer numberElimination1 = entry1[1];
                final Integer numberElimination2 = entry2[1];
                return numberElimination1.compareTo(numberElimination2);
            }
        });
	
		// create the structure for holding results,
		int currentCount = 0;
		for (int i = races.size() - 1; i >= 0; i--) {			
			KTLCRace race = KTLCRace.findById((long)numberEliminationFirstLapByRaceID[i][0]);
			Player author = Player.findByLogin(race.map.login.name);
			// the number of elimination
			int participation = numberEliminationFirstLapByRaceID[i][1];

			Rank<KTLCRace> r = new Rank<KTLCRace>(author, participation, -1, race);
			ranking.add(currentCount, r);
			
			currentCount++;
			if (currentCount == lengthTop) { break; }
		}
		
		return ranking;
	}
	
	/**
	 * Calculate the ranking for the player that finished at the last place of a race
	 * @param players the list of players
	 * @param lengthTop the size of the top
	 * @return the list of size lengthTop of players with the highest number of last places
	 */
	private static List<Rank<Integer>> calcRankingNumberlastPlaceRace(List<Player> players, int lengthTop) {
		List<Rank<Integer>> ranking = new ArrayList<Rank<Integer>>(lengthTop);
		
		// calculate the number of podiums by race for each player
		int[][] numberLastPlaceByPlayerID = new int[players.size()][2];
		
		int currentIndex = 0;
		for (Player player : players) {
			// store the player id in the first cell
			numberLastPlaceByPlayerID[currentIndex][0] = player.id.intValue();
			// store the results in the remaining cell
			List<KTLCRaceResult> results = KTLCRaceResult.findByPlayer(player);
			if (!results.isEmpty()) {
				for (KTLCRaceResult result : results) {
					int numberPlayers = result.race.results.size();
					if (result.rank == numberPlayers) {
						numberLastPlaceByPlayerID[currentIndex][1]++;
					}
				}
			}
			currentIndex++;
		}
		
		// sort the array by ASCENDING number of last place
		Arrays.sort(numberLastPlaceByPlayerID, new Comparator<int[]>() {
            @Override
            public int compare(final int[] entry1, final int[] entry2) {
                final Integer numberLastPlace1 = entry1[1];
                final Integer numberLastPlace2 = entry2[1];
                return numberLastPlace1.compareTo(numberLastPlace2);
            }
        });
		
		// create the structure for holding results
		int currentCount = 0;
		for (int i = players.size() - 1; i >= 0; i--) {	
			Player player = Player.findById((long)numberLastPlaceByPlayerID[i][0]);
			int participation = KTLCRaceResult.findByPlayer(player).size();
			
			Integer numberLastPlace = numberLastPlaceByPlayerID[i][1];
			double ratio = (double)numberLastPlace / (double)participation;
			
			Rank<Integer> r = new Rank<Integer>(player, participation, ratio, numberLastPlace);
			ranking.add(currentCount, r);
			
			currentCount++;
			if (currentCount == lengthTop) { break; }
		}
		
		return ranking;		
	}
	
	/**
	 * Calculate the ranking for the player that finished at the last place of a KTLC
	 * @param players the list of players
	 * @param lengthTop the size of the top
	 * @param minPercentage the minimal percentage that the players should have played to be considered
	 * @return the list of players of size lengthTop with the highest number of last places
	 */
	private static List<Rank<Integer>> calcRankingNumberlastPlaceKTLC(List<Player> players, int lengthTop, int minPercentage) {
		List<Rank<Integer>> ranking = new ArrayList<Rank<Integer>>(lengthTop);
		int numberKTLC = KTLCEdition.findAll().size();
		
		// calculate the number of podiums by race for each player
		int[][] numberLastPlaceByPlayerID = new int[players.size()][2];
		
		int currentIndex = 0;
		for (Player player : players) {
			// store the player id in the first cell
			numberLastPlaceByPlayerID[currentIndex][0] = player.id.intValue();
			// store the results in the remaining cell
			List<KTLCResult> results = KTLCResult.findByPlayer(player);
			if (!results.isEmpty()) {
				for (KTLCResult result : results) {
					int numberPlayers = result.ktlc.results.size();
					if (result.rank == numberPlayers) {
						numberLastPlaceByPlayerID[currentIndex][1]++;
					}
				}
			}
			currentIndex++;
		}
		
		// sort the array by ASCENDING number of last place
		Arrays.sort(numberLastPlaceByPlayerID, new Comparator<int[]>() {
            @Override
            public int compare(final int[] entry1, final int[] entry2) {
                final Integer numberLastPlace1 = entry1[1];
                final Integer numberLastPlace2 = entry2[1];
                return numberLastPlace1.compareTo(numberLastPlace2);
            }
        });
		
		// create the structure for holding results,
		// but only for players with at least minPercentage of participation
		int currentCount = 0;
		for (int i = players.size() - 1; i >= 0; i--) {			
			Player player = Player.findById((long)numberLastPlaceByPlayerID[i][0]);
			int participation = KTLCResult.findByPlayer(player).size();
			
			Integer numberLastPlace = numberLastPlaceByPlayerID[i][1];
			double ratio = (double)numberLastPlace / (double)participation;
			
			if (participation >= (minPercentage*0.01*numberKTLC)) {
				Rank<Integer> r = new Rank<Integer>(player, participation, ratio, numberLastPlace);
				ranking.add(currentCount, r);
				
				currentCount++;
				if (currentCount == lengthTop) { break; }
			}			
		}
		
		return ranking;		
	}
	
	/**
	 * Calculate the ranking for the player that made a "perfect" during a KTLC (finish all the map
	 * at first place)
	 * @return the list of players that performed perfects
	 */
	private static List<Rank<List<KTLCEdition>>> calcRankingNumberPerfect() {
		List<Rank<List<KTLCEdition>>> ranking = new ArrayList<Rank<List<KTLCEdition>>>();
		HashMap<Long, List<KTLCEdition>> listPerfectByPlayerID = new HashMap<Long, List<KTLCEdition>>(); 
		
		// find all the results of ktlc where a player finished first and with an rank average of 1, 
		// which means that the player did win all the maps that he played
		List<KTLCResult> results = KTLCResult.find("rank = 1 and rankAvg = 1").fetch(); 
		
		for (KTLCResult result : results) {
			// if the player did win all the maps
			if (result.nbRaces == result.ktlc.races.size()) {
				Long playerID = Player.findByLogin(result.login.name).id;
				List<KTLCEdition> editions;
				if (listPerfectByPlayerID.containsKey(playerID)) {
					editions = listPerfectByPlayerID.get(playerID);
				} else {
					editions = new ArrayList<KTLCEdition>();
				}
				//add the ktlc to the list of perfect of the player
				editions.add(result.ktlc);
				listPerfectByPlayerID.put(playerID, editions);
			}
		}
		
		// at this step, we know the players that really did a perfect and at which editions. Now, 
		// we have to sort them to make the ranking. The sorting has to be done by number of ktlc, 
		// and if equality, by editions...
		int[][] numberPerfectByPlayerID = new int[listPerfectByPlayerID.keySet().size()][3];
		int currentIndex = 0;
		for (Long playerID : listPerfectByPlayerID.keySet()) {
			Player player = Player.findById(playerID);
			numberPerfectByPlayerID[currentIndex][0] = playerID.intValue();							// the player id
			numberPerfectByPlayerID[currentIndex][1] = listPerfectByPlayerID.get(playerID).size();	// the number of perfect
			numberPerfectByPlayerID[currentIndex][2] = KTLCResult.findByPlayer(player).size(); 		// the number of ktlc participated
			currentIndex++;
		}
		
		// sort the array by ASCENDING number of perfect
		Arrays.sort(numberPerfectByPlayerID, new Comparator<int[]>() {
            @Override
            public int compare(final int[] entry1, final int[] entry2) {
                int numPerfect1 = entry1[1];
                int numKTLC1 = entry1[2];
                int numPerfect2 = entry2[1];
                int numKTLC2 = entry2[2];
                
                if (numPerfect1 > numPerfect2) {
                	return 1;
                } else if (numPerfect1 < numPerfect2) {
                	return -1;
                } else {
                	if (numKTLC1 > numKTLC2) {
                		return -1;
                	} else if (numKTLC1 < numKTLC2) {
                		return 1;
                	} else {
                		return 0;
                	}
                }
            }
        });
		
		// create the structure for holding results
		for (int i = 0; i < listPerfectByPlayerID.keySet().size(); i++) {
			int index = listPerfectByPlayerID.keySet().size() - 1 - i;
			
			Player player = Player.findById((long)numberPerfectByPlayerID[index][0]);
			int participation = numberPerfectByPlayerID[index][2];
			List<KTLCEdition> editions = listPerfectByPlayerID.get((long)numberPerfectByPlayerID[index][0]);
			
			Rank<List<KTLCEdition>> r = new Rank<List<KTLCEdition>>(player, participation, -1, editions);
			ranking.add(i, r);
		}
		
		return ranking;
	}
	
	/**
	 * Calculate the ranking for the player that made a "epic fail" during a KTLC (eliminated at the 
	 * first lap of each race of a KTLC)
	 * @param minNumberMaps the minimal number of maps that the player should have played during a KTLC to be considered
	 * @return the list of players that performed epic fails
	 */
	private static List<Rank<List<KTLCEdition>>> calcRankingNumberEpicFail(int minNumberMaps) {
		List<Rank<List<KTLCEdition>>> ranking = new ArrayList<Rank<List<KTLCEdition>>>();
		HashMap<Long, List<KTLCEdition>> listEpicFailByPlayerID = new HashMap<Long, List<KTLCEdition>>(); 
		
		// find all the ktlc where a player finished last on all the maps (eliminated at 1st round)
		List<KTLCEdition> ktlcs = KTLCEdition.findAll();
		for (KTLCEdition ktlc : ktlcs) {
			HashMap<Player, Integer> playersEliminatedAt1stLap = new HashMap<Player, Integer>();
			
			for (KTLCRace race : ktlc.races) {
				List<KTLCRaceResult> results = race.results;
				for (KTLCRaceResult result : results) {
					// the players that has been eliminated at first lap
					if (result.roundsCount == 1) {
						Player player = Player.findByLogin(result.login.name);
						if (playersEliminatedAt1stLap.containsKey(player)) {
							int value = playersEliminatedAt1stLap.get(player);
							playersEliminatedAt1stLap.put(player, value + 1);
						} else {
							playersEliminatedAt1stLap.put(player, 1);
						}
					}
				}
			}
			
			for (Player player : playersEliminatedAt1stLap.keySet()) {
				int numberMapsPlayedByPlayer = KTLCResult.findByKTLCAndPlayer(ktlc, player).nbRaces;
				
				// if the player has been eliminated at first lap on all the maps AND played at lest x Maps,
				// then it's a Epic Fail
				if (playersEliminatedAt1stLap.get(player) == numberMapsPlayedByPlayer 
						&& playersEliminatedAt1stLap.get(player) >= minNumberMaps) {
					List<KTLCEdition> editions;
					if (listEpicFailByPlayerID.containsKey(player.id)) {
						editions = listEpicFailByPlayerID.get(player.id);
					} else {
						editions = new ArrayList<KTLCEdition>();
					}
					//add the ktlc to the list of epic fial of the player
					editions.add(ktlc);
					listEpicFailByPlayerID.put(player.id, editions);
				}
			}
		}
		
		// at this step, we know the players that really did a perfect and at which editions. Now, 
		// we have to sort them to make the ranking. The sorting has to be done by number of ktlc, 
		// and if equality, by editions...
		int[][] numberEpicFailByPlayerID = new int[listEpicFailByPlayerID.keySet().size()][3];
		int currentIndex = 0;
		for (Long playerID : listEpicFailByPlayerID.keySet()) {
			Player player = Player.findById(playerID);
			numberEpicFailByPlayerID[currentIndex][0] = playerID.intValue();							// the player id
			numberEpicFailByPlayerID[currentIndex][1] = listEpicFailByPlayerID.get(playerID).size();	// the number of epic fail
			numberEpicFailByPlayerID[currentIndex][2] = KTLCResult.findByPlayer(player).size(); 		// the number of ktlc participated
			currentIndex++;
		}
		
		// sort the array by ASCENDING number of perfect
		Arrays.sort(numberEpicFailByPlayerID, new Comparator<int[]>() {
            @Override
            public int compare(final int[] entry1, final int[] entry2) {
                int numPerfect1 = entry1[1];
                int numKTLC1 = entry1[2];
                int numPerfect2 = entry2[1];
                int numKTLC2 = entry2[2];
                
                if (numPerfect1 > numPerfect2) {
                	return 1;
                } else if (numPerfect1 < numPerfect2) {
                	return -1;
                } else {
                	if (numKTLC1 > numKTLC2) {
                		return -1;
                	} else if (numKTLC1 < numKTLC2) {
                		return 1;
                	} else {
                		return 0;
                	}
                }
            }
        });
		
		// create the structure for holding results		
		for (int i = 0; i < listEpicFailByPlayerID.keySet().size(); i++) {
			int index = listEpicFailByPlayerID.keySet().size() - 1 - i;
			
			Player player = Player.findById((long)numberEpicFailByPlayerID[index][0]);
			int participation = numberEpicFailByPlayerID[index][2];
			List<KTLCEdition> editions = listEpicFailByPlayerID.get((long)numberEpicFailByPlayerID[index][0]);
			
			//if (participation >= (MIN_PERCENTAGE_PARTICIPATIONS*0.01*numberKTLC)) {
				Rank<List<KTLCEdition>> r = new Rank<List<KTLCEdition>>(player, participation, -1, editions);
				ranking.add(i, r);
			//}
		}
		
		return ranking;
	}
	
	/**
	 * Calculate for a particular player his number of results by ranks for the KTLCs.
	 * @param player
	 * @return an array of rankLimit values, corresponding to the number of 1st, 2nd, 3rd, ... rankLimit-th places.
	 */
	private static int[] ktlcResultsByRanksByPlayer(Player player, int rankLimit) {
		List<KTLCResult> results = KTLCResult.findByPlayer(player);
		int[] podiums = new int[rankLimit];
		
		if (!results.isEmpty()) {
			for (KTLCResult result : results) {
				if (result.rank <= rankLimit) {
					podiums[result.rank - 1]++;
				}
			}
		}
		
		return podiums;
	}
	
	/**
	 * Calculate for a particular player his number of results by ranks for the races.
	 * @param player
	 * @return an array of rankLimit values, corresponding to the number of 1st, 2nd, 3rd, ... rankLimit-th places.
	 */
	private static int[] raceResultsByRanksByPlayer(Player player, int rankLimit) {
		List<KTLCRaceResult> results = KTLCRaceResult.findByPlayer(player);
		int[] podiums = new int[rankLimit];
		
		if (!results.isEmpty()) {
			for (KTLCRaceResult result : results) {
				if (result.rank <= rankLimit) {
					podiums[result.rank - 1]++;
				}
			}
		}
		
		return podiums;
	}
	
	/**
	 * Calculate for a particular player his average rank
	 * @param player
	 * @return Double.MAX_VALUE if never played
	 */
	private static double averageRankByPlayer(Player player) {
		List<KTLCResult> results = KTLCResult.findByPlayer(player);
		if (!results.isEmpty()) {
			int rank = 0;
			for (KTLCResult result : results) {
				rank += result.rank;
			}
			return (double)rank / (double)results.size();
		} else {
			return Double.MAX_VALUE;
		}
	}
}
