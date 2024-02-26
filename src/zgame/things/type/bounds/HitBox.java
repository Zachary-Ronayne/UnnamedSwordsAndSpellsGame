package zgame.things.type.bounds;

import zgame.core.utils.Uuidable;
import zgame.physics.collision.CollisionResponse;
import zgame.physics.material.Material;
import zgame.things.entity.projectile.Projectile;
import zgame.things.type.Materialable;

/** An interface which defines an object that has a hit box, meaning something with a position that can collide and move against other bounds */
public interface HitBox extends Materialable, Uuidable{
	
	/** @return The type of this hitbox, for determining how it will collide with other hitboxes */
	HitboxType getType();
	
	/**
	 * @param h The hitbox to check
	 * @return true if this hitbox intersects the given hitbox, false otherwise
	 */
	boolean intersects(HitBox h);
	
	/**
	 * Apply the given {@link CollisionResponse} to this object
	 *
	 * @param r The {@link CollisionResponse} to use
	 */
	void collide(CollisionResponse r);
	
	/**
	 * Called when this {@link HitBox} is hit by a projectile. Does nothing by default, implement to provide custom behavior
	 *
	 * @param p The projectile which hit this {@link HitBox}
	 */
	default void hitBy(Projectile p){}
	
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
	
}
