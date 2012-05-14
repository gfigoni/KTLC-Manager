package controllers.stats;

import java.text.DecimalFormat;

import models.stats.StatisticConfig;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.With;
import controllers.Check;
import controllers.Secure;

@With(Secure.class)
public class StatisticsManager extends Controller {

	@Check("isAdmin")
	public static void regenStats() {
		Long miliseconds = StatisticsGenerator.updateAllGeneralStatistics();
		Double seconds = miliseconds / 1000.0;
		DecimalFormat df = new DecimalFormat("#.#####");

		flash.success(Messages.get("manageParameters.regenInSecs", df.format(seconds)));
		redirect("/admin");
	}
	
	@Check("isAdmin")
	public static void manageParameters() {
		StatisticConfig config = StatisticConfig.loadStatsConfig();
		render("/admin/manageParameters.html", config);
	}
	
	@Check("isAdmin")
	public static void saveParameters(Integer newLengthTop, Integer newMinPercentage, Integer newEpicFail, Integer newRankLimit) {
		// load the current config
		StatisticConfig config = StatisticConfig.loadStatsConfig();

		// validation...
		validation.required(newLengthTop);
		validation.min(newLengthTop, config.MIN_LENGTH);
		validation.max(newLengthTop, config.MAX_LENGTH);
		
		validation.required(newMinPercentage);
		validation.min(newMinPercentage, config.MIN_PERCENTAGE);
		validation.max(newMinPercentage, config.MAX_PERCENTAGE);
		
		validation.required(newEpicFail);
		validation.min(newEpicFail, config.MIN_EPICFAIL);
		validation.max(newEpicFail, config.MAX_EPICFAIL);
		
		validation.required(newRankLimit);
		validation.min(newRankLimit, config.MIN_RANKLIMIT);
		validation.max(newRankLimit, config.MAX_RANKLIMIT);
		
		// errors
		if(validation.hasErrors()) {
			// reload page with error
            flash.error(Messages.get("manageParameters.errors"));
    		render("/admin/manageParameters.html", config);
        } else if (config.getLengthTop() != newLengthTop 
        			|| config.getMinPercentageParticipations() != newMinPercentage
        			|| config.getMinNumberMapsForEpicFail() != newEpicFail
        			|| config.getRankLimit() != newRankLimit) {
        	// if the new values are different, set the parameters
			config.setLengthTop(newLengthTop);
			config.setMinPercentageParticipations(newMinPercentage);
			config.setMinNumberMapsForEpicFail(newEpicFail);
			config.setRankLimit(newRankLimit);
			// save the parameters
			StatisticConfig.saveStatsConfig(config);
			//regenerate the stats
			regenStats();
    	} else {
    		flash.success(Messages.get("manageParameters.noChanges"));
    		redirect("/admin");
    	}
        	
	}

}
