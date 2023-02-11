package zgame.stat;

import zgame.stat.modifier.ModifierType;
import zgame.stat.modifier.StatModifier;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/** An object keeping track of a single stat used an object */
public abstract class Stat{
	
	/** The stats object which is used by this {@link Stat} */
	private final Stats stats;
	
	/** The {@link StatType} identifying this {@link Stats} */
	private final StatType type;
	
	/** true if this {@link Stat} should be recalculated as soon as something about its state changes, false otherwise, defaults to false */
	private boolean instantRecalculate;
	
	/** The stats that this {@link Stat} uses in calculating itself */
	private final HashSet<StatType> dependents;
	
	/** true if this stat needs to be recalculated before it is used again */
	private boolean recalculate;
	
	/** The value of this stat since it was last calculated */
	private double calculated;
	
	/** The current modifiers applying to this {@link Stat} */
	private final Map<ModifierType, Map<String, StatModifier>> modifiers;
	
	/**
	 * Create a new stat
	 *
	 * @param stats See {@link #stats}
	 * @param type See {@link #type}
	 * @param dependents See {@link #dependents}
	 */
	public Stat(Stats stats, StatType type, StatType... dependents){
		this.instantRecalculate = false;
		this.recalculate = true;
		this.stats = stats;
		this.type = type;
		
		this.dependents = new HashSet<>();
		this.dependents.addAll(Arrays.asList(dependents));
		
		this.modifiers = new HashMap<>();
		this.modifiers.put(ModifierType.ADD, new HashMap<>());
		this.modifiers.put(ModifierType.MULT_ADD, new HashMap<>());
		this.modifiers.put(ModifierType.MULT_MULT, new HashMap<>());
	}
	
	/** @return See {@link #dependents} */
	public HashSet<StatType> getDependents(){
		return this.dependents;
	}
	
	/**
	 * Get the current value of a stat in {@link #stats}
	 *
	 * @param type The type of stat to get
	 * @return The value, or 0 if the given stat doesn't exist
	 */
	public double getOther(StatType type){
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
		var toFlag = this.stats.getDependents().get(this.getType());
		// If none were found, do nothing
		if(toFlag == null) return;
		// Flag each stat as needing to be recalculated
		for(var s : toFlag){
			this.stats.get(s).flagRecalculate();
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
	 * @param value The value of the modifier
	 * @param type The way the value is applied to the stat
	 */
	public void addModifier(double value, ModifierType type){
		this.addModifier(new StatModifier(value, type){});
	}
	
	/** @param mod The modifier to add */
	public void addModifier(StatModifier mod){
		this.modifiers.get(mod.getType()).put(mod.getUuid(), mod);
		this.flagRecalculate();
	}
	
	/** @param mod The modifier to remove, should be the same object, with the same uuid */
	public void removeModifier(StatModifier mod){
		this.modifiers.get(mod.getType()).remove(mod.getUuid());
		this.flagRecalculate();
	}
	
	/** Put the current value of {@link #calculated} through all its modifiers */
	public void applyModifiers(){
		var newCalculated = this.calculated;
		
		// Apply add modifiers first
		var mods = this.modifiers.get(ModifierType.ADD).values();
		for(var m : mods) newCalculated += m.getValue();
		
		// Combine all additive multipliers
		mods = this.modifiers.get(ModifierType.MULT_ADD).values();
		double multiplyTotal = 1;
		for(var m : mods) multiplyTotal += m.getValue();
		
		// Apply all multiplicitive multipliers
		mods = this.modifiers.get(ModifierType.MULT_MULT).values();
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
