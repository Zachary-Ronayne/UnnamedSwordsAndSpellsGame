package zgame.stat;

import zgame.stat.modifier.ModifierType;
import zgame.stat.modifier.StatModifier;

import java.util.HashMap;
import java.util.Map;

/** An object keeping track of a single stat used an object */
public abstract class Stat{
	
	/** The stats object which is used by this {@link Stat} */
	private final Stats stats;
	
	/** The {@link StatType} identifying this {@link Stats} */
	private final StatType type;
	
	/** true if this {@link Stat} should be recalculated as soon as something about its state changes, false otherwise, defaults to false */
	private boolean instantRecalculate;
	
	/** The ordinals, in no particular order, of stats that this {@link Stat} uses in calculating itself */
	private final int[] dependents;
	
	/** true if this stat needs to be recalculated before it is used again */
	private boolean recalculate;
	
	/** The value of this stat since it was last calculated */
	private double calculated;
	
	/** The current modifiers applying to this {@link Stat} */
	private final Map<String, StatModifier>[] modifiers;
	
	/**
	 * Create a new stat
	 *
	 * @param stats See {@link #stats}
	 * @param type See {@link #type}
	 * @param dependents See {@link #dependents}
	 */
	@SuppressWarnings("unchecked")
	public Stat(Stats stats, StatType type, StatType... dependents){
		this.instantRecalculate = false;
		this.recalculate = true;
		this.stats = stats;
		this.type = type;
		
		// Save the ordinals of the dependent stats
		this.dependents = new int[dependents.length];
		for(int i = 0; i < this.dependents.length; i++){
			this.dependents[i] = dependents[i].getOrdinal();
		}
		this.modifiers = (HashMap<String, StatModifier>[]) new HashMap[3];
		this.modifiers[ModifierType.ADD] = new HashMap<>();
		this.modifiers[ModifierType.MULT_ADD] = new HashMap<>();
		this.modifiers[ModifierType.MULT_MULT] = new HashMap<>();
	}
	
	/** @return See {@link #stats} */
	public Stats getStats(){
		return this.stats;
	}
	
	/**
	 * Apply any operations that must happen to this {@link Stat} over time.
	 * Does nothing by default, can override to provide custom behavior
	 *
	 * @param dt The number of sections that have passed
	 */
	public void tick(double dt){}
	
	/** @return See {@link #dependents} */
	public int[] getDependents(){
		return this.dependents;
	}
	
	/**
	 * Get the current value of a stat in {@link #stats}
	 *
	 * @param type The type of stat to get
	 * @return The value, or 0 if the given stat doesn't exist
	 */
	public final double getOther(StatType type){
		var stat = this.stats.get(type);
		if(stat == null) return 0;
		return stat.get();
	}
	
	/** @return See {@link #type} */
	public StatType getType(){
		return this.type;
	}
	
	/** Tell this {@link Stat} that it needs to be recalculated before {@link #calculated} can be used again */
	public void flagRecalculate(){
		// If instantly recalculating, do it now and stop
		if(this.instantRecalculate){
			this.recalculate();
			return;
		}

		// First, flag this stat as needing to be recalculated
		this.recalculate = true;

		// Now, find any stats that use this stat
		var toFlag = this.stats.getDependents()[this.getType().getOrdinal()];
		// Flag each stat as needing to be recalculated
		for(int i = 0; i < toFlag.length; i++){
			this.stats.get(toFlag[i]).flagRecalculate();
		}
	}
	
	/** @return What {@link #calculated} should be based on the current state of {@link #stats} */
	public abstract double calculateValue();
	
	/** @return See {@link #calculated} */
	public double get(){
		if(this.recalculate) this.recalculate();
		
		return this.calculated;
	}
	
	/** Recalculate the current value of this stat */
	public void recalculate(){
		// First calculate the value
		this.calculated = this.calculateValue();
		// Now apply modifiers
		this.applyModifiers();
		// Clear the recalculate flag
		this.recalculate = false;
	}
	
	/**
	 * Add a modifier to this {@link Stat}
	 *
	 * @param value The value of the modifier
	 * @param type The way the value is applied to the stat
	 * @return The modifier created
	 */
	public StatModifier addModifier(double value, int type){
		var m = new StatModifier(value, type);
		this.addModifier(m);
		return m;
	}
	
	/**
	 * Apply the given modifier to this stat. Does nothing if this stat already has the given modifier.
	 *
	 * @param mod The modifier to add.
	 */
	public void addModifier(StatModifier mod){
		if(this.hasModifier(mod)) return;
		
		this.modifiers[mod.getType()].put(mod.getUuid(), mod);
		mod.setStat(this);
	}
	
	/** @param mod The modifier to remove, should be the same object, with the same uuid */
	public void removeModifier(StatModifier mod){
		this.modifiers[mod.getType()].remove(mod.getUuid());
		this.flagRecalculate();
	}
	
	/**
	 * @param mod The modifier to check for
	 * @return true if this stat is being modified by the given modifier, false otherwise
	 */
	public boolean hasModifier(StatModifier mod){
		return this.modifiers[mod.getType()].containsKey(mod.getUuid());
	}
	
	/** Put the current value of {@link #calculated} through all its modifiers */
	public void applyModifiers(){
		var newCalculated = this.calculated;
		
		// Apply add modifiers first
		var mods = this.modifiers[ModifierType.ADD].values();
		for(var m : mods) newCalculated += m.getValue();
		
		// Combine all additive multipliers
		mods = this.modifiers[ModifierType.MULT_ADD].values();
		double multiplyTotal = 1;
		for(var m : mods) multiplyTotal += m.getValue();
		
		// Apply all multiplicitive multipliers
		mods = this.modifiers[ModifierType.MULT_MULT].values();
		for(var m : mods) multiplyTotal *= m.getValue();
		
		// Apply the final value
		this.calculated = newCalculated * multiplyTotal;
	}
	
	/**
	 * @param value The new value for this stat. This method just calls {@link #flagRecalculate()} by default. This will only change a value if the {@link Stat}
	 * 		implementation permits setting the value
	 */
	public void setValue(double value){
		this.flagRecalculate();
	}
	
	/**
	 * @param value The new value to add to the current value of this stat. This method just calls {@link #flagRecalculate()} by default. This will only change a value if
	 * 		the {@link Stat} implementation permits setting the value
	 */
	public void addValue(double value){
		this.flagRecalculate();
	}
	
	/** @return See {@link #instantRecalculate} */
	public boolean isInstantRecalculate(){
		return instantRecalculate;
	}
	
	/** @param instantRecalculate See {@link #instantRecalculate}. If setting to true, also recalculates the value */
	public void setInstantRecalculate(boolean instantRecalculate){
		this.instantRecalculate = instantRecalculate;
		this.recalculate();
	}
	
}
