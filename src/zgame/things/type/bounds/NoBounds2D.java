package zgame.things.type.bounds;

import zgame.physics.V2D;

/** An object that has no dimension */
public interface NoBounds2D extends Bounds2D{
	@Override
	default BoundsType getBoundsType(){
		return BoundsType.NONE;
	}
	
	@Override
	default V2D getDimensions(){
		return new V2D();
	}
	
	@Override
	default V2D getMaxPosition(){
		return this.getMinPosition();
	}
	
	@Override
	default V2D getCenterPosition(){
		return this.getMinPosition();
	}
	
	@Override
	default boolean intersects(Bounds<V2D> bounds){
		// No bounds, nothing intersects
		return false;
	}
	
	@Override
	default Bounds<V2D> shapeAsBounds(V2D minPos){
		// TODO implement
	}
}
