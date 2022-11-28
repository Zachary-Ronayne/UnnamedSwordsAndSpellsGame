package zgame.things.type;

import zgame.physics.collision.CollisionResponse;
import zgame.physics.collision.ZCollision;
import zgame.physics.material.Material;

/** An interface which describe a simple hitbox with a width and height, representing a non rotating rectangle */
public interface RectangleHitBox extends HitBox, RectangleBounds{
	
	/** @param x The new x coordinate for this object */
	public void setX(double x);
	
	/** @param y The new y coordinate for this object */
	public void setY(double y);
	
	@Override
	public default boolean keepLeft(double x){
		if(this.getX() + this.getWidth() <= x) return false;
		this.setX(x - this.getWidth());
		return true;
	}
	
	@Override
	public default boolean keepRight(double x){
		if(this.getX() >= x) return false;
		this.setX(x);
		return true;
	}
	
	@Override
	public default boolean keepAbove(double y){
		if(this.getY() + this.getHeight() <= y) return false;
		this.setY(y - this.getHeight());
		return true;
	}
	
	@Override
	public default boolean keepBelow(double y){
		if(this.getY() >= y) return false;
		this.setY(y);
		return true;
	}
	
	@Override
	public default CollisionResponse calculateRectCollision(double x, double y, double w, double h, Material m){
		return ZCollision.rectToRectBasic(x, y, w, h, this.getX(), this.getY(), this.getWidth(), this.getHeight(), m);
	}
	
	@Override
	public default boolean intersects(double x, double y, double w, double h){
		return this.getBounds().intersects(x, y, w, h);
	}
	
	@Override
	public default double maxX(){
		return this.getX() + this.getWidth();
	}
	
	@Override
	public default double maxY(){
		return this.getY() + this.getHeight();
	}
}
