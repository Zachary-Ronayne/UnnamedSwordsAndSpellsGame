package zgame.things.type.bounds;

import zgame.physics.collision.CollisionResult3D;
import zgame.physics.collision.ZCollision;
import zgame.physics.material.Material;

/** A hitbox with a vertical cylinder shape in 3D, which cannot be rotated */
public interface CylinderHitbox extends HitBox3D{

	/** @return The bottom center x coordinate of this hitbox */
	double getX();
	/** @return The bottom center y coordinate of this hitbox */
	double getY();
	/** @return The bottom center z coordinate of this hitbox */
	double getZ();
	
	/** @return The radius of the circular base of this cylinder */
	double getRadius();
	
	/** @return The total height of the circular base of this cylinder */
	double getHeight();
	
	/** @return The total width of this cylinder */
	default double getWidth(){
		return this.getRadius() * 2.0;
	}
	
	/** @return The total length of this cylinder */
	default double getLength(){
		return this.getRadius() * 2.0;
	}
	
	@Override
	default double maxX(){
		return this.getX() + this.getRadius();
	}
	@Override
	default double minX(){
		return this.getX() - this.getRadius();
	}
	@Override
	default double maxY(){
		return this.getY() + this.getHeight();
	}
	@Override
	default double minY(){
		return this.getY();
	}
	@Override
	default double maxZ(){
		return this.getZ() + this.getRadius();
	}
	@Override
	default double minZ(){
		return this.getZ() - this.getRadius();
	}
	
	@Override
	default HitboxType getHitboxType(){
		return HitboxType.CYLINDER;
	}
	
	@Override
	default CollisionResult3D calculateRectCollision(double x, double y, double z, double width, double height, double length, Material m){
		return ZCollision.rectToCylinderBasic(x, y, z, width, height, length, this.getX(), this.getY(), this.getZ(), this.getRadius(), this.getHeight(), m);
	}
	
	@Override
	default boolean intersectsRect(double x, double y, double z, double width, double height, double length){
		return ZCollision.rectIntersectsCylinder(x, y, z, width, height, length, this.getX(), this.getY(), this.getZ(), this.getRadius(), this.getHeight());
	}
}
