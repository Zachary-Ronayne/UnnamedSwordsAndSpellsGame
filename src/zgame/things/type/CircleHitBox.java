package zgame.things.type;

import zgame.core.utils.ZMath;
import zgame.core.utils.ZPoint;
import zgame.core.utils.ZStringUtils;
import zgame.physics.collision.CollisionResponse;
import zgame.physics.collision.ZCollision;
import zgame.physics.material.Material;

/** An object representing a circle  */
public interface CircleHitBox extends HitBox{
	
	@Override
	default HitboxType getType(){
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
	default CollisionResponse calculateRectCollision(double x, double y, double w, double h, Material m){
		var c = ZCollision.rectToCircleBasic(x, y, w, h, this.centerX(), this.centerY(), this.getRadius(), m);
		ZStringUtils.prints(c.left(), c.right(), c.ceiling(), c.floor()); // TODO Remove
		return c;
	}
	
	@Override
	default CollisionResponse calculateCircleCollision(double x, double y, double r, Material m){
		return ZCollision.circleToCircleBasic(x, y, r, this.centerX(), this.centerY(), this.getRadius(), m);
	}
	
	@Override
	default boolean intersectsRect(double x, double y, double w, double h) {
		return ZMath.circleIntersectsRect(this.centerX(), this.centerY(), this.getRadius(), x, y, w, h);
	}
	
	@Override
	default boolean intersectsCircle(double x, double y, double r){
		var dist = new ZPoint(x, y).distance(this.getX(), this.getY());
		return dist <= r + this.getRadius();
	}
}
