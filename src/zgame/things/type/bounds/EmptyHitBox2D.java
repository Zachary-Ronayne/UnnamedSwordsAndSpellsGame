package zgame.things.type.bounds;

import zgame.physics.V2D;
import zgame.physics.collision.Collision;
import zgame.physics.collision.Collision2D;

/** A hitbox that does not collide with anything */
public interface EmptyHitBox2D extends HitBox2D{
	
	@Override
	default V2D getMaxPosition(){
		var pos = this.getPosition();
		var dims = this.getDimensions();
		return new V2D(pos.getX() + dims.getWidth(), pos.getY() + dims.getHeight());
	}
	
	@Override
	default HitboxType getHitboxType(){
		return HitboxType.NONE;
	}
	
	// TODO see if a lot of these methods can be cleaned up
	
	@Override
	default Collision<V2D> collideAt(V2D currentPos, Bounds<V2D> bounds){
		return new Collision2D(this.getPosition());
	}
	
	@Override
	default Collision<V2D> collideBounds(Bounds<V2D> bounds){
		return new Collision2D(this.getPosition());
	}
	
	@Override
	default Collision2D calculateRectCollision(double x, double y, double w, double h){
		return new Collision2D(this.getPosition());
	}
	
	@Override
	default Collision2D calculateCircleCollision(double x, double y, double r){
		return new Collision2D(this.getPosition());
	}
	
	@Override
	default boolean intersects(Bounds<V2D> hitbox){
		return false;
	}
	
	@Override
	default boolean intersectsRect(double x, double y, double w, double h){
		return false;
	}
	
	@Override
	default boolean intersectsCircle(double x, double y, double r){
		return false;
	}
	
	@Override
	default double getGravityDragReferenceArea(){
		return this.getWidth();
	}
}
