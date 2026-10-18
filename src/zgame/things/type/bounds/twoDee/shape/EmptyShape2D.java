package zgame.things.type.bounds.twoDee.shape;

import zgame.physics.V2D;
import zgame.things.type.bounds.Bounds;
import zgame.things.type.bounds.twoDee.bounds.NoBounds2D;

/** Represents a shape taking up no area */
public interface EmptyShape2D extends Shape2D{
	
	@Override
	default V2D getDimensions(){
		return new V2D();
	}
	
	@Override
	default Bounds<V2D> shapeAsBounds(V2D minPos){
		return (NoBounds2D)() -> minPos;
	}
	
	@Override
	default ShapeType2D getShapeType(){
		return ShapeType2D.EMPTY;
	}
}
