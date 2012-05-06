package controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import play.db.jpa.Model;

import models.KTLCEdition;
import models.KTLCRace;
import models.KTLCRaceResult;
import models.KTLCResult;
import models.Player;
import models.TMEnvironment;
import models.TMMap;
import models.stats.GeneralStatistic;
import models.stats.Rank;

/**
 * This class is used to generate general statistics regarding the whole KTLC history.
 * @author Toub
 */
public class Statistics {
	/** The number of results that we want to have in the rankings */
	private static final int LENGTH_TOP = 15;
	/** The ranks that are counted for the podiums */
	private static final int RANK_INTEREST = 4;
	/** The minimal number of participation that are required to be counted in some TOPs */
	private static final int MIN_PERCENTAGE_PARTICIPATIONS = 10;
	
	/**
	 * Update all the statistics, should be called only using administrator board... 
	 */
	public static GeneralStatistic updateAllGeneralStatistics(GeneralStatistic stats) {
		
		stats.LENGTH_TOP = LENGTH_TOP;
		stats.RANK_INTEREST = RANK_INTEREST;
		stats.MIN_PERCENTAGE_PARTICIPATIONS = MIN_PERCENTAGE_PARTICIPATIONS;
		
		stats.ktlcs = KTLCEdition.find("order by date asc").fetch();
		stats.players = Player.find("order by name asc").fetch();
		stats.maps = TMMap.find("order by environment desc").fetch();
		
		// Stats
		stats.stats_numberKTLCs = stats.ktlcs.size();
		stats.stats_numberKTLC_TMU = calcNumberKTLCTMU(stats.ktlcs);
		stats.stats_numberSuperKTLC_TMU = calcNumberSuperKTLCTMU(stats.ktlcs);
		stats.stats_numberKTLC_TM2 = calcNumberKTLCTM2(stats.ktlcs);
		stats.stats_numberPlayers = stats.players.size();
		stats.stats_numberMaps = stats.maps.size();
		stats.stats_numberRuns = calcTotalNumberRuns();
		stats.stats_averageNumberPlayersByKTLC = calcAverageNumberPlayersByKTLC(stats.ktlcs);
		stats.stats_averageNumberMapsByKTLC = calcAverageNumberMapsByKTLC(stats.ktlcs);
		
		// charts
		stats.chart_numberMapsByEnviro = calcMapsByEnviro(stats.maps);
		
		// particular KTLC
		stats.smallestKTLC = calcMinNumberPlayers(stats.ktlcs);
		stats.biggestKTLC = calcMaxNumberPlayers(stats.ktlcs);
		
		// rankings - hall of fame
		stats.ranking_numberParticipation = calcRankingParticipatioRatio(stats.players);
		stats.ranking_bestAverageRank = calcRankingBestAverageRank(stats.players);
		stats.ranking_numberMaps = calcRankingNumberMaps(stats.players);
		stats.ranking_numberPodiumsKTLC = calcRankingNumberPodiumsKTLC(stats.players);
		stats.ranking_numberPodiumsRace = calcRankingNumberPodiumsRace(stats.players);
		stats.ranking_numberPerfect = calcRankingNumberPerfect();
		
		for (int i = 0; i < stats.ranking_numberPerfect.size(); i++) {
			System.out.println("player: " + stats.ranking_numberPerfect.get(i).player.name +", numPerfect: " + stats.ranking_numberPerfect.get(i).value.size() + ", numKTLC: " + stats.ranking_numberPerfect.get(i).participation);
			for (KTLCEdition edition : stats.ranking_numberPerfect.get(i).value) {
				System.out.println("\t"+edition.number);
			}
		}
		
		
		// ranking - hall of shame
		List<KTLCRace> races = KTLCRace.findAll();
		stats.ranking_violentMaps = calcRankingViolentMaps(races);
		stats.ranking_numberLastPlaceKTLC = calcRankingNumberlastPlaceKTLC(stats.players);
		stats.ranking_numberLastPlaceRace = calcRankingNumberlastPlaceRace(stats.players);
		stats.ranking_worstAverageRank = calcRankingWorstAverageRank(stats.players);
		
		// change the status
		stats.setInitialized(true);
		return stats;
	}
	
