package zusass.game.magic.effect;

import com.google.gson.JsonElement;
import zgame.stat.status.StatusEffect;
import zusass.game.things.entities.mobs.ZusassMob;

/** A spell effect that applies a status effect when it is applied to a mob */
public abstract class SpellEffectStatusEffect implements SpellEffect{
	
	/** The effect to apply when this spell is applied */
	private StatusEffect effect;
	
	/** Create a new object using see {@link #load(JsonElement)} */
	public SpellEffectStatusEffect(JsonElement e) throws ClassCastException, IllegalStateException, NullPointerException{
		this.load(e);
	}
	
	/**
	 * Create a spell effect that applies a status effect when it is applied to a mob
	 *
	 * @param effect See {@link #effect}
	 */
	public SpellEffectStatusEffect(StatusEffect effect){
		this.effect = effect;
	}
	
	@Override
	public void apply(String sourceId, ZusassMob mob){
		mob.addEffect(sourceId, this.effect);
	}
	
	/** @return See {@link #effect} */
	public StatusEffect getEffect(){
		return this.effect;
	}
	
	/** @param effect See {@link #effect} */
	public void setEffect(StatusEffect effect){
		this.effect = effect;
	}
	
	@Override
	public boolean save(JsonElement e){
		return this.getEffect().save(e);
	}
	
}
