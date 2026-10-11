package zgame.things.type.bounds;

import zgame.physics.V2D;

public interface CircleShape extends Shape2D{
	
	@Override
	default BoundsType getBoundsType(){
		return BoundsType.CIRCLE;
	}
	
	@Override
	default Bounds<V2D> shapeAsBounds(V2D minPos){
		// TODO implement
	}
}
