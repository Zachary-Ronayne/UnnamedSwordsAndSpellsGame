package zusass.game.magic.effect;

import zusass.game.magic.Spell;
import zusass.game.things.entities.mobs.ZusassMob;

/** An object representing something that a {@link Spell} can do to a mob */
public interface SpellEffect{
	
	/** @return The amount of mana this spell should cost to cast */
	double getCost();
	
	/**
	 * Effect the given mob with this effect
	 * @param mob The mob to effect
	 */
	void apply(ZusassMob mob);
	
}
