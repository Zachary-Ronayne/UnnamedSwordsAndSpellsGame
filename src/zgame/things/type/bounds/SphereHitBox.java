package zgame.things.type.bounds;

import zgame.physics.collision.CollisionResult3D;
import zgame.physics.collision.ZCollision;
import zgame.physics.material.Material;

/** An object representing a circle  */
public interface SphereHitBox extends HitBox3D{
	
	@Override
	default HitboxType getHitboxType(){
		return HitboxType.SPHERE;
	}
	
	/** @return The radius of this sphere hitbox */
	double getRadius();
	
	@Override
	default double getWidth(){
		return this.getRadius() * 2;
	}
	@Override
	default double getHeight(){
		return this.getRadius() * 2;
	}
	
	@Override
	default double getLength(){
		return this.getRadius() * 2;
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
		return this.getY() + this.getRadius();
	}
	@Override
	default double minY(){
		return this.getY() - this.getRadius();
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
	default double centerX(){
		return this.getX();
	}
	
	@Override
	default double centerY(){
		return this.getY();
	}
	
	@Override
	default double centerZ(){
		return this.getZ();
	}
	
	@Override
	default CollisionResult3D calculateRectCollision(double x, double y, double z, double width, double height, double length, Material m){
		return ZCollision.rectToSphereBasic(x, y, z, width, height, length, this.getX(), this.getY(), this.getZ(), this.getRadius(), m);
	}
	
	@Override
	default boolean intersectsRect(double x, double y, double z, double width, double height, double length){
		return ZCollision.rectIntersectsSphere(x, y, z, width, height, length, this.getX(), this.getY(), this.getZ(), this.getRadius());
	}
	
	@Override
	default boolean intersectsCylinder(double x, double y, double z, double radius, double height){
		return ZCollision.cylinderIntersectsSphere(x, y, z, radius, height, this.getX(), this.getY(), this.getZ(), this.getRadius());
	}
	
	@Override
	default boolean intersectsSphere(double x, double y, double z, double radius){
		double dx = x - this.getX();
		double dy = y - this.getY();
		double dz = z - this.getZ();
		return Math.sqrt(dx * dx + dy * dy + dz * dz) < radius + this.getRadius();
	}
	
	@Override
	default double getGravityDragReferenceArea(){
		double r = this.getRadius();
		return Math.PI * r * r;
	}
	
}
