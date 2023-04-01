package zusass.game.status;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import zgame.core.file.Saveable;
import zgame.stat.Stat;
import zgame.stat.StatType;
import zgame.stat.modifier.StatModifier;
import zgame.stat.modifier.TypedModifier;
import zgame.stat.status.StatusEffect;
import zusass.game.things.entities.mobs.ZusassMob;

import java.util.ArrayList;
import java.util.List;

/** A {@link StatusEffect} which modifies one or many {@link Stat}s */
public class StatEffect extends StatusEffect{
	
	/** The json key storing {@link #modifiers} */
	public static final String MODS_KEY = "mods";
	
	/** All the stat modifiers applied by this effect */
	private List<TypedModifier> modifiers;
	
	/** Create a new object using see {@link #load(JsonElement)} */
	public StatEffect(JsonElement e) throws ClassCastException, IllegalStateException, NullPointerException{
		super(0);
		this.load(e);
	}
	
	/**
	 * Create a new status effect for one stat
	 *
	 * @param duration The duration of the effect
	 * @param statType The stat to effect
	 */
	public StatEffect(double duration, StatModifier mod, StatType<?> statType){
		this(duration, List.of(new TypedModifier(mod, statType)));
	}
	
	/**
	 * Create a new status effect for one stat
	 *
	 * @param duration The duration of the effect
	 * @param modifiers The modifiers to apply during the effect
	 */
	public StatEffect(double duration, List<TypedModifier> modifiers){
		super(duration);
		this.modifiers = modifiers;
	}
	
	@Override
	public void apply(String sourceId, ZusassMob mob){
		for(var m : modifiers) mob.getStats().get(m.getId()).addModifier(sourceId, m.modifier());
	}
	
	@Override
	public void clear(String sourceId, ZusassMob mob){
		for(var m : modifiers) mob.getStats().get(m.getId()).removeModifier(sourceId, m.modifier());
	}
	
	@Override
	public StatusEffect copy(){
		return new StatEffect(this.getDuration(), this.modifiers);
	}
	
	@Override
	public JsonElement save(JsonElement e){
		var arr = new JsonArray();
		e.getAsJsonObject().add(MODS_KEY, arr);
		for(var m : this.modifiers) arr.add(m.save());
		return e;
	}
	
	@Override
	public boolean load(JsonElement e) throws ClassCastException, IllegalStateException, NullPointerException{
		var arr = Saveable.arr(MODS_KEY, e);
		this.modifiers = new ArrayList<>();
		for(var m : arr) modifiers.add(new TypedModifier(m));
		return true;
	}
}
