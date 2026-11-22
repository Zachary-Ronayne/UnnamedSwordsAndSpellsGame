package zgame.things.type.bounds;

import zgame.physics.ZVector;
import zgame.physics.collision.Collision;

/** An object which has a bounds that can be defined in a vector space */
public interface Bounds<V extends ZVector<V>> extends Shape<V>{
	
	/** @return The minimum coordinate of an axis aligned bounding box containing this bounds */
	V getMinPosition();
	
	// TODO make these two methods also have to be defined per bounds, but then each bounds defines them in their own way, i.e. sphere is center based, rect prism is bottom center based
	// TODO make bottom center also a required method for all 3D shapes
	// TODO entity state store the position relative to the shape that it is of, use the diff of its current position and the dims and whatever position it's currently at to find where it should be set
	//		i.e. if setting to bottom center and it stores min position, then subtract current position and bottom center to get an offset to add to the given coordinate
	//		then each entity thing implements the one position method needed for it's bounds type, where that method returns the entity state's position
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
	
	/** @return A shape representing this bounds */
	ShapedBounds<V> asShapedBoundsDimension();
	
	// TODO does this value need to be defined as a part of entity state?
	
	/** @return The surface area of this hitbox as it moves down */
	double getGravityDragReferenceArea();
}