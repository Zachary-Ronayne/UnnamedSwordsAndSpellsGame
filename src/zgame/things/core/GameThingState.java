package zgame.things.core;

/** The base object representing a game thing's state */
public abstract class GameThingState{
	
	// TODO make all things implement this, it will need to copy over all fields as needed
	
	/**
	 * Copy the values from the given state to this state
	 *
	 * @param source The state to copy data from
	 */
	public void copyState(GameThingState source){
	}
	
}
