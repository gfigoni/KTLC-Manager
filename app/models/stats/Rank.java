package models.stats;

import models.Player;

/**
 * This class is used to create ranking and store data about players
 * @author Toub
 *
 * @param <V> the generic value that is used to make the ranking
 */
public class Rank<V> {
	
	/** The concerned player */
	public Player player;
	/** The first login of the player */
	public String login;
	/** The number of participation of the player for the current ranking */
	public int participation;
	/** The ration of participation of the player for the current ranking */
	public double ratio;
	/** The generic value, which can be used to store data required for the ranking */
	public V value;
	
	/**
	 * No particular value
	 * @param player
	 * @param participation
	 * @param ratio
	 */
	public Rank(Player player, int participation, double ratio) {
		this.player = player;
		if (player != null) {
			login = player.logins.get(0).name;
		} else {
			login = null;
		}
		this.participation = participation;
		this.ratio = ratio;
		value = null;
	}
	
	/**
	 * Single value
	 * @param player
	 * @param participation
	 * @param ratio
	 * @param value
	 */	
	public Rank(Player player, int participation, double ratio, V value) {
		this.player = player;
		if (player != null) {
			login = player.logins.get(0).name;
		} else {
			login = null;
		}
		this.participation = participation;
		this.ratio = ratio;
		this.value = value;
	}
}
