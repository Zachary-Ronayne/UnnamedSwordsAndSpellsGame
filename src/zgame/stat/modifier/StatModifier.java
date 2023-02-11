package zgame.stat.modifier;

import zgame.core.utils.Uuidable;

import java.util.UUID;

/** An amount that effects a stat */
public class StatModifier implements Uuidable{
	
	/** The uuid of this {@link StatModifier} */
	private final String uuid;
	
	/** The amount of this modifier */
	private final double value;
	
	/** The type of this modifier */
	private final ModifierType type;
	
	/**
	 * Create a new modifier
	 *
	 * @param value See {@link #value}
	 * @param type See {@link #type}
	 */
	public StatModifier(double value, ModifierType type){
		this.uuid = UUID.randomUUID().toString();
		this.value = value;
		this.type = type;
	}
	
	@Override
	public String getUuid(){
		return uuid;
	}
	
	/** @return See {@link #value} */
	public double getValue(){
		return this.value;
	}
	
	/** @return See {@link #type} */
	public ModifierType getType(){
		return this.type;
	}
	
}
