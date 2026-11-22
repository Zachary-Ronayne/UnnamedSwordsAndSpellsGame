package zgame.things.type.bounds.threeDee.shape;

import zgame.physics.V3D;
import zgame.things.type.bounds.Shape;
import zgame.things.type.bounds.ShapedBounds;
import zgame.things.type.bounds.threeDee.bounds.shape.ShapedBounds3D;

/** A shape that exists in three dimensions */
public interface Shape3D extends Shape<V3D>{
	
	@Override
	default ShapedBounds<V3D> atMinPositionDimension(V3D minPos){
		return this.atMinPosition(minPos);
	}
	
	/**
	 * Create a 3D bounds from this shape at the given position
	 * @param minPos The minimum position for the bounds
	 * @return The bounds
	 */
	ShapedBounds3D atMinPosition(V3D minPos);
}
