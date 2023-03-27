package zusass.game.magic.effect;

import zusass.game.things.entities.mobs.ZusassMob;

/** A {@link SpellEffect} which does nothing */
public class SpellEffectNone implements SpellEffect{
	
	/** Create a new spell effect that does nothing */
	public SpellEffectNone(){}
	
	@Override
	public double getCost(){
		return 0;
	}
	
	@Override
	public void apply(ZusassMob mob){}
	
	@Override
	public SpellEffectType getType(){
		return SpellEffectType.NONE;
	}
}
