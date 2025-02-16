package zgame.things.type.bounds;

import zgame.core.utils.Uuidable;
import zgame.physics.collision.CollisionResult;
import zgame.physics.material.Material;
import zgame.physics.material.Materials;
import zgame.things.entity.projectile.Projectile2D;
import zgame.things.type.Materialable;

/**
 * An interface which defines an object that has a hit box, meaning something with a position that can collide and move against other bounds
 * @param <H> The specific hitbox implementation associated with this hitbox
 */
public interface HitBox<H extends HitBox<H, C>, C extends CollisionResult<C>> extends Materialable, Uuidable {
	
	/** @return The type of this hitbox, for determining how it will collide with other hitboxes */
	HitboxType getHitboxType();
	
	/**
	 * @param h The hitbox to check
	 * @return true if this hitbox intersects the given hitbox, false otherwise
	 */
	boolean intersects(H h);
	
	/**
	 * Apply the given {@link CollisionResult} to this object
	 *
	 * @param r The {@link CollisionResult} to use
	 */
	void collide(C r);
	
	/**
	 * Called when this {@link HitBox} is hit by a projectile. Does nothing by default, implement to provide custom behavior
	 *
	 * @param p The projectile which hit this {@link HitBox}
	 */
	default void hitBy(Projectile2D p){}
	
	/**
	 * A method that defines what this object does when it touches a floor
	 *
	 * @param collision The collision resulting in the floor being touched
	 */
	void touchFloor(C collision);
	
	/** A method that defines what this object does when it leaves the floor, i.e. it goes from touching the floor to not touching the floor */
	void leaveFloor();
	
	/**
	 * A method that defines what this object does when it touches a ceiling
	 *
	 * @param collision The collision resulting in the ceiling being touched
	 */
	void touchCeiling(C collision);
	
	/** A method that defines what this object does when it leaves a ceiling, i.e. it goes from touching a wall to not touching a ceiling */
	void leaveCeiling();
	
	/**
	 * A method that defines what this object does when it touches a wall
	 *
	 * @param collision The collision resulting in the wall being touched
	 */
	void touchWall(C collision);
	
	/** A method that defines what this object does when it leaves a wall, i.e. it goes from touching a wall to not touching a wall */
	void leaveWall();
	
	/** @return true if this {@link HitBox} is on the ground, false otherwise i.e. it's in the air */
	boolean isOnGround();
	
	/** @return true if this {@link HitBox} is touching a ceiling, false otherwise */
	boolean isOnCeiling();
	
	/** @return true if this {@link HitBox} is touching a wall, false otherwise */
	boolean isOnWall();
	
	/** @return The material of the ground that this thing is on, or {@link Materials#NONE} if not on ground */
	Material getFloorMaterial();
	
	/** @return The material of the ceiling that this thing is on, or {@link Materials#NONE} if not on ground */
	Material getCeilingMaterial();
	
	/** @return The material of the wall that this thing is on, or {@link Materials#NONE} if not on ground */
	Material getWallMaterial();
	
	/**
	 * Wacky java weirdness. Call to get this object as the appropriate type
	 * Can implement as just
	 * <p><code>return this;</code></p>
	 *
	 * @return This object, as the appropriate type for this hitbox
	 */
	H get();
	
	// TODO consider renaming this as it's about the surface area when falling down
	/** @return The surface area of this hitbox as it moves down */
	double getSurfaceArea();
}
