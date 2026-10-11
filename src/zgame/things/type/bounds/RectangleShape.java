package zgame.things.type.bounds;

import zgame.physics.V2D;

public interface RectangleShape extends Shape2D{
	
	@Override
	default BoundsType getBoundsType(){
		return BoundsType.RECT;
	}
	
	@Override
	default Bounds<V2D> shapeAsBounds(V2D minPos){
		// TODO implement
	}
}
