package zgame.things.type;

/** An object that tracks an x and y position */
public interface Position{
	
	// TODO somehow abstract this to 2D and 3D?
	
	/** @return The x coordinate of this object. If this object has a bounds, it's the minimum x coordinate */
	double getX();
	
	/** @return The y coordinate of this object. If this object has a bounds, it's the minimum y coordinate */
	double getY();
	
	/** @return The x coordinate of the center of this object */
	double centerX();
	
	/** @return The y coordinate of the center this object */
	double centerY();
}
