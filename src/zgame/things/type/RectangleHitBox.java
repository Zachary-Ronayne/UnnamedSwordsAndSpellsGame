package zgame.things.type;

import zgame.physics.collision.CollisionResponse;
import zgame.physics.collision.ZCollision;
import zgame.physics.material.Material;

/** An interface which describe a simple hitbox with a width and height, representing a non rotating rectangle */
public interface RectangleHitBox extends HitBox, Bounds{
	
	/** @param x The new x coordinate for this object */
	void setX(double x);
	
	/** @param y The new y coordinate for this object */
	void setY(double y);
	
	@Override
	default boolean keepLeft(double x){
		if(this.getX() + this.getWidth() <= x) return false;
		this.setX(x - this.getWidth());
		return true;
	}
	
	@Override
	default boolean keepRight(double x){
		if(this.getX() >= x) return false;
		this.setX(x);
		return true;
	}
	
	@Override
	default boolean keepAbove(double y){
		if(this.getY() + this.getHeight() <= y) return false;
		this.setY(y - this.getHeight());
		return true;
	}
	
	@Override
	default boolean keepBelow(double y){
		if(this.getY() >= y) return false;
		this.setY(y);
		return true;
	}
	
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
	
	@Override
	default double maxX(){
		return this.getX() + this.getWidth();
	}
	
	@Override
	default double maxY(){
		return this.getY() + this.getHeight();
	}
}
