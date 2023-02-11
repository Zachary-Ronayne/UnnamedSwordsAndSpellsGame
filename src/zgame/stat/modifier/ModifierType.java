package zgame.stat.modifier;

public enum ModifierType{
	/** Represents adding a stat's value */
	ADD,
	/** Represents a stat that adds itself to other modifiers to get the final multiplier */
	MULT_ADD,
	/** Represents a stat that multiplies its value with other modifiers multiplicatively */
	MULT_MULT,
}
