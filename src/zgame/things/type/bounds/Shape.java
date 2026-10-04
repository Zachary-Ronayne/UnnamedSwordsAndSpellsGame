package zgame.things.type.bounds;

import zgame.physics.ZVector;

/** Represents some arbitrary shape */
public interface Shape<V extends ZVector<V>>{
	/** @return The total size of this shape across all axes on an axis aligned bounding box */
	V getDimensions();
	
	/** @return The type of this object's bounds for world interactions */
	BoundsType getBoundsType();
	
	// TODO how should other position information be passed here? What if a shape needs a rotation or other field?
	/**
	 * @param bounds The bounds to check
	 * @return true if the given bounds intersects this at the given position, false otherwise
	 */
	boolean intersects(Bounds<V> bounds);
	
	// TODO next, figure out exactly what should be in shape vs bounds vs hitbox and move all logic as needed, figure out where intersects and collide methods should be placed
	
}
