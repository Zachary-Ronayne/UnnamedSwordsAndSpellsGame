package zgame.core;

import zgame.core.utils.Uuidable;

/** An interface which represents an object that can be ticked by the game, i.e. it can receive an updated representing time passing */
public interface GameTickable extends Uuidable{
	
	/**
	 * Called each time a game tick occurs. A tick is a game update, i.e. some amount of time passing. Override to perform an action during a game tick
	 *
	 * @param game The {@link Game} which called this method
	 * @param dt The amount of time, in seconds, which passed in this tick
	 */
	void tick(Game game, double dt);
	
}
