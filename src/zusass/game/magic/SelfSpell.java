package zusass.game.magic;

import com.google.gson.JsonElement;
import zusass.ZusassGame;
import zusass.game.magic.effect.SpellEffect;
import zusass.game.things.entities.mobs.ZusassMob;

/** A spell that applies to its caster when cast */
public class SelfSpell extends Spell{
	
	/** Create a new object using see {@link #load(JsonElement)} */
	public SelfSpell(JsonElement e) throws ClassCastException, IllegalStateException, NullPointerException{
		super(e);
	}
	
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
		this.getEffect().apply(caster.getUuid(), caster);
	}
}
