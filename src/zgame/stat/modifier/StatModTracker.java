package zgame.stat.modifier;

import zgame.stat.Stat;

/** A class used to track the state of a {@link StatModifier} and ensure its state is updated properly */
public class StatModTracker{
	
	/** The modifier used by this tracker */
	private final StatModifier mod;
	
	/** The source which provided this modifier */
	private final String sourceId;
	
	/** The stat being tracked */
	private final Stat stat;
	
	/**
	 * Create a new {@link StatModTracker}, a new {@link StatModifier} and apply the given modifier to the stat
	 * @param value The initial value of the modifier
	 * @param type The type of modifier
	 * @param stat The stat to effect
	 * @param sourceId The sourceId of where the effect came from
	 */
	public StatModTracker(double value, ModifierType type, Stat stat, String sourceId){
		this.sourceId = sourceId;
		this.stat = stat;
		this.mod = this.stat.addModifier(sourceId, value, type);
	}
	
	/** @param v The new value for the stat */
	public void setValue(double v){
		this.mod.setValue(v, this.stat, this.sourceId);
	}
	
}
