package zgame.things.type.bounds.twoDee.bounds.shape;

import zgame.physics.V2D;
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
			case CircleBounds c -> intersectsCircle(c);
			case RectangleBounds r -> intersectsRect(r);
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
}
