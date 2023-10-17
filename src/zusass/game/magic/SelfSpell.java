package zusass.game.magic;

import com.google.gson.JsonElement;
import zgame.core.utils.NotNullList;
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
	 * Create a new spell that applies a single effect to its caster
	 *
	 * @param effect A single effect for {@link #effects}
	 */
	public SelfSpell(SpellEffect effect){
		this(new NotNullList<>(effect));
	}
	
	/**
	 * Create a new spell that applies many effects to its caster
	 *
	 * @param effects {@link #effects}
	 */
	public SelfSpell(NotNullList<SpellEffect> effects){
		super(effects);
	}
	
	@Override
	protected void cast(ZusassGame zgame, ZusassMob caster){
		for(var ef : this.getEffects()) ef.apply(caster.getUuid(), caster);
	}
}
