package zgame.stat.modifier;

import zgame.core.utils.Uuidable;
import zgame.stat.Stat;

import java.util.UUID;

/** An amount that effects a stat */
public class StatModifier implements Uuidable{
	
	/** The total number of modifier types */
	public static final int TOTAL = 3;
	
	// TODO convert this back to an enum, and index by manual ordinals
	/** Represents adding a stat's value */
	public static final int ADD = 0;
	/** Represents a stat that adds itself to other modifiers to get the final multiplier */
	public static final int MULT_ADD = 1;
	/** Represents a stat that multiplies its value with other modifiers multiplicatively */
	public static final int MULT_MULT = 2;
	
	/** The uuid of this {@link StatModifier} */
	private final String uuid;
	
	/** The {@link Stat} which uses this modifier */
	private Stat stat;
	
	/** The amount of this modifier */
	private double value;
	
	/** The type of this modifier. See the constants in this class */
	private int type;
	
	/**
	 * Create a new modifier
	 *
	 * @param value See {@link #value}
	 * @param type See {@link #type}. Should use constants from this class
	 */
	public StatModifier(double value, int type){
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
	
	/** @param value See {@link #value} */
	public void setValue(double value){
		if(this.value == value) return;
		this.value = value;
		this.stat.flagRecalculate();
	}
	
	/** @return See {@link #type}. Should use constants from this class */
	public int getType(){
		return this.type;
	}
	
	/** @param type See {@link #type} and the constants from this class */
	public void setType(int type){
		if(this.type == type) return;
		this.type = type;
		this.stat.flagRecalculate();
	}
	
	/** @return See {@link #stat} */
	public Stat getStat(){
		return stat;
	}
	
	/** @param stat See {@link #stat} */
	public void setStat(Stat stat){
		this.stat = stat;
		this.stat.flagRecalculate();
	}
}
