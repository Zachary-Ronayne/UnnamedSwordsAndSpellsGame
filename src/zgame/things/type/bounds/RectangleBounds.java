package zgame.things.type.bounds;

import zgame.physics.V2D;

public interface RectangleBounds extends Bounds2D{
	@Override
	default BoundsType getBoundsType(){
		return BoundsType.RECT;
	}
	
	@Override
	default boolean intersects(Bounds<V2D> bounds){
		// TODO implement
	}
	
	@Override
	default Bounds<V2D> shapeAsBounds(V2D minPos){
		// TODO implement
	}
}
