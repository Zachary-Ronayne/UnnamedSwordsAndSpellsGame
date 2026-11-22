package zgame.things.type.bounds.threeDee.bounds.shape;

import zgame.core.utils.ZMath;
import zgame.physics.collision.Collision3D;
import zgame.physics.collision.ZCollision;
import zgame.things.type.bounds.threeDee.shape.CylinderShape;

public non-sealed interface CylinderBounds extends ShapedBounds3D, CylinderShape{
	
	@Override
	default boolean intersectsRect(RectPrismBounds bounds){
		var rPos = bounds.getBottomCenter();
		var rDims = bounds.getDimensions();
		var cPos = this.getBottomCenter();
		return ZCollision.rectIntersectsCylinder(
				rPos.getX(), rPos.getY(), rPos.getZ(), rDims.getWidth(), rDims.getHeight(), rDims.getLength(),
				cPos.getX(), cPos.getY(), cPos.getZ(), this.getRadius(), this.getCylinderHeight()
		);
	}
	
	@Override
	default boolean intersectsCylinder(CylinderBounds bounds){
		var tPos = this.getBottomCenter();
		var bPos = bounds.getBottomCenter();
		
		double dx = bPos.getX() - tPos.getX();
		double dz = bPos.getZ() - tPos.getZ();
		return
				ZMath.linesSameAxisIntersect(bPos.getY(), bPos.getY() + bounds.getCylinderHeight(), tPos.getY(), tPos.getY() + this.getCylinderHeight()) &&
				(Math.sqrt(dx * dx + dz * dz) < bounds.getRadius() + this.getRadius());
	}
	
	@Override
	default boolean intersectsSphere(SphereBounds bounds){
		var cPos = this.getBottomCenter();
		var sPos = bounds.getCenterPosition();
		return ZCollision.cylinderIntersectsSphere(
				cPos.getX(), cPos.getY(), cPos.getZ(), this.getRadius(), this.getCylinderHeight(),
				sPos.getX(), sPos.getY(), sPos.getZ(), bounds.getRadius()
		);
	}
	
	@Override
	default Collision3D collideRect(RectPrismBounds bounds){
		var rPos = bounds.getBottomCenter();
		var rDims = bounds.getDimensions();
		var cPos = this.getBottomCenter();
		
		return ZCollision.rectToCylinderBasic(
				rPos.getX(), rPos.getY(), rPos.getZ(), rDims.getWidth(), rDims.getHeight(), rDims.getLength(),
				cPos.getX(), cPos.getY(), cPos.getZ(), this.getRadius(), this.getHeight(),
				bounds.getCollisionFaces()
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
