package zgame.things;

import zgame.core.Game;
import zgame.core.utils.ZRect2D;

/** A utility interface for handling clicking on a game thing when the Player clicks on them */
public interface ThingClickDetector2D{
	
	/** @return The bounds of this object to check for when it is clicked. Should be in game coordinates */
	ZRect2D getThingBounds();
	
	/**
	 * @return The bounds of the player to check if it is intersecting this object. Should be in game coordinates
	 */
	ZRect2D getPlayerBounds();
	
	/**
	 * Utility method for checking if the player clicked an object in the game
	 * If the player is attempting to click on this object, have the object activate, otherwise do nothing
	 *
	 * @return true if the object was activated, false otherwise
	 */
	default boolean handlePress(){
		// Find the player
		var oBounds = this.getPlayerBounds();
		if(oBounds == null) return false;
		
		// Check if the player intersects the door, and the player clicked on the door, then enter it
		var dBounds = this.getThingBounds();
		var game = Game.get();
		return dBounds.intersects(oBounds) && dBounds.contains(game.mouseGX(), game.mouseGY());
	}
}