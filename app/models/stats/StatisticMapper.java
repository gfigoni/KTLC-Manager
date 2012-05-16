package models.stats;

import java.util.HashMap;
import java.util.List;

import models.Player;
import models.TMEnvironment;

public class StatisticMapper {
	
	public Player mapper;
	public int MIN_PERCENTAGE;

	public HashMap<TMEnvironment, Integer> chart_numberMapsByEnviro;

	public StatsEntry createdMaps;
	public StatsEntry distinctPlayersOnMaps;
	public int numberRunsOnMaps;
	public int numberDistinctKTLCsAsMapper;

	public List<TMEnvironment> favoriteMappingEnviros;

	public double averageNumberPlayersOnMaps;

}
