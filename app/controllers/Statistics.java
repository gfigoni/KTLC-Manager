package controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import models.KTLCEdition;
import models.KTLCRaceResult;
import models.KTLCResult;
import models.Player;
import models.TMEnvironment;
import models.TMMap;

/**
 * 
 * @author Toub
 */
public class Statistics {
	/** The number of results that we want to have in the rankings */
	private static final int LENGTH_TOP = 10;
	/** The ranks that are counted for the podiums */
	private static final int RANK_INTEREST = 4;
	/** The minimal number of participation that are required to be counted in some TOPs */
	private static final int MIN_PERCENTAGE_PARTICIPATIONS = 10;

	/** The Statistics unique instance */
	private static Statistics uniqueInstance;
	
	public List<KTLCEdition> ktlcs;
	public List<Player> players;
	public List<TMMap> maps;
	
	public KTLCEdition maxNumberPlayersKTLC;
	public KTLCEdition minNumberPlayersKTLC;
	
	public int 		stats_numberKTLCs;
	public int 		stats_numberPlayers;
	public int 		stats_numberMaps;
	public int 		stats_numberRuns;
	
	public double 	stats_averageNumberPlayersByKTLC;
	public double 	stats_averageNumberMapsByKTLC;
	
	public HashMap<TMEnvironment, Integer> chart_numberMapsByEnviro;
	
	public List<Player>  top_numberParticipation_Players;
	public List<Integer> top_numberParticipation_Values;
	public List<Double>  top_numberParticipation_Ratios;
	
	public List<Player>  top_numberPodiumsKTLC_Players;
	public List<int[]>   top_numberPodiumsKTLC_Values;
	public List<Double>  top_numberPodiumsKTLC_Ratios;
	public List<Integer> top_numberPodiumsKTLC_Participations;
	
	public List<Player>  top_numberPodiumsRace_Players;
	public List<int[]>   top_numberPodiumsRace_Values;
	public List<Double>  top_numberPodiumsRace_Ratios;
	public List<Integer> top_numberPodiumsRace_Participations;
	
	public List<Player>  top_numberMaps_Players;
	public List<Integer> top_numberMaps_Values;
	
	public List<Player>  top_averageRank_Players;
	public List<Double>  top_averageRank_Values;
	public List<Integer> top_averageRank_Participations;

	/** Private constructor for implementation of the singleton pattern */
	private Statistics() {
		updateStatistics();
	}
	
	/**
	 * Get the unique statistics
	 * @return the uniqueInstace statistics
	 */
	public static Statistics getUniqueInstance() {
		//if (uniqueInstance == null) { //TODO remove "//"
			// init the class
			uniqueInstance = new Statistics();
		//}
		return uniqueInstance;
	}
	
	/**
	 * Update all the statistics, should be called only using administrator board... 
	 */
	private void updateStatistics() {		
		ktlcs = KTLCEdition.find("order by date asc").fetch();
		players = Player.find("order by name asc").fetch();
		maps = TMMap.find("order by environment desc").fetch();
		
		// Stats
		stats_numberKTLCs = ktlcs.size();
		stats_numberPlayers = players.size();
		stats_numberMaps = maps.size();
		stats_numberRuns = calcNumberRuns();
		stats_averageNumberPlayersByKTLC = calcAverageNumberPlayersByKTLC();
		stats_averageNumberMapsByKTLC = calcAverageNumberMapsByKTLC();
		
		// charts
		chart_numberMapsByEnviro = calcMapsByEnviro();
		
		// particular KTLC
		calcMinMaxNumberPlayers();
		
		// tops
		calcTOPxParticipationRatio();
		calcTOPxNumberPodiumsKTLC();
		calcTOPxNumberPodiumsRace();
		calcTOPxNumberMapsByPlayer();
		calcTOPxAverageRankByKTLC();
	}
	
