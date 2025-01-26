package zgame.things;

import zgame.core.Game;
import zgame.physics.ZVector3D;
import zgame.things.type.bounds.Clickable3D;
import zgame.things.type.bounds.ClickerBounds;
import zgame.world.Room3D;

/** A utility interface for handling clicking on a game thing when a click happens on it */
public interface ThingClickDetector3D extends Clickable3D{
	
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
	 *
	 * @return The distance from this object
	 */
	default double findClickDistance(ClickerBounds clickerBounds){
		// Determine if the clicker is in range of the thing to click
		var clickDirection = new ZVector3D(clickerBounds.getClickAngleH(), clickerBounds.getClickAngleV(), 1, false);
		return this.rayDistance(clickerBounds.getClickX(), clickerBounds.getClickY(), clickerBounds.getClickZ(),
				clickDirection.getX(), clickDirection.getY(), clickDirection.getZ());
	}
	
	/**
	 * Utility method for determining if the thing clicking on this thing is able to click
	 *
	 * @param clickerBounds The bounds of the thing that does the click
	 *
	 * @return true if this thing can be clicked, false otherwise
	 */
	default boolean canClick(ClickerBounds clickerBounds){
		return this.canClick(clickerBounds.getClickRange(), this.findClickDistance(clickerBounds));
	}
	
	/**
	/**
	 * Utility method for determining if the thing clicking on this thing is able to click
	 *
	 * @param distance The distance the thing is away from this thing
	 * @param clickRange The distance the clicker can be away from this thing
	 *
	 * @return true if this thing can be clicked, false otherwise
	 */
	default boolean canClick(double clickRange, double distance){
		return distance <= clickRange && distance >= 0;
	}
	
}