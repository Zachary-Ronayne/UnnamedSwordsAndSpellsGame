package zgame.things.type;

/** An object that tracks an x and y position */
public interface Position{
	
	/** @return The x coordinate of this object. If this object has a bounds, it's the minimum x coordinate */
	public double getX();
	
	/** @return The y coordinate of this object. If this object has a bounds, it's the minimum y coordinate */
	public double getY();
	
	/** @return The x coordinate of the center of this object */
	public double centerX();
	
	/** @return The y coordinate of the center this object */
	public double centerY();
}
