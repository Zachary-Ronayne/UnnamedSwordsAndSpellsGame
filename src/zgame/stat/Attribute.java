package zgame.stat;

import zusass.game.stat.ZusassStat;

/** A base value stat for a thing, like strength. Attributes do not depend on any other stats */
public abstract class Attribute extends Stat{
	
	/** The stat that holds the portion of this attribute which has a maximum determined by {@link #level} */
	private final Base base;
	/** The stat for the maximum value of the base value of this attribute */
	private final ValueStat level;
	/** The stat for the regeneration value the base value of this attribute */
	private final ValueStat regen;
	
	/**
	 * Create a new attribute with the given default value
	 *
	 * @param stats See {@link #stats}
	 * @param base The base type of this attribute, i.e. the stat to hold the normal maximum before buffs
	 * @param type See {@link #type}
	 * @param level See {@link #level}
	 * @param regen See {@link #regen}
	 */
	public Attribute(Stats stats, ZusassStat type, ZusassStat base, ZusassStat level, ZusassStat regen){
		super(stats, type, base);
		this.base = new Base(stats, base, level, regen);
		this.level = new ValueStat(1, stats, level);
		this.regen = new ValueStat(0, stats, regen);
		this.getStats().add(this.base);
		this.getStats().add(this.level);
		this.getStats().add(this.regen);
	}
	
	@Override
	public double calculateValue(){
		return this.base.get();
	}
	
	@Override
	public void setValue(double value){
		this.level.setValue(value);
		this.base.setValue(value);
	}
	
	@Override
	public void addValue(double value){
		this.base.addValue(value);
	}
	
	/** The {@link RegenStat} holding the base value of this {@link Attribute} before modifiers */
	public class Base extends RegenStat{
		
		/**
		 * Create a new stat with the given default value
		 *
		 * @param stats See {@link #stats}
		 * @param base  See {@link #base}
		 * @param level See {@link #level}
		 */
		public Base(Stats stats, ZusassStat base, ZusassStat level, ZusassStat regen){
			super(0, stats, base, level, regen);
		}
		
		@Override
		public double calculateMin(){
			return 0;
		}
		
		@Override
		public double calculateMax(){
			return level.get();
		}
		
		@Override
		public double calculateRegen(){
			return regen.get();
		}
	}
	
}