	/**
	 * TODO
	 */
	private HashMap<TMEnvironment, Integer> calcMapsByEnviro() {
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
	 */
	private double calcAverageNumberPlayersByKTLC() {
		int count = 0;
		for (KTLCEdition ktlc : ktlcs) {
			count += ktlc.results.size();
		}
		return (double)count / (double)ktlcs.size();
	}
	
	/**
	 * TODO
	 */
	private double calcAverageNumberMapsByKTLC() {
		int count = 0;
		for (KTLCEdition ktlc : ktlcs) {
			count += ktlc.races.size();
		}
		return (double)count / (double)ktlcs.size();
	}
	
	/**
	 * TODO
	 */
	private void calcMinMaxNumberPlayers() {
		int max = Integer.MIN_VALUE;
		int min = Integer.MAX_VALUE;
		for (KTLCEdition ktlc : ktlcs) {
			if (ktlc.results.size() > max) {
				max = ktlc.results.size();
				maxNumberPlayersKTLC = ktlc;
			}
			if (ktlc.results.size() < min) {
				min = ktlc.results.size();
				minNumberPlayersKTLC = ktlc;
			}
		}
	}
	
	/**
	 * Calculate the number of played runs by getting the results of the winner of each maps.
	 * The number of runs is the sum of the number of round played by each winner
	 * @return the total number of runs
	 */
	private int calcNumberRuns() {
		// get only the results from the winner of each race
		List<KTLCRaceResult> results = KTLCRaceResult.find("rank = 1").fetch();
	
		int count = 0;
		for (KTLCRaceResult result : results) {
			count += result.roundsCount;
		}		
		return count;
	}
	
	/**
	 * TODO TOP10
	 */
	private void calcTOPxParticipationRatio() {
		top_numberParticipation_Players = new ArrayList<Player>(LENGTH_TOP);
		top_numberParticipation_Values = new ArrayList<Integer>(LENGTH_TOP);
		top_numberParticipation_Ratios = new ArrayList<Double>(LENGTH_TOP);
		
		// calculate the participation ratio for each player
		Double[][] participationRatiosByPlayerID = new Double[players.size()][2];
		int currentIndex = 0;
		for (Player player : players) {
			int numberParticipation = KTLCResult.findByPlayer(player).size();
			participationRatiosByPlayerID[currentIndex][0] = (double)player.id;
			participationRatiosByPlayerID[currentIndex][1] = (double)numberParticipation / (double)ktlcs.size();
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
		
		// create the structure for holding results
		for (int i = 0; i < LENGTH_TOP; i++) {
			int index = players.size() - 1 - i;
			Player player = Player.findById(participationRatiosByPlayerID[index][0].longValue());
			Double ratio = participationRatiosByPlayerID[index][1];
			Integer numberparticipations = KTLCResult.findByPlayer(player).size();
			
			top_numberParticipation_Players.add(i, player);
			top_numberParticipation_Ratios.add(i, ratio);
			top_numberParticipation_Values.add(i, numberparticipations);
		}
	}
	
	/**
	 * TODO TOP10
	 */
	private void calcTOPxNumberPodiumsKTLC() {
		top_numberPodiumsKTLC_Players = new ArrayList<Player>(LENGTH_TOP);
		top_numberPodiumsKTLC_Participations = new ArrayList<Integer>(LENGTH_TOP);
		top_numberPodiumsKTLC_Values = new ArrayList<int[]>(LENGTH_TOP);
		top_numberPodiumsKTLC_Ratios = new ArrayList<Double>(LENGTH_TOP);
		
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
			int numberParticipation = KTLCResult.findByPlayer(player).size();
			
			int[] podiums = Arrays.copyOfRange(numberPodiumByPlayerID[index], 1, RANK_INTEREST + 1);
			int totalPodium = podiums[0] + podiums[1] + podiums[3];
			
			top_numberPodiumsKTLC_Players.add(i, player);
			top_numberPodiumsKTLC_Participations.add(i, numberParticipation);
			top_numberPodiumsKTLC_Values.add(i, podiums);
			top_numberPodiumsKTLC_Ratios.add(i, (double)totalPodium / (double)numberParticipation);
		}
	}
	
	/**
	 * TODO TOP10
	 */
	private void calcTOPxNumberPodiumsRace() {
		top_numberPodiumsRace_Players = new ArrayList<Player>(LENGTH_TOP);
		top_numberPodiumsRace_Participations = new ArrayList<Integer>();
		top_numberPodiumsRace_Values = new ArrayList<int[]>(LENGTH_TOP);
		top_numberPodiumsRace_Ratios = new ArrayList<Double>(LENGTH_TOP);
		
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
			int numberPlayedMap = KTLCRaceResult.findByPlayer(player).size();
			
			int[] podiums = Arrays.copyOfRange(numberPodiumByPlayerID[index], 1, RANK_INTEREST + 1);
			int totalPodium = podiums[0] + podiums[1] + podiums[3];
			
			top_numberPodiumsRace_Players.add(i, player);
			top_numberPodiumsRace_Participations.add(i, numberPlayedMap);
			top_numberPodiumsRace_Values.add(i, podiums);
			top_numberPodiumsRace_Ratios.add(i, (double)totalPodium / (double)numberPlayedMap);
		}
	}
	
	/**
	 * TODO TOP10
	 */
	private void calcTOPxNumberMapsByPlayer() {
		top_numberMaps_Players = new ArrayList<Player>(LENGTH_TOP);
		top_numberMaps_Values = new ArrayList<Integer>(LENGTH_TOP);
		
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
			int value = numberMapsByPlayerID[index][1];
			
			top_numberMaps_Players.add(i, player);
			top_numberMaps_Values.add(i, value);
		}
	}
	
	/**
	 * TODO TOP10
	 */
	private void calcTOPxAverageRankByKTLC() {
		top_averageRank_Players = new ArrayList<Player>(LENGTH_TOP);
		top_averageRank_Values = new ArrayList<Double>(LENGTH_TOP);
		top_averageRank_Participations = new ArrayList<Integer>(LENGTH_TOP);
		
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
			
			if (numberParticipation >= (MIN_PERCENTAGE_PARTICIPATIONS*ktlcs.size()/100)) {
				top_averageRank_Players.add(currentCount, player);
				top_averageRank_Values.add(currentCount, value);
				top_averageRank_Participations.add(currentCount, numberParticipation);
				currentCount++;
				
				if (currentCount == LENGTH_TOP) { break; }
			}
		}
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
