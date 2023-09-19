package zgame.stat;

import zgame.stat.modifier.ModifierList;
import zgame.stat.modifier.ModifierType;
import zgame.stat.modifier.StatModifier;

import java.util.HashMap;
import java.util.Map;

/** An object keeping track of a single stat used by an object */
public abstract class Stat{
	
	/** The stats object which is used by this {@link Stat} */
	private final Stats stats;
	
	/** The {@link StatType} identifying this {@link Stats} */
	private final StatType<?> type;
	
	/** The ids, in no particular order, of stats that this {@link Stat} uses in calculating itself */
	private final int[] dependents;
	
	/** true if this stat needs to be recalculated before it is used again */
	private boolean recalculate;
	
	/** The value of this stat since it was last calculated */
	private double calculated;
	
	/** true if this stat is currently buffed */
	private boolean buffed;
	/** true if this stat is currently debuffed */
	private boolean debuffed;
	
	/**
	 * The current modifiers applying to this {@link Stat}.
	 * The outer array is indexed by {@link ModifierType}
	 * The Map is mapped by modifier sourceId
	 * The ArrayList is sorted descending by modifier power
	 */
	private final Map<String, ModifierList>[] modifiers;
	
	/**
	 * Create a new stat
	 *
	 * @param stats See {@link #stats}
	 * @param type See {@link #type}
	 * @param dependents See {@link #dependents}
	 */
	@SuppressWarnings("unchecked")
	public Stat(Stats stats, StatType<?> type, StatType<?>... dependents){
		this.recalculate = true;
		this.stats = stats;
		this.type = type;
		
		// Save the ids of the dependent stats
		this.dependents = new int[dependents.length];
		for(int i = 0; i < this.dependents.length; i++){
			this.dependents[i] = dependents[i].getId();
		}
		this.modifiers = (HashMap<String, ModifierList>[])new HashMap[ModifierType.values().length];
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
	public final double getOther(StatType<?> type){
		var stat = this.stats.get(type);
		if(stat == null) return 0;
		return stat.get();
	}
	
	/** @return See {@link #type} */
	public StatType<?> getType(){
		return this.type;
	}
	
	/** Tell this {@link Stat} that it needs to be recalculated before {@link #calculated} can be used again */
	public void flagRecalculate(){
		// First, flag this stat as needing to be recalculated
		this.recalculate = true;
		
		// Now, find any stats that use this stat
		var toFlag = this.stats.getDependents()[this.getType().getId()];
		// Flag each stat as needing to be recalculated
		for(int i = 0; i < toFlag.length; i++){
			var s = this.stats.get(toFlag[i]);
			if(s != null) s.flagRecalculate();
		}
	}
	
	/**
	 * Tell {@link #modifiers} that there has been a change to the modifier list, and that list needs to be recalculated.
	 * Also tells the stat itself to be recalculated
	 *
	 * @param type The type of modifiers which must be recalculated
	 */
	public void flagModifiersRecalculate(ModifierType type, String sourceId){
		// TODO fix this sometimes causing a concurrent modification exception
		this.flagRecalculate();
		var m = this.modifiers[type.getIndex()];
		var list = m.get(sourceId);
		list.flagRecalculate();
	}
	
	/** @return What {@link #calculated} should be based on the current state of {@link #stats} */
	public abstract double calculateValue();
	
	/** @return See {@link #calculated} */
	public double get(){
		if(this.recalculate) this.recalculate();
		return this.calculated;
	}
	
	/** @return See {@link #buffed} */
	public boolean buffed(){
		if(this.recalculate) this.recalculate();
		return this.buffed;
	}
	
	/** @return See {@link #debuffed} */
	public boolean debuffed(){
		if(this.recalculate) this.recalculate();
		return this.debuffed;
	}
	
	/** Recalculate the current value of this stat */
	public void recalculate(){
		// First calculate the value
		this.calculated = this.calculateValue();
		var beforeModifiers = this.calculated;
		// Now apply modifiers
		this.applyModifiers();
		// Clear the recalculate flag
		this.recalculate = false;
		// Update if this stat is buffed or debuffed
		this.buffed = beforeModifiers < this.calculated;
		this.debuffed = beforeModifiers > this.calculated;
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
		if(!map.containsKey(sourceId)) map.put(sourceId, new ModifierList(mod.getType()));
		var list = map.get(sourceId);
		if(list.contains(mod)) return;
		list.add(mod);
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
	
	/** Remove every modifier from this stat */
	public void clearModifiers(){
		for(var m : this.modifiers) m.values().clear();
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
		for(var m : mods) newCalculated += m.currentValue();
		
		// Combine all additive multipliers
		mods = this.modifiers[ModifierType.MULT_ADD.getIndex()].values();
		double multiplyTotal = 1;
		for(var m : mods) multiplyTotal += m.currentValue();
		
		// Apply all multiplicitive multipliers
		mods = this.modifiers[ModifierType.MULT_MULT.getIndex()].values();
		for(var m : mods) multiplyTotal *= m.currentValue();
		
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
	
	/** Perform any necessary actions to reset the state of this stat. Removes modifiers by default */
	public void reset(){
		this.clearModifiers();
	}
	
}
