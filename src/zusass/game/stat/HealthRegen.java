package zusass.game.stat;

import zgame.stat.Stats;
import zgame.stat.ValueStat;
import static zusass.game.stat.ZusassStat.*;

/** The stat that keeps track of much health a thing regenerates in a second */
public class HealthRegen extends ValueStat{
	
	/**
	 * Create a new {@link HealthRegen}
	 *
	 * @param stats See {@link #stats}
	 */
	public HealthRegen(Stats stats){
		super(2, stats, HEALTH_REGEN);
	}
}
