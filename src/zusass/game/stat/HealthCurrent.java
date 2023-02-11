package zusass.game.stat;

import zgame.stat.RegenStat;
import zgame.stat.Stats;
import static zusass.game.stat.ZusassStat.*;

/** The stat keeping track of the current health of a thing */
public class HealthCurrent extends RegenStat{
	
	/**
	 * Create a new stat with the given default value
	 *
	 * @param stats See {@link #stats}
	 */
	public HealthCurrent(Stats stats){
		super(0, stats, HEALTH_CURRENT, HEALTH_MAX, HEALTH_REGEN);
	}
	
	@Override
	public double calculateMin(){
		// Returning -1 instead of 0 to ensure health being below 0 can be detected
		return -1;
	}
	
	@Override
	public double calculateMax(){
		return this.getOther(HEALTH_MAX);
	}
	
	@Override
	public double calculateRegen(){
		return this.getOther(HEALTH_REGEN);
	}
}
