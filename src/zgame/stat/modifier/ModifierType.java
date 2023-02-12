package zgame.stat.modifier;

public enum ModifierType{
	
	/** Represents adding a stat's value */
	ADD(0),
	/** Represents a stat that adds itself to other modifiers to get the final multiplier */
	MULT_ADD(1),
	/** Represents a stat that multiplies its value with other modifiers multiplicatively */
	MULT_MULT(2),
	;
	
	/** The array index where this type should be stored */
	private final int index;
	
	ModifierType(int index){
		this.index = index;
	}
	
	/** @return See {@link #index} */
	public int getIndex(){
		return this.index;
	}
}
