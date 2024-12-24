package zgame.things;

import zgame.core.Game;
import zgame.core.utils.ZRect3D;

/** A utility interface for handling clicking on a game thing when the Player clicks on them */
public interface ThingClickDetector3D{
	
	/** @return The bounds of this object to check for when it is clicked. Should be in game coordinates */
	ZRect3D getThingBounds();
	
	/**
	 * @param game The game to get the player from
	 * @return The bounds of the player to check if it is intersecting this object. Should be in game coordinates
	 */
	ZRect3D getPlayerBounds(Game game);
	
	/**
	 * Utility method for checking if the player clicked an object in the game
	 * If the player is attempting to click on this object, have the object activate, otherwise do nothing
	 *
	 * @param game The game used by the tick method
	 * @return true if the object was activated, false otherwise
	 */
	default boolean handlePress(Game game){
		// Find the player
		var oBounds = this.getPlayerBounds(game);
		if(oBounds == null) return false;
		
		// TODO figure out how this should work
		return true;
//		// Check if the player intersects the door, and the player clicked on the door, then enter it
//		var dBounds = this.getThingBounds();
//		return dBounds.intersects(oBounds) && dBounds.contains(game.mouseGX(), game.mouseGY());
	}
}