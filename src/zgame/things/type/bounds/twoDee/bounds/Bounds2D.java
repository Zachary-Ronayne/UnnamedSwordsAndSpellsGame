package zgame.things.type.bounds.twoDee.bounds;

import zgame.core.utils.ZRect2D;
import zgame.physics.V2D;
import zgame.physics.collision.Collision;
import zgame.physics.collision.Collision2D;
import zgame.things.type.Position2D;
import zgame.things.type.bounds.Bounds;
import zgame.things.type.bounds.twoDee.bounds.shape.ShapedBounds2D;
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
	default boolean intersectsDimensions(Bounds<V2D> bounds){
		return this.intersects((Bounds2D)bounds);
	}
	
	/**
	 * @param bounds The bounds to check
	 * @return true if the given bounds intersect with this bounds
	 */
	boolean intersects(Bounds2D bounds);
	
	/** @return A shape representing this bounds */
	ShapedBounds2D asShapedBounds();
	
	@Override
	default Collision<V2D> collideAtDimensions(V2D currentMinPos, Bounds<V2D> bounds){
		return this.collideAt(currentMinPos, (Bounds2D)bounds);
	}
	
	/**
	 * @param currentMinPos The min position that this bounds should be considered in for this computation
	 * @param bounds The bounds that does not move during this collision
	 * @return The collision representing how this bounds will need to be moved to no longer be colliding with the given bounds
	 */
	Collision2D collideAt(V2D currentMinPos, Bounds2D bounds);
	
	// TODO probably move this system to a separate kind of bounds used by rooms
	/**
	 * Provide an x coordinate to reposition this object so that it is to the left of the given x coordinate.
	 * If the object is already to the left of the coordinate, this method returns {@link #getX()}
	 * If the object needs to be moved, its position should be such that it is as close to its original position as possible, while still being to the left of the given
	 * coordinate
	 *
	 * @param x The coordinate
	 * @return The new value the coordinate should be
	 */
	default double keepLeft(double x){
		if(this.getX() + this.getWidth() <= x) return this.getX();
		return x - this.getWidth();
	}
	
	/**
	 * Provide a coordinate to reposition this object so that it is to the right of the given x coordinate.
	 * If the object is already to the right of the coordinate, this method returns {@link #getX()}
	 * If the object needs to be moved, its position should be such that it is as close to its original position as possible, while still being to the right of the given
	 * coordinate
	 *
	 * @param x The coordinate
	 * @return The new value the coordinate should be
	 */
	default double keepRight(double x){
		return Math.max(this.getX(), x);
	}
	
	/**
	 * Provide a coordinate to reposition this object so that it is above the given y coordinate.
	 * If the object is already above the coordinate, this method returns {@link #getY()}
	 * If the object needs to be moved, its position should be such that it is as close to its original position as possible, while still being above of the given
	 * coordinate
	 *
	 * @param y The coordinate
	 * @return The new value the coordinate should be
	 */
	default double keepAbove(double y){
		if(this.getY() + this.getHeight() <= y) return this.getY();
		return y - this.getHeight();
	}
	
	/**
	 * Provide a coordinate to reposition this object so that it is below the given y coordinate.
	 * If the object is already below the coordinate, this method returns {@link #getY()}
	 * If the object needs to be moved, its position should be such that it is as close to its original position as possible, while still being below of the given
	 * coordinate
	 *
	 * @param y The coordinate
	 * @return The new value the coordinate should be
	 */
	default double keepBelow(double y){
		return Math.max(this.getY(), y);
	}
	
	
}