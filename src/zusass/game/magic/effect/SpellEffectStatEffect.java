package zusass.game.magic.effect;

import com.google.gson.JsonElement;
import zusass.game.stat.ZusassStat;
import zusass.game.status.StatEffect;

/** A spell effect that applies a status effect effecting stats when it is applied to a mob */
public class SpellEffectStatEffect extends SpellEffectStatusEffect{
	
	/** Create a new object using see {@link #load(JsonElement)} */
	public SpellEffectStatEffect(JsonElement e) throws ClassCastException, IllegalStateException, NullPointerException{
		super(e);
	}
	
	/**
	 * Create a spell effect that applies a status effect when it is applied to a mob
	 *
	 * @param effect See {@link #effect}
	 */
	public SpellEffectStatEffect(StatEffect effect){
		super(effect);
	}
	
	// TODO allow spells to have different durations for each effect, i.e. the spell can have multiple effects
	
	@Override
	public StatEffect getEffect(){
		return (StatEffect)super.getEffect();
	}
	
	@Override
	public double getCost(){
		// This is a very arbitrary calculation atm
		// Basically bigger numbers mean higher cost
		// Should make positive and negative effects cancel the cost out, i.e. a speed spell that also damages strength should cost less than if it only granted speed
		var totalCost = 0;
		var ef = this.getEffect();
		for(var m : ef.getModifiers()){
			double base;
			switch(m.modifier().getType()){
				default -> base = 0.1;
				case MULT_ADD -> base = .2;
				case MULT_MULT -> base = .3;
			}
			totalCost += Math.abs(base * m.modifier().getValue()) * ((ZusassStat)m.type()).getValue();
		}
		return totalCost * ef.getDuration();
	}
	
	@Override
	public SpellEffectType getType(){
		return SpellEffectType.STAT_EFFECT;
	}
	
	@Override
	public boolean load(JsonElement e) throws ClassCastException, IllegalStateException, NullPointerException{
		this.setEffect(new StatEffect(e));
		return true;
	}
	
}
