package zusass.game.things.entities.mobs;

import zgame.things.entity.EntityThing;
import zgame.things.type.GameThing;
import zusass.ZusassGame;

/** Represents an object with stats, i.e. health, skills, etc, which can be interacted with */
public interface StatThing{
	
	/**
	 * Should be called when a {@link zgame.core.GameTickable} thing using this {@link Stats} has its tick method called
	 * 
	 * @param game The game which caused the tick to happen
	 * @param dt The amount of time, in seconds, which passed in this tick
	 */
	public default void tickStats(ZusassGame game, double dt){
		// If this thing has 0 or less health, kill it
		if(this.getStats().getHealth() <= 0) this.die(game);
	}
	
	/**
	 * Called when this stat thing dies
	 * 
	 * @param game The game it was in when it died
	 */
	public default void die(ZusassGame game){
		// On death, by default, remove the thing from the game
		game.getCurrentRoom().removeThing(get());
	}
	
	/** @return The {@link Stats} used by this thing */
	public Stats getStats();
	
	/** @return The {@link GameThing} using this {@link StatThing} */
	public EntityThing get();
	
}
