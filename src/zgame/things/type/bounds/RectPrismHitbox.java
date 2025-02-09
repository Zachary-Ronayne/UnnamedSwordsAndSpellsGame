package zgame.things.type.bounds;

import zgame.core.utils.ZMath;
import zgame.physics.collision.CollisionResult3D;
import zgame.physics.collision.ZCollision;
import zgame.physics.material.Material;

public interface RectPrismHitbox extends HitBox3D, RectPrismBounds{
	
	@Override
	default HitboxType getHitboxType(){
		return HitboxType.RECT_PRISM;
	}
	
	@Override
	default boolean intersectsRect(double x, double y, double z, double width, double height, double length){
		double hw = width * 0.5;
		double hl = length * 0.5;
		double x1 = x - hw;
		double x2 = x + hw;
		double y2 = y + height;
		double z1 = z - hl;
		double z2 = z + hl;
		
		double tx = this.getX();
		double ty = this.getY();
		double tz = this.getZ();
		double thw = this.getWidth() * 0.5;
		double thl = this.getLength() * 0.5;
		double tx1 = tx - thw;
		double tx2 = tx + thw;
		double ty2 = ty + this.getHeight();
		double tz1 = tz - thl;
		double tz2 = tz + thl;
		
		return
				ZMath.linesSameAxisIntersect(x1, x2, tx1, tx2) &&
				ZMath.linesSameAxisIntersect(y, y2, ty, ty2) &&
				ZMath.linesSameAxisIntersect(z1, z2, tz1, tz2);
	}
	
	@Override
	default boolean intersectsCylinder(double x, double y, double z, double radius, double height){
		return ZCollision.rectIntersectsCylinder(
				this.getX(), this.getY(), this.getZ(),
				this.getWidth(), this.getHeight(), this.getLength(),
				x, y, z, radius, height
		);
	}
	
	@Override
	default CollisionResult3D calculateRectCollision(double x, double y, double z, double width, double height, double length, Material m){
		// issue#58 implement
		return new CollisionResult3D();
	}
	
	@Override
	default double getSurfaceArea(){
		return this.getWidth() * this.getHeight();
	}
	
}
