package zgame.stat;

import zgame.core.utils.ZArrayUtils;
import zgame.stat.modifier.ModifierType;
import zgame.stat.modifier.StatModifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/** An object keeping track of a single stat used by an object */
public abstract class Stat{
	
	/** The stats object which is used by this {@link Stat} */
	private final Stats stats;
	
	/** The {@link StatType} identifying this {@link Stats} */
	private final StatType type;
	
	/** true if this {@link Stat} should be recalculated as soon as something about its state changes, false otherwise, defaults to false */
	private boolean instantRecalculate;
	
	/** The ids, in no particular order, of stats that this {@link Stat} uses in calculating itself */
	private final int[] dependents;
	
	/** true if this stat needs to be recalculated before it is used again */
	private boolean recalculate;
	
	/** The value of this stat since it was last calculated */
	private double calculated;
	
	/**
	 * The current modifiers applying to this {@link Stat}.
	 * The outer array is indexed by {@link ModifierType}
	 * The Map is mapped by modifier sourceId
	 * The ArrayList is sorted descending by modifier power
	 */
	private final Map<String, ArrayList<StatModifier>>[] modifiers;
	
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
		
		// Save the ids of the dependent stats
		this.dependents = new int[dependents.length];
		for(int i = 0; i < this.dependents.length; i++){
			this.dependents[i] = dependents[i].getId();
		}
		this.modifiers = (HashMap<String, ArrayList<StatModifier>>[])new HashMap[ModifierType.values().length];
		this.modifiers[ModifierType.ADD.getIndex()] = new HashMap<>();
		this.modifiers[ModifierType.MULT_ADD.getIndex()] = new HashMap<>();
		this.modifiers[ModifierType.MULT_MULT.getIndex()] = new HashMap<>();
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
		var toFlag = this.stats.getDependents()[this.getType().getId()];
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
	 * @param sourceId The id representing the source of the modifier
	 * @param value The value of the modifier
	 * @param type The way the value is applied to the stat
	 * @return The modifier created
	 */
	public StatModifier addModifier(String sourceId, double value, ModifierType type){
		var m = new StatModifier(value, type);
		this.addModifier(sourceId, m);
		return m;
	}
	
	/**
	 * Apply the given modifier to this stat. Does nothing if this stat already has the given modifier.
	 *
	 * @param mod The modifier to add
	 * @param sourceId The id representing the source of the modifier
	 */
	public void addModifier(String sourceId, StatModifier mod){
		var map = this.modifiers[mod.getType().getIndex()];
		if(!map.containsKey(sourceId)) map.put(sourceId, new ArrayList<>());
		var list = map.get(sourceId);
		if(list.contains(mod)) return;
		ZArrayUtils.insertSorted(list, mod);
		this.flagRecalculate();
	}
	
	/**
	 * @param mod The modifier to remove, should be the exact modifier object to remove
	 * @param sourceId The id representing the source of the modifier
	 */
	public void removeModifier(String sourceId, StatModifier mod){
		var map = this.modifiers[mod.getType().getIndex()];
		if(!map.containsKey(sourceId)) return;
		
		var list = map.get(sourceId);
		list.remove(mod);
		this.flagRecalculate();
	}
	
	/**
	 * @param mod The modifier to check for
	 * @param sourceId The id representing the source of the modifier
	 * @return true if this stat is being modified by the given modifier, false otherwise
	 */
	public boolean hasModifier(String sourceId, StatModifier mod){
		var map = this.modifiers[mod.getType().getIndex()];
		if(!map.containsKey(sourceId)) return false;
		var list = map.get(sourceId);
		return list.contains(mod);
	}
	
	/** Put the current value of {@link #calculated} through all its modifiers */
	public void applyModifiers(){
		var newCalculated = this.calculated;
		
		// Apply add modifiers first
		var mods = this.modifiers[ModifierType.ADD.getIndex()].values();
		for(var m : mods){
			if(m.isEmpty()) continue;
			newCalculated += m.get(0).getValue();
		}
		
		// Combine all additive multipliers
		mods = this.modifiers[ModifierType.MULT_ADD.getIndex()].values();
		double multiplyTotal = 1;
		for(var m : mods){
			if(m.isEmpty()) continue;
			multiplyTotal += m.get(0).getValue();
		}
		
		// Apply all multiplicitive multipliers
		mods = this.modifiers[ModifierType.MULT_MULT.getIndex()].values();
		for(var m : mods){
			if(m.isEmpty()) continue;
			multiplyTotal *= m.get(0).getValue();
		}
		
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
