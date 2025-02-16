package zgame.things.type;

/** An object that tracks an x, y, and z position */
public interface Position3D{
	
	// TODO should this always be the bottom center and min y? Or should this be dependent on the bounds?
	
	/** @return The x coordinate of this object. If this object has a bounds, it's the bottom center x coordinate */
	double getX();
	
	/** @return The y coordinate of this object. If this object has a bounds, it's the minimum y coordinate */
	double getY();
	
	/** @return The z coordinate of this object. If this object has a bounds, it's the bottom center z coordinate */
	double getZ();
	
}
