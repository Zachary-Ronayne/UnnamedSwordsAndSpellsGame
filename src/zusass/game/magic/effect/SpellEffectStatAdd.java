package zusass.game.magic.effect;

import zgame.stat.StatType;
import zusass.game.things.entities.mobs.ZusassMob;

/**
 * A spell effect that instantly adds an amount to a stat of a mob when it is applied
 *
 * @param stat The stat which is effected by this effect
 * @param amount The amount to add when this effect is applied
 */
public record SpellEffectStatAdd(StatType stat, double amount) implements SpellEffect{
	@Override
	public double getCost(){
		// TODO calculate by amount and stat type
		return 10;
	}
	
	@Override
	public void apply(ZusassMob mob){
		mob.getStat(this.stat()).addValue(this.amount());
	}
}
