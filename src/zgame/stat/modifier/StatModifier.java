package zgame.stat.modifier;

import zgame.core.utils.Uuidable;
import zgame.stat.Stat;

import java.util.UUID;

/** An amount that effects a stat */
public class StatModifier implements Uuidable{
	
	/** The uuid of this {@link StatModifier} */
	private final String uuid;
	
	/** The {@link Stat} which uses this modifier */
	private Stat stat;
	
	/** The amount of this modifier */
	private double value;
	
	/** The type of this modifier */
	private int type;
	
	/**
	 * Create a new modifier
	 *
	 * @param value See {@link #value}
	 * @param type See {@link #type}. Should use values from {@link ModifierType}
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
	
	/** @return See {@link #type}. Should use values from {@link ModifierType} */
	public int getType(){
		return this.type;
	}
	
	/** @param type See {@link #type} and {@link ModifierType} */
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
