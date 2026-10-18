package zgame.things.type.bounds.twoDee.bounds;

import zgame.core.utils.ZMath;
import zgame.things.type.bounds.twoDee.shape.RectangleShape;

public interface RectangleBounds extends Bounds2D, RectangleShape{
	
	@Override
	default boolean intersectsRect(RectangleBounds bounds){
		return this.getRectBounds().intersects(bounds.getRectBounds());
	}
	
	@Override
	default boolean intersectsCircle(CircleBounds bounds){
		var cCenter = bounds.getCenterPosition();
		var rMin = this.getMinPosition();
		var rDims = this.getDimensions();
		return ZMath.circleIntersectsRect(cCenter.getX(), cCenter.getY(), bounds.getRadius(), rMin.getX(), rMin.getY(), rDims.getWidth(), rDims.getHeight());
	}
	
}
