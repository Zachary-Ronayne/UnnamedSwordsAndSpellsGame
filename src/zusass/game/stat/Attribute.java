package zusass.game.stat;

import zgame.stat.ResourceStat;
import zgame.stat.StatType;
import zgame.stat.Stats;

/** A base value stat for a thing, like strength. Attributes do not depend on any other stats */
public abstract class Attribute extends ResourceStat{
	/**
	 * Create a new stat with the given default value
	 *
	 * @param stats See {@link #stats}
	 * @param type See {@link #type}
	 * @param min See {@link #min}
	 * @param max See {@link #max}
	 * @param regen See {@link #regen}
	 */
	public Attribute(Stats stats, StatType type, StatType min, StatType max, StatType regen){
		super(stats, type, new StatType[0], min, new StatType[0], max, new StatType[0], regen, new StatType[0]);
	}
	
	@Override
	public double calculateMinStat(){
		// Just basically making attributes not have a min
		return -Double.MAX_VALUE;
	}
	
	@Override
	public double calculateMaxStat(){
		// Just basically making attributes not have a max
		return Double.MAX_VALUE;
	}
	
	@Override
	public double calculateRegenStat(){
		// Attributes do not regenerate on their own
		return 0;
	}
}
