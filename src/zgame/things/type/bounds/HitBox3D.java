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
