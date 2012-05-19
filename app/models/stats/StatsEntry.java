package models.stats;

/**
 * This class is used to create easy to use statistic values.
 * For instance, a statistic entry could by "the number of KTLCs played by the player". The value
 * will then be the number of KTLC he played, the total the number of KTLCs that where played in the
 * whole KTLC history, and the ratio the ratio of participation of the player. 
 *
 * @author Toub
 */
public class StatsEntry {
	
	/** The current value of the stats that where computed */
	public int value;
	/** The total (maximum) that could have been reached */
	public int total;
	/** The ratio between the value and the total */
	public double ratio;
	
	/**
	 * Main constructor
	 * @param value
	 * @param total
	 */
	public StatsEntry(int value, int total) {
		this.value = value;
		this.total = total;
		if (value != 0 && total != 0) {
			this.ratio = this.value / (double)this.total;
		} else {
			this.ratio = 0;
		}
	}

}
