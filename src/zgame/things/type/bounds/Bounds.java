package zgame.things.type.bounds;

import zgame.physics.ZVector;
import zgame.things.type.Position;

/** An object which has a bounds that can be defined in a vector space */
public interface Bounds<V extends ZVector<V>> extends Position<V>, Shape<V>{
	
	/** @return The minimum coordinate of an axis aligned bounding box containing this bounds */
	V getMinPosition();
	
	/** @return The maximum coordinate of an axis aligned bounding box containing this bounds */
	V getMaxPosition();
	
	/** @return The center coordinate of an axis aligned bounding box containing this bounds */
	V getCenterPosition();
	
	/**
	 * @param bounds The bounds to check
	 * @return true if the given bounds intersects this at the given position, false otherwise
	 */
	boolean intersects(Bounds<V> bounds);
}