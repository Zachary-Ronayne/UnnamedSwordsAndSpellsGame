package zusass.game.magic.effect;

import com.google.gson.JsonElement;
import zgame.stat.status.StatusEffect;
import zusass.game.status.StatEffect;
import zusass.game.things.entities.mobs.ZusassMob;

/** A spell effect that applies a status effect when it is applied to a mob */
public class SpellEffectStatusEffect implements SpellEffect{
	
	/** The effect to apply when this spell is applied */
	private StatusEffect effect;
	
	/** Create an empty spell effect. Should only be used when loading */
	public SpellEffectStatusEffect(){}
	
	/**
	 * Create a spell effect that applies a status effect when it is applied to a mob
	 *
	 * @param effect See {@link #effect}
	 */
	public SpellEffectStatusEffect(StatusEffect effect){
		this.effect = effect;
	}
	
	@Override
	public double getCost(){
		// TODO calculate cost based on effect duration, amount, stat type, etc
		return 20;
	}
	
	@Override
	public void apply(String sourceId, ZusassMob mob){
		mob.addEffect(sourceId, this.effect);
	}
	
	/** @return See {@link #effect} */
	public StatusEffect getEffect(){
		return this.effect;
	}
	
	@Override
	public SpellEffectType getType(){
		return SpellEffectType.STATUS_EFFECT;
	}
	
	@Override
	public JsonElement save(JsonElement e){
		return this.getEffect().save(e);
	}
	
	@Override
	public JsonElement load(JsonElement e) throws ClassCastException, IllegalStateException, NullPointerException{
		this.effect = new StatEffect();
		return this.effect.load(e);
	}
	
}
