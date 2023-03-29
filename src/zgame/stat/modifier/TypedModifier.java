package zgame.stat.modifier;

import com.google.gson.JsonElement;
import zgame.core.file.Saveable;
import zgame.stat.StatType;
import zusass.game.stat.ZusassStat;

/** An object holding a {@link StatModifier} and {@link StatType} */
public class TypedModifier implements Saveable{
	
	/** The json key storing {@link #modifier} */
	public static final String MOD_KEY = "mod";
	/** The json key storing {@link #type} */
	public static final String TYPE_KEY = "type";
	
	/** The modifier of this object */
	private StatModifier modifier;
	/** The type of stat of this object */
	private StatType type;
	
	/** Create an empty {@link TypedModifier}. Should only be used for loading, not for creating new instances */
	public TypedModifier(){}
	
	/**
	 * Create an object holding a {@link StatModifier} and {@link StatType}
	 * @param modifier See {@link #modifier}
	 * @param type See #type
	 */
	public TypedModifier(StatModifier modifier, StatType type){
		this.modifier = modifier;
		this.type = type;
	}
	
	/** @return See {@link #modifier} */
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
	
	@Override
	public JsonElement save(JsonElement e){
		var obj = e.getAsJsonObject();
		obj.add(MOD_KEY, this.modifier.save());
		obj.addProperty(TYPE_KEY, type.name());
		return e;
	}
	
	@Override
	public JsonElement load(JsonElement e) throws ClassCastException, IllegalStateException, NullPointerException{
		var obj = e.getAsJsonObject();
		var mod = new StatModifier();
		mod.load(obj.get(MOD_KEY));
		this.type = ZusassStat.valueOf(obj.get(TYPE_KEY).getAsString());
		return e;
	}
}
