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
		return this.getPosition();
	}
	
	@Override
	default V2D getCenterPosition(){
		return this.getPosition();
	}
	
	@Override
	default boolean intersects(Bounds<V2D> bounds){
		// No bounds means nothing intersects
		return false;
	}
}
