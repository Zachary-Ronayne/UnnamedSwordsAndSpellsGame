package zgame.things.type.bounds;

import zgame.physics.ZVector;
import zgame.physics.collision.Collision;
import zgame.things.type.Position;

/** An object which has a bounds that can be defined in a vector space */
public interface Bounds<V extends ZVector<V>> extends Position<V>, Shape<V>{
	
	/** @return The minimum coordinate of an axis aligned bounding box containing this bounds */
	V getMinPosition();
	
	/** @return The maximum coordinate of an axis aligned bounding box containing this bounds */
	default V getMaxPosition(){
		return this.getMinPosition().add(this.getDimensions());
	}
	
	/** @return The center coordinate of an axis aligned bounding box containing this bounds */
	default V getCenterPosition(){
		return this.getMinPosition().add(this.getDimensions().scale(0.5));
	}
	
	/**
	 * @param bounds The bounds to check
	 * @return true if the given bounds intersects this at the given position, false otherwise
	 */
	boolean intersectsDimensions(Bounds<V> bounds);
	/**
	 * @param currentMinPos The min position that this bounds should be considered in for this computation
	 * @param bounds The bounds that does not move during this collision
	 * @return The collision representing how this bounds will need to be moved to no longer be colliding with the given bounds
	 */
	Collision<V> collideAtDimensions(V currentMinPos, Bounds<V> bounds);
	
	// TODO does this value need to be defined as a part of entity state?
	
	/** @return The surface area of this hitbox as it moves down */
	double getGravityDragReferenceArea();
}