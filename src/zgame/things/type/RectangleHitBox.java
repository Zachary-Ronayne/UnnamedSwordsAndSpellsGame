package zgame.things.type;

import zgame.physics.collision.CollisionResponse;
import zgame.physics.collision.ZCollision;
import zgame.physics.material.Material;

import java.awt.geom.Ellipse2D;

/** An interface which describe a simple hitbox with a width and height, representing a non rotating rectangle */
public interface RectangleHitBox extends HitBox, Bounds{
	
	@Override
	default HitboxType getType(){
		return HitboxType.RECT;
	}
	
	@Override
	default CollisionResponse calculateRectCollision(double x, double y, double w, double h, Material m){
		return ZCollision.rectToRectBasic(x, y, w, h, this.getX(), this.getY(), this.getWidth(), this.getHeight(), m);
	}
	
	@Override
	default CollisionResponse calculateEllipseCollision(double x, double y, double w, double h, Material m){
		return new CollisionResponse();
	}
	
	@Override
	default boolean intersectsRect(double x, double y, double w, double h){
		return this.getBounds().intersects(this.getBounds());
	}
	
	@Override
	default boolean intersectsEllipse(double x, double y, double w, double h){
		return new Ellipse2D.Double(x, y, w, h).intersects(this.getBounds());
	}
}
