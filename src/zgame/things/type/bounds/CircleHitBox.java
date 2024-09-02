package zgame.things.type.bounds;

import zgame.core.utils.ZMath;
import zgame.core.utils.ZPoint2D;
import zgame.physics.collision.CollisionResult;
import zgame.physics.collision.ZCollision;
import zgame.physics.material.Material;

/** An object representing a circle  */
public interface CircleHitBox extends HitBox2D{
	
	@Override
	default HitboxType getHitboxType(){
		return HitboxType.CIRCLE;
	}
	
	/** @return The radius of this circle hitbox */
	double getRadius();
	
	@Override
	default double getWidth(){
		return this.getRadius() * 2;
	}
	@Override
	default double getHeight(){
		return this.getRadius() * 2;
	}
	
	@Override
	default double centerX(){
		return this.getX() + this.getRadius();
	}
	
	@Override
	default double centerY(){
		return this.getY() + this.getRadius();
	}
	
	@Override
	default CollisionResult calculateRectCollision(double x, double y, double w, double h, Material m){
		return ZCollision.rectToCircleBasic(x, y, w, h, this.centerX(), this.centerY(), this.getRadius(), m);
	}
	
	@Override
	default CollisionResult calculateCircleCollision(double x, double y, double r, Material m){
		return ZCollision.circleToCircleBasic(x, y, r, this.centerX(), this.centerY(), this.getRadius(), m);
	}
	
	@Override
	default boolean intersectsRect(double x, double y, double w, double h) {
		return ZMath.circleIntersectsRect(this.centerX(), this.centerY(), this.getRadius(), x, y, w, h);
	}
	
	@Override
	default boolean intersectsCircle(double x, double y, double r){
		var dist = new ZPoint2D(x, y).distance(this.getX(), this.getY());
		return dist <= r + this.getRadius();
	}
}
