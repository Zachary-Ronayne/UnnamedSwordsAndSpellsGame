package zgame.stat.modifier;

import zgame.core.file.Saveable;
import zgame.stat.StatType;

/** An object holding a {@link StatModifier} and {@link StatType} */
public class TypedModifier implements Saveable{
	
	/** The modifier of this object */
	private StatModifier modifier;
	/** The type of stat of this object */
	private StatType type;
	
	/**
	 * Create an object holding a {@link StatModifier} and {@link StatType}
	 * @param modifier See {@link #modifier}
	 * @param type See #type
	 */
	public TypedModifier(StatModifier modifier, StatType type){
		this.modifier = modifier;
		this.type = type;
	}
	
	/** @return See #modifier */
	public StatModifier modifier(){
		return this.modifier;
	}
	
	/** @return See #type */
	public StatType type(){
		return this.type;
	}
	
	/** @return The id of the {@link StatType} used by this object */
	public int getId(){
		return this.type.getId();
	}
}
