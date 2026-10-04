package zgame.things.type.bounds;

import zgame.physics.ZVector;
import zgame.things.type.Position;

/** An object which has a bounds that can be defined in a vector space */
public interface Bounds<V extends ZVector<V>> extends Position<V>, Shape<V>{
	
	// TODO does it even make sense to have a "min" and "max" position? May want to avoid using that system
	/** @return The minimum value of all coordinates of this object */
	V getMinPosition();
	
	/** @return The maximum value of all coordinates of this object */
	V getMaxPosition();
	
	/** @return The center coordinate of the bounds of this object */
	V getCenterPosition();
	
}