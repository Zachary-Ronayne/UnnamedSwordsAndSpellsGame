package zgame.things.type.bounds.twoDee.shape;

import zgame.physics.V2D;
import zgame.things.type.bounds.Shape;
import zgame.things.type.bounds.ShapedBounds;
import zgame.things.type.bounds.twoDee.bounds.shape.ShapedBounds2D;

/** A shape that exists in two dimensions */
public interface Shape2D extends Shape<V2D>{
	
	@Override
	default ShapedBounds<V2D> atMinPositionDimension(V2D minPos){
		return this.atMinPosition(minPos);
	}
	
	/**
	 * Create a 2D bounds from this shape at the given position
	 * @param minPos The minimum position for the bounds
	 * @return The bounds
	 */
	ShapedBounds2D atMinPosition(V2D minPos);
}
