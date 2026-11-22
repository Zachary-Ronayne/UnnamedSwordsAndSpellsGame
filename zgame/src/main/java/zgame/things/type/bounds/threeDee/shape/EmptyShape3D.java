package zgame.things.type.bounds.threeDee.shape;

import zgame.physics.V3D;
import zgame.things.type.bounds.threeDee.bounds.shape.EmptyBounds3D;
import zgame.things.type.bounds.threeDee.bounds.shape.ShapedBounds3D;

/** Represents a 3D shape taking up no area */
public interface EmptyShape3D extends Shape3D{
	
	@Override
	default V3D getDimensions(){
		return V3D.EMPTY;
	}
	
	@Override
	default ShapedBounds3D atMinPosition(V3D minPos){
		return (EmptyBounds3D)() -> minPos;
	}
}
