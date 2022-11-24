package zusass.game.things;

import zgame.things.still.Door;

/** A {@link Door} specifically used by the Zusass game */
public class ZusassDoor extends Door{

	/**
	 * Create a new door at the given position
	 * 
	 * @param x The x coordinate upper left hand corner of the door
	 * @param y The y coordinate upper left hand corner of the door
	 */
	public ZusassDoor(double x, double y){
		super(x, y, false);
	}
	
}
