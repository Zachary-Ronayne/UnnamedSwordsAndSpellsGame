package zusass.game.stat.resources;

import zgame.stat.ResourceStat;
import zgame.stat.Stats;
import zusass.game.stat.ZusassStat;

import static zusass.game.stat.ZusassStat.*;

/** The stat keeping track of the current health of a thing */
public class Health extends ResourceStat{
	
	/**
	 * Create a new stat with the given default value
	 *
	 * @param stats See {@link #stats}
	 */
	public Health(Stats stats){
		super(stats,
				/////////////////////////////////////////
				HEALTH, new ZusassStat[]{},
				/////////////////////////////////////////
				HEALTH_MIN, new ZusassStat[]{},
				/////////////////////////////////////////
				HEALTH_MAX, new ZusassStat[]{STRENGTH},
				/////////////////////////////////////////
				HEALTH_REGEN, new ZusassStat[]{});
	}
	
	@Override
	public double calculateMinStat(){
		// Returning -1 instead of 0 to ensure health being below 0 can be detected
		return -1;
	}
	
	@Override
	public double calculateMaxStat(){
		return super.getOther(STRENGTH) * 5;
	}
	
	@Override
	public double calculateRegenStat(){
		return 2;
	}
}
