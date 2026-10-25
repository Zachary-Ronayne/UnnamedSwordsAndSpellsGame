package zgame.things.type.bounds.threeDee.bounds;

import zgame.core.utils.ZRect3D;
import zgame.physics.V3D;
import zgame.physics.collision.Collision;
import zgame.physics.collision.Collision3D;
import zgame.things.type.Position3D;
import zgame.things.type.bounds.Bounds;
import zgame.things.type.bounds.threeDee.bounds.shape.ShapedBounds3D;
import zgame.things.type.bounds.threeDee.shape.Shape3D;

/** An object which has a bounds that can be defined in a 3D space */
public interface Bounds3D extends Position3D, Bounds<V3D>, Shape3D{
	
	/** @return The maximum x coordinate of an axis aligned rectangular prism bounding box entirely containing this hitbox */
	default double maxX(){
		return this.getMaxPosition().getX();
	}
	
	/** @return The minimum x coordinate of an axis aligned rectangular prism bounding box entirely containing this hitbox */
	default double minX(){
		return this.getMinPosition().getX();
	}
	
	/** @return The maximum y coordinate of an axis aligned rectangular prism bounding box entirely containing this hitbox */
	default double maxY(){
		return this.getMaxPosition().getY();
	}
	
	/** @return The minimum y coordinate of an axis aligned rectangular prism bounding box entirely containing this hitbox */
	default double minY(){
		return this.getMinPosition().getY();
	}
	
	/** @return The maximum z coordinate of an axis aligned rectangular prism bounding box entirely containing this hitbox */
	default double maxZ(){
		return this.getMaxPosition().getZ();
	}
	
	/** @return The minimum z coordinate of an axis aligned rectangular prism bounding box entirely containing this hitbox */
	default double minZ(){
		return this.getMinPosition().getZ();
	}
	
	/** @return The width this bounds takes up */
	default double getWidth(){
		return this.getDimensions().getWidth();
	}
	
	/** @return The height this bounds takes up */
	default double getHeight(){
		return this.getDimensions().getHeight();
	}
	
	/** @return The length this bounds takes up */
	default double getLength(){
		return this.getDimensions().getLength();
	}
	
	/** @return The center x coordinate of this bounds */
	default double centerX(){
		return this.getMinPosition().getX();
	}
	
	/** @return The center y coordinate of this bounds */
	default double centerY(){
		return this.getMinPosition().getY();
	}
	
	/** @return The center z coordinate of this bounds */
	default double centerZ(){
		return this.getMinPosition().getZ();
	}
	
	default V3D getBottomCenter(){
		var c = this.getCenterPosition();
		return new V3D(c.getX(), this.minY(), c.getZ());
	}
	
	/** @return A rectangle representing the full bounds which this {@link Bounds3D} takes up */
	default ZRect3D getBounds(){
		var pos = this.getMinPosition();
		var dims = this.getDimensions();
		return new ZRect3D(pos.getX(), pos.getY(), pos.getZ(), dims.getWidth(), dims.getHeight(), dims.getLength());
	}
	
	/**
	 * Find the distance from this bounds to the given bounds, based on their center bottom coordinates
	 *
	 * @param bounds The other bounds to compare
	 * @return The distance
	 */
	default double distance(Bounds3D bounds){
		var b = bounds.getBottomCenter();
		var t = this.getBottomCenter();
		double x = t.getX() - b.getX();
		double y = t.getY() - b.getY();
		double z = t.getZ() - b.getZ();
		return Math.sqrt(x * x + y + y + z * z);
	}
	
	@Override
	default boolean intersectsDimensions(Bounds<V3D> bounds){
		return this.intersects((Bounds3D)bounds);
	}
	
	/**
	 * @param bounds The bounds to check
	 * @return true if the given bounds intersect with this bounds
	 */
	boolean intersects(Bounds3D bounds);
	
	/** @return A shape representing this bounds */
	ShapedBounds3D asShapedBounds();
	
	@Override
	default Collision<V3D> collideAtDimensions(V3D currentMinPos, Bounds<V3D> bounds){
		return this.collideAt(currentMinPos, (Bounds3D)bounds);
	}
	
	/**
	 * @param currentMinPos The min position that this bounds should be considered in for this computation
	 * @param bounds The bounds this object is colliding with
	 * @return The collision representing how this bounds will need to be moved to no longer be colliding with the given bounds
	 */
	Collision3D collideAt(V3D currentMinPos, Bounds3D bounds);
}