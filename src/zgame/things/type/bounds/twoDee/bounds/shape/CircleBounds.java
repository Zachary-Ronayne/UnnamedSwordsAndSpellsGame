package zgame.things.type.bounds.twoDee.bounds.shape;

import zgame.core.utils.ZMath;
import zgame.core.utils.ZPoint2D;
import zgame.things.type.bounds.twoDee.shape.CircleShape;

/** A circle with a position */
public non-sealed interface CircleBounds extends ShapedBounds2D, CircleShape{
	
	default boolean intersectsCircle(CircleBounds bounds){
		var c1 = this.getCenterPosition();
		var c2 = bounds.getCenterPosition();
		var dist = new ZPoint2D(c1.getX(), c1.getY()).distance(c2.getX(), c2.getY());
		return dist <= bounds.getRadius() + this.getRadius();
	}
	
	default boolean intersectsRect(RectangleBounds bounds){
		var cCenter = this.getCenterPosition();
		var rMin = bounds.getMinPosition();
		var rDims = bounds.getDimensions();
		return ZMath.circleIntersectsRect(cCenter.getX(), cCenter.getY(), this.getRadius(), rMin.getX(), rMin.getY(), rDims.getWidth(), rDims.getHeight());
	}
}
