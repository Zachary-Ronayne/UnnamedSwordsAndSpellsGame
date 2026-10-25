package zgame.things.type.bounds.twoDee.bounds.shape;

import zgame.core.utils.ZMath;
import zgame.physics.collision.Collision2D;
import zgame.physics.collision.ZCollision;
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
	
	@Override
	default Collision2D collideRect(RectangleBounds bounds){
		var bPos = bounds.getMinPosition();
		var bDims = bounds.getDimensions();
		var tPos = this.getMinPosition();
		var tDims = this.getDimensions();
		return ZCollision.rectToRectBasic(
				// Unmoving bounds
				bPos.getX(), bPos.getY(), bDims.getWidth(), bDims.getHeight(),
				// Moving bounds
				tPos.getX(), tPos.getY(), tDims.getWidth(), tDims.getHeight()
		);
	}
	
	@Override
	default Collision2D collideCircle(CircleBounds bounds){
		var bPos = bounds.getCenterPosition();
		var r = bounds.getRadius();
		var tPos = this.getMinPosition();
		var tDims = this.getDimensions();
		return ZCollision
				.rectToCircleBasic(
						// Moving bounds
						tPos.getX(), tPos.getY(), tDims.getWidth(), tDims.getHeight(),
						// Unmoving bounds
						bPos.getX(), bPos.getY(), r
				)
				// TODO does inverting even make sense?
				// Must invert, the method expects the circle to be moving
				.scale(-1);
	}
	
	@Override
	default double getGravityDragReferenceArea(){
		return this.getWidth();
	}
}
