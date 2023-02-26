package zgame.things.type;

import zgame.physics.collision.CollisionResponse;
import zgame.physics.collision.ZCollision;
import zgame.physics.material.Material;

/** An object representing an ellipse that cannot rotate  */
public interface EllipseHitBox extends HitBox, Bounds{
// TODO implement this, currently it's just a copy of the rectangle hitbox
	
	@Override
	default CollisionResponse calculateRectCollision(double x, double y, double w, double h, Material m){
		return ZCollision.rectToRectBasic(x, y, w, h, this.getX(), this.getY(), this.getWidth(), this.getHeight(), m);
	}
	
	@Override
	default CollisionResponse calculateCollision(HitBox h){
		// This assumes the given hitbox is purely a rectangle
		// issue#20 need to eventually have a way of allowing any type of hitbox to collide with any other type of hitbox
		return this.calculateRectCollision(h.getX(), h.getY(), h.getWidth(), h.getHeight(), h.getMaterial());
	}
}
