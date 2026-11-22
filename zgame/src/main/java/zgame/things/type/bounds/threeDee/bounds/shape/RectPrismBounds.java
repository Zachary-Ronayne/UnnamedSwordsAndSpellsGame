package zgame.things.type.bounds.threeDee.bounds.shape;

import zgame.core.utils.ZMath;
import zgame.physics.collision.Collision3D;
import zgame.physics.collision.ZCollision;
import zgame.things.type.bounds.threeDee.shape.RectPrismShape;

/** An object with the bounds of an axis aligned rectangular prism */
public non-sealed interface RectPrismBounds extends ShapedBounds3D, RectPrismShape{
	
	@Override
	default boolean intersectsRect(RectPrismBounds bounds){
		var bPos = bounds.getBottomCenter();
		double x = bPos.getX();
		double y = bPos.getY();
		double z = bPos.getZ();
		var bDims = bounds.getDimensions();
		double width = bDims.getWidth();
		double height = bDims.getHeight();
		double length = bDims.getLength();
		
		double hw = width * 0.5;
		double hl = length * 0.5;
		double x1 = x - hw;
		double x2 = x + hw;
		double y2 = y + height;
		double z1 = z - hl;
		double z2 = z + hl;
		
		var tPos = this.getBottomCenter();
		double tx = tPos.getX();
		double ty = tPos.getY();
		double tz = tPos.getZ();
		var tDims = this.getDimensions();
		double thw = tDims.getWidth() * 0.5;
		double thl = tDims.getLength() * 0.5;
		double tx1 = tx - thw;
		double tx2 = tx + thw;
		double ty2 = ty + tDims.getHeight();
		double tz1 = tz - thl;
		double tz2 = tz + thl;
		
		return
				ZMath.linesSameAxisIntersect(x1, x2, tx1, tx2) &&
				ZMath.linesSameAxisIntersect(y, y2, ty, ty2) &&
				ZMath.linesSameAxisIntersect(z1, z2, tz1, tz2);
	}
	
	@Override
	default boolean intersectsCylinder(CylinderBounds bounds){
		var rPos = this.getBottomCenter();
		var rDims = this.getDimensions();
		var cPos = bounds.getBottomCenter();
		
		return ZCollision.rectIntersectsCylinder(
				rPos.getX(), rPos.getY(), rPos.getZ(), rDims.getWidth(), rDims.getHeight(), rDims.getLength(),
				cPos.getX(), cPos.getY(), cPos.getZ(), bounds.getRadius(), bounds.getCylinderHeight()
		);
	}
	
	@Override
	default boolean intersectsSphere(SphereBounds bounds){
		var rPos = this.getBottomCenter();
		var rDims = this.getDimensions();
		var sPos = bounds.getCenterPosition();
		return ZCollision.rectIntersectsSphere(
				rPos.getX(), rPos.getY(), rPos.getZ(), rDims.getWidth(), rDims.getHeight(), rDims.getLength(),
				sPos.getX(), sPos.getY(), sPos.getZ(), bounds.getRadius()
		);
	}
	
	@Override
	default Collision3D collideRect(RectPrismBounds bounds){
		// issue#58 implement
		return new Collision3D(bounds.getMinPosition());
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
		return this.getWidth() * this.getLength();
	}
}
