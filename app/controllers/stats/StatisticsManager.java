package controllers.stats;

import java.text.DecimalFormat;

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

		flash.success("Stats regenerated in %s seconds (I18N)", df.format(seconds));
		redirect("/admin");
	}

}
