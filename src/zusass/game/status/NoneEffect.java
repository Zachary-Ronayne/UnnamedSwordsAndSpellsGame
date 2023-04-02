package zusass.game.status;

import zgame.stat.status.StatusEffect;
import zusass.game.things.entities.mobs.ZusassMob;

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
	public void apply(String sourceId, ZusassMob mob){}
	
	@Override
	public void clear(String sourceId, ZusassMob mob){}
	
	@Override
	public double getCost(){
		return 0;
	}
}
