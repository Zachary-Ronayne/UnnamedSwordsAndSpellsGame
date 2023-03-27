package zusass.game.magic;

import static zusass.game.stat.ZusassStat.*;

import com.google.gson.JsonElement;
import zgame.core.file.Saveable;
import zgame.stat.StatType;
import zgame.stat.Stats;
import zgame.stat.modifier.ModifierType;
import zgame.stat.modifier.StatModifier;
import zusass.ZusassGame;
import zusass.game.magic.effect.*;
import zusass.game.status.StatEffect;
import zusass.game.things.entities.mobs.ZusassMob;

/** An object representing a magic spell that can be cast */
public abstract class Spell implements Saveable{
	
	/** The json key storing the type of effect of the spell */
	private static final String TYPE_KEY = "type";
	/** The json key storing the effect data of the spell */
	private static final String EFFECT_KEY = "type";
	
	/** The effect to apply when this spell effects a {@link ZusassMob} */
	private SpellEffect effect;
	
	/** Create an empty spell. Should only be used when loading from a file and initialization */
	public Spell(){}
	
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
	
	/** @return The type of this spell, used for saving */
	public abstract SpellType getType();
	
	@Override
	public JsonElement save(JsonElement e){
		// TODO make some utility methods to make saving and loading less annoying
		// TODO make a way for the utility methods to load a default value when something is null
		// TODO make a decentralized way of saving and loading the player so that things can properly load and save when in the main menu
		var obj = e.getAsJsonObject();
		obj.addProperty(TYPE_KEY, this.getEffect().getType().name());
		obj.add(EFFECT_KEY, this.getEffect().save());
		return e;
	}
	
	@Override
	public JsonElement load(JsonElement e) throws ClassCastException, IllegalStateException, NullPointerException{
		var obj = e.getAsJsonObject();
		var type = SpellEffectType.valueOf(obj.get(TYPE_KEY).getAsString());
		switch(type){
			case NONE -> this.effect = new SpellEffectNone();
			case STAT_ADD -> this.effect = new SpellEffectStatAdd();
			case STATUS_EFFECT -> this.effect = new SpellEffectStatusEffect();
			default -> throw new IllegalStateException("Invalid spell effect type: " + type);
		}
		this.effect.load(obj.get(EFFECT_KEY));
		return obj;
	}
	
	/**
	 * Create a {@link ProjectileSpell} which adds the given amount to the given stat when the spell is applied
	 * @param stat The stat to effect
	 * @param amount The amount of the stat to add
	 * @return The spell
	 */
	public static ProjectileSpell projectileAdd(StatType stat, double amount){
		return new ProjectileSpell(new SpellEffectStatAdd(stat, amount));
	}
	
	/**
	 * Create a spell that applies to the caster when cast and applies a stat status effect.
	 *
	 * @param stats The stats object of the caster
	 * @param stat The stat to effect
	 * @param source The caster's id
	 * @param magnitude The amount of power in the spell
	 * @param duration The duration of the spell, in seconds
	 * @param mod The way the spell is applied
	 * @return The spell
	 */
	public static SelfSpell selfEffect(Stats stats, StatType stat, String source, double magnitude, double duration, ModifierType mod){
		return new SelfSpell(new SpellEffectStatusEffect(new StatEffect(duration, new StatModifier(source, magnitude, mod), stat)));
	}
	
}
