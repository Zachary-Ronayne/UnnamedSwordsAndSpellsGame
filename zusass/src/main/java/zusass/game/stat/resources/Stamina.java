package zusass.game.stat.resources;

import zgame.stat.ResourceStat;
import zgame.stat.Stats;
import zusass.game.stat.ZusassStat;

import static zusass.game.stat.ZusassStat.*;

/** The stat keeping track of the current health of a thing */
public class Stamina extends ResourceStat{
	
	/**
	 * Create a new stat with the given default value
	 *
	 * @param stats See {@link #stats}
	 */
	public Stamina(Stats stats){
		super(stats,
				/////////////////////////////////////////
				STAMINA, new ZusassStat[]{},
				/////////////////////////////////////////
				STAMINA_MIN, new ZusassStat[]{},
				/////////////////////////////////////////
				STAMINA_MAX, new ZusassStat[]{STRENGTH, ENDURANCE},
				/////////////////////////////////////////
				STAMINA_REGEN, new ZusassStat[]{ENDURANCE});
	}
	
	@Override
	public double calculateMinStat(){
		return 0;
	}
	
	@Override
	public double calculateMaxStat(){
		return super.getOther(STRENGTH) * 2 + super.getOther(ENDURANCE) * 5;
	}
	
	@Override
	public double calculateRegenStat(){
		return super.getOther(ENDURANCE) * 2;
	}
}
