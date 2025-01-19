package zgame.things;

import zgame.core.Game;
import zgame.physics.ZVector3D;
import zgame.things.type.bounds.Clickable3D;
import zgame.things.type.bounds.HitBox3D;
import zgame.world.Room3D;

/** A utility interface for handling clicking on a game thing when a click happens on it */
public interface ThingClickDetector3D extends Clickable3D{
	
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
	// TODO are all of these parameters needed?
	default boolean handlePress(Game game, Room3D room, HitBox3D clickerBounds, double clickAngleH, double clickAngleV){
		// TODO split up handling the press and detecting if the thing was pressed based on distance, then go through all objects and tiles to determine which one should be clicked
		
		// TODO account for other objects being in the way, preventing this click
		// Determine if the clicker is in range of the thing to click
		var clickDirection = new ZVector3D(clickAngleH, clickAngleV, 1, false);
		// TODO maybe make a separate thing for getting the eye position for where the click would happen?
		var dist = this.rayDistance(clickerBounds.getX(), clickerBounds.getY() + clickerBounds.getHeight(), clickerBounds.getZ(),
				clickDirection.getX(), clickDirection.getY(), clickDirection.getZ());
		return dist <= this.getMaxClickRange() && dist >= 0;
	}
}