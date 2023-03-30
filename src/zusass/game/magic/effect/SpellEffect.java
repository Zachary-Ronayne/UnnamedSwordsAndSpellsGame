package zusass.game.magic.effect;

import zgame.core.file.Saveable;
import zusass.game.magic.Spell;
import zusass.game.things.entities.mobs.ZusassMob;

/** An object representing something that a {@link Spell} can do to a mob */
public interface SpellEffect extends Saveable{
	
	/** @return The amount of mana this spell should cost to cast */
	double getCost();
	
	/**
	 * Effect the given mob with this effect
	 * @param mob The mob to effect
	 * @param sourceId The id which represents who provided this effect
	 */
	void apply(String sourceId, ZusassMob mob);
	
	/** @return The type of this effect, used for saving to json */
	SpellEffectType getType();
	
}
