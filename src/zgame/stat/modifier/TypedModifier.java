package zgame.stat.modifier;

import com.google.gson.JsonElement;
import zgame.core.file.Saveable;
import zgame.stat.StatType;

/** An object holding a {@link StatModifier} and {@link StatType} */
public class TypedModifier implements Saveable{
	
	/** The json key storing {@link #modifier} */
	public static final String MOD_KEY = "mod";
	/** The json key storing {@link #stat} */
	public static final String STAT_KEY = "stat";
	
	/** The modifier of this object */
	private StatModifier modifier;
	/** The type of stat of this object */
	private StatType<?> stat;
	
	/** Create a new object using see {@link #load(JsonElement)} */
	public TypedModifier(JsonElement e) throws ClassCastException, IllegalStateException, NullPointerException{
		this.load(e);
	}
	
	/**
	 * Create an object holding a {@link StatModifier} and {@link StatType}
	 * @param modifier See {@link #modifier}
	 * @param stat See #type
	 */
	public TypedModifier(StatModifier modifier, StatType<?> stat){
		this.modifier = modifier;
		this.stat = stat;
	}
	
	/** @return See {@link #modifier} */
	public StatModifier modifier(){
		return this.modifier;
	}
	
	/** @return See #type */
	public StatType<?> type(){
		return this.stat;
	}
	
	/** @return The id of the {@link StatType} used by this object */
	public int getId(){
		return this.stat.getId();
	}
	
	@Override
	public boolean save(JsonElement e){
		var obj = e.getAsJsonObject();
		Saveable.save(MOD_KEY, e, this.modifier);
		obj.addProperty(STAT_KEY, stat.name());
		return true;
	}
	
	@Override
	public boolean load(JsonElement e) throws ClassCastException, IllegalStateException, NullPointerException{
		var typeString = Saveable.s(STAT_KEY, e, null);
		this.stat = StatType.get(typeString);
		this.modifier = Saveable.obj(MOD_KEY, e, StatModifier.class, () -> new StatModifier(0, ModifierType.ADD));
		return true;
	}
}
