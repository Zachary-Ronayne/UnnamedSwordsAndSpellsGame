package zgame.things.type.bounds;

import zgame.physics.ZVector;

/** Represents some arbitrary shape */
public interface Shape<V extends ZVector<V>>{
	
	/** @return The total size of this shape across all axes on an axis aligned bounding box */
	V getDimensions();
	
	/**
	 * Create a bounds from this shape at the given position
	 * @param minPos The minimum position for the bounds
	 * @return The bounds
	 */
	ShapedBounds<V> atMinPositionDimension(V minPos);
	
}
