package zgame.things.type.bounds;

import zgame.physics.collision.CollisionResult3D;
import zgame.physics.material.Material;
import zgame.things.entity.projectile.Projectile;

/** An interface which defines an object that has a hit box, meaning something with a position that can collide and move against other bounds */
public interface HitBox3D extends HitBox<HitBox3D, CollisionResult3D>, Bounds3D {
	
	/** @param x The new x coordinate for this object */
	void setX(double x);
	
	/** @param y The new y coordinate for this object */
	void setY(double y);
	
	/** @param z The new z coordinate for this object */
	void setZ(double z);
	
	/**
	 * Called when this {@link HitBox3D} is hit by a projectile. Does nothing by default, implement to provide custom behavior
	 *
	 * @param p The projectile which hit this {@link HitBox3D}
	 */
	default void hitBy(Projectile p){}
	
	// TODO with the touch methods, make them pass a hitbox which was touched as a parameter
	/**
	 * A method that defines what this object does when it touches a floor
	 *
	 * @param touched The Material which this {@link HitBox3D} touched
	 */
	void touchFloor(Material touched);
	
	/** A method that defines what this object does when it leaves the floor, i.e. it goes from touching the floor to not touching the floor */
	void leaveFloor();
	
	/**
	 * A method that defines what this object does when it touches a ceiling
	 *
	 * @param touched The Material which this {@link HitBox3D} touched
	 */
	void touchCeiling(Material touched);
	
	/** A method that defines what this object does when it leaves a ceiling, i.e. it goes from touching a wall to not touching a ceiling */
	void leaveCeiling();
	
	/**
	 * A method that defines what this object does when it touches a wall
	 *
	 * @param touched The Material which this {@link HitBox3D} touched
	 */
	void touchWall(Material touched);
	
	/** A method that defines what this object does when it leaves a wall, i.e. it goes from touching a wall to not touching a wall */
	void leaveWall();
	
	/** @return true if this {@link HitBox3D} is on the ground, false otherwise i.e. it's in the air */
	boolean isOnGround();
	
	/** @return true if this {@link HitBox3D} is touching a ceiling, false otherwise */
	boolean isOnCeiling();
	
	/** @return true if this {@link HitBox3D} is touching a wall, false otherwise */
	boolean isOnWall();
	
	/** @return The previous value of {@link #getX()} before the last time it was moved with velocity */
	double getPX();
	
	/** @return The previous value of {@link #getY()} before the last time it was moved with velocity */
	double getPY();
	
	/** @return The previous value of {@link #getZ()} before the last time it was moved with velocity */
	double getPZ();
	
	@Override
	default HitBox3D get(){
		return this;
	}
}
