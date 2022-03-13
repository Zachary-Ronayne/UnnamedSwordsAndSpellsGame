package zgame.core;

import zgame.core.state.PlayState;
import zgame.things.Room;

/** An interface which represents an object that can be ticked by the game, i.e. it can receive an updated representing time passing */
public interface GameTickable{
	
	/**
	 * Called each time a game tick occurs. A tick is a game update, i.e. some amount of time passing.
	 * Override to perform an action during a game tick
	 * 
	 * @param game The {@link Game} which called this method
	 * @param state The {@link PlayState} which the game was in when this method was called
	 * @param r The {@link Room} which contains this {@link GameTickable} object
	 * @param dt The amount of time, in seconds, which passed in this tick
	 */
	public void tick(Game game, PlayState state, Room r, double dt);
	
}
