package zusass.game.magic.effect;

import com.google.gson.JsonElement;
import zgame.core.file.Saveable;
import zusass.game.stat.ZusassStat;
import zusass.game.things.entities.mobs.ZusassMob;

/** A spell effect that instantly adds an amount to a stat of a mob when it is applied */
public class SpellEffectStatAdd implements SpellEffect{
	/** The key used for saving this object */
	public static final String STAT_KEY = "stat";
	/** The key used for saving this object */
	public static final String AMOUNT_KEY = "amount";
	
	/** The stat which is effected by this effect */
	private ZusassStat stat;
	/** The amount to add when this effect is applied */
	private double amount;
	
	/** Create a new object using see {@link #load(JsonElement)} */
	public SpellEffectStatAdd(JsonElement e) throws ClassCastException, IllegalStateException, NullPointerException{
		this.load(e);
	}
	
	/**
	 * Create a spell effect that instantly adds an amount to a stat of a mob when it is applied
	 *
	 * @param stat See {@link #stat}
	 * @param amount See {@link #amount}
	 */
	public SpellEffectStatAdd(ZusassStat stat, double amount){
		this.stat = stat;
		this.amount = amount;
	}
	
	/** @return See {@link #stat} */
	public ZusassStat getStat(){
		return this.stat;
	}
	
	/** @return See {@link #amount} */
	public double getAmount(){
		return this.amount;
	}
	
	@Override
	public double getCost(){
		var a = this.getAmount();
		return (a < 0 ? this.stat.getDebuffValue() : this.stat.getBuffValue()) * Math.abs(a) * 0.5;
	}
	
	@Override
	public void apply(String sourceId, ZusassMob mob){
		mob.getStat(this.getStat()).addValue(this.getAmount());
	}
	
	@Override
	public boolean save(JsonElement e){
		var obj = e.getAsJsonObject();
		obj.addProperty(STAT_KEY, this.getStat().name());
		obj.addProperty(AMOUNT_KEY, this.getAmount());
		return true;
	}
	
	@Override
	public boolean load(JsonElement e) throws ClassCastException, IllegalStateException, NullPointerException{
		this.stat = Saveable.e(STAT_KEY, e, ZusassStat.class, ZusassStat.ATTACK_SPEED);
		this.amount = Saveable.d(AMOUNT_KEY, e, 0);
		return true;
	}
}
