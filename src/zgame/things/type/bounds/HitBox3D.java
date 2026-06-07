package zgame.things.type.bounds;

import zgame.physics.V3D;
import zgame.physics.collision.Collision3D;
import zgame.physics.material.Material;
import zgame.things.entity.projectile.Projectile3D;
import zgame.world.Direction3D;

/** An interface which defines an object that has a hit box, meaning something with a position that can collide and move against other bounds */
public interface HitBox3D extends HitBox<V3D>, Bounds3D{
	
	/**
	 * Called when this {@link HitBox3D} is hit by a projectile. Does nothing by default, implement to provide custom behavior
	 *
	 * @param p The projectile which hit this {@link HitBox3D}
	 */
	default void hitBy(Projectile3D p){}
	
	@Override
	default boolean intersects(HitBox<V3D> hitBox){
		var pos = hitBox.getPosition();
		double x = pos.getX();
		double y = pos.getY();
		double z = pos.getZ();
		
		var dims = hitBox.getDimensions();
		double w = dims.getWidth();
		double h = dims.getHeight();
		double l = dims.getLength();
		
		return switch(hitBox.getHitboxType()){
			case RECT_PRISM -> this.intersectsRect(x, y, z, w, h, l);
			case CYLINDER -> this.intersectsCylinder(x, y, z, w * 0.5, h);
			case SPHERE -> this.intersectsSphere(x, y, z, w * 0.5);
			default -> false;
		};
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
	 * Determine if this hitbox intersects the given sphere
	 *
	 * @param x The center x coordinate of the sphere
	 * @param y The center y coordinate of the sphere
	 * @param z The center z coordinate of the sphere
	 * @param radius The radius of the sphere
	 * @return true if the hotboxes intersect, false otherwise
	 */
	boolean intersectsSphere(double x, double y, double z, double radius);
	
	/**
	 * Determine a {@link Collision3D} from colliding this object with the given rectangular prism bounds. Essentially, move this object so that it no longer
	 * intersecting with the given bounds. This method should not change the state of this object, it should only return an object representing how the collision should
	 * happen.
	 *
	 * @param x The bottom center x coordinate of the rectangular prism
	 * @param y The bottom center y coordinate of the rectangular prism
	 * @param z The bottom center z coordinate of the rectangular prism
	 * @param width The total width of the rectangular prism
	 * @param height The total height of the rectangular prism
	 * @param length The total length of the rectangular prism
	 * @param m The material which was collided with
	 * @param collisionFaces The faces of the rectangular prism which can cause collisions, indexed using {@link Direction3D}, true for allowing collision, false for no collision.
	 * @return The information about the collision
	 */
	Collision3D calculateRectCollision(double x, double y, double z, double width, double height, double length, Material m, boolean[] collisionFaces);
	
	// TODO probably do this in a better way to avoid casting
	@SuppressWarnings("unchecked")
	@Override
	default <H extends HitBox<V3D>> H asHitbox(Class<H> clazz){
		return (H)this;
	}
	
}
