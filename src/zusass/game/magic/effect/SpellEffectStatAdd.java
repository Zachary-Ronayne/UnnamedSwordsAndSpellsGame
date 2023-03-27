package zusass.game.magic.effect;

import com.google.gson.JsonElement;
import zgame.stat.StatType;
import zusass.game.stat.ZusassStat;
import zusass.game.things.entities.mobs.ZusassMob;

/** A spell effect that instantly adds an amount to a stat of a mob when it is applied */
public class SpellEffectStatAdd implements SpellEffect{
	/** The key used for saving this object */
	public static final String STAT_KEY = "stat";
	/** The key used for saving this object */
	public static final String AMOUNT_KEY = "amount";
	
	/** The stat which is effected by this effect */
	private StatType stat;
	/** The amount to add when this effect is applied */
	private double amount;
	
	/** Create an empty spell effect. Should only be used when loading */
	public SpellEffectStatAdd(){}
	
	/**
	 * Create a spell effect that instantly adds an amount to a stat of a mob when it is applied
	 *
	 * @param stat See {@link #stat}
	 * @param amount See {@link #amount}
	 */
	public SpellEffectStatAdd(StatType stat, double amount){
		this.stat = stat;
		this.amount = amount;
	}
	
	/** @return See {@link #stat} */
	public StatType getStat(){
		return this.stat;
	}
	
	/** @return See {@link #amount} */
	public double getAmount(){
		return this.amount;
	}
	
	@Override
	public double getCost(){
		// TODO calculate by amount and stat type
		return 10;
	}
	
	@Override
	public void apply(ZusassMob mob){
		mob.getStat(this.getStat()).addValue(this.getAmount());
	}
	
	@Override
	public SpellEffectType getType(){
		return SpellEffectType.STAT_ADD;
	}
	
	@Override
	public JsonElement save(JsonElement e){
		var obj = e.getAsJsonObject();
		obj.addProperty(STAT_KEY, this.getStat().name());
		obj.addProperty(AMOUNT_KEY, this.getAmount());
		return e;
	}
	
	@Override
	public JsonElement load(JsonElement e) throws ClassCastException, IllegalStateException, NullPointerException{
		var obj = e.getAsJsonObject();
		this.stat = ZusassStat.valueOf(obj.get(STAT_KEY).getAsString());
		this.amount = obj.get(STAT_KEY).getAsDouble();
		return SpellEffect.super.load(obj);
	}
}
