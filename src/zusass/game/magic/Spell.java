package zusass.game.magic;

import static zusass.game.stat.ZusassStat.*;

import zusass.ZusassGame;
import zusass.game.magic.effect.SpellEffect;
import zusass.game.things.entities.mobs.ZusassMob;

// TODO make this implement Saveable
/** An object representing a magic spell that can be cast */
public abstract class Spell{
	
	/** The effect to apply when this spell effects a {@link ZusassMob} */
	private final SpellEffect effect;
	
	/**
	 * Create a new spell
	 *
	 * @param effect {@link #effect}
	 */
	public Spell(SpellEffect effect){
		this.effect = effect;
	}
	
	/**
	 * Make the given mob attempt to cast this spell into the given game
	 *
	 * @param zgame The game the spell is cast in
	 * @param caster The mob that casts the spell
	 * @return true if the spell could be cast, false otherwise i.e. the caster doesn't have enough mana
	 */
	public boolean castAttempt(ZusassGame zgame, ZusassMob caster){
		var c = this.getCost();
		if(caster.stat(MANA) < c) return false;
		
		caster.getStat(MANA).addValue(-c);
		this.cast(zgame, caster);
		
		return true;
	}
	
	/** @return The cost of casting this spell */
	public double getCost(){
		return this.getEffect().getCost();
	}
	
	/**
	 * Cast this spell into the game, cast by the given mob
	 *
	 * @param zgame The game to cast in
	 * @param caster The mob that cast the spell
	 */
	protected abstract void cast(ZusassGame zgame, ZusassMob caster);
	
	/** @return See {@link #effect} */
	public SpellEffect getEffect(){
		return this.effect;
	}
	
}
