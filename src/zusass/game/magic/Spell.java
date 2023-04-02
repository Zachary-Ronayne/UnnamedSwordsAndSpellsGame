package zusass.game.magic;

import static zusass.game.stat.ZusassStat.*;

import com.google.gson.JsonElement;
import zgame.core.file.Saveable;
import zgame.stat.modifier.ModifierType;
import zgame.stat.modifier.StatModifier;
import zusass.ZusassGame;
import zusass.game.magic.effect.*;
import zusass.game.stat.ZusassStat;
import zusass.game.status.StatEffect;
import zusass.game.things.entities.mobs.ZusassMob;

/** An object representing a magic spell that can be cast */
public abstract class Spell implements Saveable{
	
	/** The json key storing the type of effect of the spell */
	private static final String TYPE_KEY = "effectType";
	/** The json key storing the effect data of the spell */
	private static final String EFFECT_KEY = "spellEffect";
	
	// TODO allow spells to have many spell effects
	
	/** The effect to apply when this spell effects a {@link ZusassMob} */
	private SpellEffect effect;
	
	/** Create a new object using see {@link #load(JsonElement)} */
	public Spell(JsonElement e) throws ClassCastException, IllegalStateException, NullPointerException{
		this.load(e);
	}
	
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
	
	@Override
	public boolean save(JsonElement e){
		var obj = e.getAsJsonObject();
		obj.addProperty(TYPE_KEY, SpellEffectType.name(this.getEffect().getClass()));
		Saveable.save(EFFECT_KEY, e, this.getEffect());
		return true;
	}
	
	@Override
	public boolean load(JsonElement e) throws ClassCastException, IllegalStateException, NullPointerException{
		this.effect = Saveable.obj(TYPE_KEY, SpellEffectType.class, EFFECT_KEY, e, SpellEffectType.NONE);
		return true;
	}
	
	/**
	 * Create a {@link ProjectileSpell} which adds the given amount to the given stat when the spell is applied
	 * @param stat The stat to effect
	 * @param amount The amount of the stat to add
	 * @return The spell
	 */
	public static ProjectileSpell projectileAdd(ZusassStat stat, double amount){
		return new ProjectileSpell(new SpellEffectStatAdd(stat, amount));
	}
	
	/**
	 * Create a spell that applies to the caster when cast and applies a stat status effect.
	 *
	 * @param stat The stat to effect
	 * @param magnitude The amount of power in the spell
	 * @param duration The duration of the spell, in seconds
	 * @param mod The way the spell is applied
	 * @return The spell
	 */
	public static SelfSpell selfEffect(ZusassStat stat, double magnitude, double duration, ModifierType mod){
		return new SelfSpell(new SpellEffectStatusEffect(new StatEffect(duration, new StatModifier(magnitude, mod), stat)));
	}
	
}
