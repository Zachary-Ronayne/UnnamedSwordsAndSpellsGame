package zgame.things.type;

import zgame.core.utils.Uuidable;
import zgame.core.utils.ZRect;
import zgame.physics.collision.CollisionResponse;
import zgame.physics.material.Material;
import zgame.things.entity.projectile.Projectile;

/** An interface which defines an object that has a hit box, meaning something with a position that can collide and move against other bounds */
public interface HitBox extends Bounds, Materialable, Uuidable{
	
	/**
	 * Apply the given {@link CollisionResponse} to this object
	 *
	 * @param r The {@link CollisionResponse} to use
	 */
	void collide(CollisionResponse r);
	
	/**
	 * Determine a {@link CollisionResponse} from colliding this object with the given rectangular bounds. Essentially, move this object so that it no longer intersecting with the
	 * given bounds. This method should not change the state of this object, it should only return an object representing how the collision should happen.
	 *
	 * @param x The x coordinate of the upper left hand corner of the bounds
	 * @param y The y coordinate of the upper left hand corner of the bounds
	 * @param w The width of the bounds
	 * @param h The height of the bounds
	 * @param m The material which was collided with
	 * @return The information about the collision
	 */
	CollisionResponse calculateRectCollision(double x, double y, double w, double h, Material m);
	
	/**
	 * Calculate a {@link CollisionResponse} from colliding with the given {@link HitBox}
	 *
	 * @param h The hitbox to collide with
	 * @return The response
	 */
	CollisionResponse calculateCollision(HitBox h);
	
	// TODO implement this as any shape being able to intersect any other shape, not just rectangles
	/**
	 * @param x The upper left hand x coordinate
	 * @param y The upper left hand y coordinate
	 * @param w The width of the bounds
	 * @param h The height of the bounds
	 * @return true if this object intersects the given rectangular bounds, false otherwise
	 */
	default boolean intersects(double x, double y, double w, double h){
		return this.getBounds().intersects(x, y, w, h);
	}
	
	/**
	 * @param r The bounds
	 * @return true if this object intersects the given rectangular bounds, false otherwise
	 */
	default boolean intersects(ZRect r){
		return this.intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}
	
	/**
	 * true if the given {@link HitBox} intersects this {@link HitBox}
	 *
	 * @param h The hitbox to check
	 * @return true if this object intersects the given {@link HitBox}, false otherwise
	 */
	default boolean intersects(HitBox h){
		// This assumes the given hitbox is purely a rectangle
		// issue#20 need to eventually have a way of allowing any type of hitbox to collide with any other type of hitbox
		return this.intersects(h.getBounds());
	}
	
	/**
	 * Called when this {@link HitBox} is hit by a projectile. Does nothing by default, implement to provide custom behavior
	 * @param p The projectile which hit this {@link HitBox}
	 */
	default void hitBy(Projectile p){}
	
	/**
	 * Reposition this object so that it is to the left of the given x coordinate.
	 * If the object is already to the left of the coordinate, this method should do nothing.
	 * If the object will be moved, it should be positioned such that it is as close to its original position as possible, while still being to the left of the given coordinate
	 *
	 * @param x The coordinate
	 * @return true if the object was moved, false otherwise
	 */
	boolean keepLeft(double x);
	
	/**
	 * Reposition this object so that it is to the right of the given x coordinate.
	 * If the object is already to the right of the coordinate, this method should do nothing.
	 * If the object will be moved, it should be positioned such that it is as close to its original position as possible, while still being to the right of the given coordinate
	 *
	 * @param x The coordinate
	 * @return true if the object was moved, false otherwise
	 */
	boolean keepRight(double x);
	
	/**
	 * Reposition this object so that it is above the given y coordinate.
	 * If the object is already above the coordinate, this method should do nothing.
	 * If the object will be moved, it should be positioned such that it is as close to its original position as possible, while still being above the given coordinate
	 *
	 * @param y The coordinate
	 * @return true if the object was moved, false otherwise
	 */
	boolean keepAbove(double y);
	
	/**
	 * Reposition this object so that it is below the given y coordinate.
	 * If the object is already below the coordinate, this method should do nothing.
	 * If the object will be moved, it should be positioned such that it is as close to its original position as possible, while still being below the given coordinate
	 *
	 * @param y The coordinate
	 * @return true if the object was moved, false otherwise
	 */
	boolean keepBelow(double y);
	
	/**
	 * A method that defines what this object does when it touches a floor
	 *
	 * @param touched The Material which this {@link HitBox} touched
	 */
	void touchFloor(Material touched);
	
	/** A method that defines what this object does when it leaves the floor, i.e. it goes from touching the floor to not touching the floor */
	void leaveFloor();
	
	/**
	 * A method that defines what this object does when it touches a ceiling
	 *
	 * @param touched The Material which this {@link HitBox} touched
	 */
	void touchCeiling(Material touched);
	
	/** A method that defines what this object does when it leaves a ceiling, i.e. it goes from touching a wall to not touching a ceiling */
	void leaveCeiling();
	
	/**
	 * A method that defines what this object does when it touches a wall
	 *
	 * @param touched The Material which this {@link HitBox} touched
	 */
	void touchWall(Material touched);
	
	/** A method that defines what this object does when it leaves a wall, i.e. it goes from touching a wall to not touching a wall */
	void leaveWall();
	
	/** @return true if this {@link HitBox} is on the ground, false otherwise i.e. it's in the air */
	boolean isOnGround();
	
	/** @return true if this {@link HitBox} is touching a ceiling, false otherwise */
	boolean isOnCeiling();
	
	/** @return true if this {@link HitBox} is touching a wall, false otherwise */
	boolean isOnWall();
	
	/** @return The previous value of {@link #getX()} before the last time it was moved with velocity */
	double getPX();
	
	/** @return The previous value of {@link #getY()} before the last time it was moved with velocity */
	double getPY();
	
}
