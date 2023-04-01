package zgame.stat;

import zusass.game.stat.ZusassStat;

/** The stat keeping track of a resource like health of a thing. A resource has a min, max, and regen rate, potentially based on other stats */
public abstract class ResourceStat extends RegenStat{
	
	/** The stat for the minimum value of this resource */
	private final Min min;
	/** The stat for the maximum value of this resource */
	private final Max max;
	/** The stat for the regeneration value of this resource */
	private final Regen regen;
	
	/**
	 * Create a new resource with the given default value
	 *
	 * @param stats See {@link #stats}
	 * @param type See {@link #type}
	 * @param dependents The dependent stats of {@link #type}
	 * @param min See {@link #min}
	 * @param minDependents The dependent stats of {@link #min}
	 * @param max See {@link #max}
	 * @param maxDependents The dependent stats of {@link #max}
	 * @param regen See {@link #regen}
	 * @param regenDependents The dependent stats of {@link #regen}
	 */
	public ResourceStat(Stats stats,
						/////////////////////////////////////////
						ZusassStat type, ZusassStat[] dependents,
						/////////////////////////////////////////
						ZusassStat min, ZusassStat[] minDependents,
						/////////////////////////////////////////
						ZusassStat max, ZusassStat[] maxDependents,
						/////////////////////////////////////////
						ZusassStat regen, ZusassStat[] regenDependents){
		
		super(0, stats, type, min, max, regen);
		this.min = new Min(stats, min, minDependents);
		this.max = new Max(stats, max, maxDependents);
		this.regen = new Regen(stats, regen, regenDependents);
		
		var s = this.getStats();
		s.add(this.min);
		s.add(this.max);
		s.add(this.regen);
	}
	
	@Override
	public final double calculateMin(){
		return this.getOther(this.min.getType());
	}
	
	/**
	 * Calculate the current value of {@link #min}
	 *
	 * @return The new value
	 */
	public abstract double calculateMinStat();
	
	@Override
	public final double calculateMax(){
		return this.max.get();
	}
	
	/**
	 * Calculate the current value of {@link #max}
	 *
	 * @return The new value
	 */
	public abstract double calculateMaxStat();
	
	@Override
	public final double calculateRegen(){
		return this.regen.get();
	}
	
	/**
	 * Calculate the current value of {@link #regen}
	 *
	 * @return The new value
	 */
	public abstract double calculateRegenStat();
	
	/** See {@link #min} */
	public class Min extends Stat{
		/**
		 * Create a new stat
		 *
		 * @param stats See {@link #stats}
		 * @param type See {@link #type}
		 * @param dependents See {@link #dependents}
		 */
		public Min(Stats stats, ZusassStat type, ZusassStat... dependents){
			super(stats, type, dependents);
		}
		
		@Override
		public double calculateValue(){
			return calculateMinStat();
		}
	}
	
	/** See {@link #max} */
	public class Max extends Stat{
		/**
		 * Create a new stat
		 *
		 * @param stats See {@link #stats}
		 * @param type See {@link #type}
		 * @param dependents See {@link #dependents}
		 */
		public Max(Stats stats, ZusassStat type, ZusassStat... dependents){
			super(stats, type, dependents);
		}
		
		@Override
		public double calculateValue(){
			return calculateMaxStat();
		}
	}
	
	/** See {@link #max} */
	public class Regen extends Stat{
		/**
		 * Create a new stat
		 *
		 * @param stats See {@link #stats}
		 * @param type See {@link #type}
		 * @param dependents See {@link #dependents}
		 */
		public Regen(Stats stats, ZusassStat type, ZusassStat... dependents){
			super(stats, type, dependents);
		}
		
		@Override
		public double calculateValue(){
			return calculateRegenStat();
		}
	}
}
