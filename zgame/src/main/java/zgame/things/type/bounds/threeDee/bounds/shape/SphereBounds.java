package zgame.things.type.bounds.threeDee.bounds.shape;

import zgame.physics.collision.Collision3D;
import zgame.physics.collision.ZCollision;
import zgame.things.type.bounds.threeDee.shape.SphereShape;

public non-sealed interface SphereBounds extends ShapedBounds3D, SphereShape{
	
	@Override
	default boolean intersectsRect(RectPrismBounds bounds){
		var rPos = bounds.getBottomCenter();
		var rDims = bounds.getDimensions();
		var sPos = this.getCenterPosition();
		
		return ZCollision.rectIntersectsSphere(
				rPos.getX(), rPos.getY(), rPos.getZ(), rDims.getWidth(), rDims.getHeight(), rDims.getLength(),
				sPos.getX(), sPos.getY(), sPos.getZ(), this.getRadius()
		);
	}
	
	@Override
	default boolean intersectsCylinder(CylinderBounds bounds){
		var cPos = bounds.getBottomCenter();
		var sPos = this.getCenterPosition();
		return ZCollision.cylinderIntersectsSphere(
				cPos.getX(), cPos.getY(), cPos.getZ(), bounds.getRadius(), bounds.getCylinderHeight(),
				sPos.getX(), sPos.getY(), sPos.getZ(), this.getRadius()
		);
	}
	
	@Override
	default boolean intersectsSphere(SphereBounds bounds){
		var tCenter = this.getCenterPosition();
		var bCenter = this.getCenterPosition();
		
		double dx = bCenter.getX() - tCenter.getX();
		double dy = bCenter.getY() - tCenter.getY();
		double dz = bCenter.getZ() - tCenter.getZ();
		return Math.sqrt(dx * dx + dy * dy + dz * dz) < bounds.getRadius() + this.getRadius();
	}
	
	@Override
	default Collision3D collideRect(RectPrismBounds bounds){
		var rPos = bounds.getBottomCenter();
		var rDims = bounds.getDimensions();
		var sPos = this.getCenterPosition();
		
		return ZCollision.rectToSphereBasic(
				// Unmoving
				rPos.getX(), rPos.getY(), rPos.getZ(), rDims.getWidth(), rDims.getHeight(), rDims.getLength(),
				// Moving
				sPos.getX(), sPos.getY(), sPos.getZ(), this.getRadius(), bounds.getCollisionFaces()
		);
	}
	
	@Override
	default Collision3D collideCylinder(CylinderBounds bounds){
		// issue#58 implement
		return new Collision3D(bounds.getMinPosition());
	}
	
	@Override
	default Collision3D collideSphere(SphereBounds bounds){
		// issue#58 implement
		return new Collision3D(bounds.getMinPosition());
	}
	
	@Override
	default double getGravityDragReferenceArea(){
		return this.getFaceArea();
	}
}
