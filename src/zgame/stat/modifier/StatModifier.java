package zgame.stat.modifier;

import com.google.gson.JsonElement;
import zgame.core.file.Saveable;
import zgame.stat.Stat;

/** An amount that effects a stat */
public class StatModifier implements Comparable<StatModifier>, Saveable{
	
	/** The json key storing {@link #value} */
	public static final String VALUE_KEY = "value";
	/** The json key storing {@link #type} */
	public static final String TYPE_KEY = "type";
	
	/** The amount of this modifier */
	private double value;
	
	/** The type of this modifier */
	private ModifierType type;
	
	/** Create a new object using see {@link #load(JsonElement)} */
	public StatModifier(JsonElement e) throws ClassCastException, IllegalStateException, NullPointerException{
		this.load(e);
	}
	
	/**
	 * Create a new modifier
	 *
	 * @param value See {@link #value}
	 * @param type See {@link #type}
	 */
	public StatModifier(double value, ModifierType type){
		this.value = value;
		this.type = type;
	}
	
	/** @return See {@link #value} */
	public double getValue(){
		return this.value;
	}
	
	/**
	 * @param value See {@link #value}
	 * @param stat The stat object which uses this modifier
	 * @param sourceId The source which is providing the modifier
	 */
	public void setValue(double value, Stat stat, String sourceId){
		if(this.value == value) return;
		this.value = value;
		stat.flagModifiersRecalculate(this.getType(), sourceId);
	}
	
	/** @return See {@link #type} */
	public ModifierType getType(){
		return this.type;
	}
	
	@Override
	public int compareTo(StatModifier o){
		// Sort descending
		return (int)(o.getValue() - this.getValue());
	}
	
	@Override
	public boolean save(JsonElement e){
		var obj = e.getAsJsonObject();
		obj.addProperty(VALUE_KEY, this.value);
		obj.addProperty(TYPE_KEY, this.type.name());
		return true;
	}
	
	@Override
	public boolean load(JsonElement e) throws ClassCastException, IllegalStateException, NullPointerException{
		this.type = Saveable.e(TYPE_KEY, e, ModifierType.class, ModifierType.ADD);
		this.value = Saveable.d(VALUE_KEY, e, this.type == ModifierType.MULT_MULT ? 1 : 0);
		return true;
	}
}
