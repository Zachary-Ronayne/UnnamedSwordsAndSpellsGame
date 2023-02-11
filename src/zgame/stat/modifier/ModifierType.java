package zgame.stat.modifier;

public class ModifierType{
	/** The total number of modifier types */
	public static final int TOTAL = 3;
	
	/** Represents adding a stat's value */
	public static final int ADD = 0;
	/** Represents a stat that adds itself to other modifiers to get the final multiplier */
	public static final int MULT_ADD = 1;
	/** Represents a stat that multiplies its value with other modifiers multiplicatively */
	public static final int MULT_MULT = 2;
	
	/** Cannot instantiate {@link ModifierType} */
	private ModifierType(){}
}
