package zgame.things.type.bounds.twoDee.shape;

import zgame.physics.V2D;
import zgame.things.type.bounds.twoDee.bounds.shape.EmptyBounds2D;
import zgame.things.type.bounds.twoDee.bounds.shape.ShapedBounds2D;

/** Represents a shape taking up no area */
public interface EmptyShape2D extends Shape2D{
	
	@Override
	default V2D getDimensions(){
		return V2D.EMPTY;
	}
	
	@Override
	default ShapedBounds2D atMinPosition(V2D minPos){
		return (EmptyBounds2D)() -> minPos;
	}
}
