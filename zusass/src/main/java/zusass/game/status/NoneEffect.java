package zusass.game.status;

import zgame.stat.StatThing;
import zgame.stat.status.StatusEffect;

/** A {@link StatusEffect} which does nothing */
public class NoneEffect extends StatusEffect{
	
	/**
	 * Create a new {@link NoneEffect}
	 */
	public NoneEffect(){
		super(0);
	}
	
	@Override
	public StatusEffect copy(){
		return new NoneEffect();
	}
	
	@Override
	public void apply(String sourceId, StatThing mob){}
	
	@Override
	public void clear(String sourceId, StatThing mob){}
	
	@Override
	public double getCost(){
		return 0;
	}
}
