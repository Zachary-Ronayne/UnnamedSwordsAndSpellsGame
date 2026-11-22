package zgame.stat.modifier;

import zgame.core.utils.NotNullList;

/** A list of {@link StatModifier}s, which also tracks if the list has been changed since the last calculation */
public class ModifierList{
	
	/** The modifiers in this list */
	private final NotNullList<StatModifier> modifiers;
	
	/** true if this list has been changed since it's last modifier access, and must be recalculated */
	private boolean recalculate;
	
	/** The current value used by this modifier list */
	private double value;
	
	/** The type of modifiers which should be used by this list. Used for determining the return value for an empty list of modifiers */
	private final ModifierType type;
	
	/** Create a new empty list of modifiers */
	public ModifierList(ModifierType type){
		this.modifiers = new NotNullList<>();
		this.flagRecalculate();
		this.type = type;
	}
	
	/** Remove every modifier from this list */
	public void clear(){
		this.modifiers.clear();
	}
	
	/**
	 * @param mod The modifier to check for
	 * @return true if the modifier is in the list, false otherwise
	 */
	public boolean contains(StatModifier mod){
		return this.modifiers.contains(mod);
	}
	
	/** @return true if there are no modifiers in the list, false otherwise */
	public boolean isEmpty(){
		return this.modifiers.isEmpty();
	}
	
	/**
	 * @param mod The modifier to remove
	 * @return true if the modifier was removed, false otherwise
	 */
	public boolean remove(StatModifier mod){
		boolean success = this.modifiers.remove(mod);
		if(success) this.flagRecalculate();
		return success;
	}
	
	/** @param mod The modifier to add to the list */
	public void add(StatModifier mod){
		this.modifiers.add(mod);
		this.flagRecalculate();
	}
	
	/** Tell this list that it has been modified and needs to be recalculated before the next return of {@link #currentValue()} */
	public void flagRecalculate(){
		this.recalculate = true;
	}
	
	/** Recalculate the current value of this modifier list */
	public void recalculate(){
		StatModifier max = null;
		for(int i = 0; i < this.modifiers.size(); i++){
			var m = this.modifiers.get(i);
			if(max == null || Math.abs(max.getValue()) < Math.abs((m.getValue()))) max = m;
		}
		
		this.modifiers.sort(StatModifier::compareTo);
		// If we are multiplying, the default value should be 1, otherwise we are just adding
		// If more complicated modifier types get created, then each enum type should probably have a method that determines default behavior and what value to return by default
		if(max == null) this.value = this.type == ModifierType.MULT_MULT ? 1 : 0;
		else this.value = max.getValue();
		this.recalculate = false;
	}
	
	/**
	 * @return The current modifier from this list which should have an effect
	 */
	public double currentValue(){
		// If we need to recalculate the modifiers, first recalculate
		if(this.recalculate) this.recalculate();
		return this.value;
	}
	
}
