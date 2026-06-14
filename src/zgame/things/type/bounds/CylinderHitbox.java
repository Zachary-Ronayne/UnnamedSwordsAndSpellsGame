package zgame.things.type.bounds;

import zgame.core.utils.ZMath;
import zgame.physics.V3D;
import zgame.physics.collision.Collision3D;
import zgame.physics.collision.ZCollision;
import zgame.physics.material.Material;

/** A hitbox with a vertical cylinder shape in 3D, which cannot be rotated */
public interface CylinderHitbox extends HitBox3D{
	
	/** @return The radius of the circular base of this cylinder */
	double getRadius();
	
	/** @return The total height of the circular base of this cylinder */
	double getHeight();
	
	@Override
	default V3D getDimensions(){
		return new V3D(this.getRadius() * 2.0, this.getHeight(), this.getRadius() * 2.0);
	}
	
	/** @return The total width of this cylinder */
	default double getWidth(){
		return this.getRadius() * 2.0;
	}
	
	@Override
	default V3D getMaxPosition(){
		return new V3D(this.getX() + this.getRadius(), this.getY() + this.getHeight(), this.getZ() + this.getRadius());
	}
	
	@Override
	default V3D getMinPosition(){
		return new V3D(this.getX() - this.getRadius(), this.getY(), this.getZ() - this.getRadius());
	}
	
	@Override
	default HitboxType getHitboxType(){
		return HitboxType.CYLINDER;
	}
	
	@Override
	default Collision3D calculateRectCollision(double x, double y, double z, double width, double height, double length, Material m, boolean[] collisionFaces){
		return ZCollision.rectToCylinderBasic(x, y, z, width, height, length, this.getX(), this.getY(), this.getZ(), this.getRadius(), this.getHeight(), m, collisionFaces);
	}
	
	@Override
	default boolean intersectsRect(double x, double y, double z, double width, double height, double length){
		return ZCollision.rectIntersectsCylinder(x, y, z, width, height, length, this.getX(), this.getY(), this.getZ(), this.getRadius(), this.getHeight());
	}
	
	@Override
	default boolean intersectsCylinder(double x, double y, double z, double radius, double height){
		double tHeight = this.getHeight();
		double ty = this.getY();
		
		double dx = x - this.getX();
		double dz = z - this.getZ();
		return
				ZMath.linesSameAxisIntersect(y, y + height, ty, ty + tHeight) &&
				(Math.sqrt(dx * dx + dz * dz) < radius + this.getRadius());
	}
	
	@Override
	default boolean intersectsSphere(double x, double y, double z, double radius){
		return ZCollision.cylinderIntersectsSphere(this.getX(), this.getY(), this.getZ(), this.getRadius(), this.getHeight(), x, y, z, radius);
	}
	
	@Override
	default double getGravityDragReferenceArea(){
		double r = this.getRadius();
		return Math.PI * r * r;
	}
	
}
