package zusass.game.magic;

import zusass.ZusassGame;
import zusass.game.magic.effect.SpellEffect;
import zusass.game.things.entities.mobs.ZusassMob;

/** A spell that applies to its caster when cast */
public class SelfSpell extends Spell{
	
	/** Create an empty spell. Should only be used when loading from a file and initialization */
	public SelfSpell(){
		super();
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
	
	@Override
	public SpellType getType(){
		return SpellType.SELF;
	}
}
