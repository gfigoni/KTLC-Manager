package models.stats;

import java.util.HashMap;
import java.util.List;


import models.KTLCEdition;
import models.Player;
import models.TMEnvironment;

public class StatisticPlayer {
	
	public int RANK_LIMIT;
	public int MIN_PERCENTAGE;
	
	public Player player;
	
	public StatsEntry playedKTLCs;
	public StatsEntry playedKTLC_TMU;
	public StatsEntry playedSuperKTLC_TMU;
	public StatsEntry playedKTLC_TM2;
	
	public int numberPlayedRaces;
	public int numberPlayedRuns;
	public List<KTLCEdition> perfects;
	public List<KTLCEdition> epicFails;
	public List<KTLCEdition> longestPodiumSerie;
	public StatsEntry numberLastPlaceRace;
	public StatsEntry numberLastPlaceKTLC;
	public StatsEntry numberPodiumsRace;
	public StatsEntry numberPodiumsKTLC;
	
	public double averageRank;
	public double averageNumberOpponents;
	
	public List<TMEnvironment> bestEnviroFromAvgRank;
	public List<TMEnvironment> bestEviroFromPodiums;
	
	public int[] chart_ranksByKTLCs;
	public int[] chart_ranksByRaces;
	
	public HashMap<TMEnvironment, Double[]> chart_averageRankByEnviro;
	public HashMap<TMEnvironment, int[]> chart_numberPodiumsByEnviro;
	
	public int[] timeFromRounds(){
		if(numberPlayedRuns > 0) {
			int minsByDay = 60 * 24;
			int minsByHour = 60;
			
			int mnumberMinutes = numberPlayedRuns;
			
			int numberDays = mnumberMinutes / minsByDay;
			mnumberMinutes = mnumberMinutes % minsByDay;
			
			int numberHours = mnumberMinutes / minsByHour;
			mnumberMinutes = mnumberMinutes % minsByHour;
			
			return new int[]{numberDays, numberHours, mnumberMinutes};
		} else {
			return null;
		}
	}
}
