package zusass.game.magic;

import com.google.gson.JsonElement;
import zusass.ZusassGame;
import zusass.game.magic.effect.SpellEffectNone;
import zusass.game.things.entities.mobs.ZusassMob;

/** A spell that does nothing when cast */
public class NoneSpell extends Spell{
	
	/**
	 * Create a new spell that does nothing
	 *
	 * @param e json to load the spell from, has no effect for a {@link NoneSpell}
	 */
	public NoneSpell(JsonElement e){
		this();
	}
	
	/** Create a new spell that does nothing */
	public NoneSpell(){
		super(new SpellEffectNone());
	}
	
	@Override
	protected void cast(ZusassGame zgame, ZusassMob caster){}
}
