package zgame.things.type.bounds;

import zgame.core.utils.ZMath;
import zgame.physics.collision.CollisionResult;
import zgame.physics.collision.ZCollision;
import zgame.physics.material.Material;

/** An interface which describe a simple hitbox with a width and height, representing a non rotating rectangle */
public interface RectangleHitBox extends HitBox2D{
	
	@Override
	default HitboxType getHitboxType(){
		return HitboxType.RECT;
	}
	
	@Override
	default CollisionResult calculateRectCollision(double x, double y, double w, double h, Material m){
		return ZCollision.rectToRectBasic(x, y, w, h, this.getX(), this.getY(), this.getWidth(), this.getHeight(), m);
	}
	
	@Override
	default CollisionResult calculateCircleCollision(double x, double y, double r, Material m){
		return ZCollision.rectToCircleBasic(this.getX(), this.getY(), this.getWidth(), this.getHeight(), x, y, r, m).scale(-1);
	}
	
	@Override
	default boolean intersectsRect(double x, double y, double w, double h){
		return this.getBounds().intersects(x, y, w, h);
	}
	
	@Override
	default boolean intersectsCircle(double x, double y, double r){
		return ZMath.circleIntersectsRect(x, y, r, this.getX(), this.getY(), this.getWidth(), this.getHeight());
	}
}
