package zgame.things.type.bounds.twoDee.bounds.shape;

import zgame.core.utils.ZMath;
import zgame.things.type.bounds.twoDee.shape.RectangleShape;

/** An axis aligned rectangle */
public non-sealed interface RectangleBounds extends ShapedBounds2D, RectangleShape{
	
	default boolean intersectsRect(RectangleBounds bounds){
		return this.getRectBounds().intersects(bounds.getRectBounds());
	}
	
	default boolean intersectsCircle(CircleBounds bounds){
		var cCenter = bounds.getCenterPosition();
		var rMin = this.getMinPosition();
		var rDims = this.getDimensions();
		return ZMath.circleIntersectsRect(cCenter.getX(), cCenter.getY(), bounds.getRadius(), rMin.getX(), rMin.getY(), rDims.getWidth(), rDims.getHeight());
	}
	
}
