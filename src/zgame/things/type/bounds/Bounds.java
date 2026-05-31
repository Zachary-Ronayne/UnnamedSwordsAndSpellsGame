package zgame.things.type.bounds;

import zgame.physics.ZVector;
import zgame.things.type.Position;

/** An object which has a bounds that can be defined in a vector space */
public interface Bounds<V extends ZVector<V>> extends Position<V>{
	
	/** @return The minimum value of all coordinates of this object */
	V getMinPosition();
	
	/** @return The maximum value of all coordinates of this object */
	V getMaxPosition();
	
	/** @return The center coordinate of the bounds of this object */
	V getCenterPosition();
	
	/** @return The total size of this bounds across all dimensions */
	V getDimensions();
	
}