package zusass.game.stat;

import zgame.stat.ResourceStat;
import zgame.stat.StatType;
import zgame.stat.Stats;

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
				STAMINA, new StatType[]{},
				/////////////////////////////////////////
				STAMINA_MIN, new StatType[]{},
				/////////////////////////////////////////
				STAMINA_MAX, new StatType[]{STRENGTH, ENDURANCE},
				/////////////////////////////////////////
				STAMINA_REGEN, new StatType[]{ENDURANCE});
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