	/**
	 * Count the number of maps by TMEnvironment
	 * @param maps the list of maps that have to be used
	 * @return a HashMap with the environment as keys and count as value
	 */
	public static HashMap<TMEnvironment, Integer> calcMapsByEnviro(List<TMMap> maps) {
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
	 * TODO
	 * @param ktlcs
	 * @return
	 */
	public static int calcNumberKTLCTMU(List<KTLCEdition> ktlcs) {
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
	 * TODO
	 * @param ktlcs
	 * @return
	 */
	public static int calcNumberSuperKTLCTMU(List<KTLCEdition> ktlcs) {
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
	 * TODO
	 * @param ktlcs
	 * @return
	 */
	public static int calcNumberKTLCTM2(List<KTLCEdition> ktlcs) {
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
	 * @param ktlcs
	 * @return the average number of players
	 */
	public static double calcAverageNumberPlayersByKTLC(List<KTLCEdition> ktlcs) {
		int count = 0;
		for (KTLCEdition ktlc : ktlcs) {
			count += ktlc.results.size();
		}
		return (double)count / (double)ktlcs.size();
	}
	
	/**
	 * Calculate the average number of maps for a list of KTLCs
	 * @param ktlcs
	 * @return the average number of maps
	 */
	public static double calcAverageNumberMapsByKTLC(List<KTLCEdition> ktlcs) {
		int count = 0;
		for (KTLCEdition ktlc : ktlcs) {
			count += ktlc.races.size();
		}
		return (double)count / (double)ktlcs.size();
	}
	
	/**
	 * Find the ktlc with the smallest number of players in a list of KTLCs
	 * @param ktlcs
	 * @return the smallest KTLCEdition
	 */
	public static KTLCEdition calcMinNumberPlayers(List<KTLCEdition> ktlcs) {
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
	 * @param ktlcs
	 * @return the biggest KTLCEdition
	 */
	public static KTLCEdition calcMaxNumberPlayers(List<KTLCEdition> ktlcs) {
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
	public static int calcTotalNumberRuns() {
		// get only the results from the winner of each race
		List<KTLCRaceResult> results = KTLCRaceResult.find("rank = 1").fetch();
	
		int count = 0;
		for (KTLCRaceResult result : results) {
			count += result.roundsCount;
		}		
		return count;
	}
	
	/**
	 * Calculate the ranking regarding the Participation Ratio of a list of players, 
	 * on ALL the KTLCs.
	 * @param players
	 * @return the list of size LENGTH_TOP of the best players with their values
	 */
	public static List<Rank> calcRankingParticipatioRatio(List<Player> players) {
		List<Rank> ranking = new ArrayList<Rank>(LENGTH_TOP);
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
		for (int i = 0; i < LENGTH_TOP; i++) {
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
	 * @param players
	 * @return the list of size LENGTH_TOP of the best players with their values
	 */
	public static List<Rank<Double>> calcRankingBestAverageRank(List<Player> players) {
		List<Rank<Double>> ranking = new ArrayList<Rank<Double>>(LENGTH_TOP);
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
			
			if (numberParticipation >= (MIN_PERCENTAGE_PARTICIPATIONS*0.01*numberKTLC)) {
				Rank<Double> r = new Rank<Double>(player, numberParticipation, -1, value);
				ranking.add(currentCount, r);
				
				currentCount++;
				if (currentCount == LENGTH_TOP) { break; }
			}
		}
		
		return ranking;
	}
	
	/**
	 * Calculate the ranking regarding the Worst Average Rank of a list of players, 
	 * on ALL the KTLCs.
	 * @param players
	 * @return the list of size LENGTH_TOP of the best players with their values
	 */
	public static List<Rank<Double>> calcRankingWorstAverageRank(List<Player> players) {
		List<Rank<Double>> ranking = new ArrayList<Rank<Double>>(LENGTH_TOP);
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
			
			if (numberParticipation >= (MIN_PERCENTAGE_PARTICIPATIONS*0.01*numberKTLC)) {
				Rank<Double> r = new Rank<Double>(player, numberParticipation, -1, value);
				ranking.add(currentCount, r);
				
				currentCount++;
				if (currentCount == LENGTH_TOP) { break; }
			}
		}
		
		return ranking;
	}
	
	/**
	 * Calculate the ranking regarding the Number of map created by the players from the list, 
	 * on ALL the KTLCs.
	 * @param players
	 * @return the list of size LENGTH_TOP of the best players with their values
	 */
	public static List<Rank<Integer>> calcRankingNumberMaps(List<Player> players) {
		List<Rank<Integer>> ranking = new ArrayList<Rank<Integer>>(LENGTH_TOP);
		
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
		for (int i = 0; i < LENGTH_TOP; i++) {
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
	 * @param players
	 * @return the list of size LENGTH_TOP of the best players with their values
	 */
	public static List<Rank<int[]>> calcRankingNumberPodiumsKTLC(List<Player> players) {
		List<Rank<int[]>> ranking = new ArrayList<Rank<int[]>>(LENGTH_TOP);
		
		// calculate the number of podiums by KTLC for each player
		int[][] numberPodiumByPlayerID = new int[players.size()][RANK_INTEREST + 1];
		
		int currentIndex = 0;
		for (Player player : players) {
			// store the player id in the first cell
			numberPodiumByPlayerID[currentIndex][0] = player.id.intValue();
			// store the results in the remaining cells
			List<KTLCResult> results = KTLCResult.findByPlayer(player);
			if (!results.isEmpty()) {
				for (KTLCResult result : results) {
					if (result.rank <= RANK_INTEREST) {
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
		for (int i = 0; i < LENGTH_TOP; i++) {			
			int index = players.size() - 1 - i;
			
			Player player = Player.findById((long)numberPodiumByPlayerID[index][0]);
			int participation = KTLCResult.findByPlayer(player).size();
			
			int[] podiums = Arrays.copyOfRange(numberPodiumByPlayerID[index], 1, RANK_INTEREST + 1);
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
	 * @param players
	 * @return the list of size LENGTH_TOP of the best players with their values
	 */
	public static List<Rank<int[]>> calcRankingNumberPodiumsRace(List<Player> players) {
		List<Rank<int[]>> ranking = new ArrayList<Rank<int[]>>();
		
		// calculate the number of podiums by race for each player
		int[][] numberPodiumByPlayerID = new int[players.size()][RANK_INTEREST + 1];
		
		int currentIndex = 0;
		for (Player player : players) {
			// store the player id in the first cell
			numberPodiumByPlayerID[currentIndex][0] = player.id.intValue();
			// store the results in the remaining cells
			List<KTLCRaceResult> results = KTLCRaceResult.findByPlayer(player);
			if (!results.isEmpty()) {
				for (KTLCRaceResult result : results) {
					if (result.rank <= RANK_INTEREST) {
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
		for (int i = 0; i < LENGTH_TOP; i++) {
			int index = players.size() - 1 - i;
			
			Player player = Player.findById((long)numberPodiumByPlayerID[index][0]);
			int participation = KTLCRaceResult.findByPlayer(player).size();
			
			int[] podiums = Arrays.copyOfRange(numberPodiumByPlayerID[index], 1, RANK_INTEREST + 1);
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
	 * @param races
	 * @return the list of size LENGTH_TOP of the most violent races with their values
	 */
	public static List<Rank<KTLCRace>> calcRankingViolentMaps(List<KTLCRace> races) {
		List<Rank<KTLCRace>> ranking = new ArrayList<Rank<KTLCRace>>(LENGTH_TOP);
		
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
		for (int i = 0; i < LENGTH_TOP; i++) {
			int index = races.size() - 1 - i;
			
			KTLCRace race = KTLCRace.findById((long)numberEliminationFirstLapByRaceID[index][0]);
			Player author = Player.findByLogin(race.map.login.name);
			// the number of elimination
			int participation = numberEliminationFirstLapByRaceID[index][1];

			Rank<KTLCRace> r = new Rank<KTLCRace>(author, participation, -1, race);
			ranking.add(i, r);
		}
		
		return ranking;
	}
	
	/**
	 * TODO
	 * @param players
	 * @return
	 */
	public static List<Rank<Integer>> calcRankingNumberlastPlaceRace(List<Player> players) {
		List<Rank<Integer>> ranking = new ArrayList<Rank<Integer>>(LENGTH_TOP);
		
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
		for (int i = 0; i < LENGTH_TOP; i++) {
			int index = players.size() - 1 - i;
			
			Player player = Player.findById((long)numberLastPlaceByPlayerID[index][0]);
			int participation = KTLCRaceResult.findByPlayer(player).size();
			
			Integer numberLastPlace = numberLastPlaceByPlayerID[index][1];
			double ratio = (double)numberLastPlace / (double)participation;
			
			Rank<Integer> r = new Rank<Integer>(player, participation, ratio, numberLastPlace);
			ranking.add(i, r);
		}
		
		return ranking;		
	}
	
	/**
	 * TODO
	 * @param players
	 * @return
	 */
	public static List<Rank<Integer>> calcRankingNumberlastPlaceKTLC(List<Player> players) {
		List<Rank<Integer>> ranking = new ArrayList<Rank<Integer>>(LENGTH_TOP);
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
		// but only for players with at least MIN_PARTICIPATION
		int currentCount = 0;
		for (int i = players.size() - 1; i >= 0; i--) {			
			Player player = Player.findById((long)numberLastPlaceByPlayerID[i][0]);
			int participation = KTLCResult.findByPlayer(player).size();
			
			Integer numberLastPlace = numberLastPlaceByPlayerID[i][1];
			double ratio = (double)numberLastPlace / (double)participation;
			
			if (participation >= (MIN_PERCENTAGE_PARTICIPATIONS*0.01*numberKTLC)) {
				Rank<Integer> r = new Rank<Integer>(player, participation, ratio, numberLastPlace);
				ranking.add(currentCount, r);
				
				currentCount++;
				if (currentCount == LENGTH_TOP) { break; }
			}			
		}
		
		return ranking;		
	}
	
	/**
	 * TODO
	 * @param players
	 * @return
	 */
	public static List<Rank<List<KTLCEdition>>> calcRankingNumberPerfect() {
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
		
		// sort the array by ASCENDING number of last place
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
		int currentCount = 0;
		for (int i = 0; i < LENGTH_TOP; i++) {
			int index = listPerfectByPlayerID.keySet().size() - 1 - i;
			
			Player player = Player.findById((long)numberPerfectByPlayerID[index][0]);
			int participation = numberPerfectByPlayerID[index][2];
			List<KTLCEdition> editions = listPerfectByPlayerID.get((long)numberPerfectByPlayerID[index][0]);
			
			Rank<List<KTLCEdition>> r = new Rank<List<KTLCEdition>>(player, participation, -1, editions);
			ranking.add(i, r);
			currentCount++;
			
			if (currentCount == listPerfectByPlayerID.keySet().size()) {
				break;
			}
		}
		
		return ranking;
	}
	
	/**
	 * Calculate for a particular player his participation ratio (# participation / # KTLCs).
	 * @param player
	 * @return value between 0 and 1
	 */
	public static double participationRatioByPlayer(Player player) {
		double numberParticipation = KTLCResult.findByPlayer(player).size();
		double numberKTLCs = KTLCEdition.findAll().size();
		return numberParticipation / numberKTLCs;
	}
	
	/**
	 * Calculate for a particular player his number of podiums by KTLC.
	 * @param player
	 * @return an array of 3 values, corresponding to the number of 1st, 2nd and 3rd places.
	 */
	public static int[] numberPodiumsKTLCByPlayer(Player player) {
		List<KTLCResult> results = KTLCResult.findByPlayer(player);
		int[] podiums = new int[RANK_INTEREST];
		
		if (!results.isEmpty()) {
			for (KTLCResult result : results) {
				if (result.rank <= RANK_INTEREST) {
					podiums[result.rank - 1]++;
				}
			}
		}
		
		return podiums;
	}
	
	/**
	 * Calculate for a particular player his number of podiums by race.
	 * @param player
	 * @return an array of 3 values, corresponding to the number of 1st, 2nd and 3rd places.
	 */
	public static int[] numberPodiumsRaceByPlayer(Player player) {
		List<KTLCRaceResult> results = KTLCRaceResult.findByPlayer(player);
		int[] podiums = new int[RANK_INTEREST];
		
		if (!results.isEmpty()) {
			for (KTLCRaceResult result : results) {
				if (result.rank <= RANK_INTEREST) {
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
	public static double averageRankByPlayer(Player player) {
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
