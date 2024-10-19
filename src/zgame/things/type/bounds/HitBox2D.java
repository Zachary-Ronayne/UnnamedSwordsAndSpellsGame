package zgame.things.type.bounds;

import zgame.physics.collision.CollisionResult2D;
import zgame.physics.material.Material;
import zgame.things.entity.projectile.Projectile;

/**
 * An interface which defines an object that has a hit box, meaning something with a position that can collide and move against other bounds
 */
public interface HitBox2D extends HitBox<HitBox2D, CollisionResult2D>, Bounds2D{
	
	/** @param x The new x coordinate for this object */
	void setX(double x);
	
	/** @param y The new y coordinate for this object */
	void setY(double y);
	
	@Override
	default double maxX(){
		return this.getX() + this.getWidth();
	}
	
	@Override
	default double maxY(){
		return this.getY() + this.getHeight();
	}
	
	/**
	 * Determine a {@link CollisionResult2D} from colliding this object with the given rectangular bounds. Essentially, move this object so that it no longer intersecting with
	 * the given bounds. This method should not change the state of this object, it should only return an object representing how the collision should happen.
	 *
	 * @param x The x coordinate of the upper left hand corner of the bounds
	 * @param y The y coordinate of the upper left hand corner of the bounds
	 * @param w The width of the bounds
	 * @param h The height of the bounds
	 * @param m The material which was collided with
	 * @return The information about the collision
	 */
	CollisionResult2D calculateRectCollision(double x, double y, double w, double h, Material m);
	
	/**
	 * Determine a {@link CollisionResult2D} from colliding this object with the given circular bounds. Essentially, move this object so that it no longer intersecting with
	 * the given bounds. This method should not change the state of this object, it should only return an object representing how the collision should happen.
	 *
	 * @param x The x coordinate of the upper left hand corner of the bounds
	 * @param y The y coordinate of the upper left hand corner of the bounds
	 * @param r The radius of the circle
	 * @param m The material which was collided with
	 * @return The information about the collision
	 */
	CollisionResult2D calculateCircleCollision(double x, double y, double r, Material m);
	
	/**
	 * @param x The upper left hand x rectangle
	 * @param y The upper left hand y rectangle
	 * @param w The width of the rectangle
	 * @param h The height of the rectangle
	 * @return true if this object intersects the given rectangular bounds, false otherwise
	 */
	boolean intersectsRect(double x, double y, double w, double h);
	
	/**
	 * @param x The center x coordinate of the circle
	 * @param y The center y coordinate of the circle
	 * @param r The radius of the circle
	 * @return true if this object intersects the given circle bounds, false otherwise
	 */
	boolean intersectsCircle(double x, double y, double r);
	
	/**
	 * @param hitbox The hitbox to check, must be 2D
	 * @return true if this hitbox intersects the given hitbox, false otherwise
	 */
	@Override
	default boolean intersects(HitBox2D hitbox){
		var h = hitbox.get();
		switch(h.getHitboxType()){
			case CIRCLE -> {
				return this.intersectsCircle(h.centerX(), h.centerY(), h.getWidth() * 0.5);
			}
			case RECT -> {
				return this.intersectsRect(h.getX(), h.getY(), h.getWidth(), h.getHeight());
			}
		}
		return false;
	}
	
	/**
	 * Called when this {@link HitBox2D} is hit by a projectile. Does nothing by default, implement to provide custom behavior
	 *
	 * @param p The projectile which hit this {@link HitBox2D}
	 */
	default void hitBy(Projectile p){}
	
	/**
	 * Reposition this object so that it is to the left of the given x coordinate.
	 * If the object is already to the left of the coordinate, this method should do nothing.
	 * If the object will be moved, it should be positioned such that it is as close to its original position as possible, while still being to the left of the given
	 * coordinate
	 *
	 * @param x The coordinate
	 * @return true if the object was moved, false otherwise
	 */
	default boolean keepLeft(double x){
		if(this.getX() + this.getWidth() <= x) return false;
		this.setX(x - this.getWidth());
		return true;
	}
	
	/**
	 * Reposition this object so that it is to the right of the given x coordinate.
	 * If the object is already to the right of the coordinate, this method should do nothing.
	 * If the object will be moved, it should be positioned such that it is as close to its original position as possible, while still being to the right of the given
	 * coordinate
	 *
	 * @param x The coordinate
	 * @return true if the object was moved, false otherwise
	 */
	default boolean keepRight(double x){
		if(this.getX() >= x) return false;
		this.setX(x);
		return true;
	}
	
	/**
	 * Reposition this object so that it is above the given y coordinate.
	 * If the object is already above the coordinate, this method should do nothing.
	 * If the object will be moved, it should be positioned such that it is as close to its original position as possible, while still being above the given coordinate
	 *
	 * @param y The coordinate
	 * @return true if the object was moved, false otherwise
	 */
	default boolean keepAbove(double y){
		if(this.getY() + this.getHeight() <= y) return false;
		this.setY(y - this.getHeight());
		return true;
	}
	
	/**
	 * Reposition this object so that it is below the given y coordinate.
	 * If the object is already below the coordinate, this method should do nothing.
	 * If the object will be moved, it should be positioned such that it is as close to its original position as possible, while still being below the given coordinate
	 *
	 * @param y The coordinate
	 * @return true if the object was moved, false otherwise
	 */
	default boolean keepBelow(double y){
		if(this.getY() >= y) return false;
		this.setY(y);
		return true;
	}
	
	/** @return The previous value of {@link #getX()} before the last time it was moved with velocity */
	double getPX();
	
	/** @return The previous value of {@link #getY()} before the last time it was moved with velocity */
	double getPY();
	
	@Override
	default HitBox2D get(){
		return this;
	}
	
}
