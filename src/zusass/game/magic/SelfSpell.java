package zusass.game.magic;

import zusass.ZusassGame;
import zusass.game.magic.effect.SpellEffect;
import zusass.game.things.entities.mobs.ZusassMob;

/** A spell that applies to its caster when cast */
public class SelfSpell extends Spell{
	
	/**
	 * Create a new spell that applies to its caster
	 *
	 * @param effect {@link #effect}
	 */
	public SelfSpell(SpellEffect effect){
		super(effect);
	}
	
	@Override
	protected void cast(ZusassGame zgame, ZusassMob caster){
		this.getEffect().apply(caster);
	}
}
