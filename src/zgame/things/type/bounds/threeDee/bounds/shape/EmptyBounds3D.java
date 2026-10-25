package zgame.things.type.bounds.threeDee.bounds.shape;

import zgame.physics.V3D;
import zgame.physics.collision.Collision;
import zgame.physics.collision.Collision3D;
import zgame.things.type.bounds.Bounds;
import zgame.things.type.bounds.ShapedBounds;
import zgame.things.type.bounds.threeDee.bounds.Bounds3D;
import zgame.things.type.bounds.threeDee.shape.EmptyShape3D;

/** A 3D object that has no dimension */
public non-sealed interface EmptyBounds3D extends ShapedBounds3D, EmptyShape3D{
	
	// No dimension, all positions are the same
	@Override
	default V3D getMaxPosition(){
		return this.getMinPosition();
	}
	
	@Override
	default V3D getCenterPosition(){
		return this.getMinPosition();
	}
	
	// Mo bounds, no intersection with anything
	@Override
	default boolean intersectsRect(RectPrismBounds bounds){
		return false;
	}
	
	@Override
	default boolean intersectsCylinder(CylinderBounds bounds){
		return false;
	}
	
	@Override
	default boolean intersectsSphere(SphereBounds bounds){
		return false;
	}
	
	@Override
	default boolean intersectsDimensions(Bounds<V3D> bounds){
		return false;
	}
	
	@Override
	default boolean intersects(Bounds3D bounds){
		return false;
	}
	
	@Override
	default double getGravityDragReferenceArea(){
		return this.getWidth() * this.getLength();
	}
	
	// No bounds, no collisions
	@Override
	default Collision<V3D> collideAtDimensions(V3D currentMinPos, Bounds<V3D> bounds){
		return new Collision3D(currentMinPos);
	}
	
	@Override
	default Collision3D collideAt(V3D currentMinPos, Bounds3D bounds){
		return new Collision3D(currentMinPos);
	}
	
	@Override
	default Collision3D collideCylinder(CylinderBounds bounds){
		return new Collision3D(bounds.getMinPosition());
	}
	
	@Override
	default Collision3D collideRect(RectPrismBounds bounds){
		return new Collision3D(bounds.getMinPosition());
	}
	
	@Override
	default Collision3D collideSphere(SphereBounds bounds){
		return new Collision3D(bounds.getMinPosition());
	}
	
	@Override
	default ShapedBounds<V3D> atMinPositionDimension(V3D minPos){
		return (EmptyBounds3D)() -> minPos;
	}
}
