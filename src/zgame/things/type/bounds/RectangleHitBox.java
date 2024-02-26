package zgame.things.type.bounds;

import zgame.core.utils.ZMath;
import zgame.physics.collision.CollisionResponse;
import zgame.physics.collision.ZCollision;
import zgame.physics.material.Material;

/** An interface which describe a simple hitbox with a width and height, representing a non rotating rectangle */
public interface RectangleHitBox extends HitBox2D{
	
	@Override
	default HitboxType getType(){
		return HitboxType.RECT;
	}
	
	@Override
	default CollisionResponse calculateRectCollision(double x, double y, double w, double h, Material m){
		return ZCollision.rectToRectBasic(x, y, w, h, this.getX(), this.getY(), this.getWidth(), this.getHeight(), m);
	}
	
	@Override
	default CollisionResponse calculateCircleCollision(double x, double y, double r, Material m){
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
