package controllers;

import java.util.HashMap;
import java.util.List;

import org.hibernate.hql.ast.tree.UpdateStatement;

import models.KTLCEdition;
import models.KTLCRace;
import models.KTLCRaceResult;
import models.Player;
import models.TMEnvironment;
import models.TMMap;

/**
 * 
 * @author Toub
 */
public class Statistics {

	/** The Statistics unique instance */
	private static Statistics uniqueInstance;
	
	public int numberKTLCs;
	public int numberPlayers;
	public int numberMaps;
	public int numberRuns;
	
	public HashMap<TMEnvironment, Integer> numberMapsByEnviro;
	
	public double averageNumberPlayersByKTLC;
	public double averageNumberMapsByKTLC;
	
	public KTLCEdition lastKTLC;
	public KTLCEdition firstKTLC;
	public KTLCEdition maxNumberPlayersKTLC;
	public KTLCEdition minNumberPlayersKTLC;
	
	public List<KTLCEdition> ktlcs;

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
		
		List<Player> players = Player.find("order by name asc").fetch();
		List<TMMap> maps = TMMap.find("order by environment desc").fetch();
		
		numberKTLCs = ktlcs.size();
		numberPlayers = players.size();
		numberMaps = maps.size();
		
		firstKTLC = ktlcs.get(0);
		lastKTLC = ktlcs.get(ktlcs.size() - 1);
		
		calcNumberRuns();
		calcMapsByEnviro(maps);		
		calcAverageNumberPlayers(ktlcs);
		calcAverageNumberMaps(ktlcs);
		calcMinMaxNumberPlayers(ktlcs);
	}
	
	private void calcMapsByEnviro(List<TMMap> maps) {
		numberMapsByEnviro = new HashMap<TMEnvironment, Integer>();

		for (TMMap map : maps) {
			if (numberMapsByEnviro.containsKey(map.environment)) {
				int currentCount = numberMapsByEnviro.get(map.environment);
				numberMapsByEnviro.put(map.environment, currentCount + 1);
			} else {
				numberMapsByEnviro.put(map.environment, 1);
			}
		}
	}
	
	private void calcAverageNumberPlayers(List<KTLCEdition> ktlcs) {
		int count = 0;
		for (KTLCEdition ktlc : ktlcs) {
			count += ktlc.results.size();
		}
		averageNumberPlayersByKTLC = (double)count / (double)ktlcs.size();
	}
	
	private void calcAverageNumberMaps(List<KTLCEdition> ktlcs) {
		int count = 0;
		for (KTLCEdition ktlc : ktlcs) {
			count += ktlc.races.size();
		}
		averageNumberMapsByKTLC = (double)count / (double)ktlcs.size();
	}
	
	private void calcMinMaxNumberPlayers(List<KTLCEdition> ktlcs) {
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
	private void calcNumberRuns() {
		// get only the results from the winner of each race
		List<KTLCRaceResult> results = KTLCRaceResult.find("rank = 1").fetch();
	
		int count = 0;
		for (KTLCRaceResult result : results) {
			count += result.roundsCount;
		}		
		numberRuns = count;
	}
}
