package zgame.things;

import zgame.core.Game;
import zgame.core.utils.ZRect3D;
import zgame.things.type.bounds.Bounds3D;
import zgame.world.Room3D;

/** A utility interface for handling clicking on a game thing when a click happens on it */
public interface ThingClickDetector3D extends Bounds3D{
	
	/** @return The maximum distance from the clicker to this thing. Based on the bottom center of the things */
	double getMaxClickRange();
	
	/**
	 * Utility method for checking if the clicker clicked an object in the game
	 * If the clicker is attempting to click on this object, have the object activate, otherwise do nothing
	 *
	 * @param game The game used by the tick method
	 * @param room The room where this click happened
	 * @param clickerBounds The bounds of the thing that does the click
	 * @param clickAngleH The angle on the horizontal axis where the clicker clicked
	 * @param clickAngleV The angle on the vertical axis where the clicker clicked
	 *
	 * @return true if the object was activated, false otherwise
	 */
	default boolean handlePress(Game game, Room3D room, ZRect3D clickerBounds, double clickAngleH, double clickAngleV){
		
		// Find the bounds of this thing
		var thisBounds = this.getBounds();
		if(thisBounds == null) return false;
		
		// Determine if the player is in range of the thing to click
		var dist = thisBounds.distance(clickerBounds);
		if(dist > this.getMaxClickRange()) return false;
		
		// TODO Determine if a ray from the player to this object could be clicked
		
		// TODO account for other objects being in the way, preventing this click
		return true;
	}
}