package models.stats;

import java.util.HashMap;
import java.util.List;

import models.Player;
import models.TMEnvironment;

public class StatisticPlayer {
	
	public int RANK_LIMIT;
	
	public Player player;
	
	// stats player
	public int totalMaps;
	public double ratioMaps;
	
	public int numberPlayedKTLCs;
	public int totalKTLCs;
	public double partRatioKTLC;
	
	public int numberPlayedKTLC_TMU;
	public int totalKTLC_TMU;
	public double partRatioKTLC_TMU;
	
	public int numberPlayedSuperKTLC_TMU;
	public int totalSuperKTLC_TMU;
	public double partRatioSuperKTLC_TMU;
	
	public int numberPlayedKTLC_TM2;
	public int totalKTLC_TM2;
	public double partRatioKTLC_TM2;
	
	public int numberPlayedRaces;
	public int numberPlayedRuns;
	
	public double averageRank;
	public double averageNumberOpponents;
	
	public List<TMEnvironment> bestEnviroFromAvgRank;
	public List<TMEnvironment> bestEviroFromPodiums;
	
	public int[] chart_ranksByKTLCs;
	public int[] chart_ranksByRaces;
	
	public HashMap<TMEnvironment, Double[]> chart_averageRankByEnviro;
	public HashMap<TMEnvironment, int[]> chart_numberPodiumsByEnviro;
	
	// stats mapper
	public HashMap<TMEnvironment, Integer> chart_numberMapsByEnviro;
	
	public int numberCreatedMaps;
	public int numberDistinctPlayersOnMaps;
	public int numberRunsOnMaps;
	public int numberDistinctKTLCsAsMapper;
	
	public List<TMEnvironment> favoriteMappingEnviros;
	
	public double averageNumberPlayersOnMaps;
}
