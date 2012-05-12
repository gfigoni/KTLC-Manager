package models.stats;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class StatisticConfig {

	/** The number of results that we want to have in the rankings */
	private int lengthTop = 15;
	public static final int MIN_LENGTH = 5;
	public static final int MAX_LENGTH = 50;
	
	/** The ranks that are counted for the podiums */
	private int rankInterest = 4;
	
	/** The minimal number of participation that are required to be counted in some TOPs */
	private int minPercentageParticipations = 10;
	public static final int MIN_PERCENTAGE = 0;
	public static final int MAX_PERCENTAGE = 75;
	
	/** The minimal number of map that a player have to play to be counted in the epic fail ranking */
	private int minNumberMapsForEpicFail = 2;
	public static final int MIN_EPICFAIL = 1;
	public static final int MAX_EPICFAIL = 4;
	
	/** The number of rank to study for the player statistics */
	private int rankLimit = 20;
	public static final int MIN_RANKLIMIT = 5;
	public static final int MAX_RANKLIMIT = 50;

	public int getLengthTop() {
		return lengthTop;
	}

	@XmlElement
	public void setLengthTop(int lengthTop) {
		this.lengthTop = lengthTop;
	}

	public int getRankInterest() {
		return rankInterest;
	}

	@XmlElement
	public void setRankInterest(int rankInterest) {
		this.rankInterest = rankInterest;
	}

	public int getMinPercentageParticipations() {
		return minPercentageParticipations;
	}

	@XmlElement
	public void setMinPercentageParticipations(int minPercentageParticipations) {
		this.minPercentageParticipations = minPercentageParticipations;
	}

	public int getMinNumberMapsForEpicFail() {
		return minNumberMapsForEpicFail;
	}

	@XmlElement
	public void setMinNumberMapsForEpicFail(int minNumberMapsForEpicFail) {
		this.minNumberMapsForEpicFail = minNumberMapsForEpicFail;
	}
	
	public int getRankLimit() {
		return rankLimit;
	}

	@XmlElement
	public void setRankLimit(int rankLimit) {
		this.rankLimit = rankLimit;
	}

	public static StatisticConfig loadStatsConfig() {
		StatisticConfig config;

		try {

			File file = new File("conf\\configStats.xml");
			JAXBContext jaxbContext = JAXBContext
					.newInstance(StatisticConfig.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			config = (StatisticConfig) jaxbUnmarshaller.unmarshal(file);

		} catch (JAXBException e) {
			config = new StatisticConfig();
			StatisticConfig.saveStatsConfig(config);
		}

		return config;
	}

	public static void saveStatsConfig(StatisticConfig config) {
		try {

			File file = new File("conf\\configStats.xml");
			JAXBContext jaxbContext = JAXBContext
					.newInstance(StatisticConfig.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			jaxbMarshaller.marshal(config, file);

		} catch (JAXBException e) {
		}
	}
}
