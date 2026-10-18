package zgame.things.type.bounds.twoDee.bounds;

import zgame.physics.V2D;
import zgame.things.type.bounds.Bounds;
import zgame.things.type.bounds.twoDee.shape.EmptyShape2D;
import zgame.things.type.bounds.twoDee.shape.ShapeType2D;

/** An object that has no dimension */
public interface NoBounds2D extends Bounds2D, EmptyShape2D{
	
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
	default boolean intersectsRect(RectangleBounds bounds){
		// No bounds, nothing intersects
		return false;
	}
	
	@Override
	default boolean intersectsCircle(CircleBounds bounds){
		// No bounds, nothing intersects
		return false;
	}
	
}
