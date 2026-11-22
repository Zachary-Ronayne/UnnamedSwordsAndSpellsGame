package zgame.things.type.bounds.twoDee.bounds.shape;

import zgame.physics.V2D;
import zgame.physics.collision.Collision2D;
import zgame.things.type.bounds.ShapedBounds;
import zgame.things.type.bounds.twoDee.bounds.Bounds2D;

/** Bounds that are represented by a 2D shape */
public sealed interface ShapedBounds2D extends Bounds2D, ShapedBounds<V2D> permits RectangleBounds, CircleBounds, EmptyBounds2D{
	
	@Override
	default boolean intersects(Bounds2D bounds){
		return switch(bounds.asShapedBounds()){
			// No bounds, no intersection
			case EmptyBounds2D ignore -> false;
			// Otherwise, use the shape
			case CircleBounds c -> this.intersectsCircle(c);
			case RectangleBounds r -> this.intersectsRect(r);
		};
	}
	
	/** @return true if this bounds intersects the given rectangle bounds */
	boolean intersectsRect(RectangleBounds bounds);
	
	/** @return true if this bounds intersects the given circle bounds */
	boolean intersectsCircle(CircleBounds bounds);
	
	@Override
	default ShapedBounds2D asShapedBounds(){
		return this;
	}
	
	@Override
	default Collision2D collideAt(V2D currentMinPos, Bounds2D bounds){
		return switch(bounds.asShapedBounds()){
			// No bounds, no intersection
			case EmptyBounds2D ignore -> new Collision2D(this.getMinPosition());
			// Otherwise, use the shape
			case CircleBounds c -> this.collideCircle(c);
			case RectangleBounds r -> this.collideRect(r);
		};
	}
	
	/**
	 * Determine a {@link Collision2D} from colliding this object with the given rectangular bounds. Essentially, move this object so that it no longer intersecting with the given bounds.
	 *
	 * @param bounds The bounds of the rectangle collided with
	 * @return The information about the collision
	 */
	Collision2D collideRect(RectangleBounds bounds);
	
	/**
	 * Determine a {@link Collision2D} from colliding this object with the given circular bounds. Essentially, move this object so that it no longer intersecting with the given bounds.
	 *
	 * @param bounds The bounds of the circle collided with
	 * @return The information about the collision
	 */
	Collision2D collideCircle(CircleBounds bounds);
	
}
