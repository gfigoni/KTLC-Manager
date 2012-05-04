package models;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * @author Toub
 */
public class GeneralStatistic {
	
	/** The GeneralStatistic unique instance */
	private static GeneralStatistic uniqueInstance;
	/** The initialization tag */
	private boolean initialized;
	/** The creation date of the statistics */
	private Date creationDate; 
	
	public int LENGTH_TOP;
	public int RANK_INTEREST;
	public int MIN_PERCENTAGE_PARTICIPATIONS;
	
	public List<KTLCEdition> ktlcs;
	public List<Player> players;
	public List<TMMap> maps;
	
	public KTLCEdition biggestKTLC;
	public KTLCEdition smallestKTLC;
	
	public int 	  stats_numberKTLCs;
	public int 	  stats_numberPlayers;
	public int 	  stats_numberMaps;
	public int 	  stats_numberRuns;
	
	public double stats_averageNumberPlayersByKTLC;
	public double stats_averageNumberMapsByKTLC;
	
	public HashMap<TMEnvironment, Integer> chart_numberMapsByEnviro;
	
	public List<Rank> 		 	ranking_numberParticipation;
	public List<Rank<Double>> 	ranking_averageRank;
	public List<Rank<Integer>>	ranking_numberMaps;
	public List<Rank<int[]>> 	ranking_numberPodiumsRTLC;
	public List<Rank<int[]>>	ranking_numberPodiumsRace;

	/** Private constructor for implementation of the singleton pattern */
	private GeneralStatistic() { 
		initialized = false;
	}
	
	/**
	 * Get the unique statistics
	 * @return the uniqueInstace statistic
	 */
	public static GeneralStatistic getUniqueInstance() {
		if (uniqueInstance == null) {
			// init the class
			uniqueInstance = new GeneralStatistic();
		}
		return uniqueInstance;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
		if(initialized) {
			creationDate = new Date();
		}
	}
	
	public Date getCreationDate() {
		return creationDate;
	}
 
}
