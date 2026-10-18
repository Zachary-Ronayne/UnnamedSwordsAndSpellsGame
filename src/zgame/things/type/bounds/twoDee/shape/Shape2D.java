package zgame.things.type.bounds.twoDee.shape;

import zgame.physics.V2D;
import zgame.things.type.bounds.Shape;

// TODO add docs
public interface Shape2D extends Shape<V2D>{
	/** @return The type of shape that this bounds represents */
	ShapeType2D getShapeType();
}
