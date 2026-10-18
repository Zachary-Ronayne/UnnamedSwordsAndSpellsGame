package zgame.things.type.bounds.twoDee.bounds;

import zgame.core.utils.ZRect2D;
import zgame.physics.V2D;
import zgame.things.type.Position2D;
import zgame.things.type.bounds.Bounds;
import zgame.things.type.bounds.twoDee.shape.Shape2D;

/** An object which has a bounds that can be defined in a 2D space */
public interface Bounds2D extends Position2D, Bounds<V2D>, Shape2D{
	
	/** @return The minimum x coordinate of this bounds */
	default double minX(){
		return this.getMinPosition().getX();
	}
	
	/** @return The minimum y coordinate of this bounds */
	default double minY(){
		return this.getMinPosition().getY();
	}
	
	/** @return The maximum x coordinate of this bounds */
	default double maxX(){
		return this.getMaxPosition().getX();
	}
	
	/** @return The maximum y coordinate of this bounds */
	default double maxY(){
		return this.getMaxPosition().getY();
	}
	
	/** @return The width this bounds takes up */
	default double getWidth(){
		return this.getDimensions().getWidth();
	}
	
	/** @return The height this bounds takes up */
	default double getHeight(){
		return this.getDimensions().getHeight();
	}
	
	/** @return The center x coordinate of this bounds */
	default double centerX(){
		return this.getCenterPosition().getX();
	}
	
	/** @return The center y coordinate of this bounds */
	default double centerY(){
		return this.getCenterPosition().getY();
	}
	
	/** @return An axis aligned rectangle representing the full bounds which this {@link Bounds2D} takes up */
	default ZRect2D getRectBounds(){
		var pos = this.getMinPosition();
		var dims = this.getDimensions();
		return new ZRect2D(pos.getX(), pos.getY(), dims.getWidth(), dims.getHeight());
	}
	
	@Override
	default boolean intersects(Bounds<V2D> bounds){
		// TODO consider not using a type enum, use sealed classes
		return switch(this.getShapeType()){
			// No shape, no intersection
			case EMPTY -> false;
			case RECT -> this.intersectsRect((RectangleBounds)bounds);
			case CIRCLE -> this.intersectsCircle((CircleBounds)bounds);
		};
	}
	
	/** @return true if this bounds intersects the given rectangle bounds */
	boolean intersectsRect(RectangleBounds bounds);
	
	/** @return true if this bounds intersects the given circle bounds */
	boolean intersectsCircle(CircleBounds bounds);
	
}