package models.stats;

import java.util.HashMap;

import models.Player;
import models.TMEnvironment;

public class StatisticPlayer {
	
	public int RANK_LIMIT;
	
	public Player player;
	
	public int totalKTLCs;
	public int totalRaces;
	public int totalRuns;
	
	public int createdMaps;
	public int totalMaps;
	public double ratioMaps;
	
	public int numberKTLC;	
	public double partRatioKTLC;
	
	public int numberKTLC_TMU;
	public int totalKTLC_TMU;
	public double partRatioKTLC_TMU;
	
	public int numberSuperKTLC_TMU;
	public int totalSuperKTLC_TMU;
	public double partRatioSuperKTLC_TMU;
	
	public int numberKTLC_TM2;
	public int totalKTLC_TM2;
	public double partRatioKTLC_TM2;
	
	public int numberMapsPlayed;
	public int numberRunsPlayed;
	
	public double averageRank;
	
	public int[] chart_ranksByKTLCs;
	public int[] chart_ranksByRaces;
	
	public HashMap<TMEnvironment, Integer> chart_numberMapsByEnviro;
}
