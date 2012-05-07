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
	/** The ranks that are counted for the podiums */
	private int rankInterest = 4;
	/**
	 * The minimal number of participation that are required to be counted in
	 * some TOPs
	 */
	private int minPercentageParticipations = 10;
	/**
	 * The minimal number of map that a player have to play to be counted in the
	 * epic fail ranking
	 */
	private int minNumberMapsForEpicFail = 2;

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
