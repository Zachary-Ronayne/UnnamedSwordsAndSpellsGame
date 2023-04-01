package zusass.game.stat.resources;

import zgame.stat.ResourceStat;
import zgame.stat.Stats;
import zusass.game.stat.ZusassStat;

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
				MANA, new ZusassStat[]{},
				/////////////////////////////////////////
				MANA_MIN, new ZusassStat[]{},
				/////////////////////////////////////////
				MANA_MAX, new ZusassStat[]{INTELLIGENCE},
				/////////////////////////////////////////
				MANA_REGEN, new ZusassStat[]{INTELLIGENCE});
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
