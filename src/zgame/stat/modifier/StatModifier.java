package zgame.stat.modifier;

import zgame.stat.Stat;

/** An amount that effects a stat */
public class StatModifier implements Comparable<StatModifier>{
	
	/** The id representing the source of where this modifier came from */
	private final String sourceId;
	
	/** The {@link Stat} which uses this modifier */
	private Stat stat;
	
	/** The amount of this modifier */
	private double value;
	
	/** The type of this modifier */
	private ModifierType type;
	
	/**
	 * Create a new modifier
	 *
	 * @param sourceId See {@link #sourceId}
	 * @param value See {@link #value}
	 * @param type See {@link #type}
	 */
	public StatModifier(String sourceId, double value, ModifierType type){
		this.sourceId = sourceId;
		this.value = value;
		this.type = type;
	}
	
	/** @return See {@link #sourceId} */
	public String getSourceId(){
		return this.sourceId;
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
	
	/** @return See {@link #type} */
	public ModifierType getType(){
		return this.type;
	}
	
	/** @param type See {@link #type} */
	public void setType(ModifierType type){
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
	
	@Override
	public int compareTo(StatModifier o){
		// Sort descending
		return (int)(o.getValue() - this.getValue());
	}
}
