package zgame.things.type.bounds;

import zgame.core.utils.ZMath;
import zgame.physics.collision.CollisionResult3D;
import zgame.physics.material.Material;

public interface RectPrismHitbox extends HitBox3D{
	
	/** @return The bottom center x coordinate of this hitbox */
	double getX();
	/** @return The bottom center y coordinate of this hitbox */
	double getY();
	/** @return The bottom center z coordinate of this hitbox */
	double getZ();
	
	/** @return The width, i.e. x axis size, of this hitbox */
	double getWidth();
	
	/** @return The height, i.e. y axis size, of this hitbox */
	double getHeight();
	
	/** @return The length, i.e. z axis size, of this hitbox */
	double getLength();
	
	@Override
	default double maxX(){
		return this.getX() + this.getWidth() * 0.5;
	}
	
	@Override
	default double minX(){
		return this.getX() - this.getWidth() * 0.5;
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
		return this.getZ() + this.getLength() * 0.5;
	}
	
	@Override
	default double minZ(){
		return this.getZ() - this.getLength() * 0.5;
	}
	
	@Override
	default HitboxType getHitboxType(){
		return HitboxType.RECT_PRISM;
	}
	
	@Override
	default boolean intersectsRect(double x, double y, double z, double width, double height, double length){
		// TODO implement
		return false;
	}
	
	@Override
	default CollisionResult3D calculateRectCollision(double x, double y, double z, double width, double height, double length, Material m){
		// TODO implement
		return new CollisionResult3D();
	}
	
	@Override
	default double getSurfaceArea(){
		return this.getWidth() * this.getHeight();
	}
	
	@Override
	default double rayDistance(double rx, double ry, double rz, double dx, double dy, double dz){
		return ZMath.rayDistanceToRectPrism(rx, ry, rz, dx, dy, dz, this.minX(), this.minY(), this.minZ(), this.maxX(), this.maxY(), this.maxZ());
	}
}
