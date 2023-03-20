package zusass.game.magic.effect;

import zgame.stat.status.StatusEffect;
import zusass.game.things.entities.mobs.ZusassMob;

/**
 * A spell effect that applies a status effect when it is applied to a mob
 *
 * @param effect The effect to apply when this spell is applied
 */
public record SpellEffectStatusEffect(StatusEffect effect) implements SpellEffect{
	@Override
	public double getCost(){
		// TODO calculate cost based on effect duration, amount, stat type, etc
		return 20;
	}
	
	@Override
	public void apply(ZusassMob mob){
		mob.addEffect(this.effect);
	}
	
}
