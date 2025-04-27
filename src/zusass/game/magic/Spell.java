package zusass.game.magic;

import static zusass.game.stat.ZusassStat.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import zgame.core.file.Saveable;
import zgame.core.utils.NotNullList;
import zgame.stat.modifier.ModifierType;
import zgame.stat.modifier.StatModifier;
import zusass.game.magic.effect.*;
import zusass.game.stat.ZusassStat;
import zusass.game.status.StatEffect;
import zusass.game.things.entities.mobs.ZusassMob;

/** An object representing a magic spell that can be cast */
public abstract class Spell implements Saveable{
	
	/** The json key storing {@link #effects} */
	public static final String EFFECTS_KEY = "spellEffects";
	
	/** The json key storing the type of effect of the spell */
	public static final String TYPE_KEY = "effectType";
	/** The json key storing the data of an effect of the spell */
	public static final String EFFECT_KEY = "spellEffect";
	/** The json key storing the data of the name of this spell */
	public static final String NAME_KEY = "name";
	
	/** The effects to apply when this spell effects a {@link ZusassMob} */
	private NotNullList<SpellEffect> effects;
	
	/** The name of this spell */
	private String name;
	
	/** The current cost of the spell */
	private double cost;
	
	/** Create a new object using see {@link #load(JsonElement)} */
	public Spell(JsonElement e) throws ClassCastException, IllegalStateException, NullPointerException{
		this.load(e);
	}
	
	/**
	 * Create a new spell with one effect
	 *
	 * @param effect A single effect for {@link #effects}
	 */
	public Spell(SpellEffect effect){
		this(new NotNullList<>(effect));
	}
	
	/**
	 * Create a new spell with many effects
	 *
	 * @param effects See {@link #effects}
	 */
	public Spell(NotNullList<SpellEffect> effects){
		this.effects = effects;
		this.name = "spell";
		this.calculateCost();
	}
	
	/** @return See {@link #name} */
	public String getName(){
		return this.name;
	}
	
	/** @param name See {@link #name} */
	public void setName(String name){
		this.name = name;
	}
	
	/** @return A string representing the name od this spell, and its cost */
	public String nameAndCost(){
		return new StringBuilder(this.getName()).append(" (").append(String.format("%.2f", this.getCost())).append(")").toString();
	}
	
	/**
	 * @param name The new name of this spell
	 * @return this
	 */
	public Spell named(String name){
		this.setName(name);
		return this;
	}
	
	/**
	 * Make the given mob attempt to cast this spell into the given game
	 *
	 * @param caster The mob that casts the spell
	 * @return true if the spell could be cast, false otherwise i.e. the caster doesn't have enough mana
	 */
	public final boolean castAttempt(ZusassMob caster){
		var c = this.getCost();
		if(caster.stat(MANA) < c) return false;
		
		caster.getStat(MANA).addValue(-c);
		this.cast(caster);
		
		return true;
	}
	
	/** @return The cost of casting this spell */
	public double getCost(){
		return this.cost;
	}
	
	/** Calculate the cost of casting this spell */
	public void calculateCost(){
		double totalCost = 0;
		for(var ef : this.getEffects()) totalCost += ef.getCost();
		this.cost = totalCost;
	}
	
	/**
	 * Cast this spell into the game, cast by the given mob. This method instantly casts the spell and does not account for things like current mana or the amount of time it
	 * takes to cast the spell. Use {@link #castAttempt(ZusassMob)} for such cases
	 *
	 * @param caster The mob that cast the spell
	 */
	protected abstract void cast(ZusassMob caster);
	
	/** @return See {@link #effects}. If updating the returned value, also call {@link #calculateCost()} to update the spell cost */
	public NotNullList<SpellEffect> getEffects(){
		return this.effects;
	}
	
	@Override
	public boolean save(JsonElement e){
		var obj = e.getAsJsonObject();
		obj.addProperty(NAME_KEY, this.getName());
		var arr = Saveable.newArr(EFFECTS_KEY, e);
		for(var ef : this.effects){
			obj = new JsonObject();
			arr.add(obj);
			
			obj.addProperty(TYPE_KEY, SpellEffectType.name(ef.getClass()));
			Saveable.save(EFFECT_KEY, obj, ef);
		}
		return true;
	}
	
	@Override
	public boolean load(JsonElement e) throws ClassCastException, IllegalStateException, NullPointerException{
		this.name = Saveable.s(NAME_KEY, e, "spell");
		this.effects = new NotNullList<>();
		var arr = Saveable.arr(EFFECTS_KEY, e);
		for(var ef : arr){
			this.effects.add(Saveable.obj(TYPE_KEY, SpellEffectType.class, EFFECT_KEY, ef, SpellEffectType.NONE));
		}
		this.calculateCost();
		return true;
	}
	
	/**
	 * Create a {@link ProjectileSpell} which adds the given amount to the given stat when the spell is applied
	 *
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
