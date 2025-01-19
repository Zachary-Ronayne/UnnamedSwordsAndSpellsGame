package zgame.things.type.bounds;

import zgame.physics.collision.CollisionResult3D;
import zgame.physics.material.Material;

public interface RectPrismHitbox extends HitBox3D, RectPrismBounds{
	
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
	
}
