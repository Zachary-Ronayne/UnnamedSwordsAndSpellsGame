package zusass.game.magic.effect;

import com.google.gson.JsonElement;
import zgame.core.file.Saveable;
import zgame.core.utils.NotNullList;
import zgame.stat.status.StatusEffect;
import zgame.stat.status.StatusEffectType;
import zusass.game.status.StatEffect;
import zusass.game.things.entities.mobs.ZusassMob;

/** A spell effect that applies a status effect when it is applied to a mob */
public class SpellEffectStatusEffect implements SpellEffect{
	
	/** The json key for saving {@link #effects} */
	public static final String EFFECTS_KEY = "effects";
	/** The json key for saving the effect data in each element of {@link #effects} */
	public static final String EFFECT_KEY = "effect";
	/** The json key for saving the type of each element of {@link #effects} */
	public static final String TYPE_KEY = "type";
	
	/** The effects to apply when this spell is applied */
	private NotNullList<StatusEffect> effects;
	
	/** Create a new object using see {@link #load(JsonElement)} */
	public SpellEffectStatusEffect(JsonElement e) throws ClassCastException, IllegalStateException, NullPointerException{
		this.load(e);
	}
	
	/**
	 * Create a spell effect that applies a status effect when it is applied to a mob
	 *
	 * @param effect A single effect for see {@link #effects}
	 */
	public SpellEffectStatusEffect(StatusEffect effect){
		this(new NotNullList<>(effect));
	}
	
	/**
	 * Create a spell effect that applies a status effect when it is applied to a mob
	 *
	 * @param effects See {@link #effects}
	 */
	public SpellEffectStatusEffect(NotNullList<StatusEffect> effects){
		this.effects = effects;
	}
	
	@Override
	public double getCost(){
		double totalCost = 0;
		for(var ef : this.effects) totalCost += ef.getCost();
		return totalCost;
	}
	
	@Override
	public void apply(String sourceId, ZusassMob mob){
		for(var effect : this.effects) mob.addEffect(sourceId, effect);
	}
	
	@Override
	public SpellEffectType getType(){
		return SpellEffectType.STATUS_EFFECT;
	}
	
	/** @return See {@link #effects} */
	public NotNullList<StatusEffect> getEffects(){
		return this.effects;
	}
	
	/** @param effect An element to add to see {@link #effects} */
	public void addEffect(StatusEffect effect){
		this.effects.add(effect);
	}
	
	@Override
	public boolean save(JsonElement e){
		var arr = Saveable.newArr(EFFECTS_KEY, e);
		for(var ef : this.effects){
			var effectItem = Saveable.newObj(arr);
			Saveable.save(EFFECT_KEY, effectItem, ef);
			effectItem.addProperty(TYPE_KEY, ef.getType().name());
		}
		return true;
	}
	
	@Override
	public boolean load(JsonElement e){
		this.effects = new NotNullList<>();
		var effects = Saveable.arr(EFFECTS_KEY, e);
		for(var ef : effects){
			var type = Saveable.e(TYPE_KEY, ef, StatusEffectType.class, StatusEffectType.STAT_EFFECT);
			StatusEffect effect;
			// For now type will always be a STAT_EFFECT, need to use a switch statement on the type when other status effect types are added
			if(type != StatusEffectType.STAT_EFFECT) continue;
			effect = new StatEffect(ef.getAsJsonObject().get(EFFECT_KEY));
			this.effects.add(effect);
		}
		return true;
	}
	
}
