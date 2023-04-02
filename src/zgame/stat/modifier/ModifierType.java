package zgame.stat.modifier;

import zgame.stat.Stat;

/** Constants that represent different ways {@link StatModifier}s can be applied to {@link Stat}s */
public enum ModifierType{
	
	/** Represents adding a stat's value */
	ADD(0, 0.5),
	/** Represents a stat that adds itself to other modifiers to get the final multiplier */
	MULT_ADD(1, 0.8),
	/** Represents a stat that multiplies its value with other modifiers multiplicatively */
	MULT_MULT(2, 1.5),
	;
	
	/** The array index where this type should be stored */
	private final int index;
	
	/** How valuable modifiers of this type are, used for spell costs */
	private final double value;
	
	ModifierType(int index, double value){
		this.index = index;
		this.value = value;
	}
	
	/** @return See {@link #index} */
	public int getIndex(){
		return this.index;
	}
	
	/** @return See {@link #value} */
	public double getValue(){
		return value;
	}
}
