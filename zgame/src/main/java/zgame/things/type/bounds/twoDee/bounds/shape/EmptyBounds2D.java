package zgame.things.type.bounds.twoDee.bounds.shape;

import zgame.physics.V2D;
import zgame.physics.collision.Collision;
import zgame.physics.collision.Collision2D;
import zgame.things.type.bounds.Bounds;
import zgame.things.type.bounds.ShapedBounds;
import zgame.things.type.bounds.twoDee.bounds.Bounds2D;
import zgame.things.type.bounds.twoDee.shape.EmptyShape2D;

/** A 2D object that has no dimension */
public non-sealed interface EmptyBounds2D extends ShapedBounds2D, EmptyShape2D{
	
	// No dimension, all positions are the same
	@Override
	default V2D getMaxPosition(){
		return this.getMinPosition();
	}
	
	@Override
	default V2D getCenterPosition(){
		return this.getMinPosition();
	}
	
	// No bounds, no intersection with anything
	@Override
	default boolean intersectsRect(RectangleBounds bounds){
		return false;
	}
	@Override
	default boolean intersectsCircle(CircleBounds bounds){
		return false;
	}
	@Override
	default boolean intersectsDimensions(Bounds<V2D> bounds){
		return false;
	}
	@Override
	default boolean intersects(Bounds2D bounds){
		return false;
	}
	
	// No bounds, no collisions
	@Override
	default Collision<V2D> collideAtDimensions(V2D currentMinPos, Bounds<V2D> bounds){
		return new Collision2D(currentMinPos);
	}
	@Override
	default Collision2D collideAt(V2D currentMinPos, Bounds2D bounds){
		return new Collision2D(currentMinPos);
	}
	@Override
	default Collision2D collideCircle(CircleBounds bounds){
		return new Collision2D(bounds.getMinPosition());
	}
	@Override
	default Collision2D collideRect(RectangleBounds bounds){
		return new Collision2D(bounds.getMinPosition());
	}
	@Override
	default double getGravityDragReferenceArea(){
		return this.getWidth();
	}
	
	@Override
	default ShapedBounds<V2D> atMinPositionDimension(V2D minPos){
		return (EmptyBounds2D)() -> minPos;
	}
}
