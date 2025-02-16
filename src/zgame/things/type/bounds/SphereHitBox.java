package zgame.things.type.bounds;

// TODO add this hitbox to the other 3D handler methods

import zgame.physics.collision.CollisionResult3D;
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
		// TODO implement
		return new CollisionResult3D();
	}
	
	@Override
	default boolean intersectsRect(double x, double y, double z, double width, double height, double length){
		// TODO implement
		return false;
	}
	
	@Override
	default boolean intersectsCylinder(double x, double y, double z, double radius, double height){
		// TODO implement
		return false;
	}
	
	// TODO add a method for intersects circle
	
	@Override
	default double getSurfaceArea(){
		double r = this.getRadius();
		// Times 2 not 4 because this is the surface area when falling down
		return 2 * Math.PI * r * r;
	}
	
}
