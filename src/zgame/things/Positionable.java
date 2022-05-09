package zgame.things;

/** An interface which describes an object that tracks an x and y position */
public interface Positionable{
	
	/** @return The x coordinate of this object */
	public double getX();
	
	/** @return The y coordinate of this object */
	public double getY();

	/** @return The x coordinate of the center of this object */
	public double centerX();
	
	/** @return The y coordinate of the center this object */
	public double centerY();
}
