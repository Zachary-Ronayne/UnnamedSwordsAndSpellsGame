package zgame.things.type;

import zgame.physics.collision.CollisionResponse;
import zgame.physics.collision.ZCollision;
import zgame.physics.material.Material;

import java.awt.geom.Ellipse2D;

/** An object representing an ellipse that cannot rotate  */
public interface EllipseHitBox extends HitBox, Bounds{
	
	@Override
	default HitboxType getType(){
		return HitboxType.ELLIPSE;
	}
	
	@Override
	default CollisionResponse calculateRectCollision(double x, double y, double w, double h, Material m){
		// TODO implement
		return ZCollision.rectToRectBasic(x, y, w, h, this.getX(), this.getY(), this.getWidth(), this.getHeight(), m);
	}
	
	@Override
	default CollisionResponse calculateEllipseCollision(double x, double y, double w, double h, Material m){
		// TODO implement
		return new CollisionResponse();
	}
	
	@Override
	default boolean intersectsRect(double x, double y, double w, double h){
		return new Ellipse2D.Double(this.getX(), this.getY(), this.getWidth(), this.getHeight()).intersects(x, y, w, h);
	}
	
	@Override
	default boolean intersectsEllipse(double x, double y, double w, double h){
		// TODO implement
		return false;
	}
}
