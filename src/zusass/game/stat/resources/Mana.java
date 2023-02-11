package zusass.game.stat.resources;

import zgame.stat.ResourceStat;
import zgame.stat.StatType;
import zgame.stat.Stats;

import static zusass.game.stat.ZusassStat.*;

/** The stat keeping track of the current health of a thing */
public class Mana extends ResourceStat{
	
	/**
	 * Create a new stat with the given default value
	 *
	 * @param stats See {@link #stats}
	 */
	public Mana(Stats stats){
		super(stats,
				/////////////////////////////////////////
				MANA, new StatType[]{},
				/////////////////////////////////////////
				MANA_MIN, new StatType[]{},
				/////////////////////////////////////////
				MANA_MAX, new StatType[]{INTELLIGENCE},
				/////////////////////////////////////////
				MANA_REGEN, new StatType[]{INTELLIGENCE});
	}
	
	@Override
	public double calculateMinStat(){
		return 0;
	}
	
	@Override
	public double calculateMaxStat(){
		return super.getOther(INTELLIGENCE) * 10;
	}
	
	@Override
	public double calculateRegenStat(){
		return super.getOther(INTELLIGENCE);
	}
}
