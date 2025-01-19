package zgame.things;

import zgame.core.Game;
import zgame.physics.ZVector3D;
import zgame.things.entity.mobility.MobilityEntity3D;
import zgame.things.type.bounds.Bounds3D;
import zgame.things.type.bounds.Clickable3D;
import zgame.world.Room3D;

/** A utility interface for handling clicking on a game thing when a click happens on it */
public interface ThingClickDetector3D extends Clickable3D{
	
	/** @return The maximum distance from the clicker to this thing. Based on the bottom center of the things */
	double getMaxClickRange();
	
	/**
	 * Run when this thing is clicked
	 *
	 * @param game The game used by the tick method
	 * @param room The room where this click happened
	 */
	void handlePress(Game game, Room3D room);
	
	/**
	 * Utility method for finding the distance from a thing clicking on this thing
	 *
	 * @param clickerBounds The bounds of the thing that does the click
	 * @param clickAngleH The angle on the horizontal axis where the clicker clicked
	 * @param clickAngleV The angle on the vertical axis where the clicker clicked
	 *
	 * @return The distance from this object
	 */
	default double findClickDistance(Bounds3D clickerBounds, double clickAngleH, double clickAngleV){
		// Determine if the clicker is in range of the thing to click
		var clickDirection = new ZVector3D(clickAngleH, clickAngleV, 1, false);
		// TODO maybe make a separate thing for getting the eye position for where the click would happen?
		return this.rayDistance(clickerBounds.getX(), clickerBounds.getY() + clickerBounds.getHeight(), clickerBounds.getZ(),
				clickDirection.getX(), clickDirection.getY(), clickDirection.getZ());
	}
	
	/**
	 * Utility method for determining if the thing clicking on this thing is able to click
	 *
	 * @param clickerBounds The bounds of the thing that does the click
	 * @param clickAngleH The angle on the horizontal axis where the clicker clicked
	 * @param clickAngleV The angle on the vertical axis where the clicker clicked
	 *
	 * @return true if this thing can be clicked, false otherwise
	 */
	default boolean canClick(Bounds3D clickerBounds, double clickAngleH, double clickAngleV){
		return this.canClick(this.findClickDistance(clickerBounds, clickAngleH, clickAngleV));
	}
	
	/**
	 * Utility method for determining if the thing clicking on this thing is able to click
	 *
	 * @param clicker The thing clicking
	 *
	 * @return true if this thing can be clicked, false otherwise
	 */
	default boolean canClick(MobilityEntity3D clicker){
		var mobilityData = clicker.getMobilityData();
		return this.canClick(this.findClickDistance(clicker, mobilityData.getFacingHorizontalAngle(), mobilityData.getFacingVerticalAngle()));
	}
	
	/**
	 * Utility method for determining if the thing clicking on this thing is able to click
	 *
	 * @param distance The distance the thing is away from this thing
	 *
	 * @return true if this thing can be clicked, false otherwise
	 */
	default boolean canClick(double distance){
		return distance <= this.getMaxClickRange() && distance >= 0;
	}
	
}