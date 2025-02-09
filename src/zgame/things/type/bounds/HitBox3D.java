package zgame.things.type.bounds;

import zgame.physics.collision.CollisionResult3D;
import zgame.physics.material.Material;
import zgame.things.entity.projectile.Projectile;

/** An interface which defines an object that has a hit box, meaning something with a position that can collide and move against other bounds */
public interface HitBox3D extends HitBox<HitBox3D, CollisionResult3D>, Bounds3D{
	
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
	
	@Override
	default boolean intersects(HitBox3D hitBox){
		if(hitBox.getHitboxType() == HitboxType.RECT_PRISM){
			return this.intersectsRect(hitBox.getX(), hitBox.getY(), hitBox.getZ(), hitBox.getWidth(), hitBox.getHeight(), hitBox.getLength());
		}
		else if(hitBox.getHitboxType() == HitboxType.CYLINDER){
			return this.intersectsCylinder(hitBox.getX(), hitBox.getY(), hitBox.getZ(), hitBox.getWidth() * 0.5, hitBox.getHeight());
		}
		return false;
	}
	
	/**
	 * Determine if this hitbox intersects the given rectangular prism
	 *
	 * @param x The bottom center x coordinate of the rectangular prism
	 * @param y The bottom center y coordinate of the rectangular prism
	 * @param z The bottom center z coordinate of the rectangular prism
	 * @param width The total width of the rectangular prism
	 * @param height The total height of the rectangular prism
	 * @param length The total length of the rectangular prism
	 * @return true if the hitboxes intersect, false otherwise
	 */
	boolean intersectsRect(double x, double y, double z, double width, double height, double length);
	
	/**
	 * Determine if this hitbox intersects the given cylinder
	 *
	 * @param x The bottom center x coordinate of the cylinder
	 * @param y The bottom center y coordinate of the cylinder
	 * @param z The bottom center z coordinate of the cylinder
	 * @param radius The radius of the cylinder
	 * @param height The height of the cylinder
	 * @return true if the hotboxes intersect, false otherwise
	 */
	boolean intersectsCylinder(double x, double y, double z, double radius, double height);
	
	/**
	 * Determine a {@link CollisionResult3D} from colliding this object with the given rectangular prism bounds. Essentially, move this object so that it no longer
	 * intersecting with the given bounds. This method should not change the state of this object, it should only return an object representing how the collision should happen.
	 *
	 * @param x The bottom center x coordinate of the rectangular prism
	 * @param y The bottom center y coordinate of the rectangular prism
	 * @param z The bottom center z coordinate of the rectangular prism
	 * @param width The total width of the rectangular prism
	 * @param height The total height of the rectangular prism
	 * @param length The total length of the rectangular prism
	 * @param m The material which was collided with
	 * @return The information about the collision
	 */
	CollisionResult3D calculateRectCollision(double x, double y, double z, double width, double height, double length, Material m);
	
	/** @return The surface area of this hitbox */
	
	double getSurfaceArea();
	
}
